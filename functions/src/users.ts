/**
 * User & role management (admin-only callable functions).
 *
 * These let a System Admin provision accounts, assign roles, change passwords, and remove users
 * from the server using the Admin SDK — which is the only place roles (custom claims) and another
 * user's password can be set securely. Document ids in the `users` collection are the username,
 * matching the client's UserCloudSync.
 */
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { defineSecret } from "firebase-functions/params";
import * as logger from "firebase-functions/logger";
import { FieldValue } from "firebase-admin/firestore";
import {
  auth,
  db,
  emailFromUsername,
  firebasePassword,
  isValidRole,
  requireAdmin,
} from "./common";

/** Bootstrap secret for the first admin, read from Secret Manager (set via CI or the CLI). */
const adminBootstrapSecret = defineSecret("ADMIN_BOOTSTRAP_SECRET");

interface CreateUserData {
  username: string;
  password: string;
  name?: string;
  role?: string;
  craft?: string;
  assignedGroups?: string;
  phone?: string;
  department?: string;
  employeeId?: string;
  email?: string;
}

/** Creates a Firebase Auth account, sets its role claim, and writes the user profile. */
export const adminCreateUser = onCall(async (req) => {
  requireAdmin(req);
  const data = (req.data ?? {}) as CreateUserData;
  const username = (data.username ?? "").trim();
  if (!username) throw new HttpsError("invalid-argument", "اسم المستخدم مطلوب.");
  if (!data.password || data.password.length < 1) {
    throw new HttpsError("invalid-argument", "كلمة المرور مطلوبة.");
  }
  const role = isValidRole(data.role) ? data.role : "Requester";
  const synthEmail = emailFromUsername(username);

  // Create (or reuse) the Auth account.
  let uid: string;
  try {
    const created = await auth.createUser({
      email: synthEmail,
      password: firebasePassword(data.password),
      displayName: data.name ?? username,
    });
    uid = created.uid;
  } catch (e: unknown) {
    const code = (e as { code?: string }).code;
    if (code === "auth/email-already-exists") {
      const existing = await auth.getUserByEmail(synthEmail);
      uid = existing.uid;
      await auth.updateUser(uid, { password: firebasePassword(data.password) });
    } else {
      throw new HttpsError("internal", `تعذّر إنشاء الحساب: ${String(e)}`);
    }
  }

  const groups = (data.assignedGroups ?? "")
    .split(",")
    .map((g) => g.trim())
    .filter((g) => g.length > 0);
  await auth.setCustomUserClaims(uid, { role, craft: data.craft ?? "", groups });

  await db.collection("users").doc(username).set(
    {
      username,
      name: data.name ?? username,
      role,
      craft: data.craft ?? "",
      assignedGroups: data.assignedGroups ?? "",
      phone: data.phone ?? "",
      department: data.department ?? "",
      employeeId: data.employeeId ?? "",
      email: data.email ?? "",
      isActive: true,
      locked: false,
      mustChangePassword: false,
      createdAt: FieldValue.serverTimestamp(),
    },
    { merge: true }
  );

  logger.info(`Admin created user ${username} (${role})`);
  return { ok: true, uid, username, role };
});

/** Updates a user's role / craft / assigned groups (claims + profile). */
export const adminSetRole = onCall(async (req) => {
  requireAdmin(req);
  const { username, role, craft, assignedGroups } = (req.data ?? {}) as {
    username?: string;
    role?: string;
    craft?: string;
    assignedGroups?: string;
  };
  if (!username) throw new HttpsError("invalid-argument", "اسم المستخدم مطلوب.");
  if (!isValidRole(role)) throw new HttpsError("invalid-argument", "دور غير صالح.");

  const userRecord = await auth.getUserByEmail(emailFromUsername(username));
  const groups = (assignedGroups ?? "")
    .split(",")
    .map((g) => g.trim())
    .filter((g) => g.length > 0);
  await auth.setCustomUserClaims(userRecord.uid, { role, craft: craft ?? "", groups });
  await db
    .collection("users")
    .doc(username)
    .set({ role, craft: craft ?? "", assignedGroups: assignedGroups ?? "" }, { merge: true });

  logger.info(`Admin set role ${role} for ${username}`);
  return { ok: true, username, role };
});

/** Resets another user's password (server-side; keeps the synthetic-password derivation). */
export const adminSetPassword = onCall(async (req) => {
  requireAdmin(req);
  const { username, newPassword } = (req.data ?? {}) as {
    username?: string;
    newPassword?: string;
  };
  if (!username || !newPassword) {
    throw new HttpsError("invalid-argument", "اسم المستخدم وكلمة المرور الجديدة مطلوبان.");
  }
  const userRecord = await auth.getUserByEmail(emailFromUsername(username));
  await auth.updateUser(userRecord.uid, { password: firebasePassword(newPassword) });
  await db
    .collection("users")
    .doc(username)
    .set({ mustChangePassword: false }, { merge: true });
  logger.info(`Admin reset password for ${username}`);
  return { ok: true, username };
});

/** Deletes a user's Auth account and profile document. */
export const adminDeleteUser = onCall(async (req) => {
  requireAdmin(req);
  const { username } = (req.data ?? {}) as { username?: string };
  if (!username) throw new HttpsError("invalid-argument", "اسم المستخدم مطلوب.");
  try {
    const userRecord = await auth.getUserByEmail(emailFromUsername(username));
    await auth.deleteUser(userRecord.uid);
  } catch (e) {
    logger.warn(`No auth account to delete for ${username}: ${String(e)}`);
  }
  await db.collection("users").doc(username).delete();
  logger.info(`Admin deleted user ${username}`);
  return { ok: true, username };
});

/**
 * One-time bootstrap for the very first System Admin, before any admin claim exists.
 * Protected by a shared secret set via:  firebase functions:config / env ADMIN_BOOTSTRAP_SECRET.
 * Call once, then it should be considered disabled (rotate the secret).
 */
export const setupAdminClaim = onCall({ secrets: [adminBootstrapSecret] }, async (req) => {
  const { username, secret } = (req.data ?? {}) as { username?: string; secret?: string };
  const expected = adminBootstrapSecret.value();
  if (!expected || !secret || secret !== expected) {
    throw new HttpsError("permission-denied", "secret غير صحيح أو غير مهيأ.");
  }
  if (!username) throw new HttpsError("invalid-argument", "اسم المستخدم مطلوب.");
  const userRecord = await auth.getUserByEmail(emailFromUsername(username));
  await auth.setCustomUserClaims(userRecord.uid, { role: "SystemAdmin", craft: "", groups: [] });
  await db.collection("users").doc(username).set({ role: "SystemAdmin" }, { merge: true });
  logger.info(`Bootstrapped SystemAdmin claim for ${username}`);
  return { ok: true, username };
});

/**
 * Custom-claims synchronization: keeps each Firebase Auth user's role claim in step with their
 * Firestore profile, so the security rules can authorize by role server-side.
 *
 * Two triggers cover the lazy-creation flow used by the app:
 *  - onAuthUserCreate: the app creates the Auth account on first online login; this stamps the
 *    claim from the existing users/{username} profile right away.
 *  - onUserProfileWritten: when an admin changes a user's role/craft/groups, the matching Auth
 *    user's claims are refreshed.
 */
import * as logger from "firebase-functions/logger";
import * as functionsV1 from "firebase-functions/v1";
import { onDocumentWritten } from "firebase-functions/v2/firestore";
import { auth, db, emailFromUsername, isValidRole } from "./common";

interface ClaimSource {
  role?: string;
  craft?: string;
  assignedGroups?: string;
}

function buildClaims(data: ClaimSource): Record<string, unknown> {
  const role = isValidRole(data.role) ? data.role : "Requester";
  const groups = (data.assignedGroups ?? "")
    .split(",")
    .map((g) => g.trim())
    .filter((g) => g.length > 0);
  return { role, craft: data.craft ?? "", groups };
}

async function applyClaimsByUsername(username: string): Promise<void> {
  const email = emailFromUsername(username);
  let userRecord;
  try {
    userRecord = await auth.getUserByEmail(email);
  } catch {
    // The Auth account may not exist yet (created lazily on first login) — nothing to stamp.
    return;
  }
  const snap = await db.collection("users").doc(username).get();
  const claims = buildClaims((snap.data() ?? {}) as ClaimSource);
  await auth.setCustomUserClaims(userRecord.uid, claims);
  logger.info(`Claims set for ${username}`, claims);
}

/** When the Auth account is first created (on first login), stamp claims from the profile. */
export const onAuthUserCreate = functionsV1.auth.user().onCreate(async (user) => {
  const email = user.email ?? "";
  const at = email.indexOf("@");
  if (at <= 0) return;
  const username = email.slice(0, at);
  const snap = await db.collection("users").doc(username).get();
  const claims = buildClaims((snap.data() ?? {}) as ClaimSource);
  await auth.setCustomUserClaims(user.uid, claims);
  logger.info(`Initial claims for new auth user ${username}`, claims);
});

/** When an admin edits a user profile, refresh that user's claims. */
export const onUserProfileWritten = onDocumentWritten("users/{userId}", async (event) => {
  const after = event.data?.after.data() as ClaimSource | undefined;
  if (!after) return; // deletion — the Auth account is removed by adminDeleteUser separately.
  const username = event.params.userId as string;
  await applyClaimsByUsername(username);
});

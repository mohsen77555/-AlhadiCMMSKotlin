/**
 * Shared helpers for the Al-Hadi CMMS Cloud Functions backend.
 *
 * Initializes the Admin SDK and centralizes the role model, the username<->synthetic-email mapping
 * (which MUST stay in sync with the Android FirebaseAuthGateway), id generation for server-created
 * documents, and authorization checks.
 */
import { initializeApp } from "firebase-admin/app";
import { getAuth } from "firebase-admin/auth";
import { getFirestore } from "firebase-admin/firestore";
import { HttpsError, CallableRequest } from "firebase-functions/v2/https";

initializeApp();

export const auth = getAuth();
export const db = getFirestore();

/** Roles recognized by the app (mirror of AccessControl.AppRole on the client). */
export const ROLES = [
  "SystemAdmin",
  "MaintenanceManager",
  "Technician",
  "Procurement",
  "Warehouse",
  "Requester",
] as const;
export type Role = (typeof ROLES)[number];

const EMAIL_DOMAIN = "alhadi.local";
/** Fixed suffix guaranteeing the synthetic Firebase password meets the 6-char minimum. */
const PASSWORD_SUFFIX = "#Alhadi6";

/** Maps a username to its stable synthetic email — must match the Android FirebaseAuthGateway. */
export function emailFromUsername(username: string): string {
  const local = username
    .trim()
    .toLowerCase()
    .replace(/[^a-z0-9._-]/g, "_");
  return `${local}@${EMAIL_DOMAIN}`;
}

/** Recovers the username from a synthetic email (best-effort; underscores are not reversible). */
export function usernameFromEmail(email: string | undefined): string | null {
  if (!email) return null;
  const at = email.indexOf("@");
  if (at <= 0) return null;
  const domain = email.slice(at + 1).toLowerCase();
  if (domain !== EMAIL_DOMAIN) return null;
  return email.slice(0, at);
}

/** Derives the Firebase-side password from the app password — must match the client. */
export function firebasePassword(password: string): string {
  return password + PASSWORD_SUFFIX;
}

/**
 * Server-side id for documents the backend creates, kept well above the app's sequential Room ids
 * and inside JavaScript's safe-integer range so it round-trips through Firestore and kotlinx
 * serialization as a Long without collisions.
 */
export function newId(): number {
  return Date.now() * 1000 + Math.floor(Math.random() * 1000);
}

/** Today as yyyy-MM-dd (matches DateStrings.today() on the client). */
export function today(): string {
  return new Date().toISOString().slice(0, 10);
}

/** Now as yyyy-MM-dd HH:mm (matches DateStrings.now() on the client). */
export function now(): string {
  const d = new Date();
  return `${d.toISOString().slice(0, 10)} ${d.toISOString().slice(11, 16)}`;
}

/** Throws unless the caller is authenticated. Returns the caller's uid. */
export function requireAuth(req: CallableRequest): string {
  if (!req.auth) {
    throw new HttpsError("unauthenticated", "يجب تسجيل الدخول.");
  }
  return req.auth.uid;
}

/** Throws unless the caller is a System Admin (by custom claim). */
export function requireAdmin(req: CallableRequest): void {
  requireAuth(req);
  if (req.auth?.token?.role !== "SystemAdmin") {
    throw new HttpsError("permission-denied", "هذا الإجراء مقصور على مدير النظام.");
  }
}

export function isValidRole(role: unknown): role is Role {
  return typeof role === "string" && (ROLES as readonly string[]).includes(role);
}

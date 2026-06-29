/**
 * Shared FCM helpers. Device tokens are registered by the app into `device_tokens/{token}`
 * as { token, username, role, updatedAt }.
 */
import * as logger from "firebase-functions/logger";
import { getMessaging } from "firebase-admin/messaging";
import { db } from "./common";

export async function tokensFor(field: "username" | "role", value: string): Promise<string[]> {
  if (!value) return [];
  const snap = await db.collection("device_tokens").where(field, "==", value).get();
  return snap.docs.map((d) => (d.data().token as string) ?? d.id).filter(Boolean);
}

export async function push(
  tokens: string[],
  title: string,
  body: string,
  data: Record<string, string>
): Promise<void> {
  const unique = Array.from(new Set(tokens)).filter((t) => t && t.length > 0);
  if (unique.length === 0) return;
  try {
    const res = await getMessaging().sendEachForMulticast({
      tokens: unique,
      notification: { title, body },
      data,
      android: { priority: "high" },
    });
    const stale: Promise<unknown>[] = [];
    res.responses.forEach((r, i) => {
      const code = r.error?.code;
      if (
        code === "messaging/registration-token-not-registered" ||
        code === "messaging/invalid-argument"
      ) {
        stale.push(db.collection("device_tokens").doc(unique[i]).delete().catch(() => undefined));
      }
    });
    await Promise.all(stale);
    logger.info(`Pushed "${title}" to ${unique.length} tokens (success ${res.successCount}).`);
  } catch (e) {
    logger.error(`Push failed: ${String(e)}`);
  }
}

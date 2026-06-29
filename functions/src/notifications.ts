/**
 * Push notifications (FCM). Fan out relevant events to the right people:
 *  - a work order assigned to a technician -> push to that technician
 *  - a new maintenance request (notification) -> push to managers and admins
 */
import { onDocumentCreated, onDocumentWritten } from "firebase-functions/v2/firestore";
import { push, tokensFor } from "./messaging";

/** Notify the assigned technician when a work order is created or (re)assigned. */
export const onWorkOrderWritten = onDocumentWritten("work_orders/{orderId}", async (event) => {
  const before = event.data?.before.data();
  const after = event.data?.after.data();
  if (!after) return; // deleted
  const assignedTo = (after.assignedTo as string) ?? "";
  if (!assignedTo) return;
  const isNew = !before;
  const reassigned = before && before.assignedTo !== after.assignedTo;
  if (!isNew && !reassigned) return;

  const tokens = await tokensFor("username", assignedTo);
  await push(
    tokens,
    "أمر عمل جديد موكَل إليك",
    `${after.title ?? "أمر عمل"} — الأولوية: ${after.priority ?? ""}`,
    { type: "work_order", orderId: String(event.params.orderId) }
  );
});

/** Notify managers/admins when a new maintenance request (notification) is raised. */
export const onMaintenanceRequestCreated = onDocumentCreated(
  "notifications/{notificationId}",
  async (event) => {
    const data = event.data?.data();
    if (!data) return;
    const managerTokens = await tokensFor("role", "MaintenanceManager");
    const adminTokens = await tokensFor("role", "SystemAdmin");
    await push(
      [...managerTokens, ...adminTokens],
      "بلاغ صيانة جديد",
      `${data.title ?? "بلاغ"} — ${data.priority ?? ""}`,
      { type: "notification", notificationId: String(event.params.notificationId) }
    );
  }
);

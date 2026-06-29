/**
 * Reporting & export (callable). Computes aggregate KPIs and produces CSV exports server-side so the
 * app (and any future dashboard) can fetch ready figures without scanning the whole database on the
 * device. All endpoints require an authenticated user.
 */
import { onCall, HttpsError } from "firebase-functions/v2/https";
import { db, requireAuth, today } from "./common";

/** Maintenance KPIs across work orders, inventory, and PM plans. */
export const maintenanceKpis = onCall(async (req) => {
  requireAuth(req);
  const todayStr = today();

  const [wo, parts, pm] = await Promise.all([
    db.collection("work_orders").get(),
    db.collection("spare_parts").get(),
    db.collection("preventive_maintenance").get(),
  ]);

  const byStatus: Record<string, number> = {};
  let overdue = 0;
  let totalLaborHours = 0;
  let totalPartsCost = 0;
  let closed = 0;
  const closedStatuses = new Set(["Closed", "Completed", "Cancelled"]);

  wo.docs.forEach((d) => {
    const w = d.data();
    const status = (w.status as string) ?? "Unknown";
    byStatus[status] = (byStatus[status] ?? 0) + 1;
    if (closedStatuses.has(status)) closed++;
    else if ((w.dueAt as string) && (w.dueAt as string) < todayStr) overdue++;
    totalLaborHours += (w.laborHours as number) ?? 0;
    totalPartsCost += (w.partsCost as number) ?? 0;
  });

  let lowStock = 0;
  parts.docs.forEach((d) => {
    const p = d.data();
    if (((p.onHandQty as number) ?? 0) <= ((p.minQty as number) ?? 0)) lowStock++;
  });

  let pmDue = 0;
  pm.docs.forEach((d) => {
    const m = d.data();
    if ((m.planActive as boolean) !== false && (m.nextDueAt as string) && (m.nextDueAt as string) <= todayStr) {
      pmDue++;
    }
  });

  return {
    generatedAt: todayStr,
    workOrders: {
      total: wo.size,
      open: wo.size - closed,
      closed,
      overdue,
      byStatus,
      totalLaborHours,
      totalPartsCost,
    },
    inventory: { totalParts: parts.size, lowStock },
    preventiveMaintenance: { totalPlans: pm.size, due: pmDue },
  };
});

function csvCell(value: unknown): string {
  const s = value === null || value === undefined ? "" : String(value);
  return /[",\n]/.test(s) ? `"${s.replace(/"/g, '""')}"` : s;
}

/** Exports work orders as a CSV string. Optional { status } filter. */
export const exportWorkOrdersCsv = onCall(async (req) => {
  requireAuth(req);
  const status = (req.data as { status?: string } | undefined)?.status;
  let query: FirebaseFirestore.Query = db.collection("work_orders");
  if (status) query = query.where("status", "==", status);
  const snap = await query.get();

  const headers = [
    "id",
    "title",
    "assetCode",
    "priority",
    "status",
    "assignedTo",
    "type",
    "createdAt",
    "dueAt",
    "closedAt",
    "laborHours",
    "partsCost",
  ];
  const rows = [headers.join(",")];
  snap.docs.forEach((d) => {
    const w = d.data();
    rows.push(headers.map((h) => csvCell(w[h])).join(","));
  });

  if (snap.size === 0) {
    throw new HttpsError("not-found", "لا توجد أوامر عمل مطابقة للتصدير.");
  }
  return { csv: rows.join("\n"), count: snap.size, generatedAt: today() };
});

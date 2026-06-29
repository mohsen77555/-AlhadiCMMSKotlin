/**
 * Server-side maintenance automation (scheduled Pub/Sub jobs). These mirror the in-app logic so the
 * system keeps working even when no one opens the app:
 *  - generatePreventiveWorkOrders: raise work orders for due PM plans (within their call horizon)
 *  - autoReorderLowStock: raise draft purchase orders for low-stock parts with a preferred supplier
 *  - escalateOverdueWorkOrders: notify managers about overdue, still-open work orders
 *
 * Documents the backend creates use newId() (a high, collision-free id) so they round-trip into the
 * app's Room database through the existing pull-sync.
 */
import * as logger from "firebase-functions/logger";
import { onSchedule } from "firebase-functions/v2/scheduler";
import { db, newId, today } from "./common";
import { push, tokensFor } from "./messaging";

const TZ = "Asia/Riyadh";

function addDays(yyyyMmDd: string, days: number): string {
  const d = new Date(yyyyMmDd + "T00:00:00Z");
  d.setUTCDate(d.getUTCDate() + days);
  return d.toISOString().slice(0, 10);
}

/** Raise work orders for preventive-maintenance plans that are due within their call horizon. */
export const generatePreventiveWorkOrders = onSchedule(
  { schedule: "every day 02:00", timeZone: TZ },
  async () => {
    const snap = await db.collection("preventive_maintenance").where("planActive", "==", true).get();
    const todayStr = today();
    let created = 0;

    for (const doc of snap.docs) {
      const pm = doc.data();
      const scheduleType = (pm.scheduleType as string) ?? "Time";
      const timeScheduled = scheduleType === "Time" || scheduleType === "Both";
      if (!timeScheduled) continue;

      const nextDueAt = (pm.nextDueAt as string) ?? "";
      if (!nextDueAt) continue;
      const horizon = addDays(todayStr, (pm.callHorizonDays as number) ?? 0);
      if (nextDueAt > horizon) continue; // not due yet

      // Guard: only one work order per due cycle.
      if ((pm.lastGeneratedDueAt as string) === nextDueAt) continue;

      const id = newId();
      const asset = pm.assetId
        ? (await db.collection("assets").doc(String(pm.assetId)).get()).data()
        : undefined;

      await db.collection("work_orders").doc(String(id)).set({
        id,
        assetId: pm.assetId ?? 0,
        title: pm.title ?? "صيانة دورية",
        description: `أمر عمل تلقائي من خطة الصيانة الدورية: ${pm.title ?? ""}`,
        priority: (pm.priority as string) ?? "Medium",
        status: "Open",
        assignedTo: "",
        createdAt: todayStr,
        dueAt: nextDueAt,
        estimatedCost: 0,
        type: "Preventive",
        approvalStatus: "NotRequired",
        sourcePmId: pm.id ?? Number(doc.id),
        assetCode: (asset?.code as string) ?? "",
        assetName: (asset?.name as string) ?? "",
      });

      await doc.ref.set({ lastGeneratedDueAt: nextDueAt }, { merge: true });
      created++;
    }

    logger.info(`PM generation: created ${created} work order(s).`);
  }
);

/** Raise draft purchase orders for low-stock parts that have a preferred supplier. */
export const autoReorderLowStock = onSchedule(
  { schedule: "every day 03:00", timeZone: TZ },
  async () => {
    const parts = await db.collection("spare_parts").get();
    const todayStr = today();
    const compact = todayStr.replace(/-/g, "");

    // Group low-stock parts by preferred supplier.
    const bySupplier = new Map<number, FirebaseFirestore.DocumentData[]>();
    for (const doc of parts.docs) {
      const p = doc.data();
      const onHand = (p.onHandQty as number) ?? 0;
      const minQty = (p.minQty as number) ?? 0;
      const supplierId = p.preferredSupplierId as number | null | undefined;
      if (onHand > minQty || !supplierId) continue;

      const reorderQty = (p.reorderQty as number) ?? 0;
      const maxQty = (p.maxQty as number) ?? 0;
      const safety = (p.safetyStock as number) ?? 0;
      const target =
        reorderQty > 0 ? reorderQty : maxQty > 0 ? maxQty - onHand : minQty + safety - onHand;
      if (target <= 0) continue;
      (p as Record<string, unknown>).__orderQty = target;

      const list = bySupplier.get(supplierId) ?? [];
      list.push(p);
      bySupplier.set(supplierId, list);
    }

    let created = 0;
    for (const [supplierId, supplierParts] of bySupplier) {
      const poNumber = `PO-RO-${compact}-${supplierId}`;
      // Idempotent: skip if today's auto-PO for this supplier already exists.
      const existing = await db
        .collection("purchase_orders")
        .where("poNumber", "==", poNumber)
        .limit(1)
        .get();
      if (!existing.empty) continue;

      const supplier = (await db.collection("suppliers").doc(String(supplierId)).get()).data();
      const poId = newId();
      let total = 0;
      const batch = db.batch();
      batch.set(db.collection("purchase_orders").doc(String(poId)), {
        id: poId,
        poNumber,
        supplierId,
        supplierName: (supplier?.name as string) ?? "",
        status: "Draft",
        orderDate: todayStr,
        currency: "SAR",
        notes: "أمر شراء تلقائي للنواقص (خادمي)",
        createdBy: "system",
      });
      for (const p of supplierParts) {
        const qty = (p.__orderQty as number) ?? 0;
        const price = (p.lastPrice as number) ?? 0;
        const lineId = newId();
        batch.set(db.collection("purchase_order_lines").doc(String(lineId)), {
          id: lineId,
          poId,
          partId: p.id ?? 0,
          partNumber: (p.partNumber as string) ?? "",
          description: (p.name as string) ?? "",
          quantity: qty,
          unitPrice: price,
          receivedQty: 0,
        });
        total += qty * price;
      }
      batch.update(db.collection("purchase_orders").doc(String(poId)), { totalAmount: total });
      await batch.commit();
      created++;
    }

    logger.info(`Auto-reorder: created ${created} draft purchase order(s).`);
  }
);

/** Notify managers/admins about overdue, still-open work orders (once each). */
export const escalateOverdueWorkOrders = onSchedule(
  { schedule: "every day 06:00", timeZone: TZ },
  async () => {
    const todayStr = today();
    const openStatuses = ["Open", "In Progress", "On Hold", "Assigned", "Released"];
    const snap = await db.collection("work_orders").where("status", "in", openStatuses).get();

    const managerTokens = await tokensFor("role", "MaintenanceManager");
    const adminTokens = await tokensFor("role", "SystemAdmin");
    const targets = [...managerTokens, ...adminTokens];

    let escalated = 0;
    for (const doc of snap.docs) {
      const wo = doc.data();
      const dueAt = (wo.dueAt as string) ?? "";
      if (!dueAt || dueAt >= todayStr) continue; // not overdue
      if (wo.escalatedAt) continue; // already escalated
      await push(
        targets,
        "أمر عمل متأخر",
        `${wo.title ?? "أمر عمل"} تجاوز تاريخ الاستحقاق (${dueAt}).`,
        { type: "overdue_work_order", orderId: String(doc.id) }
      );
      await doc.ref.set({ escalatedAt: todayStr }, { merge: true });
      escalated++;
    }

    logger.info(`Overdue escalation: ${escalated} work order(s).`);
  }
);

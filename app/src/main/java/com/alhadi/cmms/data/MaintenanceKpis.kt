package com.alhadi.cmms.data

import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.util.DateStrings

/**
 * Reliability and maintenance KPIs (chapter 09), derived from the governed work-order,
 * preventive-maintenance and notification data. Computed by [computeMaintenanceKpis] so the
 * reporting screen consumes a single, consistent source of truth.
 */
data class MaintenanceKpis(
    val totalAssets: Int = 0,
    /** Asset availability over the window (%). */
    val availability: Double = 0.0,
    /** Mean time between failures (hours of uptime per failure). */
    val mtbfHours: Double = 0.0,
    /** Mean time to repair (downtime hours per failure). */
    val mttrHours: Double = 0.0,
    val failureCount: Int = 0,
    val totalDowntimeHours: Double = 0.0,
    /** Share of active PM plans that are not overdue (%). */
    val pmCompliance: Double = 100.0,
    val duePmCount: Int = 0,
    val totalPmCount: Int = 0,
    /** Share of notifications needing a response that have been acknowledged (%). */
    val notificationResponseRate: Double = 100.0,
    val pendingResponseCount: Int = 0,
    val breakdownCount: Int = 0,
    val openWorkOrders: Int = 0,
    val overdueWorkOrders: Int = 0,
    val totalCost: Double = 0.0,
    val laborCost: Double = 0.0,
    val partsCost: Double = 0.0
)

/**
 * Computes the maintenance KPIs over a [windowDays]-day window. Pure function — all inputs are
 * explicit and the result depends only on them (plus the current date for overdue checks).
 */
fun computeMaintenanceKpis(
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    pmItems: List<PreventiveMaintenanceEntity>,
    notifications: List<MaintenanceNotificationEntity>,
    windowDays: Int = 30
): MaintenanceKpis {
    val failures = workOrders.filter { it.isFailure }
    val totalDowntime = failures.sumOf { it.downtimeHours }
    val failureCount = failures.size

    val windowHours = assets.size.coerceAtLeast(1) * windowDays * 24.0
    val uptimeHours = (windowHours - totalDowntime).coerceAtLeast(0.0)
    val availability = if (windowHours > 0) (uptimeHours / windowHours * 100.0).coerceIn(0.0, 100.0) else 0.0
    val mtbf = if (failureCount > 0) uptimeHours / failureCount else uptimeHours
    val mttr = if (failureCount > 0) totalDowntime / failureCount else 0.0

    val activePm = pmItems.filter { it.planActive }
    val duePm = activePm.count { DateStrings.isDueOrOverdue(it.nextDueAt) }
    val pmCompliance = if (activePm.isNotEmpty()) ((activePm.size - duePm).toDouble() / activePm.size * 100.0) else 100.0

    val pendingResponse = notifications.count { it.isResponsePending() }
    val acknowledged = notifications.count { it.acknowledgedAt.isNotBlank() }
    val responseDenominator = acknowledged + pendingResponse
    val responseRate = if (responseDenominator > 0) (acknowledged.toDouble() / responseDenominator * 100.0) else 100.0
    val breakdowns = notifications.count { it.breakdown }

    val openWos = workOrders.filter { it.status != "Closed" }
    val overdue = openWos.count { DateStrings.isDueOrOverdue(it.dueAt) }

    return MaintenanceKpis(
        totalAssets = assets.size,
        availability = availability,
        mtbfHours = mtbf,
        mttrHours = mttr,
        failureCount = failureCount,
        totalDowntimeHours = totalDowntime,
        pmCompliance = pmCompliance,
        duePmCount = duePm,
        totalPmCount = activePm.size,
        notificationResponseRate = responseRate,
        pendingResponseCount = pendingResponse,
        breakdownCount = breakdowns,
        openWorkOrders = openWos.size,
        overdueWorkOrders = overdue,
        totalCost = workOrders.sumOf { it.totalCost() },
        laborCost = workOrders.sumOf { it.laborCost() },
        partsCost = workOrders.sumOf { it.partsCost }
    )
}

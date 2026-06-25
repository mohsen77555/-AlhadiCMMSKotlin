package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_orders",
    indices = [Index(value = ["assetId"]), Index(value = ["status"]), Index(value = ["priority"])]
)
@Serializable
data class WorkOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val assignedTo: String,
    val createdAt: String,
    val dueAt: String,
    val estimatedCost: Double,
    val closeNotes: String = "",
    val isFailure: Boolean = false,
    val downtimeHours: Double = 0.0,
    val laborHours: Double = 0.0,
    val laborRate: Double = 0.0,
    val partsCost: Double = 0.0,
    val approvalStatus: String = "NotRequired",
    val approvedBy: String = "",
    val requiresPermit: Boolean = false,
    val linearStartPoint: Double? = null,
    val linearEndPoint: Double? = null,
    @ColumnInfo(defaultValue = "''")
    val linearMarker: String = "",
    val linearHorizontalOffset: Double? = null,
    val linearVerticalOffset: Double? = null,
    // --- Warranty governance (AST-WAR-008..010) ---
    /** AST-WAR-008: "Internal" repair or "WarrantyClaim" when the asset is under warranty. */
    @ColumnInfo(defaultValue = "''")
    val repairType: String = "",
    /** AST-WAR-009/010: whether the warranty was reviewed before charging internal cost. */
    @ColumnInfo(defaultValue = "0")
    val warrantyReviewed: Boolean = false,
    /** AST-WAR-010: the recorded outcome of the warranty review. */
    @ColumnInfo(defaultValue = "''")
    val warrantyReviewResult: String = "",
    // --- Work order type (WO-GOV-005 / WO-TYPE-*) ---
    @ColumnInfo(defaultValue = "'Corrective'")
    val type: String = "Corrective",
    // --- Organizational snapshot inherited from the asset (WO-ORG-001..008) ---
    @ColumnInfo(defaultValue = "''") val companyCode: String = "",
    @ColumnInfo(defaultValue = "''") val siteCode: String = "",
    @ColumnInfo(defaultValue = "''") val plantCode: String = "",
    @ColumnInfo(defaultValue = "''") val maintenancePlantCode: String = "",
    @ColumnInfo(defaultValue = "''") val planningPlantCode: String = "",
    @ColumnInfo(defaultValue = "''") val plannerGroup: String = "",
    @ColumnInfo(defaultValue = "''") val workCenter: String = "",
    @ColumnInfo(defaultValue = "''") val costCenter: String = "",
    // --- Asset / functional-location snapshot at creation (WO-AST-006/007/008) ---
    @ColumnInfo(defaultValue = "''") val assetCode: String = "",
    @ColumnInfo(defaultValue = "''") val assetName: String = "",
    @ColumnInfo(defaultValue = "''") val functionalLocation: String = "",
    // --- Failure data (WO-FLR-001..004) ---
    @ColumnInfo(defaultValue = "''") val failureCode: String = "",
    @ColumnInfo(defaultValue = "''") val failureCause: String = "",
    @ColumnInfo(defaultValue = "''") val failureEffect: String = "",
    @ColumnInfo(defaultValue = "''") val rootCause: String = "",
    // --- Planning (WO-PLAN-001) ---
    @ColumnInfo(defaultValue = "''") val plannedStart: String = "",
    // --- Lifecycle / closure (WO-GOV-004, WO-CLS-005/006) ---
    @ColumnInfo(defaultValue = "''") val cancelledReason: String = "",
    @ColumnInfo(defaultValue = "''") val closedAt: String = "",
    @ColumnInfo(defaultValue = "''") val closedBy: String = ""
) {
    /** Recorded labour cost (hours × rate). */
    fun laborCost(): Double = laborHours * laborRate

    /**
     * Realised total cost: actual labour + parts when recorded, otherwise the
     * estimate so historical/seed orders still contribute to per-asset rollups.
     */
    fun totalCost(): Double = (laborCost() + partsCost).let { if (it > 0.0) it else estimatedCost }

    /** Critical or high-value orders require supervisor/admin sign-off. */
    fun needsApproval(): Boolean = priority == "Critical" || estimatedCost >= APPROVAL_THRESHOLD

    /** Pending approval blocks starting/closing the work order. */
    fun isBlockedByApproval(): Boolean = approvalStatus == "Pending" || approvalStatus == "Rejected"

    fun hasLinearReference(): Boolean =
        linearStartPoint != null || linearEndPoint != null || linearMarker.isNotBlank() ||
            linearHorizontalOffset != null || linearVerticalOffset != null

    companion object {
        const val APPROVAL_THRESHOLD = 1000.0
    }
}

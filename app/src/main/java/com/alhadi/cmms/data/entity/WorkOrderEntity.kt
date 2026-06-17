package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_orders",
    indices = [Index(value = ["assetId"]), Index(value = ["status"]), Index(value = ["priority"])]
)
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
    val requiresPermit: Boolean = false
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

    companion object {
        const val APPROVAL_THRESHOLD = 1000.0
    }
}

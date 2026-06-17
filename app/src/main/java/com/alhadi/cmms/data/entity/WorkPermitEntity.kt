package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A work permit (تصريح عمل) attached to a work order. High-risk work cannot start without
 * an Approved, non-expired permit. [type] ∈ Hot Work / Confined Space / Electrical /
 * Working at Height / LOTO / General. [status] ∈ Pending / Approved / Rejected / Closed.
 */
@Entity(
    tableName = "work_permits",
    indices = [Index(value = ["orderId"])]
)
data class WorkPermitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val type: String,
    val hazards: String = "",
    val ppe: String = "",
    val status: String = "Pending",
    val approvedBy: String = "",
    val validUntil: String = "",
    val createdBy: String,
    val createdAt: String
) {
    /** Approved and still within its validity window on [today]. */
    fun isValidOn(today: String): Boolean =
        status == "Approved" && (validUntil.isBlank() || validUntil >= today)
}

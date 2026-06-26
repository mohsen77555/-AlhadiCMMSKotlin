package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A maintenance notification (بلاغ): the documented starting point of maintenance work —
 * a reported problem, observation, inspection finding, or request. It is screened and
 * approved, then converted into a work order ([linkedOrderId]).
 *
 * [status] ∈ New, Screened, Approved, Rejected, OrderCreated, Closed.
 * [type]   ∈ Corrective, Breakdown, Inspection, Request.
 */
@Entity(
    tableName = "maintenance_notifications",
    indices = [Index(value = ["assetId"]), Index(value = ["status"])]
)
@Serializable
data class MaintenanceNotificationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: String,
    val type: String,
    val title: String,
    val description: String,
    val assetId: Long? = null,
    val priority: String,
    val damageCode: String = "",
    val causeCode: String = "",
    val reportedBy: String,
    val reportedAt: String,
    val requiredEnd: String = "",
    val status: String,
    val linkedOrderId: Long? = null,
    val linearStartPoint: Double? = null,
    val linearEndPoint: Double? = null,
    @ColumnInfo(defaultValue = "''")
    val linearMarker: String = "",
    val linearHorizontalOffset: Double? = null,
    val linearVerticalOffset: Double? = null,
    // --- Breakdown & response governance (chapter 07) ---
    /** Flags a breakdown/downtime event (NTF-BRK-001). */
    @ColumnInfo(defaultValue = "0")
    val breakdown: Boolean = false,
    /** Effect/consequence code, completing the damage → cause → effect catalog (NTF-CAT-003). */
    @ColumnInfo(defaultValue = "''")
    val effectCode: String = "",
    /** When the malfunction began. */
    @ColumnInfo(defaultValue = "''")
    val malfunctionStart: String = "",
    /** When the asset was restored to service. */
    @ColumnInfo(defaultValue = "''")
    val malfunctionEnd: String = "",
    /** First-response stamp (NTF-SLA-002). */
    @ColumnInfo(defaultValue = "''")
    val acknowledgedAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val acknowledgedBy: String = "",
    /** Closure stamp. */
    @ColumnInfo(defaultValue = "''")
    val closedAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val closedBy: String = ""
) {
    fun hasLinearReference(): Boolean =
        linearStartPoint != null || linearEndPoint != null || linearMarker.isNotBlank() ||
            linearHorizontalOffset != null || linearVerticalOffset != null

    /** Still open (not closed or rejected). */
    fun isOpen(): Boolean = !status.equals("Closed", true) && !status.equals("Rejected", true)

    /** NTF-SLA-001: target first-response window (hours) derived from priority. */
    fun slaResponseHours(): Int = when (priority.lowercase()) {
        "critical" -> 2
        "high" -> 8
        "medium" -> 24
        else -> 48
    }

    /** Awaiting first response: still open and never acknowledged. */
    fun isResponsePending(): Boolean = isOpen() && acknowledgedAt.isBlank()
}

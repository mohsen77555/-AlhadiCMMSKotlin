package com.alhadi.cmms.data.entity

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
    val linkedOrderId: Long? = null
)

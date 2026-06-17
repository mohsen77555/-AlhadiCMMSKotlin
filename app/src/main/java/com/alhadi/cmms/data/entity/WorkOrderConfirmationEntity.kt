package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A confirmation against a work-order operation: the documented record of work actually
 * performed — hours, the damage/cause found, the action taken, and any downtime.
 * A [finalConfirmation] closes the operation.
 */
@Entity(
    tableName = "work_order_confirmations",
    indices = [Index(value = ["orderId"]), Index(value = ["operationId"])]
)
data class WorkOrderConfirmationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val operationId: Long,
    val technician: String,
    val workDate: String,
    val actualWork: Double = 0.0,
    val activityText: String = "",
    val damageFound: String = "",
    val causeFound: String = "",
    val actionTaken: String = "",
    val downtime: Double = 0.0,
    val finalConfirmation: Boolean = false,
    val createdAt: String
)

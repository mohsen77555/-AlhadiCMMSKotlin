package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single executable step (operation) inside a maintenance work order.
 * Each operation carries its own work center, planned/actual hours, and a confirmation
 * status. [status] ∈ Open, In Progress, Confirmed.
 */
@Entity(
    tableName = "work_order_operations",
    indices = [Index(value = ["orderId"])]
)
@Serializable
data class WorkOrderOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val operationNumber: String,
    val description: String,
    val workCenter: String = "",
    val plannedHours: Double = 0.0,
    val actualHours: Double = 0.0,
    val requiresConfirmation: Boolean = true,
    val status: String = "Open"
)

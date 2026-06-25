package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * An immutable change record for a work order (WO-HIS-001..005): which field
 * changed, its old and new value, who changed it and when. Rows are only ever
 * inserted — never updated or individually deleted.
 */
@Entity(
    tableName = "work_order_history",
    indices = [Index(value = ["orderId"])]
)
@Serializable
data class WorkOrderHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val field: String,
    val oldValue: String = "",
    val newValue: String = "",
    val actor: String,
    val changedAt: String
)

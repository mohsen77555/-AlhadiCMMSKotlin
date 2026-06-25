package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** A single line of a purchase order (a requested part/material with quantity and price). */
@Entity(
    tableName = "purchase_order_lines",
    indices = [Index(value = ["poId"]), Index(value = ["partId"])]
)
@Serializable
data class PurchaseOrderLineEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val poId: Long,
    val partId: Long? = null,
    @ColumnInfo(defaultValue = "''") val partNumber: String = "",
    val description: String,
    @ColumnInfo(defaultValue = "1") val quantity: Int = 1,
    @ColumnInfo(defaultValue = "0") val unitPrice: Double = 0.0,
    @ColumnInfo(defaultValue = "0") val receivedQty: Int = 0
) {
    val lineTotal: Double get() = quantity * unitPrice
    val isFullyReceived: Boolean get() = receivedQty >= quantity
}

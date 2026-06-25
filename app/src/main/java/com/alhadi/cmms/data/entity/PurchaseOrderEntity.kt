package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A purchase-order header. Status lifecycle:
 * Draft -> Approved -> Ordered -> PartiallyReceived -> Received -> Closed,
 * with Cancelled as a terminal state (orders are cancelled, never hard-deleted).
 */
@Entity(
    tableName = "purchase_orders",
    indices = [Index(value = ["supplierId"]), Index(value = ["status"])]
)
@Serializable
data class PurchaseOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val poNumber: String,
    val supplierId: Long,
    @ColumnInfo(defaultValue = "''") val supplierName: String = "",
    @ColumnInfo(defaultValue = "'Draft'") val status: String = "Draft",
    @ColumnInfo(defaultValue = "''") val orderDate: String = "",
    @ColumnInfo(defaultValue = "''") val expectedDate: String = "",
    @ColumnInfo(defaultValue = "'SAR'") val currency: String = "SAR",
    @ColumnInfo(defaultValue = "0") val totalAmount: Double = 0.0,
    @ColumnInfo(defaultValue = "''") val warehouse: String = "",
    @ColumnInfo(defaultValue = "''") val notes: String = "",
    @ColumnInfo(defaultValue = "''") val createdBy: String = "",
    @ColumnInfo(defaultValue = "''") val approvedBy: String = "",
    @ColumnInfo(defaultValue = "''") val cancelledReason: String = ""
)

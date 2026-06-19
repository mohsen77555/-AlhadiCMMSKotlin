package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "inventory_transactions",
    indices = [Index(value = ["partId"]), Index(value = ["workOrderId"]), Index(value = ["serializedItemId"])]
)
@Serializable
data class InventoryTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partId: Long,
    val workOrderId: Long?,
    val transactionType: String,
    val quantity: Int,
    val createdAt: String,
    val createdBy: String,
    val note: String,
    val serializedItemId: Long? = null,
    @ColumnInfo(defaultValue = "''")
    val batchNumber: String = "",
    @ColumnInfo(defaultValue = "''")
    val referenceNumber: String = ""
)

package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "inventory_transactions",
    indices = [Index(value = ["partId"]), Index(value = ["workOrderId"])]
)
data class InventoryTransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partId: Long,
    val workOrderId: Long?,
    val transactionType: String,
    val quantity: Int,
    val createdAt: String,
    val createdBy: String,
    val note: String
)

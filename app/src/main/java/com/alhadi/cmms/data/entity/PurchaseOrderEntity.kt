package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A purchase request / order for a spare part or material. Links to inventory (a [partId] whose
 * stock and last price update on receipt) and optionally to a work order it was raised for.
 * Status flow: Requested -> Approved -> Ordered -> Received (or Cancelled).
 */
@Serializable
@Entity(tableName = "purchase_orders")
data class PurchaseOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val number: String = "",
    val status: String = "Requested",
    val partId: Long? = null,
    val itemName: String = "",
    val quantity: Int = 1,
    val unitPrice: Double = 0.0,
    val supplier: String = "",
    val workOrderId: Long? = null,
    val requestedBy: String = "",
    val createdAt: String = "",
    val neededBy: String = "",
    val receivedAt: String = "",
    val notes: String = ""
) {
    val total: Double get() = quantity * unitPrice
}

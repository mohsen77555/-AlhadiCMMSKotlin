package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "financial_postings",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["workOrderId"]),
        Index(value = ["purchaseOrderId"])
    ]
)
@Serializable
data class FinancialPostingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val workOrderId: Long? = null,
    val purchaseOrderId: Long? = null,
    val costCategory: String,
    val amount: Double,
    val currency: String = "SAR",
    val externalDocumentNumber: String = "",
    val postingDate: String = "",
    val sourceSystem: String = "Manual",
    val status: String = "Unposted",
    val notes: String = ""
)

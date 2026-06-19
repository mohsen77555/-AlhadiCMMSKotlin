package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "asset_warranties",
    indices = [Index(value = ["assetId"]), Index(value = ["status"])]
)
@Serializable
data class AssetWarrantyEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val warrantyType: String = "Standard",
    val provider: String,
    val startDate: String = "",
    val endDate: String = "",
    val counterLimit: Double? = null,
    val counterUnit: String = "",
    val terms: String = "",
    val coveredServices: String = "",
    val excludedServices: String = "",
    val documentId: Long? = null,
    val claimContact: String = "",
    val status: String = "Active",
    val createdBy: String = "",
    val createdAt: String = ""
) {
    fun isActiveOn(date: String, currentCounter: Double? = null): Boolean {
        val withinDates = (startDate.isBlank() || date >= startDate) && (endDate.isBlank() || date <= endDate)
        val withinCounter = counterLimit == null || currentCounter == null || currentCounter <= counterLimit
        return status == "Active" && withinDates && withinCounter
    }
}

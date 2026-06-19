package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "warranty_claims",
    indices = [Index(value = ["warrantyId"]), Index(value = ["assetId"]), Index(value = ["status"])]
)
@Serializable
data class WarrantyClaimEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val warrantyId: Long,
    val assetId: Long,
    val claimNumber: String = "",
    val status: String = "Open",
    val openedAt: String = "",
    val closedAt: String = "",
    val description: String,
    val contact: String = "",
    val estimatedValue: Double = 0.0,
    val approvedValue: Double = 0.0,
    val linkedWorkOrderId: Long? = null,
    val resolution: String = "",
    val createdBy: String = ""
)

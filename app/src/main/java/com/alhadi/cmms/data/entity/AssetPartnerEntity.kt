package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A person or organisation that holds a governed role for an asset.
 * Roles include owner, operator, maintenance responsible, planner, safety,
 * finance, manufacturer, supplier, and external service provider.
 */
@Entity(
    tableName = "asset_partners",
    indices = [Index(value = ["assetId"]), Index(value = ["partnerRole"])]
)
@Serializable
data class AssetPartnerEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val partnerRole: String,
    val partnerName: String,
    val organization: String = "",
    val phone: String = "",
    val email: String = "",
    val validFrom: String = "",
    val validTo: String = "",
    val isPrimary: Boolean = false,
    val notes: String = "",
    val createdBy: String = "",
    val createdAt: String = ""
)

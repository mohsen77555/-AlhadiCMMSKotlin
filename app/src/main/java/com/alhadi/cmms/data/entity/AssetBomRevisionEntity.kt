package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "asset_bom_revisions",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["assetId", "revisionCode"], unique = true)
    ]
)
@Serializable
data class AssetBomRevisionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val revisionCode: String,
    val status: String = "Draft",
    val effectiveFrom: String = "",
    val effectiveTo: String = "",
    val changeReason: String = "",
    val approvedBy: String = "",
    val approvedAt: String = "",
    val createdBy: String = "",
    val createdAt: String = ""
)

package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A maintenance BOM line: links a spare part to an asset with a quantity. */
@Entity(
    tableName = "asset_bom_items",
    indices = [Index(value = ["assetId"]), Index(value = ["partId"])]
)
data class AssetBomItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val partId: Long,
    val quantity: Int
)

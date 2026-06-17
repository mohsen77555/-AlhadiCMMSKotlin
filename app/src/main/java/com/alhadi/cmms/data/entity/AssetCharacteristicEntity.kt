package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A flexible technical characteristic (key/value) describing an asset —
 * e.g. Flow=120 m3/h, Power=37 kW, Voltage=400 V.
 */
@Entity(
    tableName = "asset_characteristics",
    indices = [Index(value = ["assetId"])]
)
data class AssetCharacteristicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val name: String,
    val value: String,
    val unit: String
)

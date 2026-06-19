package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A governed technical characteristic describing an asset.
 */
@Entity(
    tableName = "asset_characteristics",
    indices = [Index(value = ["assetId"]), Index(value = ["characteristicCode"])]
)
@Serializable
data class AssetCharacteristicEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val name: String,
    val value: String,
    val unit: String,
    @ColumnInfo(defaultValue = "''")
    val characteristicCode: String = "",
    @ColumnInfo(defaultValue = "'Text'")
    val dataType: String = "Text",
    @ColumnInfo(defaultValue = "0")
    val isRequired: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val allowedValues: String = "",
    @ColumnInfo(defaultValue = "'Manual'")
    val source: String = "Manual",
    @ColumnInfo(defaultValue = "0")
    val isInherited: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val changedAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val changedBy: String = ""
)

package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Governed maintenance BOM line for an asset and spare part. */
@Entity(
    tableName = "asset_bom_items",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["partId"]),
        Index(value = ["revisionId"])
    ]
)
@Serializable
data class AssetBomItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val partId: Long,
    val quantity: Int,
    val revisionId: Long? = null,
    @ColumnInfo(defaultValue = "''")
    val position: String = "",
    @ColumnInfo(defaultValue = "'SparePart'")
    val componentType: String = "SparePart",
    @ColumnInfo(defaultValue = "1.0")
    val quantityPerAsset: Double = 1.0,
    @ColumnInfo(defaultValue = "'pcs'")
    val unit: String = "pcs",
    @ColumnInfo(defaultValue = "0")
    val isCritical: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val validFrom: String = "",
    @ColumnInfo(defaultValue = "''")
    val validTo: String = "",
    @ColumnInfo(defaultValue = "'Manual'")
    val source: String = "Manual",
    @ColumnInfo(defaultValue = "''")
    val notes: String = "",
    @ColumnInfo(defaultValue = "1")
    val isActive: Boolean = true
)

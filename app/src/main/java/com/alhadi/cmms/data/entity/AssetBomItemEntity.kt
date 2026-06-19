package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A maintenance BOM line: links a spare part to an asset with a quantity. */
@Entity(
    tableName = "asset_bom_items",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["partId"]),
        Index(value = ["headerId"]),
        Index(value = ["parentItemId"]),
        Index(value = ["assemblyAssetId"])
    ]
)
@Serializable
data class AssetBomItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val partId: Long,
    val quantity: Int,
    @ColumnInfo(defaultValue = "0")
    val headerId: Long = 0,
    @ColumnInfo(defaultValue = "10")
    val itemNumber: Int = 10,
    @ColumnInfo(defaultValue = "'Stock'")
    val itemCategory: String = "Stock",
    @ColumnInfo(defaultValue = "'Active'")
    val status: String = "Active",
    @ColumnInfo(defaultValue = "''")
    val validFrom: String = "",
    @ColumnInfo(defaultValue = "''")
    val validTo: String = "",
    @ColumnInfo(defaultValue = "0")
    val isCritical: Boolean = false,
    @ColumnInfo(defaultValue = "1")
    val useInOrders: Boolean = true,
    @ColumnInfo(defaultValue = "''")
    val notes: String = "",
    val parentItemId: Long? = null,
    val assemblyAssetId: Long? = null,
    @ColumnInfo(defaultValue = "''")
    val alternativeGroup: String = "",
    @ColumnInfo(defaultValue = "0")
    val isAlternative: Boolean = false
) {
    fun isActiveOn(date: String): Boolean =
        status == "Active" && (validFrom.isBlank() || validFrom <= date) && (validTo.isBlank() || validTo >= date)

    fun hasValidDates(): Boolean = validFrom.isBlank() || validTo.isBlank() || validFrom <= validTo

    fun isMaterialItem(): Boolean = itemCategory == "Stock" || itemCategory == "NonStock"
}

package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Header and assignment information for a structured maintenance component list. */
@Entity(
    tableName = "asset_bom_headers",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["constructionType"]),
        Index(value = ["code", "alternative"], unique = true)
    ]
)
@Serializable
data class AssetBomHeaderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long? = null,
    val code: String,
    val name: String,
    @ColumnInfo(defaultValue = "'Asset'")
    val category: String = "Asset",
    @ColumnInfo(defaultValue = "'Maintenance'")
    val usage: String = "Maintenance",
    @ColumnInfo(defaultValue = "'01'")
    val alternative: String = "01",
    @ColumnInfo(defaultValue = "'Active'")
    val status: String = "Active",
    @ColumnInfo(defaultValue = "''")
    val validFrom: String = "",
    @ColumnInfo(defaultValue = "''")
    val validTo: String = "",
    @ColumnInfo(defaultValue = "''")
    val revision: String = "",
    @ColumnInfo(defaultValue = "'Direct'")
    val assignmentType: String = "Direct",
    @ColumnInfo(defaultValue = "''")
    val constructionType: String = "",
    @ColumnInfo(defaultValue = "''")
    val description: String = ""
) {
    fun isActiveOn(date: String): Boolean =
        status == "Active" && (validFrom.isBlank() || validFrom <= date) && (validTo.isBlank() || validTo >= date)

    fun hasValidDates(): Boolean = validFrom.isBlank() || validTo.isBlank() || validFrom <= validTo
}

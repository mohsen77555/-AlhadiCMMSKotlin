package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "asset_documents",
    indices = [Index(value = ["assetId"]), Index(value = ["supersedesDocumentId"])]
)
@Serializable
data class AssetDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val type: String,
    val title: String,
    val reference: String,
    val uploadedBy: String,
    val uploadedAt: String,
    @ColumnInfo(defaultValue = "''")
    val description: String = "",
    @ColumnInfo(defaultValue = "'1.0'")
    val version: String = "1.0",
    @ColumnInfo(defaultValue = "'Current'")
    val status: String = "Current",
    @ColumnInfo(defaultValue = "''")
    val documentDate: String = "",
    @ColumnInfo(defaultValue = "''")
    val expiryDate: String = "",
    val supersedesDocumentId: Long? = null,
    @ColumnInfo(defaultValue = "''")
    val categoryCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val checksum: String = ""
) {
    fun isExpired(today: String): Boolean = expiryDate.isNotBlank() && expiryDate < today
}

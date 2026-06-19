package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "spare_parts",
    indices = [Index(value = ["partNumber"], unique = true)]
)
@Serializable
data class SparePartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partNumber: String,
    val name: String,
    val equipmentGroup: String,
    val unit: String,
    val onHandQty: Int,
    val minQty: Int,
    val location: String,
    val lastPrice: Double,
    @ColumnInfo(defaultValue = "0")
    val isSerialized: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val requiresBatch: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val manufacturerPartNumber: String = "",
    @ColumnInfo(defaultValue = "''")
    val valuationClass: String = "",
    @ColumnInfo(defaultValue = "'SAR'")
    val currency: String = "SAR",
    @ColumnInfo(defaultValue = "0.0")
    val standardPrice: Double = 0.0,
    @ColumnInfo(defaultValue = "''")
    val lastCountedAt: String = ""
)

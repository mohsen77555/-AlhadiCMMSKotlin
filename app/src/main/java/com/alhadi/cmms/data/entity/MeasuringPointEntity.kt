package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "measuring_points",
    indices = [Index(value = ["assetId"]), Index(value = ["pointCode"], unique = true)]
)
@Serializable
data class MeasuringPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val name: String,
    val unit: String,
    val isCounter: Boolean,
    val upperLimit: Double?,
    val lastReading: Double,
    val lastReadingAt: String,
    @ColumnInfo(defaultValue = "''")
    val pointCode: String = "",
    @ColumnInfo(defaultValue = "'Measurement'")
    val measurementType: String = "Measurement",
    val lowerLimit: Double? = null,
    val lowerWarningLimit: Double? = null,
    val upperWarningLimit: Double? = null,
    @ColumnInfo(defaultValue = "'Active'")
    val status: String = "Active",
    val functionalLocationId: Long? = null,
    @ColumnInfo(defaultValue = "'Manual'")
    val sourceType: String = "Manual",
    @ColumnInfo(defaultValue = "''")
    val meterSerial: String = "",
    @ColumnInfo(defaultValue = "''")
    val createdAt: String = ""
)

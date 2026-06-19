package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "measurement_readings",
    indices = [Index(value = ["pointId"]), Index(value = ["assetId"]), Index(value = ["referenceTime"])]
)
@Serializable
data class MeasurementReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pointId: Long,
    val assetId: Long,
    val value: Double,
    val createdAt: String,
    val createdBy: String,
    val note: String,
    @ColumnInfo(defaultValue = "''")
    val readingUnit: String = "",
    @ColumnInfo(defaultValue = "''")
    val referenceTime: String = "",
    @ColumnInfo(defaultValue = "'Manual'")
    val source: String = "Manual",
    @ColumnInfo(defaultValue = "'Accepted'")
    val processingStatus: String = "Accepted",
    @ColumnInfo(defaultValue = "''")
    val additionalInfo: String = "",
    @ColumnInfo(defaultValue = "''")
    val resultData: String = "",
    @ColumnInfo(defaultValue = "0")
    val actionRequired: Boolean = false,
    val correctedFromReadingId: Long? = null
)

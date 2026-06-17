package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "measurement_readings",
    indices = [Index(value = ["pointId"]), Index(value = ["assetId"])]
)
data class MeasurementReadingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pointId: Long,
    val assetId: Long,
    val value: Double,
    val createdAt: String,
    val createdBy: String,
    val note: String
)

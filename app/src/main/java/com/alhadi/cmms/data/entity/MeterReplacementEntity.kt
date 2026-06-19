package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "meter_replacements",
    indices = [Index(value = ["pointId"]), Index(value = ["assetId"])]
)
@Serializable
data class MeterReplacementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pointId: Long,
    val assetId: Long,
    val oldMeterSerial: String = "",
    val newMeterSerial: String = "",
    val finalOldReading: Double = 0.0,
    val initialNewReading: Double = 0.0,
    val replacedAt: String = "",
    val replacedBy: String = "",
    val reason: String = ""
)

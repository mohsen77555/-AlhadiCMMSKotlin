package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A measuring point on an asset (running hours, temperature, vibration, pressure...).
 * Either a cumulative counter or a plain reading, with optional upper/lower alarm limits.
 */
@Entity(
    tableName = "measuring_points",
    indices = [Index(value = ["assetId"])]
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
    // --- Measurement governance (chapter 08) ---
    /** Lower alarm limit; a reading below this is an alarm (MEAS-LIM-002). */
    val lowerLimit: Double? = null,
    /** Warning band before a limit; 0 disables warnings (MEAS-LIM-003). */
    @ColumnInfo(defaultValue = "0")
    val warningMargin: Double = 0.0,
    /** Raise a maintenance notification automatically when a reading breaches a limit (MEAS-ALM-010). */
    @ColumnInfo(defaultValue = "0")
    val autoNotifyOnAlarm: Boolean = false
) {
    /** Classifies a reading against the limits: Alarm (breach) / Warning (within margin) / Normal. */
    fun readingStatus(value: Double): String {
        val high = upperLimit
        val low = lowerLimit
        if ((high != null && value > high) || (low != null && value < low)) return "Alarm"
        if (warningMargin > 0) {
            if (high != null && value >= high - warningMargin) return "Warning"
            if (low != null && value <= low + warningMargin) return "Warning"
        }
        return "Normal"
    }
}

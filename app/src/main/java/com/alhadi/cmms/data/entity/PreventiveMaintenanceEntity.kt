package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "preventive_maintenance",
    indices = [Index(value = ["assetId"]), Index(value = ["nextDueAt"])]
)
@Serializable
data class PreventiveMaintenanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val title: String,
    val frequencyDays: Int,
    val lastDoneAt: String,
    val nextDueAt: String,
    val status: String,
    val estimatedDurationMinutes: Int,
    val taskListId: Long? = null,
    // --- Maintenance plan governance (chapter 06) ---
    /** Scheduling basis: Time / Counter / Both (PM-SCH-001). */
    @ColumnInfo(defaultValue = "'Time'")
    val scheduleType: String = "Time",
    /** Measuring point (counter) driving counter-based scheduling. */
    val measuringPointId: Long? = null,
    /** Trigger a due cycle every this many counter units (PM-SCH-010). */
    @ColumnInfo(defaultValue = "0")
    val counterInterval: Double = 0.0,
    /** Counter reading captured at the last completion. */
    @ColumnInfo(defaultValue = "0")
    val lastCounterReading: Double = 0.0,
    /** Counter reading at which the next cycle becomes due. */
    @ColumnInfo(defaultValue = "0")
    val nextCounterReading: Double = 0.0,
    /** Call horizon: generate the work order this many days before the due date (PM-SCH-020). */
    @ColumnInfo(defaultValue = "0")
    val callHorizonDays: Int = 0,
    /** Priority applied to work orders generated from this plan. */
    @ColumnInfo(defaultValue = "'Medium'")
    val priority: String = "Medium",
    /** Floating schedule (next due from actual completion) vs fixed (from planned date) — PM-SCH-030. */
    @ColumnInfo(defaultValue = "1")
    val floatingSchedule: Boolean = true,
    /** Plan active flag; an inactive plan is suspended, not deleted (PM-GOV-002). */
    @ColumnInfo(defaultValue = "1")
    val planActive: Boolean = true,
    /** Maintenance strategy label (e.g. weekly/monthly package). */
    @ColumnInfo(defaultValue = "''")
    val strategy: String = ""
) {
    val isCounterScheduled: Boolean get() = scheduleType == "Counter" || scheduleType == "Both"
    val isTimeScheduled: Boolean get() = scheduleType == "Time" || scheduleType == "Both"

    /** Counter-based due when the current reading has reached the next trigger reading. */
    fun isCounterDue(currentReading: Double): Boolean =
        isCounterScheduled && counterInterval > 0 && nextCounterReading > 0 && currentReading >= nextCounterReading
}

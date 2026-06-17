package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "preventive_maintenance",
    indices = [Index(value = ["assetId"]), Index(value = ["nextDueAt"])]
)
data class PreventiveMaintenanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val title: String,
    val frequencyDays: Int,
    val lastDoneAt: String,
    val nextDueAt: String,
    val status: String,
    val estimatedDurationMinutes: Int
)

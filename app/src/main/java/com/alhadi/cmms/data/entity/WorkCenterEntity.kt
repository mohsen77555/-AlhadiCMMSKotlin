package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "work_centers", indices = [Index(value = ["plantId", "code"], unique = true)])
@Serializable
data class WorkCenterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val plantId: Long,
    val discipline: String = "",
    val capacityType: String = "",
    val defaultHourlyRate: Double = 0.0,
    val status: String = "Active",
    val createdBy: String = "System",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = ""
)

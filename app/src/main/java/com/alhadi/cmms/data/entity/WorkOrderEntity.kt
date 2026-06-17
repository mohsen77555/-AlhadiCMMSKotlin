package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "work_orders",
    indices = [Index(value = ["assetId"]), Index(value = ["status"]), Index(value = ["priority"])]
)
data class WorkOrderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val title: String,
    val description: String,
    val priority: String,
    val status: String,
    val assignedTo: String,
    val createdAt: String,
    val dueAt: String,
    val estimatedCost: Double,
    val closeNotes: String = "",
    val isFailure: Boolean = false,
    val downtimeHours: Double = 0.0
)

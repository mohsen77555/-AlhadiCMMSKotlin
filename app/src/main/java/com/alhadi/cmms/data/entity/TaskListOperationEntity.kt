package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/** A template operation belonging to a [TaskListEntity]. */
@Entity(
    tableName = "task_list_operations",
    indices = [Index(value = ["taskListId"])]
)
data class TaskListOperationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskListId: Long,
    val operationNumber: String,
    val description: String,
    val workCenter: String = "",
    val plannedHours: Double = 0.0
)

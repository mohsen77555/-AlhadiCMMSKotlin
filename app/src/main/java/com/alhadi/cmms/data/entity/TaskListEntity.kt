package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A reusable work template (قالب عمل). It holds a set of template operations
 * ([TaskListOperationEntity]) that are copied into a work order when one is generated
 * from a linked preventive-maintenance plan.
 */
@Entity(tableName = "task_lists")
data class TaskListEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val defaultWorkCenter: String = ""
)

package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "planner_groups", indices = [Index(value = ["planningPlantId", "code"], unique = true)])
@Serializable
data class PlannerGroupEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val planningPlantId: Long,
    val discipline: String = "",
    val responsibleUserId: Long? = null,
    val status: String = "Active",
    val createdBy: String = "System",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = ""
)

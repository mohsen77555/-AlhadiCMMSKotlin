package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A functional location: a physical/logical place where assets are installed
 * (Factory → Line → Area → Position). Supports a parent/child hierarchy.
 */
@Entity(
    tableName = "functional_locations",
    indices = [Index(value = ["code"], unique = true), Index(value = ["parentId"])]
)
@Serializable
data class FunctionalLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    val parentId: Long?,
    val description: String,
    val status: String = "Active",
    val companyId: String = "",
    val siteId: String = "",
    val plantId: String = "",
    val maintenancePlantId: String = "",
    val planningPlantId: String = "",
    val workCenterId: String = "",
    val plannerGroupId: String = "",
    val departmentId: String = "",
    val costCenterId: String = "",
    val physicalLocation: String = "",
    val building: String = "",
    val floor: String = "",
    val room: String = "",
    val area: String = "",
    val line: String = "",
    val position: String = "",
    val level: Int = 0,
    val path: String = "",
    val createdBy: String = "System",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = ""
)

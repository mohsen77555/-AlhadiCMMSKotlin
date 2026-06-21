package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "plants", indices = [Index(value = ["companyId", "code"], unique = true), Index(value = ["siteId"])])
@Serializable
data class PlantEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyId: Long,
    val siteId: Long? = null,
    val code: String,
    val name: String,
    val plantType: String = "Operational",
    val status: String = "Active",
    val createdBy: String = "System",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = ""
)

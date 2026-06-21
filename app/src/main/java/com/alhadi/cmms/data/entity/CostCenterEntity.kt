package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "cost_centers", indices = [Index(value = ["companyId", "code"], unique = true), Index(value = ["departmentId"])])
@Serializable
data class CostCenterEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val companyId: Long,
    val departmentId: Long? = null,
    val validFrom: String = "",
    val validTo: String = "",
    val status: String = "Active",
    val createdBy: String = "System",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = ""
)

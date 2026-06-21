package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "sites", indices = [Index(value = ["companyId", "code"], unique = true)])
@Serializable
data class SiteEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val companyId: Long,
    val code: String,
    val name: String,
    val physicalAddress: String = "",
    val status: String = "Active",
    val createdBy: String = "System",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = ""
)

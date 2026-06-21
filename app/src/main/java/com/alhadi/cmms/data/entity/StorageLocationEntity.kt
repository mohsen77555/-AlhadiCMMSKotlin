package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "storage_locations", indices = [Index(value = ["plantId", "code"], unique = true)])
@Serializable
data class StorageLocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val code: String,
    val name: String,
    val plantId: Long,
    val storageType: String = "General",
    val status: String = "Active",
    val createdBy: String = "System",
    val createdAt: String = "",
    val updatedBy: String = "",
    val updatedAt: String = ""
)

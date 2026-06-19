package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "serialized_items",
    indices = [
        Index(value = ["serialNumber"], unique = true),
        Index(value = ["assetId"]),
        Index(value = ["partId"]),
        Index(value = ["currentAssetId"]),
        Index(value = ["status"])
    ]
)
@Serializable
data class SerializedItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serialNumber: String,
    val itemType: String = "SparePart",
    val assetId: Long? = null,
    val partId: Long? = null,
    val currentAssetId: Long? = null,
    val status: String = "InStock",
    val currentLocation: String = "",
    val batchNumber: String = "",
    val manufacturerSerial: String = "",
    val installedAt: String = "",
    val removedAt: String = "",
    val warrantyEnd: String = "",
    val currentWorkOrderId: Long? = null,
    val notes: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

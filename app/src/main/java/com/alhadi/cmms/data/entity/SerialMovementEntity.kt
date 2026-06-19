package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "serial_movements",
    indices = [Index(value = ["serializedItemId"]), Index(value = ["workOrderId"])]
)
@Serializable
data class SerialMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serializedItemId: Long,
    val movementType: String,
    val fromAssetId: Long? = null,
    val toAssetId: Long? = null,
    val fromLocation: String = "",
    val toLocation: String = "",
    val workOrderId: Long? = null,
    val occurredAt: String,
    val performedBy: String,
    val note: String = ""
)

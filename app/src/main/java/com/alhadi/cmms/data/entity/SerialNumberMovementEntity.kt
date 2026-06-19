package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Immutable movement history for one serialized unit. */
@Entity(
    tableName = "serial_number_movements",
    indices = [
        Index(value = ["serialId"]),
        Index(value = ["partId"]),
        Index(value = ["workOrderId"]),
        Index(value = ["createdAt"])
    ]
)
@Serializable
data class SerialNumberMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serialId: Long,
    val partId: Long,
    val workOrderId: Long? = null,
    val movementType: String,
    @ColumnInfo(defaultValue = "''")
    val fromStatus: String = "",
    @ColumnInfo(defaultValue = "''")
    val toStatus: String = "",
    @ColumnInfo(defaultValue = "''")
    val fromPlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val toPlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val fromStorageLocation: String = "",
    @ColumnInfo(defaultValue = "''")
    val toStorageLocation: String = "",
    @ColumnInfo(defaultValue = "''")
    val fromStockType: String = "",
    @ColumnInfo(defaultValue = "''")
    val toStockType: String = "",
    val createdAt: String,
    val createdBy: String,
    @ColumnInfo(defaultValue = "''")
    val note: String = ""
)

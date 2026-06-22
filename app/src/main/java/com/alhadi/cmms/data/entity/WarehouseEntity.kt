package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A storage warehouse / store where spare parts are physically kept.
 * Spare parts reference a warehouse by its [code] through `SparePartEntity.location`.
 */
@Entity(
    tableName = "warehouses",
    indices = [Index(value = ["code"], unique = true)]
)
@Serializable
data class WarehouseEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    @ColumnInfo(defaultValue = "''")
    val location: String = "",
    @ColumnInfo(defaultValue = "''")
    val keeper: String = "",
    @ColumnInfo(defaultValue = "''")
    val phone: String = "",
    @ColumnInfo(defaultValue = "'Main'")
    val type: String = "Main",
    @ColumnInfo(defaultValue = "'Active'")
    val status: String = "Active",
    @ColumnInfo(defaultValue = "''")
    val notes: String = ""
)

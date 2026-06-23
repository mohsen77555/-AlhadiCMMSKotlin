package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Rules that control creation, stock verification, and movement of serialized parts. */
@Entity(
    tableName = "serial_number_profiles",
    indices = [Index(value = ["code"], unique = true)]
)
@Serializable
data class SerialNumberProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    @ColumnInfo(defaultValue = "1")
    val requireOnReceipt: Boolean = true,
    @ColumnInfo(defaultValue = "1")
    val requireOnIssue: Boolean = true,
    @ColumnInfo(defaultValue = "1")
    val autoCreate: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    val equipmentRequired: Boolean = false,
    @ColumnInfo(defaultValue = "'Block'")
    val stockCheckMode: String = "Block",
    @ColumnInfo(defaultValue = "0")
    val allowManualStockEdit: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val equipmentCategory: String = "",
    @ColumnInfo(defaultValue = "''")
    val description: String = ""
)

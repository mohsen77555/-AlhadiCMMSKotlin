package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

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
    @ColumnInfo(defaultValue = "''")
    val organizationCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val plantCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val locationCategory: String = "",
    @ColumnInfo(defaultValue = "''")
    val costCenterCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val workCenterCode: String = "",
    val referenceLocationId: Long? = null,
    @ColumnInfo(defaultValue = "0")
    val isReference: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val createdAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val updatedAt: String = ""
)

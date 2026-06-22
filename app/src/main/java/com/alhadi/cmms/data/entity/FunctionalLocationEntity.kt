package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A functional location: a physical/logical place where assets are installed
 * (Factory → Line → Area → Position). Supports a parent/child hierarchy and carries the
 * organizational units (plant / work center / cost center / planner group) that assets
 * installed in it inherit by default (AST-ORG-009/010).
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
    val plantCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val workCenterCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val costCenterCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val plannerGroupCode: String = ""
) {
    val isActive: Boolean get() = status.equals("Active", ignoreCase = true)
}

package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A generic organizational master-data unit: Company, Plant, Work Center, Cost Center,
 * Planner Group, or Department. Assets reference these by [code] so the asset form can
 * validate that an active asset only points at active organizational units (AST-ORG-SAVE-*).
 */
@Entity(
    tableName = "org_units",
    indices = [Index(value = ["type", "code"], unique = true)]
)
@Serializable
data class OrgUnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    @ColumnInfo(defaultValue = "'WorkCenter'")
    val type: String = "WorkCenter",
    val code: String,
    val name: String,
    @ColumnInfo(defaultValue = "'Active'")
    val status: String = "Active",
    val parentId: Long? = null,
    @ColumnInfo(defaultValue = "''")
    val notes: String = ""
) {
    val isActive: Boolean get() = status.equals("Active", ignoreCase = true)
}

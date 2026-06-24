package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A generic organizational master-data unit covering the full org hierarchy
 * (Company → Site → Plant → Maintenance/Planning Plant, Work Center, Storage
 * Location, Department → Cost Center, Planner Group). The [type] discriminates
 * the kind; only the fields relevant to that type are filled by the form.
 * Assets reference these by [code] so an active asset only points at active
 * organizational units (AST-ORG-SAVE-* / ORG-*).
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
    val notes: String = "",
    // --- Company identity (ORG-COMP) ---
    @ColumnInfo(defaultValue = "''") val shortName: String = "",
    @ColumnInfo(defaultValue = "''") val legalName: String = "",
    @ColumnInfo(defaultValue = "''") val taxNumber: String = "",
    @ColumnInfo(defaultValue = "''") val commercialRegistration: String = "",
    // --- Geography (Company / Site) ---
    @ColumnInfo(defaultValue = "''") val country: String = "",
    @ColumnInfo(defaultValue = "''") val region: String = "",
    @ColumnInfo(defaultValue = "''") val city: String = "",
    @ColumnInfo(defaultValue = "''") val address: String = "",
    @ColumnInfo(defaultValue = "''") val phone: String = "",
    @ColumnInfo(defaultValue = "''") val email: String = "",
    @ColumnInfo(defaultValue = "''") val website: String = "",
    val latitude: Double? = null,
    val longitude: Double? = null,
    // --- Work Center / Department people & capacity ---
    @ColumnInfo(defaultValue = "''") val capacity: String = "",
    @ColumnInfo(defaultValue = "''") val supervisor: String = "",
    @ColumnInfo(defaultValue = "''") val manager: String = ""
) {
    val isActive: Boolean get() = status.equals("Active", ignoreCase = true)
}

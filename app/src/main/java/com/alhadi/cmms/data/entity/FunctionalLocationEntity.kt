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
    val plannerGroupCode: String = "",
    // --- Functional-location governance (chapter 02) ---
    /** FLOC category: Standard / Production / Building / Utility / Storage / Outdoor. */
    @ColumnInfo(defaultValue = "'Standard'")
    val category: String = "Standard",
    /** Lifecycle / system status: Planned / Created / Installed / Inactive (FLOC-020). */
    @ColumnInfo(defaultValue = "'Created'")
    val lifecycleStatus: String = "Created",
    /** Criticality / ABC indicator: A (high) / B / C / blank. */
    @ColumnInfo(defaultValue = "''")
    val abcIndicator: String = "",
    /** Sort field used for structured reporting. */
    @ColumnInfo(defaultValue = "''")
    val sortField: String = "",
    /** Authorization group controlling who may maintain this location. */
    @ColumnInfo(defaultValue = "''")
    val authorizationGroup: String = "",
    /** Installation position: when true only ONE asset may be installed here at a time (FLOC-031). */
    @ColumnInfo(defaultValue = "0")
    val singleInstallation: Boolean = false,
    /** A reference (template) functional location, not a real installed place (FLOC-040). */
    @ColumnInfo(defaultValue = "0")
    val isReference: Boolean = false,
    /** Code of the reference location this real location was created from. */
    @ColumnInfo(defaultValue = "''")
    val referenceCode: String = "",
    /** Physical room / position detail within the plant. */
    @ColumnInfo(defaultValue = "''")
    val room: String = "",
    /** Plant section the location belongs to. */
    @ColumnInfo(defaultValue = "''")
    val plantSection: String = ""
) {
    val isActive: Boolean get() = status.equals("Active", ignoreCase = true)
}

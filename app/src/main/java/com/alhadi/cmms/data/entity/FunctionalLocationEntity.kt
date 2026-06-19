package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "functional_locations",
    indices = [Index(value = ["code"], unique = true), Index(value = ["parentId"]), Index(value = ["referenceLocationId"])]
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
    val updatedAt: String = "",
    @ColumnInfo(defaultValue = "1")
    val inheritFromParent: Boolean = true,
    @ColumnInfo(defaultValue = "1")
    val inheritFromReference: Boolean = true,
    @ColumnInfo(defaultValue = "''")
    val defaultOwnerDepartment: String = "",
    @ColumnInfo(defaultValue = "''")
    val defaultPlanningGroup: String = "",
    @ColumnInfo(defaultValue = "''")
    val defaultCriticality: String = "",
    @ColumnInfo(defaultValue = "''")
    val defaultAssetType: String = "",
    @ColumnInfo(defaultValue = "''")
    val defaultMaintenanceStrategy: String = ""
)

package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "assets",
    indices = [Index(value = ["code"], unique = true), Index(value = ["assetClassId"])]
)
@Serializable
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    val groupName: String,
    val location: String,
    val manufacturer: String,
    val model: String,
    val status: String,
    val criticality: String,
    val installedAt: String,
    val lastInspectionAt: String,
    val locationId: Long? = null,
    val warrantyProvider: String = "",
    val warrantyStart: String = "",
    val warrantyEnd: String = "",
    val parentAssetId: Long? = null,
    val serialNumber: String = "",
    val assetTag: String = "",
    val supplier: String = "",
    val purchaseOrder: String = "",
    val purchaseCost: Double = 0.0,
    val acquiredAt: String = "",

    @ColumnInfo(defaultValue = "'Equipment'")
    val assetType: String = "Equipment",
    @ColumnInfo(defaultValue = "''")
    val assetCategory: String = "",
    @ColumnInfo(defaultValue = "''")
    val description: String = "",
    @ColumnInfo(defaultValue = "''")
    val organizationCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val plantCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val maintenanceWorkCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val planningGroup: String = "",
    @ColumnInfo(defaultValue = "''")
    val costCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val ownerDepartment: String = "",
    @ColumnInfo(defaultValue = "''")
    val responsiblePerson: String = "",
    val manufacturingYear: Int? = null,
    @ColumnInfo(defaultValue = "''")
    val purchaseDate: String = "",
    @ColumnInfo(defaultValue = "''")
    val commissioningDate: String = "",
    @ColumnInfo(defaultValue = "''")
    val financialAssetRef: String = "",
    val assetClassId: Long? = null,

    @ColumnInfo(defaultValue = "'InService'")
    val lifecycleStatus: String = "InService",
    @ColumnInfo(defaultValue = "''")
    val operationalStatus: String = "",
    @ColumnInfo(defaultValue = "'Good'")
    val healthStatus: String = "Good",

    @ColumnInfo(defaultValue = "1")
    val criticalitySafetyImpact: Int = 1,
    @ColumnInfo(defaultValue = "1")
    val criticalityProductionImpact: Int = 1,
    @ColumnInfo(defaultValue = "1")
    val criticalityEnvironmentalImpact: Int = 1,
    @ColumnInfo(defaultValue = "1")
    val criticalityServiceImpact: Int = 1,
    @ColumnInfo(defaultValue = "1")
    val criticalityFinancialImpact: Int = 1,
    @ColumnInfo(defaultValue = "5")
    val criticalityScore: Int = 5,
    @ColumnInfo(defaultValue = "''")
    val criticalityAssessedAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val criticalityAssessedBy: String = "",

    @ColumnInfo(defaultValue = "''")
    val createdBy: String = "",
    @ColumnInfo(defaultValue = "''")
    val createdAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val updatedBy: String = "",
    @ColumnInfo(defaultValue = "''")
    val updatedAt: String = ""
) {
    fun isUnderWarranty(today: String): Boolean =
        warrantyEnd.isNotBlank() && today <= warrantyEnd && (warrantyStart.isBlank() || warrantyStart <= today)

    fun effectiveOperationalStatus(): String = operationalStatus.ifBlank { status }

    fun calculatedCriticalityScore(): Int = listOf(
        criticalitySafetyImpact,
        criticalityProductionImpact,
        criticalityEnvironmentalImpact,
        criticalityServiceImpact,
        criticalityFinancialImpact
    ).sum()

    fun calculatedCriticalityRating(): String = when (calculatedCriticalityScore()) {
        in 0..7 -> "Low"
        in 8..12 -> "Medium"
        in 13..18 -> "High"
        else -> "Critical"
    }
}

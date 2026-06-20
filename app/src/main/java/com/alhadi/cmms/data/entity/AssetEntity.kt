package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets",
    indices = [Index(value = ["code"], unique = true)]
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
    val assetType: String = "Equipment",
    val assetCategory: String = "",
    val equipmentCategory: String = "",
    val objectType: String = "",
    val assetClass: String = "",
    val assetSubclass: String = "",
    val description: String = "",
    val longDescription: String = "",
    val alternativeLabel: String = "",
    val externalAssetCode: String = "",
    val legacyAssetCode: String = "",
    val barcode: String = "",
    val qrCode: String = "",
    val companyId: String = "",
    val siteId: String = "",
    val plantId: String = "",
    val maintenancePlantId: String = "",
    val planningPlantId: String = "",
    val workCenterId: String = "",
    val plannerGroupId: String = "",
    val costCenterId: String = "",
    val departmentId: String = "",
    val responsiblePersonId: String = "",
    val constructionType: String = "",
    val commissioningAt: String = "",
    val financialAssetRef: String = "",
    val notes: String = "",
    val partners: String = "",
    val safetyCritical: Boolean = false,
    val riskLevel: String = "",
    val requiredPermits: String = "",
    val safetyInstructions: String = "",
    val ppeRequired: String = "",
    val isolationRequired: Boolean = false,
    val complianceRequirements: String = "",
    val financialStatus: String = "",
    val bookValue: Double = 0.0,
    val capitalizationAt: String = "",
    val linearStartPoint: String = "",
    val linearEndPoint: String = "",
    val linearLength: Double = 0.0,
    val linearUnit: String = "",
    val linearRoute: String = ""
) {
    /** Whether the asset is currently covered by warranty on the given date. */
    fun isUnderWarranty(today: String): Boolean =
        warrantyEnd.isNotBlank() && today <= warrantyEnd && (warrantyStart.isBlank() || warrantyStart <= today)
}

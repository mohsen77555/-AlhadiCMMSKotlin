package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
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
    @ColumnInfo(defaultValue = "'Machine'")
    val category: String = "Machine",
    @ColumnInfo(defaultValue = "''")
    val objectType: String = "",
    @ColumnInfo(defaultValue = "''")
    val description: String = "",
    @ColumnInfo(defaultValue = "''")
    val maintenancePlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val planningPlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val plannerGroup: String = "",
    @ColumnInfo(defaultValue = "''")
    val mainWorkCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val productionWorkCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val costCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val responsiblePerson: String = "",
    @ColumnInfo(defaultValue = "''")
    val assetNumber: String = "",
    @ColumnInfo(defaultValue = "''")
    val constructionYear: String = "",
    @ColumnInfo(defaultValue = "''")
    val constructionMonth: String = "",
    @ColumnInfo(defaultValue = "''")
    val startupDate: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerName: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerRole: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerPhone: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerEmail: String = "",
    @ColumnInfo(defaultValue = "''")
    val addressLine: String = "",
    @ColumnInfo(defaultValue = "''")
    val city: String = "",
    @ColumnInfo(defaultValue = "''")
    val country: String = "",
    @ColumnInfo(defaultValue = "''")
    val standardClass: String = "",
    @ColumnInfo(defaultValue = "''")
    val constructionType: String = "",
    @ColumnInfo(defaultValue = "1")
    val inheritParentCharacteristics: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    val isLinearAsset: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val linearStartPoint: Double = 0.0,
    @ColumnInfo(defaultValue = "0")
    val linearEndPoint: Double = 0.0,
    @ColumnInfo(defaultValue = "'km'")
    val linearUnit: String = "km",
    @ColumnInfo(defaultValue = "''")
    val linearReferencePattern: String = "",
    @ColumnInfo(defaultValue = "''")
    val linearRouteCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val linearStartMarker: String = "",
    @ColumnInfo(defaultValue = "''")
    val linearEndMarker: String = "",
    @ColumnInfo(defaultValue = "0")
    val linearStartMarkerDistance: Double = 0.0,
    @ColumnInfo(defaultValue = "0")
    val linearEndMarkerDistance: Double = 0.0,
    @ColumnInfo(defaultValue = "'km'")
    val linearMarkerUnit: String = "km",
    @ColumnInfo(defaultValue = "0")
    val linearHorizontalOffset: Double = 0.0,
    @ColumnInfo(defaultValue = "0")
    val linearVerticalOffset: Double = 0.0,
    @ColumnInfo(defaultValue = "'m'")
    val linearOffsetUnit: String = "m",
    @ColumnInfo(defaultValue = "'Both'")
    val linearDirection: String = "Both",
    @ColumnInfo(defaultValue = "''")
    val networkObjectCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val networkObjectType: String = "",
    @ColumnInfo(defaultValue = "''")
    val networkRelation: String = "",
    @ColumnInfo(defaultValue = "''")
    val networkAttributes: String = "",
    val linearStartLatitude: Double? = null,
    val linearStartLongitude: Double? = null,
    val linearEndLatitude: Double? = null,
    val linearEndLongitude: Double? = null,
    val linkedSerialId: Long? = null,
    val serializedPartId: Long? = null,
    // --- Asset identity governance: secondary identifiers ---
    @ColumnInfo(defaultValue = "''")
    val longDescription: String = "",
    @ColumnInfo(defaultValue = "''")
    val alternativeLabel: String = "",
    @ColumnInfo(defaultValue = "''")
    val externalAssetCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val legacyAssetCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val barcode: String = "",
    @ColumnInfo(defaultValue = "''")
    val qrCode: String = "",
    // --- Classification hierarchy ---
    @ColumnInfo(defaultValue = "''")
    val equipmentCategory: String = "",
    @ColumnInfo(defaultValue = "''")
    val assetClass: String = "",
    @ColumnInfo(defaultValue = "''")
    val assetSubclass: String = "",
    // --- Organizational hierarchy ---
    @ColumnInfo(defaultValue = "''")
    val company: String = "",
    @ColumnInfo(defaultValue = "''")
    val site: String = "",
    // --- Safety & compliance governance ---
    @ColumnInfo(defaultValue = "0")
    val safetyCritical: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val riskLevel: String = "",
    @ColumnInfo(defaultValue = "''")
    val requiredPermits: String = "",
    @ColumnInfo(defaultValue = "''")
    val safetyInstructions: String = "",
    @ColumnInfo(defaultValue = "''")
    val ppeRequired: String = "",
    @ColumnInfo(defaultValue = "0")
    val isolationRequired: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val complianceRequirements: String = "",
    // --- Financial governance ---
    @ColumnInfo(defaultValue = "''")
    val financialStatus: String = "",
    @ColumnInfo(defaultValue = "0")
    val bookValue: Double = 0.0,
    @ColumnInfo(defaultValue = "''")
    val capitalizationAt: String = "",
    // --- Manufacturing & technical specifications (nameplate data) ---
    @ColumnInfo(defaultValue = "''")
    val countryOfOrigin: String = "",
    @ColumnInfo(defaultValue = "''")
    val nameplateData: String = "",
    @ColumnInfo(defaultValue = "''")
    val capacity: String = "",
    @ColumnInfo(defaultValue = "''")
    val power: String = "",
    @ColumnInfo(defaultValue = "''")
    val voltage: String = "",
    @ColumnInfo(defaultValue = "''")
    val current: String = "",
    @ColumnInfo(defaultValue = "''")
    val frequency: String = "",
    @ColumnInfo(defaultValue = "''")
    val speed: String = "",
    @ColumnInfo(defaultValue = "''")
    val pressure: String = "",
    @ColumnInfo(defaultValue = "''")
    val flowRate: String = "",
    @ColumnInfo(defaultValue = "''")
    val temperatureRange: String = "",
    @ColumnInfo(defaultValue = "''")
    val weight: String = "",
    @ColumnInfo(defaultValue = "''")
    val dimensions: String = "",
    @ColumnInfo(defaultValue = "''")
    val material: String = "",
    @ColumnInfo(defaultValue = "''")
    val designStandard: String = "",
    @ColumnInfo(defaultValue = "''")
    val technicalSpecGroup: String = "",
    /** Rule AST-TECH-002: when true the asset must carry a serial number (individual tracking). */
    @ColumnInfo(defaultValue = "0")
    val requiresSerialTracking: Boolean = false,
    // --- Warranty governance (AST-WAR-*) ---
    @ColumnInfo(defaultValue = "''")
    val warrantyType: String = "",
    @ColumnInfo(defaultValue = "''")
    val warrantyCategory: String = "",
    @ColumnInfo(defaultValue = "''")
    val warrantyTerms: String = "",
    @ColumnInfo(defaultValue = "''")
    val coveredServices: String = "",
    @ColumnInfo(defaultValue = "''")
    val excludedServices: String = "",
    /** AST-WAR-002: coverage can be by date, by counter, or both. "" = date-only. */
    @ColumnInfo(defaultValue = "''")
    val warrantyCounterType: String = "",
    @ColumnInfo(defaultValue = "0")
    val warrantyCounterLimit: Double = 0.0,
    @ColumnInfo(defaultValue = "0")
    val warrantyClaimRequired: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val warrantyClaimStatus: String = "",
    @ColumnInfo(defaultValue = "''")
    val warrantyContact: String = "",
    @ColumnInfo(defaultValue = "''")
    val warrantyDocument: String = "",
    @ColumnInfo(defaultValue = "0")
    val vendorWarranty: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val manufacturerWarranty: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val customerWarranty: Boolean = false,
    /** AST-WAR-007: assets sharing the same warranty reference are covered by one warranty/contract. */
    @ColumnInfo(defaultValue = "''")
    val warrantyReference: String = ""
) {
    /** Whether the asset is currently covered by warranty on the given date. */
    fun isUnderWarranty(today: String): Boolean =
        warrantyEnd.isNotBlank() && today <= warrantyEnd && (warrantyStart.isBlank() || warrantyStart <= today)

    fun hasValidLinearRange(): Boolean = !isLinearAsset || linearEndPoint > linearStartPoint

    fun linearLength(): Double =
        if (isLinearAsset && linearEndPoint >= linearStartPoint) linearEndPoint - linearStartPoint else 0.0

    fun containsLinearRange(start: Double, end: Double): Boolean =
        isLinearAsset && start <= end && start >= linearStartPoint && end <= linearEndPoint
}

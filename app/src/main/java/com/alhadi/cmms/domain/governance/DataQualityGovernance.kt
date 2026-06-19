package com.alhadi.cmms.domain.governance

import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetFinancialRecordEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.SerializedItemEntity
import com.alhadi.cmms.data.entity.SparePartEntity

data class DataQualityFinding(
    val ruleCode: String,
    val severity: String,
    val entityType: String,
    val entityId: Long?,
    val fieldName: String,
    val message: String
)

object DataQualityGovernance {
    fun scan(
        assets: List<AssetEntity>,
        parts: List<SparePartEntity>,
        locations: List<FunctionalLocationEntity>,
        bom: List<AssetBomItemEntity>,
        serials: List<SerializedItemEntity>,
        financial: List<AssetFinancialRecordEntity>
    ): List<DataQualityFinding> {
        val findings = mutableListOf<DataQualityFinding>()
        val assetCodes = mutableSetOf<String>()
        val assetSerials = mutableSetOf<String>()

        assets.forEach { asset ->
            if (asset.code.isBlank()) findings += finding("ASSET_CODE_REQUIRED", "Critical", "Asset", asset.id, "code", "Asset code is missing.")
            if (asset.code.isNotBlank() && !assetCodes.add(asset.code.uppercase())) findings += finding("ASSET_CODE_DUPLICATE", "Critical", "Asset", asset.id, "code", "Duplicate asset code: ${asset.code}.")
            if (asset.name.isBlank()) findings += finding("ASSET_NAME_REQUIRED", "High", "Asset", asset.id, "name", "Asset name is missing.")
            if (asset.locationId == null && asset.location.isBlank()) findings += finding("ASSET_LOCATION_REQUIRED", "High", "Asset", asset.id, "locationId", "Asset has no governed location.")
            if (asset.organizationCode.isBlank()) findings += finding("ASSET_ORG_REQUIRED", "Medium", "Asset", asset.id, "organizationCode", "Asset organization is missing.")
            if (asset.serialNumber.isNotBlank() && !assetSerials.add(asset.serialNumber.uppercase())) findings += finding("ASSET_SERIAL_DUPLICATE", "Critical", "Asset", asset.id, "serialNumber", "Duplicate asset serial number.")
            if ((asset.criticality == "Critical" || asset.criticalityScore >= 19) && bom.none { it.assetId == asset.id && it.isActive }) {
                findings += finding("CRITICAL_ASSET_NO_BOM", "High", "Asset", asset.id, "bom", "Critical asset has no active BOM.")
            }
            if (asset.purchaseCost > 0.0 && financial.none { it.assetId == asset.id }) {
                findings += finding("FINANCIAL_LINK_MISSING", "Medium", "Asset", asset.id, "financial", "Purchased asset has no financial master-data link.")
            }
        }

        val partNumbers = mutableSetOf<String>()
        parts.forEach { part ->
            if (part.partNumber.isBlank()) findings += finding("PART_NUMBER_REQUIRED", "Critical", "SparePart", part.id, "partNumber", "Part number is missing.")
            if (part.partNumber.isNotBlank() && !partNumbers.add(part.partNumber.uppercase())) findings += finding("PART_NUMBER_DUPLICATE", "Critical", "SparePart", part.id, "partNumber", "Duplicate part number.")
            if (part.name.isBlank()) findings += finding("PART_NAME_REQUIRED", "High", "SparePart", part.id, "name", "Part name is missing.")
            if (part.onHandQty < 0 || part.minQty < 0) findings += finding("PART_QTY_INVALID", "Critical", "SparePart", part.id, "quantity", "Inventory quantity cannot be negative.")
            if (part.isSerialized && serials.none { it.partId == part.id }) findings += finding("SERIAL_MASTER_MISSING", "Medium", "SparePart", part.id, "serials", "Serialized part has no serial master records.")
        }

        locations.forEach { location ->
            if (location.code.isBlank()) findings += finding("LOCATION_CODE_REQUIRED", "High", "Location", location.id, "code", "Functional-location code is missing.")
            if (LocationInheritanceResolver.detectCycle(location, locations)) findings += finding("LOCATION_CYCLE", "Critical", "Location", location.id, "parentId", "Functional-location hierarchy contains a cycle.")
        }

        val serialNumbers = mutableSetOf<String>()
        serials.forEach { item ->
            if (item.serialNumber.isBlank()) findings += finding("SERIAL_REQUIRED", "Critical", "SerializedItem", item.id, "serialNumber", "Serial number is missing.")
            if (item.serialNumber.isNotBlank() && !serialNumbers.add(item.serialNumber.uppercase())) findings += finding("SERIAL_DUPLICATE", "Critical", "SerializedItem", item.id, "serialNumber", "Duplicate serialized item number.")
        }

        financial.forEach { item ->
            if (item.netBookValue < 0 || item.netBookValue > item.acquisitionValue + 0.01) findings += finding("NBV_INVALID", "High", "FinancialRecord", item.id, "netBookValue", "Net book value is inconsistent with acquisition value.")
        }
        return findings
    }

    private fun finding(code: String, severity: String, type: String, id: Long?, field: String, message: String) =
        DataQualityFinding(code, severity, type, id, field, message)
}

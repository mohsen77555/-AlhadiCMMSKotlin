package com.alhadi.cmms.domain.governance

import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetBomRevisionEntity
import com.alhadi.cmms.data.entity.BomAlternativeEntity

object BomGovernance {
    fun validateRevision(item: AssetBomRevisionEntity) {
        require(item.assetId > 0) { "Asset is required for a BOM revision." }
        require(item.revisionCode.trim().isNotEmpty()) { "BOM revision code is required." }
        require(item.status in setOf("Draft", "Approved", "Obsolete")) { "Unsupported BOM revision status." }
        require(item.effectiveTo.isBlank() || item.effectiveFrom.isBlank() || item.effectiveTo >= item.effectiveFrom) {
            "BOM revision validity range is invalid."
        }
        if (item.status == "Approved") {
            require(item.approvedBy.isNotBlank()) { "Approved BOM revision requires approver." }
        }
    }

    fun validateItem(item: AssetBomItemEntity) {
        require(item.assetId > 0 && item.partId > 0) { "Asset and part are required for a BOM item." }
        require(item.quantity > 0) { "BOM quantity must be greater than zero." }
        require(item.quantityPerAsset > 0.0) { "Quantity per asset must be greater than zero." }
        require(item.unit.isNotBlank()) { "BOM unit is required." }
        require(item.validTo.isBlank() || item.validFrom.isBlank() || item.validTo >= item.validFrom) {
            "BOM item validity range is invalid."
        }
    }

    fun validateAlternative(item: BomAlternativeEntity, basePartId: Long) {
        require(item.bomItemId > 0 && item.alternatePartId > 0) { "BOM item and alternative part are required." }
        require(item.alternatePartId != basePartId) { "Alternative part must differ from the main part." }
        require(item.priority > 0) { "Alternative priority must be greater than zero." }
    }
}

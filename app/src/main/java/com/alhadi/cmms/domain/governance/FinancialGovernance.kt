package com.alhadi.cmms.domain.governance

import com.alhadi.cmms.data.entity.AssetFinancialRecordEntity
import com.alhadi.cmms.data.entity.FinancialPostingEntity

object FinancialGovernance {
    fun validateRecord(item: AssetFinancialRecordEntity) {
        require(item.assetId > 0) { "Asset is required for financial linkage." }
        require(item.fixedAssetNumber.trim().isNotEmpty()) { "Fixed asset number is required." }
        require(item.acquisitionValue >= 0.0) { "Acquisition value cannot be negative." }
        require(item.accumulatedDepreciation >= 0.0) { "Accumulated depreciation cannot be negative." }
        require(item.netBookValue >= 0.0) { "Net book value cannot be negative." }
        require(item.netBookValue <= item.acquisitionValue + 0.01) { "Net book value cannot exceed acquisition value." }
        require(item.currency.length == 3) { "Currency must be a 3-letter code." }
        require(item.usefulLifeMonths >= 0) { "Useful life cannot be negative." }
    }

    fun validatePosting(item: FinancialPostingEntity) {
        require(item.assetId > 0) { "Asset is required for financial posting." }
        require(item.costCategory.isNotBlank()) { "Cost category is required." }
        require(item.amount >= 0.0) { "Posting amount cannot be negative." }
        require(item.currency.length == 3) { "Currency must be a 3-letter code." }
    }

    fun calculatedNetBookValue(acquisitionValue: Double, accumulatedDepreciation: Double): Double =
        (acquisitionValue - accumulatedDepreciation).coerceAtLeast(0.0)
}

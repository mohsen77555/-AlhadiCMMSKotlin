package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "asset_financial_records",
    indices = [Index(value = ["assetId"]), Index(value = ["fixedAssetNumber"])]
)
@Serializable
data class AssetFinancialRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val fixedAssetNumber: String,
    val companyCode: String = "",
    val ledger: String = "",
    val costCenter: String = "",
    val capitalizationDate: String = "",
    val acquisitionValue: Double = 0.0,
    val currency: String = "SAR",
    val depreciationMethod: String = "StraightLine",
    val usefulLifeMonths: Int = 0,
    val accumulatedDepreciation: Double = 0.0,
    val netBookValue: Double = 0.0,
    val sourceSystem: String = "Manual",
    val syncStatus: String = "Local",
    val lastSyncedAt: String = "",
    val notes: String = ""
)

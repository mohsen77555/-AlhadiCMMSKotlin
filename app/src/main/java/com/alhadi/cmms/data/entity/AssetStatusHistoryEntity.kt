package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * An immutable equipment lifecycle record (EQ-LIFE-001..004): captures every status
 * transition of an asset — the previous status, the new status, the reason, who made the
 * change and when. Rows are only ever inserted, never updated or individually deleted.
 */
@Entity(
    tableName = "asset_status_history",
    indices = [Index(value = ["assetId"])]
)
@Serializable
data class AssetStatusHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val fromStatus: String = "",
    val toStatus: String,
    val reason: String = "",
    val changedBy: String,
    val changedAt: String
)

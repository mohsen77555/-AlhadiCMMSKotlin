package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An immutable install / dismantle history record: tracks when an asset entered a functional
 * location (Install) and when it left it (Dismantle). Generated automatically whenever an
 * asset's functional location changes (chapter 02 — install/dismantle governance).
 */
@Entity(
    tableName = "asset_installations",
    indices = [Index(value = ["assetId"]), Index(value = ["locationId"])]
)
@Serializable
data class AssetInstallationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val locationId: Long?,
    @ColumnInfo(defaultValue = "''")
    val locationCode: String = "",
    /** Event type: Install / Dismantle. */
    val eventType: String,
    val eventDate: String,
    @ColumnInfo(defaultValue = "''")
    val performedBy: String = "",
    @ColumnInfo(defaultValue = "''")
    val reason: String = "",
    @ColumnInfo(defaultValue = "''")
    val createdAt: String = ""
)

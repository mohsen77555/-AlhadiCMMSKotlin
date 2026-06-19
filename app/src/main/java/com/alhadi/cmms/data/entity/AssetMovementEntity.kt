package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A lifecycle / movement event for an asset: install, transfer, dismantle, retire...
 * Together these rows form the per-asset history timeline used by Asset 360.
 *
 * [eventType] is one of [com.alhadi.cmms.data.MovementType]. [fromLocationId] /
 * [toLocationId] reference functional locations (nullable, e.g. install has no source,
 * dismantle has no destination).
 */
@Entity(
    tableName = "asset_movements",
    indices = [Index(value = ["assetId"])]
)
@Serializable
data class AssetMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val eventType: String,
    val fromLocationId: Long? = null,
    val toLocationId: Long? = null,
    val fromLocationName: String = "",
    val toLocationName: String = "",
    val notes: String = "",
    val performedBy: String,
    val occurredAt: String,
    @ColumnInfo(defaultValue = "''")
    val reason: String = "",
    @ColumnInfo(defaultValue = "''")
    val previousLifecycleStatus: String = "",
    @ColumnInfo(defaultValue = "''")
    val newLifecycleStatus: String = "",
    @ColumnInfo(defaultValue = "''")
    val approvedBy: String = "",
    @ColumnInfo(defaultValue = "''")
    val referenceType: String = "",
    val referenceId: Long? = null
)

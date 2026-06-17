package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A soft-deleted record kept in the recycle bin. The original row is serialized to [payload]
 * (JSON) so it can be restored exactly, or purged permanently.
 */
@Serializable
@Entity(tableName = "trash")
data class TrashEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val entityType: String,
    val entityId: Long,
    val label: String,
    val payload: String,
    val deletedAt: String,
    val deletedBy: String
)

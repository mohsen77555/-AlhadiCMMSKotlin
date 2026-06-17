package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Corrective / Preventive Action (CAPA). Tracks actions raised from failures,
 * inspections or audits, with a type, owner, due date and status.
 */
@Entity(
    tableName = "capa_actions",
    indices = [Index(value = ["assetId"]), Index(value = ["status"])]
)
@Serializable
data class CapaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val title: String,
    val type: String,
    val description: String,
    val assetId: Long?,
    val priority: String,
    val status: String,
    val assignedTo: String,
    val dueAt: String,
    val createdAt: String
)

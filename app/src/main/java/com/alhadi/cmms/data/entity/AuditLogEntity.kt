package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Governance audit trail. Every meaningful state change (work orders, inventory,
 * PM completion, user/role changes, logins) is recorded here so administrators can
 * review "who did what, and when".
 */
@Entity(tableName = "audit_log")
@Serializable
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val action: String,
    val entityType: String,
    val details: String,
    val performedBy: String,
    val createdAt: String
)

package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * An enterprise organization unit (governance §5): Company → Site → Plant.
 * Supports a parent/child hierarchy so assets can be assigned to a specific
 * plant under a site under a company.
 */
@Entity(
    tableName = "org_units",
    indices = [Index(value = ["code"], unique = true), Index(value = ["parentId"])]
)
@Serializable
data class OrgUnitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    /** "Company", "Site", or "Plant". */
    val type: String,
    val parentId: Long? = null,
    val status: String = "Active"
)

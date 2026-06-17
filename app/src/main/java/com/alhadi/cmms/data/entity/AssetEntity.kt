package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets",
    indices = [Index(value = ["code"], unique = true)]
)
data class AssetEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    val groupName: String,
    val location: String,
    val manufacturer: String,
    val model: String,
    val status: String,
    val criticality: String,
    val installedAt: String,
    val lastInspectionAt: String,
    val locationId: Long? = null,
    val warrantyProvider: String = "",
    val warrantyStart: String = "",
    val warrantyEnd: String = ""
) {
    /** Whether the asset is currently covered by warranty on the given date. */
    fun isUnderWarranty(today: String): Boolean =
        warrantyEnd.isNotBlank() && today <= warrantyEnd && (warrantyStart.isBlank() || warrantyStart <= today)
}

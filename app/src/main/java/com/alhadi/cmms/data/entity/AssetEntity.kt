package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "assets",
    indices = [Index(value = ["code"], unique = true)]
)
@Serializable
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
    val warrantyEnd: String = "",
    val parentAssetId: Long? = null,
    val serialNumber: String = "",
    val assetTag: String = "",
    val supplier: String = "",
    val purchaseOrder: String = "",
    val purchaseCost: Double = 0.0,
    val acquiredAt: String = "",
    @ColumnInfo(defaultValue = "'Machine'")
    val category: String = "Machine",
    @ColumnInfo(defaultValue = "''")
    val objectType: String = "",
    @ColumnInfo(defaultValue = "''")
    val description: String = "",
    @ColumnInfo(defaultValue = "''")
    val maintenancePlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val planningPlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val plannerGroup: String = "",
    @ColumnInfo(defaultValue = "''")
    val mainWorkCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val productionWorkCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val costCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val responsiblePerson: String = "",
    @ColumnInfo(defaultValue = "''")
    val assetNumber: String = "",
    @ColumnInfo(defaultValue = "''")
    val constructionYear: String = "",
    @ColumnInfo(defaultValue = "''")
    val constructionMonth: String = "",
    @ColumnInfo(defaultValue = "''")
    val startupDate: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerName: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerRole: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerPhone: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerEmail: String = "",
    @ColumnInfo(defaultValue = "''")
    val addressLine: String = "",
    @ColumnInfo(defaultValue = "''")
    val city: String = "",
    @ColumnInfo(defaultValue = "''")
    val country: String = "",
    @ColumnInfo(defaultValue = "''")
    val standardClass: String = "",
    @ColumnInfo(defaultValue = "1")
    val inheritParentCharacteristics: Boolean = true
) {
    /** Whether the asset is currently covered by warranty on the given date. */
    fun isUnderWarranty(today: String): Boolean =
        warrantyEnd.isNotBlank() && today <= warrantyEnd && (warrantyStart.isBlank() || warrantyStart <= today)
}

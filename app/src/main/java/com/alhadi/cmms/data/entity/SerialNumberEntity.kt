package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Individually tracked unit of a serialized spare part or maintainable assembly. */
@Entity(
    tableName = "serial_numbers",
    indices = [
        Index(value = ["partId", "serialNumber"], unique = true),
        Index(value = ["profileId"]),
        Index(value = ["assetId"], unique = true),
        Index(value = ["currentWorkOrderId"]),
        Index(value = ["status"]),
        Index(value = ["storageLocation"])
    ]
)
@Serializable
data class SerialNumberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partId: Long,
    val serialNumber: String,
    val profileId: Long? = null,
    val assetId: Long? = null,
    val currentWorkOrderId: Long? = null,
    @ColumnInfo(defaultValue = "'Created'")
    val status: String = "Created",
    @ColumnInfo(defaultValue = "''")
    val stockType: String = "",
    @ColumnInfo(defaultValue = "''")
    val plant: String = "",
    @ColumnInfo(defaultValue = "''")
    val storageLocation: String = "",
    @ColumnInfo(defaultValue = "''")
    val batch: String = "",
    @ColumnInfo(defaultValue = "''")
    val vendor: String = "",
    @ColumnInfo(defaultValue = "''")
    val customer: String = "",
    @ColumnInfo(defaultValue = "''")
    val salesOrder: String = "",
    @ColumnInfo(defaultValue = "''")
    val specialStock: String = "",
    @ColumnInfo(defaultValue = "''")
    val createdAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val lastMovementAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val notes: String = ""
) {
    fun isInStock(): Boolean = status == "InStock"
    fun isInstalled(): Boolean = status == "Installed"
}

package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A procurement supplier / vendor. Purchase orders reference a supplier by [id];
 * spare parts may reference one by [code] for reordering.
 */
@Entity(
    tableName = "suppliers",
    indices = [Index(value = ["code"], unique = true)]
)
@Serializable
data class SupplierEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    @ColumnInfo(defaultValue = "''") val contactPerson: String = "",
    @ColumnInfo(defaultValue = "''") val phone: String = "",
    @ColumnInfo(defaultValue = "''") val email: String = "",
    @ColumnInfo(defaultValue = "''") val address: String = "",
    @ColumnInfo(defaultValue = "'Parts'") val category: String = "Parts",
    @ColumnInfo(defaultValue = "''") val taxNumber: String = "",
    @ColumnInfo(defaultValue = "''") val paymentTerms: String = "",
    @ColumnInfo(defaultValue = "0") val rating: Int = 0,
    @ColumnInfo(defaultValue = "'Active'") val status: String = "Active",
    @ColumnInfo(defaultValue = "''") val notes: String = ""
) {
    val isActive: Boolean get() = status.equals("Active", ignoreCase = true)
}

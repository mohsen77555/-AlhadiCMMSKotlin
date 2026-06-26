package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/**
 * A planned material line on a work order (WO-MAT-001..006): the spare part required for the job,
 * how much is planned, and how much has actually been issued from stock. Issuing against a planned
 * line decrements inventory and advances [issuedQty], so planned-vs-actual variance stays visible.
 */
@Entity(
    tableName = "work_order_materials",
    indices = [Index(value = ["orderId"]), Index(value = ["partId"])]
)
@Serializable
data class WorkOrderMaterialEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val partId: Long?,
    @ColumnInfo(defaultValue = "''")
    val partNumber: String = "",
    val description: String,
    @ColumnInfo(defaultValue = "1")
    val plannedQty: Int = 1,
    @ColumnInfo(defaultValue = "0")
    val issuedQty: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val unitPrice: Double = 0.0
) {
    val remainingQty: Int get() = (plannedQty - issuedQty).coerceAtLeast(0)
    val isFullyIssued: Boolean get() = issuedQty >= plannedQty
    val plannedTotal: Double get() = plannedQty * unitPrice
    val issuedTotal: Double get() = issuedQty * unitPrice
}

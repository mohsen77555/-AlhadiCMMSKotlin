package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spare_parts",
    indices = [Index(value = ["partNumber"], unique = true)]
)
@Serializable
data class SparePartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partNumber: String,
    val name: String,
    val equipmentGroup: String,
    val unit: String,
    val onHandQty: Int,
    val minQty: Int,
    val location: String,
    val lastPrice: Double,
    @ColumnInfo(defaultValue = "0")
    val serializationActive: Boolean = false,
    val serialProfileId: Long? = null,
    // --- Inventory governance (chapter 04): reorder policy ---
    /** Maximum stock level; 0 = no ceiling (INV-REO-002). */
    @ColumnInfo(defaultValue = "0")
    val maxQty: Int = 0,
    /** Fixed reorder/economic order quantity; 0 = order up to max instead (INV-REO-003). */
    @ColumnInfo(defaultValue = "0")
    val reorderQty: Int = 0,
    /** Safety-stock buffer kept above the reorder point (INV-REO-004). */
    @ColumnInfo(defaultValue = "0")
    val safetyStock: Int = 0,
    /** Supplier lead time in days, used for planning (INV-REO-005). */
    @ColumnInfo(defaultValue = "0")
    val leadTimeDays: Int = 0,
    /** ABC criticality class: A / B / C / blank (INV-ABC-001). */
    @ColumnInfo(defaultValue = "''")
    val abcClass: String = "",
    /** Preferred supplier used when raising an automatic purchase order (INV-REO-010). */
    val preferredSupplierId: Long? = null
) {
    /** INV-REO-001: at or below the reorder point (minimum quantity). */
    val isLowStock: Boolean get() = onHandQty <= minQty
    /** Stock exceeds the configured maximum. */
    val isOverMax: Boolean get() = maxQty > 0 && onHandQty > maxQty
    /** Current monetary value of the on-hand stock. */
    val stockValue: Double get() = onHandQty * lastPrice

    /**
     * Suggested replenishment quantity when reordering: a fixed reorder quantity if set,
     * otherwise enough to reach the maximum (or the reorder point plus safety stock).
     */
    fun suggestedOrderQty(): Int {
        val target = when {
            reorderQty > 0 -> reorderQty
            maxQty > 0 -> maxQty - onHandQty
            else -> (minQty + safetyStock) - onHandQty
        }
        return target.coerceAtLeast(0)
    }
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseOrderDao {
    @Query("SELECT * FROM purchase_orders ORDER BY id DESC")
    fun observePurchaseOrders(): Flow<List<PurchaseOrderEntity>>

    @Query("SELECT * FROM purchase_orders ORDER BY id ASC")
    suspend fun dumpAll(): List<PurchaseOrderEntity>

    @Query("SELECT COUNT(*) FROM purchase_orders")
    suspend fun countOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: PurchaseOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<PurchaseOrderEntity>)

    @Query("DELETE FROM purchase_orders WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM purchase_orders")
    suspend fun deleteAll()
}

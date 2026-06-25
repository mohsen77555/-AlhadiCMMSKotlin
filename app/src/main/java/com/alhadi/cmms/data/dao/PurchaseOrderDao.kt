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
    fun observeAll(): Flow<List<PurchaseOrderEntity>>

    @Query("SELECT * FROM purchase_orders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): PurchaseOrderEntity?

    @Query("SELECT * FROM purchase_orders ORDER BY id DESC")
    suspend fun dumpAll(): List<PurchaseOrderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(order: PurchaseOrderEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<PurchaseOrderEntity>)

    @Query("UPDATE purchase_orders SET totalAmount = :total WHERE id = :id")
    suspend fun updateTotal(id: Long, total: Double)

    @Query("DELETE FROM purchase_orders WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM purchase_orders")
    suspend fun deleteAll()
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PurchaseOrderLineDao {
    @Query("SELECT * FROM purchase_order_lines ORDER BY id ASC")
    fun observeAll(): Flow<List<PurchaseOrderLineEntity>>

    @Query("SELECT * FROM purchase_order_lines WHERE poId = :poId ORDER BY id ASC")
    suspend fun forOrder(poId: Long): List<PurchaseOrderLineEntity>

    @Query("SELECT * FROM purchase_order_lines ORDER BY id ASC")
    suspend fun dumpAll(): List<PurchaseOrderLineEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(line: PurchaseOrderLineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(lines: List<PurchaseOrderLineEntity>)

    @Query("DELETE FROM purchase_order_lines WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM purchase_order_lines WHERE poId = :poId")
    suspend fun deleteForOrder(poId: Long)

    @Query("DELETE FROM purchase_order_lines")
    suspend fun deleteAll()
}

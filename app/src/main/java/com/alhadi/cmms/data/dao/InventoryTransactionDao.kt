package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryTransactionDao {
    @Query("SELECT * FROM inventory_transactions ORDER BY createdAt DESC, id DESC LIMIT 50")
    fun observeRecentTransactions(): Flow<List<InventoryTransactionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(transactions: List<InventoryTransactionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaction: InventoryTransactionEntity): Long

    @Query("DELETE FROM inventory_transactions")
    suspend fun deleteAll()
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.SparePartEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SparePartDao {
    @Query("SELECT * FROM spare_parts ORDER BY equipmentGroup ASC, partNumber ASC")
    fun observeSpareParts(): Flow<List<SparePartEntity>>

    @Query("SELECT COUNT(*) FROM spare_parts WHERE onHandQty <= minQty")
    fun observeLowStockCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM spare_parts")
    suspend fun countOnce(): Int

    @Query("SELECT * FROM spare_parts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SparePartEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parts: List<SparePartEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(part: SparePartEntity): Long

    @Query("UPDATE spare_parts SET onHandQty = onHandQty + :delta WHERE id = :id")
    suspend fun adjustStock(id: Long, delta: Int)

    @Query("UPDATE spare_parts SET onHandQty = onHandQty + :delta WHERE id = :id AND onHandQty + :delta >= 0")
    suspend fun adjustStockSafe(id: Long, delta: Int): Int

    @Query("UPDATE spare_parts SET onHandQty = :quantity WHERE id = :id")
    suspend fun setStock(id: Long, quantity: Int)

    @Query("UPDATE spare_parts SET lastPrice = :price WHERE id = :id")
    suspend fun updateLastPrice(id: Long, price: Double)

    @Query("DELETE FROM spare_parts WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM spare_parts")
    suspend fun deleteAll()
}

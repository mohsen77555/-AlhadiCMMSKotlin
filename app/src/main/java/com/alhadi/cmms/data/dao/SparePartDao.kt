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

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(parts: List<SparePartEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(part: SparePartEntity): Long

    @Query("UPDATE spare_parts SET onHandQty = onHandQty + :delta WHERE id = :id")
    suspend fun adjustStock(id: Long, delta: Int)

    @Query("DELETE FROM spare_parts")
    suspend fun deleteAll()
}

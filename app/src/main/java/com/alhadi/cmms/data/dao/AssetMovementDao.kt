package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetMovementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetMovementDao {
    @Query("SELECT * FROM asset_movements ORDER BY occurredAt DESC, id DESC")
    fun observeMovements(): Flow<List<AssetMovementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(movement: AssetMovementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(movements: List<AssetMovementEntity>)

    @Query("DELETE FROM asset_movements WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_movements")
    suspend fun deleteAll()
}

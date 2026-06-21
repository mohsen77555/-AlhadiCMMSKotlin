package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.PlantEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {
    @Query("SELECT * FROM plants ORDER BY code ASC") fun observeAll(): Flow<List<PlantEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: PlantEntity): Long
    @Query("DELETE FROM plants WHERE id = :id") suspend fun deleteById(id: Long)
}

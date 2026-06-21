package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.StorageLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StorageLocationDao {
    @Query("SELECT * FROM storage_locations ORDER BY code ASC") fun observeAll(): Flow<List<StorageLocationEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: StorageLocationEntity): Long
    @Query("DELETE FROM storage_locations WHERE id = :id") suspend fun deleteById(id: Long)
}

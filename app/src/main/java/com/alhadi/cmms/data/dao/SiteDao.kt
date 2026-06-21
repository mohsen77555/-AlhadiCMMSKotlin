package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.SiteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SiteDao {
    @Query("SELECT * FROM sites ORDER BY code ASC") fun observeAll(): Flow<List<SiteEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: SiteEntity): Long
    @Query("DELETE FROM sites WHERE id = :id") suspend fun deleteById(id: Long)
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.WorkCenterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkCenterDao {
    @Query("SELECT * FROM work_centers ORDER BY code ASC") fun observeAll(): Flow<List<WorkCenterEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: WorkCenterEntity): Long
    @Query("DELETE FROM work_centers WHERE id = :id") suspend fun deleteById(id: Long)
}

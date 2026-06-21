package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.PlannerGroupEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlannerGroupDao {
    @Query("SELECT * FROM planner_groups ORDER BY code ASC") fun observeAll(): Flow<List<PlannerGroupEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: PlannerGroupEntity): Long
    @Query("DELETE FROM planner_groups WHERE id = :id") suspend fun deleteById(id: Long)
}

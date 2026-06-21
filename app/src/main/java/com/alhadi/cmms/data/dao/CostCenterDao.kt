package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.CostCenterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CostCenterDao {
    @Query("SELECT * FROM cost_centers ORDER BY code ASC") fun observeAll(): Flow<List<CostCenterEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: CostCenterEntity): Long
    @Query("DELETE FROM cost_centers WHERE id = :id") suspend fun deleteById(id: Long)
}

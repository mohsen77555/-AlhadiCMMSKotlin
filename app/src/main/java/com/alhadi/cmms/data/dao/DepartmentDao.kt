package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.DepartmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DepartmentDao {
    @Query("SELECT * FROM departments ORDER BY code ASC") fun observeAll(): Flow<List<DepartmentEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: DepartmentEntity): Long
    @Query("DELETE FROM departments WHERE id = :id") suspend fun deleteById(id: Long)
}

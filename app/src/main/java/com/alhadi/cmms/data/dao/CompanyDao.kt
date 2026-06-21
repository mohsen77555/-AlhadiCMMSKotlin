package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.CompanyEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CompanyDao {
    @Query("SELECT * FROM companies ORDER BY code ASC") fun observeAll(): Flow<List<CompanyEntity>>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: CompanyEntity): Long
    @Query("DELETE FROM companies WHERE id = :id") suspend fun deleteById(id: Long)
}

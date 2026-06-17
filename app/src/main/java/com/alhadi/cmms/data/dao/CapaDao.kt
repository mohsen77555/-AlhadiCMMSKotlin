package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.CapaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CapaDao {
    @Query("SELECT * FROM capa_actions ORDER BY CASE status WHEN 'Closed' THEN 1 ELSE 0 END, dueAt ASC")
    fun observeCapa(): Flow<List<CapaEntity>>

    @Query("SELECT COUNT(*) FROM capa_actions WHERE status != 'Closed'")
    fun observeOpenCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM capa_actions")
    suspend fun countOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CapaEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<CapaEntity>)

    @Query("UPDATE capa_actions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("DELETE FROM capa_actions WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM capa_actions")
    suspend fun deleteAll()
}

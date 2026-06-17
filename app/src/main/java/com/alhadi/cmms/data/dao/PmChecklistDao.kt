package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PmChecklistDao {
    @Query("SELECT * FROM pm_checklist_items ORDER BY pmId, orderIndex, id")
    fun observeItems(): Flow<List<PmChecklistItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PmChecklistItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PmChecklistItemEntity>)

    @Query("UPDATE pm_checklist_items SET result = '' WHERE pmId = :pmId")
    suspend fun resetResults(pmId: Long)

    @Query("DELETE FROM pm_checklist_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM pm_checklist_items")
    suspend fun deleteAll()
}

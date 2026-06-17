package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PreventiveMaintenanceDao {
    @Query("SELECT * FROM preventive_maintenance ORDER BY nextDueAt ASC")
    fun observePreventiveMaintenance(): Flow<List<PreventiveMaintenanceEntity>>

    @Query("SELECT COUNT(*) FROM preventive_maintenance WHERE nextDueAt <= :today AND status != 'Done'")
    fun observeDueCount(today: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM preventive_maintenance")
    suspend fun countOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<PreventiveMaintenanceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: PreventiveMaintenanceEntity): Long

    @Query("UPDATE preventive_maintenance SET status = :status, lastDoneAt = :doneAt, nextDueAt = :nextDueAt WHERE id = :id")
    suspend fun markDone(id: Long, status: String, doneAt: String, nextDueAt: String)

    @Query("DELETE FROM preventive_maintenance")
    suspend fun deleteAll()
}

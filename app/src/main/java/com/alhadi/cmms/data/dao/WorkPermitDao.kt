package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.WorkPermitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkPermitDao {
    @Query("SELECT * FROM work_permits ORDER BY id DESC")
    fun observePermits(): Flow<List<WorkPermitEntity>>

    @Query("SELECT COUNT(*) FROM work_permits WHERE orderId = :orderId AND status = 'Approved' AND (validUntil = '' OR validUntil >= :today)")
    suspend fun countValid(orderId: Long, today: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(permit: WorkPermitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(permits: List<WorkPermitEntity>)

    @Query("DELETE FROM work_permits WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM work_permits")
    suspend fun deleteAll()
}

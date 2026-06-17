package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderConfirmationDao {
    @Query("SELECT * FROM work_order_confirmations ORDER BY id DESC")
    fun observeConfirmations(): Flow<List<WorkOrderConfirmationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(confirmation: WorkOrderConfirmationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(confirmations: List<WorkOrderConfirmationEntity>)

    @Query("DELETE FROM work_order_confirmations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM work_order_confirmations")
    suspend fun deleteAll()
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.alhadi.cmms.data.entity.WorkOrderHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderHistoryDao {
    @Query("SELECT * FROM work_order_history ORDER BY id DESC")
    fun observeAll(): Flow<List<WorkOrderHistoryEntity>>

    @Query("SELECT * FROM work_order_history WHERE orderId = :orderId ORDER BY id DESC")
    suspend fun forOrder(orderId: Long): List<WorkOrderHistoryEntity>

    // WO-HIS-005: history rows are immutable — insert only.
    @Insert
    suspend fun insert(entry: WorkOrderHistoryEntity): Long

    @Query("DELETE FROM work_order_history")
    suspend fun deleteAll()
}

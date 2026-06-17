package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.WorkOrderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderDao {
    @Query("SELECT * FROM work_orders ORDER BY dueAt ASC, priority DESC")
    fun observeWorkOrders(): Flow<List<WorkOrderEntity>>

    @Query("SELECT COUNT(*) FROM work_orders WHERE status IN ('Open', 'In Progress')")
    fun observeOpenCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM work_orders")
    suspend fun countOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkOrders(workOrders: List<WorkOrderEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkOrder(workOrder: WorkOrderEntity): Long

    @Query("UPDATE work_orders SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String)

    @Query("SELECT requiresPermit FROM work_orders WHERE id = :id")
    suspend fun requiresPermit(id: Long): Boolean?

    @Query("DELETE FROM work_orders WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM work_orders")
    suspend fun deleteAll()
}

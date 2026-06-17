package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderOperationDao {
    @Query("SELECT * FROM work_order_operations ORDER BY orderId, operationNumber, id")
    fun observeOperations(): Flow<List<WorkOrderOperationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(operation: WorkOrderOperationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(operations: List<WorkOrderOperationEntity>)

    @Query("DELETE FROM work_order_operations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM work_order_operations")
    suspend fun deleteAll()
}

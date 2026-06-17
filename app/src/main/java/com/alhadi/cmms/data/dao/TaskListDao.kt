package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskListDao {
    @Query("SELECT * FROM task_lists ORDER BY name")
    fun observeTaskLists(): Flow<List<TaskListEntity>>

    @Query("SELECT * FROM task_list_operations ORDER BY taskListId, operationNumber, id")
    fun observeTaskListOperations(): Flow<List<TaskListOperationEntity>>

    @Query("SELECT * FROM task_list_operations WHERE taskListId = :taskListId ORDER BY operationNumber, id")
    suspend fun operationsForList(taskListId: Long): List<TaskListOperationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskList(taskList: TaskListEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskLists(taskLists: List<TaskListEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperation(operation: TaskListOperationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOperations(operations: List<TaskListOperationEntity>)

    @Query("DELETE FROM task_lists WHERE id = :id")
    suspend fun deleteTaskListById(id: Long)

    @Query("DELETE FROM task_list_operations WHERE taskListId = :taskListId")
    suspend fun deleteOperationsForList(taskListId: Long)

    @Query("DELETE FROM task_list_operations WHERE id = :id")
    suspend fun deleteOperationById(id: Long)

    @Query("DELETE FROM task_lists")
    suspend fun deleteAllTaskLists()

    @Query("DELETE FROM task_list_operations")
    suspend fun deleteAllOperations()
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.WorkOrderMaterialEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderMaterialDao {
    @Query("SELECT * FROM work_order_materials ORDER BY id ASC")
    fun observeAll(): Flow<List<WorkOrderMaterialEntity>>

    @Query("SELECT * FROM work_order_materials WHERE orderId = :orderId ORDER BY id ASC")
    suspend fun forOrder(orderId: Long): List<WorkOrderMaterialEntity>

    @Query("SELECT * FROM work_order_materials WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkOrderMaterialEntity?

    @Query("SELECT * FROM work_order_materials ORDER BY id ASC")
    suspend fun dumpAll(): List<WorkOrderMaterialEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(material: WorkOrderMaterialEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(materials: List<WorkOrderMaterialEntity>)

    @Query("DELETE FROM work_order_materials WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM work_order_materials WHERE orderId = :orderId")
    suspend fun deleteForOrder(orderId: Long)

    @Query("DELETE FROM work_order_materials")
    suspend fun deleteAll()
}

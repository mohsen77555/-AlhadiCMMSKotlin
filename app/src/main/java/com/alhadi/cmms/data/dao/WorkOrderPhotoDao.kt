package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkOrderPhotoDao {
    @Query("SELECT * FROM work_order_photos ORDER BY id DESC")
    fun observePhotos(): Flow<List<WorkOrderPhotoEntity>>

    @Query("SELECT COUNT(*) FROM work_order_photos WHERE orderId = :orderId")
    suspend fun countForOrder(orderId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: WorkOrderPhotoEntity): Long

    @Query("DELETE FROM work_order_photos WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM work_order_photos")
    suspend fun deleteAll()
}

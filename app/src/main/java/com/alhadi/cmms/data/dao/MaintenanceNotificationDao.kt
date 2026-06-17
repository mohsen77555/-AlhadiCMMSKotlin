package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MaintenanceNotificationDao {
    @Query("SELECT * FROM maintenance_notifications ORDER BY id DESC")
    fun observeNotifications(): Flow<List<MaintenanceNotificationEntity>>

    @Query("SELECT COUNT(*) FROM maintenance_notifications")
    suspend fun countOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notification: MaintenanceNotificationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(notifications: List<MaintenanceNotificationEntity>)

    @Query("DELETE FROM maintenance_notifications WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM maintenance_notifications")
    suspend fun deleteAll()
}

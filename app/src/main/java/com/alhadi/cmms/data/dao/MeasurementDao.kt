package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measuring_points ORDER BY assetId ASC, name ASC")
    fun observePoints(): Flow<List<MeasuringPointEntity>>

    @Query("SELECT * FROM measuring_points WHERE id = :id LIMIT 1")
    suspend fun getPointById(id: Long): MeasuringPointEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoint(point: MeasuringPointEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<MeasuringPointEntity>)

    @Query("UPDATE measuring_points SET lastReading = :value, lastReadingAt = :at WHERE id = :id")
    suspend fun updateLastReading(id: Long, value: Double, at: String)

    @Query("DELETE FROM measuring_points WHERE id = :id")
    suspend fun deletePointById(id: Long)

    @Query("SELECT * FROM measurement_readings ORDER BY id DESC LIMIT 200")
    fun observeReadings(): Flow<List<MeasurementReadingEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: MeasurementReadingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<MeasurementReadingEntity>)

    @Query("DELETE FROM measuring_points")
    suspend fun deleteAllPoints()

    @Query("DELETE FROM measurement_readings")
    suspend fun deleteAllReadings()
}

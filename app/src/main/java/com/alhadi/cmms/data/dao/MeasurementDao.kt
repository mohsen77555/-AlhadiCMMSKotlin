package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.MeterReplacementEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MeasurementDao {
    @Query("SELECT * FROM measuring_points ORDER BY assetId ASC, name ASC")
    fun observePoints(): Flow<List<MeasuringPointEntity>>

    @Query("SELECT * FROM measuring_points ORDER BY id ASC")
    suspend fun dumpAllPoints(): List<MeasuringPointEntity>

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

    @Query("SELECT * FROM measurement_readings ORDER BY id DESC LIMIT 500")
    fun observeReadings(): Flow<List<MeasurementReadingEntity>>

    @Query("SELECT * FROM measurement_readings ORDER BY id ASC")
    suspend fun dumpAllReadings(): List<MeasurementReadingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: MeasurementReadingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReadings(readings: List<MeasurementReadingEntity>)

    @Query("SELECT * FROM meter_replacements ORDER BY replacedAt DESC, id DESC")
    fun observeReplacements(): Flow<List<MeterReplacementEntity>>

    @Query("SELECT * FROM meter_replacements ORDER BY id ASC")
    suspend fun dumpAllReplacements(): List<MeterReplacementEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplacement(item: MeterReplacementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReplacements(items: List<MeterReplacementEntity>)

    @Query("DELETE FROM meter_replacements WHERE id = :id")
    suspend fun deleteReplacementById(id: Long)

    @Query("DELETE FROM meter_replacements")
    suspend fun deleteAllReplacements()

    @Query("DELETE FROM measuring_points")
    suspend fun deleteAllPoints()

    @Query("DELETE FROM measurement_readings")
    suspend fun deleteAllReadings()
}

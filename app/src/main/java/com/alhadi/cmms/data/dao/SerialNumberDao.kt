package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SerialNumberDao {
    @Query("SELECT * FROM serial_number_profiles ORDER BY code ASC")
    fun observeProfiles(): Flow<List<SerialNumberProfileEntity>>

    @Query("SELECT * FROM serial_numbers ORDER BY serialNumber ASC")
    fun observeSerialNumbers(): Flow<List<SerialNumberEntity>>

    @Query("SELECT * FROM serial_number_movements ORDER BY createdAt DESC, id DESC LIMIT 250")
    fun observeMovements(): Flow<List<SerialNumberMovementEntity>>

    @Query("SELECT * FROM serial_number_profiles ORDER BY id ASC")
    suspend fun dumpProfiles(): List<SerialNumberProfileEntity>

    @Query("SELECT * FROM serial_numbers ORDER BY id ASC")
    suspend fun dumpSerialNumbers(): List<SerialNumberEntity>

    @Query("SELECT * FROM serial_number_movements ORDER BY id ASC")
    suspend fun dumpMovements(): List<SerialNumberMovementEntity>

    @Query("SELECT * FROM serial_number_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: Long): SerialNumberProfileEntity?

    @Query("SELECT * FROM serial_numbers WHERE id = :id LIMIT 1")
    suspend fun getSerial(id: Long): SerialNumberEntity?

    @Query("SELECT * FROM serial_numbers WHERE partId = :partId AND serialNumber = :serialNumber LIMIT 1")
    suspend fun findByPartAndNumber(partId: Long, serialNumber: String): SerialNumberEntity?

    @Query("SELECT * FROM serial_numbers WHERE assetId = :assetId LIMIT 1")
    suspend fun findByAsset(assetId: Long): SerialNumberEntity?

    @Query("SELECT * FROM serial_numbers WHERE partId = :partId ORDER BY serialNumber ASC")
    suspend fun serialsForPart(partId: Long): List<SerialNumberEntity>

    @Query("SELECT * FROM serial_numbers WHERE partId = :partId AND status = 'InStock' ORDER BY serialNumber ASC")
    suspend fun availableForPart(partId: Long): List<SerialNumberEntity>

    @Query("SELECT COUNT(*) FROM serial_numbers WHERE partId = :partId AND status = 'InStock'")
    suspend fun countInStock(partId: Long): Int

    @Query("SELECT COUNT(*) FROM serial_numbers WHERE partId = :partId")
    suspend fun countForPart(partId: Long): Int

    @Query("SELECT COUNT(*) FROM serial_numbers WHERE profileId = :profileId")
    suspend fun countForProfile(profileId: Long): Int

    @Query("SELECT COUNT(*) FROM spare_parts WHERE serialProfileId = :profileId")
    suspend fun countPartsUsingProfile(profileId: Long): Int

    @Query("SELECT COUNT(*) FROM serial_numbers WHERE assetId = :assetId")
    suspend fun countForAsset(assetId: Long): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProfile(profile: SerialNumberProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: SerialNumberProfileEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProfiles(profiles: List<SerialNumberProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSerial(serial: SerialNumberEntity): Long

    @Update
    suspend fun updateSerial(serial: SerialNumberEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSerials(serials: List<SerialNumberEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMovement(movement: SerialNumberMovementEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMovements(movements: List<SerialNumberMovementEntity>)

    @Query("DELETE FROM serial_number_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Long)

    @Query("DELETE FROM serial_number_movements WHERE serialId = :serialId")
    suspend fun deleteMovementsForSerial(serialId: Long)

    @Query("DELETE FROM serial_numbers WHERE id = :id")
    suspend fun deleteSerial(id: Long)

    @Query("DELETE FROM serial_number_movements")
    suspend fun deleteAllMovements()

    @Query("DELETE FROM serial_numbers")
    suspend fun deleteAllSerialNumbers()

    @Query("DELETE FROM serial_number_profiles")
    suspend fun deleteAllProfiles()
}

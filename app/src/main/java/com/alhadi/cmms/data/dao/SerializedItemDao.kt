package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.SerialMovementEntity
import com.alhadi.cmms.data.entity.SerializedItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SerializedItemDao {
    @Query("SELECT * FROM serialized_items ORDER BY serialNumber ASC")
    fun observeItems(): Flow<List<SerializedItemEntity>>

    @Query("SELECT * FROM serial_movements ORDER BY occurredAt DESC, id DESC")
    fun observeMovements(): Flow<List<SerialMovementEntity>>

    @Query("SELECT * FROM serialized_items ORDER BY id ASC")
    suspend fun dumpItems(): List<SerializedItemEntity>

    @Query("SELECT * FROM serial_movements ORDER BY id ASC")
    suspend fun dumpMovements(): List<SerialMovementEntity>

    @Query("SELECT * FROM serialized_items WHERE serialNumber = :serial AND id != :excludeId LIMIT 1")
    suspend fun findDuplicate(serial: String, excludeId: Long): SerializedItemEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: SerializedItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<SerializedItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovement(item: SerialMovementEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMovements(items: List<SerialMovementEntity>)

    @Query("DELETE FROM serialized_items WHERE id = :id")
    suspend fun deleteItemById(id: Long)

    @Query("DELETE FROM serial_movements WHERE id = :id")
    suspend fun deleteMovementById(id: Long)

    @Query("DELETE FROM serial_movements")
    suspend fun deleteAllMovements()

    @Query("DELETE FROM serialized_items")
    suspend fun deleteAllItems()
}

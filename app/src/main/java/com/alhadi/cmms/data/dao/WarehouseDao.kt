package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.WarehouseEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WarehouseDao {
    @Query("SELECT * FROM warehouses ORDER BY code ASC")
    fun observeWarehouses(): Flow<List<WarehouseEntity>>

    @Query("SELECT * FROM warehouses ORDER BY code ASC")
    suspend fun dumpAll(): List<WarehouseEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(warehouse: WarehouseEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(warehouses: List<WarehouseEntity>)

    @Query("DELETE FROM warehouses WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM warehouses")
    suspend fun deleteAll()
}

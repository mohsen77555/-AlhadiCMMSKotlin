package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetStatusHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetStatusHistoryDao {
    @Query("SELECT * FROM asset_status_history ORDER BY id DESC")
    fun observeAll(): Flow<List<AssetStatusHistoryEntity>>

    @Query("SELECT * FROM asset_status_history WHERE assetId = :assetId ORDER BY id DESC")
    suspend fun forAsset(assetId: Long): List<AssetStatusHistoryEntity>

    @Query("SELECT * FROM asset_status_history ORDER BY id ASC")
    suspend fun dumpAll(): List<AssetStatusHistoryEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AssetStatusHistoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AssetStatusHistoryEntity>)

    @Query("DELETE FROM asset_status_history")
    suspend fun deleteAll()
}

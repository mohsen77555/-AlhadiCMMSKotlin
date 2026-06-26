package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetInstallationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetInstallationDao {
    @Query("SELECT * FROM asset_installations ORDER BY id DESC")
    fun observeAll(): Flow<List<AssetInstallationEntity>>

    @Query("SELECT * FROM asset_installations WHERE assetId = :assetId ORDER BY id DESC")
    suspend fun forAsset(assetId: Long): List<AssetInstallationEntity>

    @Query("SELECT * FROM asset_installations ORDER BY id ASC")
    suspend fun dumpAll(): List<AssetInstallationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(record: AssetInstallationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(records: List<AssetInstallationEntity>)

    @Query("DELETE FROM asset_installations")
    suspend fun deleteAll()
}

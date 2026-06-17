package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDao {
    @Query("SELECT * FROM assets ORDER BY groupName ASC, code ASC")
    fun observeAssets(): Flow<List<AssetEntity>>

    @Query("SELECT * FROM assets WHERE id = :id LIMIT 1")
    suspend fun getAssetById(id: Long): AssetEntity?

    @Query("SELECT COUNT(*) FROM assets")
    fun observeAssetCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM assets")
    suspend fun countOnce(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAssets(assets: List<AssetEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAsset(asset: AssetEntity): Long

    @Query("DELETE FROM assets")
    suspend fun deleteAll()
}

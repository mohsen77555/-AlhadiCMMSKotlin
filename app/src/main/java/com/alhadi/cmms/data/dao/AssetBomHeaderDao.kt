package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetBomHeaderDao {
    @Query("SELECT * FROM asset_bom_headers ORDER BY code ASC, alternative ASC, id ASC")
    fun observeHeaders(): Flow<List<AssetBomHeaderEntity>>

    @Query("SELECT * FROM asset_bom_headers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AssetBomHeaderEntity?

    @Query("SELECT * FROM asset_bom_headers WHERE assetId = :assetId ORDER BY code ASC, alternative ASC")
    suspend fun headersForAsset(assetId: Long): List<AssetBomHeaderEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(header: AssetBomHeaderEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(headers: List<AssetBomHeaderEntity>)

    @Update
    suspend fun update(header: AssetBomHeaderEntity)

    @Query("DELETE FROM asset_bom_headers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_bom_headers")
    suspend fun deleteAll()
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetClassEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetClassDao {
    @Query("SELECT * FROM asset_classes ORDER BY assetType ASC, code ASC")
    fun observeClasses(): Flow<List<AssetClassEntity>>

    @Query("SELECT * FROM asset_classes ORDER BY id ASC")
    suspend fun dumpAll(): List<AssetClassEntity>

    @Query("SELECT * FROM asset_classes WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AssetClassEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AssetClassEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AssetClassEntity>)

    @Query("DELETE FROM asset_classes WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_classes")
    suspend fun deleteAll()
}

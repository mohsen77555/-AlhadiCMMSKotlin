package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetBomDao {
    @Query("SELECT * FROM asset_bom_items ORDER BY id ASC")
    fun observeBom(): Flow<List<AssetBomItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AssetBomItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AssetBomItemEntity>)

    @Query("DELETE FROM asset_bom_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_bom_items")
    suspend fun deleteAll()
}

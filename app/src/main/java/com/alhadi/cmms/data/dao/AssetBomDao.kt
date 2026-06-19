package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetBomDao {
    @Query("SELECT * FROM asset_bom_items ORDER BY headerId ASC, itemNumber ASC, id ASC")
    fun observeBom(): Flow<List<AssetBomItemEntity>>

    @Query("SELECT * FROM asset_bom_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AssetBomItemEntity?

    @Query("SELECT * FROM asset_bom_items WHERE headerId = :headerId ORDER BY itemNumber ASC, id ASC")
    suspend fun itemsForHeader(headerId: Long): List<AssetBomItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AssetBomItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AssetBomItemEntity>)

    @Query("UPDATE asset_bom_items SET parentItemId = NULL WHERE parentItemId = :parentItemId")
    suspend fun clearParent(parentItemId: Long)

    @Query("DELETE FROM asset_bom_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_bom_items WHERE headerId = :headerId")
    suspend fun deleteForHeader(headerId: Long)

    @Query("DELETE FROM asset_bom_items")
    suspend fun deleteAll()
}

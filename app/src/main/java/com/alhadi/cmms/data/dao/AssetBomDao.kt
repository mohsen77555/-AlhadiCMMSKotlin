package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetBomRevisionEntity
import com.alhadi.cmms.data.entity.BomAlternativeEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetBomDao {
    @Query("SELECT * FROM asset_bom_items ORDER BY assetId ASC, position ASC, id ASC")
    fun observeBom(): Flow<List<AssetBomItemEntity>>

    @Query("SELECT * FROM asset_bom_revisions ORDER BY assetId ASC, revisionCode DESC")
    fun observeRevisions(): Flow<List<AssetBomRevisionEntity>>

    @Query("SELECT * FROM bom_alternatives ORDER BY bomItemId ASC, priority ASC")
    fun observeAlternatives(): Flow<List<BomAlternativeEntity>>

    @Query("SELECT * FROM asset_bom_items ORDER BY id ASC")
    suspend fun dumpItems(): List<AssetBomItemEntity>

    @Query("SELECT * FROM asset_bom_revisions ORDER BY id ASC")
    suspend fun dumpRevisions(): List<AssetBomRevisionEntity>

    @Query("SELECT * FROM bom_alternatives ORDER BY id ASC")
    suspend fun dumpAlternatives(): List<BomAlternativeEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AssetBomItemEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AssetBomItemEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevision(item: AssetBomRevisionEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRevisions(items: List<AssetBomRevisionEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlternative(item: BomAlternativeEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlternatives(items: List<BomAlternativeEntity>)

    @Query("DELETE FROM asset_bom_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_bom_revisions WHERE id = :id")
    suspend fun deleteRevisionById(id: Long)

    @Query("DELETE FROM bom_alternatives WHERE id = :id")
    suspend fun deleteAlternativeById(id: Long)

    @Query("DELETE FROM bom_alternatives")
    suspend fun deleteAllAlternatives()

    @Query("DELETE FROM asset_bom_revisions")
    suspend fun deleteAllRevisions()

    @Query("DELETE FROM asset_bom_items")
    suspend fun deleteAll()
}

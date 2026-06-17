package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetDocumentDao {
    @Query("SELECT * FROM asset_documents ORDER BY id DESC")
    fun observeDocuments(): Flow<List<AssetDocumentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(doc: AssetDocumentEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(docs: List<AssetDocumentEntity>)

    @Query("DELETE FROM asset_documents WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_documents")
    suspend fun deleteAll()
}

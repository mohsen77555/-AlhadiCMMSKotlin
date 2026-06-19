package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetPartnerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetPartnerDao {
    @Query("SELECT * FROM asset_partners ORDER BY assetId ASC, isPrimary DESC, partnerRole ASC")
    fun observePartners(): Flow<List<AssetPartnerEntity>>

    @Query("SELECT * FROM asset_partners ORDER BY id ASC")
    suspend fun dumpAll(): List<AssetPartnerEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AssetPartnerEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AssetPartnerEntity>)

    @Query("UPDATE asset_partners SET isPrimary = 0 WHERE assetId = :assetId AND partnerRole = :role")
    suspend fun clearPrimaryForRole(assetId: Long, role: String)

    @Query("DELETE FROM asset_partners WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_partners")
    suspend fun deleteAll()
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetWarrantyEntity
import com.alhadi.cmms.data.entity.WarrantyClaimEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetWarrantyDao {
    @Query("SELECT * FROM asset_warranties ORDER BY assetId ASC, endDate DESC")
    fun observeWarranties(): Flow<List<AssetWarrantyEntity>>

    @Query("SELECT * FROM warranty_claims ORDER BY openedAt DESC, id DESC")
    fun observeClaims(): Flow<List<WarrantyClaimEntity>>

    @Query("SELECT * FROM asset_warranties ORDER BY id ASC")
    suspend fun dumpAllWarranties(): List<AssetWarrantyEntity>

    @Query("SELECT * FROM warranty_claims ORDER BY id ASC")
    suspend fun dumpAllClaims(): List<WarrantyClaimEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarranty(item: AssetWarrantyEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWarranties(items: List<AssetWarrantyEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaim(item: WarrantyClaimEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertClaims(items: List<WarrantyClaimEntity>)

    @Query("DELETE FROM asset_warranties WHERE id = :id")
    suspend fun deleteWarrantyById(id: Long)

    @Query("DELETE FROM warranty_claims WHERE id = :id")
    suspend fun deleteClaimById(id: Long)

    @Query("DELETE FROM warranty_claims")
    suspend fun deleteAllClaims()

    @Query("DELETE FROM asset_warranties")
    suspend fun deleteAllWarranties()

    @Query("SELECT COUNT(*) FROM warranty_claims")
    suspend fun claimCount(): Int
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetCharacteristicDao {
    @Query("SELECT * FROM asset_characteristics ORDER BY name ASC")
    fun observeCharacteristics(): Flow<List<AssetCharacteristicEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AssetCharacteristicEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<AssetCharacteristicEntity>)

    @Query("DELETE FROM asset_characteristics WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_characteristics")
    suspend fun deleteAll()
}

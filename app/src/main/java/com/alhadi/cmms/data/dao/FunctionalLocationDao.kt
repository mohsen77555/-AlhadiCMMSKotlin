package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FunctionalLocationDao {
    @Query("SELECT * FROM functional_locations ORDER BY code ASC")
    fun observeLocations(): Flow<List<FunctionalLocationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(location: FunctionalLocationEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(locations: List<FunctionalLocationEntity>)

    @Query("DELETE FROM functional_locations WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM functional_locations")
    suspend fun deleteAll()
}

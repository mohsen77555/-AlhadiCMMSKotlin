package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.OrgUnitEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface OrgUnitDao {
    @Query("SELECT * FROM org_units ORDER BY code ASC")
    fun observeOrgUnits(): Flow<List<OrgUnitEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(unit: OrgUnitEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(units: List<OrgUnitEntity>)

    @Query("DELETE FROM org_units WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM org_units")
    suspend fun deleteAll()
}

package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetFinancialRecordEntity
import com.alhadi.cmms.data.entity.FinancialPostingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetFinancialDao {
    @Query("SELECT * FROM asset_financial_records ORDER BY assetId ASC, id DESC")
    fun observeRecords(): Flow<List<AssetFinancialRecordEntity>>

    @Query("SELECT * FROM financial_postings ORDER BY postingDate DESC, id DESC")
    fun observePostings(): Flow<List<FinancialPostingEntity>>

    @Query("SELECT * FROM asset_financial_records ORDER BY id ASC")
    suspend fun dumpRecords(): List<AssetFinancialRecordEntity>

    @Query("SELECT * FROM financial_postings ORDER BY id ASC")
    suspend fun dumpPostings(): List<FinancialPostingEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(item: AssetFinancialRecordEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecords(items: List<AssetFinancialRecordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPosting(item: FinancialPostingEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPostings(items: List<FinancialPostingEntity>)

    @Query("DELETE FROM asset_financial_records WHERE id = :id")
    suspend fun deleteRecordById(id: Long)

    @Query("DELETE FROM financial_postings WHERE id = :id")
    suspend fun deletePostingById(id: Long)

    @Query("DELETE FROM financial_postings")
    suspend fun deleteAllPostings()

    @Query("DELETE FROM asset_financial_records")
    suspend fun deleteAllRecords()
}

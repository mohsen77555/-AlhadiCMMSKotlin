package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.DataQualityIssueEntity
import com.alhadi.cmms.data.entity.ImportBatchEntity
import com.alhadi.cmms.data.entity.ImportIssueEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DataQualityDao {
    @Query("SELECT * FROM import_batches ORDER BY startedAt DESC, id DESC")
    fun observeBatches(): Flow<List<ImportBatchEntity>>

    @Query("SELECT * FROM import_issues ORDER BY batchId DESC, severity DESC, rowNumber ASC")
    fun observeImportIssues(): Flow<List<ImportIssueEntity>>

    @Query("SELECT * FROM data_quality_issues ORDER BY status ASC, severity DESC, detectedAt DESC")
    fun observeQualityIssues(): Flow<List<DataQualityIssueEntity>>

    @Query("SELECT * FROM import_batches ORDER BY id ASC")
    suspend fun dumpBatches(): List<ImportBatchEntity>

    @Query("SELECT * FROM import_issues ORDER BY id ASC")
    suspend fun dumpImportIssues(): List<ImportIssueEntity>

    @Query("SELECT * FROM data_quality_issues ORDER BY id ASC")
    suspend fun dumpQualityIssues(): List<DataQualityIssueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(item: ImportBatchEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatches(items: List<ImportBatchEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportIssue(item: ImportIssueEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImportIssues(items: List<ImportIssueEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQualityIssue(item: DataQualityIssueEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQualityIssues(items: List<DataQualityIssueEntity>)

    @Query("UPDATE import_batches SET status = :status, acceptedRows = :accepted, rejectedRows = :rejected, completedAt = :completedAt, summary = :summary WHERE id = :id")
    suspend fun finishBatch(id: Long, status: String, accepted: Int, rejected: Int, completedAt: String, summary: String)

    @Query("UPDATE data_quality_issues SET status = 'Resolved', resolvedAt = :resolvedAt, resolvedBy = :resolvedBy WHERE id = :id")
    suspend fun resolveQualityIssue(id: Long, resolvedAt: String, resolvedBy: String)

    @Query("DELETE FROM data_quality_issues WHERE status = 'Open'")
    suspend fun deleteOpenQualityIssues()

    @Query("DELETE FROM data_quality_issues")
    suspend fun deleteAllQualityIssues()

    @Query("DELETE FROM import_issues")
    suspend fun deleteAllImportIssues()

    @Query("DELETE FROM import_batches")
    suspend fun deleteAllBatches()
}

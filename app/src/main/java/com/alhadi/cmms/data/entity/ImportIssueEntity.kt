package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "import_issues",
    indices = [Index(value = ["batchId"]), Index(value = ["severity"])]
)
@Serializable
data class ImportIssueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val batchId: Long,
    val sheetName: String,
    val rowNumber: Int,
    val fieldName: String = "",
    val severity: String,
    val code: String,
    val message: String,
    val rawValue: String = "",
    val status: String = "Open"
)

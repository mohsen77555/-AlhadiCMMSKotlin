package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "import_batches",
    indices = [Index(value = ["status"]), Index(value = ["startedAt"])]
)
@Serializable
data class ImportBatchEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fileName: String,
    val sourceType: String = "Excel",
    val status: String = "Validating",
    val totalRows: Int = 0,
    val acceptedRows: Int = 0,
    val rejectedRows: Int = 0,
    val startedAt: String,
    val completedAt: String = "",
    val actor: String,
    val checksum: String = "",
    val summary: String = ""
)

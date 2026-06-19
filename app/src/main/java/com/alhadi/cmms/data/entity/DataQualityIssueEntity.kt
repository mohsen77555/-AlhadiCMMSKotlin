package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "data_quality_issues",
    indices = [Index(value = ["status"]), Index(value = ["severity"]), Index(value = ["entityType", "entityId"])]
)
@Serializable
data class DataQualityIssueEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val ruleCode: String,
    val severity: String,
    val entityType: String,
    val entityId: Long? = null,
    val fieldName: String = "",
    val message: String,
    val status: String = "Open",
    val detectedAt: String,
    val resolvedAt: String = "",
    val resolvedBy: String = ""
)

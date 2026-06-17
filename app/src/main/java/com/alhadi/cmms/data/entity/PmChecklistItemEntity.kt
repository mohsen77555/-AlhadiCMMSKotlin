package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A single inspection step within a preventive-maintenance task's checklist.
 * [result] is one of "" (pending), "OK", "NotOK", "NA"; [orderIndex] controls display order.
 */
@Entity(
    tableName = "pm_checklist_items",
    indices = [Index(value = ["pmId"])]
)
@Serializable
data class PmChecklistItemEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val pmId: Long,
    val text: String,
    val result: String = "",
    val note: String = "",
    val orderIndex: Int = 0
)

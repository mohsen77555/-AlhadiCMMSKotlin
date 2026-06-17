package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "spare_parts",
    indices = [Index(value = ["partNumber"], unique = true)]
)
@Serializable
data class SparePartEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partNumber: String,
    val name: String,
    val equipmentGroup: String,
    val unit: String,
    val onHandQty: Int,
    val minQty: Int,
    val location: String,
    val lastPrice: Double
)

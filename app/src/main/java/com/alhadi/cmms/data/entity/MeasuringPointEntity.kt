package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A measuring point on an asset (running hours, temperature, vibration, pressure...).
 * Either a cumulative counter or a plain reading, with an optional upper alarm limit.
 */
@Entity(
    tableName = "measuring_points",
    indices = [Index(value = ["assetId"])]
)
@Serializable
data class MeasuringPointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val name: String,
    val unit: String,
    val isCounter: Boolean,
    val upperLimit: Double?,
    val lastReading: Double,
    val lastReadingAt: String
)

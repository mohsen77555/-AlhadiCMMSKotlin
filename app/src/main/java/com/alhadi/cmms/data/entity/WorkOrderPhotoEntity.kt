package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A photo attached to a work order as proof of executed work. The image bytes are copied
 * into the app's internal storage and [path] holds the absolute file path.
 * At least one photo is required before a work order can be closed.
 */
@Entity(
    tableName = "work_order_photos",
    indices = [Index(value = ["orderId"])]
)
@Serializable
data class WorkOrderPhotoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderId: Long,
    val path: String,
    val caption: String = "",
    val addedBy: String,
    val addedAt: String
)

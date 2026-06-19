package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "asset_classes",
    indices = [Index(value = ["code"], unique = true), Index(value = ["assetType"])]
)
@Serializable
data class AssetClassEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    val description: String = "",
    val assetType: String = "Equipment",
    val isActive: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = ""
)

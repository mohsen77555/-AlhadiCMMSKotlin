package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A document attached to an asset (image, manual, drawing, certificate...).
 * Stores a reference (URL / path / note) rather than the binary itself.
 */
@Entity(
    tableName = "asset_documents",
    indices = [Index(value = ["assetId"])]
)
@Serializable
data class AssetDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long,
    val type: String,
    val title: String,
    val reference: String,
    val uploadedBy: String,
    val uploadedAt: String
)

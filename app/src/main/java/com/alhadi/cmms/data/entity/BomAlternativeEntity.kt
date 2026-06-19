package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "bom_alternatives",
    indices = [Index(value = ["bomItemId"]), Index(value = ["alternatePartId"])]
)
@Serializable
data class BomAlternativeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val bomItemId: Long,
    val alternatePartId: Long,
    val priority: Int = 1,
    val interchangeability: String = "Full",
    val validFrom: String = "",
    val validTo: String = "",
    val notes: String = ""
)

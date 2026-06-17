package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * A functional location: a physical/logical place where assets are installed
 * (Factory → Line → Area → Position). Supports a parent/child hierarchy.
 */
@Entity(
    tableName = "functional_locations",
    indices = [Index(value = ["code"], unique = true), Index(value = ["parentId"])]
)
data class FunctionalLocationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    val parentId: Long?,
    val description: String,
    val status: String = "Active"
)

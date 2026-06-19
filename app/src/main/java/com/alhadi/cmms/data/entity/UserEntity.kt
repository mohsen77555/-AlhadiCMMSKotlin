package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
@Serializable
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val username: String,
    val role: String,
    val isActive: Boolean,
    val password: String = "1234"
) {
    val isAdmin: Boolean
        get() = role.equals("Admin", ignoreCase = true)

    val isSupervisor: Boolean
        get() = role.equals("Supervisor", ignoreCase = true)

    val isPlanner: Boolean
        get() = role.equals("Planner", ignoreCase = true)

    /** Admins, supervisors and planners may create work orders and manage assets / PM. */
    val canManage: Boolean
        get() = isAdmin || isSupervisor || isPlanner

    val initials: String
        get() = name.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { username.take(2).uppercase() }
}

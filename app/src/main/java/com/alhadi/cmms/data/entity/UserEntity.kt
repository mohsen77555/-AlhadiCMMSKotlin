package com.alhadi.cmms.data.entity

import kotlinx.serialization.Serializable

import androidx.room.ColumnInfo
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
    val password: String = "1234",
    // --- Account governance (chapter 10) ---
    @ColumnInfo(defaultValue = "''")
    val email: String = "",
    @ColumnInfo(defaultValue = "''")
    val phone: String = "",
    @ColumnInfo(defaultValue = "''")
    val department: String = "",
    @ColumnInfo(defaultValue = "''")
    val employeeId: String = "",
    /** Last successful login timestamp (USR-LOG-001). */
    @ColumnInfo(defaultValue = "''")
    val lastLoginAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val createdAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val passwordChangedAt: String = "",
    /** Force a password change at next login (USR-PWD-002). */
    @ColumnInfo(defaultValue = "0")
    val mustChangePassword: Boolean = false,
    /** Consecutive failed login attempts (USR-SEC-003). */
    @ColumnInfo(defaultValue = "0")
    val failedLoginCount: Int = 0,
    /** Account locked after too many failed attempts (USR-SEC-004). */
    @ColumnInfo(defaultValue = "0")
    val locked: Boolean = false,
    // --- Role-based access (chapter 11) ---
    /** Technician craft/trade: Electrical / Mechanical / Welding / Lathe (display + filtering). */
    @ColumnInfo(defaultValue = "''")
    val craft: String = "",
    /** Comma-separated asset group names this user is responsible for (scoping). Blank = unrestricted. */
    @ColumnInfo(defaultValue = "''")
    val assignedGroups: String = ""
) {
    val isAdmin: Boolean
        get() = role.equals("Admin", ignoreCase = true) || role.equals("SystemAdmin", ignoreCase = true)

    val isSupervisor: Boolean
        get() = role.equals("Supervisor", ignoreCase = true) || role.equals("MaintenanceManager", ignoreCase = true)

    /** Admins and maintenance managers may create work orders and manage inventory / PM. */
    val canManage: Boolean
        get() = isAdmin || isSupervisor

    /** The asset group names this user is scoped to (empty = unrestricted). */
    val assignedGroupSet: Set<String>
        get() = assignedGroups.split(',').map { it.trim() }.filter { it.isNotBlank() }.toSet()

    val initials: String
        get() = name.trim()
            .split(" ")
            .filter { it.isNotBlank() }
            .take(2)
            .joinToString("") { it.first().uppercase() }
            .ifBlank { username.take(2).uppercase() }
}

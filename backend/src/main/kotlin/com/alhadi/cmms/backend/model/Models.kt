package com.alhadi.cmms.backend.model

import kotlinx.serialization.Serializable
import java.time.Instant
import java.util.UUID

internal data class UserRecord(
    val id: UUID,
    val organizationId: UUID,
    val organizationCode: String,
    val name: String,
    val username: String,
    val passwordHash: String,
    val role: String,
    val isActive: Boolean,
    val tokenVersion: Int,
    val createdAt: Instant
)

internal data class RefreshTokenRecord(
    val id: UUID,
    val userId: UUID,
    val tokenHash: String,
    val expiresAt: Instant,
    val revokedAt: Instant?
)

@Serializable
data class LoginRequest(
    val username: String,
    val password: String,
    val organizationCode: String? = null
)

@Serializable
data class RefreshRequest(val refreshToken: String)

@Serializable
data class LogoutRequest(val refreshToken: String)

@Serializable
data class UserResponse(
    val id: String,
    val organizationId: String,
    val organizationCode: String,
    val name: String,
    val username: String,
    val role: String,
    val isActive: Boolean
)

@Serializable
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer",
    val expiresInSeconds: Long,
    val user: UserResponse
)

@Serializable
data class ApiError(
    val code: String,
    val message: String,
    val requestId: String? = null
)

@Serializable
data class HealthResponse(
    val status: String,
    val service: String,
    val version: String,
    val timestamp: String
)

@Serializable
data class ServiceInfoResponse(
    val service: String,
    val version: String,
    val environment: String,
    val authenticatedUser: UserResponse
)

internal fun UserRecord.toResponse() = UserResponse(
    id = id.toString(),
    organizationId = organizationId.toString(),
    organizationCode = organizationCode,
    name = name,
    username = username,
    role = role,
    isActive = isActive
)

package com.alhadi.cmms.backend.auth

import com.alhadi.cmms.backend.config.AppConfig
import com.alhadi.cmms.backend.model.TokenResponse
import com.alhadi.cmms.backend.model.UserRecord
import com.alhadi.cmms.backend.model.toResponse
import com.alhadi.cmms.backend.security.AuthenticationException
import com.alhadi.cmms.backend.security.JwtService
import com.alhadi.cmms.backend.security.PasswordHasher
import com.alhadi.cmms.backend.security.RefreshTokenFactory
import com.alhadi.cmms.backend.security.TooManyAttemptsException
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

internal class AuthService(
    private val config: AppConfig,
    private val repository: AuthRepository,
    private val jwtService: JwtService
) {
    private val throttle = LoginThrottle()

    fun bootstrapAdminIfNeeded() {
        if (repository.countUsers() > 0) return
        val username = config.bootstrapAdminUsername ?: return
        val password = config.bootstrapAdminPassword ?: return
        val user = repository.transaction { connection ->
            val organizationId = repository.findOrganizationId(config.defaultOrganizationCode, connection)
                ?: repository.createOrganization(
                    config.defaultOrganizationCode,
                    config.defaultOrganizationName,
                    connection
                )
            val passwordHash = PasswordHasher.hash(password.toCharArray())
            repository.insertUser(
                organizationId = organizationId,
                name = config.bootstrapAdminName ?: "System Administrator",
                username = username,
                passwordHash = passwordHash,
                role = "Admin",
                connection = connection
            )
        }
        repository.audit(
            organizationId = user.organizationId,
            userId = user.id,
            action = "Bootstrap",
            entityType = "User",
            details = "Initial administrator created",
            ipAddress = null
        )
    }

    fun login(
        organizationCode: String?,
        username: String,
        password: String,
        ipAddress: String?,
        userAgent: String?
    ): TokenResponse {
        require(username.isNotBlank()) { "Username is required" }
        require(password.isNotBlank()) { "Password is required" }
        val organization = organizationCode?.trim()?.uppercase()
            ?.takeIf { it.isNotBlank() }
            ?: config.defaultOrganizationCode
        val key = "$organization:${AuthRepository.normalizeUsername(username)}:${ipAddress.orEmpty()}"
        if (!throttle.canAttempt(key)) throw TooManyAttemptsException()

        val user = repository.findUserByUsername(organization, username)
        val valid = user != null && user.isActive && PasswordHasher.verify(password.toCharArray(), user.passwordHash)
        if (!valid) {
            throttle.recordFailure(key)
            repository.audit(
                organizationId = user?.organizationId,
                userId = user?.id,
                action = "LoginFailed",
                entityType = "User",
                details = "Invalid login attempt for ${username.take(100)}",
                ipAddress = ipAddress
            )
            throw AuthenticationException("Invalid username or password")
        }

        throttle.recordSuccess(key)
        val result = issueTokens(user, ipAddress, userAgent)
        repository.audit(user.organizationId, user.id, "Login", "User", "Login successful", ipAddress)
        return result
    }

    fun refresh(refreshToken: String, ipAddress: String?, userAgent: String?): TokenResponse {
        require(refreshToken.isNotBlank()) { "Refresh token is required" }
        val newRefreshToken = RefreshTokenFactory.create()
        val user = try {
            repository.rotateRefreshToken(
                oldHash = RefreshTokenFactory.hash(refreshToken),
                newHash = RefreshTokenFactory.hash(newRefreshToken),
                newExpiresAt = Instant.now().plus(config.refreshTokenDays, ChronoUnit.DAYS),
                ipAddress = ipAddress,
                userAgent = userAgent
            )
        } catch (_: IllegalStateException) {
            throw AuthenticationException("Refresh token is invalid or expired")
        }
        repository.audit(user.organizationId, user.id, "Refresh", "Session", "Session token rotated", ipAddress)
        return TokenResponse(
            accessToken = jwtService.createAccessToken(user),
            refreshToken = newRefreshToken,
            expiresInSeconds = jwtService.expiresInSeconds,
            user = user.toResponse()
        )
    }

    fun logout(refreshToken: String, ipAddress: String?) {
        if (refreshToken.isBlank()) return
        repository.revokeRefreshToken(RefreshTokenFactory.hash(refreshToken))
        repository.audit(null, null, "Logout", "Session", "Refresh token revoked", ipAddress)
    }

    fun validateUser(userId: String, tokenVersion: Int): UserRecord? {
        val id = runCatching { UUID.fromString(userId) }.getOrNull() ?: return null
        val user = repository.findUserById(id) ?: return null
        return user.takeIf { it.isActive && it.tokenVersion == tokenVersion }
    }

    fun cleanup() = repository.deleteExpiredRefreshTokens()

    private fun issueTokens(user: UserRecord, ipAddress: String?, userAgent: String?): TokenResponse {
        val refreshToken = RefreshTokenFactory.create()
        repository.insertRefreshToken(
            userId = user.id,
            tokenHash = RefreshTokenFactory.hash(refreshToken),
            expiresAt = Instant.now().plus(config.refreshTokenDays, ChronoUnit.DAYS),
            ipAddress = ipAddress,
            userAgent = userAgent
        )
        return TokenResponse(
            accessToken = jwtService.createAccessToken(user),
            refreshToken = refreshToken,
            expiresInSeconds = jwtService.expiresInSeconds,
            user = user.toResponse()
        )
    }
}

private class LoginThrottle(
    private val maxFailures: Int = 5,
    private val blockSeconds: Long = 15 * 60
) {
    private data class Entry(val failures: Int, val blockedUntil: Instant?)
    private val entries = ConcurrentHashMap<String, Entry>()

    fun canAttempt(key: String): Boolean {
        val entry = entries[key] ?: return true
        val blockedUntil = entry.blockedUntil ?: return true
        if (blockedUntil <= Instant.now()) {
            entries.remove(key)
            return true
        }
        return false
    }

    fun recordFailure(key: String) {
        entries.compute(key) { _, old ->
            val failures = (old?.failures ?: 0) + 1
            Entry(
                failures = failures,
                blockedUntil = if (failures >= maxFailures) Instant.now().plusSeconds(blockSeconds) else null
            )
        }
    }

    fun recordSuccess(key: String) {
        entries.remove(key)
    }
}

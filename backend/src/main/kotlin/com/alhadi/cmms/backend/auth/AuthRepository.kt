package com.alhadi.cmms.backend.auth

import com.alhadi.cmms.backend.db.Database
import com.alhadi.cmms.backend.model.RefreshTokenRecord
import com.alhadi.cmms.backend.model.UserRecord
import java.sql.Connection
import java.sql.ResultSet
import java.time.Instant
import java.util.UUID

internal class AuthRepository(private val database: Database) {
    fun countUsers(): Int = database.query { connection ->
        connection.prepareStatement("SELECT COUNT(*) FROM users").use { statement ->
            statement.executeQuery().use { result -> result.next(); result.getInt(1) }
        }
    }

    fun findOrganizationId(code: String, connection: Connection? = null): UUID? {
        val query: (Connection) -> UUID? = { activeConnection ->
            activeConnection.prepareStatement(
                "SELECT id FROM organizations WHERE code = ? AND is_active = TRUE LIMIT 1"
            ).use { statement ->
                statement.setString(1, code.uppercase())
                statement.executeQuery().use { result ->
                    if (result.next()) result.getObject("id", UUID::class.java) else null
                }
            }
        }
        return connection?.let(query) ?: database.query(query)
    }

    fun createOrganization(code: String, name: String, connection: Connection): UUID {
        val id = UUID.randomUUID()
        connection.prepareStatement(
            "INSERT INTO organizations(id, code, name) VALUES (?, ?, ?)"
        ).use { statement ->
            statement.setObject(1, id)
            statement.setString(2, code.uppercase())
            statement.setString(3, name.trim())
            statement.executeUpdate()
        }
        return id
    }

    fun insertUser(
        organizationId: UUID,
        name: String,
        username: String,
        passwordHash: String,
        role: String,
        connection: Connection
    ): UserRecord {
        val id = UUID.randomUUID()
        val normalized = normalizeUsername(username)
        connection.prepareStatement(
            """
            INSERT INTO users(
                id, organization_id, name, username, username_normalized, password_hash, role
            ) VALUES (?, ?, ?, ?, ?, ?, ?)
            """.trimIndent()
        ).use { statement ->
            statement.setObject(1, id)
            statement.setObject(2, organizationId)
            statement.setString(3, name.trim())
            statement.setString(4, username.trim())
            statement.setString(5, normalized)
            statement.setString(6, passwordHash)
            statement.setString(7, role)
            statement.executeUpdate()
        }
        return findUserById(id, connection) ?: throw IllegalStateException("Failed to create user")
    }

    fun findUserByUsername(organizationCode: String, username: String): UserRecord? = database.query { connection ->
        connection.prepareStatement(USER_SELECT + " WHERE o.code = ? AND u.username_normalized = ? LIMIT 1").use { statement ->
            statement.setString(1, organizationCode.uppercase())
            statement.setString(2, normalizeUsername(username))
            statement.executeQuery().use { result -> if (result.next()) result.toUserRecord() else null }
        }
    }

    fun findUserById(id: UUID, connection: Connection? = null): UserRecord? {
        val query: (Connection) -> UserRecord? = { activeConnection ->
            activeConnection.prepareStatement(USER_SELECT + " WHERE u.id = ? LIMIT 1").use { statement ->
                statement.setObject(1, id)
                statement.executeQuery().use { result -> if (result.next()) result.toUserRecord() else null }
            }
        }
        return connection?.let(query) ?: database.query(query)
    }

    fun insertRefreshToken(
        userId: UUID,
        tokenHash: String,
        expiresAt: Instant,
        ipAddress: String?,
        userAgent: String?,
        connection: Connection? = null
    ) {
        val insert: (Connection) -> Unit = { activeConnection ->
            activeConnection.prepareStatement(
                """
                INSERT INTO refresh_tokens(
                    id, user_id, token_hash, expires_at, created_by_ip, user_agent
                ) VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { statement ->
                statement.setObject(1, UUID.randomUUID())
                statement.setObject(2, userId)
                statement.setString(3, tokenHash)
                statement.setObject(
                    4,
                    java.time.OffsetDateTime.ofInstant(expiresAt, java.time.ZoneOffset.UTC)
                )
                statement.setString(5, ipAddress?.take(64))
                statement.setString(6, userAgent?.take(500))
                statement.executeUpdate()
            }
        }
        if (connection != null) insert(connection) else database.transaction(insert)
    }

    fun rotateRefreshToken(
        oldHash: String,
        newHash: String,
        newExpiresAt: Instant,
        ipAddress: String?,
        userAgent: String?
    ): UserRecord = database.transaction { connection ->
        val token = connection.prepareStatement(
            """
            SELECT id, user_id, token_hash, expires_at, revoked_at
            FROM refresh_tokens
            WHERE token_hash = ?
            FOR UPDATE
            """.trimIndent()
        ).use { statement ->
            statement.setString(1, oldHash)
            statement.executeQuery().use { result ->
                if (!result.next()) throw IllegalStateException("Invalid refresh token")
                result.toRefreshTokenRecord()
            }
        }
        if (token.revokedAt != null || token.expiresAt <= Instant.now()) {
            throw IllegalStateException("Refresh token expired or revoked")
        }
        val user = findUserById(token.userId, connection)
            ?: throw IllegalStateException("User no longer exists")
        if (!user.isActive) throw IllegalStateException("User account is disabled")

        connection.prepareStatement(
            "UPDATE refresh_tokens SET revoked_at = NOW() WHERE id = ? AND revoked_at IS NULL"
        ).use { statement ->
            statement.setObject(1, token.id)
            if (statement.executeUpdate() != 1) throw IllegalStateException("Refresh token already used")
        }
        insertRefreshToken(user.id, newHash, newExpiresAt, ipAddress, userAgent, connection)
        user
    }

    fun revokeRefreshToken(tokenHash: String) {
        database.query { connection ->
            connection.prepareStatement(
                "UPDATE refresh_tokens SET revoked_at = NOW() WHERE token_hash = ? AND revoked_at IS NULL"
            ).use { statement ->
                statement.setString(1, tokenHash)
                statement.executeUpdate()
            }
        }
    }

    fun deleteExpiredRefreshTokens() {
        database.query { connection ->
            connection.prepareStatement(
                "DELETE FROM refresh_tokens WHERE expires_at < NOW() OR revoked_at < NOW() - INTERVAL '7 days'"
            ).use { it.executeUpdate() }
        }
    }

    fun audit(
        organizationId: UUID?,
        userId: UUID?,
        action: String,
        entityType: String,
        details: String,
        ipAddress: String?
    ) {
        database.query { connection ->
            connection.prepareStatement(
                """
                INSERT INTO audit_logs(
                    organization_id, user_id, action, entity_type, details, ip_address
                ) VALUES (?, ?, ?, ?, ?, ?)
                """.trimIndent()
            ).use { statement ->
                statement.setObject(1, organizationId)
                statement.setObject(2, userId)
                statement.setString(3, action.take(80))
                statement.setString(4, entityType.take(80))
                statement.setString(5, details)
                statement.setString(6, ipAddress?.take(64))
                statement.executeUpdate()
            }
        }
    }

    fun <T> transaction(block: (Connection) -> T): T = database.transaction(block)

    private fun ResultSet.toUserRecord() = UserRecord(
        id = getObject("id", UUID::class.java),
        organizationId = getObject("organization_id", UUID::class.java),
        organizationCode = getString("organization_code"),
        name = getString("name"),
        username = getString("username"),
        passwordHash = getString("password_hash"),
        role = getString("role"),
        isActive = getBoolean("is_active"),
        tokenVersion = getInt("token_version"),
        createdAt = getObject("created_at", java.time.OffsetDateTime::class.java).toInstant()
    )

    private fun ResultSet.toRefreshTokenRecord() = RefreshTokenRecord(
        id = getObject("id", UUID::class.java),
        userId = getObject("user_id", UUID::class.java),
        tokenHash = getString("token_hash"),
        expiresAt = getObject("expires_at", java.time.OffsetDateTime::class.java).toInstant(),
        revokedAt = getObject("revoked_at", java.time.OffsetDateTime::class.java)?.toInstant()
    )

    companion object {
        private const val USER_SELECT = """
            SELECT
                u.id,
                u.organization_id,
                o.code AS organization_code,
                u.name,
                u.username,
                u.password_hash,
                u.role,
                u.is_active,
                u.token_version,
                u.created_at
            FROM users u
            JOIN organizations o ON o.id = u.organization_id
        """

        fun normalizeUsername(value: String): String = value.trim().lowercase()
    }
}

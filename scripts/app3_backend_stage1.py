from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BACKEND = ROOT / "backend"


def write(relative: str, content: str) -> None:
    path = ROOT / relative
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(content.strip() + "\n", encoding="utf-8")


write("backend/settings.gradle.kts", r'''
pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "alhadi-cmms-backend"
''')

write("backend/build.gradle.kts", r'''
plugins {
    kotlin("jvm") version "2.2.21"
    kotlin("plugin.serialization") version "2.2.21"
    id("io.ktor.plugin") version "3.5.0"
    application
}

group = "com.alhadi.cmms"
version = "0.1.0"

application {
    mainClass.set("com.alhadi.cmms.backend.ApplicationKt")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core")
    implementation("io.ktor:ktor-server-netty")
    implementation("io.ktor:ktor-server-content-negotiation")
    implementation("io.ktor:ktor-serialization-kotlinx-json")
    implementation("io.ktor:ktor-server-auth")
    implementation("io.ktor:ktor-server-auth-jwt")
    implementation("io.ktor:ktor-server-status-pages")
    implementation("io.ktor:ktor-server-call-logging")
    implementation("io.ktor:ktor-server-cors")
    implementation("io.ktor:ktor-server-default-headers")

    implementation("com.zaxxer:HikariCP:6.3.0")
    implementation("org.postgresql:postgresql:42.7.7")
    implementation("ch.qos.logback:logback-classic:1.5.18")

    testImplementation("io.ktor:ktor-server-test-host")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
''')

write("backend/src/main/kotlin/com/alhadi/cmms/backend/config/AppConfig.kt", r'''
package com.alhadi.cmms.backend.config

data class AppConfig(
    val host: String,
    val port: Int,
    val databaseUrl: String,
    val databaseUser: String,
    val databasePassword: String,
    val databasePoolSize: Int,
    val jwtSecret: String,
    val jwtIssuer: String,
    val jwtAudience: String,
    val jwtRealm: String,
    val accessTokenMinutes: Long,
    val refreshTokenDays: Long,
    val defaultOrganizationCode: String,
    val defaultOrganizationName: String,
    val bootstrapAdminName: String?,
    val bootstrapAdminUsername: String?,
    val bootstrapAdminPassword: String?,
    val corsHosts: List<String>,
    val environment: String
) {
    companion object {
        fun fromEnvironment(env: Map<String, String> = System.getenv()): AppConfig {
            fun value(name: String, default: String? = null): String =
                env[name]?.trim()?.takeIf { it.isNotEmpty() }
                    ?: default
                    ?: throw IllegalStateException("Missing required environment variable: $name")

            fun optional(name: String): String? = env[name]?.trim()?.takeIf { it.isNotEmpty() }

            val jwtSecret = value("JWT_SECRET")
            require(jwtSecret.length >= 32) { "JWT_SECRET must be at least 32 characters" }

            val bootstrapUsername = optional("BOOTSTRAP_ADMIN_USERNAME")
            val bootstrapPassword = optional("BOOTSTRAP_ADMIN_PASSWORD")
            if (bootstrapUsername != null || bootstrapPassword != null) {
                require(bootstrapUsername != null && bootstrapPassword != null) {
                    "BOOTSTRAP_ADMIN_USERNAME and BOOTSTRAP_ADMIN_PASSWORD must be provided together"
                }
                require(bootstrapPassword.length >= 12) {
                    "BOOTSTRAP_ADMIN_PASSWORD must be at least 12 characters"
                }
            }

            return AppConfig(
                host = value("HOST", "0.0.0.0"),
                port = value("PORT", "8080").toIntOrNull()
                    ?: throw IllegalArgumentException("PORT must be a number"),
                databaseUrl = value("DB_URL", "jdbc:postgresql://localhost:5432/alhadi_cmms"),
                databaseUser = value("DB_USER", "alhadi"),
                databasePassword = value("DB_PASSWORD"),
                databasePoolSize = value("DB_POOL_SIZE", "10").toIntOrNull()?.coerceIn(2, 50)
                    ?: throw IllegalArgumentException("DB_POOL_SIZE must be a number"),
                jwtSecret = jwtSecret,
                jwtIssuer = value("JWT_ISSUER", "alhadi-cmms-backend"),
                jwtAudience = value("JWT_AUDIENCE", "alhadi-cmms-app"),
                jwtRealm = value("JWT_REALM", "Alhadi CMMS API"),
                accessTokenMinutes = value("ACCESS_TOKEN_MINUTES", "15").toLongOrNull()?.coerceIn(5, 240)
                    ?: throw IllegalArgumentException("ACCESS_TOKEN_MINUTES must be a number"),
                refreshTokenDays = value("REFRESH_TOKEN_DAYS", "30").toLongOrNull()?.coerceIn(1, 180)
                    ?: throw IllegalArgumentException("REFRESH_TOKEN_DAYS must be a number"),
                defaultOrganizationCode = value("ORGANIZATION_CODE", "DEFAULT").uppercase(),
                defaultOrganizationName = value("ORGANIZATION_NAME", "Alhadi Maintenance"),
                bootstrapAdminName = optional("BOOTSTRAP_ADMIN_NAME") ?: "System Administrator",
                bootstrapAdminUsername = bootstrapUsername?.lowercase(),
                bootstrapAdminPassword = bootstrapPassword,
                corsHosts = value("CORS_HOSTS", "localhost:8080,localhost:3000")
                    .split(',')
                    .map { it.trim() }
                    .filter { it.isNotEmpty() },
                environment = value("APP_ENV", "development").lowercase()
            )
        }
    }
}
''')

write("backend/src/main/kotlin/com/alhadi/cmms/backend/model/Models.kt", r'''
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
''')

write("backend/src/main/kotlin/com/alhadi/cmms/backend/db/Database.kt", r'''
package com.alhadi.cmms.backend.db

import com.alhadi.cmms.backend.config.AppConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import java.sql.Connection

class Database(config: AppConfig) : AutoCloseable {
    private val dataSource = HikariDataSource(
        HikariConfig().apply {
            jdbcUrl = config.databaseUrl
            username = config.databaseUser
            password = config.databasePassword
            driverClassName = "org.postgresql.Driver"
            maximumPoolSize = config.databasePoolSize
            minimumIdle = 1
            connectionTimeout = 10_000
            validationTimeout = 5_000
            idleTimeout = 300_000
            maxLifetime = 1_500_000
            isAutoCommit = true
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            poolName = "alhadi-backend-pool"
            addDataSourceProperty("ApplicationName", "alhadi-cmms-backend")
            addDataSourceProperty("tcpKeepAlive", "true")
        }
    )

    init {
        migrate()
    }

    fun <T> query(block: (Connection) -> T): T = dataSource.connection.use(block)

    fun <T> transaction(block: (Connection) -> T): T = dataSource.connection.use { connection ->
        val originalAutoCommit = connection.autoCommit
        connection.autoCommit = false
        try {
            val result = block(connection)
            connection.commit()
            result
        } catch (error: Throwable) {
            connection.rollback()
            throw error
        } finally {
            connection.autoCommit = originalAutoCommit
        }
    }

    fun ping(): Boolean = runCatching {
        query { connection ->
            connection.prepareStatement("SELECT 1").use { statement ->
                statement.executeQuery().use { result -> result.next() && result.getInt(1) == 1 }
            }
        }
    }.getOrDefault(false)

    private fun migrate() {
        transaction { connection ->
            connection.createStatement().use { statement ->
                statement.execute(
                    """
                    CREATE TABLE IF NOT EXISTS schema_migrations (
                        version INTEGER PRIMARY KEY,
                        name VARCHAR(200) NOT NULL,
                        applied_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
                    )
                    """.trimIndent()
                )
            }

            val migrations = listOf(
                Migration(1, "baseline", "db/migration/V1__baseline.sql")
            )

            migrations.forEach { migration ->
                val applied = connection.prepareStatement(
                    "SELECT EXISTS(SELECT 1 FROM schema_migrations WHERE version = ?)"
                ).use { statement ->
                    statement.setInt(1, migration.version)
                    statement.executeQuery().use { result -> result.next() && result.getBoolean(1) }
                }
                if (!applied) {
                    val script = javaClass.classLoader.getResourceAsStream(migration.resource)
                        ?.bufferedReader()
                        ?.use { it.readText() }
                        ?: throw IllegalStateException("Missing migration resource: ${migration.resource}")
                    splitStatements(script).forEach { sql ->
                        connection.createStatement().use { statement -> statement.execute(sql) }
                    }
                    connection.prepareStatement(
                        "INSERT INTO schema_migrations(version, name) VALUES (?, ?)"
                    ).use { statement ->
                        statement.setInt(1, migration.version)
                        statement.setString(2, migration.name)
                        statement.executeUpdate()
                    }
                }
            }
        }
    }

    private fun splitStatements(script: String): List<String> {
        val statements = mutableListOf<String>()
        val current = StringBuilder()
        var inSingleQuote = false
        var index = 0
        while (index < script.length) {
            val char = script[index]
            if (char == '\'' && (index == 0 || script[index - 1] != '\\')) {
                if (inSingleQuote && index + 1 < script.length && script[index + 1] == '\'') {
                    current.append("''")
                    index += 2
                    continue
                }
                inSingleQuote = !inSingleQuote
            }
            if (char == ';' && !inSingleQuote) {
                current.toString().trim().takeIf { it.isNotEmpty() }?.let(statements::add)
                current.clear()
            } else {
                current.append(char)
            }
            index++
        }
        current.toString().trim().takeIf { it.isNotEmpty() }?.let(statements::add)
        return statements
    }

    override fun close() {
        dataSource.close()
    }

    private data class Migration(val version: Int, val name: String, val resource: String)
}
''')

write("backend/src/main/resources/db/migration/V1__baseline.sql", r'''
CREATE TABLE organizations (
    id UUID PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE RESTRICT,
    name VARCHAR(200) NOT NULL,
    username VARCHAR(100) NOT NULL,
    username_normalized VARCHAR(100) NOT NULL,
    password_hash TEXT NOT NULL,
    role VARCHAR(30) NOT NULL CHECK (role IN ('Admin', 'Supervisor', 'Planner', 'Technician', 'Viewer')),
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    token_version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    UNIQUE (organization_id, username_normalized)
);

CREATE INDEX index_users_organization_id ON users(organization_id);
CREATE INDEX index_users_active ON users(is_active);

CREATE TABLE refresh_tokens (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash CHAR(64) NOT NULL UNIQUE,
    expires_at TIMESTAMPTZ NOT NULL,
    revoked_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by_ip VARCHAR(64),
    user_agent VARCHAR(500)
);

CREATE INDEX index_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX index_refresh_tokens_expires_at ON refresh_tokens(expires_at);

CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,
    organization_id UUID REFERENCES organizations(id) ON DELETE SET NULL,
    user_id UUID REFERENCES users(id) ON DELETE SET NULL,
    action VARCHAR(80) NOT NULL,
    entity_type VARCHAR(80) NOT NULL,
    entity_id VARCHAR(100),
    details TEXT NOT NULL,
    ip_address VARCHAR(64),
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX index_audit_logs_organization_id ON audit_logs(organization_id);
CREATE INDEX index_audit_logs_created_at ON audit_logs(created_at DESC);
''')

write("backend/src/main/kotlin/com/alhadi/cmms/backend/security/Security.kt", r'''
package com.alhadi.cmms.backend.security

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.alhadi.cmms.backend.config.AppConfig
import com.alhadi.cmms.backend.model.UserRecord
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.Instant
import java.util.Base64
import java.util.Date
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PasswordHasher {
    private const val ALGORITHM = "PBKDF2WithHmacSHA256"
    private const val PREFIX = "pbkdf2_sha256"
    private const val ITERATIONS = 310_000
    private const val KEY_BITS = 256
    private const val SALT_BYTES = 16
    private val random = SecureRandom()

    fun hash(password: CharArray): String {
        require(password.size >= 12) { "Password must be at least 12 characters" }
        val salt = ByteArray(SALT_BYTES).also(random::nextBytes)
        val derived = derive(password, salt, ITERATIONS, KEY_BITS)
        return listOf(
            PREFIX,
            ITERATIONS.toString(),
            Base64.getEncoder().encodeToString(salt),
            Base64.getEncoder().encodeToString(derived)
        ).joinToString("$")
    }

    fun verify(password: CharArray, encoded: String): Boolean = runCatching {
        val parts = encoded.split('$')
        if (parts.size != 4 || parts[0] != PREFIX) return false
        val iterations = parts[1].toInt()
        if (iterations !in 100_000..1_000_000) return false
        val salt = Base64.getDecoder().decode(parts[2])
        val expected = Base64.getDecoder().decode(parts[3])
        if (salt.size !in 16..64 || expected.size !in 16..128) return false
        val actual = derive(password, salt, iterations, expected.size * 8)
        MessageDigest.isEqual(expected, actual)
    }.getOrDefault(false)

    private fun derive(password: CharArray, salt: ByteArray, iterations: Int, keyBits: Int): ByteArray {
        val specification = PBEKeySpec(password, salt, iterations, keyBits)
        return try {
            SecretKeyFactory.getInstance(ALGORITHM).generateSecret(specification).encoded
        } finally {
            specification.clearPassword()
            password.fill('\u0000')
        }
    }
}

class JwtService(private val config: AppConfig) {
    private val algorithm = Algorithm.HMAC256(config.jwtSecret)
    val verifier: JWTVerifier = JWT.require(algorithm)
        .withIssuer(config.jwtIssuer)
        .withAudience(config.jwtAudience)
        .build()

    val expiresInSeconds: Long = config.accessTokenMinutes * 60

    fun createAccessToken(user: UserRecord): String {
        val now = Instant.now()
        val expires = now.plusSeconds(expiresInSeconds)
        return JWT.create()
            .withIssuer(config.jwtIssuer)
            .withAudience(config.jwtAudience)
            .withSubject(user.id.toString())
            .withClaim("orgId", user.organizationId.toString())
            .withClaim("orgCode", user.organizationCode)
            .withClaim("role", user.role)
            .withClaim("tokenVersion", user.tokenVersion)
            .withIssuedAt(Date.from(now))
            .withExpiresAt(Date.from(expires))
            .sign(algorithm)
    }
}

object RefreshTokenFactory {
    private val random = SecureRandom()

    fun create(): String = ByteArray(48)
        .also(random::nextBytes)
        .let { Base64.getUrlEncoder().withoutPadding().encodeToString(it) }

    fun hash(token: String): String = MessageDigest.getInstance("SHA-256")
        .digest(token.toByteArray(Charsets.UTF_8))
        .joinToString("") { byte -> "%02x".format(byte) }
}

class AuthenticationException(message: String = "Authentication required") : RuntimeException(message)
class AuthorizationException(message: String = "Insufficient permissions") : RuntimeException(message)
class TooManyAttemptsException(message: String = "Too many login attempts") : RuntimeException(message)
''')

write("backend/src/main/kotlin/com/alhadi/cmms/backend/auth/AuthRepository.kt", r'''
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
                statement.setObject(4, expiresAt)
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
''')

write("backend/src/main/kotlin/com/alhadi/cmms/backend/auth/AuthService.kt", r'''
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
        repository.transaction { connection ->
            val organizationId = repository.findOrganizationId(config.defaultOrganizationCode, connection)
                ?: repository.createOrganization(
                    config.defaultOrganizationCode,
                    config.defaultOrganizationName,
                    connection
                )
            val passwordHash = PasswordHasher.hash(password.toCharArray())
            val user = repository.insertUser(
                organizationId = organizationId,
                name = config.bootstrapAdminName ?: "System Administrator",
                username = username,
                passwordHash = passwordHash,
                role = "Admin",
                connection = connection
            )
            repository.audit(
                organizationId = user.organizationId,
                userId = user.id,
                action = "Bootstrap",
                entityType = "User",
                details = "Initial administrator created",
                ipAddress = null
            )
        }
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
''')

write("backend/src/main/kotlin/com/alhadi/cmms/backend/Application.kt", r'''
package com.alhadi.cmms.backend

import com.auth0.jwt.interfaces.DecodedJWT
import com.alhadi.cmms.backend.auth.AuthRepository
import com.alhadi.cmms.backend.auth.AuthService
import com.alhadi.cmms.backend.config.AppConfig
import com.alhadi.cmms.backend.db.Database
import com.alhadi.cmms.backend.model.ApiError
import com.alhadi.cmms.backend.model.HealthResponse
import com.alhadi.cmms.backend.model.LoginRequest
import com.alhadi.cmms.backend.model.LogoutRequest
import com.alhadi.cmms.backend.model.RefreshRequest
import com.alhadi.cmms.backend.model.ServiceInfoResponse
import com.alhadi.cmms.backend.model.toResponse
import com.alhadi.cmms.backend.security.AuthenticationException
import com.alhadi.cmms.backend.security.AuthorizationException
import com.alhadi.cmms.backend.security.JwtService
import com.alhadi.cmms.backend.security.TooManyAttemptsException
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.ApplicationStopped
import io.ktor.server.application.call
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt
import io.ktor.server.auth.principal
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.defaultheaders.DefaultHeaders
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.receive
import io.ktor.server.request.userAgent
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory
import java.time.Instant
import java.util.UUID

private const val SERVICE_NAME = "alhadi-cmms-backend"
private const val SERVICE_VERSION = "0.1.0"
private val logger = LoggerFactory.getLogger("Application")

fun main() {
    val config = AppConfig.fromEnvironment()
    embeddedServer(
        factory = Netty,
        host = config.host,
        port = config.port,
        module = { module(config) }
    ).start(wait = true)
}

fun Application.module(config: AppConfig = AppConfig.fromEnvironment()) {
    val database = Database(config)
    val repository = AuthRepository(database)
    val jwtService = JwtService(config)
    val authService = AuthService(config, repository, jwtService)

    withContextBlocking { authService.bootstrapAdminIfNeeded() }
    withContextBlocking { authService.cleanup() }

    environment.monitor.subscribe(ApplicationStopped) {
        database.close()
    }

    install(DefaultHeaders)
    install(CallLogging)
    install(ContentNegotiation) {
        json(
            Json {
                ignoreUnknownKeys = true
                encodeDefaults = true
                explicitNulls = false
            }
        )
    }
    install(CORS) {
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Patch)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        allowHeader("X-Request-ID")
        exposeHeader("X-Request-ID")
        config.corsHosts.forEach { host ->
            if (host == "*") anyHost() else allowHost(host, schemes = listOf("http", "https"))
        }
    }
    install(StatusPages) {
        exception<IllegalArgumentException> { call, cause ->
            call.respondError(HttpStatusCode.BadRequest, "VALIDATION_ERROR", cause.message ?: "Invalid request")
        }
        exception<AuthenticationException> { call, cause ->
            call.respondError(HttpStatusCode.Unauthorized, "AUTHENTICATION_FAILED", cause.message ?: "Authentication failed")
        }
        exception<AuthorizationException> { call, cause ->
            call.respondError(HttpStatusCode.Forbidden, "ACCESS_DENIED", cause.message ?: "Access denied")
        }
        exception<TooManyAttemptsException> { call, cause ->
            call.respondError(HttpStatusCode.TooManyRequests, "TOO_MANY_ATTEMPTS", cause.message ?: "Try again later")
        }
        exception<Throwable> { call, cause ->
            logger.error("Unhandled request failure", cause)
            call.respondError(HttpStatusCode.InternalServerError, "INTERNAL_ERROR", "An unexpected error occurred")
        }
    }
    install(Authentication) {
        jwt("auth-jwt") {
            realm = config.jwtRealm
            verifier(jwtService.verifier)
            validate { credential ->
                val userId = credential.payload.subject ?: return@validate null
                val tokenVersion = credential.payload.getClaim("tokenVersion").asInt() ?: return@validate null
                val user = withContext(Dispatchers.IO) { authService.validateUser(userId, tokenVersion) }
                    ?: return@validate null
                JWTPrincipal(credential.payload)
            }
            challenge { _, _ ->
                call.respondError(HttpStatusCode.Unauthorized, "TOKEN_INVALID", "Access token is missing or invalid")
            }
        }
    }

    routing {
        get("/") {
            call.respond(
                mapOf(
                    "service" to SERVICE_NAME,
                    "version" to SERVICE_VERSION,
                    "api" to "/api/v1",
                    "health" to "/health/ready"
                )
            )
        }

        route("/health") {
            get("/live") {
                call.respond(
                    HealthResponse(
                        status = "UP",
                        service = SERVICE_NAME,
                        version = SERVICE_VERSION,
                        timestamp = Instant.now().toString()
                    )
                )
            }
            get("/ready") {
                val ready = withContext(Dispatchers.IO) { database.ping() }
                call.respond(
                    if (ready) HttpStatusCode.OK else HttpStatusCode.ServiceUnavailable,
                    HealthResponse(
                        status = if (ready) "UP" else "DOWN",
                        service = SERVICE_NAME,
                        version = SERVICE_VERSION,
                        timestamp = Instant.now().toString()
                    )
                )
            }
        }

        route("/api/v1") {
            route("/auth") {
                post("/login") {
                    val request = call.receive<LoginRequest>()
                    val response = withContext(Dispatchers.IO) {
                        authService.login(
                            organizationCode = request.organizationCode,
                            username = request.username,
                            password = request.password,
                            ipAddress = call.clientIp(),
                            userAgent = call.request.userAgent()
                        )
                    }
                    call.respond(response)
                }
                post("/refresh") {
                    val request = call.receive<RefreshRequest>()
                    val response = withContext(Dispatchers.IO) {
                        authService.refresh(request.refreshToken, call.clientIp(), call.request.userAgent())
                    }
                    call.respond(response)
                }
                post("/logout") {
                    val request = call.receive<LogoutRequest>()
                    withContext(Dispatchers.IO) { authService.logout(request.refreshToken, call.clientIp()) }
                    call.respond(HttpStatusCode.NoContent)
                }
            }

            authenticate("auth-jwt") {
                get("/me") {
                    val user = call.currentUser(authService)
                    call.respond(user.toResponse())
                }
                get("/system/info") {
                    val user = call.currentUser(authService)
                    if (user.role !in setOf("Admin", "Supervisor")) {
                        throw AuthorizationException()
                    }
                    call.respond(
                        ServiceInfoResponse(
                            service = SERVICE_NAME,
                            version = SERVICE_VERSION,
                            environment = config.environment,
                            authenticatedUser = user.toResponse()
                        )
                    )
                }
            }
        }
    }
}

private suspend fun ApplicationCall.currentUser(authService: AuthService) =
    principal<JWTPrincipal>()
        ?.payload
        ?.let { jwt -> authService.validateUser(jwt.subject, jwt.tokenVersion()) }
        ?: throw AuthenticationException()

private fun DecodedJWT.tokenVersion(): Int = getClaim("tokenVersion").asInt() ?: -1

private suspend fun ApplicationCall.respondError(status: HttpStatusCode, code: String, message: String) {
    val requestId = request.headers["X-Request-ID"] ?: UUID.randomUUID().toString()
    response.headers.append("X-Request-ID", requestId, safeOnly = false)
    respond(status, ApiError(code = code, message = message, requestId = requestId))
}

private fun ApplicationCall.clientIp(): String? =
    request.headers["X-Forwarded-For"]
        ?.substringBefore(',')
        ?.trim()
        ?.takeIf { it.isNotEmpty() }
        ?: request.headers["X-Real-IP"]?.trim()?.takeIf { it.isNotEmpty() }

private fun withContextBlocking(block: () -> Unit) {
    block()
}
''')

write("backend/src/main/resources/logback.xml", r'''
<configuration>
    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%d{yyyy-MM-dd'T'HH:mm:ss.SSSXXX} %-5level [%thread] %logger{36} - %msg%n</pattern>
        </encoder>
    </appender>
    <root level="INFO">
        <appender-ref ref="STDOUT"/>
    </root>
</configuration>
''')

write("backend/src/test/kotlin/com/alhadi/cmms/backend/PasswordHasherTest.kt", r'''
package com.alhadi.cmms.backend

import com.alhadi.cmms.backend.security.PasswordHasher
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class PasswordHasherTest {
    @Test
    fun `password hash verifies and uses a unique salt`() {
        val password = "A-strong-test-password"
        val first = PasswordHasher.hash(password.toCharArray())
        val second = PasswordHasher.hash(password.toCharArray())

        assertNotEquals(first, second)
        assertTrue(PasswordHasher.verify(password.toCharArray(), first))
        assertTrue(PasswordHasher.verify(password.toCharArray(), second))
        assertFalse(PasswordHasher.verify("wrong-password".toCharArray(), first))
    }

    @Test
    fun `malformed hashes are rejected`() {
        assertFalse(PasswordHasher.verify("any-password".toCharArray(), "not-a-hash"))
    }
}
''')

write("backend/Dockerfile", r'''
FROM gradle:8.13-jdk17 AS build
WORKDIR /workspace
COPY settings.gradle.kts build.gradle.kts ./
COPY src ./src
RUN gradle --no-daemon clean test installDist

FROM eclipse-temurin:17-jre
RUN useradd --system --create-home --uid 10001 appuser
WORKDIR /app
COPY --from=build /workspace/build/install/alhadi-cmms-backend/ /app/
USER appuser
EXPOSE 8080
ENTRYPOINT ["/app/bin/alhadi-cmms-backend"]
''')

write("backend/.dockerignore", r'''
.gradle
build
out
.idea
*.iml
''')

write("docker-compose.backend.yml", r'''
services:
  database:
    image: postgres:16-alpine
    restart: unless-stopped
    environment:
      POSTGRES_DB: ${POSTGRES_DB:-alhadi_cmms}
      POSTGRES_USER: ${POSTGRES_USER:-alhadi}
      POSTGRES_PASSWORD: ${POSTGRES_PASSWORD}
    volumes:
      - alhadi_backend_db:/var/lib/postgresql/data
    healthcheck:
      test: ["CMD-SHELL", "pg_isready -U ${POSTGRES_USER:-alhadi} -d ${POSTGRES_DB:-alhadi_cmms}"]
      interval: 5s
      timeout: 5s
      retries: 20

  api:
    build:
      context: ./backend
    restart: unless-stopped
    depends_on:
      database:
        condition: service_healthy
    environment:
      APP_ENV: ${APP_ENV:-production}
      PORT: 8080
      DB_URL: jdbc:postgresql://database:5432/${POSTGRES_DB:-alhadi_cmms}
      DB_USER: ${POSTGRES_USER:-alhadi}
      DB_PASSWORD: ${POSTGRES_PASSWORD}
      JWT_SECRET: ${JWT_SECRET}
      JWT_ISSUER: ${JWT_ISSUER:-alhadi-cmms-backend}
      JWT_AUDIENCE: ${JWT_AUDIENCE:-alhadi-cmms-app}
      ORGANIZATION_CODE: ${ORGANIZATION_CODE:-DEFAULT}
      ORGANIZATION_NAME: ${ORGANIZATION_NAME:-Alhadi Maintenance}
      BOOTSTRAP_ADMIN_NAME: ${BOOTSTRAP_ADMIN_NAME:-System Administrator}
      BOOTSTRAP_ADMIN_USERNAME: ${BOOTSTRAP_ADMIN_USERNAME}
      BOOTSTRAP_ADMIN_PASSWORD: ${BOOTSTRAP_ADMIN_PASSWORD}
      CORS_HOSTS: ${CORS_HOSTS:-localhost:8080,localhost:3000}
    ports:
      - "${API_PORT:-8080}:8080"

volumes:
  alhadi_backend_db:
''')

write(".env.backend.example", r'''
POSTGRES_DB=alhadi_cmms
POSTGRES_USER=alhadi
POSTGRES_PASSWORD=replace-with-a-long-database-password
JWT_SECRET=replace-with-at-least-32-random-characters
JWT_ISSUER=alhadi-cmms-backend
JWT_AUDIENCE=alhadi-cmms-app
ORGANIZATION_CODE=DEFAULT
ORGANIZATION_NAME=Alhadi Maintenance
BOOTSTRAP_ADMIN_NAME=System Administrator
BOOTSTRAP_ADMIN_USERNAME=admin
BOOTSTRAP_ADMIN_PASSWORD=replace-with-a-strong-password
CORS_HOSTS=localhost:8080,localhost:3000
API_PORT=8080
APP_ENV=production
''')

write("backend/README.md", r'''
# الخادم الخلفي للتطبيق

خادم مستقل مبني بلغة Kotlin، ويعمل مع قاعدة PostgreSQL. المرحلة الأولى توفر:

- فحص الجاهزية والحالة.
- تسجيل الدخول باستخدام اسم مستخدم وكلمة مرور.
- رموز وصول قصيرة المدة ورموز تحديث دوّارة قابلة للإلغاء.
- تشفير كلمات المرور باستخدام PBKDF2 مع ملح مستقل لكل مستخدم.
- حماية من محاولات تسجيل الدخول المتكررة.
- إنشاء أول مدير من متغيرات البيئة فقط عند خلو قاعدة البيانات.
- سجل تدقيق أساسي لعمليات الدخول والجلسات.
- تشغيل محلي أو على خادم باستخدام Docker Compose.

## التشغيل

```bash
cp .env.backend.example .env.backend
# عدّل جميع كلمات المرور والأسرار داخل الملف

docker compose --env-file .env.backend -f docker-compose.backend.yml up --build -d
```

فحص الجاهزية:

```bash
curl http://localhost:8080/health/ready
```

تسجيل الدخول:

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"YOUR_PASSWORD","organizationCode":"DEFAULT"}'
```

## أهم متغيرات البيئة

- `DB_URL`, `DB_USER`, `DB_PASSWORD`
- `JWT_SECRET` بطول لا يقل عن 32 حرفاً
- `BOOTSTRAP_ADMIN_USERNAME`, `BOOTSTRAP_ADMIN_PASSWORD`
- `ORGANIZATION_CODE`, `ORGANIZATION_NAME`
- `CORS_HOSTS`

لا توجد بيانات دخول افتراضية داخل الكود. بعد إنشاء أول مدير، لا يعاد إنشاؤه عند إعادة تشغيل الخادم.
''')

print("Backend foundation stage 1 files generated successfully.")

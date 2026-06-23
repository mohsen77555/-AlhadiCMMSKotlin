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

internal class JwtService(private val config: AppConfig) {
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

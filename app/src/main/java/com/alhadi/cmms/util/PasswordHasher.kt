package com.alhadi.cmms.util

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Salted PBKDF2 password hashing so credentials are never stored in plain text.
 * Stored format: "pbkdf2:<iterations>:<saltBase64>:<hashBase64>".
 *
 * [verify] also accepts legacy plain-text values so existing accounts keep working; callers
 * should re-hash on a successful legacy login (see CmmsRepository.authenticate).
 */
object PasswordHasher {
    private const val PREFIX = "pbkdf2"
    private const val ITERATIONS = 100_000
    private const val KEY_LENGTH_BITS = 256
    // PBKDF2WithHmacSHA1 is available since API 19 (minSdk here is 23).
    private const val ALGORITHM = "PBKDF2WithHmacSHA1"

    fun isHashed(stored: String): Boolean = stored.startsWith("$PREFIX:")

    fun hash(password: String): String {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        val hash = pbkdf2(password.toCharArray(), salt, ITERATIONS, KEY_LENGTH_BITS)
        return "$PREFIX:$ITERATIONS:${encode(salt)}:${encode(hash)}"
    }

    fun verify(password: String, stored: String): Boolean {
        if (!isHashed(stored)) return stored == password // legacy plain text
        val parts = stored.split(":")
        if (parts.size != 4) return false
        val iterations = parts[1].toIntOrNull() ?: return false
        val salt = decode(parts[2])
        val expected = decode(parts[3])
        val actual = pbkdf2(password.toCharArray(), salt, iterations, expected.size * 8)
        return constantTimeEquals(expected, actual)
    }

    private fun pbkdf2(password: CharArray, salt: ByteArray, iterations: Int, keyLengthBits: Int): ByteArray {
        val spec = PBEKeySpec(password, salt, iterations, keyLengthBits)
        return SecretKeyFactory.getInstance(ALGORITHM).generateSecret(spec).encoded
    }

    private fun encode(bytes: ByteArray): String = Base64.encodeToString(bytes, Base64.NO_WRAP)
    private fun decode(value: String): ByteArray = Base64.decode(value, Base64.NO_WRAP)

    /** Length-constant comparison to avoid timing leaks. */
    private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
        if (a.size != b.size) return false
        var result = 0
        for (i in a.indices) result = result or (a[i].toInt() xor b[i].toInt())
        return result == 0
    }
}

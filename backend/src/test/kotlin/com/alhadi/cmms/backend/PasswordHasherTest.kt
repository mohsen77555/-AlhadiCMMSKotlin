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

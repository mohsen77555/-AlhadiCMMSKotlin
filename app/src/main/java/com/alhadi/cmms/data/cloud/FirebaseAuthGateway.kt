package com.alhadi.cmms.data.cloud

import com.google.firebase.auth.FirebaseAuth

/**
 * Bridges the app's username/password login to Firebase Authentication so Firestore security
 * rules can require an authenticated user instead of running the database wide open (Test mode).
 *
 * Offline-first and best-effort: every call is a no-op when Firebase isn't configured, and any
 * failure is swallowed so it never blocks the local (Room) login, which remains the source of
 * truth for credentials and works without connectivity.
 *
 * The user keeps signing in with their username; it is mapped to a stable synthetic email
 * (username@alhadi.local) behind the scenes — the email is never shown or entered.
 */
object FirebaseAuthGateway {

    private const val EMAIL_DOMAIN = "alhadi.local"

    /** Maps a username to its stable synthetic Firebase email. */
    fun emailFor(username: String): String =
        username.trim().lowercase().replace(Regex("[^a-z0-9._-]"), "_") + "@" + EMAIL_DOMAIN

    /**
     * Derives the Firebase-side password from the app password. Firebase requires at least 6
     * characters, but app/seed passwords can be shorter (e.g. the default "1234"), so we append a
     * fixed suffix. Deterministic, so the same app password always yields the same Firebase one.
     */
    private fun passwordFor(password: String): String = password + "#Alhadi6"

    /**
     * Ensures the active Firebase session matches [username]. Signs in with the synthetic email;
     * if the account doesn't exist yet — first online login, or a user provisioned before Auth was
     * wired up — it is created on the fly (which also signs them in). Fire-and-forget: Firestore
     * queues writes in its offline cache until auth resolves, then flushes them authorized.
     */
    fun ensureSignedIn(username: String, password: String) {
        if (!FirebaseGateway.isAvailable()) return
        runCatching {
            val auth = FirebaseAuth.getInstance()
            val email = emailFor(username)
            val secret = passwordFor(password)
            if (auth.currentUser?.email == email) return
            auth.signInWithEmailAndPassword(email, secret)
                .addOnFailureListener {
                    // No such account (or the local password was changed offline) → (re)create it
                    // so this and future sign-ins succeed. This is the user's own login, so the
                    // automatic sign-in that createUser performs is exactly what we want.
                    runCatching { auth.createUserWithEmailAndPassword(email, secret) }
                }
        }
    }

    /** Clears the cloud session on logout (best-effort, no-op without Firebase). */
    fun signOut() {
        if (!FirebaseGateway.isAvailable()) return
        runCatching { FirebaseAuth.getInstance().signOut() }
    }
}

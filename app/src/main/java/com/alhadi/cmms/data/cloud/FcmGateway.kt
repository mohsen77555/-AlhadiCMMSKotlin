package com.alhadi.cmms.data.cloud

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging

/**
 * Registers this device's FCM push token so the Cloud Functions backend can target notifications at
 * the signed-in user (work-order assignments) or their role (new requests, overdue escalations).
 *
 * Tokens live in the `device_tokens/{token}` collection as { token, username, role, updatedAt }.
 * Offline-first and best-effort: a no-op when Firebase isn't configured, and any failure is
 * swallowed so it never affects login. Re-registering on each login keeps the token fresh.
 */
object FcmGateway {

    fun registerDeviceToken(username: String, role: String) {
        if (!FirebaseGateway.isAvailable()) return
        runCatching {
            FirebaseMessaging.getInstance().token.addOnSuccessListener { token ->
                if (token.isNullOrBlank()) return@addOnSuccessListener
                val data = mapOf(
                    "token" to token,
                    "username" to username.trim(),
                    "role" to role,
                    "updatedAt" to System.currentTimeMillis()
                )
                runCatching {
                    FirebaseFirestore.getInstance()
                        .collection("device_tokens")
                        .document(token)
                        .set(data)
                }
            }
        }
    }
}

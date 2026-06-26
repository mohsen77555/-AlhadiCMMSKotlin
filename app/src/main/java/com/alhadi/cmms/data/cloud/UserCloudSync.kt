package com.alhadi.cmms.data.cloud

import com.alhadi.cmms.data.entity.UserEntity
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Mirrors app users to Firestore (collection "users") when Firebase is configured. Offline-first:
 * if no google-services.json is present this is a no-op and users stay local. The password hash is
 * never written to the cloud. Firestore's own offline cache queues writes until connectivity returns.
 */
object UserCloudSync {

    private const val COLLECTION = "users"

    private fun docId(user: UserEntity): String =
        user.username.ifBlank { user.id.toString() }

    /** Pushes (creates or updates) the user profile to Firestore. */
    fun upsert(user: UserEntity) {
        if (!FirebaseGateway.isAvailable()) return
        val data = mapOf(
            "id" to user.id,
            "name" to user.name,
            "username" to user.username,
            "role" to user.role,
            "isActive" to user.isActive,
            "email" to user.email,
            "phone" to user.phone,
            "department" to user.department,
            "employeeId" to user.employeeId,
            "craft" to user.craft,
            "assignedGroups" to user.assignedGroups,
            "lastLoginAt" to user.lastLoginAt,
            "locked" to user.locked,
            "mustChangePassword" to user.mustChangePassword
        )
        runCatching {
            FirebaseFirestore.getInstance().collection(COLLECTION).document(docId(user)).set(data)
        }
    }

    /** Removes the user profile from Firestore. */
    fun remove(user: UserEntity) {
        if (!FirebaseGateway.isAvailable()) return
        runCatching {
            FirebaseFirestore.getInstance().collection(COLLECTION).document(docId(user)).delete()
        }
    }
}

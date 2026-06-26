package com.alhadi.cmms.data.cloud

import kotlinx.serialization.KSerializer
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Generic Firestore mirror for any @Serializable entity (offline-first). Pushes the local write to
 * the cloud when Firebase is configured; a no-op otherwise. Firestore's offline cache queues writes
 * until connectivity returns. Pull/listen is handled separately.
 */
object EntityCloudSync {

    /** Firestore collection names, kept in one place. */
    object Collections {
        const val ASSETS = "assets"
        const val WORK_ORDERS = "work_orders"
        const val NOTIFICATIONS = "notifications"
    }

    fun <T> upsert(collection: String, docId: String, serializer: KSerializer<T>, value: T) {
        if (!FirebaseGateway.isAvailable()) return
        runCatching {
            FirebaseFirestore.getInstance()
                .collection(collection)
                .document(docId)
                .set(CloudCodec.toMap(serializer, value))
        }
    }

    fun remove(collection: String, docId: String) {
        if (!FirebaseGateway.isAvailable()) return
        runCatching {
            FirebaseFirestore.getInstance().collection(collection).document(docId).delete()
        }
    }
}

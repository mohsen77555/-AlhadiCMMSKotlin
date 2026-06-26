package com.alhadi.cmms.data.cloud

import kotlinx.serialization.KSerializer
import com.google.firebase.firestore.FirebaseFirestore

/**
 * Generic Firestore mirror for any @Serializable entity (offline-first). Pushes the local write to
 * the cloud when Firebase is configured; a no-op otherwise. Firestore's offline cache queues writes
 * until connectivity returns. Pull/listen is handled separately.
 */
object EntityCloudSync {

    /** Firestore collection names (match the Room table names), kept in one place. */
    object Collections {
        const val ASSETS = "assets"
        const val WORK_ORDERS = "work_orders"
        const val NOTIFICATIONS = "notifications"
        const val SPARE_PARTS = "spare_parts"
        const val SUPPLIERS = "suppliers"
        const val PURCHASE_ORDERS = "purchase_orders"
        const val PURCHASE_ORDER_LINES = "purchase_order_lines"
        const val WAREHOUSES = "warehouses"
        const val FUNCTIONAL_LOCATIONS = "functional_locations"
        const val ORG_UNITS = "org_units"
        const val PREVENTIVE_MAINTENANCE = "preventive_maintenance"
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

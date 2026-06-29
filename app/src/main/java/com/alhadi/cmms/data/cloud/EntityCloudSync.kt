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
        const val CAPA = "capa_actions"
        const val WO_OPERATIONS = "work_order_operations"
        const val WO_CONFIRMATIONS = "work_order_confirmations"
        const val WO_PHOTOS = "work_order_photos"
        const val WORK_PERMITS = "work_permits"
        const val PM_CHECKLIST = "pm_checklist_items"
        const val MEASURING_POINTS = "measuring_points"
        const val MEASUREMENT_READINGS = "measurement_readings"
        const val WO_MATERIALS = "work_order_materials"
        const val TASK_LISTS = "task_lists"
        const val TASK_LIST_OPERATIONS = "task_list_operations"
        const val ASSET_DOCUMENTS = "asset_documents"
        const val ASSET_CHARACTERISTICS = "asset_characteristics"
        const val ASSET_BOM_HEADERS = "asset_bom_headers"
        const val ASSET_BOM_ITEMS = "asset_bom_items"
        const val ASSET_MOVEMENTS = "asset_movements"
        const val INVENTORY_TRANSACTIONS = "inventory_transactions"
        const val SERIAL_PROFILES = "serial_number_profiles"
        const val SERIAL_NUMBERS = "serial_numbers"
        const val SERIAL_MOVEMENTS = "serial_number_movements"
        const val ASSET_INSTALLATIONS = "asset_installations"
        const val ASSET_STATUS_HISTORY = "asset_status_history"
        const val WORK_ORDER_HISTORY = "work_order_history"
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

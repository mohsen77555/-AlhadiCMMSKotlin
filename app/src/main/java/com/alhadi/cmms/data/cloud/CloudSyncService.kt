package com.alhadi.cmms.data.cloud

import com.alhadi.cmms.data.AppDatabase
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.KSerializer

/**
 * Pull side of the offline-first sync: listens to Firestore collections and writes incoming
 * documents into the local Room database. No-op when Firebase isn't configured. Writes go straight
 * to the DAOs (never the repository CRUD), so they don't echo back to the cloud.
 *
 * Users are intentionally NOT pulled: their password hash is never uploaded, so pulling would
 * overwrite the local hash. Users remain push-only (for cloud visibility).
 */
class CloudSyncService(
    private val database: AppDatabase,
    private val scope: CoroutineScope
) {
    private val registrations = mutableListOf<ListenerRegistration>()

    fun start() {
        if (!FirebaseGateway.isAvailable() || registrations.isNotEmpty()) return
        val db = FirebaseFirestore.getInstance()

        registrations += listen(db, EntityCloudSync.Collections.ASSETS, AssetEntity.serializer(),
            upsert = { database.assetDao().insertAsset(it) },
            deleteById = { database.assetDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.WORK_ORDERS, WorkOrderEntity.serializer(),
            upsert = { database.workOrderDao().insertWorkOrder(it) },
            deleteById = { database.workOrderDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.NOTIFICATIONS, MaintenanceNotificationEntity.serializer(),
            upsert = { database.maintenanceNotificationDao().insert(it) },
            deleteById = { database.maintenanceNotificationDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.SPARE_PARTS, SparePartEntity.serializer(),
            upsert = { database.sparePartDao().insert(it) },
            deleteById = { database.sparePartDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.SUPPLIERS, SupplierEntity.serializer(),
            upsert = { database.supplierDao().insert(it) },
            deleteById = { database.supplierDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.PURCHASE_ORDERS, PurchaseOrderEntity.serializer(),
            upsert = { database.purchaseOrderDao().insert(it) },
            deleteById = { database.purchaseOrderDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.PURCHASE_ORDER_LINES, PurchaseOrderLineEntity.serializer(),
            upsert = { database.purchaseOrderLineDao().insert(it) },
            deleteById = { database.purchaseOrderLineDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.WAREHOUSES, WarehouseEntity.serializer(),
            upsert = { database.warehouseDao().insert(it) },
            deleteById = { database.warehouseDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.FUNCTIONAL_LOCATIONS, FunctionalLocationEntity.serializer(),
            upsert = { database.functionalLocationDao().insert(it) },
            deleteById = { database.functionalLocationDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.ORG_UNITS, OrgUnitEntity.serializer(),
            upsert = { database.orgUnitDao().insert(it) },
            deleteById = { database.orgUnitDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.PREVENTIVE_MAINTENANCE, PreventiveMaintenanceEntity.serializer(),
            upsert = { database.preventiveMaintenanceDao().insert(it) },
            deleteById = { database.preventiveMaintenanceDao().deleteById(it) },
            idOf = { it.id })
    }

    fun stop() {
        registrations.forEach { it.remove() }
        registrations.clear()
    }

    private fun <T> listen(
        db: FirebaseFirestore,
        collection: String,
        serializer: KSerializer<T>,
        upsert: suspend (T) -> Unit,
        deleteById: suspend (Long) -> Unit,
        idOf: (T) -> Long
    ): ListenerRegistration =
        db.collection(collection).addSnapshotListener { snapshot, error ->
            if (error != null || snapshot == null) return@addSnapshotListener
            // Skip purely local echoes that haven't reached the server yet.
            if (snapshot.metadata.hasPendingWrites()) return@addSnapshotListener
            val changes = snapshot.documentChanges
            scope.launch(Dispatchers.IO) {
                for (change in changes) {
                    val entity = runCatching { CloudCodec.fromMap(serializer, change.document.data) }.getOrNull()
                        ?: continue
                    runCatching {
                        if (change.type == DocumentChange.Type.REMOVED) deleteById(idOf(entity)) else upsert(entity)
                    }
                }
            }
        }
}

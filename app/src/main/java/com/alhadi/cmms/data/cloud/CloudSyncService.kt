package com.alhadi.cmms.data.cloud

import com.alhadi.cmms.data.AppDatabase
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetInstallationEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.AssetStatusHistoryEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderHistoryEntity
import com.alhadi.cmms.data.entity.WorkOrderMaterialEntity
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

        registrations += listen(db, EntityCloudSync.Collections.CAPA, CapaEntity.serializer(),
            upsert = { database.capaDao().insert(it) },
            deleteById = { database.capaDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.WO_OPERATIONS, WorkOrderOperationEntity.serializer(),
            upsert = { database.workOrderOperationDao().insert(it) },
            deleteById = { database.workOrderOperationDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.WO_CONFIRMATIONS, WorkOrderConfirmationEntity.serializer(),
            upsert = { database.workOrderConfirmationDao().insert(it) },
            deleteById = { database.workOrderConfirmationDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.WO_PHOTOS, WorkOrderPhotoEntity.serializer(),
            upsert = { database.workOrderPhotoDao().insert(it) },
            deleteById = { database.workOrderPhotoDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.WORK_PERMITS, WorkPermitEntity.serializer(),
            upsert = { database.workPermitDao().insert(it) },
            deleteById = { database.workPermitDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.PM_CHECKLIST, PmChecklistItemEntity.serializer(),
            upsert = { database.pmChecklistDao().insert(it) },
            deleteById = { database.pmChecklistDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.MEASURING_POINTS, MeasuringPointEntity.serializer(),
            upsert = { database.measurementDao().insertPoint(it) },
            deleteById = { database.measurementDao().deletePointById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.MEASUREMENT_READINGS, MeasurementReadingEntity.serializer(),
            upsert = { database.measurementDao().insertReading(it) },
            deleteById = { /* readings are append-only locally */ },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.ASSET_DOCUMENTS, AssetDocumentEntity.serializer(),
            upsert = { database.assetDocumentDao().insert(it) },
            deleteById = { database.assetDocumentDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.ASSET_CHARACTERISTICS, AssetCharacteristicEntity.serializer(),
            upsert = { database.assetCharacteristicDao().insert(it) },
            deleteById = { database.assetCharacteristicDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.ASSET_BOM_HEADERS, AssetBomHeaderEntity.serializer(),
            upsert = { database.assetBomHeaderDao().insert(it) },
            deleteById = { database.assetBomHeaderDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.ASSET_BOM_ITEMS, AssetBomItemEntity.serializer(),
            upsert = { database.assetBomDao().insert(it) },
            deleteById = { database.assetBomDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.ASSET_MOVEMENTS, AssetMovementEntity.serializer(),
            upsert = { database.assetMovementDao().insert(it) },
            deleteById = { database.assetMovementDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.TASK_LISTS, TaskListEntity.serializer(),
            upsert = { database.taskListDao().insertTaskList(it) },
            deleteById = { database.taskListDao().deleteTaskListById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.TASK_LIST_OPERATIONS, TaskListOperationEntity.serializer(),
            upsert = { database.taskListDao().insertOperation(it) },
            deleteById = { database.taskListDao().deleteOperationById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.WO_MATERIALS, WorkOrderMaterialEntity.serializer(),
            upsert = { database.workOrderMaterialDao().insert(it) },
            deleteById = { database.workOrderMaterialDao().deleteById(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.INVENTORY_TRANSACTIONS, InventoryTransactionEntity.serializer(),
            upsert = { database.inventoryTransactionDao().insert(it) },
            deleteById = { /* inventory transactions are append-only locally */ },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.SERIAL_PROFILES, SerialNumberProfileEntity.serializer(),
            upsert = { database.serialNumberDao().upsertProfile(it) },
            deleteById = { database.serialNumberDao().deleteProfile(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.SERIAL_NUMBERS, SerialNumberEntity.serializer(),
            upsert = { database.serialNumberDao().upsertSerial(it) },
            deleteById = { database.serialNumberDao().deleteSerial(it) },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.SERIAL_MOVEMENTS, SerialNumberMovementEntity.serializer(),
            upsert = { database.serialNumberDao().upsertMovement(it) },
            deleteById = { /* serial movements are append-only locally */ },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.ASSET_INSTALLATIONS, AssetInstallationEntity.serializer(),
            upsert = { database.assetInstallationDao().insert(it) },
            deleteById = { /* installation log is append-only locally */ },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.ASSET_STATUS_HISTORY, AssetStatusHistoryEntity.serializer(),
            upsert = { database.assetStatusHistoryDao().insert(it) },
            deleteById = { /* status history is append-only locally */ },
            idOf = { it.id })

        registrations += listen(db, EntityCloudSync.Collections.WORK_ORDER_HISTORY, WorkOrderHistoryEntity.serializer(),
            upsert = { database.workOrderHistoryDao().insert(it) },
            deleteById = { /* work-order history is append-only locally */ },
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

package com.alhadi.cmms.data

import androidx.room.withTransaction
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.WorkOrderHistoryEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import com.alhadi.cmms.data.entity.AssetInstallationEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class CmmsRepository(internal val database: AppDatabase) {
    internal val assetDao = database.assetDao()
    internal val workOrderDao = database.workOrderDao()
    internal val pmDao = database.preventiveMaintenanceDao()
    internal val sparePartDao = database.sparePartDao()
    internal val serialDao = database.serialNumberDao()
    internal val transactionDao = database.inventoryTransactionDao()
    internal val userDao = database.userDao()
    internal val auditLogDao = database.auditLogDao()
    internal val measurementDao = database.measurementDao()
    internal val locationDao = database.functionalLocationDao()
    internal val capaDao = database.capaDao()
    internal val documentDao = database.assetDocumentDao()
    internal val characteristicDao = database.assetCharacteristicDao()
    internal val bomHeaderDao = database.assetBomHeaderDao()
    internal val bomDao = database.assetBomDao()
    internal val movementDao = database.assetMovementDao()
    internal val checklistDao = database.pmChecklistDao()
    internal val notificationDao = database.maintenanceNotificationDao()
    internal val operationDao = database.workOrderOperationDao()
    internal val confirmationDao = database.workOrderConfirmationDao()
    internal val photoDao = database.workOrderPhotoDao()
    internal val taskListDao = database.taskListDao()
    internal val permitDao = database.workPermitDao()
    internal val warehouseDao = database.warehouseDao()
    internal val orgUnitDao = database.orgUnitDao()
    internal val workOrderHistoryDao = database.workOrderHistoryDao()
    internal val supplierDao = database.supplierDao()
    internal val purchaseOrderDao = database.purchaseOrderDao()
    internal val purchaseOrderLineDao = database.purchaseOrderLineDao()
    internal val assetInstallationDao = database.assetInstallationDao()
    internal val serialService = SerialNumberService(database, ::recordAudit)

    val assets: Flow<List<AssetEntity>> = assetDao.observeAssets()
    val workOrders: Flow<List<WorkOrderEntity>> = workOrderDao.observeWorkOrders()
    val preventiveMaintenance: Flow<List<PreventiveMaintenanceEntity>> = pmDao.observePreventiveMaintenance()
    val serialNumberProfiles: Flow<List<SerialNumberProfileEntity>> = serialService.profiles
    val serialNumbers: Flow<List<SerialNumberEntity>> = serialService.serialNumbers
    val serialNumberMovements: Flow<List<SerialNumberMovementEntity>> = serialService.movements
    val spareParts: Flow<List<SparePartEntity>> = sparePartDao.observeSpareParts()
    val recentTransactions: Flow<List<InventoryTransactionEntity>> = transactionDao.observeRecentTransactions()
    val users: Flow<List<UserEntity>> = userDao.observeUsers()
    val currentUser: Flow<UserEntity?> = userDao.observeCurrentUser()
    val auditLog: Flow<List<AuditLogEntity>> = auditLogDao.observeRecent()
    val measuringPoints: Flow<List<MeasuringPointEntity>> = measurementDao.observePoints()
    val readings: Flow<List<MeasurementReadingEntity>> = measurementDao.observeReadings()
    val functionalLocations: Flow<List<FunctionalLocationEntity>> = locationDao.observeLocations()
    val warehouses: Flow<List<WarehouseEntity>> = warehouseDao.observeWarehouses()
    val suppliers: Flow<List<SupplierEntity>> = supplierDao.observeSuppliers()
    val purchaseOrders: Flow<List<PurchaseOrderEntity>> = purchaseOrderDao.observeAll()
    val purchaseOrderLines: Flow<List<PurchaseOrderLineEntity>> = purchaseOrderLineDao.observeAll()
    val assetInstallations: Flow<List<AssetInstallationEntity>> = assetInstallationDao.observeAll()
    val orgUnits: Flow<List<OrgUnitEntity>> = orgUnitDao.observeOrgUnits()
    val capaActions: Flow<List<CapaEntity>> = capaDao.observeCapa()
    val assetDocuments: Flow<List<AssetDocumentEntity>> = documentDao.observeDocuments()
    val assetCharacteristics: Flow<List<AssetCharacteristicEntity>> = characteristicDao.observeCharacteristics()
    val assetBomHeaders: Flow<List<AssetBomHeaderEntity>> = bomHeaderDao.observeHeaders()
    val assetBom: Flow<List<AssetBomItemEntity>> = bomDao.observeBom()
    val assetMovements: Flow<List<AssetMovementEntity>> = movementDao.observeMovements()
    val pmChecklist: Flow<List<PmChecklistItemEntity>> = checklistDao.observeItems()
    val notifications: Flow<List<MaintenanceNotificationEntity>> = notificationDao.observeNotifications()
    val workOrderOperations: Flow<List<WorkOrderOperationEntity>> = operationDao.observeOperations()
    val workOrderConfirmations: Flow<List<WorkOrderConfirmationEntity>> = confirmationDao.observeConfirmations()
    val workOrderPhotos: Flow<List<WorkOrderPhotoEntity>> = photoDao.observePhotos()
    val workOrderHistory: Flow<List<WorkOrderHistoryEntity>> = workOrderHistoryDao.observeAll()
    val taskLists: Flow<List<TaskListEntity>> = taskListDao.observeTaskLists()
    val taskListOperations: Flow<List<TaskListOperationEntity>> = taskListDao.observeTaskListOperations()
    val workPermits: Flow<List<WorkPermitEntity>> = permitDao.observePermits()

    fun observeOpenCapaCount(): Flow<Int> = capaDao.observeOpenCount()

    fun observeAssetCount(): Flow<Int> = assetDao.observeAssetCount()
    fun observeOpenWorkOrderCount(): Flow<Int> = workOrderDao.observeOpenCount()
    fun observeDuePmCount(today: String): Flow<Int> = pmDao.observeDueCount(today)
    fun observeLowStockCount(): Flow<Int> = sparePartDao.observeLowStockCount()
    fun observeUserById(id: Long): Flow<UserEntity?> = userDao.observeUserById(id)

    // ---------------------------------------------------------------------
    // Authentication / governance
    // ---------------------------------------------------------------------

    suspend fun authenticate(username: String, password: String): UserEntity? {
        val user = userDao.findActiveByUsername(username.trim()) ?: return null
        if (!PasswordHasher.verify(password, user.password)) return null
        // Transparently upgrade any legacy plain-text password to a salted hash on first login.
        if (!PasswordHasher.isHashed(user.password)) {
            userDao.insert(user.copy(password = PasswordHasher.hash(password)))
        }
        recordAudit("Login", "User", "تسجيل دخول ناجح", user.name)
        return user
    }

    internal suspend fun recordAudit(action: String, entityType: String, details: String, actor: String) {
        auditLogDao.insert(
            AuditLogEntity(
                action = action,
                entityType = entityType,
                details = details,
                performedBy = actor,
                createdAt = DateStrings.now()
            )
        )
    }

    /** WO-HIS-001..004: append an immutable history row for a work-order field change. */
    internal suspend fun recordWoHistory(orderId: Long, field: String, oldValue: String, newValue: String, actor: String) {
        if (oldValue == newValue) return
        workOrderHistoryDao.insert(
            WorkOrderHistoryEntity(
                orderId = orderId,
                field = field,
                oldValue = oldValue,
                newValue = newValue,
                actor = actor,
                changedAt = DateStrings.now()
            )
        )
    }

    /** All inventory transactions linked to a specific work order (for PDF / traceability). */
    suspend fun transactionsForOrder(orderId: Long): List<InventoryTransactionEntity> =
        transactionDao.dumpAll().filter { it.workOrderId == orderId }

    // ---------------------------------------------------------------------
    // Backup & restore (full database snapshot as portable JSON)
    // ---------------------------------------------------------------------

    internal val backupJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }


    internal suspend fun restoreBomData(
        headers: List<AssetBomHeaderEntity>,
        items: List<AssetBomItemEntity>
    ) {
        val restoredHeaderIds = headers.mapTo(mutableSetOf()) { it.id }
        if (headers.isNotEmpty()) bomHeaderDao.insertAll(headers)

        val assigned = items.filter { it.headerId != 0L && it.headerId in restoredHeaderIds }
        if (assigned.isNotEmpty()) bomDao.insertAll(assigned)

        val legacy = items.filterNot { it in assigned }
        legacy.groupBy { it.assetId }.forEach { (assetId, lines) ->
            val headerId = bomHeaderDao.insert(
                AssetBomHeaderEntity(
                    assetId = assetId.takeIf { it != 0L },
                    code = "RESTORED-$assetId",
                    name = "قائمة مكونات مستعادة",
                    category = "Asset",
                    usage = "Maintenance",
                    alternative = "01",
                    status = "Active",
                    revision = "A",
                    assignmentType = "Direct"
                )
            )
            bomDao.insertAll(
                lines.sortedBy { it.id }.mapIndexed { index, item ->
                    item.copy(headerId = headerId, itemNumber = item.itemNumber.takeIf { it > 0 } ?: (index + 1) * 10)
                }
            )
        }
    }

    /** Serializes every table into a portable JSON backup string. */
    suspend fun exportBackup(): String {
        val bundle = BackupBundle(
            appDbVersion = 27,
            exportedAt = DateStrings.now(),
            assets = assets.first(),
            workOrders = workOrders.first(),
            preventiveMaintenance = preventiveMaintenance.first(),
            serialNumberProfiles = serialDao.dumpProfiles(),
            serialNumbers = serialDao.dumpSerialNumbers(),
            serialNumberMovements = serialDao.dumpMovements(),
            spareParts = spareParts.first(),
            inventoryTransactions = transactionDao.dumpAll(),
            users = users.first(),
            auditLog = auditLogDao.dumpAll(),
            measuringPoints = measuringPoints.first(),
            measurementReadings = measurementDao.dumpAllReadings(),
            functionalLocations = functionalLocations.first(),
            warehouses = warehouseDao.dumpAll(),
            orgUnits = orgUnitDao.dumpAll(),
            capa = capaActions.first(),
            assetDocuments = assetDocuments.first(),
            assetCharacteristics = assetCharacteristics.first(),
            assetBomHeaders = assetBomHeaders.first(),
            assetBom = assetBom.first(),
            assetMovements = assetMovements.first(),
            pmChecklist = pmChecklist.first(),
            notifications = notifications.first(),
            operations = workOrderOperations.first(),
            confirmations = workOrderConfirmations.first(),
            photos = workOrderPhotos.first(),
            taskLists = taskLists.first(),
            taskListOperations = taskListOperations.first(),
            permits = workPermits.first()
        )
        return backupJson.encodeToString(BackupBundle.serializer(), bundle)
    }

    /**
     * Replaces ALL current data with the contents of a backup, atomically. Returns the parsed
     * bundle so the caller can show a restore summary. Throws if the JSON is not a valid backup.
     */
    suspend fun importBackup(content: String): BackupBundle {
        val bundle = backupJson.decodeFromString(BackupBundle.serializer(), content)
        database.withTransaction {
            // Clear everything first.
            auditLogDao.deleteAll()
            serialDao.deleteAllMovements()
            serialDao.deleteAllSerialNumbers()
            serialDao.deleteAllProfiles()
            permitDao.deleteAll()
            taskListDao.deleteAllOperations()
            taskListDao.deleteAllTaskLists()
            photoDao.deleteAll()
            confirmationDao.deleteAll()
            operationDao.deleteAll()
            notificationDao.deleteAll()
            checklistDao.deleteAll()
            movementDao.deleteAll()
            bomDao.deleteAll()
            bomHeaderDao.deleteAll()
            characteristicDao.deleteAll()
            documentDao.deleteAll()
            capaDao.deleteAll()
            warehouseDao.deleteAll()
            orgUnitDao.deleteAll()
            locationDao.deleteAll()
            measurementDao.deleteAllReadings()
            measurementDao.deleteAllPoints()
            transactionDao.deleteAll()
            pmDao.deleteAll()
            workOrderDao.deleteAll()
            sparePartDao.deleteAll()
            userDao.deleteAll()
            assetDao.deleteAll()

            // Restore (parents first; REPLACE semantics make ordering otherwise safe).
            assetDao.insertAssets(bundle.assets)
            userDao.insertAll(bundle.users)
            locationDao.insertAll(bundle.functionalLocations)
            warehouseDao.insertAll(bundle.warehouses)
            orgUnitDao.insertAll(bundle.orgUnits)
            if (bundle.serialNumberProfiles.isNotEmpty()) serialDao.insertProfiles(bundle.serialNumberProfiles)
            sparePartDao.insertAll(bundle.spareParts)
            workOrderDao.insertWorkOrders(bundle.workOrders)
            if (bundle.serialNumbers.isNotEmpty()) serialDao.insertSerials(bundle.serialNumbers)
            if (bundle.serialNumberMovements.isNotEmpty()) serialDao.insertMovements(bundle.serialNumberMovements)
            pmDao.insertAll(bundle.preventiveMaintenance)
            checklistDao.insertAll(bundle.pmChecklist)
            transactionDao.insertAll(bundle.inventoryTransactions)
            measurementDao.insertPoints(bundle.measuringPoints)
            measurementDao.insertReadings(bundle.measurementReadings)
            capaDao.insertAll(bundle.capa)
            documentDao.insertAll(bundle.assetDocuments)
            characteristicDao.insertAll(bundle.assetCharacteristics)
            restoreBomData(bundle.assetBomHeaders, bundle.assetBom)
            movementDao.insertAll(bundle.assetMovements)
            notificationDao.insertAll(bundle.notifications)
            operationDao.insertAll(bundle.operations)
            confirmationDao.insertAll(bundle.confirmations)
            photoDao.insertAll(bundle.photos)
            taskListDao.insertTaskLists(bundle.taskLists)
            taskListDao.insertOperations(bundle.taskListOperations)
            permitDao.insertAll(bundle.permits)

            recordAudit("Import", "Backup", "تم استعادة نسخة احتياطية (${bundle.totalRecords} سجل)", "System")
        }
        return bundle
    }

    internal fun validateLinearAssetMaster(asset: AssetEntity) {
        if (!asset.isLinearAsset) return
        require(asset.linearEndPoint > asset.linearStartPoint) {
            "يجب أن تكون نقطة نهاية الأصل الخطي أكبر من نقطة البداية"
        }
        require(asset.linearStartMarkerDistance >= 0.0 && asset.linearEndMarkerDistance >= 0.0) {
            "مسافات العلامات المرجعية لا يمكن أن تكون سالبة"
        }
        asset.linearStartLatitude?.let { require(it in -90.0..90.0) { "خط عرض البداية غير صالح" } }
        asset.linearEndLatitude?.let { require(it in -90.0..90.0) { "خط عرض النهاية غير صالح" } }
        asset.linearStartLongitude?.let { require(it in -180.0..180.0) { "خط طول البداية غير صالح" } }
        asset.linearEndLongitude?.let { require(it in -180.0..180.0) { "خط طول النهاية غير صالح" } }
    }

    internal suspend fun validateLinearMaintenanceReference(
        assetId: Long?,
        startPoint: Double?,
        endPoint: Double?,
        marker: String,
        horizontalOffset: Double?,
        verticalOffset: Double?
    ) {
        val hasReference = startPoint != null || endPoint != null || marker.isNotBlank() ||
            horizontalOffset != null || verticalOffset != null
        if (!hasReference) return

        val id = assetId ?: throw IllegalStateException("يجب تحديد أصل للموقع الخطي")
        val asset = assetDao.getAssetById(id) ?: throw IllegalStateException("الأصل المحدد غير موجود")
        if (!asset.isLinearAsset) throw IllegalStateException("الأصل المحدد غير مفعّل كأصل خطي")
        val start = startPoint ?: throw IllegalStateException("أدخل نقطة بداية الموقع الخطي")
        val end = endPoint ?: throw IllegalStateException("أدخل نقطة نهاية الموقع الخطي")
        if (!asset.containsLinearRange(start, end)) {
            throw IllegalStateException("الموقع الخطي خارج نطاق الأصل ${asset.linearStartPoint} – ${asset.linearEndPoint} ${asset.linearUnit}")
        }
    }

    internal fun frequencyDaysFor(text: String): Int = when {
        text.contains("يومي") -> 1
        text.contains("أسبوعي") -> 7
        text.contains("شهري") && !text.contains("نصف") && !text.contains("ربع") -> 30
        text.contains("ربع") -> 90
        text.contains("نصف") -> 180
        text.contains("سنوي") || text.contains("سنوى") -> 365
        else -> 30
    }

    internal fun sheetByKey(sheets: Map<String, List<List<String>>>, key: String): List<List<String>> =
        sheets.entries.firstOrNull { it.key.contains(key) }?.value ?: emptyList()

}

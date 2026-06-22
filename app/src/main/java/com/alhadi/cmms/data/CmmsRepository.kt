package com.alhadi.cmms.data

import androidx.room.withTransaction
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
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
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

class CmmsRepository(private val database: AppDatabase) {
    private val assetDao = database.assetDao()
    private val workOrderDao = database.workOrderDao()
    private val pmDao = database.preventiveMaintenanceDao()
    private val sparePartDao = database.sparePartDao()
    private val serialDao = database.serialNumberDao()
    private val transactionDao = database.inventoryTransactionDao()
    private val userDao = database.userDao()
    private val auditLogDao = database.auditLogDao()
    private val measurementDao = database.measurementDao()
    private val locationDao = database.functionalLocationDao()
    private val capaDao = database.capaDao()
    private val documentDao = database.assetDocumentDao()
    private val characteristicDao = database.assetCharacteristicDao()
    private val bomHeaderDao = database.assetBomHeaderDao()
    private val bomDao = database.assetBomDao()
    private val movementDao = database.assetMovementDao()
    private val checklistDao = database.pmChecklistDao()
    private val notificationDao = database.maintenanceNotificationDao()
    private val operationDao = database.workOrderOperationDao()
    private val confirmationDao = database.workOrderConfirmationDao()
    private val photoDao = database.workOrderPhotoDao()
    private val taskListDao = database.taskListDao()
    private val permitDao = database.workPermitDao()
    private val warehouseDao = database.warehouseDao()
    private val serialService = SerialNumberService(database, ::recordAudit)

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

    private suspend fun recordAudit(action: String, entityType: String, details: String, actor: String) {
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

    /** All inventory transactions linked to a specific work order (for PDF / traceability). */
    suspend fun transactionsForOrder(orderId: Long): List<InventoryTransactionEntity> =
        transactionDao.dumpAll().filter { it.workOrderId == orderId }

    // ---------------------------------------------------------------------
    // Backup & restore (full database snapshot as portable JSON)
    // ---------------------------------------------------------------------

    private val backupJson = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
    }


    private suspend fun restoreBomData(
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

    // ---------------------------------------------------------------------
    // Sample data
    // ---------------------------------------------------------------------

    suspend fun seedSampleData(replace: Boolean = false) {
        database.withTransaction {
            if (replace) {
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
                locationDao.deleteAll()
                measurementDao.deleteAllReadings()
                measurementDao.deleteAllPoints()
                transactionDao.deleteAll()
                pmDao.deleteAll()
                workOrderDao.deleteAll()
                sparePartDao.deleteAll()
                userDao.deleteAll()
                assetDao.deleteAll()
            }

            if (!replace && assetDao.countOnce() > 0) return@withTransaction

            val today = DateStrings.today()
            val assets = listOf(
                AssetEntity(1, "BE-101", "Bucket Elevator 1", "Bucket Elevators", "Silo Area", "Buhler", "BE-18", "Running", "High", "2020-02-15", DateStrings.daysFromToday(-10)),
                AssetEntity(2, "CC-205", "Chain Conveyor 205", "Chain Conveyors", "Raw Wheat Intake", "Mysilo", "CC-250", "Running", "Medium", "2019-08-20", DateStrings.daysFromToday(-18)),
                AssetEntity(3, "SC-077", "Screw Conveyor 77", "Screw Conveyors", "Bran Line", "Ocrim", "SC-220", "Running", "Medium", "2021-05-03", DateStrings.daysFromToday(-5)),
                AssetEntity(4, "SF-030", "Silo Fan 30", "Silo Fans", "Silo Roof", "WAM", "AX-45", "Stopped", "High", "2018-11-11", DateStrings.daysFromToday(-40)),
                AssetEntity(5, "SG-014", "Silo Gate 14", "Silo Gates", "Silo Bottom", "Custom", "Slide-400", "Running", "Medium", "2022-01-18", DateStrings.daysFromToday(-12), parentAssetId = 1),
                AssetEntity(6, "SN-400", "Level Sensor 400", "Sensors", "Flour Bin", "Siemens", "SITRANS", "Warning", "High", "2023-03-21", DateStrings.daysFromToday(-2), warrantyProvider = "Siemens", warrantyStart = "2023-03-21", warrantyEnd = DateStrings.daysFromToday(60), parentAssetId = 4),
                AssetEntity(7, "RM-01", "Rollermill C1", "Rollermills", "Milling Floor", "Buhler", "Antares", "Running", "Critical", "2017-09-10", DateStrings.daysFromToday(-7), serialNumber = "BUH-ANT-2017-0091", assetTag = "TAG-RM01", supplier = "Buhler AG", purchaseOrder = "PO-2017-114", purchaseCost = 285000.0, acquiredAt = "2017-08-01"),
                AssetEntity(8, "PL-04", "Plansifter 4", "Plansifters", "Milling Floor", "Buhler", "MPA", "Running", "Critical", "2017-10-02", DateStrings.daysFromToday(-21)),
                AssetEntity(9, "PR-02", "Purifier 2", "Purifiers", "Milling Floor", "Golfetto", "Pur-2", "Running", "High", "2018-04-19", DateStrings.daysFromToday(-16)),
                AssetEntity(10, "CP-01", "Air Compressor 1", "Compressors", "Utility Room", "Atlas Copco", "GA-37", "Running", "Critical", "2016-12-12", DateStrings.daysFromToday(-8), serialNumber = "AC-GA37-2016-0455", assetTag = "TAG-CP01", supplier = "Atlas Copco", purchaseOrder = "PO-2016-088", purchaseCost = 96000.0, acquiredAt = "2016-11-20"),
                AssetEntity(11, "PK-01", "Packing Machine 1", "Packing Machines", "Packing Hall", "Fawema", "FA-10", "Warning", "High", "2020-07-07", DateStrings.daysFromToday(-31), warrantyProvider = "Fawema", warrantyStart = "2020-07-07", warrantyEnd = DateStrings.daysFromToday(15)),
                AssetEntity(12, "SCAL-01", "Truck Scale 1", "Scales", "Gate", "Mettler Toledo", "TS-60", "Running", "Medium", "2021-02-09", DateStrings.daysFromToday(-14))
            )

            val users = listOf(
                UserEntity(1, "Mohsen Alhadi", "mohsen", "Admin", true, "admin"),
                UserEntity(2, "Maintenance Supervisor", "supervisor", "Supervisor", true, "1234"),
                UserEntity(3, "Electrical Technician", "electrical", "Technician", true, "1234"),
                UserEntity(4, "Mechanical Technician", "mechanical", "Technician", true, "1234")
            )

            val workOrders = listOf(
                WorkOrderEntity(1, 7, "Check rollermill vibration", "Inspect bearings, belts, and abnormal noise on RM-01.", "High", "Open", "Mechanical Technician", today, DateStrings.daysFromToday(1), 120.0),
                WorkOrderEntity(2, 4, "Silo fan not starting", "Check overload, contactor, motor insulation, and fan impeller.", "High", "In Progress", "Electrical Technician", DateStrings.daysFromToday(-1), today, 250.0, isFailure = true, downtimeHours = 6.0, requiresPermit = true),
                WorkOrderEntity(3, 11, "Packing machine bag sensor alarm", "Clean and align sensor, verify signal in control panel.", "Medium", "Open", "Electrical Technician", today, DateStrings.daysFromToday(2), 1500.0, approvalStatus = "Pending"),
                WorkOrderEntity(4, 2, "Chain conveyor lubrication", "Lubricate chain and inspect tension.", "Low", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-7), DateStrings.daysFromToday(-5), 30.0, "Completed and tested."),
                WorkOrderEntity(5, 4, "Silo fan bearing failure", "Replaced damaged fan bearing.", "High", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-45), DateStrings.daysFromToday(-44), 180.0, "Bearing replaced.", isFailure = true, downtimeHours = 8.0, laborHours = 6.0, laborRate = 25.0, partsCost = 90.0),
                WorkOrderEntity(6, 4, "Silo fan motor overheat", "Cleaned and re-greased motor, fixed ventilation.", "High", "Closed", "Electrical Technician", DateStrings.daysFromToday(-90), DateStrings.daysFromToday(-89), 120.0, "Motor serviced.", isFailure = true, downtimeHours = 5.0),
                WorkOrderEntity(7, 7, "Rollermill jam", "Cleared product jam and inspected rolls.", "Critical", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-30), DateStrings.daysFromToday(-30), 90.0, "Cleared.", isFailure = true, downtimeHours = 3.5),
                WorkOrderEntity(8, 7, "Rollermill belt break", "Replaced broken drive belt.", "Critical", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-75), DateStrings.daysFromToday(-75), 150.0, "Belt replaced.", isFailure = true, downtimeHours = 4.0, laborHours = 3.0, laborRate = 25.0, partsCost = 70.0)
            )

            val preventiveMaintenance = listOf(
                PreventiveMaintenanceEntity(1, 1, "Inspect buckets and belt tension", 30, DateStrings.daysFromToday(-35), DateStrings.daysFromToday(-5), "Due", 90),
                PreventiveMaintenanceEntity(2, 7, "Rollermill weekly inspection", 7, DateStrings.daysFromToday(-6), DateStrings.daysFromToday(1), "Scheduled", 45, taskListId = 1),
                PreventiveMaintenanceEntity(3, 8, "Plansifter sieve and frame inspection", 14, DateStrings.daysFromToday(-18), DateStrings.daysFromToday(-4), "Due", 60),
                PreventiveMaintenanceEntity(4, 10, "Compressor oil and air filter check", 30, DateStrings.daysFromToday(-22), DateStrings.daysFromToday(8), "Scheduled", 50),
                PreventiveMaintenanceEntity(5, 12, "Scale calibration verification", 90, DateStrings.daysFromToday(-70), DateStrings.daysFromToday(20), "Scheduled", 120)
            )

            val serialProfiles = listOf(
                SerialNumberProfileEntity(
                    id = 1,
                    code = "S001",
                    name = "تتبع فردي قياسي",
                    requireOnReceipt = true,
                    requireOnIssue = true,
                    autoCreate = true,
                    equipmentRequired = false,
                    stockCheckMode = "Block",
                    allowManualStockEdit = true,
                    description = "تتبع كامل للاستلام والصرف والموقع"
                )
            )

            val spareParts = listOf(
                SparePartEntity(1, "BRG-6205", "Bearing 6205 ZZ", "Rollermills", "pcs", 12, 6, "Store A-01", 4.5),
                SparePartEntity(2, "BELT-A45", "V-Belt A45", "Bucket Elevators", "pcs", 3, 5, "Store B-03", 8.0),
                SparePartEntity(3, "CHAIN-16B", "Chain 16B", "Chain Conveyors", "meter", 18, 10, "Store C-01", 12.5),
                SparePartEntity(4, "SENSOR-PNP", "PNP Proximity Sensor", "Sensors", "pcs", 2, 4, "Electrical Cabinet", 18.0, serializationActive = true, serialProfileId = 1),
                SparePartEntity(5, "FILTER-GA37", "Compressor Air Filter", "Compressors", "pcs", 7, 3, "Utility Store", 25.0),
                SparePartEntity(6, "BAG-NEEDLE", "Packing Stitching Needle", "Packing Machines", "pcs", 40, 20, "Packing Store", 0.8)
            )

            val sampleSerials = listOf(
                SerialNumberEntity(1, 4, "PNP-0001", 1, status = "InStock", stockType = "Unrestricted", plant = "FAC-01", storageLocation = "Electrical Cabinet", createdAt = today, lastMovementAt = today),
                SerialNumberEntity(2, 4, "PNP-0002", 1, status = "InStock", stockType = "Unrestricted", plant = "FAC-01", storageLocation = "Electrical Cabinet", createdAt = today, lastMovementAt = today)
            )

            val sampleSerialMovements = listOf(
                SerialNumberMovementEntity(1, 1, 4, movementType = "Receive", toStatus = "InStock", toPlant = "FAC-01", toStorageLocation = "Electrical Cabinet", toStockType = "Unrestricted", createdAt = today, createdBy = "System", note = "رصيد افتتاحي"),
                SerialNumberMovementEntity(2, 2, 4, movementType = "Receive", toStatus = "InStock", toPlant = "FAC-01", toStorageLocation = "Electrical Cabinet", toStockType = "Unrestricted", createdAt = today, createdBy = "System", note = "رصيد افتتاحي")
            )

            val transactions = listOf(
                InventoryTransactionEntity(1, 1, 1, "Issue", 2, DateStrings.daysFromToday(-2), "Mohsen Alhadi", "Issued for RM-01 vibration check."),
                InventoryTransactionEntity(2, 3, 4, "Issue", 1, DateStrings.daysFromToday(-5), "Maintenance Supervisor", "Issued for CC-205 lubrication."),
                InventoryTransactionEntity(3, 5, null, "Receive", 4, DateStrings.daysFromToday(-8), "Store Keeper", "Monthly purchase receipt.")
            )

            val measuringPoints = listOf(
                MeasuringPointEntity(1, 7, "Running Hours", "hr", true, 12000.0, 8450.0, today),
                MeasuringPointEntity(2, 7, "Bearing Vibration", "mm/s", false, 11.0, 6.4, today),
                MeasuringPointEntity(3, 10, "Compressor Hours", "hr", true, 20000.0, 15230.0, today),
                MeasuringPointEntity(4, 4, "Motor Temperature", "°C", false, 80.0, 72.0, today),
                MeasuringPointEntity(5, 1, "Belt Tension", "N", false, 500.0, 410.0, today)
            )

            val capaActions = listOf(
                CapaEntity(1, "CAPA-001", "تكرار اهتزاز مطحنة الأسطوانات", "Corrective", "تحليل السبب الجذري للاهتزاز المتكرر على RM-01 ومعالجته.", 7, "High", "In Progress", "Maintenance Supervisor", DateStrings.daysFromToday(2), DateStrings.daysFromToday(-3)),
                CapaEntity(2, "CAPA-002", "خطة وقائية لمروحة الصومعة", "Preventive", "وضع خطة فحص دوري لمنع تكرار توقف SF-030.", 4, "Medium", "Open", "Electrical Technician", DateStrings.daysFromToday(7), DateStrings.daysFromToday(-1)),
                CapaEntity(3, "CAPA-003", "مراجعة معايرة الميزان", "Corrective", "انحراف في قراءات الميزان يتطلب إجراء تصحيحي وتوثيق.", 12, "Low", "Closed", "Mohsen Alhadi", DateStrings.daysFromToday(-10), DateStrings.daysFromToday(-20))
            )

            val locations = listOf(
                FunctionalLocationEntity(1, "FAC-01", "Flour Mill Factory", null, "المصنع الرئيسي"),
                FunctionalLocationEntity(2, "SILO", "Silo Area", 1, "منطقة الصوامع"),
                FunctionalLocationEntity(3, "MILL", "Milling Floor", 1, "صالة الطحن"),
                FunctionalLocationEntity(4, "PACK", "Packing Hall", 1, "صالة التعبئة"),
                FunctionalLocationEntity(5, "UTIL", "Utility Room", 1, "غرفة المرافق"),
                FunctionalLocationEntity(6, "MILL-RM", "Rollermill Station", 3, "محطة مطاحن الأسطوانات")
            )

            assetDao.insertAssets(assets)
            userDao.insertAll(users.map { it.copy(password = PasswordHasher.hash(it.password)) })
            workOrderDao.insertWorkOrders(workOrders)
            pmDao.insertAll(preventiveMaintenance)
            serialDao.insertProfiles(serialProfiles)
            sparePartDao.insertAll(spareParts)
            serialDao.insertSerials(sampleSerials)
            serialDao.insertMovements(sampleSerialMovements)
            transactionDao.insertAll(transactions)
            val documents = listOf(
                AssetDocumentEntity(1, 7, "Manual", "دليل تشغيل Rollermill", "https://example.com/rm01-manual.pdf", "Mohsen Alhadi", today),
                AssetDocumentEntity(2, 7, "Drawing", "مخطط كهربائي RM-01", "internal://drawings/rm01.dwg", "Maintenance Supervisor", today),
                AssetDocumentEntity(3, 10, "Certificate", "شهادة فحص الضاغط", "https://example.com/cp01-cert.pdf", "Mohsen Alhadi", today)
            )

            val characteristics = listOf(
                AssetCharacteristicEntity(1, 7, "Capacity", "6", "rolls"),
                AssetCharacteristicEntity(2, 7, "Motor Power", "37", "kW"),
                AssetCharacteristicEntity(3, 10, "Working Pressure", "8", "bar"),
                AssetCharacteristicEntity(4, 10, "Air Flow", "6.5", "m3/min"),
                AssetCharacteristicEntity(5, 4, "Voltage", "400", "V")
            )

            val bomHeaders = listOf(
                AssetBomHeaderEntity(1, 7, "RM-01-MAIN", "مكونات صيانة المطحنة", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", "قطع الغيار والتجميعات الرئيسية"),
                AssetBomHeaderEntity(2, 1, "BE-101-MAIN", "مكونات المصعد", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", ""),
                AssetBomHeaderEntity(3, 2, "CC-205-MAIN", "مكونات ناقل السلسلة", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", ""),
                AssetBomHeaderEntity(4, 10, "CP-01-MAIN", "مكونات الضاغط", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", ""),
                AssetBomHeaderEntity(5, 4, "SF-030-MAIN", "مكونات مروحة الصومعة", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", "هيكل متعدد المستويات")
            )

            val bomItems = listOf(
                AssetBomItemEntity(id = 1, assetId = 7, partId = 1, quantity = 4, headerId = 1, itemNumber = 10, itemCategory = "Stock", isCritical = true, notes = "محامل التشغيل الرئيسية"),
                AssetBomItemEntity(id = 2, assetId = 1, partId = 2, quantity = 2, headerId = 2, itemNumber = 10, itemCategory = "Stock", isCritical = true),
                AssetBomItemEntity(id = 3, assetId = 2, partId = 3, quantity = 6, headerId = 3, itemNumber = 10, itemCategory = "Stock"),
                AssetBomItemEntity(id = 4, assetId = 10, partId = 5, quantity = 1, headerId = 4, itemNumber = 10, itemCategory = "Stock"),
                AssetBomItemEntity(id = 5, assetId = 4, partId = 0, quantity = 1, headerId = 5, itemNumber = 10, itemCategory = "Assembly", useInOrders = false, assemblyAssetId = 6, notes = "تجميعة الاستشعار والتحكم"),
                AssetBomItemEntity(id = 6, assetId = 4, partId = 4, quantity = 1, headerId = 5, itemNumber = 20, itemCategory = "Stock", parentItemId = 5, isCritical = true)
            )

            val movements = listOf(
                AssetMovementEntity(1, 7, MovementType.INSTALL, null, 6, "", "Rollermill Station", "تركيب أولي في الموقع", "Mohsen Alhadi", today),
                AssetMovementEntity(2, 10, MovementType.INSTALL, null, 5, "", "Utility Room", "تركيب الضاغط", "Mohsen Alhadi", today),
                AssetMovementEntity(3, 4, MovementType.TRANSFER, 5, 3, "Utility Room", "Milling Floor", "نقل اللوحة الكهربائية", "Maintenance Supervisor", today)
            )

            val checklist = listOf(
                PmChecklistItemEntity(1, 2, "فحص اهتزاز وضجيج الأسطوانات", "", "", 1),
                PmChecklistItemEntity(2, 2, "فحص شدّ السيور", "", "", 2),
                PmChecklistItemEntity(3, 2, "تشحيم المحامل", "", "", 3),
                PmChecklistItemEntity(4, 2, "تنظيف نقاط التغذية", "", "", 4),
                PmChecklistItemEntity(5, 4, "فحص مستوى الزيت", "", "", 1),
                PmChecklistItemEntity(6, 4, "تنظيف/تبديل فلتر الهواء", "", "", 2),
                PmChecklistItemEntity(7, 4, "فحص تسريبات الهواء", "", "", 3)
            )

            val notifications = listOf(
                MaintenanceNotificationEntity(1, "NTF-0001", "Breakdown", "اهتزاز غير طبيعي في المطحنة", "اهتزاز وضجيج عالٍ أثناء التشغيل على RM-01.", 7, "Critical", "Vibration", "Bearing wear", "Mohsen Alhadi", today, DateStrings.daysFromToday(1), "Approved", null),
                MaintenanceNotificationEntity(2, "NTF-0002", "Corrective", "تسريب هواء من الضاغط", "انخفاض ضغط ملحوظ في خط الهواء.", 10, "High", "Leak", "Seal failure", "Maintenance Supervisor", today, DateStrings.daysFromToday(2), "New", null),
                MaintenanceNotificationEntity(3, "NTF-0003", "Inspection", "ملاحظة أثناء جولة الفحص", "صوت خفيف من ناقل السلسلة، يُنصح بالفحص.", 2, "Medium", "Noise", "", "Electrical Technician", today, "", "Screened", null)
            )

            val operations = listOf(
                WorkOrderOperationEntity(1, 1, "0010", "عزل الطاقة وتأمين المعدة", "Mechanical", 0.5, 0.0, true, "Open"),
                WorkOrderOperationEntity(2, 1, "0020", "فحص المحامل والسيور", "Mechanical", 1.5, 0.0, true, "Open"),
                WorkOrderOperationEntity(3, 1, "0030", "إعادة التشغيل والاختبار", "Mechanical", 0.5, 0.0, true, "Open"),
                WorkOrderOperationEntity(4, 2, "0010", "فحص الحمل الزائد والكونتاكتور", "Electrical", 1.0, 1.0, true, "Confirmed"),
                WorkOrderOperationEntity(5, 2, "0020", "فحص عزل المحرك", "Electrical", 1.0, 0.0, true, "In Progress")
            )

            val confirmations = listOf(
                WorkOrderConfirmationEntity(1, 2, 4, "Electrical Technician", today, 1.0, "فحص الحمل الزائد واستبدال الكونتاكتور", "Contactor failure", "Electrical wear", "تم استبدال الكونتاكتور واختبار التشغيل", 2.0, true, today)
            )

            val taskLists = listOf(
                TaskListEntity(1, "فحص أسبوعي للمطاحن", "قالب فحص دوري لمطاحن الأسطوانات", "Mechanical")
            )
            val taskListOps = listOf(
                TaskListOperationEntity(1, 1, "0010", "تنظيف وتفقّد الأسطوانات", "Mechanical", 0.5),
                TaskListOperationEntity(2, 1, "0020", "فحص المحامل والتشحيم", "Mechanical", 1.0),
                TaskListOperationEntity(3, 1, "0030", "فحص شدّ السيور وضبطها", "Mechanical", 0.5)
            )

            val permits = listOf(
                WorkPermitEntity(1, 2, "LOTO", "طاقة كهربائية مخزّنة، أجزاء دوّارة", "قفازات عازلة، نظارة واقية", "Approved", "Maintenance Supervisor", DateStrings.daysFromToday(2), "Mohsen Alhadi", today)
            )

            val warehouses = listOf(
                WarehouseEntity(1, "WH-MAIN", "المستودع الرئيسي", "المبنى الإداري", "Mohsen Alhadi", "", "Main", "Active", "قطع الغيار الرئيسية"),
                WarehouseEntity(2, "WH-SPARE", "مستودع قطع الغيار", "صالة الطحن", "", "", "Spare", "Active", ""),
                WarehouseEntity(3, "WH-TOOLS", "مخزن العدد والأدوات", "ورشة الصيانة", "", "", "Tools", "Active", "")
            )

            measurementDao.insertPoints(measuringPoints)
            locationDao.insertAll(locations)
            warehouseDao.insertAll(warehouses)
            capaDao.insertAll(capaActions)
            documentDao.insertAll(documents)
            characteristicDao.insertAll(characteristics)
            bomHeaderDao.insertAll(bomHeaders)
            bomDao.insertAll(bomItems)
            movementDao.insertAll(movements)
            checklistDao.insertAll(checklist)
            notificationDao.insertAll(notifications)
            operationDao.insertAll(operations)
            confirmationDao.insertAll(confirmations)
            taskListDao.insertTaskLists(taskLists)
            taskListDao.insertOperations(taskListOps)
            permitDao.insertAll(permits)
            recordAudit("Seed", "System", "تم تجهيز البيانات التجريبية", "System")
        }
    }

    // ---------------------------------------------------------------------
    // Work orders
    // ---------------------------------------------------------------------

    suspend fun createWorkOrder(
        assetId: Long,
        title: String,
        description: String,
        priority: String,
        assignedTo: String,
        dueAt: String,
        estimatedCost: Double,
        actor: String
    ) {
        val now = DateStrings.today()
        workOrderDao.insertWorkOrder(
            WorkOrderEntity(
                assetId = assetId,
                title = title.ifBlank { "Maintenance request" },
                description = description,
                priority = priority,
                status = "Open",
                assignedTo = assignedTo,
                createdAt = now,
                dueAt = dueAt,
                estimatedCost = estimatedCost,
                approvalStatus = if (priority == "Critical" || estimatedCost >= WorkOrderEntity.APPROVAL_THRESHOLD) "Pending" else "NotRequired"
            )
        )
        recordAudit("Create", "WorkOrder", "إنشاء أمر عمل: $title", actor)
    }

    suspend fun createDemoWorkOrder(assetId: Long = 7, actor: String = "System") {
        createWorkOrder(
            assetId = assetId,
            title = "New inspection request",
            description = "Created from mobile app for quick maintenance follow-up.",
            priority = "Medium",
            assignedTo = "Maintenance Supervisor",
            dueAt = DateStrings.daysFromToday(3),
            estimatedCost = 0.0,
            actor = actor
        )
    }

    suspend fun updateWorkOrderStatus(id: Long, status: String, actor: String = "System") {
        // Governance: hazardous work cannot start without an approved, valid permit (SAFE-002).
        if (status == "In Progress" && workOrderDao.requiresPermit(id) == true &&
            permitDao.countValid(id, DateStrings.today()) == 0
        ) {
            throw IllegalStateException("يتطلّب تصريح عمل ساري المفعول قبل بدء التنفيذ")
        }
        // Governance: no technical completion before every required operation is confirmed (TC-001).
        if (status == "Technically Completed") {
            if (operationDao.countForOrder(id) == 0) {
                throw IllegalStateException("لا يمكن الإكمال الفني بدون عمليات على أمر العمل")
            }
            if (operationDao.countUnconfirmedRequired(id) > 0) {
                throw IllegalStateException("أكمل تأكيد كل العمليات المطلوبة قبل الإكمال الفني")
            }
        }
        // Governance: a work order cannot be closed without photo evidence of the work (EXE-006).
        if (status == "Closed" && photoDao.countForOrder(id) == 0) {
            throw IllegalStateException("التقط صورة دليل تنفيذ بالكاميرا قبل إغلاق أمر العمل")
        }
        workOrderDao.updateStatus(id, status)
        recordAudit("Update", "WorkOrder", "تحديث حالة أمر العمل #$id إلى $status", actor)
    }

    // ---------------------------------------------------------------------
    // Preventive maintenance
    // ---------------------------------------------------------------------

    suspend fun markPreventiveMaintenanceDone(item: PreventiveMaintenanceEntity, actor: String = "System") {
        val today = DateStrings.today()
        pmDao.markDone(
            id = item.id,
            status = "Scheduled",
            doneAt = today,
            nextDueAt = DateStrings.addDays(today, item.frequencyDays)
        )
        checklistDao.resetResults(item.id)
        recordAudit("Complete", "PreventiveMaintenance", "تنفيذ صيانة دورية: ${item.title}", actor)
    }


    // ---------------------------------------------------------------------
    // Serial number profiles, units, stock checks, and movement history
    // ---------------------------------------------------------------------

    suspend fun saveSerialProfile(profile: SerialNumberProfileEntity, actor: String = "System") =
        serialService.saveProfile(profile, actor)

    suspend fun deleteSerialProfile(profile: SerialNumberProfileEntity, actor: String = "System") =
        serialService.deleteProfile(profile, actor)

    suspend fun createSerialMaster(request: SerialMasterRequest, actor: String = "System") =
        serialService.createMaster(request, actor)

    suspend fun receiveSerializedPart(request: SerializedReceiptRequest, actor: String = "System") =
        serialService.receive(request, actor)

    suspend fun issueSerializedPart(request: SerializedIssueRequest, actor: String = "System") =
        serialService.issue(request, actor)

    suspend fun transferSerialNumber(request: SerialTransferRequest, actor: String = "System") =
        serialService.transfer(request, actor)

    suspend fun installSerialNumber(request: SerialInstallRequest, actor: String = "System") =
        serialService.install(request, actor)

    suspend fun dismantleSerialNumber(serialId: Long, note: String = "", actor: String = "System") =
        serialService.dismantle(serialId, note, actor)

    suspend fun reconcileSerializedStock(partId: Long, actor: String = "System") =
        serialService.reconcileStock(partId, actor)

    suspend fun deleteSerialNumber(serial: SerialNumberEntity, actor: String = "System") =
        serialService.deleteSerial(serial, actor)


    // ---------------------------------------------------------------------
    // Inventory
    // ---------------------------------------------------------------------

    suspend fun issuePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {
        require(quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
        val current = sparePartDao.getById(part.id) ?: throw IllegalStateException("قطعة الغيار غير موجودة")
        if (current.serializationActive) throw IllegalStateException("استخدم شاشة الأرقام التسلسلية لصرف هذه القطعة")
        database.withTransaction {
            if (sparePartDao.adjustStockSafe(current.id, -quantity) == 0) {
                throw IllegalStateException("الكمية المطلوبة ($quantity) أكبر من المتوفر (${current.onHandQty})")
            }
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = current.id,
                    workOrderId = null,
                    transactionType = "Issue",
                    quantity = quantity,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "صرف يدوي من شاشة المخزون"
                )
            )
            recordAudit("Issue", "Inventory", "صرف $quantity من ${current.partNumber}", actor)
        }
    }

    suspend fun issuePartToWorkOrder(order: WorkOrderEntity, part: SparePartEntity, quantity: Int, actor: String = "System") {
        require(quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
        val current = sparePartDao.getById(part.id) ?: throw IllegalStateException("قطعة الغيار غير موجودة")
        if (current.serializationActive) throw IllegalStateException("استخدم شاشة الأرقام التسلسلية لصرف هذه القطعة لأمر العمل")
        database.withTransaction {
            if (sparePartDao.adjustStockSafe(current.id, -quantity) == 0) {
                throw IllegalStateException("الكمية المطلوبة ($quantity) أكبر من المتوفر (${current.onHandQty})")
            }
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = current.id,
                    workOrderId = order.id,
                    transactionType = "Issue",
                    quantity = quantity,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "صرف لأمر العمل: ${order.title}"
                )
            )
            workOrderDao.insertWorkOrder(order.copy(partsCost = order.partsCost + quantity * current.lastPrice))
            recordAudit("Issue", "Inventory", "صرف $quantity من ${current.partNumber} لأمر العمل #${order.id}", actor)
        }
    }

    suspend fun receivePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {
        require(quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
        val current = sparePartDao.getById(part.id) ?: throw IllegalStateException("قطعة الغيار غير موجودة")
        if (current.serializationActive) throw IllegalStateException("استخدم شاشة الأرقام التسلسلية لاستلام هذه القطعة")
        database.withTransaction {
            if (sparePartDao.adjustStockSafe(current.id, quantity) == 0) {
                throw IllegalStateException("تعذّر تحديث كمية المخزون")
            }
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = current.id,
                    workOrderId = null,
                    transactionType = "Receive",
                    quantity = quantity,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "استلام يدوي من شاشة المخزون"
                )
            )
            recordAudit("Receive", "Inventory", "استلام $quantity من ${current.partNumber}", actor)
        }
    }

    // ---------------------------------------------------------------------
    // Users
    // ---------------------------------------------------------------------

    suspend fun addTechnician(actor: String = "System") {
        val number = (userDao.countOnce() + 1).coerceAtLeast(5)
        userDao.insert(
            UserEntity(
                name = "Technician $number",
                username = "tech$number",
                role = "Technician",
                isActive = true,
                password = PasswordHasher.hash("1234")
            )
        )
        recordAudit("Create", "User", "إضافة فني جديد tech$number", actor)
    }


    private fun validateLinearAssetMaster(asset: AssetEntity) {
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

    private suspend fun validateLinearMaintenanceReference(
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

    // ---------------------------------------------------------------------
    // CRUD — Assets
    // ---------------------------------------------------------------------

    suspend fun saveAsset(asset: AssetEntity, actor: String = "System") {
        validateLinearAssetMaster(asset)
        val isNew = asset.id == 0L
        assetDao.insertAsset(asset)
        recordAudit(if (isNew) "Create" else "Update", "Asset", "${if (isNew) "إضافة" else "تعديل"} أصل: ${asset.code}", actor)
    }

    suspend fun deleteAsset(asset: AssetEntity, actor: String = "System") {
        serialService.ensureAssetDeletable(asset.id)
        database.withTransaction {
            bomHeaderDao.headersForAsset(asset.id).forEach { header ->
                bomDao.deleteForHeader(header.id)
                bomHeaderDao.deleteById(header.id)
            }
            assetDao.deleteById(asset.id)
            recordAudit("Delete", "Asset", "حذف أصل: ${asset.code}", actor)
        }
    }

    suspend fun changeAssetStatus(asset: AssetEntity, status: String, reason: String = "", actor: String = "System") {
        assetDao.insertAsset(asset.copy(status = status))
        val reasonSuffix = if (reason.isNotBlank()) " — السبب: $reason" else ""
        recordAudit("Status", "Asset", "تغيير حالة ${asset.code} من ${asset.status} إلى $status$reasonSuffix", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Spare parts
    // ---------------------------------------------------------------------

    suspend fun savePart(part: SparePartEntity, actor: String = "System") {
        serialService.validatePartChange(part)
        val isNew = part.id == 0L
        sparePartDao.insert(part)
        recordAudit(if (isNew) "Create" else "Update", "Inventory", "${if (isNew) "إضافة" else "تعديل"} قطعة: ${part.partNumber}", actor)
    }

    suspend fun deletePart(part: SparePartEntity, actor: String = "System") {
        serialService.ensurePartDeletable(part.id)
        sparePartDao.deleteById(part.id)
        recordAudit("Delete", "Inventory", "حذف قطعة: ${part.partNumber}", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Work orders (edit / delete)
    // ---------------------------------------------------------------------

    suspend fun saveWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
        validateLinearMaintenanceReference(
            workOrder.assetId,
            workOrder.linearStartPoint,
            workOrder.linearEndPoint,
            workOrder.linearMarker,
            workOrder.linearHorizontalOffset,
            workOrder.linearVerticalOffset
        )
        val isNew = workOrder.id == 0L
        // Keep an existing decision; otherwise (re)derive whether sign-off is needed.
        val toSave = if (workOrder.approvalStatus == "Approved" || workOrder.approvalStatus == "Rejected") {
            workOrder
        } else {
            workOrder.copy(approvalStatus = if (workOrder.needsApproval()) "Pending" else "NotRequired")
        }
        workOrderDao.insertWorkOrder(toSave)
        recordAudit(if (isNew) "Create" else "Update", "WorkOrder", "${if (isNew) "إنشاء" else "تعديل"} أمر عمل: ${workOrder.title}", actor)
    }

    suspend fun setWorkOrderApproval(workOrder: WorkOrderEntity, approved: Boolean, actor: String = "System") {
        val status = if (approved) "Approved" else "Rejected"
        workOrderDao.insertWorkOrder(workOrder.copy(approvalStatus = status, approvedBy = actor))
        recordAudit("Approval", "WorkOrder", "${if (approved) "اعتماد" else "رفض"} أمر العمل: ${workOrder.title}", actor)
    }

    suspend fun deleteWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
        workOrderDao.deleteById(workOrder.id)
        recordAudit("Delete", "WorkOrder", "حذف أمر عمل: ${workOrder.title}", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Preventive maintenance
    // ---------------------------------------------------------------------

    suspend fun savePreventiveMaintenance(item: PreventiveMaintenanceEntity, actor: String = "System") {
        val isNew = item.id == 0L
        pmDao.insert(item)
        recordAudit(if (isNew) "Create" else "Update", "PreventiveMaintenance", "${if (isNew) "إضافة" else "تعديل"} صيانة دورية: ${item.title}", actor)
    }

    suspend fun deletePreventiveMaintenance(item: PreventiveMaintenanceEntity, actor: String = "System") {
        pmDao.deleteById(item.id)
        recordAudit("Delete", "PreventiveMaintenance", "حذف صيانة دورية: ${item.title}", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Users
    // ---------------------------------------------------------------------

    suspend fun saveUser(user: UserEntity, actor: String = "System") {
        val isNew = user.id == 0L
        // Resolve the password: keep the existing hash when editing without a new password,
        // hash any new plain-text password, and never store plain text.
        val resolved = when {
            user.password.isBlank() && !isNew ->
                user.copy(password = userDao.getById(user.id)?.password ?: PasswordHasher.hash("1234"))
            user.password.isBlank() && isNew ->
                user.copy(password = PasswordHasher.hash("1234"))
            PasswordHasher.isHashed(user.password) -> user
            else -> user.copy(password = PasswordHasher.hash(user.password))
        }
        userDao.insert(resolved)
        recordAudit(if (isNew) "Create" else "Update", "User", "${if (isNew) "إضافة" else "تعديل"} مستخدم: ${user.username}", actor)
    }

    suspend fun setUserActive(user: UserEntity, active: Boolean, actor: String = "System") {
        userDao.setActive(user.id, active)
        recordAudit("Update", "User", "${if (active) "تفعيل" else "تعطيل"} المستخدم: ${user.username}", actor)
    }

    suspend fun deleteUser(user: UserEntity, actor: String = "System") {
        userDao.deleteById(user.id)
        recordAudit("Delete", "User", "حذف المستخدم: ${user.username}", actor)
    }

    // ---------------------------------------------------------------------
    // Meters & readings
    // ---------------------------------------------------------------------

    suspend fun saveMeasuringPoint(point: MeasuringPointEntity, actor: String = "System") {
        val isNew = point.id == 0L
        measurementDao.insertPoint(point)
        recordAudit(if (isNew) "Create" else "Update", "Meter", "${if (isNew) "إضافة" else "تعديل"} نقطة قياس: ${point.name}", actor)
    }

    suspend fun deleteMeasuringPoint(point: MeasuringPointEntity, actor: String = "System") {
        measurementDao.deletePointById(point.id)
        recordAudit("Delete", "Meter", "حذف نقطة قياس: ${point.name}", actor)
    }

    /**
     * Records a reading. Returns an optional warning message (counter decrease /
     * over-limit) for the UI to surface. Cumulative counters may not decrease.
     */
    suspend fun addReading(point: MeasuringPointEntity, value: Double, note: String, actor: String = "System"): String? {
        if (point.isCounter && value < point.lastReading) {
            return "لا يمكن أن تقل قراءة العداد التراكمي عن ${point.lastReading}"
        }
        val now = DateStrings.now()
        database.withTransaction {
            measurementDao.insertReading(
                MeasurementReadingEntity(
                    pointId = point.id,
                    assetId = point.assetId,
                    value = value,
                    createdAt = now,
                    createdBy = actor,
                    note = note
                )
            )
            measurementDao.updateLastReading(point.id, value, now)
            recordAudit("Reading", "Meter", "قراءة ${point.name}: $value ${point.unit}", actor)
        }
        val limit = point.upperLimit
        return if (limit != null && value > limit) "تنبيه: تجاوزت القراءة الحد الأعلى ($limit ${point.unit})" else null
    }

    // ---------------------------------------------------------------------
    // Functional locations
    // ---------------------------------------------------------------------

    suspend fun saveFunctionalLocation(location: FunctionalLocationEntity, actor: String = "System") {
        val isNew = location.id == 0L
        locationDao.insert(location)
        recordAudit(if (isNew) "Create" else "Update", "Location", "${if (isNew) "إضافة" else "تعديل"} موقع فني: ${location.code}", actor)
    }

    suspend fun deleteFunctionalLocation(location: FunctionalLocationEntity, actor: String = "System") {
        locationDao.deleteById(location.id)
        recordAudit("Delete", "Location", "حذف موقع فني: ${location.code}", actor)
    }

    // ---------------------------------------------------------------------
    // Warehouses (stores)
    // ---------------------------------------------------------------------

    suspend fun saveWarehouse(warehouse: WarehouseEntity, actor: String = "System") {
        val isNew = warehouse.id == 0L
        warehouseDao.insert(warehouse)
        recordAudit(if (isNew) "Create" else "Update", "Warehouse", "${if (isNew) "إضافة" else "تعديل"} مستودع: ${warehouse.code}", actor)
    }

    suspend fun deleteWarehouse(warehouse: WarehouseEntity, actor: String = "System") {
        warehouseDao.deleteById(warehouse.id)
        recordAudit("Delete", "Warehouse", "حذف مستودع: ${warehouse.code}", actor)
    }

    // ---------------------------------------------------------------------
    // CAPA (corrective / preventive actions)
    // ---------------------------------------------------------------------

    suspend fun saveCapa(item: CapaEntity, actor: String = "System") {
        val isNew = item.id == 0L
        val toSave = if (isNew && item.code.isBlank()) {
            item.copy(code = "CAPA-%03d".format(capaDao.countOnce() + 1))
        } else {
            item
        }
        capaDao.insert(toSave)
        recordAudit(if (isNew) "Create" else "Update", "CAPA", "${if (isNew) "إنشاء" else "تعديل"} إجراء: ${toSave.title}", actor)
    }

    suspend fun updateCapaStatus(item: CapaEntity, status: String, actor: String = "System") {
        capaDao.updateStatus(item.id, status)
        recordAudit("Update", "CAPA", "تحديث حالة الإجراء ${item.code} إلى $status", actor)
    }

    suspend fun deleteCapa(item: CapaEntity, actor: String = "System") {
        capaDao.deleteById(item.id)
        recordAudit("Delete", "CAPA", "حذف إجراء: ${item.title}", actor)
    }

    // ---------------------------------------------------------------------
    // Asset documents
    // ---------------------------------------------------------------------

    suspend fun saveAssetDocument(doc: AssetDocumentEntity, actor: String = "System") {
        val isNew = doc.id == 0L
        val toSave = if (isNew) doc.copy(uploadedBy = actor, uploadedAt = DateStrings.now()) else doc
        documentDao.insert(toSave)
        recordAudit(if (isNew) "Create" else "Update", "Document", "${if (isNew) "إضافة" else "تعديل"} مستند: ${doc.title}", actor)
    }

    suspend fun deleteAssetDocument(doc: AssetDocumentEntity, actor: String = "System") {
        documentDao.deleteById(doc.id)
        recordAudit("Delete", "Document", "حذف مستند: ${doc.title}", actor)
    }

    // ---------------------------------------------------------------------
    // Asset characteristics (classification)
    // ---------------------------------------------------------------------

    suspend fun saveCharacteristic(item: AssetCharacteristicEntity, actor: String = "System") {
        val isNew = item.id == 0L
        characteristicDao.insert(item)
        recordAudit(if (isNew) "Create" else "Update", "Characteristic", "${if (isNew) "إضافة" else "تعديل"} خاصية: ${item.name}", actor)
    }

    suspend fun deleteCharacteristic(item: AssetCharacteristicEntity, actor: String = "System") {
        characteristicDao.deleteById(item.id)
        recordAudit("Delete", "Characteristic", "حذف خاصية: ${item.name}", actor)
    }

    // ---------------------------------------------------------------------
    // Asset BOM (maintenance bill of materials)
    // ---------------------------------------------------------------------

    suspend fun saveBomHeader(header: AssetBomHeaderEntity, actor: String = "System") {
        require(header.code.isNotBlank()) { "أدخل كود قائمة المكونات" }
        require(header.name.isNotBlank()) { "أدخل اسم قائمة المكونات" }
        require(header.hasValidDates()) { "تاريخ بداية الصلاحية يجب ألا يتجاوز تاريخ النهاية" }
        if (header.assignmentType == "Direct") {
            require(header.assetId != null && header.assetId != 0L) { "حدد الأصل المرتبط بالقائمة" }
        } else {
            require(header.constructionType.isNotBlank()) { "أدخل نوع الإنشاء للتعيين المشترك" }
        }

        val normalized = header.copy(
            assetId = header.assetId.takeIf { header.assignmentType == "Direct" },
            code = header.code.trim().uppercase(),
            name = header.name.trim(),
            alternative = header.alternative.trim().ifBlank { "01" },
            constructionType = header.constructionType.trim(),
            description = header.description.trim()
        )
        val isNew = normalized.id == 0L
        if (isNew) bomHeaderDao.insert(normalized) else bomHeaderDao.update(normalized)
        recordAudit(if (isNew) "Create" else "Update", "BOM", "${if (isNew) "إنشاء" else "تعديل"} قائمة مكونات: ${normalized.code}", actor)
    }

    suspend fun deleteBomHeader(header: AssetBomHeaderEntity, actor: String = "System") {
        database.withTransaction {
            bomDao.deleteForHeader(header.id)
            bomHeaderDao.deleteById(header.id)
            recordAudit("Delete", "BOM", "حذف قائمة مكونات: ${header.code}", actor)
        }
    }

    suspend fun saveBomItem(item: AssetBomItemEntity, actor: String = "System") {
        require(item.headerId != 0L) { "حدد قائمة المكونات" }
        require(item.itemNumber > 0) { "رقم البند يجب أن يكون أكبر من صفر" }
        require(item.quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
        require(item.hasValidDates()) { "تاريخ بداية صلاحية البند يجب ألا يتجاوز تاريخ النهاية" }

        val header = bomHeaderDao.getById(item.headerId)
            ?: throw IllegalStateException("قائمة المكونات المحددة غير موجودة")
        val parent = item.parentItemId?.let { bomDao.getById(it) }
        if (parent != null) require(parent.headerId == item.headerId) { "البند الأب يجب أن يكون ضمن القائمة نفسها" }
        require(item.id == 0L || item.parentItemId != item.id) { "لا يمكن أن يكون البند أباً لنفسه" }

        val normalized = when (item.itemCategory) {
            "Stock", "NonStock" -> {
                require(item.partId != 0L) { "حدد قطعة الغيار" }
                item.copy(assetId = header.assetId ?: 0L, assemblyAssetId = null)
            }
            "Assembly" -> {
                require(item.assemblyAssetId != null && item.assemblyAssetId != 0L) { "حدد تجميعة الصيانة" }
                require(item.assemblyAssetId != header.assetId) { "لا يمكن ربط الأصل بنفسه كتجميعة" }
                item.copy(assetId = header.assetId ?: 0L, partId = 0L, useInOrders = false)
            }
            "Text" -> {
                require(item.notes.isNotBlank()) { "أدخل وصف البند النصي" }
                item.copy(assetId = header.assetId ?: 0L, partId = 0L, assemblyAssetId = null, useInOrders = false)
            }
            else -> throw IllegalArgumentException("فئة بند غير مدعومة")
        }

        val isNew = normalized.id == 0L
        bomDao.insert(normalized)
        recordAudit(if (isNew) "Create" else "Update", "BOM", "${if (isNew) "إضافة" else "تعديل"} بند ${normalized.itemNumber} في ${header.code}", actor)
    }

    suspend fun deleteBomItem(item: AssetBomItemEntity, actor: String = "System") {
        database.withTransaction {
            bomDao.clearParent(item.id)
            bomDao.deleteById(item.id)
            recordAudit("Delete", "BOM", "حذف بند مكونات رقم ${item.itemNumber}", actor)
        }
    }

    // ---------------------------------------------------------------------
    // Asset movements (install / transfer / dismantle / retire)
    // ---------------------------------------------------------------------

    /**
     * Records a lifecycle event for an asset AND keeps the asset's own location/status
     * consistent so the 360 card and the timeline never disagree:
     * - Install / Transfer move the asset to [toLocationId]/[toLocationName] and set it Running.
     * - Dismantle clears the asset's functional location and sets it to Standby.
     * - Retire sets the asset to Retired.
     */
    suspend fun performAssetMovement(
        asset: AssetEntity,
        eventType: String,
        toLocationId: Long?,
        toLocationName: String,
        notes: String,
        actor: String = "System"
    ) {
        database.withTransaction {
            val now = DateStrings.now()
            val updated = when (eventType) {
                MovementType.INSTALL, MovementType.TRANSFER ->
                    asset.copy(locationId = toLocationId, location = toLocationName.ifBlank { asset.location }, status = "Running")
                MovementType.DISMANTLE ->
                    asset.copy(locationId = null, status = "Standby")
                MovementType.RETIRE ->
                    asset.copy(status = "Retired")
                else -> asset
            }
            assetDao.insertAsset(updated)
            movementDao.insert(
                AssetMovementEntity(
                    assetId = asset.id,
                    eventType = eventType,
                    fromLocationId = asset.locationId,
                    toLocationId = if (eventType == MovementType.DISMANTLE || eventType == MovementType.RETIRE) null else toLocationId,
                    fromLocationName = asset.location,
                    toLocationName = if (eventType == MovementType.DISMANTLE || eventType == MovementType.RETIRE) "" else toLocationName,
                    notes = notes,
                    performedBy = actor,
                    occurredAt = now
                )
            )
            recordAudit("Movement", "Asset", "${MovementType.label(eventType)} للأصل: ${asset.code}", actor)
        }
    }

    suspend fun deleteAssetMovement(movement: AssetMovementEntity, actor: String = "System") {
        movementDao.deleteById(movement.id)
        recordAudit("Delete", "Movement", "حذف حركة (${MovementType.label(movement.eventType)})", actor)
    }

    // ---------------------------------------------------------------------
    // PM inspection checklist
    // ---------------------------------------------------------------------

    suspend fun saveChecklistItem(item: PmChecklistItemEntity, actor: String = "System") {
        val isNew = item.id == 0L
        checklistDao.insert(item)
        recordAudit(if (isNew) "Create" else "Update", "Checklist", "${if (isNew) "إضافة" else "تعديل"} بند فحص: ${item.text}", actor)
    }

    suspend fun setChecklistResult(item: PmChecklistItemEntity, result: String, actor: String = "System") {
        checklistDao.insert(item.copy(result = result))
        recordAudit("Update", "Checklist", "نتيجة بند الفحص \"${item.text}\": $result", actor)
    }

    suspend fun deleteChecklistItem(item: PmChecklistItemEntity, actor: String = "System") {
        checklistDao.deleteById(item.id)
        recordAudit("Delete", "Checklist", "حذف بند فحص: ${item.text}", actor)
    }

    // ---------------------------------------------------------------------
    // Maintenance notifications (بلاغات) — the trigger of maintenance work
    // ---------------------------------------------------------------------

    suspend fun saveNotification(notification: MaintenanceNotificationEntity, actor: String = "System") {
        validateLinearMaintenanceReference(
            notification.assetId,
            notification.linearStartPoint,
            notification.linearEndPoint,
            notification.linearMarker,
            notification.linearHorizontalOffset,
            notification.linearVerticalOffset
        )
        val isNew = notification.id == 0L
        val toSave = if (isNew) {
            val seq = notificationDao.countOnce() + 1
            notification.copy(
                number = "NTF-%04d".format(seq),
                reportedBy = actor,
                reportedAt = DateStrings.now(),
                status = "New"
            )
        } else {
            notification
        }
        notificationDao.insert(toSave)
        recordAudit(if (isNew) "Create" else "Update", "Notification", "${if (isNew) "إنشاء" else "تعديل"} بلاغ: ${notification.title}", actor)
    }

    suspend fun setNotificationStatus(notification: MaintenanceNotificationEntity, status: String, actor: String = "System") {
        notificationDao.insert(notification.copy(status = status))
        recordAudit("Status", "Notification", "تغيير حالة البلاغ ${notification.number} إلى $status", actor)
    }

    /**
     * Converts an approved notification into a work order, copying asset/priority/description,
     * then links the two (notification → OrderCreated + linkedOrderId).
     */
    suspend fun createOrderFromNotification(
        notification: MaintenanceNotificationEntity,
        assignedTo: String,
        dueAt: String,
        actor: String = "System"
    ) {
        validateLinearMaintenanceReference(
            notification.assetId,
            notification.linearStartPoint,
            notification.linearEndPoint,
            notification.linearMarker,
            notification.linearHorizontalOffset,
            notification.linearVerticalOffset
        )
        database.withTransaction {
            val now = DateStrings.today()
            val orderId = workOrderDao.insertWorkOrder(
                WorkOrderEntity(
                    assetId = notification.assetId ?: 0L,
                    title = notification.title,
                    description = notification.description,
                    priority = notification.priority,
                    status = "Open",
                    assignedTo = assignedTo,
                    createdAt = now,
                    dueAt = dueAt,
                    estimatedCost = 0.0,
                    isFailure = notification.type == "Breakdown",
                    approvalStatus = if (notification.priority == "Critical") "Pending" else "NotRequired",
                    linearStartPoint = notification.linearStartPoint,
                    linearEndPoint = notification.linearEndPoint,
                    linearMarker = notification.linearMarker,
                    linearHorizontalOffset = notification.linearHorizontalOffset,
                    linearVerticalOffset = notification.linearVerticalOffset
                )
            )
            notificationDao.insert(notification.copy(status = "OrderCreated", linkedOrderId = orderId))
            recordAudit("Create", "WorkOrder", "إنشاء أمر عمل من البلاغ ${notification.number}", actor)
        }
    }

    suspend fun deleteNotification(notification: MaintenanceNotificationEntity, actor: String = "System") {
        notificationDao.deleteById(notification.id)
        recordAudit("Delete", "Notification", "حذف بلاغ: ${notification.title}", actor)
    }

    // ---------------------------------------------------------------------
    // Work order operations (steps within an order)
    // ---------------------------------------------------------------------

    suspend fun saveOperation(operation: WorkOrderOperationEntity, actor: String = "System") {
        val isNew = operation.id == 0L
        operationDao.insert(operation)
        recordAudit(if (isNew) "Create" else "Update", "Operation", "${if (isNew) "إضافة" else "تعديل"} عملية ${operation.operationNumber}: ${operation.description}", actor)
    }

    suspend fun setOperationStatus(operation: WorkOrderOperationEntity, status: String, actor: String = "System") {
        // Confirming with no recorded actual hours falls back to the planned estimate.
        val actual = if (status == "Confirmed" && operation.actualHours == 0.0) operation.plannedHours else operation.actualHours
        operationDao.insert(operation.copy(status = status, actualHours = actual))
        recordAudit("Status", "Operation", "عملية ${operation.operationNumber} → $status", actor)
    }

    suspend fun deleteOperation(operation: WorkOrderOperationEntity, actor: String = "System") {
        operationDao.deleteById(operation.id)
        recordAudit("Delete", "Operation", "حذف عملية ${operation.operationNumber}", actor)
    }

    // ---------------------------------------------------------------------
    // Operation confirmations (تأكيدات)
    // ---------------------------------------------------------------------

    /**
     * Records a confirmation against an operation and keeps the operation consistent:
     * accumulates actual hours and, on a final confirmation, closes the operation.
     */
    suspend fun addConfirmation(
        confirmation: WorkOrderConfirmationEntity,
        operation: WorkOrderOperationEntity,
        actor: String = "System"
    ) {
        database.withTransaction {
            confirmationDao.insert(
                confirmation.copy(
                    technician = confirmation.technician.ifBlank { actor },
                    workDate = confirmation.workDate.ifBlank { DateStrings.today() },
                    createdAt = DateStrings.now()
                )
            )
            operationDao.insert(
                operation.copy(
                    actualHours = operation.actualHours + confirmation.actualWork,
                    status = if (confirmation.finalConfirmation) "Confirmed" else "In Progress"
                )
            )
            recordAudit(
                if (confirmation.finalConfirmation) "Confirm" else "PartialConfirm",
                "Operation",
                "تأكيد عملية ${operation.operationNumber} (${confirmation.actualWork}س)",
                actor
            )
        }
    }

    suspend fun deleteConfirmation(confirmation: WorkOrderConfirmationEntity, actor: String = "System") {
        confirmationDao.deleteById(confirmation.id)
        recordAudit("Delete", "Confirmation", "حذف تأكيد عملية #${confirmation.operationId}", actor)
    }

    // ---------------------------------------------------------------------
    // Work order evidence photos (أدلة التنفيذ)
    // ---------------------------------------------------------------------

    suspend fun addWorkOrderPhoto(orderId: Long, path: String, actor: String = "System") {
        photoDao.insert(
            WorkOrderPhotoEntity(orderId = orderId, path = path, addedBy = actor, addedAt = DateStrings.now())
        )
        recordAudit("Attach", "WorkOrder", "إرفاق صورة دليل لأمر العمل #$orderId", actor)
    }

    suspend fun deleteWorkOrderPhoto(photo: WorkOrderPhotoEntity, actor: String = "System") {
        photoDao.deleteById(photo.id)
        recordAudit("Delete", "WorkOrder", "حذف صورة دليل لأمر العمل #${photo.orderId}", actor)
    }

    // ---------------------------------------------------------------------
    // Work permits (تصاريح العمل / السلامة)
    // ---------------------------------------------------------------------

    suspend fun savePermit(permit: WorkPermitEntity, actor: String = "System") {
        val isNew = permit.id == 0L
        val toSave = if (isNew) permit.copy(createdBy = actor, createdAt = DateStrings.now(), status = "Pending") else permit
        permitDao.insert(toSave)
        recordAudit(if (isNew) "Create" else "Update", "Permit", "${if (isNew) "إصدار" else "تعديل"} تصريح (${permit.type}) لأمر العمل #${permit.orderId}", actor)
    }

    suspend fun setPermitStatus(permit: WorkPermitEntity, approved: Boolean, actor: String = "System") {
        val status = if (approved) "Approved" else "Rejected"
        permitDao.insert(permit.copy(status = status, approvedBy = actor))
        recordAudit("Approval", "Permit", "${if (approved) "اعتماد" else "رفض"} تصريح العمل #${permit.id}", actor)
    }

    suspend fun deletePermit(permit: WorkPermitEntity, actor: String = "System") {
        permitDao.deleteById(permit.id)
        recordAudit("Delete", "Permit", "حذف تصريح عمل #${permit.id}", actor)
    }

    // ---------------------------------------------------------------------
    // Task lists (قوالب العمل) + generation of orders from PM plans
    // ---------------------------------------------------------------------

    suspend fun saveTaskList(taskList: TaskListEntity, actor: String = "System") {
        val isNew = taskList.id == 0L
        taskListDao.insertTaskList(taskList)
        recordAudit(if (isNew) "Create" else "Update", "TaskList", "${if (isNew) "إضافة" else "تعديل"} قالب عمل: ${taskList.name}", actor)
    }

    suspend fun deleteTaskList(taskList: TaskListEntity, actor: String = "System") {
        taskListDao.deleteOperationsForList(taskList.id)
        taskListDao.deleteTaskListById(taskList.id)
        recordAudit("Delete", "TaskList", "حذف قالب عمل: ${taskList.name}", actor)
    }

    suspend fun saveTaskListOperation(operation: TaskListOperationEntity, actor: String = "System") {
        val isNew = operation.id == 0L
        taskListDao.insertOperation(operation)
        recordAudit(if (isNew) "Create" else "Update", "TaskList", "${if (isNew) "إضافة" else "تعديل"} عملية قالب ${operation.operationNumber}", actor)
    }

    suspend fun deleteTaskListOperation(operation: TaskListOperationEntity, actor: String = "System") {
        taskListDao.deleteOperationById(operation.id)
        recordAudit("Delete", "TaskList", "حذف عملية قالب ${operation.operationNumber}", actor)
    }

    // ---------------------------------------------------------------------
    // Excel (maintenance-kit) import
    // ---------------------------------------------------------------------

    private fun frequencyDaysFor(text: String): Int = when {
        text.contains("يومي") -> 1
        text.contains("أسبوعي") -> 7
        text.contains("شهري") && !text.contains("نصف") && !text.contains("ربع") -> 30
        text.contains("ربع") -> 90
        text.contains("نصف") -> 180
        text.contains("سنوي") || text.contains("سنوى") -> 365
        else -> 30
    }

    private fun sheetByKey(sheets: Map<String, List<List<String>>>, key: String): List<List<String>> =
        sheets.entries.firstOrNull { it.key.contains(key) }?.value ?: emptyList()

    /**
     * Imports a machine maintenance-kit workbook (the FVV template) and populates the relevant
     * modules: asset + characteristics, preventive-maintenance plans, a reusable task list with
     * its operations, the spare-parts catalogue, and a generated work order. Returns a summary.
     */
    suspend fun importMachineKit(sheets: Map<String, List<List<String>>>, actor: String = "System"): String {
        return database.withTransaction {
            val today = DateStrings.today()

            // --- Technical data → asset + characteristics ---
            val tech = sheetByKey(sheets, "البيانات الفنية")
            val techRows = tech.drop(1).filter { it.size >= 2 && it[0].isNotBlank() }
            val model = techRows.firstOrNull { it[0].contains("موديل") || it[0].contains("Model") }
                ?.getOrNull(1)?.trim().orEmpty().ifBlank { "Imported Machine" }
            val code = model.trim().replace(Regex("\\s+"), "-").uppercase().ifBlank { "IMPORT-${System.currentTimeMillis() % 100000}" }

            val existing = assetDao.findByCode(code)
            val asset = (existing ?: AssetEntity(
                id = 0, code = code, name = model, groupName = "Vibro Finishers",
                location = "Milling Floor", manufacturer = "OCRIM", model = model,
                status = "Running", criticality = "High", installedAt = today, lastInspectionAt = today
            )).copy(name = model, manufacturer = "OCRIM", model = model)
            val assetId = if (existing != null) { assetDao.insertAsset(asset); existing.id } else assetDao.insertAsset(asset)

            characteristicDao.deleteForAsset(assetId)
            val characteristics = techRows
                .filter { it.size >= 2 && it[1].isNotBlank() && !it[0].contains("البند") }
                .map { AssetCharacteristicEntity(0, assetId, it[0].trim(), it[1].trim(), "") }
            if (characteristics.isNotEmpty()) characteristicDao.insertAll(characteristics)

            // --- Maintenance plan → PM plans ---
            pmDao.deleteForAsset(assetId)
            val planRows = sheetByKey(sheets, "خطة الصيانة").drop(1).filter { it.size >= 2 && it[1].isNotBlank() }
            var plansCount = 0
            planRows.forEach { row ->
                val freq = frequencyDaysFor(row[0])
                pmDao.insert(
                    PreventiveMaintenanceEntity(
                        id = 0, assetId = assetId, title = row[1].trim().take(120),
                        frequencyDays = freq, lastDoneAt = today,
                        nextDueAt = DateStrings.addDays(today, freq), status = "Scheduled",
                        estimatedDurationMinutes = 30
                    )
                )
                plansCount++
            }

            // --- Procedures → task list + template operations ---
            val procRows = sheetByKey(sheets, "إجراءات الصيانة").drop(1).filter { it.isNotEmpty() && it[0].isNotBlank() }
            val taskListId = taskListDao.insertTaskList(
                TaskListEntity(0, "إجراءات صيانة $model", "مستورد من ملف صيانة الآلة", "Mechanical")
            )
            val templateOps = procRows.mapIndexed { i, row ->
                TaskListOperationEntity(0, taskListId, "%04d".format((i + 1) * 10), row[0].trim().take(120), "Mechanical", 1.0)
            }
            if (templateOps.isNotEmpty()) taskListDao.insertOperations(templateOps)

            // --- Spare parts catalogue ---
            val partRows = sheetByKey(sheets, "قطع الغيار").drop(1)
            var partsCount = 0
            val parts = partRows.mapNotNull { row ->
                val partNumber = row.getOrNull(1)?.trim().orEmpty()
                if (partNumber.isBlank() || partNumber == "—" || partNumber == "Code") return@mapNotNull null
                partsCount++
                SparePartEntity(
                    id = 0, partNumber = partNumber, name = row.getOrNull(2)?.trim().orEmpty(),
                    equipmentGroup = code, unit = "pcs", onHandQty = 0, minQty = 0,
                    location = "Store", lastPrice = 0.0
                )
            }
            if (parts.isNotEmpty()) sparePartDao.insertAll(parts)

            // --- Generate a work order from the imported task list ---
            val orderId = workOrderDao.insertWorkOrder(
                WorkOrderEntity(
                    assetId = assetId, title = "صيانة شاملة — $model",
                    description = "أمر عمل مُولّد من ملف الصيانة المستورد", priority = "Medium",
                    status = "Open", assignedTo = actor, createdAt = today,
                    dueAt = DateStrings.addDays(today, 7), estimatedCost = 0.0
                )
            )
            if (templateOps.isNotEmpty()) {
                operationDao.insertAll(
                    templateOps.map {
                        WorkOrderOperationEntity(
                            orderId = orderId, operationNumber = it.operationNumber,
                            description = it.description, workCenter = it.workCenter,
                            plannedHours = it.plannedHours, requiresConfirmation = true, status = "Open"
                        )
                    }
                )
            }

            recordAudit("Import", "System", "استيراد ملف صيانة: $model", actor)
            "تم استيراد $model — ${characteristics.size} خاصية، $plansCount خطة، $partsCount قطعة، قالب عمل (${templateOps.size} عملية)، وأمر عمل."
        }
    }

    /**
     * Generates a work order from a preventive-maintenance plan, copying the linked
     * task list's template operations into the new order (TLIST-006 / SCH-007).
     */
    suspend fun generateWorkOrderFromPm(pm: PreventiveMaintenanceEntity, actor: String = "System") {
        database.withTransaction {
            val today = DateStrings.today()
            val orderId = workOrderDao.insertWorkOrder(
                WorkOrderEntity(
                    assetId = pm.assetId,
                    title = "صيانة دورية: ${pm.title}",
                    description = "مُولّد تلقائياً من الخطة الوقائية",
                    priority = "Medium",
                    status = "Open",
                    assignedTo = actor,
                    createdAt = today,
                    dueAt = pm.nextDueAt,
                    estimatedCost = 0.0
                )
            )
            val template = pm.taskListId?.let { taskListDao.operationsForList(it) } ?: emptyList()
            if (template.isNotEmpty()) {
                operationDao.insertAll(
                    template.map {
                        WorkOrderOperationEntity(
                            orderId = orderId,
                            operationNumber = it.operationNumber,
                            description = it.description,
                            workCenter = it.workCenter,
                            plannedHours = it.plannedHours,
                            requiresConfirmation = true,
                            status = "Open"
                        )
                    }
                )
            }
            recordAudit("Generate", "WorkOrder", "توليد أمر عمل من الصيانة الدورية: ${pm.title} (${template.size} عملية)", actor)
        }
    }
}

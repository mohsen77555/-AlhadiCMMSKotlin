package com.alhadi.cmms.data

import androidx.room.withTransaction
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.util.DateStrings
import kotlinx.coroutines.flow.Flow

class CmmsRepository(private val database: AppDatabase) {
    private val assetDao = database.assetDao()
    private val workOrderDao = database.workOrderDao()
    private val pmDao = database.preventiveMaintenanceDao()
    private val sparePartDao = database.sparePartDao()
    private val transactionDao = database.inventoryTransactionDao()
    private val userDao = database.userDao()
    private val auditLogDao = database.auditLogDao()
    private val measurementDao = database.measurementDao()
    private val locationDao = database.functionalLocationDao()
    private val capaDao = database.capaDao()
    private val documentDao = database.assetDocumentDao()
    private val characteristicDao = database.assetCharacteristicDao()
    private val bomDao = database.assetBomDao()
    private val movementDao = database.assetMovementDao()

    val assets: Flow<List<AssetEntity>> = assetDao.observeAssets()
    val workOrders: Flow<List<WorkOrderEntity>> = workOrderDao.observeWorkOrders()
    val preventiveMaintenance: Flow<List<PreventiveMaintenanceEntity>> = pmDao.observePreventiveMaintenance()
    val spareParts: Flow<List<SparePartEntity>> = sparePartDao.observeSpareParts()
    val recentTransactions: Flow<List<InventoryTransactionEntity>> = transactionDao.observeRecentTransactions()
    val users: Flow<List<UserEntity>> = userDao.observeUsers()
    val currentUser: Flow<UserEntity?> = userDao.observeCurrentUser()
    val auditLog: Flow<List<AuditLogEntity>> = auditLogDao.observeRecent()
    val measuringPoints: Flow<List<MeasuringPointEntity>> = measurementDao.observePoints()
    val readings: Flow<List<MeasurementReadingEntity>> = measurementDao.observeReadings()
    val functionalLocations: Flow<List<FunctionalLocationEntity>> = locationDao.observeLocations()
    val capaActions: Flow<List<CapaEntity>> = capaDao.observeCapa()
    val assetDocuments: Flow<List<AssetDocumentEntity>> = documentDao.observeDocuments()
    val assetCharacteristics: Flow<List<AssetCharacteristicEntity>> = characteristicDao.observeCharacteristics()
    val assetBom: Flow<List<AssetBomItemEntity>> = bomDao.observeBom()
    val assetMovements: Flow<List<AssetMovementEntity>> = movementDao.observeMovements()

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
        val user = userDao.authenticate(username.trim(), password)
        if (user != null) {
            recordAudit("Login", "User", "تسجيل دخول ناجح", user.name)
        }
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

    // ---------------------------------------------------------------------
    // Sample data
    // ---------------------------------------------------------------------

    suspend fun seedSampleData(replace: Boolean = false) {
        database.withTransaction {
            if (replace) {
                auditLogDao.deleteAll()
                movementDao.deleteAll()
                bomDao.deleteAll()
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
                AssetEntity(7, "RM-01", "Rollermill C1", "Rollermills", "Milling Floor", "Buhler", "Antares", "Running", "Critical", "2017-09-10", DateStrings.daysFromToday(-7)),
                AssetEntity(8, "PL-04", "Plansifter 4", "Plansifters", "Milling Floor", "Buhler", "MPA", "Running", "Critical", "2017-10-02", DateStrings.daysFromToday(-21)),
                AssetEntity(9, "PR-02", "Purifier 2", "Purifiers", "Milling Floor", "Golfetto", "Pur-2", "Running", "High", "2018-04-19", DateStrings.daysFromToday(-16)),
                AssetEntity(10, "CP-01", "Air Compressor 1", "Compressors", "Utility Room", "Atlas Copco", "GA-37", "Running", "Critical", "2016-12-12", DateStrings.daysFromToday(-8)),
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
                WorkOrderEntity(2, 4, "Silo fan not starting", "Check overload, contactor, motor insulation, and fan impeller.", "High", "In Progress", "Electrical Technician", DateStrings.daysFromToday(-1), today, 250.0, isFailure = true, downtimeHours = 6.0),
                WorkOrderEntity(3, 11, "Packing machine bag sensor alarm", "Clean and align sensor, verify signal in control panel.", "Medium", "Open", "Electrical Technician", today, DateStrings.daysFromToday(2), 45.0),
                WorkOrderEntity(4, 2, "Chain conveyor lubrication", "Lubricate chain and inspect tension.", "Low", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-7), DateStrings.daysFromToday(-5), 30.0, "Completed and tested."),
                WorkOrderEntity(5, 4, "Silo fan bearing failure", "Replaced damaged fan bearing.", "High", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-45), DateStrings.daysFromToday(-44), 180.0, "Bearing replaced.", isFailure = true, downtimeHours = 8.0),
                WorkOrderEntity(6, 4, "Silo fan motor overheat", "Cleaned and re-greased motor, fixed ventilation.", "High", "Closed", "Electrical Technician", DateStrings.daysFromToday(-90), DateStrings.daysFromToday(-89), 120.0, "Motor serviced.", isFailure = true, downtimeHours = 5.0),
                WorkOrderEntity(7, 7, "Rollermill jam", "Cleared product jam and inspected rolls.", "Critical", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-30), DateStrings.daysFromToday(-30), 90.0, "Cleared.", isFailure = true, downtimeHours = 3.5),
                WorkOrderEntity(8, 7, "Rollermill belt break", "Replaced broken drive belt.", "Critical", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-75), DateStrings.daysFromToday(-75), 150.0, "Belt replaced.", isFailure = true, downtimeHours = 4.0)
            )

            val preventiveMaintenance = listOf(
                PreventiveMaintenanceEntity(1, 1, "Inspect buckets and belt tension", 30, DateStrings.daysFromToday(-35), DateStrings.daysFromToday(-5), "Due", 90),
                PreventiveMaintenanceEntity(2, 7, "Rollermill weekly inspection", 7, DateStrings.daysFromToday(-6), DateStrings.daysFromToday(1), "Scheduled", 45),
                PreventiveMaintenanceEntity(3, 8, "Plansifter sieve and frame inspection", 14, DateStrings.daysFromToday(-18), DateStrings.daysFromToday(-4), "Due", 60),
                PreventiveMaintenanceEntity(4, 10, "Compressor oil and air filter check", 30, DateStrings.daysFromToday(-22), DateStrings.daysFromToday(8), "Scheduled", 50),
                PreventiveMaintenanceEntity(5, 12, "Scale calibration verification", 90, DateStrings.daysFromToday(-70), DateStrings.daysFromToday(20), "Scheduled", 120)
            )

            val spareParts = listOf(
                SparePartEntity(1, "BRG-6205", "Bearing 6205 ZZ", "Rollermills", "pcs", 12, 6, "Store A-01", 4.5),
                SparePartEntity(2, "BELT-A45", "V-Belt A45", "Bucket Elevators", "pcs", 3, 5, "Store B-03", 8.0),
                SparePartEntity(3, "CHAIN-16B", "Chain 16B", "Chain Conveyors", "meter", 18, 10, "Store C-01", 12.5),
                SparePartEntity(4, "SENSOR-PNP", "PNP Proximity Sensor", "Sensors", "pcs", 2, 4, "Electrical Cabinet", 18.0),
                SparePartEntity(5, "FILTER-GA37", "Compressor Air Filter", "Compressors", "pcs", 7, 3, "Utility Store", 25.0),
                SparePartEntity(6, "BAG-NEEDLE", "Packing Stitching Needle", "Packing Machines", "pcs", 40, 20, "Packing Store", 0.8)
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
            userDao.insertAll(users)
            workOrderDao.insertWorkOrders(workOrders)
            pmDao.insertAll(preventiveMaintenance)
            sparePartDao.insertAll(spareParts)
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

            val bomItems = listOf(
                AssetBomItemEntity(1, 7, 1, 4),
                AssetBomItemEntity(2, 1, 2, 2),
                AssetBomItemEntity(3, 2, 3, 6),
                AssetBomItemEntity(4, 10, 5, 1)
            )

            val movements = listOf(
                AssetMovementEntity(1, 7, MovementType.INSTALL, null, 6, "", "Rollermill Station", "تركيب أولي في الموقع", "Mohsen Alhadi", today),
                AssetMovementEntity(2, 10, MovementType.INSTALL, null, 5, "", "Utility Room", "تركيب الضاغط", "Mohsen Alhadi", today),
                AssetMovementEntity(3, 4, MovementType.TRANSFER, 5, 3, "Utility Room", "Milling Floor", "نقل اللوحة الكهربائية", "Maintenance Supervisor", today)
            )

            measurementDao.insertPoints(measuringPoints)
            locationDao.insertAll(locations)
            capaDao.insertAll(capaActions)
            documentDao.insertAll(documents)
            characteristicDao.insertAll(characteristics)
            bomDao.insertAll(bomItems)
            movementDao.insertAll(movements)
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
                estimatedCost = estimatedCost
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
        recordAudit("Complete", "PreventiveMaintenance", "تنفيذ صيانة دورية: ${item.title}", actor)
    }

    // ---------------------------------------------------------------------
    // Inventory
    // ---------------------------------------------------------------------

    suspend fun issuePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {
        database.withTransaction {
            sparePartDao.adjustStock(part.id, -quantity)
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = part.id,
                    workOrderId = null,
                    transactionType = "Issue",
                    quantity = quantity,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "Manual issue from mobile inventory screen."
                )
            )
            recordAudit("Issue", "Inventory", "صرف $quantity من ${part.partNumber}", actor)
        }
    }

    suspend fun receivePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {
        database.withTransaction {
            sparePartDao.adjustStock(part.id, quantity)
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = part.id,
                    workOrderId = null,
                    transactionType = "Receive",
                    quantity = quantity,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "Manual receipt from mobile inventory screen."
                )
            )
            recordAudit("Receive", "Inventory", "استلام $quantity من ${part.partNumber}", actor)
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
                password = "1234"
            )
        )
        recordAudit("Create", "User", "إضافة فني جديد tech$number", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Assets
    // ---------------------------------------------------------------------

    suspend fun saveAsset(asset: AssetEntity, actor: String = "System") {
        val isNew = asset.id == 0L
        assetDao.insertAsset(asset)
        recordAudit(if (isNew) "Create" else "Update", "Asset", "${if (isNew) "إضافة" else "تعديل"} أصل: ${asset.code}", actor)
    }

    suspend fun deleteAsset(asset: AssetEntity, actor: String = "System") {
        assetDao.deleteById(asset.id)
        recordAudit("Delete", "Asset", "حذف أصل: ${asset.code}", actor)
    }

    suspend fun changeAssetStatus(asset: AssetEntity, status: String, actor: String = "System") {
        assetDao.insertAsset(asset.copy(status = status))
        recordAudit("Status", "Asset", "تغيير حالة ${asset.code} من ${asset.status} إلى $status", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Spare parts
    // ---------------------------------------------------------------------

    suspend fun savePart(part: SparePartEntity, actor: String = "System") {
        val isNew = part.id == 0L
        sparePartDao.insert(part)
        recordAudit(if (isNew) "Create" else "Update", "Inventory", "${if (isNew) "إضافة" else "تعديل"} قطعة: ${part.partNumber}", actor)
    }

    suspend fun deletePart(part: SparePartEntity, actor: String = "System") {
        sparePartDao.deleteById(part.id)
        recordAudit("Delete", "Inventory", "حذف قطعة: ${part.partNumber}", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Work orders (edit / delete)
    // ---------------------------------------------------------------------

    suspend fun saveWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
        val isNew = workOrder.id == 0L
        workOrderDao.insertWorkOrder(workOrder)
        recordAudit(if (isNew) "Create" else "Update", "WorkOrder", "${if (isNew) "إنشاء" else "تعديل"} أمر عمل: ${workOrder.title}", actor)
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
        userDao.insert(user)
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

    suspend fun saveBomItem(item: AssetBomItemEntity, actor: String = "System") {
        val isNew = item.id == 0L
        bomDao.insert(item)
        recordAudit(if (isNew) "Create" else "Update", "BOM", "${if (isNew) "إضافة" else "تعديل"} بند مكوّنات (قطعة #${item.partId})", actor)
    }

    suspend fun deleteBomItem(item: AssetBomItemEntity, actor: String = "System") {
        bomDao.deleteById(item.id)
        recordAudit("Delete", "BOM", "حذف بند مكوّنات (قطعة #${item.partId})", actor)
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
}

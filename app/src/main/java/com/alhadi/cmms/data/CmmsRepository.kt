package com.alhadi.cmms.data

import androidx.room.withTransaction
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
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

    val assets: Flow<List<AssetEntity>> = assetDao.observeAssets()
    val workOrders: Flow<List<WorkOrderEntity>> = workOrderDao.observeWorkOrders()
    val preventiveMaintenance: Flow<List<PreventiveMaintenanceEntity>> = pmDao.observePreventiveMaintenance()
    val spareParts: Flow<List<SparePartEntity>> = sparePartDao.observeSpareParts()
    val recentTransactions: Flow<List<InventoryTransactionEntity>> = transactionDao.observeRecentTransactions()
    val users: Flow<List<UserEntity>> = userDao.observeUsers()
    val currentUser: Flow<UserEntity?> = userDao.observeCurrentUser()
    val auditLog: Flow<List<AuditLogEntity>> = auditLogDao.observeRecent()

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
                AssetEntity(5, "SG-014", "Silo Gate 14", "Silo Gates", "Silo Bottom", "Custom", "Slide-400", "Running", "Medium", "2022-01-18", DateStrings.daysFromToday(-12)),
                AssetEntity(6, "SN-400", "Level Sensor 400", "Sensors", "Flour Bin", "Siemens", "SITRANS", "Warning", "High", "2023-03-21", DateStrings.daysFromToday(-2)),
                AssetEntity(7, "RM-01", "Rollermill C1", "Rollermills", "Milling Floor", "Buhler", "Antares", "Running", "Critical", "2017-09-10", DateStrings.daysFromToday(-7)),
                AssetEntity(8, "PL-04", "Plansifter 4", "Plansifters", "Milling Floor", "Buhler", "MPA", "Running", "Critical", "2017-10-02", DateStrings.daysFromToday(-21)),
                AssetEntity(9, "PR-02", "Purifier 2", "Purifiers", "Milling Floor", "Golfetto", "Pur-2", "Running", "High", "2018-04-19", DateStrings.daysFromToday(-16)),
                AssetEntity(10, "CP-01", "Air Compressor 1", "Compressors", "Utility Room", "Atlas Copco", "GA-37", "Running", "Critical", "2016-12-12", DateStrings.daysFromToday(-8)),
                AssetEntity(11, "PK-01", "Packing Machine 1", "Packing Machines", "Packing Hall", "Fawema", "FA-10", "Warning", "High", "2020-07-07", DateStrings.daysFromToday(-31)),
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
                WorkOrderEntity(2, 4, "Silo fan not starting", "Check overload, contactor, motor insulation, and fan impeller.", "High", "In Progress", "Electrical Technician", DateStrings.daysFromToday(-1), today, 250.0),
                WorkOrderEntity(3, 11, "Packing machine bag sensor alarm", "Clean and align sensor, verify signal in control panel.", "Medium", "Open", "Electrical Technician", today, DateStrings.daysFromToday(2), 45.0),
                WorkOrderEntity(4, 2, "Chain conveyor lubrication", "Lubricate chain and inspect tension.", "Low", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-7), DateStrings.daysFromToday(-5), 30.0, "Completed and tested.")
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

            assetDao.insertAssets(assets)
            userDao.insertAll(users)
            workOrderDao.insertWorkOrders(workOrders)
            pmDao.insertAll(preventiveMaintenance)
            sparePartDao.insertAll(spareParts)
            transactionDao.insertAll(transactions)
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
}

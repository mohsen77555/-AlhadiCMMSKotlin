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
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

    // ---------------------------------------------------------------------
    // Work orders
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.createWorkOrder(
        assetId: Long,
        title: String,
        description: String,
        priority: String,
        assignedTo: String,
        dueAt: String,
        estimatedCost: Double,
        actor: String,
        type: String = "Corrective"
    ) {
        val now = DateStrings.today()
        val asset = assetDao.getAssetById(assetId)
        // WO-AST-003/004: retired/disposed assets cannot receive new work orders.
        val assetStatus = asset?.status?.lowercase().orEmpty()
        if (assetStatus == "retired" || assetStatus == "disposed") {
            throw IllegalStateException(
                if (assetStatus == "disposed") "الأصل مُستبعَد — لا يمكن إنشاء أوامر عمل جديدة عليه"
                else "الأصل متقاعد — لا يمكن إنشاء أوامر عمل جديدة عليه"
            )
        }
        val order = WorkOrderEntity(
            assetId = assetId,
            title = title.ifBlank { "Maintenance request" },
            description = description,
            priority = priority,
            status = "Open",
            assignedTo = assignedTo,
            createdAt = now,
            dueAt = dueAt,
            estimatedCost = estimatedCost,
            type = type,
            approvalStatus = if (priority == "Critical" || estimatedCost >= WorkOrderEntity.APPROVAL_THRESHOLD) "Pending" else "NotRequired"
        ).inheritOrgFrom(asset)
        workOrderDao.insertWorkOrder(order)
        recordAudit("Create", "WorkOrder", "إنشاء أمر عمل: $title", actor)
    }

internal suspend fun CmmsRepository.createDemoWorkOrder(assetId: Long = 7, actor: String = "System") {
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

internal suspend fun CmmsRepository.updateWorkOrderStatus(id: Long, status: String, actor: String = "System") {
        val current = workOrderDao.getById(id)
        // WO-STAT-008: a cancelled order cannot be executed or transitioned further.
        if (current != null && current.status.equals("Cancelled", ignoreCase = true) && !status.equals("Cancelled", ignoreCase = true)) {
            throw IllegalStateException("أمر العمل ملغى — لا يمكن تنفيذه")
        }
        // WO-STAT-003: a draft must be released (-> Open) before any execution.
        if (current != null && current.status.equals("Draft", ignoreCase = true) &&
            status in listOf("In Progress", "Technically Completed", "Closed")
        ) {
            throw IllegalStateException("أطلق الأمر (Release) قبل بدء التنفيذ")
        }
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
        // WO-CLS-003: cannot close while issued materials are not settled into the order cost.
        if (status == "Closed" && current != null && current.partsCost <= 0.0 && transactionsForOrder(id).isNotEmpty()) {
            throw IllegalStateException("سوِّ تكاليف المواد المصروفة (سجّل تكلفة القطع) قبل إغلاق الأمر")
        }
        // WO-CLS-005/006: stamp who closed the order and when.
        if (status == "Closed" && current != null) {
            workOrderDao.insertWorkOrder(current.copy(status = status, closedAt = DateStrings.today(), closedBy = actor))
        } else {
            workOrderDao.updateStatus(id, status)
        }
        // WO-HIS-001..004 & WO-STAT-006: record the status transition (old -> new).
        recordWoHistory(id, "status", current?.status ?: "", status, actor)
        recordAudit("Update", "WorkOrder", "تحديث حالة أمر العمل #$id إلى $status", actor)
    }

    // ---------------------------------------------------------------------
    // Preventive maintenance
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.markPreventiveMaintenanceDone(item: PreventiveMaintenanceEntity, actor: String = "System") {
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

internal suspend fun CmmsRepository.saveSerialProfile(profile: SerialNumberProfileEntity, actor: String = "System") =
        serialService.saveProfile(profile, actor)

internal suspend fun CmmsRepository.deleteSerialProfile(profile: SerialNumberProfileEntity, actor: String = "System") =
        serialService.deleteProfile(profile, actor)

internal suspend fun CmmsRepository.createSerialMaster(request: SerialMasterRequest, actor: String = "System") =
        serialService.createMaster(request, actor)

internal suspend fun CmmsRepository.receiveSerializedPart(request: SerializedReceiptRequest, actor: String = "System") =
        serialService.receive(request, actor)

internal suspend fun CmmsRepository.issueSerializedPart(request: SerializedIssueRequest, actor: String = "System") =
        serialService.issue(request, actor)

internal suspend fun CmmsRepository.transferSerialNumber(request: SerialTransferRequest, actor: String = "System") =
        serialService.transfer(request, actor)

internal suspend fun CmmsRepository.installSerialNumber(request: SerialInstallRequest, actor: String = "System") =
        serialService.install(request, actor)

internal suspend fun CmmsRepository.dismantleSerialNumber(serialId: Long, note: String = "", actor: String = "System") =
        serialService.dismantle(serialId, note, actor)

internal suspend fun CmmsRepository.reconcileSerializedStock(partId: Long, actor: String = "System") =
        serialService.reconcileStock(partId, actor)

internal suspend fun CmmsRepository.deleteSerialNumber(serial: SerialNumberEntity, actor: String = "System") =
        serialService.deleteSerial(serial, actor)


    // ---------------------------------------------------------------------
    // Inventory
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.issuePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {
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

internal suspend fun CmmsRepository.issuePartToWorkOrder(order: WorkOrderEntity, part: SparePartEntity, quantity: Int, actor: String = "System") {
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

internal suspend fun CmmsRepository.receivePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {
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

/**
 * INV-CNT-001: physical stock count (جرد). Sets the on-hand quantity to the counted value and
 * records the difference as an Adjustment inventory transaction so the variance is auditable.
 */
internal suspend fun CmmsRepository.cycleCountPart(part: SparePartEntity, countedQty: Int, actor: String = "System") {
        require(countedQty >= 0) { "الكمية لا يمكن أن تكون سالبة" }
        val current = sparePartDao.getById(part.id) ?: throw IllegalStateException("قطعة الغيار غير موجودة")
        if (current.serializationActive) throw IllegalStateException("استخدم شاشة الأرقام التسلسلية لجرد هذه القطعة")
        val delta = countedQty - current.onHandQty
        if (delta == 0) return
        database.withTransaction {
            sparePartDao.setStock(current.id, countedQty)
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = current.id,
                    workOrderId = null,
                    transactionType = "Adjustment",
                    quantity = delta,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "جرد: من ${current.onHandQty} إلى $countedQty"
                )
            )
            recordAudit("Adjust", "Inventory", "جرد ${current.partNumber}: ${current.onHandQty} ← $countedQty", actor)
        }
    }

    // ---------------------------------------------------------------------
    // Users
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.addTechnician(actor: String = "System") {
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



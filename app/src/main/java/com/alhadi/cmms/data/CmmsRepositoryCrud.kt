package com.alhadi.cmms.data

import androidx.room.withTransaction
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetInstallationEntity
import com.alhadi.cmms.data.entity.AssetStatusHistoryEntity
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
import com.alhadi.cmms.data.entity.WorkOrderMaterialEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

    // ---------------------------------------------------------------------
    // CRUD — Assets
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveAsset(asset: AssetEntity, actor: String = "System") {
        validateLinearAssetMaster(asset)
        enforceSingleInstallation(asset)
        val isNew = asset.id == 0L
        val previous = if (isNew) null else assetDao.getAssetById(asset.id)
        val savedId = assetDao.insertAsset(asset)
        val effectiveId = if (isNew) savedId else asset.id
        recordInstallationChange(previous, asset.copy(id = effectiveId), actor)
        recordAudit(if (isNew) "Create" else "Update", "Asset", "${if (isNew) "إضافة" else "تعديل"} أصل: ${asset.code}", actor)
    }

/**
 * Install/dismantle history: whenever an asset's functional location changes, log a Dismantle
 * event for the old location and an Install event for the new one (immutable timeline).
 */
private suspend fun CmmsRepository.recordInstallationChange(
    previous: AssetEntity?,
    current: AssetEntity,
    actor: String
) {
    val oldLocation = previous?.locationId
    val newLocation = current.locationId
    if (oldLocation == newLocation) return
    val today = DateStrings.today()
    if (oldLocation != null) {
        assetInstallationDao.insert(
            AssetInstallationEntity(
                assetId = current.id,
                locationId = oldLocation,
                locationCode = locationDao.getById(oldLocation)?.code ?: "",
                eventType = "Dismantle",
                eventDate = today,
                performedBy = actor,
                reason = "نقل الأصل ${current.code}",
                createdAt = today
            )
        )
    }
    if (newLocation != null) {
        assetInstallationDao.insert(
            AssetInstallationEntity(
                assetId = current.id,
                locationId = newLocation,
                locationCode = locationDao.getById(newLocation)?.code ?: "",
                eventType = "Install",
                eventDate = today,
                performedBy = actor,
                reason = "تركيب الأصل ${current.code}",
                createdAt = today
            )
        )
    }
}

/**
 * FLOC-031: a functional location flagged as a single-installation position may hold only one
 * active asset at a time. An asset that is Retired/Disposed no longer occupies the position.
 */
private suspend fun CmmsRepository.enforceSingleInstallation(asset: AssetEntity) {
    val locationId = asset.locationId ?: return
    if (asset.status == "Retired" || asset.status == "Disposed") return
    val location = locationDao.getById(locationId) ?: return
    if (!location.singleInstallation) return
    val occupied = assetDao.assetsAtLocation(locationId).any { other ->
        other.id != asset.id && other.status != "Retired" && other.status != "Disposed"
    }
    if (occupied) {
        throw IllegalStateException("الموقع ${location.code} موضع تركيب مفرد ويحتوي أصلاً نشطاً بالفعل")
    }
}

internal suspend fun CmmsRepository.deleteAsset(asset: AssetEntity, actor: String = "System") {
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

internal suspend fun CmmsRepository.changeAssetStatus(asset: AssetEntity, status: String, reason: String = "", actor: String = "System") {
        if (status == asset.status) return
        // EQ-LIFE-010: only governed lifecycle transitions are permitted.
        if (!AssetLifecycle.canTransition(asset.status, status)) {
            throw IllegalStateException("انتقال غير مسموح: من ${asset.status} إلى $status")
        }
        database.withTransaction {
            assetDao.insertAsset(asset.copy(status = status))
            assetStatusHistoryDao.insert(
                AssetStatusHistoryEntity(
                    assetId = asset.id,
                    fromStatus = asset.status,
                    toStatus = status,
                    reason = reason,
                    changedBy = actor,
                    changedAt = DateStrings.today()
                )
            )
            val reasonSuffix = if (reason.isNotBlank()) " — السبب: $reason" else ""
            recordAudit("Status", "Asset", "تغيير حالة ${asset.code} من ${asset.status} إلى $status$reasonSuffix", actor)
        }
    }

    // ---------------------------------------------------------------------
    // CRUD — Spare parts
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.savePart(part: SparePartEntity, actor: String = "System") {
        serialService.validatePartChange(part)
        val isNew = part.id == 0L
        sparePartDao.insert(part)
        recordAudit(if (isNew) "Create" else "Update", "Inventory", "${if (isNew) "إضافة" else "تعديل"} قطعة: ${part.partNumber}", actor)
    }

internal suspend fun CmmsRepository.deletePart(part: SparePartEntity, actor: String = "System") {
        serialService.ensurePartDeletable(part.id)
        sparePartDao.deleteById(part.id)
        recordAudit("Delete", "Inventory", "حذف قطعة: ${part.partNumber}", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Work orders (edit / delete)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
        validateLinearMaintenanceReference(
            workOrder.assetId,
            workOrder.linearStartPoint,
            workOrder.linearEndPoint,
            workOrder.linearMarker,
            workOrder.linearHorizontalOffset,
            workOrder.linearVerticalOffset
        )
        // WO-LAB-003: no negative hours/cost.
        require(workOrder.laborHours >= 0.0 && workOrder.laborRate >= 0.0 && workOrder.partsCost >= 0.0 && workOrder.downtimeHours >= 0.0) {
            "لا يسمح بقيم سالبة للساعات أو التكلفة"
        }
        val isNew = workOrder.id == 0L
        val asset = assetDao.getAssetById(workOrder.assetId)
        // WO-AST-003/004: new orders are not allowed on retired/disposed assets.
        if (isNew) {
            val assetStatus = asset?.status?.lowercase().orEmpty()
            if (assetStatus == "retired" || assetStatus == "disposed") {
                throw IllegalStateException(
                    if (assetStatus == "disposed") "الأصل مُستبعَد — لا يمكن إنشاء أوامر عمل جديدة عليه"
                    else "الأصل متقاعد — لا يمكن إنشاء أوامر عمل جديدة عليه"
                )
            }
        }
        // WO-ORG-001..008 & WO-AST-006/007: inherit the org + asset snapshot on creation only
        // (WO-AST-008: later asset moves must not rewrite the historical order).
        val inherited = if (isNew) workOrder.inheritOrgFrom(asset) else workOrder
        // Keep an existing decision; otherwise (re)derive whether sign-off is needed.
        val toSave = if (inherited.approvalStatus == "Approved" || inherited.approvalStatus == "Rejected") {
            inherited
        } else {
            inherited.copy(approvalStatus = if (inherited.needsApproval()) "Pending" else "NotRequired")
        }
        workOrderDao.insertWorkOrder(toSave)
        recordAudit(if (isNew) "Create" else "Update", "WorkOrder", "${if (isNew) "إنشاء" else "تعديل"} أمر عمل: ${workOrder.title}", actor)
        // AST-WAR-010: persist the warranty decision/review outcome into the asset history.
        if (workOrder.repairType.isNotBlank()) {
            val assetCode = asset?.code ?: workOrder.assetId.toString()
            val decision = when (workOrder.repairType) {
                "Internal" -> "إصلاح داخلي"
                "WarrantyClaim" -> "مطالبة ضمان"
                else -> workOrder.repairType
            }
            val review = if (workOrder.warrantyReviewed) {
                "تمت مراجعة الضمان" + workOrder.warrantyReviewResult.takeIf { it.isNotBlank() }?.let { ": $it" }.orEmpty()
            } else {
                "بدون مراجعة ضمان"
            }
            recordAudit("Warranty", "Asset", "قرار ضمان على الأصل $assetCode بأمر «${workOrder.title}»: $decision — $review", actor)
        }
    }

internal suspend fun CmmsRepository.setWorkOrderApproval(workOrder: WorkOrderEntity, approved: Boolean, actor: String = "System") {
        val status = if (approved) "Approved" else "Rejected"
        workOrderDao.insertWorkOrder(workOrder.copy(approvalStatus = status, approvedBy = actor))
        recordWoHistory(workOrder.id, "approvalStatus", workOrder.approvalStatus, status, actor)
        recordAudit("Approval", "WorkOrder", "${if (approved) "اعتماد" else "رفض"} أمر العمل: ${workOrder.title}", actor)
    }

internal suspend fun CmmsRepository.deleteWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
        // WO-GOV-003/004 & WO-LC-005/006: work orders are never hard-deleted — cancel and keep the record.
        val current = workOrderDao.getById(workOrder.id) ?: workOrder
        workOrderDao.insertWorkOrder(current.copy(status = "Cancelled", cancelledReason = current.cancelledReason.ifBlank { "أُلغي بدل الحذف" }))
        recordWoHistory(workOrder.id, "status", current.status, "Cancelled", actor)
        recordAudit("Cancel", "WorkOrder", "إلغاء أمر عمل: ${workOrder.title}", actor)
    }

/** WO-GOV-004: cancel a work order with a reason instead of deleting it. */
internal suspend fun CmmsRepository.cancelWorkOrder(workOrder: WorkOrderEntity, reason: String, actor: String = "System") {
    val current = workOrderDao.getById(workOrder.id) ?: workOrder
    workOrderDao.insertWorkOrder(current.copy(status = "Cancelled", cancelledReason = reason.ifBlank { "ملغى" }))
    recordWoHistory(workOrder.id, "status", current.status, "Cancelled", actor)
    recordAudit("Cancel", "WorkOrder", "إلغاء أمر عمل: ${workOrder.title} — ${reason.ifBlank { "بدون سبب" }}", actor)
}

/** WO-CLS-008: reopen a closed/cancelled order (Admin only, enforced in the UI). */
internal suspend fun CmmsRepository.reopenWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
    val current = workOrderDao.getById(workOrder.id) ?: workOrder
    workOrderDao.insertWorkOrder(current.copy(status = "In Progress", closedAt = "", closedBy = ""))
    recordWoHistory(workOrder.id, "status", current.status, "In Progress", actor)
    recordAudit("Reopen", "WorkOrder", "إعادة فتح أمر عمل: ${workOrder.title}", actor)
}

/** Snapshots the asset's organizational + identity data onto a new work order (WO-ORG-001..008 / WO-AST-006/007). */
internal fun WorkOrderEntity.inheritOrgFrom(asset: AssetEntity?): WorkOrderEntity =
    if (asset == null) this else copy(
        assetCode = asset.code,
        assetName = asset.name,
        functionalLocation = asset.location,
        companyCode = asset.company,
        siteCode = asset.site,
        plantCode = asset.maintenancePlant,
        maintenancePlantCode = asset.maintenancePlant,
        planningPlantCode = asset.planningPlant,
        plannerGroup = asset.plannerGroup,
        workCenter = asset.mainWorkCenter,
        costCenter = asset.costCenter
    )

    // ---------------------------------------------------------------------
    // CRUD — Preventive maintenance
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.savePreventiveMaintenance(item: PreventiveMaintenanceEntity, actor: String = "System") {
        val isNew = item.id == 0L
        pmDao.insert(item)
        recordAudit(if (isNew) "Create" else "Update", "PreventiveMaintenance", "${if (isNew) "إضافة" else "تعديل"} صيانة دورية: ${item.title}", actor)
    }

internal suspend fun CmmsRepository.deletePreventiveMaintenance(item: PreventiveMaintenanceEntity, actor: String = "System") {
        pmDao.deleteById(item.id)
        recordAudit("Delete", "PreventiveMaintenance", "حذف صيانة دورية: ${item.title}", actor)
    }

    // ---------------------------------------------------------------------
    // CRUD — Users
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveUser(user: UserEntity, actor: String = "System") {
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
        // USR-LOG-001: stamp account creation and password-change times.
        val now = DateStrings.now()
        val passwordProvided = user.password.isNotBlank()
        val stamped = resolved.copy(
            createdAt = if (isNew) now else resolved.createdAt.ifBlank { now },
            passwordChangedAt = if (isNew || passwordProvided) now else resolved.passwordChangedAt,
            mustChangePassword = if (passwordProvided) false else resolved.mustChangePassword
        )
        userDao.insert(stamped)
        recordAudit(if (isNew) "Create" else "Update", "User", "${if (isNew) "إضافة" else "تعديل"} مستخدم: ${user.username}", actor)
    }

/** USR-GOV-002: an inactive account is preserved; the last active admin cannot be deactivated. */
internal suspend fun CmmsRepository.setUserActive(user: UserEntity, active: Boolean, actor: String = "System") {
        if (!active && user.isAdmin && userDao.countActiveAdmins() <= 1) {
            throw IllegalStateException("لا يمكن تعطيل آخر مدير نشط")
        }
        userDao.setActive(user.id, active)
        recordAudit("Update", "User", "${if (active) "تفعيل" else "تعطيل"} المستخدم: ${user.username}", actor)
    }

internal suspend fun CmmsRepository.deleteUser(user: UserEntity, actor: String = "System") {
        if (user.isAdmin && user.isActive && userDao.countActiveAdmins() <= 1) {
            throw IllegalStateException("لا يمكن حذف آخر مدير نشط")
        }
        userDao.deleteById(user.id)
        recordAudit("Delete", "User", "حذف المستخدم: ${user.username}", actor)
    }

/** USR-SEC-005: an admin clears a lockout and resets the failed-attempt counter. */
internal suspend fun CmmsRepository.resetUserLock(user: UserEntity, actor: String = "System") {
        userDao.insert(user.copy(locked = false, failedLoginCount = 0))
        recordAudit("Unlock", "User", "إلغاء قفل المستخدم: ${user.username}", actor)
    }

    // ---------------------------------------------------------------------
    // Meters & readings
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveMeasuringPoint(point: MeasuringPointEntity, actor: String = "System") {
        val isNew = point.id == 0L
        measurementDao.insertPoint(point)
        recordAudit(if (isNew) "Create" else "Update", "Meter", "${if (isNew) "إضافة" else "تعديل"} نقطة قياس: ${point.name}", actor)
    }

internal suspend fun CmmsRepository.deleteMeasuringPoint(point: MeasuringPointEntity, actor: String = "System") {
        measurementDao.deletePointById(point.id)
        recordAudit("Delete", "Meter", "حذف نقطة قياس: ${point.name}", actor)
    }

    /**
     * Records a reading. Returns an optional warning message (counter decrease /
     * over-limit) for the UI to surface. Cumulative counters may not decrease.
     */
internal suspend fun CmmsRepository.addReading(point: MeasuringPointEntity, value: Double, note: String, actor: String = "System"): String? {
        if (point.isCounter && value < point.lastReading) {
            return "لا يمكن أن تقل قراءة العداد التراكمي عن ${point.lastReading}"
        }
        val now = DateStrings.now()
        val status = point.readingStatus(value)
        database.withTransaction {
            measurementDao.insertReading(
                MeasurementReadingEntity(
                    pointId = point.id,
                    assetId = point.assetId,
                    value = value,
                    createdAt = now,
                    createdBy = actor,
                    note = note,
                    status = status
                )
            )
            measurementDao.updateLastReading(point.id, value, now)
            recordAudit("Reading", "Meter", "قراءة ${point.name}: $value ${point.unit} ($status)", actor)
            // MEAS-ALM-010: an alarm breach can raise a maintenance notification automatically.
            if (status == "Alarm" && point.autoNotifyOnAlarm) {
                notificationDao.insert(
                    MaintenanceNotificationEntity(
                        number = "MTR-${point.id}-${System.currentTimeMillis()}",
                        type = "Condition",
                        title = "تجاوز حد القياس: ${point.name}",
                        description = "سُجّلت قراءة $value ${point.unit} خارج الحدود المسموحة للأصل.",
                        assetId = point.assetId,
                        priority = "High",
                        reportedBy = actor,
                        reportedAt = now,
                        status = "Open"
                    )
                )
                recordAudit("Generate", "Notification", "بلاغ تلقائي من تجاوز قياس ${point.name}", actor)
            }
        }
        return when (status) {
            "Alarm" -> "إنذار: القراءة $value ${point.unit} خارج الحدود المسموحة"
            "Warning" -> "تحذير: القراءة $value ${point.unit} قريبة من الحد"
            else -> null
        }
    }

    // ---------------------------------------------------------------------
    // Functional locations
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveFunctionalLocation(location: FunctionalLocationEntity, actor: String = "System") {
        val isNew = location.id == 0L
        locationDao.insert(location)
        recordAudit(if (isNew) "Create" else "Update", "Location", "${if (isNew) "إضافة" else "تعديل"} موقع فني: ${location.code}", actor)
    }

internal suspend fun CmmsRepository.deleteFunctionalLocation(location: FunctionalLocationEntity, actor: String = "System") {
        locationDao.deleteById(location.id)
        recordAudit("Delete", "Location", "حذف موقع فني: ${location.code}", actor)
    }

    // ---------------------------------------------------------------------
    // Warehouses (stores)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveWarehouse(warehouse: WarehouseEntity, actor: String = "System") {
        val isNew = warehouse.id == 0L
        warehouseDao.insert(warehouse)
        recordAudit(if (isNew) "Create" else "Update", "Warehouse", "${if (isNew) "إضافة" else "تعديل"} مستودع: ${warehouse.code}", actor)
    }

internal suspend fun CmmsRepository.deleteWarehouse(warehouse: WarehouseEntity, actor: String = "System") {
        warehouseDao.deleteById(warehouse.id)
        recordAudit("Delete", "Warehouse", "حذف مستودع: ${warehouse.code}", actor)
    }

internal suspend fun CmmsRepository.saveSupplier(supplier: SupplierEntity, actor: String = "System") {
    val isNew = supplier.id == 0L
    supplierDao.insert(supplier)
    recordAudit(if (isNew) "Create" else "Update", "Supplier", "${if (isNew) "إضافة" else "تعديل"} مورّد: ${supplier.code}", actor)
}

internal suspend fun CmmsRepository.deleteSupplier(supplier: SupplierEntity, actor: String = "System") {
    supplierDao.deleteById(supplier.id)
    recordAudit("Delete", "Supplier", "حذف مورّد: ${supplier.code}", actor)
}

// --- Purchase orders ---

private suspend fun CmmsRepository.recalcPoTotal(poId: Long) {
    val total = purchaseOrderLineDao.forOrder(poId).sumOf { it.lineTotal }
    purchaseOrderDao.updateTotal(poId, total)
}

internal suspend fun CmmsRepository.savePurchaseOrder(order: PurchaseOrderEntity, actor: String = "System") {
    val isNew = order.id == 0L
    val supplierName = supplierDao.dumpAll().firstOrNull { it.id == order.supplierId }?.name ?: order.supplierName
    val toSave = order.copy(
        supplierName = supplierName,
        createdBy = if (isNew) actor else order.createdBy
    )
    val id = purchaseOrderDao.insert(toSave)
    if (!isNew) recalcPoTotal(order.id)
    recordAudit(if (isNew) "Create" else "Update", "PurchaseOrder", "${if (isNew) "إنشاء" else "تعديل"} أمر شراء: ${order.poNumber}", actor)
}

/** Procurement governance: orders are cancelled, never hard-deleted. */
internal suspend fun CmmsRepository.cancelPurchaseOrder(order: PurchaseOrderEntity, reason: String, actor: String = "System") {
    val current = purchaseOrderDao.getById(order.id) ?: order
    purchaseOrderDao.insert(current.copy(status = "Cancelled", cancelledReason = reason.ifBlank { "ملغى" }))
    recordAudit("Cancel", "PurchaseOrder", "إلغاء أمر شراء: ${order.poNumber}", actor)
}

internal suspend fun CmmsRepository.setPurchaseOrderStatus(order: PurchaseOrderEntity, status: String, actor: String = "System") {
    val current = purchaseOrderDao.getById(order.id) ?: order
    val stamped = if (status == "Approved") current.copy(status = status, approvedBy = actor) else current.copy(status = status)
    purchaseOrderDao.insert(stamped)
    recordAudit("Update", "PurchaseOrder", "تحديث حالة أمر الشراء ${order.poNumber} إلى $status", actor)
}

internal suspend fun CmmsRepository.savePurchaseOrderLine(line: PurchaseOrderLineEntity, actor: String = "System") {
    purchaseOrderLineDao.insert(line)
    recalcPoTotal(line.poId)
    recordAudit("Update", "PurchaseOrder", "بند شراء: ${line.description}", actor)
}

internal suspend fun CmmsRepository.deletePurchaseOrderLine(line: PurchaseOrderLineEntity, actor: String = "System") {
    purchaseOrderLineDao.deleteById(line.id)
    recalcPoTotal(line.poId)
    recordAudit("Update", "PurchaseOrder", "حذف بند شراء: ${line.description}", actor)
}

/**
 * Goods receipt: receives [quantity] units against a purchase-order line.
 * - Increments the line's receivedQty.
 * - If the line is linked to a stock part, raises on-hand stock, refreshes the last price,
 *   and records an inventory "Receive" transaction referencing the PO.
 * - Re-derives the order status (PartiallyReceived / Received) from its lines.
 * Everything runs in a single DB transaction so stock and the line stay consistent.
 */
internal suspend fun CmmsRepository.receivePurchaseOrderLine(
    line: PurchaseOrderLineEntity,
    quantity: Int,
    actor: String = "System"
) {
    require(quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
    database.withTransaction {
        val order = purchaseOrderDao.getById(line.poId)
            ?: throw IllegalStateException("أمر الشراء غير موجود")
        if (order.status == "Cancelled") throw IllegalStateException("أمر الشراء ملغى")
        val current = purchaseOrderLineDao.forOrder(line.poId).firstOrNull { it.id == line.id }
            ?: throw IllegalStateException("بند الشراء غير موجود")
        val remaining = current.quantity - current.receivedQty
        if (remaining <= 0) throw IllegalStateException("تم استلام هذا البند بالكامل")
        val receiveQty = quantity.coerceAtMost(remaining)

        // 1) advance the line
        purchaseOrderLineDao.insert(current.copy(receivedQty = current.receivedQty + receiveQty))

        // 2) raise stock + record inventory movement when linked to a part
        current.partId?.let { partId ->
            val part = sparePartDao.getById(partId)
            if (part != null) {
                sparePartDao.adjustStock(partId, receiveQty)
                if (current.unitPrice > 0) sparePartDao.updateLastPrice(partId, current.unitPrice)
                transactionDao.insert(
                    InventoryTransactionEntity(
                        partId = partId,
                        workOrderId = null,
                        transactionType = "Receive",
                        quantity = receiveQty,
                        createdAt = DateStrings.today(),
                        createdBy = actor,
                        note = "استلام أمر شراء ${order.poNumber}",
                        storageLocation = order.warehouse
                    )
                )
            }
        }

        // 3) re-derive the order status from its lines
        val lines = purchaseOrderLineDao.forOrder(line.poId)
        val allReceived = lines.isNotEmpty() && lines.all { it.isFullyReceived }
        val anyReceived = lines.any { it.receivedQty > 0 }
        val newStatus = when {
            allReceived -> "Received"
            anyReceived -> "PartiallyReceived"
            else -> order.status
        }
        if (newStatus != order.status) purchaseOrderDao.insert(order.copy(status = newStatus))

        recordAudit(
            "Receive",
            "PurchaseOrder",
            "استلام $receiveQty من ${current.description} (أمر ${order.poNumber})",
            actor
        )
    }
}

/**
 * INV-REO-010: automatic replenishment. Scans for low-stock parts that have a preferred supplier
 * and a positive suggested order quantity, then raises one draft purchase order per supplier with
 * a line for each part. Returns the number of purchase orders created.
 */
internal suspend fun CmmsRepository.createReorderPurchaseOrders(actor: String = "System"): Int {
    val lowStock = spareParts.first().filter {
        it.isLowStock && it.preferredSupplierId != null && it.suggestedOrderQty() > 0
    }
    if (lowStock.isEmpty()) return 0
    val suppliersById = supplierDao.dumpAll().associateBy { it.id }
    val today = DateStrings.today()
    var created = 0
    database.withTransaction {
        lowStock.groupBy { it.preferredSupplierId!! }.forEach { (supplierId, parts) ->
            val supplier = suppliersById[supplierId]
            val poId = purchaseOrderDao.insert(
                PurchaseOrderEntity(
                    poNumber = "PO-RO-${today.replace("-", "")}-$supplierId",
                    supplierId = supplierId,
                    supplierName = supplier?.name ?: "",
                    status = "Draft",
                    orderDate = today,
                    currency = "SAR",
                    notes = "أمر شراء تلقائي للنواقص",
                    createdBy = actor
                )
            )
            var total = 0.0
            parts.forEach { p ->
                val qty = p.suggestedOrderQty()
                purchaseOrderLineDao.insert(
                    PurchaseOrderLineEntity(
                        poId = poId,
                        partId = p.id,
                        partNumber = p.partNumber,
                        description = p.name,
                        quantity = qty,
                        unitPrice = p.lastPrice,
                        receivedQty = 0
                    )
                )
                total += qty * p.lastPrice
            }
            purchaseOrderDao.updateTotal(poId, total)
            created++
        }
        recordAudit("Create", "PurchaseOrder", "إنشاء $created أمر شراء تلقائي للنواقص", actor)
    }
    return created
}

// --- Work-order material planning (WO-MAT-*) ---

internal suspend fun CmmsRepository.savePlannedMaterial(material: WorkOrderMaterialEntity, actor: String = "System") {
    workOrderMaterialDao.insert(material)
    recordAudit("Update", "WorkOrder", "مادة مخطّطة: ${material.description}", actor)
}

internal suspend fun CmmsRepository.deletePlannedMaterial(material: WorkOrderMaterialEntity, actor: String = "System") {
    workOrderMaterialDao.deleteById(material.id)
    recordAudit("Update", "WorkOrder", "حذف مادة مخطّطة: ${material.description}", actor)
}

/**
 * WO-MAT-005: issue [quantity] of a planned material from stock against its work order.
 * Decrements inventory, records an Issue transaction, advances issuedQty and rolls the cost
 * into the order's partsCost — all atomically.
 */
internal suspend fun CmmsRepository.issuePlannedMaterial(
    material: WorkOrderMaterialEntity,
    quantity: Int,
    actor: String = "System"
) {
    require(quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
    database.withTransaction {
        val current = workOrderMaterialDao.getById(material.id)
            ?: throw IllegalStateException("المادة المخطّطة غير موجودة")
        val remaining = current.remainingQty
        if (remaining <= 0) throw IllegalStateException("تم صرف هذه المادة بالكامل")
        val issueQty = quantity.coerceAtMost(remaining)
        val partId = current.partId
            ?: throw IllegalStateException("هذه المادة غير مرتبطة بقطعة مخزون")
        val part = sparePartDao.getById(partId)
            ?: throw IllegalStateException("قطعة الغيار غير موجودة")
        if (part.serializationActive) throw IllegalStateException("استخدم شاشة الأرقام التسلسلية لصرف هذه القطعة")
        if (sparePartDao.adjustStockSafe(partId, -issueQty) == 0) {
            throw IllegalStateException("الكمية المطلوبة ($issueQty) أكبر من المتوفر (${part.onHandQty})")
        }
        transactionDao.insert(
            InventoryTransactionEntity(
                partId = partId,
                workOrderId = current.orderId,
                transactionType = "Issue",
                quantity = issueQty,
                createdAt = DateStrings.today(),
                createdBy = actor,
                note = "صرف مادة مخطّطة لأمر العمل #${current.orderId}"
            )
        )
        workOrderMaterialDao.insert(current.copy(issuedQty = current.issuedQty + issueQty))
        workOrderDao.getById(current.orderId)?.let { order ->
            workOrderDao.insertWorkOrder(order.copy(partsCost = order.partsCost + issueQty * part.lastPrice))
        }
        recordAudit("Issue", "WorkOrder", "صرف $issueQty من ${part.partNumber} لأمر العمل #${current.orderId}", actor)
    }
}

    // ---------------------------------------------------------------------
    // Organizational units (Company / Plant / Work Center / Cost Center / Planner Group / Department)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveOrgUnit(unit: OrgUnitEntity, actor: String = "System") {
        val isNew = unit.id == 0L
        orgUnitDao.insert(unit)
        recordAudit(if (isNew) "Create" else "Update", "OrgUnit", "${if (isNew) "إضافة" else "تعديل"} وحدة تنظيمية: ${unit.type}/${unit.code}", actor)
    }

internal suspend fun CmmsRepository.deleteOrgUnit(unit: OrgUnitEntity, actor: String = "System") {
        orgUnitDao.deleteById(unit.id)
        recordAudit("Delete", "OrgUnit", "حذف وحدة تنظيمية: ${unit.type}/${unit.code}", actor)
    }

    // ---------------------------------------------------------------------
    // CAPA (corrective / preventive actions)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveCapa(item: CapaEntity, actor: String = "System") {
        val isNew = item.id == 0L
        val toSave = if (isNew && item.code.isBlank()) {
            item.copy(code = "CAPA-%03d".format(capaDao.countOnce() + 1))
        } else {
            item
        }
        capaDao.insert(toSave)
        recordAudit(if (isNew) "Create" else "Update", "CAPA", "${if (isNew) "إنشاء" else "تعديل"} إجراء: ${toSave.title}", actor)
    }

internal suspend fun CmmsRepository.updateCapaStatus(item: CapaEntity, status: String, actor: String = "System") {
        capaDao.updateStatus(item.id, status)
        recordAudit("Update", "CAPA", "تحديث حالة الإجراء ${item.code} إلى $status", actor)
    }

internal suspend fun CmmsRepository.deleteCapa(item: CapaEntity, actor: String = "System") {
        capaDao.deleteById(item.id)
        recordAudit("Delete", "CAPA", "حذف إجراء: ${item.title}", actor)
    }

    // ---------------------------------------------------------------------
    // Asset documents
    // ---------------------------------------------------------------------


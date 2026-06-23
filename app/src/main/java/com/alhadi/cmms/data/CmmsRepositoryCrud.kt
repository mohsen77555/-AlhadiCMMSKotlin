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
    // CRUD — Assets
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveAsset(asset: AssetEntity, actor: String = "System") {
        validateLinearAssetMaster(asset)
        val isNew = asset.id == 0L
        assetDao.insertAsset(asset)
        recordAudit(if (isNew) "Create" else "Update", "Asset", "${if (isNew) "إضافة" else "تعديل"} أصل: ${asset.code}", actor)
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
        assetDao.insertAsset(asset.copy(status = status))
        val reasonSuffix = if (reason.isNotBlank()) " — السبب: $reason" else ""
        recordAudit("Status", "Asset", "تغيير حالة ${asset.code} من ${asset.status} إلى $status$reasonSuffix", actor)
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
        val isNew = workOrder.id == 0L
        // Keep an existing decision; otherwise (re)derive whether sign-off is needed.
        val toSave = if (workOrder.approvalStatus == "Approved" || workOrder.approvalStatus == "Rejected") {
            workOrder
        } else {
            workOrder.copy(approvalStatus = if (workOrder.needsApproval()) "Pending" else "NotRequired")
        }
        workOrderDao.insertWorkOrder(toSave)
        recordAudit(if (isNew) "Create" else "Update", "WorkOrder", "${if (isNew) "إنشاء" else "تعديل"} أمر عمل: ${workOrder.title}", actor)
        // AST-WAR-010: persist the warranty decision/review outcome into the asset history.
        if (workOrder.repairType.isNotBlank()) {
            val assetCode = assetDao.getAssetById(workOrder.assetId)?.code ?: workOrder.assetId.toString()
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
        recordAudit("Approval", "WorkOrder", "${if (approved) "اعتماد" else "رفض"} أمر العمل: ${workOrder.title}", actor)
    }

internal suspend fun CmmsRepository.deleteWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
        workOrderDao.deleteById(workOrder.id)
        recordAudit("Delete", "WorkOrder", "حذف أمر عمل: ${workOrder.title}", actor)
    }

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
        userDao.insert(resolved)
        recordAudit(if (isNew) "Create" else "Update", "User", "${if (isNew) "إضافة" else "تعديل"} مستخدم: ${user.username}", actor)
    }

internal suspend fun CmmsRepository.setUserActive(user: UserEntity, active: Boolean, actor: String = "System") {
        userDao.setActive(user.id, active)
        recordAudit("Update", "User", "${if (active) "تفعيل" else "تعطيل"} المستخدم: ${user.username}", actor)
    }

internal suspend fun CmmsRepository.deleteUser(user: UserEntity, actor: String = "System") {
        userDao.deleteById(user.id)
        recordAudit("Delete", "User", "حذف المستخدم: ${user.username}", actor)
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


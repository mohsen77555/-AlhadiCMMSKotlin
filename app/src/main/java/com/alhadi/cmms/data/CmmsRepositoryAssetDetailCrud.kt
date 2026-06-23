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

internal suspend fun CmmsRepository.saveAssetDocument(doc: AssetDocumentEntity, actor: String = "System") {
        val isNew = doc.id == 0L
        val toSave = if (isNew) doc.copy(uploadedBy = actor, uploadedAt = DateStrings.now()) else doc
        documentDao.insert(toSave)
        recordAudit(if (isNew) "Create" else "Update", "Document", "${if (isNew) "إضافة" else "تعديل"} مستند: ${doc.title}", actor)
    }

internal suspend fun CmmsRepository.deleteAssetDocument(doc: AssetDocumentEntity, actor: String = "System") {
        documentDao.deleteById(doc.id)
        recordAudit("Delete", "Document", "حذف مستند: ${doc.title}", actor)
    }

    // ---------------------------------------------------------------------
    // Asset characteristics (classification)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveCharacteristic(item: AssetCharacteristicEntity, actor: String = "System") {
        val isNew = item.id == 0L
        characteristicDao.insert(item)
        recordAudit(if (isNew) "Create" else "Update", "Characteristic", "${if (isNew) "إضافة" else "تعديل"} خاصية: ${item.name}", actor)
    }

internal suspend fun CmmsRepository.deleteCharacteristic(item: AssetCharacteristicEntity, actor: String = "System") {
        characteristicDao.deleteById(item.id)
        recordAudit("Delete", "Characteristic", "حذف خاصية: ${item.name}", actor)
    }

    // ---------------------------------------------------------------------
    // Asset BOM (maintenance bill of materials)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveBomHeader(header: AssetBomHeaderEntity, actor: String = "System") {
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

internal suspend fun CmmsRepository.deleteBomHeader(header: AssetBomHeaderEntity, actor: String = "System") {
        database.withTransaction {
            bomDao.deleteForHeader(header.id)
            bomHeaderDao.deleteById(header.id)
            recordAudit("Delete", "BOM", "حذف قائمة مكونات: ${header.code}", actor)
        }
    }

internal suspend fun CmmsRepository.saveBomItem(item: AssetBomItemEntity, actor: String = "System") {
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

internal suspend fun CmmsRepository.deleteBomItem(item: AssetBomItemEntity, actor: String = "System") {
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
internal suspend fun CmmsRepository.performAssetMovement(
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

internal suspend fun CmmsRepository.deleteAssetMovement(movement: AssetMovementEntity, actor: String = "System") {
        movementDao.deleteById(movement.id)
        recordAudit("Delete", "Movement", "حذف حركة (${MovementType.label(movement.eventType)})", actor)
    }

    // ---------------------------------------------------------------------
    // PM inspection checklist
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveChecklistItem(item: PmChecklistItemEntity, actor: String = "System") {
        val isNew = item.id == 0L
        checklistDao.insert(item)
        recordAudit(if (isNew) "Create" else "Update", "Checklist", "${if (isNew) "إضافة" else "تعديل"} بند فحص: ${item.text}", actor)
    }

internal suspend fun CmmsRepository.setChecklistResult(item: PmChecklistItemEntity, result: String, actor: String = "System") {
        checklistDao.insert(item.copy(result = result))
        recordAudit("Update", "Checklist", "نتيجة بند الفحص \"${item.text}\": $result", actor)
    }

internal suspend fun CmmsRepository.deleteChecklistItem(item: PmChecklistItemEntity, actor: String = "System") {
        checklistDao.deleteById(item.id)
        recordAudit("Delete", "Checklist", "حذف بند فحص: ${item.text}", actor)
    }

    // ---------------------------------------------------------------------
    // Maintenance notifications (بلاغات) — the trigger of maintenance work
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveNotification(notification: MaintenanceNotificationEntity, actor: String = "System") {
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

internal suspend fun CmmsRepository.setNotificationStatus(notification: MaintenanceNotificationEntity, status: String, actor: String = "System") {
        notificationDao.insert(notification.copy(status = status))
        recordAudit("Status", "Notification", "تغيير حالة البلاغ ${notification.number} إلى $status", actor)
    }

    /**
     * Converts an approved notification into a work order, copying asset/priority/description,
     * then links the two (notification → OrderCreated + linkedOrderId).
     */
internal suspend fun CmmsRepository.createOrderFromNotification(
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

internal suspend fun CmmsRepository.deleteNotification(notification: MaintenanceNotificationEntity, actor: String = "System") {
        notificationDao.deleteById(notification.id)
        recordAudit("Delete", "Notification", "حذف بلاغ: ${notification.title}", actor)
    }

    // ---------------------------------------------------------------------
    // Work order operations (steps within an order)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveOperation(operation: WorkOrderOperationEntity, actor: String = "System") {
        val isNew = operation.id == 0L
        operationDao.insert(operation)
        recordAudit(if (isNew) "Create" else "Update", "Operation", "${if (isNew) "إضافة" else "تعديل"} عملية ${operation.operationNumber}: ${operation.description}", actor)
    }

internal suspend fun CmmsRepository.setOperationStatus(operation: WorkOrderOperationEntity, status: String, actor: String = "System") {
        // Confirming with no recorded actual hours falls back to the planned estimate.
        val actual = if (status == "Confirmed" && operation.actualHours == 0.0) operation.plannedHours else operation.actualHours
        operationDao.insert(operation.copy(status = status, actualHours = actual))
        recordAudit("Status", "Operation", "عملية ${operation.operationNumber} → $status", actor)
    }

internal suspend fun CmmsRepository.deleteOperation(operation: WorkOrderOperationEntity, actor: String = "System") {
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
internal suspend fun CmmsRepository.addConfirmation(
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

internal suspend fun CmmsRepository.deleteConfirmation(confirmation: WorkOrderConfirmationEntity, actor: String = "System") {
        confirmationDao.deleteById(confirmation.id)
        recordAudit("Delete", "Confirmation", "حذف تأكيد عملية #${confirmation.operationId}", actor)
    }

    // ---------------------------------------------------------------------
    // Work order evidence photos (أدلة التنفيذ)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.addWorkOrderPhoto(orderId: Long, path: String, actor: String = "System") {
        photoDao.insert(
            WorkOrderPhotoEntity(orderId = orderId, path = path, addedBy = actor, addedAt = DateStrings.now())
        )
        recordAudit("Attach", "WorkOrder", "إرفاق صورة دليل لأمر العمل #$orderId", actor)
    }

internal suspend fun CmmsRepository.deleteWorkOrderPhoto(photo: WorkOrderPhotoEntity, actor: String = "System") {
        photoDao.deleteById(photo.id)
        recordAudit("Delete", "WorkOrder", "حذف صورة دليل لأمر العمل #${photo.orderId}", actor)
    }

    // ---------------------------------------------------------------------
    // Work permits (تصاريح العمل / السلامة)
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.savePermit(permit: WorkPermitEntity, actor: String = "System") {
        val isNew = permit.id == 0L
        val toSave = if (isNew) permit.copy(createdBy = actor, createdAt = DateStrings.now(), status = "Pending") else permit
        permitDao.insert(toSave)
        recordAudit(if (isNew) "Create" else "Update", "Permit", "${if (isNew) "إصدار" else "تعديل"} تصريح (${permit.type}) لأمر العمل #${permit.orderId}", actor)
    }

internal suspend fun CmmsRepository.setPermitStatus(permit: WorkPermitEntity, approved: Boolean, actor: String = "System") {
        val status = if (approved) "Approved" else "Rejected"
        permitDao.insert(permit.copy(status = status, approvedBy = actor))
        recordAudit("Approval", "Permit", "${if (approved) "اعتماد" else "رفض"} تصريح العمل #${permit.id}", actor)
    }

internal suspend fun CmmsRepository.deletePermit(permit: WorkPermitEntity, actor: String = "System") {
        permitDao.deleteById(permit.id)
        recordAudit("Delete", "Permit", "حذف تصريح عمل #${permit.id}", actor)
    }

    // ---------------------------------------------------------------------
    // Task lists (قوالب العمل) + generation of orders from PM plans
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.saveTaskList(taskList: TaskListEntity, actor: String = "System") {
        val isNew = taskList.id == 0L
        taskListDao.insertTaskList(taskList)
        recordAudit(if (isNew) "Create" else "Update", "TaskList", "${if (isNew) "إضافة" else "تعديل"} قالب عمل: ${taskList.name}", actor)
    }

internal suspend fun CmmsRepository.deleteTaskList(taskList: TaskListEntity, actor: String = "System") {
        taskListDao.deleteOperationsForList(taskList.id)
        taskListDao.deleteTaskListById(taskList.id)
        recordAudit("Delete", "TaskList", "حذف قالب عمل: ${taskList.name}", actor)
    }

internal suspend fun CmmsRepository.saveTaskListOperation(operation: TaskListOperationEntity, actor: String = "System") {
        val isNew = operation.id == 0L
        taskListDao.insertOperation(operation)
        recordAudit(if (isNew) "Create" else "Update", "TaskList", "${if (isNew) "إضافة" else "تعديل"} عملية قالب ${operation.operationNumber}", actor)
    }

internal suspend fun CmmsRepository.deleteTaskListOperation(operation: TaskListOperationEntity, actor: String = "System") {
        taskListDao.deleteOperationById(operation.id)
        recordAudit("Delete", "TaskList", "حذف عملية قالب ${operation.operationNumber}", actor)
    }

    // ---------------------------------------------------------------------
    // Excel (maintenance-kit) import
    // ---------------------------------------------------------------------


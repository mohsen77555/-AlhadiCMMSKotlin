package com.alhadi.cmms.viewmodel

import com.alhadi.cmms.data.* // repository CRUD methods are extension functions in this package
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.util.DateStrings
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

// ----- CRUD: Assets -----
fun CmmsViewModel.saveAsset(asset: AssetEntity) = launchAction("تم حفظ الأصل") { repository.saveAsset(asset, actor()) }
fun CmmsViewModel.deleteAsset(asset: AssetEntity) = launchAction("تم حذف الأصل") { repository.deleteAsset(asset, actor()) }
fun CmmsViewModel.changeAssetStatus(asset: AssetEntity, status: String, reason: String = "") = launchAction("تم تغيير حالة الأصل") { repository.changeAssetStatus(asset, status, reason, actor()) }

// ----- CRUD: Spare parts -----
fun CmmsViewModel.savePart(part: SparePartEntity) = launchAction("تم حفظ القطعة") { repository.savePart(part, actor()) }
fun CmmsViewModel.deletePart(part: SparePartEntity) = launchAction("تم حذف القطعة") { repository.deletePart(part, actor()) }

// ----- CRUD: Work orders -----
fun CmmsViewModel.saveWorkOrder(workOrder: WorkOrderEntity) = launchAction("تم حفظ أمر العمل") { repository.saveWorkOrder(workOrder, actor()) }
fun CmmsViewModel.setWorkOrderApproval(workOrder: WorkOrderEntity, approved: Boolean) =
    launchAction(if (approved) "تم اعتماد أمر العمل" else "تم رفض أمر العمل") { repository.setWorkOrderApproval(workOrder, approved, actor()) }
fun CmmsViewModel.deleteWorkOrder(workOrder: WorkOrderEntity) = launchAction("تم حذف أمر العمل") { repository.deleteWorkOrder(workOrder, actor()) }

// ----- CRUD: Preventive maintenance -----
fun CmmsViewModel.savePreventiveMaintenance(item: PreventiveMaintenanceEntity) = launchAction("تم حفظ مهمة الصيانة") { repository.savePreventiveMaintenance(item, actor()) }
fun CmmsViewModel.deletePreventiveMaintenance(item: PreventiveMaintenanceEntity) = launchAction("تم حذف مهمة الصيانة") { repository.deletePreventiveMaintenance(item, actor()) }

// ----- CRUD: Users -----
fun CmmsViewModel.saveUser(user: UserEntity) = launchAction("تم حفظ المستخدم") { repository.saveUser(user, actor()) }
fun CmmsViewModel.setUserActive(user: UserEntity, active: Boolean) = launchAction("تم تحديث حالة المستخدم") { repository.setUserActive(user, active, actor()) }
fun CmmsViewModel.deleteUser(user: UserEntity) = launchAction("تم حذف المستخدم") { repository.deleteUser(user, actor()) }

// ----- Meters & readings -----
fun CmmsViewModel.saveMeasuringPoint(point: MeasuringPointEntity) = launchAction("تم حفظ نقطة القياس") { repository.saveMeasuringPoint(point, actor()) }
fun CmmsViewModel.deleteMeasuringPoint(point: MeasuringPointEntity) = launchAction("تم حذف نقطة القياس") { repository.deleteMeasuringPoint(point, actor()) }
fun CmmsViewModel.addReading(point: MeasuringPointEntity, value: Double, note: String) {
    viewModelScope.launch {
        runCatching { repository.addReading(point, value, note, actor()) }
            .onSuccess { warning -> _message.value = warning ?: "تم تسجيل القراءة" }
            .onFailure { _message.value = "حدث خطأ: ${it.message ?: "غير معروف"}" }
    }
}

// ----- Functional locations -----
fun CmmsViewModel.saveFunctionalLocation(location: FunctionalLocationEntity) = launchAction("تم حفظ الموقع الفني") { repository.saveFunctionalLocation(location, actor()) }
fun CmmsViewModel.deleteFunctionalLocation(location: FunctionalLocationEntity) = launchAction("تم حذف الموقع الفني") { repository.deleteFunctionalLocation(location, actor()) }

fun CmmsViewModel.saveWarehouse(warehouse: WarehouseEntity) = launchAction("تم حفظ المستودع") { repository.saveWarehouse(warehouse, actor()) }
fun CmmsViewModel.deleteWarehouse(warehouse: WarehouseEntity) = launchAction("تم حذف المستودع") { repository.deleteWarehouse(warehouse, actor()) }

fun CmmsViewModel.saveOrgUnit(unit: OrgUnitEntity) = launchAction("تم حفظ الوحدة التنظيمية") { repository.saveOrgUnit(unit, actor()) }
fun CmmsViewModel.deleteOrgUnit(unit: OrgUnitEntity) = launchAction("تم حذف الوحدة التنظيمية") { repository.deleteOrgUnit(unit, actor()) }

// ----- Asset documents -----
fun CmmsViewModel.saveAssetDocument(doc: AssetDocumentEntity) = launchAction("تم حفظ المستند") { repository.saveAssetDocument(doc, actor()) }
fun CmmsViewModel.deleteAssetDocument(doc: AssetDocumentEntity) = launchAction("تم حذف المستند") { repository.deleteAssetDocument(doc, actor()) }

// ----- Asset characteristics -----
fun CmmsViewModel.saveCharacteristic(item: AssetCharacteristicEntity) = launchAction("تم حفظ الخاصية") { repository.saveCharacteristic(item, actor()) }
fun CmmsViewModel.deleteCharacteristic(item: AssetCharacteristicEntity) = launchAction("تم حذف الخاصية") { repository.deleteCharacteristic(item, actor()) }

// ----- Asset BOM -----
fun CmmsViewModel.saveBomHeader(header: AssetBomHeaderEntity) = launchAction("تم حفظ قائمة المكونات") { repository.saveBomHeader(header, actor()) }
fun CmmsViewModel.deleteBomHeader(header: AssetBomHeaderEntity) = launchAction("تم حذف قائمة المكونات") { repository.deleteBomHeader(header, actor()) }
fun CmmsViewModel.saveBomItem(item: AssetBomItemEntity) = launchAction("تم حفظ بند المكوّنات") { repository.saveBomItem(item, actor()) }
fun CmmsViewModel.deleteBomItem(item: AssetBomItemEntity) = launchAction("تم حذف بند المكوّنات") { repository.deleteBomItem(item, actor()) }

fun CmmsViewModel.performAssetMovement(asset: AssetEntity, eventType: String, toLocationId: Long?, toLocationName: String, notes: String) =
    launchAction("تم تسجيل الحركة") { repository.performAssetMovement(asset, eventType, toLocationId, toLocationName, notes, actor()) }
fun CmmsViewModel.deleteAssetMovement(movement: AssetMovementEntity) = launchAction("تم حذف الحركة") { repository.deleteAssetMovement(movement, actor()) }

fun CmmsViewModel.saveChecklistItem(item: PmChecklistItemEntity) = launchAction("تم حفظ بند الفحص") { repository.saveChecklistItem(item, actor()) }
fun CmmsViewModel.setChecklistResult(item: PmChecklistItemEntity, result: String) = launchAction("تم تحديث الفحص") { repository.setChecklistResult(item, result, actor()) }
fun CmmsViewModel.deleteChecklistItem(item: PmChecklistItemEntity) = launchAction("تم حذف بند الفحص") { repository.deleteChecklistItem(item, actor()) }

// ----- Notifications & work order workflow -----
fun CmmsViewModel.saveNotification(notification: MaintenanceNotificationEntity) = launchAction("تم حفظ البلاغ") { repository.saveNotification(notification, actor()) }
fun CmmsViewModel.setNotificationStatus(notification: MaintenanceNotificationEntity, status: String) = launchAction("تم تحديث حالة البلاغ") { repository.setNotificationStatus(notification, status, actor()) }
fun CmmsViewModel.createOrderFromNotification(notification: MaintenanceNotificationEntity) = launchAction("تم إنشاء أمر عمل من البلاغ") {
    repository.createOrderFromNotification(notification, assignedTo = actor(), dueAt = DateStrings.daysFromToday(3), actor = actor())
}
fun CmmsViewModel.deleteNotification(notification: MaintenanceNotificationEntity) = launchAction("تم حذف البلاغ") { repository.deleteNotification(notification, actor()) }

fun CmmsViewModel.saveOperation(operation: WorkOrderOperationEntity) = launchAction("تم حفظ العملية") { repository.saveOperation(operation, actor()) }
fun CmmsViewModel.setOperationStatus(operation: WorkOrderOperationEntity, status: String) = launchAction("تم تحديث العملية") { repository.setOperationStatus(operation, status, actor()) }
fun CmmsViewModel.deleteOperation(operation: WorkOrderOperationEntity) = launchAction("تم حذف العملية") { repository.deleteOperation(operation, actor()) }

fun CmmsViewModel.addConfirmation(confirmation: WorkOrderConfirmationEntity, operation: WorkOrderOperationEntity) =
    launchAction("تم تسجيل التأكيد") { repository.addConfirmation(confirmation, operation, actor()) }
fun CmmsViewModel.deleteConfirmation(confirmation: WorkOrderConfirmationEntity) = launchAction("تم حذف التأكيد") { repository.deleteConfirmation(confirmation, actor()) }

fun CmmsViewModel.addWorkOrderPhoto(orderId: Long, path: String) = launchAction("تم إرفاق صورة الدليل") { repository.addWorkOrderPhoto(orderId, path, actor()) }
fun CmmsViewModel.deleteWorkOrderPhoto(photo: WorkOrderPhotoEntity) = launchAction("تم حذف الصورة") { repository.deleteWorkOrderPhoto(photo, actor()) }

fun CmmsViewModel.saveTaskList(taskList: TaskListEntity) = launchAction("تم حفظ القالب") { repository.saveTaskList(taskList, actor()) }
fun CmmsViewModel.deleteTaskList(taskList: TaskListEntity) = launchAction("تم حذف القالب") { repository.deleteTaskList(taskList, actor()) }
fun CmmsViewModel.saveTaskListOperation(operation: TaskListOperationEntity) = launchAction("تم حفظ عملية القالب") { repository.saveTaskListOperation(operation, actor()) }
fun CmmsViewModel.deleteTaskListOperation(operation: TaskListOperationEntity) = launchAction("تم حذف عملية القالب") { repository.deleteTaskListOperation(operation, actor()) }
fun CmmsViewModel.generateWorkOrderFromPm(pm: PreventiveMaintenanceEntity) = launchAction("تم توليد أمر عمل من الخطة") { repository.generateWorkOrderFromPm(pm, actor()) }

fun CmmsViewModel.savePermit(permit: WorkPermitEntity) = launchAction("تم حفظ التصريح") { repository.savePermit(permit, actor()) }
fun CmmsViewModel.setPermitStatus(permit: WorkPermitEntity, approved: Boolean) =
    launchAction(if (approved) "تم اعتماد التصريح" else "تم رفض التصريح") { repository.setPermitStatus(permit, approved, actor()) }
fun CmmsViewModel.deletePermit(permit: WorkPermitEntity) = launchAction("تم حذف التصريح") { repository.deletePermit(permit, actor()) }

// ----- CAPA -----
fun CmmsViewModel.saveCapa(item: CapaEntity) = launchAction("تم حفظ الإجراء") { repository.saveCapa(item, actor()) }
fun CmmsViewModel.updateCapaStatus(item: CapaEntity, status: String) = launchAction("تم تحديث حالة الإجراء") { repository.updateCapaStatus(item, status, actor()) }
fun CmmsViewModel.deleteCapa(item: CapaEntity) = launchAction("تم حذف الإجراء") { repository.deleteCapa(item, actor()) }

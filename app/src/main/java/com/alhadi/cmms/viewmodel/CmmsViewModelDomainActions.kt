package com.alhadi.cmms.viewmodel

import com.alhadi.cmms.data.* // repository CRUD methods are extension functions in this package
import com.alhadi.cmms.data.SerialInstallRequest
import com.alhadi.cmms.data.SerialMasterRequest
import com.alhadi.cmms.data.SerialTransferRequest
import com.alhadi.cmms.data.SerializedIssueRequest
import com.alhadi.cmms.data.SerializedReceiptRequest
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity

// ----- Domain actions -----
fun CmmsViewModel.createDemoWorkOrder() = launchAction("تم إنشاء أمر عمل جديد") {
    repository.createDemoWorkOrder(actor = actor())
}

fun CmmsViewModel.createWorkOrder(
    assetId: Long,
    title: String,
    description: String,
    priority: String,
    assignedTo: String,
    dueAt: String,
    estimatedCost: Double
) = launchAction("تم إنشاء أمر العمل") {
    repository.createWorkOrder(
        assetId = assetId,
        title = title,
        description = description,
        priority = priority,
        assignedTo = assignedTo,
        dueAt = dueAt,
        estimatedCost = estimatedCost,
        actor = actor()
    )
}

fun CmmsViewModel.updateWorkOrderStatus(workOrder: WorkOrderEntity, status: String) = launchAction("تم تحديث حالة أمر العمل") {
    repository.updateWorkOrderStatus(workOrder.id, status, actor())
}

fun CmmsViewModel.markPreventiveMaintenanceDone(item: PreventiveMaintenanceEntity) = launchAction("تم إغلاق مهمة الصيانة الدورية وجدولتها من جديد") {
    repository.markPreventiveMaintenanceDone(item, actor())
}

fun CmmsViewModel.issuePart(part: SparePartEntity, quantity: Int = 1) = launchAction("تم صرف $quantity من المخزون") {
    repository.issuePart(part, quantity = quantity, actor = actor())
}

fun CmmsViewModel.receivePart(part: SparePartEntity, quantity: Int = 1) = launchAction("تم استلام $quantity إلى المخزون") {
    repository.receivePart(part, quantity = quantity, actor = actor())
}

fun CmmsViewModel.issuePartToWorkOrder(order: WorkOrderEntity, part: SparePartEntity, quantity: Int) =
    launchAction("تم صرف المادة لأمر العمل") { repository.issuePartToWorkOrder(order, part, quantity, actor()) }

fun CmmsViewModel.addTechnician() = launchAction("تمت إضافة فني تجريبي") {
    repository.addTechnician(actor())
}

fun CmmsViewModel.resetSampleData() = launchAction("تمت إعادة تعبئة البيانات التجريبية") {
    repository.seedSampleData(replace = true)
}

// ----- Serial number management -----
fun CmmsViewModel.saveSerialProfile(profile: SerialNumberProfileEntity) =
    launchAction("تم حفظ ملف التتبع") { repository.saveSerialProfile(profile, actor()) }

fun CmmsViewModel.deleteSerialProfile(profile: SerialNumberProfileEntity) =
    launchAction("تم حذف ملف التتبع") { repository.deleteSerialProfile(profile, actor()) }

fun CmmsViewModel.createSerialMaster(request: SerialMasterRequest) =
    launchAction("تم إنشاء الرقم التسلسلي") { repository.createSerialMaster(request, actor()) }

fun CmmsViewModel.receiveSerializedPart(request: SerializedReceiptRequest) =
    launchAction("تم استلام الوحدات المتسلسلة") { repository.receiveSerializedPart(request, actor()) }

fun CmmsViewModel.issueSerializedPart(request: SerializedIssueRequest) =
    launchAction("تم صرف الوحدات المتسلسلة") { repository.issueSerializedPart(request, actor()) }

fun CmmsViewModel.transferSerialNumber(request: SerialTransferRequest) =
    launchAction("تم نقل الرقم التسلسلي") { repository.transferSerialNumber(request, actor()) }

fun CmmsViewModel.installSerialNumber(request: SerialInstallRequest) =
    launchAction("تم تركيب الرقم التسلسلي") { repository.installSerialNumber(request, actor()) }

fun CmmsViewModel.dismantleSerialNumber(serialId: Long, note: String = "") =
    launchAction("تم فك الرقم التسلسلي") { repository.dismantleSerialNumber(serialId, note, actor()) }

fun CmmsViewModel.reconcileSerializedStock(partId: Long) =
    launchAction("تمت تسوية المخزون المتسلسل") { repository.reconcileSerializedStock(partId, actor()) }

fun CmmsViewModel.deleteSerialNumber(serial: SerialNumberEntity) =
    launchAction("تم حذف الرقم التسلسلي") { repository.deleteSerialNumber(serial, actor()) }

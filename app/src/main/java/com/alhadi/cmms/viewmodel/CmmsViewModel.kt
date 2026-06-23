package com.alhadi.cmms.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alhadi.cmms.data.CmmsRepository
import com.alhadi.cmms.data.* // repository CRUD methods are now extension functions in this package
import com.alhadi.cmms.data.SerialInstallRequest
import com.alhadi.cmms.data.SerialMasterRequest
import com.alhadi.cmms.data.SerialTransferRequest
import com.alhadi.cmms.data.SerializedIssueRequest
import com.alhadi.cmms.data.SerializedReceiptRequest
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
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
import com.alhadi.cmms.util.PdfExporter
import com.alhadi.cmms.util.XlsxReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class DashboardStats(
    val assets: Int = 0,
    val openWorkOrders: Int = 0,
    val duePm: Int = 0,
    val lowStock: Int = 0,
    val capa: Int = 0
)

@OptIn(ExperimentalCoroutinesApi::class)
class CmmsViewModel(private val repository: CmmsRepository) : ViewModel() {
    private val today = DateStrings.today()

    // ----- Authentication / session -----
    private val _sessionUserId = MutableStateFlow<Long?>(null)

    val currentUser: StateFlow<UserEntity?> = _sessionUserId
        .flatMapLatest { id ->
            if (id == null) flowOf(null) else repository.observeUserById(id)
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)

    val isLoggedIn: StateFlow<Boolean> = currentUser
        .map { it != null }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    private val _loginError = MutableStateFlow<String?>(null)
    val loginError: StateFlow<String?> = _loginError

    // ----- Data streams -----
    val dashboardStats: StateFlow<DashboardStats> = combine(
        repository.observeAssetCount(),
        repository.observeOpenWorkOrderCount(),
        repository.observeDuePmCount(today),
        repository.observeLowStockCount(),
        repository.observeOpenCapaCount()
    ) { assets, openWorkOrders, duePm, lowStock, capa ->
        DashboardStats(
            assets = assets,
            openWorkOrders = openWorkOrders,
            duePm = duePm,
            lowStock = lowStock,
            capa = capa
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), DashboardStats())

    val assets: StateFlow<List<AssetEntity>> = repository.assets
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workOrders: StateFlow<List<WorkOrderEntity>> = repository.workOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val preventiveMaintenance: StateFlow<List<PreventiveMaintenanceEntity>> = repository.preventiveMaintenance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val serialNumberProfiles: StateFlow<List<SerialNumberProfileEntity>> = repository.serialNumberProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val serialNumbers: StateFlow<List<SerialNumberEntity>> = repository.serialNumbers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val serialNumberMovements: StateFlow<List<SerialNumberMovementEntity>> = repository.serialNumberMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val spareParts: StateFlow<List<SparePartEntity>> = repository.spareParts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val transactions: StateFlow<List<InventoryTransactionEntity>> = repository.recentTransactions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val users: StateFlow<List<UserEntity>> = repository.users
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val auditLog: StateFlow<List<AuditLogEntity>> = repository.auditLog
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val measuringPoints: StateFlow<List<MeasuringPointEntity>> = repository.measuringPoints
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val readings: StateFlow<List<MeasurementReadingEntity>> = repository.readings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val functionalLocations: StateFlow<List<FunctionalLocationEntity>> = repository.functionalLocations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val warehouses: StateFlow<List<WarehouseEntity>> = repository.warehouses
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val orgUnits: StateFlow<List<OrgUnitEntity>> = repository.orgUnits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val capaActions: StateFlow<List<CapaEntity>> = repository.capaActions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetDocuments: StateFlow<List<AssetDocumentEntity>> = repository.assetDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetCharacteristics: StateFlow<List<AssetCharacteristicEntity>> = repository.assetCharacteristics
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetBomHeaders: StateFlow<List<AssetBomHeaderEntity>> = repository.assetBomHeaders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetBom: StateFlow<List<AssetBomItemEntity>> = repository.assetBom
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetMovements: StateFlow<List<AssetMovementEntity>> = repository.assetMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val pmChecklist: StateFlow<List<PmChecklistItemEntity>> = repository.pmChecklist
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val notifications: StateFlow<List<MaintenanceNotificationEntity>> = repository.notifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workOrderOperations: StateFlow<List<WorkOrderOperationEntity>> = repository.workOrderOperations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workOrderConfirmations: StateFlow<List<WorkOrderConfirmationEntity>> = repository.workOrderConfirmations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workOrderPhotos: StateFlow<List<WorkOrderPhotoEntity>> = repository.workOrderPhotos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val taskLists: StateFlow<List<TaskListEntity>> = repository.taskLists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val taskListOperations: StateFlow<List<TaskListOperationEntity>> = repository.taskListOperations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workPermits: StateFlow<List<WorkPermitEntity>> = repository.workPermits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    private fun actor(): String = currentUser.value?.name ?: "System"

    // ----- Auth actions -----
    fun login(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            _loginError.value = "أدخل اسم المستخدم وكلمة المرور"
            return
        }
        viewModelScope.launch {
            val user = runCatching { repository.authenticate(username, password) }.getOrNull()
            if (user != null) {
                _sessionUserId.value = user.id
                _loginError.value = null
                _message.value = "مرحباً ${user.name}"
            } else {
                _loginError.value = "بيانات الدخول غير صحيحة"
            }
        }
    }

    fun logout() {
        _sessionUserId.value = null
        _loginError.value = null
    }

    fun clearLoginError() {
        _loginError.value = null
    }

    // ----- Domain actions -----
    fun createDemoWorkOrder() = launchAction("تم إنشاء أمر عمل جديد") {
        repository.createDemoWorkOrder(actor = actor())
    }

    fun createWorkOrder(
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

    fun updateWorkOrderStatus(workOrder: WorkOrderEntity, status: String) = launchAction("تم تحديث حالة أمر العمل") {
        repository.updateWorkOrderStatus(workOrder.id, status, actor())
    }

    fun markPreventiveMaintenanceDone(item: PreventiveMaintenanceEntity) = launchAction("تم إغلاق مهمة الصيانة الدورية وجدولتها من جديد") {
        repository.markPreventiveMaintenanceDone(item, actor())
    }

    fun issuePart(part: SparePartEntity, quantity: Int = 1) = launchAction("تم صرف $quantity من المخزون") {
        repository.issuePart(part, quantity = quantity, actor = actor())
    }

    fun receivePart(part: SparePartEntity, quantity: Int = 1) = launchAction("تم استلام $quantity إلى المخزون") {
        repository.receivePart(part, quantity = quantity, actor = actor())
    }

    fun issuePartToWorkOrder(order: WorkOrderEntity, part: SparePartEntity, quantity: Int) =
        launchAction("تم صرف المادة لأمر العمل") { repository.issuePartToWorkOrder(order, part, quantity, actor()) }

    fun addTechnician() = launchAction("تمت إضافة فني تجريبي") {
        repository.addTechnician(actor())
    }

    fun resetSampleData() = launchAction("تمت إعادة تعبئة البيانات التجريبية") {
        repository.seedSampleData(replace = true)
    }


    // ----- Serial number management -----
    fun saveSerialProfile(profile: SerialNumberProfileEntity) =
        launchAction("تم حفظ ملف التتبع") { repository.saveSerialProfile(profile, actor()) }

    fun deleteSerialProfile(profile: SerialNumberProfileEntity) =
        launchAction("تم حذف ملف التتبع") { repository.deleteSerialProfile(profile, actor()) }

    fun createSerialMaster(request: SerialMasterRequest) =
        launchAction("تم إنشاء الرقم التسلسلي") { repository.createSerialMaster(request, actor()) }

    fun receiveSerializedPart(request: SerializedReceiptRequest) =
        launchAction("تم استلام الوحدات المتسلسلة") { repository.receiveSerializedPart(request, actor()) }

    fun issueSerializedPart(request: SerializedIssueRequest) =
        launchAction("تم صرف الوحدات المتسلسلة") { repository.issueSerializedPart(request, actor()) }

    fun transferSerialNumber(request: SerialTransferRequest) =
        launchAction("تم نقل الرقم التسلسلي") { repository.transferSerialNumber(request, actor()) }

    fun installSerialNumber(request: SerialInstallRequest) =
        launchAction("تم تركيب الرقم التسلسلي") { repository.installSerialNumber(request, actor()) }

    fun dismantleSerialNumber(serialId: Long, note: String = "") =
        launchAction("تم فك الرقم التسلسلي") { repository.dismantleSerialNumber(serialId, note, actor()) }

    fun reconcileSerializedStock(partId: Long) =
        launchAction("تمت تسوية المخزون المتسلسل") { repository.reconcileSerializedStock(partId, actor()) }

    fun deleteSerialNumber(serial: SerialNumberEntity) =
        launchAction("تم حذف الرقم التسلسلي") { repository.deleteSerialNumber(serial, actor()) }


    // ----- CRUD: Assets -----
    fun saveAsset(asset: AssetEntity) = launchAction("تم حفظ الأصل") { repository.saveAsset(asset, actor()) }
    fun deleteAsset(asset: AssetEntity) = launchAction("تم حذف الأصل") { repository.deleteAsset(asset, actor()) }
    fun changeAssetStatus(asset: AssetEntity, status: String, reason: String = "") = launchAction("تم تغيير حالة الأصل") { repository.changeAssetStatus(asset, status, reason, actor()) }

    // ----- CRUD: Spare parts -----
    fun savePart(part: SparePartEntity) = launchAction("تم حفظ القطعة") { repository.savePart(part, actor()) }
    fun deletePart(part: SparePartEntity) = launchAction("تم حذف القطعة") { repository.deletePart(part, actor()) }

    // ----- CRUD: Work orders -----
    fun saveWorkOrder(workOrder: WorkOrderEntity) = launchAction("تم حفظ أمر العمل") { repository.saveWorkOrder(workOrder, actor()) }
    fun setWorkOrderApproval(workOrder: WorkOrderEntity, approved: Boolean) =
        launchAction(if (approved) "تم اعتماد أمر العمل" else "تم رفض أمر العمل") { repository.setWorkOrderApproval(workOrder, approved, actor()) }
    fun deleteWorkOrder(workOrder: WorkOrderEntity) = launchAction("تم حذف أمر العمل") { repository.deleteWorkOrder(workOrder, actor()) }

    // ----- CRUD: Preventive maintenance -----
    fun savePreventiveMaintenance(item: PreventiveMaintenanceEntity) = launchAction("تم حفظ مهمة الصيانة") { repository.savePreventiveMaintenance(item, actor()) }
    fun deletePreventiveMaintenance(item: PreventiveMaintenanceEntity) = launchAction("تم حذف مهمة الصيانة") { repository.deletePreventiveMaintenance(item, actor()) }

    // ----- CRUD: Users -----
    fun saveUser(user: UserEntity) = launchAction("تم حفظ المستخدم") { repository.saveUser(user, actor()) }
    fun setUserActive(user: UserEntity, active: Boolean) = launchAction("تم تحديث حالة المستخدم") { repository.setUserActive(user, active, actor()) }
    fun deleteUser(user: UserEntity) = launchAction("تم حذف المستخدم") { repository.deleteUser(user, actor()) }

    // ----- Meters & readings -----
    fun saveMeasuringPoint(point: MeasuringPointEntity) = launchAction("تم حفظ نقطة القياس") { repository.saveMeasuringPoint(point, actor()) }
    fun deleteMeasuringPoint(point: MeasuringPointEntity) = launchAction("تم حذف نقطة القياس") { repository.deleteMeasuringPoint(point, actor()) }
    fun addReading(point: MeasuringPointEntity, value: Double, note: String) {
        viewModelScope.launch {
            runCatching { repository.addReading(point, value, note, actor()) }
                .onSuccess { warning -> _message.value = warning ?: "تم تسجيل القراءة" }
                .onFailure { _message.value = "حدث خطأ: ${it.message ?: "غير معروف"}" }
        }
    }

    // ----- Functional locations -----
    fun saveFunctionalLocation(location: FunctionalLocationEntity) = launchAction("تم حفظ الموقع الفني") { repository.saveFunctionalLocation(location, actor()) }
    fun deleteFunctionalLocation(location: FunctionalLocationEntity) = launchAction("تم حذف الموقع الفني") { repository.deleteFunctionalLocation(location, actor()) }

    fun saveWarehouse(warehouse: WarehouseEntity) = launchAction("تم حفظ المستودع") { repository.saveWarehouse(warehouse, actor()) }
    fun deleteWarehouse(warehouse: WarehouseEntity) = launchAction("تم حذف المستودع") { repository.deleteWarehouse(warehouse, actor()) }

    fun saveOrgUnit(unit: OrgUnitEntity) = launchAction("تم حفظ الوحدة التنظيمية") { repository.saveOrgUnit(unit, actor()) }
    fun deleteOrgUnit(unit: OrgUnitEntity) = launchAction("تم حذف الوحدة التنظيمية") { repository.deleteOrgUnit(unit, actor()) }

    // ----- Asset documents -----
    fun saveAssetDocument(doc: AssetDocumentEntity) = launchAction("تم حفظ المستند") { repository.saveAssetDocument(doc, actor()) }
    fun deleteAssetDocument(doc: AssetDocumentEntity) = launchAction("تم حذف المستند") { repository.deleteAssetDocument(doc, actor()) }

    // ----- Asset characteristics -----
    fun saveCharacteristic(item: AssetCharacteristicEntity) = launchAction("تم حفظ الخاصية") { repository.saveCharacteristic(item, actor()) }
    fun deleteCharacteristic(item: AssetCharacteristicEntity) = launchAction("تم حذف الخاصية") { repository.deleteCharacteristic(item, actor()) }

    // ----- Asset BOM -----
    fun saveBomHeader(header: AssetBomHeaderEntity) = launchAction("تم حفظ قائمة المكونات") { repository.saveBomHeader(header, actor()) }
    fun deleteBomHeader(header: AssetBomHeaderEntity) = launchAction("تم حذف قائمة المكونات") { repository.deleteBomHeader(header, actor()) }
    fun saveBomItem(item: AssetBomItemEntity) = launchAction("تم حفظ بند المكوّنات") { repository.saveBomItem(item, actor()) }
    fun deleteBomItem(item: AssetBomItemEntity) = launchAction("تم حذف بند المكوّنات") { repository.deleteBomItem(item, actor()) }

    fun performAssetMovement(asset: AssetEntity, eventType: String, toLocationId: Long?, toLocationName: String, notes: String) =
        launchAction("تم تسجيل الحركة") { repository.performAssetMovement(asset, eventType, toLocationId, toLocationName, notes, actor()) }
    fun deleteAssetMovement(movement: AssetMovementEntity) = launchAction("تم حذف الحركة") { repository.deleteAssetMovement(movement, actor()) }

    fun saveChecklistItem(item: PmChecklistItemEntity) = launchAction("تم حفظ بند الفحص") { repository.saveChecklistItem(item, actor()) }
    fun setChecklistResult(item: PmChecklistItemEntity, result: String) = launchAction("تم تحديث الفحص") { repository.setChecklistResult(item, result, actor()) }
    fun deleteChecklistItem(item: PmChecklistItemEntity) = launchAction("تم حذف بند الفحص") { repository.deleteChecklistItem(item, actor()) }

    fun saveNotification(notification: MaintenanceNotificationEntity) = launchAction("تم حفظ البلاغ") { repository.saveNotification(notification, actor()) }
    fun setNotificationStatus(notification: MaintenanceNotificationEntity, status: String) = launchAction("تم تحديث حالة البلاغ") { repository.setNotificationStatus(notification, status, actor()) }
    fun createOrderFromNotification(notification: MaintenanceNotificationEntity) = launchAction("تم إنشاء أمر عمل من البلاغ") {
        repository.createOrderFromNotification(notification, assignedTo = actor(), dueAt = DateStrings.daysFromToday(3), actor = actor())
    }
    fun deleteNotification(notification: MaintenanceNotificationEntity) = launchAction("تم حذف البلاغ") { repository.deleteNotification(notification, actor()) }

    fun saveOperation(operation: WorkOrderOperationEntity) = launchAction("تم حفظ العملية") { repository.saveOperation(operation, actor()) }
    fun setOperationStatus(operation: WorkOrderOperationEntity, status: String) = launchAction("تم تحديث العملية") { repository.setOperationStatus(operation, status, actor()) }
    fun deleteOperation(operation: WorkOrderOperationEntity) = launchAction("تم حذف العملية") { repository.deleteOperation(operation, actor()) }

    fun addConfirmation(confirmation: WorkOrderConfirmationEntity, operation: WorkOrderOperationEntity) =
        launchAction("تم تسجيل التأكيد") { repository.addConfirmation(confirmation, operation, actor()) }
    fun deleteConfirmation(confirmation: WorkOrderConfirmationEntity) = launchAction("تم حذف التأكيد") { repository.deleteConfirmation(confirmation, actor()) }

    fun addWorkOrderPhoto(orderId: Long, path: String) = launchAction("تم إرفاق صورة الدليل") { repository.addWorkOrderPhoto(orderId, path, actor()) }
    fun deleteWorkOrderPhoto(photo: WorkOrderPhotoEntity) = launchAction("تم حذف الصورة") { repository.deleteWorkOrderPhoto(photo, actor()) }

    fun saveTaskList(taskList: TaskListEntity) = launchAction("تم حفظ القالب") { repository.saveTaskList(taskList, actor()) }
    fun deleteTaskList(taskList: TaskListEntity) = launchAction("تم حذف القالب") { repository.deleteTaskList(taskList, actor()) }
    fun saveTaskListOperation(operation: TaskListOperationEntity) = launchAction("تم حفظ عملية القالب") { repository.saveTaskListOperation(operation, actor()) }
    fun deleteTaskListOperation(operation: TaskListOperationEntity) = launchAction("تم حذف عملية القالب") { repository.deleteTaskListOperation(operation, actor()) }
    fun generateWorkOrderFromPm(pm: PreventiveMaintenanceEntity) = launchAction("تم توليد أمر عمل من الخطة") { repository.generateWorkOrderFromPm(pm, actor()) }

    fun savePermit(permit: WorkPermitEntity) = launchAction("تم حفظ التصريح") { repository.savePermit(permit, actor()) }
    fun setPermitStatus(permit: WorkPermitEntity, approved: Boolean) =
        launchAction(if (approved) "تم اعتماد التصريح" else "تم رفض التصريح") { repository.setPermitStatus(permit, approved, actor()) }
    fun deletePermit(permit: WorkPermitEntity) = launchAction("تم حذف التصريح") { repository.deletePermit(permit, actor()) }

    // ----- CAPA -----
    fun saveCapa(item: CapaEntity) = launchAction("تم حفظ الإجراء") { repository.saveCapa(item, actor()) }
    fun updateCapaStatus(item: CapaEntity, status: String) = launchAction("تم تحديث حالة الإجراء") { repository.updateCapaStatus(item, status, actor()) }
    fun deleteCapa(item: CapaEntity) = launchAction("تم حذف الإجراء") { repository.deleteCapa(item, actor()) }

    /** Imports a maintenance-kit workbook the user picked (content URI). */
    fun importExcel(context: Context, uri: Uri) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val sheets = context.contentResolver.openInputStream(uri)?.use { XlsxReader.read(it) }
                        ?: throw IllegalStateException("تعذّر فتح الملف")
                    repository.importMachineKit(sheets, actor())
                }
            }.onSuccess { _message.value = it }
                .onFailure { _message.value = "تعذّر الاستيراد: ${it.message ?: "ملف غير صالح"}" }
        }
    }

    /** Imports the maintenance-kit workbook bundled with the app (assets/). */
    fun importBundledKit(context: Context, assetFile: String = "FVV_maintenance_kit.xlsx") {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val sheets = context.assets.open(assetFile).use { XlsxReader.read(it) }
                    repository.importMachineKit(sheets, actor())
                }
            }.onSuccess { _message.value = it }
                .onFailure { _message.value = "تعذّر استيراد القالب المرفق: ${it.message ?: "غير معروف"}" }
        }
    }

    /** Writes a full JSON backup of the database to the user-chosen file. */
    fun exportBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val content = repository.exportBackup()
                    context.contentResolver.openOutputStream(uri)?.use { it.write(content.toByteArray()) }
                        ?: throw IllegalStateException("تعذّر إنشاء الملف")
                }
            }.onSuccess { _message.value = "تم حفظ النسخة الاحتياطية بنجاح" }
                .onFailure { _message.value = "تعذّر التصدير: ${it.message ?: "غير معروف"}" }
        }
    }

    /** Replaces all data with the contents of a chosen JSON backup file. */
    fun importBackup(context: Context, uri: Uri) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val content = context.contentResolver.openInputStream(uri)?.use { it.readBytes().decodeToString() }
                        ?: throw IllegalStateException("تعذّر فتح الملف")
                    repository.importBackup(content)
                }
            }.onSuccess { _message.value = "تمت الاستعادة بنجاح (${it.totalRecords} سجل)" }
                .onFailure { _message.value = "تعذّر الاستعادة: ${it.message ?: "ملف غير صالح"}" }
        }
    }

    /** Renders the given work order to a printable PDF at the chosen file location. */
    fun exportWorkOrderPdf(context: Context, uri: Uri, order: WorkOrderEntity) {
        viewModelScope.launch {
            runCatching {
                withContext(Dispatchers.IO) {
                    val asset = repository.assets.first().firstOrNull { it.id == order.assetId }
                    val operations = repository.workOrderOperations.first().filter { it.orderId == order.id }
                    val materials = repository.transactionsForOrder(order.id)
                    val partsById = repository.spareParts.first().associateBy { it.id }
                    val permits = repository.workPermits.first().filter { it.orderId == order.id }
                    context.contentResolver.openOutputStream(uri)?.use { out ->
                        PdfExporter.writeWorkOrder(
                            out = out,
                            order = order,
                            asset = asset,
                            operations = operations,
                            materials = materials,
                            partsById = partsById,
                            permits = permits,
                            generatedAt = DateStrings.now()
                        )
                    } ?: throw IllegalStateException("تعذّر إنشاء الملف")
                }
            }.onSuccess { _message.value = "تم تصدير أمر العمل PDF" }
                .onFailure { _message.value = "تعذّر تصدير PDF: ${it.message ?: "غير معروف"}" }
        }
    }

    fun clearMessage() {
        _message.value = null
    }

    private fun launchAction(successMessage: String, block: suspend () -> Unit) {
        viewModelScope.launch {
            runCatching { block() }
                .onSuccess { _message.value = successMessage }
                .onFailure { _message.value = "حدث خطأ: ${it.message ?: "غير معروف"}" }
        }
    }
}

class CmmsViewModelFactory(private val repository: CmmsRepository) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CmmsViewModel::class.java)) {
            return CmmsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

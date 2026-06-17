package com.alhadi.cmms.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.alhadi.cmms.data.CmmsRepository
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
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
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

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

    val capaActions: StateFlow<List<CapaEntity>> = repository.capaActions
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetDocuments: StateFlow<List<AssetDocumentEntity>> = repository.assetDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetCharacteristics: StateFlow<List<AssetCharacteristicEntity>> = repository.assetCharacteristics
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

    fun issuePart(part: SparePartEntity) = launchAction("تم صرف قطعة من المخزون") {
        repository.issuePart(part, actor = actor())
    }

    fun receivePart(part: SparePartEntity) = launchAction("تم إضافة قطعة إلى المخزون") {
        repository.receivePart(part, actor = actor())
    }

    fun addTechnician() = launchAction("تمت إضافة فني تجريبي") {
        repository.addTechnician(actor())
    }

    fun resetSampleData() = launchAction("تمت إعادة تعبئة البيانات التجريبية") {
        repository.seedSampleData(replace = true)
    }

    // ----- CRUD: Assets -----
    fun saveAsset(asset: AssetEntity) = launchAction("تم حفظ الأصل") { repository.saveAsset(asset, actor()) }
    fun deleteAsset(asset: AssetEntity) = launchAction("تم حذف الأصل") { repository.deleteAsset(asset, actor()) }
    fun changeAssetStatus(asset: AssetEntity, status: String) = launchAction("تم تغيير حالة الأصل") { repository.changeAssetStatus(asset, status, actor()) }

    // ----- CRUD: Spare parts -----
    fun savePart(part: SparePartEntity) = launchAction("تم حفظ القطعة") { repository.savePart(part, actor()) }
    fun deletePart(part: SparePartEntity) = launchAction("تم حذف القطعة") { repository.deletePart(part, actor()) }

    // ----- CRUD: Work orders -----
    fun saveWorkOrder(workOrder: WorkOrderEntity) = launchAction("تم حفظ أمر العمل") { repository.saveWorkOrder(workOrder, actor()) }
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

    // ----- Asset documents -----
    fun saveAssetDocument(doc: AssetDocumentEntity) = launchAction("تم حفظ المستند") { repository.saveAssetDocument(doc, actor()) }
    fun deleteAssetDocument(doc: AssetDocumentEntity) = launchAction("تم حذف المستند") { repository.deleteAssetDocument(doc, actor()) }

    // ----- Asset characteristics -----
    fun saveCharacteristic(item: AssetCharacteristicEntity) = launchAction("تم حفظ الخاصية") { repository.saveCharacteristic(item, actor()) }
    fun deleteCharacteristic(item: AssetCharacteristicEntity) = launchAction("تم حذف الخاصية") { repository.deleteCharacteristic(item, actor()) }

    // ----- CAPA -----
    fun saveCapa(item: CapaEntity) = launchAction("تم حفظ الإجراء") { repository.saveCapa(item, actor()) }
    fun updateCapaStatus(item: CapaEntity, status: String) = launchAction("تم تحديث حالة الإجراء") { repository.updateCapaStatus(item, status, actor()) }
    fun deleteCapa(item: CapaEntity) = launchAction("تم حذف الإجراء") { repository.deleteCapa(item, actor()) }

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

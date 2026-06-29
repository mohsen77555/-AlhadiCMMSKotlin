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
import com.alhadi.cmms.data.entity.WorkOrderHistoryEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import com.alhadi.cmms.data.entity.AssetInstallationEntity
import com.alhadi.cmms.data.entity.AssetStatusHistoryEntity
import com.alhadi.cmms.data.entity.WorkOrderMaterialEntity
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
class CmmsViewModel(internal val repository: CmmsRepository) : ViewModel() {
    private val today = DateStrings.today()

    init {
        // Start Firestore down-sync (no-op without Firebase config).
        repository.startCloudSync(viewModelScope)
    }

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

    val suppliers: StateFlow<List<SupplierEntity>> = repository.suppliers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val purchaseOrders: StateFlow<List<PurchaseOrderEntity>> = repository.purchaseOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val purchaseOrderLines: StateFlow<List<PurchaseOrderLineEntity>> = repository.purchaseOrderLines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetInstallations: StateFlow<List<AssetInstallationEntity>> = repository.assetInstallations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val assetStatusHistory: StateFlow<List<AssetStatusHistoryEntity>> = repository.assetStatusHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workOrderMaterials: StateFlow<List<WorkOrderMaterialEntity>> = repository.workOrderMaterials
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

    val workOrderHistory: StateFlow<List<WorkOrderHistoryEntity>> = repository.workOrderHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val taskLists: StateFlow<List<TaskListEntity>> = repository.taskLists
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val taskListOperations: StateFlow<List<TaskListOperationEntity>> = repository.taskListOperations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val workPermits: StateFlow<List<WorkPermitEntity>> = repository.workPermits
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    internal val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    internal fun actor(): String = currentUser.value?.name ?: "System"

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
        repository.signOutCloud()
    }

    fun clearLoginError() {
        _loginError.value = null
    }


    fun clearMessage() {
        _message.value = null
    }

    internal fun launchAction(successMessage: String, block: suspend () -> Unit) {
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

package com.alhadi.cmms.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alhadi.cmms.data.MovementType
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.notify.Reminders
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
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
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.ui.theme.AccentBlue
import com.alhadi.cmms.ui.theme.AccentBrown
import com.alhadi.cmms.ui.theme.AccentGreen
import com.alhadi.cmms.ui.theme.AccentNavy
import com.alhadi.cmms.ui.theme.AccentOrange
import com.alhadi.cmms.ui.theme.AccentPurple
import com.alhadi.cmms.ui.theme.AccentRed
import com.alhadi.cmms.ui.theme.AccentTeal
import com.alhadi.cmms.ui.theme.StatusInfo
import com.alhadi.cmms.ui.theme.StatusRunning
import com.alhadi.cmms.ui.theme.StatusRunningContainer
import com.alhadi.cmms.ui.theme.StatusStopped
import com.alhadi.cmms.ui.theme.StatusStoppedContainer
import com.alhadi.cmms.ui.theme.priorityTone
import com.alhadi.cmms.ui.theme.statusTone
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.util.ImageStore
import com.alhadi.cmms.viewmodel.CmmsViewModel
import com.alhadi.cmms.viewmodel.DashboardStats
import java.util.Locale
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Navigation model
// ---------------------------------------------------------------------------

internal enum class BottomTab(val label: String, val icon: ImageVector, val accent: Color) {
    Home("الرئيسية", Icons.Filled.Home, AccentNavy),
    WorkOrders("أوامر العمل", Icons.Filled.Assignment, AccentBlue),
    Supervision("الإشراف", Icons.Filled.Verified, AccentTeal),
    Assets("الأصول", Icons.Filled.PrecisionManufacturing, AccentGreen),
    More("المزيد", Icons.Filled.GridView, AccentBrown)
}

internal enum class MoreRoute { Notifications, Inventory, SerialNumbers, Reports, Audit, Admin, PreventiveMaintenance, TaskLists, Meters, Locations, Warehouses, OrgUnits, Capa, Failures }

internal data class ScreenMeta(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CmmsApp(viewModel: CmmsViewModel) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val workOrders by viewModel.workOrders.collectAsStateWithLifecycle()
    val preventiveMaintenance by viewModel.preventiveMaintenance.collectAsStateWithLifecycle()
    val serialNumberProfiles by viewModel.serialNumberProfiles.collectAsStateWithLifecycle()
    val serialNumbers by viewModel.serialNumbers.collectAsStateWithLifecycle()
    val serialNumberMovements by viewModel.serialNumberMovements.collectAsStateWithLifecycle()
    val spareParts by viewModel.spareParts.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val auditLog by viewModel.auditLog.collectAsStateWithLifecycle()
    val measuringPoints by viewModel.measuringPoints.collectAsStateWithLifecycle()
    val readings by viewModel.readings.collectAsStateWithLifecycle()
    val locations by viewModel.functionalLocations.collectAsStateWithLifecycle()
    val warehouses by viewModel.warehouses.collectAsStateWithLifecycle()
    val orgUnits by viewModel.orgUnits.collectAsStateWithLifecycle()
    val capaActions by viewModel.capaActions.collectAsStateWithLifecycle()
    val assetDocuments by viewModel.assetDocuments.collectAsStateWithLifecycle()
    val assetCharacteristics by viewModel.assetCharacteristics.collectAsStateWithLifecycle()
    val assetBomHeaders by viewModel.assetBomHeaders.collectAsStateWithLifecycle()
    val assetBom by viewModel.assetBom.collectAsStateWithLifecycle()
    val assetMovements by viewModel.assetMovements.collectAsStateWithLifecycle()
    val pmChecklist by viewModel.pmChecklist.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val workOrderOperations by viewModel.workOrderOperations.collectAsStateWithLifecycle()
    val workOrderConfirmations by viewModel.workOrderConfirmations.collectAsStateWithLifecycle()
    val workOrderPhotos by viewModel.workOrderPhotos.collectAsStateWithLifecycle()
    val workPermits by viewModel.workPermits.collectAsStateWithLifecycle()
    val taskLists by viewModel.taskLists.collectAsStateWithLifecycle()
    val taskListOperations by viewModel.taskListOperations.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.Home) }
    var moreRoute by rememberSaveable { mutableStateOf<MoreRoute?>(null) }
    val actorName = currentUser?.name ?: "Unassigned"

    val isAdmin = currentUser?.isAdmin == true
    val canManage = currentUser?.canManage == true

    val appContext = LocalContext.current
    val excelPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importExcel(appContext, uri)
    }
    val backupExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri != null) viewModel.exportBackup(appContext, uri)
    }
    val backupImportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) viewModel.importBackup(appContext, uri)
    }
    var pendingPdfOrder by remember { mutableStateOf<WorkOrderEntity?>(null) }
    val pdfExportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri ->
        val order = pendingPdfOrder
        if (uri != null && order != null) viewModel.exportWorkOrderPdf(appContext, uri, order)
        pendingPdfOrder = null
    }

    BackHandler(enabled = selectedTab == BottomTab.More && moreRoute != null) {
        moreRoute = null
    }

    LaunchedEffect(message) {
        val text = message
        if (!text.isNullOrBlank()) {
            snackbarHostState.showSnackbar(text)
            viewModel.clearMessage()
        }
    }

    val meta = screenMeta(selectedTab, moreRoute)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppHeader(
                    meta = meta,
                    user = currentUser,
                    openWorkOrders = stats.openWorkOrders,
                    assetsCount = stats.assets,
                    showBack = selectedTab == BottomTab.More && moreRoute != null,
                    onBack = { moreRoute = null },
                    onLogout = viewModel::logout,
                    onSchedule = { selectedTab = BottomTab.Supervision },
                    onMaintenance = { selectedTab = BottomTab.Supervision },
                    onAlerts = { selectedTab = BottomTab.Home }
                )
            },
            bottomBar = {
                AppBottomBar(
                    selected = selectedTab,
                    isAdmin = isAdmin,
                    onSelect = {
                        selectedTab = it
                        if (it != BottomTab.More) moreRoute = null
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            val assetMap = assets.associateBy { it.id }
            AnimatedContent(
                targetState = meta.title,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen"
            ) {
                when (selectedTab) {
                    BottomTab.Home -> DashboardScreen(
                        innerPadding = innerPadding,
                        stats = stats,
                        assets = assets,
                        workOrders = workOrders,
                        parts = spareParts,
                        pmItems = preventiveMaintenance,
                        notifications = notifications,
                        onReports = { selectedTab = BottomTab.More; moreRoute = MoreRoute.Reports },
                        onGovernance = { selectedTab = BottomTab.More; moreRoute = MoreRoute.Audit },
                        onOpenTab = { selectedTab = it; if (it != BottomTab.More) moreRoute = null },
                        onOpenMore = { selectedTab = BottomTab.More; moreRoute = it }
                    )

                    BottomTab.WorkOrders -> WorkOrdersScreen(
                        innerPadding = innerPadding,
                        workOrders = workOrders,
                        assets = assets,
                        assetMap = assetMap,
                        operations = workOrderOperations,
                        confirmations = workOrderConfirmations,
                        photos = workOrderPhotos,
                        permits = workPermits,
                        parts = spareParts,
                        transactions = transactions,
                        bomHeaders = assetBomHeaders,
                        bom = assetBom,
                        canManage = canManage,
                        defaultAssignee = actorName,
                        onIssueMaterial = viewModel::issuePartToWorkOrder,
                        onExportPdf = { order ->
                            pendingPdfOrder = order
                            pdfExportLauncher.launch("WO-${order.id}-${DateStrings.today()}.pdf")
                        },
                        onSave = viewModel::saveWorkOrder,
                        onDelete = viewModel::deleteWorkOrder,
                        onUpdateStatus = viewModel::updateWorkOrderStatus,
                        onApprove = viewModel::setWorkOrderApproval,
                        onSaveOperation = viewModel::saveOperation,
                        onSetOperationStatus = viewModel::setOperationStatus,
                        onDeleteOperation = viewModel::deleteOperation,
                        onConfirmOperation = viewModel::addConfirmation,
                        onAddPhoto = viewModel::addWorkOrderPhoto,
                        onDeletePhoto = viewModel::deleteWorkOrderPhoto,
                        onSavePermit = viewModel::savePermit,
                        onSetPermitStatus = viewModel::setPermitStatus,
                        onDeletePermit = viewModel::deletePermit
                    )

                    BottomTab.Supervision -> PreventiveMaintenanceScreen(
                        innerPadding = innerPadding,
                        pmItems = preventiveMaintenance,
                        assets = assets,
                        assetMap = assetMap,
                        canManage = canManage,
                        checklist = pmChecklist,
                        taskLists = taskLists,
                        onSave = viewModel::savePreventiveMaintenance,
                        onDelete = viewModel::deletePreventiveMaintenance,
                        onDone = viewModel::markPreventiveMaintenanceDone,
                        onSaveChecklistItem = viewModel::saveChecklistItem,
                        onSetChecklistResult = viewModel::setChecklistResult,
                        onDeleteChecklistItem = viewModel::deleteChecklistItem,
                        onGenerateOrder = viewModel::generateWorkOrderFromPm
                    )

                    BottomTab.Assets -> AssetsScreen(
                        innerPadding = innerPadding,
                        assets = assets,
                        workOrders = workOrders,
                        pmItems = preventiveMaintenance,
                        locations = locations,
                        documents = assetDocuments,
                        characteristics = assetCharacteristics,
                        bomHeaders = assetBomHeaders,
                        bomItems = assetBom,
                        movements = assetMovements,
                        spareParts = spareParts,
                        serials = serialNumbers,
                        serialMovements = serialNumberMovements,
                        orgUnits = orgUnits,
                        canManage = canManage,
                        isAdmin = isAdmin,
                        defaultAssignee = actorName,
                        onSave = viewModel::saveAsset,
                        onDelete = viewModel::deleteAsset,
                        onChangeStatus = viewModel::changeAssetStatus,
                        onSaveWorkOrder = viewModel::saveWorkOrder,
                        onUpdateWorkOrderStatus = viewModel::updateWorkOrderStatus,
                        onSaveDocument = viewModel::saveAssetDocument,
                        onDeleteDocument = viewModel::deleteAssetDocument,
                        onSaveCharacteristic = viewModel::saveCharacteristic,
                        onDeleteCharacteristic = viewModel::deleteCharacteristic,
                        onSaveBomHeader = viewModel::saveBomHeader,
                        onDeleteBomHeader = viewModel::deleteBomHeader,
                        onSaveBom = viewModel::saveBomItem,
                        onDeleteBom = viewModel::deleteBomItem,
                        onMove = viewModel::performAssetMovement
                    )

                    BottomTab.More -> when (moreRoute) {
                        null -> MoreGrid(
                            innerPadding = innerPadding,
                            isAdmin = isAdmin,
                            canManage = canManage,
                            onOpen = { moreRoute = it },
                            onImportBundled = { viewModel.importBundledKit(appContext) },
                            onPickExcel = { excelPicker.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/octet-stream", "*/*")) },
                            onLogout = viewModel::logout
                        )
                        MoreRoute.Notifications -> NotificationsScreen(
                            innerPadding = innerPadding,
                            notifications = notifications,
                            assets = assets,
                            assetMap = assetMap,
                            canManage = canManage,
                            onSave = viewModel::saveNotification,
                            onSetStatus = viewModel::setNotificationStatus,
                            onCreateOrder = viewModel::createOrderFromNotification,
                            onDelete = viewModel::deleteNotification
                        )
                        MoreRoute.Inventory -> InventoryScreen(
                            innerPadding = innerPadding,
                            parts = spareParts,
                            profiles = serialNumberProfiles,
                            serials = serialNumbers,
                            transactions = transactions,
                            warehouses = warehouses,
                            canReceive = canManage,
                            canManage = canManage,
                            onOpenSerialNumbers = { moreRoute = MoreRoute.SerialNumbers },
                            onIssue = viewModel::issuePart,
                            onReceive = viewModel::receivePart,
                            onSave = viewModel::savePart,
                            onDelete = viewModel::deletePart
                        )
                        MoreRoute.SerialNumbers -> SerialNumbersScreen(
                            innerPadding = innerPadding,
                            profiles = serialNumberProfiles,
                            serials = serialNumbers,
                            movements = serialNumberMovements,
                            parts = spareParts,
                            assets = assets,
                            workOrders = workOrders,
                            canManage = canManage,
                            onSaveProfile = viewModel::saveSerialProfile,
                            onDeleteProfile = viewModel::deleteSerialProfile,
                            onCreateMaster = viewModel::createSerialMaster,
                            onReceive = viewModel::receiveSerializedPart,
                            onIssue = viewModel::issueSerializedPart,
                            onTransfer = viewModel::transferSerialNumber,
                            onInstall = viewModel::installSerialNumber,
                            onDismantle = viewModel::dismantleSerialNumber,
                            onReconcile = viewModel::reconcileSerializedStock,
                            onDeleteSerial = viewModel::deleteSerialNumber
                        )
                        MoreRoute.Reports -> ReportsScreen(
                            innerPadding = innerPadding,
                            stats = stats,
                            assets = assets,
                            workOrders = workOrders,
                            parts = spareParts,
                            pmItems = preventiveMaintenance
                        )
                        MoreRoute.Audit -> AuditScreen(innerPadding = innerPadding, auditLog = auditLog)
                        MoreRoute.Meters -> MetersScreen(
                            innerPadding = innerPadding,
                            points = measuringPoints,
                            readings = readings,
                            assetMap = assetMap,
                            assets = assets,
                            canManage = canManage,
                            onSavePoint = viewModel::saveMeasuringPoint,
                            onDeletePoint = viewModel::deleteMeasuringPoint,
                            onAddReading = viewModel::addReading
                        )
                        MoreRoute.Locations -> LocationsScreen(
                            innerPadding = innerPadding,
                            locations = locations,
                            assets = assets,
                            orgUnits = orgUnits,
                            canManage = canManage,
                            onSave = viewModel::saveFunctionalLocation,
                            onDelete = viewModel::deleteFunctionalLocation
                        )
                        MoreRoute.Warehouses -> WarehousesScreen(
                            innerPadding = innerPadding,
                            warehouses = warehouses,
                            parts = spareParts,
                            canManage = canManage,
                            onSave = viewModel::saveWarehouse,
                            onDelete = viewModel::deleteWarehouse
                        )
                        MoreRoute.OrgUnits -> OrgUnitsScreen(
                            innerPadding = innerPadding,
                            units = orgUnits,
                            assets = assets,
                            canManage = canManage,
                            onSave = viewModel::saveOrgUnit,
                            onDelete = viewModel::deleteOrgUnit
                        )
                        MoreRoute.Capa -> CapaScreen(
                            innerPadding = innerPadding,
                            items = capaActions,
                            assets = assets,
                            assetMap = assetMap,
                            canManage = canManage,
                            defaultAssignee = actorName,
                            onSave = viewModel::saveCapa,
                            onUpdateStatus = viewModel::updateCapaStatus,
                            onDelete = viewModel::deleteCapa
                        )
                        MoreRoute.Failures -> FailureAnalysisScreen(
                            innerPadding = innerPadding,
                            workOrders = workOrders,
                            assetMap = assetMap
                        )
                        MoreRoute.Admin -> AdminScreen(
                            innerPadding = innerPadding,
                            users = users,
                            currentUser = currentUser,
                            onAddTechnician = viewModel::addTechnician,
                            onResetSampleData = viewModel::resetSampleData,
                            onExportBackup = { backupExportLauncher.launch("alhadi-cmms-backup-${DateStrings.today()}.json") },
                            onImportBackup = { backupImportLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                            onRunReminders = { Reminders.runNow(appContext) },
                            onSave = viewModel::saveUser,
                            onSetActive = viewModel::setUserActive,
                            onDelete = viewModel::deleteUser
                        )
                        MoreRoute.PreventiveMaintenance -> PreventiveMaintenanceScreen(
                            innerPadding = innerPadding,
                            pmItems = preventiveMaintenance,
                            assets = assets,
                            assetMap = assetMap,
                            canManage = canManage,
                            checklist = pmChecklist,
                            taskLists = taskLists,
                            onSave = viewModel::savePreventiveMaintenance,
                            onDelete = viewModel::deletePreventiveMaintenance,
                            onDone = viewModel::markPreventiveMaintenanceDone,
                            onSaveChecklistItem = viewModel::saveChecklistItem,
                            onSetChecklistResult = viewModel::setChecklistResult,
                            onDeleteChecklistItem = viewModel::deleteChecklistItem,
                            onGenerateOrder = viewModel::generateWorkOrderFromPm
                        )
                        MoreRoute.TaskLists -> TaskListsScreen(
                            innerPadding = innerPadding,
                            taskLists = taskLists,
                            operations = taskListOperations,
                            canManage = canManage,
                            onSaveTaskList = viewModel::saveTaskList,
                            onDeleteTaskList = viewModel::deleteTaskList,
                            onSaveOperation = viewModel::saveTaskListOperation,
                            onDeleteOperation = viewModel::deleteTaskListOperation
                        )
                    }
                }
            }
        }

    }
}

internal fun screenMeta(tab: BottomTab, route: MoreRoute?): ScreenMeta = when (tab) {
    BottomTab.Home -> ScreenMeta("الرئيسية", "لوحة المؤشرات والتنبيهات", Icons.Filled.Home, AccentNavy)
    BottomTab.WorkOrders -> ScreenMeta("أوامر العمل", "إنشاء، إصدار، متابعة، وإغلاق", Icons.Filled.Assignment, AccentBlue)
    BottomTab.Supervision -> ScreenMeta("الإشراف والصيانة", "الصيانة الدورية والمتابعة", Icons.Filled.Verified, AccentTeal)
    BottomTab.Assets -> ScreenMeta("الأصول", "سجل الأصول والمعدات", Icons.Filled.PrecisionManufacturing, AccentGreen)
    BottomTab.More -> when (route) {
        null -> ScreenMeta("المزيد", "كل الوحدات والإعدادات", Icons.Filled.GridView, AccentBrown)
        MoreRoute.Notifications -> ScreenMeta("البلاغات", "بلاغات الصيانة وتحويلها لأوامر", Icons.Filled.NotificationsActive, AccentRed)
        MoreRoute.Inventory -> ScreenMeta("المخزون", "قطع الغيار والحركات", Icons.Filled.Inventory2, AccentPurple)
        MoreRoute.SerialNumbers -> ScreenMeta("الأرقام التسلسلية", "تتبّع الوحدات والحركات والمواقع", Icons.Filled.QrCodeScanner, AccentTeal)
        MoreRoute.Reports -> ScreenMeta("التقارير", "مؤشرات وتصدير وتحليلات", Icons.Filled.Analytics, AccentBlue)
        MoreRoute.Audit -> ScreenMeta("سجل الحوكمة", "من فعل ماذا ومتى", Icons.Filled.History, AccentRed)
        MoreRoute.Admin -> ScreenMeta("الإدارة", "المستخدمون والصلاحيات", Icons.Filled.AdminPanelSettings, AccentOrange)
        MoreRoute.PreventiveMaintenance -> ScreenMeta("الصيانة الدورية", "جدول المهام الوقائية", Icons.Filled.EventRepeat, AccentTeal)
        MoreRoute.TaskLists -> ScreenMeta("قوالب العمل", "قوالب العمليات للخطط الوقائية", Icons.AutoMirrored.Filled.List, AccentBlue)
        MoreRoute.Meters -> ScreenMeta("العدّادات والقراءات", "مراقبة الأداء والقياسات", Icons.Filled.Speed, AccentPurple)
        MoreRoute.Locations -> ScreenMeta("المواقع الفنية", "هرمية المواقع والمصانع", Icons.Filled.AccountTree, AccentGreen)
        MoreRoute.Warehouses -> ScreenMeta("المستودعات", "المخازن وأمناء العهدة", Icons.Filled.Warehouse, AccentPurple)
        MoreRoute.OrgUnits -> ScreenMeta("الوحدات التنظيمية", "الشركات والمصانع ومراكز العمل والتكلفة", Icons.Filled.CorporateFare, AccentNavy)
        MoreRoute.Capa -> ScreenMeta("الإجراءات CAPA", "إجراءات تصحيحية ووقائية", Icons.Filled.FactCheck, AccentOrange)
        MoreRoute.Failures -> ScreenMeta("تحليل الأعطال", "MTTR و MTBF وتكرار الأعطال", Icons.Filled.TrendingUp, AccentRed)
    }
}

// ---------------------------------------------------------------------------
// Header
// ---------------------------------------------------------------------------

@Composable
internal fun AppHeader(
    meta: ScreenMeta,
    user: UserEntity?,
    openWorkOrders: Int,
    assetsCount: Int,
    showBack: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onSchedule: () -> Unit,
    onMaintenance: () -> Unit,
    onAlerts: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title row: section icon (right) + title block + production badge (left).
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(meta.accent, RoundedCornerShape(14.dp))
                        .clickable(enabled = showBack, onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (showBack) Icons.AutoMirrored.Filled.ArrowBack else meta.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Alhadi CMMS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            meta.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(meta.accent, CircleShape)
                        )
                    }
                    Text(
                        meta.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    ProductionBadge()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        user?.role ?: "محلي",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Status chips row.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SyncCard()
                StatCard("أمر مفتوح", openWorkOrders.toString(), StatusInfo)
                StatCard("أصل", assetsCount.toString(), StatusRunning)
            }

            // Quick action pills row.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickPill("محرك الجدولة", Icons.Filled.Schedule, onSchedule)
                QuickPill("إدارة الصيانة", Icons.Filled.Build, onMaintenance)
                QuickPill("التنبيهات", Icons.Filled.NotificationsActive, onAlerts)
                QuickPill("تسجيل الخروج", Icons.AutoMirrored.Filled.Logout, onLogout)
            }
        }
    }
}

@Composable
internal fun ProductionBadge() {
    Surface(shape = CircleShape, color = StatusRunningContainer) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(modifier = Modifier.size(7.dp).background(StatusRunning, CircleShape))
            Text(
                "Production",
                style = MaterialTheme.typography.labelMedium,
                color = StatusRunning,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
internal fun SyncCard() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = StatusRunningContainer
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Text(
                "حالة المزامنة",
                style = MaterialTheme.typography.labelMedium,
                color = StatusRunning,
                fontWeight = FontWeight.Bold
            )
            Text(
                "محلي • جاهز للعمل بدون إنترنت",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun StatCard(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
internal fun QuickPill(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------------------------------------------------------------------
// Bottom navigation (custom, filled-square selected icon)
// ---------------------------------------------------------------------------

@Composable
internal fun AppBottomBar(selected: BottomTab, isAdmin: Boolean, onSelect: (BottomTab) -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab.entries.forEach { tab ->
                val isSel = tab == selected
                Column(
                    modifier = Modifier
                        .clickable { onSelect(tab) }
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isSel) tab.accent else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            tab.icon,
                            contentDescription = tab.label,
                            tint = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSel) tab.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Dashboard
// ---------------------------------------------------------------------------

@Composable
internal fun DashboardScreen(
    innerPadding: PaddingValues,
    stats: DashboardStats,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    parts: List<SparePartEntity>,
    pmItems: List<PreventiveMaintenanceEntity>,
    notifications: List<MaintenanceNotificationEntity>,
    onReports: () -> Unit,
    onGovernance: () -> Unit,
    onOpenTab: (BottomTab) -> Unit,
    onOpenMore: (MoreRoute) -> Unit
) {
    val today = DateStrings.today()
    val soon = DateStrings.daysFromToday(30)
    val criticalAssets = assets.count { it.status != "Running" }
    val overdue = workOrders.count { it.status != "Closed" && it.dueAt < today }
    val inProgress = workOrders.count { it.status == "In Progress" }
    val urgent = workOrders.count { it.priority == "Critical" || it.priority == "High" }
    val assigned = workOrders.count { it.assignedTo.isNotBlank() }
    val governance = if (workOrders.isEmpty()) 100 else (assigned * 100 / workOrders.size)
    val pendingApprovals = workOrders.filter { it.approvalStatus == "Pending" }
    val expiringWarranty = assets.filter { it.warrantyEnd.isNotBlank() && it.warrantyEnd in today..soon }
    val totalCost = workOrders.sumOf { it.totalCost() }
    val failures = workOrders.filter { it.isFailure }
    val downtime = failures.sumOf { it.downtimeHours }
    val windowHours = (assets.size.coerceAtLeast(1)) * 30.0 * 24.0
    val availability = ((windowHours - downtime) / windowHours * 100.0).coerceIn(0.0, 100.0)
    val woOpen = workOrders.count { it.status == "Open" }
    val woProgress = workOrders.count { it.status == "In Progress" }
    val woTech = workOrders.count { it.status == "Technically Completed" }
    val woClosed = workOrders.count { it.status == "Closed" }
    val statusSegments = listOf(
        ChartSegment("مفتوح", woOpen, AccentBlue),
        ChartSegment("قيد التنفيذ", woProgress, AccentOrange),
        ChartSegment("مكتمل فنياً", woTech, AccentTeal),
        ChartSegment("مغلق", woClosed, AccentGreen)
    )
    val laborCost = workOrders.sumOf { it.laborCost() }
    val partsCostTotal = workOrders.sumOf { it.partsCost }
    val maxCost = listOf(laborCost, partsCostTotal, 1.0).max()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { DotSectionTitle("واجهة الإدارة السريعة", AccentOrange) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                KpiTile("أصول حرجة", criticalAssets.toString(), AccentRed, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.Assets) })
                KpiTile("CAPA", stats.capa.toString(), AccentOrange, Modifier.weight(1f), onClick = { onOpenMore(MoreRoute.Capa) })
                KpiTile("متأخرة", overdue.toString(), MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
                KpiTile("أوامر مفتوحة", stats.openWorkOrders.toString(), AccentBlue, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                BigActionButton("التقارير", AccentBlue, Modifier.weight(1f), onReports)
                BigActionButton("حوكمة $governance%", AccentNavy, Modifier.weight(1f), onGovernance)
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("توزيع أوامر العمل")
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DonutChart(
                            segments = statusSegments,
                            centerValue = workOrders.size.toString(),
                            centerLabel = "أمر عمل"
                        )
                        ChartLegend(statusSegments, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RingGauge(percent = availability.toFloat(), color = AccentGreen, centerLabel = "التوفّر")
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader("الأداء والتكاليف")
                        BarMeter("تكلفة العمالة", (laborCost / maxCost).toFloat(), AccentBlue, money(laborCost))
                        BarMeter("تكلفة قطع الغيار", (partsCostTotal / maxCost).toFloat(), AccentPurple, money(partsCostTotal))
                        InfoRow("زمن التوقف", "${"%.0f".format(downtime)} ساعة")
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                KpiTile("مفتوح", stats.openWorkOrders.toString(), AccentBlue, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
                KpiTile("قيد التنفيذ", inProgress.toString(), AccentOrange, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
                KpiTile("متأخر", overdue.toString(), AccentRed, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
                KpiTile("طارئ", urgent.toString(), AccentPurple, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
            }
        }

        item { DotSectionTitle("يحتاج انتباهك", AccentRed) }

        val warningAssets = assets.filter { it.status != "Running" }.take(4)
        val lowStockParts = parts.filter { it.onHandQty <= it.minQty }.take(4)
        val duePm = pmItems.filter { DateStrings.isDueOrOverdue(it.nextDueAt) }.take(4)
        val openNotifications = notifications.filter {
            (it.status == "New" || it.status == "Screened") && (it.priority == "Critical" || it.priority == "High")
        }.take(4)

        if (warningAssets.isEmpty() && lowStockParts.isEmpty() && duePm.isEmpty() &&
            pendingApprovals.isEmpty() && expiringWarranty.isEmpty() && openNotifications.isEmpty()
        ) {
            item { CalmCard() }
        } else {
            items(openNotifications, key = { "nt-${it.id}" }) { ntf ->
                AlertRow(Icons.Filled.NotificationsActive, AccentRed, "${ntf.number} • ${ntf.title}", "بلاغ ${notificationStatusLabel(ntf.status)} • ${ntf.priority}", onClick = { onOpenMore(MoreRoute.Notifications) })
            }
            items(pendingApprovals.take(4), key = { "ap-${it.id}" }) { wo ->
                AlertRow(Icons.Filled.FactCheck, AccentPurple, wo.title, "بانتظار اعتماد المشرف", onClick = { onOpenTab(BottomTab.WorkOrders) })
            }
            items(warningAssets, key = { "a-${it.id}" }) { asset ->
                AlertRow(Icons.Filled.Warning, statusTone(asset.status).content, "${asset.code} • ${asset.name}", "الحالة: ${asset.status} • ${asset.location}", onClick = { onOpenTab(BottomTab.Assets) })
            }
            items(lowStockParts, key = { "p-${it.id}" }) { part ->
                AlertRow(Icons.Filled.Inventory2, AccentRed, "${part.partNumber} • ${part.name}", "المتوفر ${part.onHandQty} • الحد الأدنى ${part.minQty}", onClick = { onOpenMore(MoreRoute.Inventory) })
            }
            items(duePm, key = { "m-${it.id}" }) { pm ->
                AlertRow(Icons.Filled.EventRepeat, AccentOrange, pm.title, "مستحقة بتاريخ ${pm.nextDueAt}", onClick = { onOpenTab(BottomTab.Supervision) })
            }
            items(expiringWarranty.take(4), key = { "w-${it.id}" }) { asset ->
                AlertRow(Icons.Filled.Verified, AccentTeal, "${asset.code} • ${asset.name}", "ينتهي الضمان بتاريخ ${asset.warrantyEnd}", onClick = { onOpenTab(BottomTab.Assets) })
            }
        }
    }
}

@Composable
internal fun MetricColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun DotSectionTitle(text: String, dot: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.size(8.dp).background(dot, CircleShape))
    }
}

@Composable
internal fun KpiTile(label: String, value: String, color: Color, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    ElevatedCard(
        modifier = modifier.height(92.dp).then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun BigActionButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
internal fun CalmCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconBubble(Icons.Filled.CheckCircle, StatusRunning, StatusRunningContainer, 40)
            Column {
                Text("لا شيء عاجل", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text("كل شيء تحت السيطرة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
internal fun AlertRow(icon: ImageVector, tint: Color, title: String, body: String, onClick: (() -> Unit)? = null) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(icon, tint, tint.copy(alpha = 0.14f), 40)
            Column(modifier = Modifier.weight(1f)) {
                LtrText(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (onClick != null) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// "More" grid
// ---------------------------------------------------------------------------

@Composable
internal fun MoreGrid(
    innerPadding: PaddingValues,
    isAdmin: Boolean,
    canManage: Boolean,
    onOpen: (MoreRoute) -> Unit,
    onImportBundled: () -> Unit,
    onPickExcel: () -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        if (canManage) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            IconBubble(Icons.Filled.UploadFile, AccentGreen, AccentGreen.copy(alpha = 0.14f), 40)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("استيراد من Excel", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                Text("حوّل ملف صيانة الآلة إلى أصل وخطط وقطع غيار وأمر عمل تلقائياً.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = onImportBundled, modifier = Modifier.weight(1f)) { Text("استيراد قالب FVV المرفق") }
                            OutlinedButton(onClick = onPickExcel, modifier = Modifier.weight(1f)) { Text("رفع ملف Excel") }
                        }
                    }
                }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("البلاغات", "بلاغات الصيانة", Icons.Filled.NotificationsActive, AccentRed, Modifier.weight(1f)) { onOpen(MoreRoute.Notifications) }
                ModuleCard("المخزون", "قطع الغيار والحركات", Icons.Filled.Inventory2, AccentPurple, Modifier.weight(1f)) { onOpen(MoreRoute.Inventory) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("التقارير", "مؤشرات وتحليلات", Icons.Filled.Analytics, AccentBlue, Modifier.weight(1f)) { onOpen(MoreRoute.Reports) }
                ModuleCard("الصيانة الدورية", "جدول المهام الوقائية", Icons.Filled.EventRepeat, AccentTeal, Modifier.weight(1f)) { onOpen(MoreRoute.PreventiveMaintenance) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("قوالب العمل", "قوالب العمليات", Icons.AutoMirrored.Filled.List, AccentBlue, Modifier.weight(1f)) { onOpen(MoreRoute.TaskLists) }
                ModuleCard("العدّادات", "القراءات والقياسات", Icons.Filled.Speed, AccentPurple, Modifier.weight(1f)) { onOpen(MoreRoute.Meters) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("الأرقام التسلسلية", "تتبّع الوحدات والحركات", Icons.Filled.QrCodeScanner, AccentTeal, Modifier.weight(1f)) { onOpen(MoreRoute.SerialNumbers) }
                ModuleCard("المواقع الفنية", "هرمية المواقع", Icons.Filled.AccountTree, AccentGreen, Modifier.weight(1f)) { onOpen(MoreRoute.Locations) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("الإجراءات CAPA", "تصحيحية ووقائية", Icons.Filled.FactCheck, AccentOrange, Modifier.weight(1f)) { onOpen(MoreRoute.Capa) }
                ModuleCard("المستودعات", "المخازن وأمناء العهدة", Icons.Filled.Warehouse, AccentPurple, Modifier.weight(1f)) { onOpen(MoreRoute.Warehouses) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("الوحدات التنظيمية", "مراكز العمل والتكلفة", Icons.Filled.CorporateFare, AccentNavy, Modifier.weight(1f)) { onOpen(MoreRoute.OrgUnits) }
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("تحليل الأعطال", "MTTR / MTBF", Icons.Filled.TrendingUp, AccentRed, Modifier.weight(1f)) { onOpen(MoreRoute.Failures) }
                ModuleCard("سجل الحوكمة", "من فعل ماذا ومتى", Icons.Filled.History, AccentNavy, Modifier.weight(1f)) { onOpen(MoreRoute.Audit) }
            }
        }
        if (isAdmin) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    ModuleCard("الإدارة", "المستخدمون والصلاحيات", Icons.Filled.AdminPanelSettings, AccentOrange, Modifier.weight(1f)) { onOpen(MoreRoute.Admin) }
                    ModuleCard("تسجيل الخروج", "إنهاء الجلسة الحالية", Icons.AutoMirrored.Filled.Logout, AccentNavy, Modifier.weight(1f)) { onLogout() }
                }
            }
        } else {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    ModuleCard("تسجيل الخروج", "إنهاء الجلسة الحالية", Icons.AutoMirrored.Filled.Logout, AccentNavy, Modifier.weight(1f)) { onLogout() }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
internal fun ModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .height(132.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(icon, accent, accent.copy(alpha = 0.14f), 40)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Shared
// ---------------------------------------------------------------------------

/** Formats a monetary amount with thousands separators and a currency suffix. */
internal fun money(value: Double): String = "%,.0f ر.س".format(value)

/** Shares a plain-text report through the Android share sheet (email, WhatsApp, notes…). */
internal fun shareText(context: Context, subject: String, body: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(Intent.createChooser(intent, subject))
}

/** Builds the shareable maintenance report body from the dashboard figures. */
internal fun buildReportText(
    assets: Int, openWo: Int, closed: Int,
    totalCost: Double, laborCost: Double, partsCost: Double, openCost: Double,
    availability: Double, failures: Int, downtime: Double, mttr: Double,
    overdue: Int, pendingApprovals: Int, duePm: Int,
    lowStock: Int, underWarranty: Int, expiringSoon: Int
): String = buildString {
    appendLine("تقرير الصيانة — الهادي CMMS")
    appendLine("التاريخ: ${DateStrings.today()}")
    appendLine("──────────────")
    appendLine("الأصول: $assets")
    appendLine("أوامر العمل — مفتوحة: $openWo | مغلقة: $closed | متأخرة: $overdue | بانتظار الاعتماد: $pendingApprovals")
    appendLine("التكاليف — إجمالي: ${money(totalCost)} | عمالة: ${money(laborCost)} | قطع: ${money(partsCost)} | تقديرية مفتوحة: ${money(openCost)}")
    appendLine("التوفّر (30 يوم): ${"%.1f".format(availability)}%")
    appendLine("الأعطال: $failures | زمن التوقف: ${"%.1f".format(downtime)}س | MTTR: ${"%.1f".format(mttr)}س")
    appendLine("الصيانة الدورية المستحقة: $duePm")
    appendLine("المخزون تحت الحد الأدنى: $lowStock")
    appendLine("الضمان — ساري: $underWarranty | ينتهي خلال 30 يوم: $expiringSoon")
}

@Composable
internal fun SearchField(query: String, onChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = query,
        onValueChange = onChange,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
internal fun AddButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
internal fun EditDeleteRow(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تعديل")
        }
        TextButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(6.dp))
            Text("حذف", color = MaterialTheme.colorScheme.error)
        }
    }
}

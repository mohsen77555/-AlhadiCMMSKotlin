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

private enum class BottomTab(val label: String, val icon: ImageVector, val accent: Color) {
    Home("الرئيسية", Icons.Filled.Home, AccentNavy),
    WorkOrders("أوامر العمل", Icons.Filled.Assignment, AccentBlue),
    Supervision("الإشراف", Icons.Filled.Verified, AccentTeal),
    Assets("الأصول", Icons.Filled.PrecisionManufacturing, AccentGreen),
    More("المزيد", Icons.Filled.GridView, AccentBrown)
}

private enum class MoreRoute { Notifications, Inventory, SerialNumbers, Reports, Audit, Admin, PreventiveMaintenance, TaskLists, Meters, Locations, Warehouses, Capa, Failures }

private data class ScreenMeta(
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
                        canManage = canManage,
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

private fun screenMeta(tab: BottomTab, route: MoreRoute?): ScreenMeta = when (tab) {
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
        MoreRoute.Capa -> ScreenMeta("الإجراءات CAPA", "إجراءات تصحيحية ووقائية", Icons.Filled.FactCheck, AccentOrange)
        MoreRoute.Failures -> ScreenMeta("تحليل الأعطال", "MTTR و MTBF وتكرار الأعطال", Icons.Filled.TrendingUp, AccentRed)
    }
}

// ---------------------------------------------------------------------------
// Header
// ---------------------------------------------------------------------------

@Composable
private fun AppHeader(
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
private fun ProductionBadge() {
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
private fun SyncCard() {
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
private fun StatCard(label: String, value: String, color: Color) {
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
private fun QuickPill(label: String, icon: ImageVector, onClick: () -> Unit) {
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
private fun AppBottomBar(selected: BottomTab, isAdmin: Boolean, onSelect: (BottomTab) -> Unit) {
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
private fun DashboardScreen(
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
private fun MetricColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun DotSectionTitle(text: String, dot: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.size(8.dp).background(dot, CircleShape))
    }
}

@Composable
private fun KpiTile(label: String, value: String, color: Color, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
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
private fun BigActionButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
private fun CalmCard() {
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
private fun AlertRow(icon: ImageVector, tint: Color, title: String, body: String, onClick: (() -> Unit)? = null) {
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
private fun MoreGrid(
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
private fun ModuleCard(
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
// Assets
// ---------------------------------------------------------------------------

@Composable
private fun AssetsScreen(
    innerPadding: PaddingValues,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    pmItems: List<PreventiveMaintenanceEntity>,
    locations: List<FunctionalLocationEntity>,
    documents: List<AssetDocumentEntity>,
    characteristics: List<AssetCharacteristicEntity>,
    bomHeaders: List<AssetBomHeaderEntity>,
    bomItems: List<AssetBomItemEntity>,
    movements: List<AssetMovementEntity>,
    spareParts: List<SparePartEntity>,
    serials: List<SerialNumberEntity>,
    serialMovements: List<SerialNumberMovementEntity>,
    canManage: Boolean,
    defaultAssignee: String,
    onSave: (AssetEntity) -> Unit,
    onDelete: (AssetEntity) -> Unit,
    onChangeStatus: (AssetEntity, String) -> Unit,
    onSaveWorkOrder: (WorkOrderEntity) -> Unit,
    onUpdateWorkOrderStatus: (WorkOrderEntity, String) -> Unit,
    onSaveDocument: (AssetDocumentEntity) -> Unit,
    onDeleteDocument: (AssetDocumentEntity) -> Unit,
    onSaveCharacteristic: (AssetCharacteristicEntity) -> Unit,
    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,
    onSaveBomHeader: (AssetBomHeaderEntity) -> Unit,
    onDeleteBomHeader: (AssetBomHeaderEntity) -> Unit,
    onSaveBom: (AssetBomItemEntity) -> Unit,
    onDeleteBom: (AssetBomItemEntity) -> Unit,
    onMove: (AssetEntity, String, Long?, String, String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AssetEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<AssetEntity?>(null) }
    var detailId by remember { mutableStateOf<Long?>(null) }

    val scanLauncher = rememberLauncherForActivityResult(ScanContract()) { result ->
        val raw = result.contents
        if (raw != null) {
            val code = raw.removePrefix("ALHADI:").trim()
            val match = assets.firstOrNull { it.code.equals(code, ignoreCase = true) }
            if (match != null) detailId = match.id else query = code
        }
    }
    fun launchScan() {
        scanLauncher.launch(
            ScanOptions()
                .setDesiredBarcodeFormats(ScanOptions.QR_CODE)
                .setPrompt("وجّه الكاميرا إلى رمز الأصل")
                .setBeepEnabled(false)
                .setOrientationLocked(false)
        )
    }

    // Detail view (re-resolved from the live list so edits/status changes reflect).
    val detailAsset = detailId?.let { id -> assets.firstOrNull { it.id == id } }
    if (detailAsset != null) {
        BackHandler { detailId = null }
        AssetDetailScreen(
            innerPadding = innerPadding,
            asset = detailAsset,
            allAssets = assets,
            workOrders = workOrders.filter { it.assetId == detailAsset.id },
            pmItems = pmItems.filter { it.assetId == detailAsset.id },
            documents = documents.filter { it.assetId == detailAsset.id },
            characteristics = characteristics,
            bomHeaders = bomHeaders,
            bomItems = bomItems,
            movements = movements.filter { it.assetId == detailAsset.id },
            spareParts = spareParts,
            serials = serials,
            serialMovements = serialMovements,
            locations = locations,
            canManage = canManage,
            defaultAssignee = defaultAssignee,
            onBack = { detailId = null },
            onOpenAsset = { detailId = it },
            onSaveAsset = onSave,
            onChangeStatus = onChangeStatus,
            onSaveWorkOrder = onSaveWorkOrder,
            onUpdateWorkOrderStatus = onUpdateWorkOrderStatus,
            onSaveDocument = onSaveDocument,
            onDeleteDocument = onDeleteDocument,
            onSaveCharacteristic = onSaveCharacteristic,
            onDeleteCharacteristic = onDeleteCharacteristic,
            onSaveBomHeader = onSaveBomHeader,
            onDeleteBomHeader = onDeleteBomHeader,
            onSaveBom = onSaveBom,
            onDeleteBom = onDeleteBom,
            onMove = onMove
        )
        return
    }

    val filtered = remember(query, assets, characteristics) {
        if (query.isBlank()) assets else assets.filter { asset ->
            val q = query.lowercase(Locale.getDefault())
            asset.code.lowercase(Locale.getDefault()).contains(q) ||
                asset.name.lowercase(Locale.getDefault()).contains(q) ||
                asset.groupName.lowercase(Locale.getDefault()).contains(q) ||
                asset.location.lowercase(Locale.getDefault()).contains(q) ||
                asset.serialNumber.lowercase(Locale.getDefault()).contains(q) ||
                asset.assetTag.lowercase(Locale.getDefault()).contains(q) ||
                asset.externalAssetCode.lowercase(Locale.getDefault()).contains(q) ||
                asset.legacyAssetCode.lowercase(Locale.getDefault()).contains(q) ||
                asset.barcode.lowercase(Locale.getDefault()).contains(q) ||
                asset.qrCode.lowercase(Locale.getDefault()).contains(q) ||
                asset.description.lowercase(Locale.getDefault()).contains(q) ||
                asset.category.lowercase(Locale.getDefault()).contains(q) ||
                asset.objectType.lowercase(Locale.getDefault()).contains(q) ||
                asset.maintenancePlant.lowercase(Locale.getDefault()).contains(q) ||
                asset.planningPlant.lowercase(Locale.getDefault()).contains(q) ||
                asset.plannerGroup.lowercase(Locale.getDefault()).contains(q) ||
                asset.mainWorkCenter.lowercase(Locale.getDefault()).contains(q) ||
                asset.costCenter.lowercase(Locale.getDefault()).contains(q) ||
                asset.responsiblePerson.lowercase(Locale.getDefault()).contains(q) ||
                asset.assetNumber.lowercase(Locale.getDefault()).contains(q) ||
                asset.partnerName.lowercase(Locale.getDefault()).contains(q) ||
                asset.city.lowercase(Locale.getDefault()).contains(q) ||
                asset.country.lowercase(Locale.getDefault()).contains(q) ||
                asset.standardClass.lowercase(Locale.getDefault()).contains(q) ||
                asset.constructionType.lowercase(Locale.getDefault()).contains(q) ||
                asset.linearRouteCode.lowercase(Locale.getDefault()).contains(q) ||
                asset.linearReferencePattern.lowercase(Locale.getDefault()).contains(q) ||
                asset.linearStartMarker.lowercase(Locale.getDefault()).contains(q) ||
                asset.linearEndMarker.lowercase(Locale.getDefault()).contains(q) ||
                asset.networkObjectCode.lowercase(Locale.getDefault()).contains(q) ||
                asset.networkAttributes.lowercase(Locale.getDefault()).contains(q) ||
                resolveAssetCharacteristics(asset, assets, characteristics).any { resolved ->
                    resolved.resolvedClassName.lowercase(Locale.getDefault()).contains(q) ||
                        resolved.item.name.lowercase(Locale.getDefault()).contains(q) ||
                        resolved.item.value.lowercase(Locale.getDefault()).contains(q) ||
                        resolved.item.unit.lowercase(Locale.getDefault()).contains(q)
                }
        }
    }
    val grouped = filtered.groupBy { it.groupName }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (query.isBlank() && assets.isNotEmpty()) {
                item {
                    val running = assets.count { it.status == "Running" }
                    val stopped = assets.count { it.status == "Stopped" || it.status == "Retired" }
                    val warning = assets.count { it.status == "Warning" || it.status == "Under Maintenance" }
                    val other = assets.size - running - stopped - warning
                    val seg = listOf(
                        ChartSegment("تعمل", running, AccentGreen),
                        ChartSegment("تحذير/صيانة", warning, AccentOrange),
                        ChartSegment("متوقفة/متقاعدة", stopped, AccentRed),
                        ChartSegment("أخرى", other.coerceAtLeast(0), AccentNavy)
                    )
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            DonutChart(segments = seg, centerValue = assets.size.toString(), centerLabel = "أصل")
                            ChartLegend(seg, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.weight(1f)) {
                        SearchField(query = query, onChange = { query = it }, placeholder = "بحث: RM-01 أو Rollermill")
                    }
                    FilledTonalIconButton(onClick = { launchScan() }) {
                        Icon(Icons.Filled.QrCodeScanner, contentDescription = "مسح رمز الأصل")
                    }
                }
            }
            if (canManage) {
                item { AddButton("أصل جديد") { editing = null; showForm = true } }
            }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد أصول مطابقة للبحث", Icons.Filled.Search) }
            }
            grouped.forEach { (group, groupAssets) ->
                item { SectionHeader("$group (${groupAssets.size})") }
                items(groupAssets, key = { it.id }) { asset ->
                    AssetCard(
                        asset = asset,
                        canManage = canManage,
                        onOpen = { detailId = asset.id },
                        onEdit = { editing = asset; showForm = true },
                        onDelete = { deleteTarget = asset }
                    )
                }
            }
        }
    }

    if (showForm) {
        AssetFormSheet(initial = editing, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false }, locations = locations, allAssets = assets)
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف الأصل",
            text = "هل تريد حذف ${target.code} - ${target.name}؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun AssetCard(
    asset: AssetEntity,
    canManage: Boolean,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onOpen),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                val tone = statusTone(asset.status)
                IconBubble(Icons.Filled.PrecisionManufacturing, tone.content, tone.container, 44)
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(asset.code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LtrText(asset.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(asset.status, tone)
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(asset.criticality, priorityTone(asset.criticality))
                AssistChip(onClick = {}, label = { Text(assetCategoryLabel(asset.category), maxLines = 1) })
                if (asset.objectType.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(asset.objectType, maxLines = 1) })
                }
                if (asset.standardClass.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(asset.standardClass, maxLines = 1) })
                }
                if (asset.isLinearAsset) {
                    StatusBadge("أصل خطي", statusTone("info"))
                }
            }
            if (asset.location.isNotBlank()) {
                Text(asset.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

@Composable
private fun AssetDetailScreen(
    innerPadding: PaddingValues,
    asset: AssetEntity,
    allAssets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    pmItems: List<PreventiveMaintenanceEntity>,
    documents: List<AssetDocumentEntity>,
    characteristics: List<AssetCharacteristicEntity>,
    bomHeaders: List<AssetBomHeaderEntity>,
    bomItems: List<AssetBomItemEntity>,
    movements: List<AssetMovementEntity>,
    spareParts: List<SparePartEntity>,
    serials: List<SerialNumberEntity>,
    serialMovements: List<SerialNumberMovementEntity>,
    locations: List<FunctionalLocationEntity>,
    canManage: Boolean,
    defaultAssignee: String,
    onBack: () -> Unit,
    onOpenAsset: (Long) -> Unit,
    onSaveAsset: (AssetEntity) -> Unit,
    onChangeStatus: (AssetEntity, String) -> Unit,
    onSaveWorkOrder: (WorkOrderEntity) -> Unit,
    onUpdateWorkOrderStatus: (WorkOrderEntity, String) -> Unit,
    onSaveDocument: (AssetDocumentEntity) -> Unit,
    onDeleteDocument: (AssetDocumentEntity) -> Unit,
    onSaveCharacteristic: (AssetCharacteristicEntity) -> Unit,
    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,
    onSaveBomHeader: (AssetBomHeaderEntity) -> Unit,
    onDeleteBomHeader: (AssetBomHeaderEntity) -> Unit,
    onSaveBom: (AssetBomItemEntity) -> Unit,
    onDeleteBom: (AssetBomItemEntity) -> Unit,
    onMove: (AssetEntity, String, Long?, String, String) -> Unit
) {
    var showDocForm by remember { mutableStateOf(false) }
    var editingDoc by remember { mutableStateOf<AssetDocumentEntity?>(null) }
    var deleteDoc by remember { mutableStateOf<AssetDocumentEntity?>(null) }
    var showCharForm by remember { mutableStateOf(false) }
    var editingChar by remember { mutableStateOf<AssetCharacteristicEntity?>(null) }
    var deleteChar by remember { mutableStateOf<AssetCharacteristicEntity?>(null) }
    var showBomForm by remember { mutableStateOf(false) }
    var deleteBom by remember { mutableStateOf<AssetBomItemEntity?>(null) }
    val partMap = spareParts.associateBy { it.id }
    val locationLabel = asset.locationId?.let { id -> locations.firstOrNull { it.id == id }?.let { "${it.code} • ${it.name}" } } ?: "غير محدد"
    val today = DateStrings.today()
    val underWarranty = asset.isUnderWarranty(today)
    val hasWarranty = asset.warrantyEnd.isNotBlank()
    val parent = asset.parentAssetId?.let { id -> allAssets.firstOrNull { it.id == id } }
    val children = allAssets.filter { it.parentAssetId == asset.id }
    var showEdit by remember { mutableStateOf(false) }
    var showStatus by remember { mutableStateOf(false) }
    var showWoForm by remember { mutableStateOf(false) }
    var showMoveForm by remember { mutableStateOf(false) }
    val lifecycle = listOf("Running", "Warning", "Stopped", "Under Maintenance", "Standby", "Retired")
    val retired = asset.status.equals("Retired", ignoreCase = true)
    val hasOrganization = listOf(
        asset.company,
        asset.site,
        asset.maintenancePlant,
        asset.planningPlant,
        asset.plannerGroup,
        asset.mainWorkCenter,
        asset.productionWorkCenter,
        asset.costCenter,
        asset.responsiblePerson
    ).any { it.isNotBlank() }
    val hasIdentityCodes = listOf(
        asset.alternativeLabel,
        asset.externalAssetCode,
        asset.legacyAssetCode,
        asset.barcode,
        asset.qrCode
    ).any { it.isNotBlank() }
    val hasSafety = asset.safetyCritical || asset.isolationRequired || listOf(
        asset.riskLevel,
        asset.requiredPermits,
        asset.safetyInstructions,
        asset.ppeRequired,
        asset.complianceRequirements
    ).any { it.isNotBlank() }
    val hasPartner = listOf(asset.partnerName, asset.partnerRole, asset.partnerPhone, asset.partnerEmail).any { it.isNotBlank() }
    val hasAddress = listOf(asset.addressLine, asset.city, asset.country).any { it.isNotBlank() }
    val constructionDate = listOf(asset.constructionYear, asset.constructionMonth)
        .filter { it.isNotBlank() }
        .joinToString(" / ")
    val resolvedCharacteristics = resolveAssetCharacteristics(asset, allAssets, characteristics)
    val directCharacteristics = resolvedCharacteristics.filterNot { it.inherited }
    val inheritedCharacteristics = resolvedCharacteristics.filter { it.inherited }
    val characteristicGroups = resolvedCharacteristics.groupBy { it.resolvedClassName }
    val hasLinearData = asset.isLinearAsset

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع") }
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(asset.code, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    LtrText(asset.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(asset.status, statusTone(asset.status))
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionHeader("المعلومات الأساسية")
                    if (asset.description.isNotBlank()) InfoRow("الوصف", asset.description)
                    InfoRow("فئة الأصل", assetCategoryLabel(asset.category))
                    if (asset.objectType.isNotBlank()) InfoRow("نوع الأصل", asset.objectType)
                    if (asset.constructionType.isNotBlank()) InfoRow("نوع الإنشاء", asset.constructionType)
                    InfoRow("المجموعة", asset.groupName)
                    InfoRow("الموقع", asset.location.ifBlank { "غير محدد" })
                    InfoRow("الموقع الفني", locationLabel)
                    InfoRow("الأصل الأب", parent?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                    InfoRow("الشركة/الموديل", listOf(asset.manufacturer, asset.model).filter { it.isNotBlank() }.joinToString(" • ").ifBlank { "غير محدد" })
                    if (asset.serialNumber.isNotBlank()) InfoRow("الرقم التسلسلي", asset.serialNumber)
                    if (asset.assetTag.isNotBlank()) InfoRow("وسم الأصل", asset.assetTag)
                    if (asset.assetNumber.isNotBlank()) InfoRow("رقم الأصل المالي", asset.assetNumber)
                    InfoRow("الأهمية", asset.criticality)
                    if (constructionDate.isNotBlank()) InfoRow("سنة / شهر الصنع", constructionDate)
                    InfoRow("تاريخ التركيب", asset.installedAt)
                    if (asset.startupDate.isNotBlank()) InfoRow("تاريخ بدء التشغيل", asset.startupDate)
                    InfoRow("آخر فحص", asset.lastInspectionAt)
                }
            }
        }

        if (hasLinearData) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            SectionHeader("البيانات الخطية")
                            Spacer(modifier = Modifier.weight(1f))
                            StatusBadge("${formatLinearNumber(asset.linearLength())} ${asset.linearUnit}", statusTone("info"))
                        }
                        InfoRow("النطاق", linearRangeLabel(asset))
                        if (asset.linearRouteCode.isNotBlank()) InfoRow("رمز المسار / الخط", asset.linearRouteCode)
                        if (asset.linearReferencePattern.isNotBlank()) InfoRow("نمط المرجع", asset.linearReferencePattern)
                        InfoRow("الاتجاه", linearDirectionLabel(asset.linearDirection))
                        if (asset.linearStartMarker.isNotBlank()) {
                            InfoRow("علامة البداية", "${asset.linearStartMarker} • ${formatLinearNumber(asset.linearStartMarkerDistance)} ${asset.linearMarkerUnit}")
                        }
                        if (asset.linearEndMarker.isNotBlank()) {
                            InfoRow("علامة النهاية", "${asset.linearEndMarker} • ${formatLinearNumber(asset.linearEndMarkerDistance)} ${asset.linearMarkerUnit}")
                        }
                        if (asset.linearHorizontalOffset != 0.0) InfoRow("الإزاحة الأفقية", "${formatLinearNumber(asset.linearHorizontalOffset)} ${asset.linearOffsetUnit}")
                        if (asset.linearVerticalOffset != 0.0) InfoRow("الإزاحة الرأسية", "${formatLinearNumber(asset.linearVerticalOffset)} ${asset.linearOffsetUnit}")
                        if (asset.linearStartLatitude != null && asset.linearStartLongitude != null) {
                            InfoRow("إحداثيات البداية", "${formatLinearNumber(asset.linearStartLatitude)}، ${formatLinearNumber(asset.linearStartLongitude)}")
                        }
                        if (asset.linearEndLatitude != null && asset.linearEndLongitude != null) {
                            InfoRow("إحداثيات النهاية", "${formatLinearNumber(asset.linearEndLatitude)}، ${formatLinearNumber(asset.linearEndLongitude)}")
                        }
                        if (asset.networkObjectCode.isNotBlank()) InfoRow("كائن الشبكة", asset.networkObjectCode)
                        if (asset.networkObjectType.isNotBlank()) InfoRow("نوع كائن الشبكة", networkObjectTypeLabel(asset.networkObjectType))
                        if (asset.networkRelation.isNotBlank()) InfoRow("العلاقة", networkRelationLabel(asset.networkRelation))
                        if (asset.networkAttributes.isNotBlank()) InfoRow("سمات الشبكة", asset.networkAttributes)
                    }
                }
            }
        }

        item {
            AssetSerialSection(
                asset = asset,
                serials = serials,
                movements = serialMovements,
                parts = spareParts
            )
        }

        if (hasOrganization) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("التنظيم والمسؤولية")
                        if (asset.company.isNotBlank()) InfoRow("الشركة", asset.company)
                        if (asset.site.isNotBlank()) InfoRow("الموقع/المنشأة", asset.site)
                        if (asset.maintenancePlant.isNotBlank()) InfoRow("مصنع الصيانة", asset.maintenancePlant)
                        if (asset.planningPlant.isNotBlank()) InfoRow("مصنع التخطيط", asset.planningPlant)
                        if (asset.plannerGroup.isNotBlank()) InfoRow("مجموعة المخططين", asset.plannerGroup)
                        if (asset.mainWorkCenter.isNotBlank()) InfoRow("مركز العمل الرئيسي", asset.mainWorkCenter)
                        if (asset.productionWorkCenter.isNotBlank()) InfoRow("مركز عمل الإنتاج", asset.productionWorkCenter)
                        if (asset.costCenter.isNotBlank()) InfoRow("مركز التكلفة", asset.costCenter)
                        if (asset.responsiblePerson.isNotBlank()) InfoRow("الشخص المسؤول", asset.responsiblePerson)
                    }
                }
            }
        }

        if (hasPartner || hasAddress) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("جهة الاتصال والعنوان")
                        if (asset.partnerName.isNotBlank()) InfoRow("الجهة أو الشخص", asset.partnerName)
                        if (asset.partnerRole.isNotBlank()) InfoRow("الصفة", assetPartnerRoleLabel(asset.partnerRole))
                        if (asset.partnerPhone.isNotBlank()) InfoRow("الهاتف", asset.partnerPhone)
                        if (asset.partnerEmail.isNotBlank()) InfoRow("البريد الإلكتروني", asset.partnerEmail)
                        if (asset.addressLine.isNotBlank()) InfoRow("العنوان", asset.addressLine)
                        if (asset.city.isNotBlank()) InfoRow("المدينة", asset.city)
                        if (asset.country.isNotBlank()) InfoRow("الدولة", asset.country)
                    }
                }
            }
        }

        if (hasIdentityCodes) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("الهوية والترميز")
                        if (asset.alternativeLabel.isNotBlank()) InfoRow("التسمية البديلة", asset.alternativeLabel)
                        if (asset.externalAssetCode.isNotBlank()) InfoRow("الكود الخارجي", asset.externalAssetCode)
                        if (asset.legacyAssetCode.isNotBlank()) InfoRow("الكود القديم", asset.legacyAssetCode)
                        if (asset.barcode.isNotBlank()) InfoRow("الباركود", asset.barcode)
                        if (asset.qrCode.isNotBlank()) InfoRow("رمز QR", asset.qrCode)
                    }
                }
            }
        }

        if (hasSafety) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("السلامة والامتثال")
                        InfoRow("أصل حرج للسلامة", if (asset.safetyCritical) "نعم" else "لا")
                        if (asset.riskLevel.isNotBlank()) InfoRow("مستوى المخاطر", asset.riskLevel)
                        InfoRow("يتطلب عزل الطاقة", if (asset.isolationRequired) "نعم" else "لا")
                        if (asset.requiredPermits.isNotBlank()) InfoRow("التصاريح المطلوبة", asset.requiredPermits)
                        if (asset.ppeRequired.isNotBlank()) InfoRow("معدات الوقاية (PPE)", asset.ppeRequired)
                        if (asset.safetyInstructions.isNotBlank()) InfoRow("تعليمات السلامة", asset.safetyInstructions)
                        if (asset.complianceRequirements.isNotBlank()) InfoRow("متطلبات الامتثال", asset.complianceRequirements)
                    }
                }
            }
        }

        item {
            val laborTotal = workOrders.sumOf { it.laborCost() }
            val partsTotal = workOrders.sumOf { it.partsCost }
            val grandTotal = workOrders.sumOf { it.totalCost() }
            val closedCount = workOrders.count { it.status == "Closed" }
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        SectionHeader("التكاليف")
                        Spacer(modifier = Modifier.weight(1f))
                        StatusBadge(money(grandTotal), statusTone("info"))
                    }
                    InfoRow("إجمالي التكلفة", money(grandTotal))
                    InfoRow("تكلفة العمالة", money(laborTotal))
                    InfoRow("تكلفة قطع الغيار", money(partsTotal))
                    InfoRow("أوامر العمل", "${workOrders.size} (مغلقة: $closedCount)")
                }
            }
        }

        val hasFinancial = asset.supplier.isNotBlank() || asset.purchaseOrder.isNotBlank() ||
            asset.purchaseCost > 0.0 || asset.acquiredAt.isNotBlank() ||
            asset.financialStatus.isNotBlank() || asset.bookValue > 0.0 || asset.capitalizationAt.isNotBlank()
        if (hasFinancial) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("المعلومات المالية")
                        if (asset.supplier.isNotBlank()) InfoRow("المورّد", asset.supplier)
                        if (asset.purchaseOrder.isNotBlank()) InfoRow("أمر الشراء", asset.purchaseOrder)
                        if (asset.purchaseCost > 0.0) InfoRow("تكلفة الشراء", money(asset.purchaseCost))
                        if (asset.acquiredAt.isNotBlank()) InfoRow("تاريخ الاقتناء", asset.acquiredAt)
                        if (asset.financialStatus.isNotBlank()) InfoRow("الحالة المالية", asset.financialStatus)
                        if (asset.bookValue > 0.0) InfoRow("القيمة الدفترية", money(asset.bookValue))
                        if (asset.capitalizationAt.isNotBlank()) InfoRow("تاريخ الرسملة", asset.capitalizationAt)
                    }
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("التصنيف والخصائص (${resolvedCharacteristics.size})")
                Spacer(modifier = Modifier.weight(1f))
                if (canManage) {
                    OutlinedButton(onClick = { editingChar = null; showCharForm = true }) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة")
                    }
                }
            }
        }
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoRow("التصنيف القياسي", asset.standardClass.ifBlank { "غير محدد" })
                    if (asset.equipmentCategory.isNotBlank()) InfoRow("فئة المعدّة", asset.equipmentCategory)
                    if (asset.assetClass.isNotBlank()) InfoRow("صنف الأصل", asset.assetClass)
                    if (asset.assetSubclass.isNotBlank()) InfoRow("الصنف الفرعي", asset.assetSubclass)
                    InfoRow("توريث خصائص الأصل الأب", if (asset.inheritParentCharacteristics) "مفعّل" else "متوقف")
                    InfoRow("الخصائص المباشرة", directCharacteristics.size.toString())
                    if (inheritedCharacteristics.isNotEmpty()) {
                        InfoRow("الخصائص الموروثة", inheritedCharacteristics.size.toString())
                    }
                }
            }
        }
        if (resolvedCharacteristics.isEmpty()) {
            item { EmptyState("لا توجد خصائص مسجّلة") }
        }
        characteristicGroups.forEach { (className, classItems) ->
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    SectionHeader("$className (${classItems.size})")
                    Spacer(modifier = Modifier.weight(1f))
                    if (asset.standardClass.isNotBlank() && className.equals(asset.standardClass, ignoreCase = true)) {
                        StatusBadge("قياسي", statusTone("info"))
                    }
                }
            }
            items(classItems, key = { resolved -> "char-${resolved.sourceAsset.id}-${resolved.item.id}-${resolved.inherited}" }) { resolved ->
                val ch = resolved.item
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(ch.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                            LtrText(characteristicDisplayValue(ch), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StatusBadge(characteristicTypeLabel(ch.dataType), statusTone("info"))
                            if (ch.isRequired) StatusBadge("إلزامية", statusTone("overdue"))
                            if (resolved.inherited) {
                                StatusBadge("موروثة من ${resolved.sourceAsset.code}", statusTone("scheduled"))
                            }
                        }
                        if (canManage && !resolved.inherited) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(onClick = { editingChar = ch; showCharForm = true }, modifier = Modifier.weight(1f)) {
                                    Text("تعديل")
                                }
                                TextButton(onClick = { deleteChar = ch }, modifier = Modifier.weight(1f)) {
                                    Text("حذف", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }

        item {
            AssetBomSection(
                asset = asset,
                allAssets = allAssets,
                headers = bomHeaders,
                items = bomItems,
                parts = spareParts,
                canManage = canManage,
                onSaveHeader = onSaveBomHeader,
                onDeleteHeader = onDeleteBomHeader,
                onSaveItem = onSaveBom,
                onDeleteItem = onDeleteBom
            )
        }

        item {
            val qr = rememberQrBitmap("ALHADI:${asset.code}")
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SectionHeader("رمز QR للأصل")
                    if (qr != null) {
                        Image(
                            bitmap = qr,
                            contentDescription = "QR ${asset.code}",
                            modifier = Modifier
                                .size(180.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                    LtrText("ALHADI:${asset.code}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("امسح الرمز للوصول إلى بطاقة الأصل.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        if (hasWarranty) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            SectionHeader("الضمان")
                            Spacer(modifier = Modifier.weight(1f))
                            StatusBadge(if (underWarranty) "ضمن الضمان" else "منتهٍ", statusTone(if (underWarranty) "running" else "stopped"))
                        }
                        InfoRow("الجهة", asset.warrantyProvider)
                        InfoRow("من", asset.warrantyStart)
                        InfoRow("إلى", asset.warrantyEnd)
                    }
                }
            }
        }

        if (canManage) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { showEdit = true }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("تعديل")
                    }
                    OutlinedButton(onClick = { showStatus = true }, modifier = Modifier.weight(1f)) {
                        Text("تغيير الحالة")
                    }
                }
            }
            item {
                OutlinedButton(onClick = { showMoveForm = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تركيب / نقل / فك")
                }
            }
            item {
                if (retired) {
                    Text(
                        "الأصل متقاعد — لا يمكن إنشاء أوامر عمل جديدة عليه.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        if (underWarranty) {
                            Text(
                                "تنبيه: هذا الأصل ضمن الضمان — قد تتحمّل جهة الضمان تكلفة الإصلاح.",
                                style = MaterialTheme.typography.bodySmall,
                                color = AccentOrange,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        AddButton("أمر عمل لهذا الأصل") { showWoForm = true }
                    }
                }
            }
        }

        if (parent != null || children.isNotEmpty()) {
            item { SectionHeader("الأصول الفرعية (${children.size})") }
            items(children, key = { "ch-${it.id}" }) { child ->
                ElevatedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOpenAsset(child.id) },
                    colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconBubble(Icons.Filled.PrecisionManufacturing, AccentGreen, AccentGreen.copy(alpha = 0.14f), 36)
                        Column(modifier = Modifier.weight(1f)) {
                            LtrText(child.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            LtrText(child.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusBadge(child.status, statusTone(child.status))
                        Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
            if (children.isEmpty()) {
                item { EmptyState("لا توجد أصول فرعية") }
            }
        }

        item { SectionHeader("أوامر العمل المرتبطة (${workOrders.size})") }
        if (workOrders.isEmpty()) {
            item { EmptyState("لا توجد أوامر عمل لهذا الأصل") }
        } else {
            item {
                val seg = listOf(
                    ChartSegment("مفتوح", workOrders.count { it.status == "Open" }, AccentBlue),
                    ChartSegment("قيد التنفيذ", workOrders.count { it.status == "In Progress" }, AccentOrange),
                    ChartSegment("مكتمل فنياً", workOrders.count { it.status == "Technically Completed" }, AccentTeal),
                    ChartSegment("مغلق", workOrders.count { it.status == "Closed" }, AccentGreen)
                )
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DonutChart(segments = seg, centerValue = workOrders.size.toString(), centerLabel = "أمر")
                        ChartLegend(seg, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
        items(workOrders, key = { "wo-${it.id}" }) { wo ->
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(wo.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        StatusBadge(workOrderStatusLabel(wo.status), statusTone(wo.status))
                    }
                    Text("الاستحقاق: ${wo.dueAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (asset.isLinearAsset && wo.hasLinearReference()) {
                        InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, wo.linearStartPoint, wo.linearEndPoint, wo.linearMarker, wo.linearHorizontalOffset, wo.linearVerticalOffset))
                    }
                    if (wo.approvalStatus == "Pending") {
                        StatusBadge("بانتظار الاعتماد", statusTone("overdue"))
                    }
                    if (canManage && wo.status != "Closed" && !wo.isBlockedByApproval()) {
                        when (wo.status) {
                            "Open" -> Button(onClick = { onUpdateWorkOrderStatus(wo, "In Progress") }, modifier = Modifier.fillMaxWidth()) { Text("بدء التنفيذ") }
                            "In Progress" -> Button(onClick = { onUpdateWorkOrderStatus(wo, "Technically Completed") }, modifier = Modifier.fillMaxWidth()) { Text("إكمال فني") }
                            "Technically Completed" -> Button(onClick = { onUpdateWorkOrderStatus(wo, "Closed") }, modifier = Modifier.fillMaxWidth()) { Text("إغلاق نهائي") }
                        }
                    }
                }
            }
        }

        item { SectionHeader("الصيانة الدورية المرتبطة (${pmItems.size})") }
        if (pmItems.isEmpty()) {
            item { EmptyState("لا توجد مهام صيانة لهذا الأصل") }
        }
        items(pmItems, key = { "pm-${it.id}" }) { pm ->
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(pm.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        StatusBadge(if (DateStrings.isDueOrOverdue(pm.nextDueAt)) "مستحقة" else "مجدولة", statusTone(if (DateStrings.isDueOrOverdue(pm.nextDueAt)) "overdue" else "scheduled"))
                    }
                    Text("التنفيذ القادم: ${pm.nextDueAt} • كل ${pm.frequencyDays} يوم", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("المستندات (${documents.size})")
                Spacer(modifier = Modifier.weight(1f))
                if (canManage) {
                    OutlinedButton(onClick = { editingDoc = null; showDocForm = true }) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة")
                    }
                }
            }
        }
        if (documents.isEmpty()) {
            item { EmptyState("لا توجد مستندات لهذا الأصل", Icons.Filled.Description) }
        }
        items(documents, key = { "doc-${it.id}" }) { doc ->
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        IconBubble(Icons.Filled.Description, AccentBlue, AccentBlue.copy(alpha = 0.14f), 36)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(doc.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            LtrText(doc.reference, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusBadge(doc.type, statusTone("info"))
                    }
                    Text("${doc.uploadedBy} • ${doc.uploadedAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (canManage) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(onClick = { editingDoc = doc; showDocForm = true }, modifier = Modifier.weight(1f)) { Text("تعديل") }
                            TextButton(onClick = { deleteDoc = doc }, modifier = Modifier.weight(1f)) { Text("حذف", color = MaterialTheme.colorScheme.error) }
                        }
                    }
                }
            }
        }

        item { SectionHeader("سجل الحركات (${movements.size})") }
        if (movements.isEmpty()) {
            item { EmptyState("لا توجد حركات تركيب/نقل مسجّلة", Icons.Filled.SwapHoriz) }
        }
        items(movements, key = { "mv-${it.id}" }) { mv ->
            val tone = when (mv.eventType) {
                MovementType.INSTALL -> AccentGreen
                MovementType.TRANSFER -> AccentBlue
                MovementType.DISMANTLE -> AccentOrange
                else -> MaterialTheme.colorScheme.error
            }
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconBubble(Icons.Filled.SwapHoriz, tone, tone.copy(alpha = 0.14f), 36)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(MovementType.label(mv.eventType), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        val route = when {
                            mv.fromLocationName.isNotBlank() && mv.toLocationName.isNotBlank() -> "${mv.fromLocationName} ← ${mv.toLocationName}"
                            mv.toLocationName.isNotBlank() -> "إلى ${mv.toLocationName}"
                            mv.fromLocationName.isNotBlank() -> "من ${mv.fromLocationName}"
                            else -> ""
                        }
                        if (route.isNotBlank()) LtrText(route, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (mv.notes.isNotBlank()) Text(mv.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${mv.performedBy} • ${mv.occurredAt}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }
    }

    if (showMoveForm) {
        MovementFormSheet(
            asset = asset,
            locations = locations,
            onDismiss = { showMoveForm = false },
            onSave = { type, locId, locName, notes -> onMove(asset, type, locId, locName, notes); showMoveForm = false }
        )
    }
    if (showBomForm) {
        BomFormSheet(
            initial = null,
            assetId = asset.id,
            parts = spareParts,
            onDismiss = { showBomForm = false },
            onSave = { onSaveBom(it); showBomForm = false }
        )
    }
    deleteBom?.let { target ->
        ConfirmDialog(
            title = "حذف بند المكوّنات",
            text = "هل تريد حذف هذا البند من قائمة المكوّنات؟",
            onConfirm = { onDeleteBom(target); deleteBom = null },
            onDismiss = { deleteBom = null }
        )
    }
    if (showCharForm) {
        CharacteristicFormSheet(
            initial = editingChar,
            assetId = asset.id,
            defaultClass = asset.standardClass,
            onDismiss = { showCharForm = false },
            onSave = { onSaveCharacteristic(it); showCharForm = false }
        )
    }
    deleteChar?.let { target ->
        ConfirmDialog(
            title = "حذف الخاصية",
            text = "هل تريد حذف \"${target.name}\"؟",
            onConfirm = { onDeleteCharacteristic(target); deleteChar = null },
            onDismiss = { deleteChar = null }
        )
    }
    if (showDocForm) {
        DocumentFormSheet(
            initial = editingDoc,
            assetId = asset.id,
            onDismiss = { showDocForm = false },
            onSave = { onSaveDocument(it); showDocForm = false }
        )
    }
    deleteDoc?.let { target ->
        ConfirmDialog(
            title = "حذف المستند",
            text = "هل تريد حذف \"${target.title}\"؟",
            onConfirm = { onDeleteDocument(target); deleteDoc = null },
            onDismiss = { deleteDoc = null }
        )
    }

    if (showEdit) {
        AssetFormSheet(initial = asset, onDismiss = { showEdit = false }, onSave = { onSaveAsset(it); showEdit = false }, locations = locations, allAssets = allAssets)
    }
    if (showStatus) {
        StatusPickerDialog(
            current = asset.status,
            options = lifecycle,
            onPick = { onChangeStatus(asset, it); showStatus = false },
            onDismiss = { showStatus = false }
        )
    }
    if (showWoForm) {
        WorkOrderFormSheet(
            initial = null,
            assets = listOf(asset),
            defaultAssignee = defaultAssignee,
            onDismiss = { showWoForm = false },
            onSave = { onSaveWorkOrder(it); showWoForm = false }
        )
    }
}

// ---------------------------------------------------------------------------
// Maintenance notifications (بلاغات)
// ---------------------------------------------------------------------------

private fun notificationStatusLabel(status: String): String = when (status) {
    "New" -> "جديد"
    "Screened" -> "تمت المراجعة"
    "Approved" -> "معتمد"
    "Rejected" -> "مرفوض"
    "OrderCreated" -> "تحوّل لأمر"
    "Closed" -> "مغلق"
    else -> status
}

private fun roleLabel(role: String): String = when (role.lowercase(Locale.getDefault())) {
    "admin" -> "مدير"
    "supervisor" -> "مشرف"
    "technician" -> "فني"
    else -> role
}

private fun workOrderStatusLabel(status: String): String = when (status) {
    "Open" -> "مفتوح"
    "In Progress" -> "قيد التنفيذ"
    "Technically Completed" -> "مكتمل فنياً"
    "Closed" -> "مغلق"
    else -> status
}

private fun notificationStatusTone(status: String) = when (status) {
    "Approved", "OrderCreated" -> statusTone("running")
    "Rejected", "Closed" -> statusTone("stopped")
    "Screened" -> statusTone("scheduled")
    else -> statusTone("overdue")
}

@Composable
private fun NotificationsScreen(
    innerPadding: PaddingValues,
    notifications: List<MaintenanceNotificationEntity>,
    assets: List<AssetEntity>,
    assetMap: Map<Long, AssetEntity>,
    canManage: Boolean,
    onSave: (MaintenanceNotificationEntity) -> Unit,
    onSetStatus: (MaintenanceNotificationEntity, String) -> Unit,
    onCreateOrder: (MaintenanceNotificationEntity) -> Unit,
    onDelete: (MaintenanceNotificationEntity) -> Unit
) {
    val filters = listOf("All", "New", "Screened", "Approved", "OrderCreated", "Closed")
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<MaintenanceNotificationEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<MaintenanceNotificationEntity?>(null) }
    val filtered = remember(selectedFilter, notifications) {
        if (selectedFilter == "All") notifications else notifications.filter { it.status == selectedFilter }
    }
    val openCount = notifications.count { it.status == "New" || it.status == "Screened" }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("بلاغات الصيانة")
                Text("نقطة بداية كل عمل صيانة — تُراجع وتُعتمد ثم تتحول إلى أوامر عمل.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                val seg = listOf(
                    ChartSegment("جديدة/مراجعة", notifications.count { it.status == "New" || it.status == "Screened" }, AccentOrange),
                    ChartSegment("معتمدة", notifications.count { it.status == "Approved" }, AccentTeal),
                    ChartSegment("تحوّلت لأمر", notifications.count { it.status == "OrderCreated" }, AccentGreen),
                    ChartSegment("مرفوضة/مغلقة", notifications.count { it.status == "Rejected" || it.status == "Closed" }, AccentRed)
                )
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DonutChart(segments = seg, centerValue = notifications.size.toString(), centerLabel = "بلاغ")
                        ChartLegend(seg, modifier = Modifier.weight(1f))
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { f ->
                        FilterChip(selected = selectedFilter == f, onClick = { selectedFilter = f }, label = { Text(if (f == "All") "الكل" else notificationStatusLabel(f)) })
                    }
                }
            }
            item { AddButton("بلاغ جديد") { editing = null; showForm = true } }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد بلاغات", Icons.Filled.NotificationsActive) }
            }
            items(filtered, key = { it.id }) { ntf ->
                NotificationCard(
                    notification = ntf,
                    asset = ntf.assetId?.let { assetMap[it] },
                    canManage = canManage,
                    onSetStatus = onSetStatus,
                    onCreateOrder = onCreateOrder,
                    onEdit = { editing = ntf; showForm = true },
                    onDelete = { deleteTarget = ntf }
                )
            }
        }
    }

    if (showForm) {
        NotificationFormSheet(
            initial = editing,
            assets = assets,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف البلاغ",
            text = "هل تريد حذف \"${target.title}\"؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun NotificationCard(
    notification: MaintenanceNotificationEntity,
    asset: AssetEntity?,
    canManage: Boolean,
    onSetStatus: (MaintenanceNotificationEntity, String) -> Unit,
    onCreateOrder: (MaintenanceNotificationEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(notification.number, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(notification.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                StatusBadge(notificationStatusLabel(notification.status), notificationStatusTone(notification.status))
            }
            Text(notification.description, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(notification.type, statusTone("info"))
                StatusBadge(notification.priority, priorityTone(notification.priority))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            if (asset != null) InfoRow("الأصل", "${asset.code} • ${asset.name}")
            if (asset?.isLinearAsset == true && notification.hasLinearReference()) {
                InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, notification.linearStartPoint, notification.linearEndPoint, notification.linearMarker, notification.linearHorizontalOffset, notification.linearVerticalOffset))
            }
            if (notification.damageCode.isNotBlank()) InfoRow("كود الضرر", notification.damageCode)
            if (notification.causeCode.isNotBlank()) InfoRow("كود السبب", notification.causeCode)
            InfoRow("المُبلِّغ", notification.reportedBy)
            if (notification.requiredEnd.isNotBlank()) InfoRow("مطلوب الإنجاز قبل", notification.requiredEnd)
            if (notification.linkedOrderId != null) {
                StatusBadge("أمر عمل #${notification.linkedOrderId}", statusTone("running"))
            }
            if (canManage && notification.status != "Closed" && notification.status != "OrderCreated") {
                when (notification.status) {
                    "New" -> {
                        OutlinedButton(onClick = { onSetStatus(notification, "Screened") }, modifier = Modifier.fillMaxWidth()) { Text("مراجعة البلاغ") }
                    }
                    "Screened" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { onSetStatus(notification, "Approved") }, modifier = Modifier.weight(1f)) { Text("اعتماد") }
                            OutlinedButton(onClick = { onSetStatus(notification, "Rejected") }, modifier = Modifier.weight(1f)) { Text("رفض") }
                        }
                    }
                    "Approved" -> {
                        Button(onClick = { onCreateOrder(notification) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إنشاء أمر عمل")
                        }
                    }
                    "Rejected" -> {
                        OutlinedButton(onClick = { onSetStatus(notification, "Closed") }, modifier = Modifier.fillMaxWidth()) { Text("إغلاق البلاغ") }
                    }
                }
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

// ---------------------------------------------------------------------------
// Work orders
// ---------------------------------------------------------------------------

@Composable
private fun WorkOrdersScreen(
    innerPadding: PaddingValues,
    workOrders: List<WorkOrderEntity>,
    assets: List<AssetEntity>,
    assetMap: Map<Long, AssetEntity>,
    operations: List<WorkOrderOperationEntity>,
    confirmations: List<WorkOrderConfirmationEntity>,
    photos: List<WorkOrderPhotoEntity>,
    permits: List<WorkPermitEntity>,
    parts: List<SparePartEntity>,
    transactions: List<InventoryTransactionEntity>,
    bomHeaders: List<AssetBomHeaderEntity>,
    bom: List<AssetBomItemEntity>,
    canManage: Boolean,
    defaultAssignee: String,
    onIssueMaterial: (WorkOrderEntity, SparePartEntity, Int) -> Unit,
    onExportPdf: (WorkOrderEntity) -> Unit,
    onSave: (WorkOrderEntity) -> Unit,
    onDelete: (WorkOrderEntity) -> Unit,
    onUpdateStatus: (WorkOrderEntity, String) -> Unit,
    onApprove: (WorkOrderEntity, Boolean) -> Unit,
    onSaveOperation: (WorkOrderOperationEntity) -> Unit,
    onSetOperationStatus: (WorkOrderOperationEntity, String) -> Unit,
    onDeleteOperation: (WorkOrderOperationEntity) -> Unit,
    onConfirmOperation: (WorkOrderConfirmationEntity, WorkOrderOperationEntity) -> Unit,
    onAddPhoto: (Long, String) -> Unit,
    onDeletePhoto: (WorkOrderPhotoEntity) -> Unit,
    onSavePermit: (WorkPermitEntity) -> Unit,
    onSetPermitStatus: (WorkPermitEntity, Boolean) -> Unit,
    onDeletePermit: (WorkPermitEntity) -> Unit
) {
    val partMap = remember(parts) { parts.associateBy { it.id } }
    val today = DateStrings.today()
    val statusFilters = listOf("All", "Open", "In Progress", "Technically Completed", "Closed")
    val priorityFilters = listOf("All", "Critical", "High", "Medium", "Low")
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var selectedPriority by rememberSaveable { mutableStateOf("All") }
    var pendingOnly by rememberSaveable { mutableStateOf(false) }
    var query by rememberSaveable { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<WorkOrderEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<WorkOrderEntity?>(null) }
    val filtered = remember(selectedFilter, selectedPriority, pendingOnly, query, workOrders, assetMap) {
        val q = query.lowercase(Locale.getDefault())
        workOrders.filter { wo ->
            val asset = assetMap[wo.assetId]
            (selectedFilter == "All" || wo.status == selectedFilter) &&
                (selectedPriority == "All" || wo.priority == selectedPriority) &&
                (!pendingOnly || wo.approvalStatus == "Pending") &&
                (q.isBlank() ||
                    wo.title.lowercase(Locale.getDefault()).contains(q) ||
                    wo.linearMarker.lowercase(Locale.getDefault()).contains(q) ||
                    (asset?.linearRouteCode?.lowercase(Locale.getDefault())?.contains(q) == true) ||
                    (asset?.networkObjectCode?.lowercase(Locale.getDefault())?.contains(q) == true) ||
                    (asset?.code?.lowercase(Locale.getDefault())?.contains(q) == true) ||
                    (asset?.name?.lowercase(Locale.getDefault())?.contains(q) == true))
        }.sortedWith(compareBy({ it.status == "Closed" }, { it.dueAt }))
    }
    val pendingCount = workOrders.count { it.approvalStatus == "Pending" }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (workOrders.isNotEmpty()) {
                item {
                    val seg = listOf(
                        ChartSegment("مفتوح", workOrders.count { it.status == "Open" }, AccentBlue),
                        ChartSegment("قيد التنفيذ", workOrders.count { it.status == "In Progress" }, AccentOrange),
                        ChartSegment("مكتمل فنياً", workOrders.count { it.status == "Technically Completed" }, AccentTeal),
                        ChartSegment("مغلق", workOrders.count { it.status == "Closed" }, AccentGreen)
                    )
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            DonutChart(segments = seg, centerValue = workOrders.size.toString(), centerLabel = "أمر")
                            ChartLegend(seg, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            item { SearchField(query = query, onChange = { query = it }, placeholder = "بحث بالعنوان أو الأصل…") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusFilters.forEach { filter ->
                        FilterChip(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, label = { Text(if (filter == "All") "الكل" else workOrderStatusLabel(filter)) })
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    priorityFilters.forEach { p ->
                        FilterChip(selected = selectedPriority == p, onClick = { selectedPriority = p }, label = { Text(p) })
                    }
                    if (pendingCount > 0) {
                        FilterChip(
                            selected = pendingOnly,
                            onClick = { pendingOnly = !pendingOnly },
                            label = { Text("بانتظار الاعتماد ($pendingCount)") },
                            leadingIcon = { Icon(Icons.Filled.FactCheck, contentDescription = null, modifier = Modifier.size(16.dp)) }
                        )
                    }
                }
            }
            item {
                Text(
                    "عرض ${filtered.size} من ${workOrders.size} أمر عمل",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (canManage) {
                item { AddButton("أمر عمل جديد") { editing = null; showForm = true } }
            }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد أوامر عمل هنا.", Icons.Filled.Assignment) }
            }
            items(filtered, key = { it.id }) { workOrder ->
                WorkOrderCard(
                    workOrder = workOrder,
                    asset = assetMap[workOrder.assetId],
                    operations = operations.filter { it.orderId == workOrder.id },
                    confirmations = confirmations.filter { it.orderId == workOrder.id },
                    photos = photos.filter { it.orderId == workOrder.id },
                    permits = permits.filter { it.orderId == workOrder.id },
                    materials = transactions.filter { it.workOrderId == workOrder.id },
                    catalog = parts,
                    bomPartIds = assetMap[workOrder.assetId]?.let { orderAsset ->
                        val activeHeaderIds = resolveAssetBomHeaders(orderAsset, bomHeaders)
                            .filter { bomHeaderUsableInOrder(it, today) }
                            .mapTo(mutableSetOf()) { it.id }
                        bom.filter { it.headerId in activeHeaderIds && bomItemUsableInOrder(it, today) }
                            .mapTo(mutableSetOf()) { it.partId }
                    } ?: emptySet(),
                    partMap = partMap,
                    onIssueMaterial = onIssueMaterial,
                    onExportPdf = onExportPdf,
                    canManage = canManage,
                    onUpdateStatus = onUpdateStatus,
                    onApprove = onApprove,
                    onSaveOperation = onSaveOperation,
                    onSetOperationStatus = onSetOperationStatus,
                    onDeleteOperation = onDeleteOperation,
                    onConfirmOperation = onConfirmOperation,
                    onAddPhoto = onAddPhoto,
                    onDeletePhoto = onDeletePhoto,
                    onSavePermit = onSavePermit,
                    onSetPermitStatus = onSetPermitStatus,
                    onDeletePermit = onDeletePermit,
                    onEdit = { editing = workOrder; showForm = true },
                    onDelete = { deleteTarget = workOrder }
                )
            }
        }
    }

    if (showForm) {
        WorkOrderFormSheet(
            initial = editing,
            assets = assets,
            defaultAssignee = defaultAssignee,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف أمر العمل",
            text = "هل تريد حذف \"${target.title}\"؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun WorkOrderCard(
    workOrder: WorkOrderEntity,
    asset: AssetEntity?,
    operations: List<WorkOrderOperationEntity>,
    confirmations: List<WorkOrderConfirmationEntity>,
    photos: List<WorkOrderPhotoEntity>,
    permits: List<WorkPermitEntity>,
    materials: List<InventoryTransactionEntity>,
    catalog: List<SparePartEntity>,
    bomPartIds: Set<Long>,
    partMap: Map<Long, SparePartEntity>,
    onIssueMaterial: (WorkOrderEntity, SparePartEntity, Int) -> Unit,
    onExportPdf: (WorkOrderEntity) -> Unit,
    canManage: Boolean,
    onUpdateStatus: (WorkOrderEntity, String) -> Unit,
    onApprove: (WorkOrderEntity, Boolean) -> Unit,
    onSaveOperation: (WorkOrderOperationEntity) -> Unit,
    onSetOperationStatus: (WorkOrderOperationEntity, String) -> Unit,
    onDeleteOperation: (WorkOrderOperationEntity) -> Unit,
    onConfirmOperation: (WorkOrderConfirmationEntity, WorkOrderOperationEntity) -> Unit,
    onAddPhoto: (Long, String) -> Unit,
    onDeletePhoto: (WorkOrderPhotoEntity) -> Unit,
    onSavePermit: (WorkPermitEntity) -> Unit,
    onSetPermitStatus: (WorkPermitEntity, Boolean) -> Unit,
    onDeletePermit: (WorkPermitEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val today = DateStrings.today()
    var showMaterialPicker by remember { mutableStateOf(false) }
    var materialTarget by remember { mutableStateOf<SparePartEntity?>(null) }
    val pending = workOrder.approvalStatus == "Pending"
    val rejected = workOrder.approvalStatus == "Rejected"
    val blocked = workOrder.isBlockedByApproval()
    var showOperations by remember { mutableStateOf(false) }
    var showAddOp by remember { mutableStateOf(false) }
    var showAddPermit by remember { mutableStateOf(false) }
    var confirmTarget by remember { mutableStateOf<WorkOrderOperationEntity?>(null) }
    val confirmedOps = operations.count { it.status == "Confirmed" }
    val hasValidPermit = permits.any { it.isValidOn(today) }
    val permitBlocked = workOrder.requiresPermit && !hasValidPermit
    val hasEvidence = photos.isNotEmpty()
    var pendingPhotoPath by remember { mutableStateOf<String?>(null) }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        val path = pendingPhotoPath
        if (success && path != null) onAddPhoto(workOrder.id, path) else if (path != null) ImageStore.delete(path)
        pendingPhotoPath = null
    }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(workOrder.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LtrText(asset?.let { "${it.code} • ${it.name}" } ?: "Asset #${workOrder.assetId}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(workOrderStatusLabel(workOrder.status), statusTone(workOrder.status))
            }
            Text(workOrder.description, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(workOrder.priority, priorityTone(workOrder.priority))
                AssistChip(onClick = {}, label = { Text(workOrder.assignedTo, maxLines = 1) })
            }
            when (workOrder.approvalStatus) {
                "Pending" -> StatusBadge("بانتظار الاعتماد", statusTone("overdue"))
                "Approved" -> StatusBadge("معتمد${if (workOrder.approvedBy.isNotBlank()) " • ${workOrder.approvedBy}" else ""}", statusTone("running"))
                "Rejected" -> StatusBadge("مرفوض", statusTone("stopped"))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            InfoRow("تاريخ الإنشاء", workOrder.createdAt)
            InfoRow("تاريخ الاستحقاق", workOrder.dueAt)
            InfoRow("التكلفة التقديرية", "%.2f".format(workOrder.estimatedCost))
            if (asset?.isLinearAsset == true && workOrder.hasLinearReference()) {
                InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, workOrder.linearStartPoint, workOrder.linearEndPoint, workOrder.linearMarker, workOrder.linearHorizontalOffset, workOrder.linearVerticalOffset))
            }

            Row(
                modifier = Modifier.fillMaxWidth().clickable { showOperations = !showOperations },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentBlue)
                Spacer(modifier = Modifier.width(6.dp))
                Text("العمليات ($confirmedOps/${operations.size} مؤكدة)", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Icon(if (showOperations) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null)
            }
            if (showOperations) {
                if (operations.isEmpty()) {
                    Text("لا توجد عمليات. أضِف خطوات التنفيذ.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                operations.forEach { op ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            LtrText(op.operationNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(op.description, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            StatusBadge(
                                when (op.status) { "Confirmed" -> "مؤكد"; "In Progress" -> "جارٍ"; else -> "مفتوح" },
                                statusTone(when (op.status) { "Confirmed" -> "running"; "In Progress" -> "scheduled"; else -> "info" })
                            )
                        }
                        Text(
                            "مركز العمل: ${op.workCenter.ifBlank { "—" }} • مخطط ${op.plannedHours}س" + if (op.actualHours > 0) " • فعلي ${op.actualHours}س" else "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        confirmations.filter { it.operationId == op.id }.forEach { c ->
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(14.dp), tint = AccentGreen)
                                Text(
                                    "${c.technician} • ${c.workDate} • ${c.actualWork}س" + if (c.finalConfirmation) " • نهائي" else "",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (c.actionTaken.isNotBlank()) {
                                Text("الإجراء: ${c.actionTaken}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        if (canManage && op.status != "Confirmed") {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                if (op.status == "Open") {
                                    OutlinedButton(onClick = { onSetOperationStatus(op, "In Progress") }, modifier = Modifier.weight(1f)) { Text("بدء", style = MaterialTheme.typography.labelMedium) }
                                }
                                Button(onClick = { confirmTarget = op }, modifier = Modifier.weight(1f)) { Text("تأكيد", style = MaterialTheme.typography.labelMedium) }
                                IconButton(onClick = { onDeleteOperation(op) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                if (canManage) {
                    OutlinedButton(onClick = { showAddOp = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة عملية")
                    }
                }
            }

            run {
                val materialsCost = materials.sumOf { (partMap[it.partId]?.lastPrice ?: 0.0) * it.quantity }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Inventory2, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentPurple)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("المواد المستهلكة (${materials.size})", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    if (materialsCost > 0) Text(money(materialsCost), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AccentPurple)
                }
                materials.forEach { tx ->
                    val p = partMap[tx.partId]
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("×${tx.quantity}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = AccentPurple)
                        Column(modifier = Modifier.weight(1f)) {
                            LtrText(p?.partNumber ?: "Part #${tx.partId}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text(p?.name ?: "", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                if (canManage && workOrder.status != "Closed") {
                    OutlinedButton(onClick = { showMaterialPicker = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("صرف قطعة للأمر")
                    }
                }
            }

            if (canManage && (pending || rejected)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    if (pending) {
                        Button(onClick = { onApprove(workOrder, true) }, modifier = Modifier.weight(1f)) { Text("اعتماد") }
                        OutlinedButton(onClick = { onApprove(workOrder, false) }, modifier = Modifier.weight(1f)) { Text("رفض") }
                    } else {
                        Button(onClick = { onApprove(workOrder, true) }, modifier = Modifier.fillMaxWidth()) { Text("إعادة الاعتماد") }
                    }
                }
            }
            if (workOrder.status != "Closed") {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("أدلة التنفيذ (${photos.size})", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    if (canManage) {
                        TextButton(onClick = {
                            val file = ImageStore.createCaptureFile(context, workOrder.id)
                            pendingPhotoPath = file.absolutePath
                            cameraLauncher.launch(ImageStore.uriFor(context, file))
                        }) {
                            Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("التقاط صورة")
                        }
                    }
                }
                if (photos.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        photos.forEach { photo ->
                            Box {
                                val bmp = remember(photo.path) { ImageStore.decode(photo.path) }
                                if (bmp != null) {
                                    Image(
                                        bitmap = bmp,
                                        contentDescription = "دليل تنفيذ",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)).clip(RoundedCornerShape(8.dp))
                                    )
                                } else {
                                    Box(modifier = Modifier.size(72.dp).background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp)), contentAlignment = Alignment.Center) {
                                        Icon(Icons.Filled.Description, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                                if (canManage) {
                                    Box(
                                        modifier = Modifier.align(Alignment.TopEnd).size(22.dp).background(MaterialTheme.colorScheme.error, CircleShape).clickable { onDeletePhoto(photo) },
                                        contentAlignment = Alignment.Center
                                    ) { Icon(Icons.Filled.Delete, contentDescription = "حذف", tint = Color.White, modifier = Modifier.size(14.dp)) }
                                }
                            }
                        }
                    }
                } else {
                    Text("لا توجد صور بعد — التقط صورة دليل بالكاميرا قبل الإغلاق.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (workOrder.status != "Closed" && (workOrder.requiresPermit || permits.isNotEmpty())) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.HealthAndSafety, contentDescription = null, modifier = Modifier.size(18.dp), tint = if (permitBlocked) AccentRed else AccentGreen)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("تصاريح العمل (${permits.size})", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    if (canManage) {
                        TextButton(onClick = { showAddPermit = true }) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("إصدار تصريح")
                        }
                    }
                }
                permits.forEach { permit ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(permitTypeLabel(permit.type), fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            val valid = permit.isValidOn(today)
                            StatusBadge(
                                when {
                                    permit.status == "Approved" && valid -> "ساري"
                                    permit.status == "Approved" -> "منتهٍ"
                                    permit.status == "Rejected" -> "مرفوض"
                                    else -> "بانتظار الاعتماد"
                                },
                                statusTone(if (permit.status == "Approved" && valid) "running" else if (permit.status == "Rejected") "stopped" else "overdue")
                            )
                        }
                        if (permit.hazards.isNotBlank()) Text("المخاطر: ${permit.hazards}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (permit.ppe.isNotBlank()) Text("الوقاية: ${permit.ppe}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (permit.validUntil.isNotBlank()) Text("صالح حتى: ${permit.validUntil}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (canManage) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                if (permit.status == "Pending") {
                                    Button(onClick = { onSetPermitStatus(permit, true) }, modifier = Modifier.weight(1f)) { Text("اعتماد", style = MaterialTheme.typography.labelMedium) }
                                    OutlinedButton(onClick = { onSetPermitStatus(permit, false) }, modifier = Modifier.weight(1f)) { Text("رفض", style = MaterialTheme.typography.labelMedium) }
                                }
                                IconButton(onClick = { onDeletePermit(permit) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                if (permitBlocked) {
                    Text("هذا العمل خطر — يلزم تصريح عمل ساري قبل البدء.", style = MaterialTheme.typography.bodySmall, color = AccentRed, fontWeight = FontWeight.Bold)
                }
            }

            if (blocked) {
                Text(
                    if (pending) "يتطلّب اعتماد المشرف قبل البدء/الإغلاق." else "أمر العمل مرفوض — لا يمكن تنفيذه.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            } else {
                val allConfirmed = operations.isNotEmpty() && operations.none { it.requiresConfirmation && it.status != "Confirmed" }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    when (workOrder.status) {
                        "Open" -> {
                            Button(onClick = { onUpdateStatus(workOrder, "In Progress") }, enabled = !permitBlocked, modifier = Modifier.fillMaxWidth()) { Text("بدء التنفيذ") }
                        }
                        "In Progress" -> {
                            Button(onClick = { onUpdateStatus(workOrder, "Technically Completed") }, enabled = allConfirmed, modifier = Modifier.fillMaxWidth()) { Text("إكمال فني") }
                        }
                        "Technically Completed" -> {
                            Button(onClick = { onUpdateStatus(workOrder, "Closed") }, enabled = hasEvidence, modifier = Modifier.fillMaxWidth()) { Text("إغلاق نهائي") }
                        }
                    }
                }
                if (workOrder.status == "In Progress" && !allConfirmed) {
                    Text("الإكمال الفني يتطلّب تأكيد كل العمليات المطلوبة.", style = MaterialTheme.typography.bodySmall, color = AccentOrange, fontWeight = FontWeight.Bold)
                }
                if (workOrder.status == "Technically Completed" && !hasEvidence) {
                    Text("الإغلاق النهائي يتطلّب التقاط صورة دليل تنفيذ بالكاميرا.", style = MaterialTheme.typography.bodySmall, color = AccentOrange, fontWeight = FontWeight.Bold)
                }
            }
            OutlinedButton(onClick = { onExportPdf(workOrder) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.PictureAsPdf, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("تصدير أمر العمل PDF")
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }

    if (showAddOp) {
        OperationFormSheet(
            orderId = workOrder.id,
            nextNumber = "%04d".format(((operations.mapNotNull { it.operationNumber.toIntOrNull() }.maxOrNull() ?: 0) + 10)),
            onDismiss = { showAddOp = false },
            onSave = { onSaveOperation(it); showAddOp = false }
        )
    }
    confirmTarget?.let { op ->
        ConfirmationFormSheet(
            operation = op,
            isFailureOrder = workOrder.isFailure,
            onDismiss = { confirmTarget = null },
            onSave = { onConfirmOperation(it, op); confirmTarget = null }
        )
    }
    if (showAddPermit) {
        PermitFormSheet(
            orderId = workOrder.id,
            onDismiss = { showAddPermit = false },
            onSave = { onSavePermit(it); showAddPermit = false }
        )
    }
    if (showMaterialPicker) {
        MaterialPickerSheet(
            catalog = catalog,
            bomPartIds = bomPartIds,
            onDismiss = { showMaterialPicker = false },
            onPick = { materialTarget = it; showMaterialPicker = false }
        )
    }
    materialTarget?.let { part ->
        QuantityDialog(
            title = "صرف ${part.partNumber} لأمر العمل",
            label = "الكمية (المتوفر ${part.onHandQty})",
            maxValue = part.onHandQty,
            onConfirm = { qty -> onIssueMaterial(workOrder, part, qty); materialTarget = null },
            onDismiss = { materialTarget = null }
        )
    }
}

@Composable
private fun MaterialPickerSheet(
    catalog: List<SparePartEntity>,
    bomPartIds: Set<Long>,
    onDismiss: () -> Unit,
    onPick: (SparePartEntity) -> Unit
) {
    var query by remember { mutableStateOf("") }
    val filtered = remember(query, catalog, bomPartIds) {
        val q = query.lowercase(Locale.getDefault())
        catalog
            .filter { it.onHandQty > 0 && (q.isBlank() || it.partNumber.lowercase(Locale.getDefault()).contains(q) || it.name.lowercase(Locale.getDefault()).contains(q)) }
            .sortedByDescending { it.id in bomPartIds }
    }
    FormSheet("اختر قطعة للصرف", onDismiss) {
        SearchField(query = query, onChange = { query = it }, placeholder = "بحث في القطع…")
        if (filtered.isEmpty()) {
            Text("لا توجد قطع متوفرة مطابقة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        filtered.take(30).forEach { part ->
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().then(if (part.serializationActive) Modifier else Modifier.clickable { onPick(part) }),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Column(modifier = Modifier.weight(1f)) {
                        LtrText(part.partNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        Text(part.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (part.serializationActive) {
                        StatusBadge("صرف تسلسلي فقط", statusTone("scheduled"))
                    } else {
                        if (part.id in bomPartIds) StatusBadge("موصى بها", statusTone("info"))
                        StatusBadge("متوفر ${part.onHandQty}", statusTone("running"))
                    }
                }
            }
        }
    }
}

/** Arabic label for a permit type. */
private fun permitTypeLabel(type: String): String = when (type) {
    "Hot Work" -> "أعمال ساخنة"
    "Confined Space" -> "أماكن مغلقة"
    "Electrical" -> "أعمال كهربائية"
    "Working at Height" -> "العمل على ارتفاع"
    "LOTO" -> "عزل الطاقة (LOTO)"
    "General" -> "تصريح عام"
    else -> type
}

// ---------------------------------------------------------------------------
// Preventive maintenance (Supervision tab)
// ---------------------------------------------------------------------------

@Composable
private fun PreventiveMaintenanceScreen(
    innerPadding: PaddingValues,
    pmItems: List<PreventiveMaintenanceEntity>,
    assets: List<AssetEntity>,
    assetMap: Map<Long, AssetEntity>,
    canManage: Boolean,
    checklist: List<PmChecklistItemEntity>,
    taskLists: List<TaskListEntity>,
    onSave: (PreventiveMaintenanceEntity) -> Unit,
    onDelete: (PreventiveMaintenanceEntity) -> Unit,
    onDone: (PreventiveMaintenanceEntity) -> Unit,
    onSaveChecklistItem: (PmChecklistItemEntity) -> Unit,
    onSetChecklistResult: (PmChecklistItemEntity, String) -> Unit,
    onDeleteChecklistItem: (PmChecklistItemEntity) -> Unit,
    onGenerateOrder: (PreventiveMaintenanceEntity) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<PreventiveMaintenanceEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<PreventiveMaintenanceEntity?>(null) }
    val taskListMap = taskLists.associateBy { it.id }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("جدول الصيانة الدورية")
                Text("المهام مرتبة حسب أقرب تاريخ استحقاق.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (pmItems.isNotEmpty()) {
                item {
                    val due = pmItems.count { DateStrings.isDueOrOverdue(it.nextDueAt) }
                    val seg = listOf(
                        ChartSegment("مستحقة", due, AccentOrange),
                        ChartSegment("مجدولة", pmItems.size - due, AccentGreen)
                    )
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            DonutChart(segments = seg, centerValue = pmItems.size.toString(), centerLabel = "مهمة")
                            ChartLegend(seg, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            if (canManage) {
                item { AddButton("مهمة صيانة جديدة") { editing = null; showForm = true } }
            }
            if (pmItems.isEmpty()) {
                item { EmptyState("لا توجد مهام صيانة دورية", Icons.Filled.EventRepeat) }
            }
            items(pmItems, key = { it.id }) { item ->
                PreventiveMaintenanceCard(
                    item = item,
                    asset = assetMap[item.assetId],
                    canManage = canManage,
                    checklist = checklist.filter { it.pmId == item.id },
                    taskListName = item.taskListId?.let { taskListMap[it]?.name },
                    onDone = onDone,
                    onGenerateOrder = onGenerateOrder,
                    onEdit = { editing = item; showForm = true },
                    onDelete = { deleteTarget = item },
                    onSaveChecklistItem = onSaveChecklistItem,
                    onSetChecklistResult = onSetChecklistResult,
                    onDeleteChecklistItem = onDeleteChecklistItem
                )
            }
        }
    }

    if (showForm) {
        PmFormSheet(initial = editing, assets = assets, taskLists = taskLists, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف مهمة الصيانة",
            text = "هل تريد حذف \"${target.title}\"؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun PreventiveMaintenanceCard(
    item: PreventiveMaintenanceEntity,
    asset: AssetEntity?,
    canManage: Boolean,
    checklist: List<PmChecklistItemEntity>,
    taskListName: String?,
    onDone: (PreventiveMaintenanceEntity) -> Unit,
    onGenerateOrder: (PreventiveMaintenanceEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onSaveChecklistItem: (PmChecklistItemEntity) -> Unit,
    onSetChecklistResult: (PmChecklistItemEntity, String) -> Unit,
    onDeleteChecklistItem: (PmChecklistItemEntity) -> Unit
) {
    val due = DateStrings.isDueOrOverdue(item.nextDueAt)
    var showChecklist by remember { mutableStateOf(false) }
    var showAddItem by remember { mutableStateOf(false) }
    val doneCount = checklist.count { it.result == "OK" || it.result == "NA" }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LtrText(asset?.let { "${it.code} • ${it.name}" } ?: "Asset #${item.assetId}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (due) "مستحقة" else "مجدولة", statusTone(if (due) "overdue" else "scheduled"))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            InfoRow("التكرار", "كل ${item.frequencyDays} يوم")
            InfoRow("آخر تنفيذ", item.lastDoneAt)
            InfoRow("التنفيذ القادم", item.nextDueAt)
            InfoRow("المدة المقدرة", "${item.estimatedDurationMinutes} دقيقة")
            if (taskListName != null) InfoRow("قالب العمل", taskListName)

            Row(
                modifier = Modifier.fillMaxWidth().clickable { showChecklist = !showChecklist },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Filled.Checklist, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentTeal)
                Spacer(modifier = Modifier.width(6.dp))
                Text("قائمة الفحص ($doneCount/${checklist.size})", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Icon(
                    if (showChecklist) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = null
                )
            }
            if (checklist.isNotEmpty()) {
                BarMeter(
                    label = "إنجاز الفحص",
                    fraction = doneCount.toFloat() / checklist.size,
                    color = if (doneCount == checklist.size) AccentGreen else AccentTeal,
                    valueLabel = "$doneCount/${checklist.size}"
                )
            }
            if (showChecklist) {
                if (checklist.isEmpty()) {
                    Text("لا توجد بنود فحص.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                checklist.forEach { ci ->
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(ci.text, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyMedium)
                            if (canManage) {
                                IconButton(onClick = { onDeleteChecklistItem(ci) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("OK" to "سليم", "NotOK" to "عطل", "NA" to "لا ينطبق").forEach { (value, label) ->
                                FilterChip(
                                    selected = ci.result == value,
                                    onClick = { onSetChecklistResult(ci, if (ci.result == value) "" else value) },
                                    label = { Text(label, style = MaterialTheme.typography.labelSmall) }
                                )
                            }
                        }
                    }
                }
                if (canManage) {
                    OutlinedButton(onClick = { showAddItem = true }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة بند فحص")
                    }
                }
            }

            Button(onClick = { onDone(item) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تم التنفيذ")
            }
            if (canManage) {
                OutlinedButton(onClick = { onGenerateOrder(item) }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (taskListName != null) "توليد أمر عمل من القالب" else "توليد أمر عمل")
                }
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }

    if (showAddItem) {
        ChecklistItemFormSheet(
            pmId = item.id,
            nextOrder = (checklist.maxOfOrNull { it.orderIndex } ?: 0) + 1,
            onDismiss = { showAddItem = false },
            onSave = { onSaveChecklistItem(it); showAddItem = false }
        )
    }
}

// ---------------------------------------------------------------------------
// Task lists (قوالب العمل)
// ---------------------------------------------------------------------------

@Composable
private fun TaskListsScreen(
    innerPadding: PaddingValues,
    taskLists: List<TaskListEntity>,
    operations: List<TaskListOperationEntity>,
    canManage: Boolean,
    onSaveTaskList: (TaskListEntity) -> Unit,
    onDeleteTaskList: (TaskListEntity) -> Unit,
    onSaveOperation: (TaskListOperationEntity) -> Unit,
    onDeleteOperation: (TaskListOperationEntity) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<TaskListEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<TaskListEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("قوالب العمل")
                Text("قوالب عمليات قابلة لإعادة الاستخدام، تُنسخ إلى أمر العمل عند توليده من خطة وقائية.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canManage) {
                item { AddButton("قالب عمل جديد") { editing = null; showForm = true } }
            }
            if (taskLists.isEmpty()) {
                item { EmptyState("لا توجد قوالب عمل", Icons.AutoMirrored.Filled.List) }
            }
            items(taskLists, key = { it.id }) { tl ->
                TaskListCard(
                    taskList = tl,
                    operations = operations.filter { it.taskListId == tl.id },
                    canManage = canManage,
                    onSaveOperation = onSaveOperation,
                    onDeleteOperation = onDeleteOperation,
                    onEdit = { editing = tl; showForm = true },
                    onDelete = { deleteTarget = tl }
                )
            }
        }
    }

    if (showForm) {
        TaskListFormSheet(initial = editing, onDismiss = { showForm = false }, onSave = { onSaveTaskList(it); showForm = false })
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف القالب",
            text = "هل تريد حذف \"${target.name}\" وكل عملياته؟",
            onConfirm = { onDeleteTaskList(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun TaskListCard(
    taskList: TaskListEntity,
    operations: List<TaskListOperationEntity>,
    canManage: Boolean,
    onSaveOperation: (TaskListOperationEntity) -> Unit,
    onDeleteOperation: (TaskListOperationEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    var showAddOp by remember { mutableStateOf(false) }
    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconBubble(Icons.AutoMirrored.Filled.List, AccentBlue, AccentBlue.copy(alpha = 0.14f), 40)
                Column(modifier = Modifier.weight(1f)) {
                    Text(taskList.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    if (taskList.description.isNotBlank()) Text(taskList.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge("${operations.size} عملية", statusTone("info"))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            operations.forEach { op ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    LtrText(op.operationNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(op.description, style = MaterialTheme.typography.bodyMedium)
                        Text("${op.workCenter.ifBlank { "—" }} • ${op.plannedHours}س", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (canManage) {
                        IconButton(onClick = { onDeleteOperation(op) }) {
                            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
            if (operations.isEmpty()) {
                Text("لا توجد عمليات في هذا القالب.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canManage) {
                OutlinedButton(onClick = { showAddOp = true }, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إضافة عملية للقالب")
                }
                EditDeleteRow(onEdit, onDelete)
            }
        }
    }

    if (showAddOp) {
        TaskListOperationFormSheet(
            taskListId = taskList.id,
            defaultWorkCenter = taskList.defaultWorkCenter,
            nextNumber = "%04d".format(((operations.mapNotNull { it.operationNumber.toIntOrNull() }.maxOrNull() ?: 0) + 10)),
            onDismiss = { showAddOp = false },
            onSave = { onSaveOperation(it); showAddOp = false }
        )
    }
}

// ---------------------------------------------------------------------------
// Inventory
// ---------------------------------------------------------------------------

@Composable
private fun InventoryScreen(
    innerPadding: PaddingValues,
    parts: List<SparePartEntity>,
    profiles: List<SerialNumberProfileEntity>,
    serials: List<SerialNumberEntity>,
    transactions: List<InventoryTransactionEntity>,
    canReceive: Boolean,
    canManage: Boolean,
    onOpenSerialNumbers: () -> Unit,
    onIssue: (SparePartEntity, Int) -> Unit,
    onReceive: (SparePartEntity, Int) -> Unit,
    onSave: (SparePartEntity) -> Unit,
    onDelete: (SparePartEntity) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var lowStockOnly by rememberSaveable { mutableStateOf(false) }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<SparePartEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<SparePartEntity?>(null) }
    val lowStockCount = parts.count { it.onHandQty <= it.minQty }
    val totalValue = parts.sumOf { it.onHandQty * it.lastPrice }
    val filtered = remember(query, lowStockOnly, parts) {
        parts.filter { part ->
            val q = query.lowercase(Locale.getDefault())
            (!lowStockOnly || part.onHandQty <= part.minQty) &&
                (q.isBlank() ||
                    part.partNumber.lowercase(Locale.getDefault()).contains(q) ||
                    part.name.lowercase(Locale.getDefault()).contains(q) ||
                    part.equipmentGroup.lowercase(Locale.getDefault()).contains(q))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                val healthy = (parts.size - lowStockCount).coerceAtLeast(0)
                val healthPct = if (parts.isEmpty()) 100f else healthy.toFloat() / parts.size * 100f
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        RingGauge(percent = healthPct, color = if (lowStockCount > 0) AccentOrange else AccentGreen, centerLabel = "متوفر")
                        Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            SectionHeader("حالة المخزون")
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                MetricColumn("أصناف", parts.size.toString(), AccentBlue)
                                MetricColumn("منخفض", lowStockCount.toString(), if (lowStockCount > 0) AccentRed else AccentGreen)
                            }
                            InfoRow("قيمة المخزون", money(totalValue))
                        }
                    }
                }
            }
            item { SearchField(query = query, onChange = { query = it }, placeholder = "بحث: BRG-6205 أو Sensor") }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = !lowStockOnly, onClick = { lowStockOnly = false }, label = { Text("الكل") })
                    FilterChip(
                        selected = lowStockOnly,
                        onClick = { lowStockOnly = true },
                        label = { Text("منخفض المخزون ($lowStockCount)") },
                        leadingIcon = { Icon(Icons.Filled.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) }
                    )
                }
            }
            if (canManage) {
                item { AddButton("قطعة غيار جديدة") { editing = null; showForm = true } }
            }
            item { SectionHeader("قطع الغيار (${filtered.size})") }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد قطع غيار مطابقة", Icons.Filled.Inventory2) }
            }
            filtered.groupBy { it.equipmentGroup.ifBlank { "عام" } }.forEach { (group, groupParts) ->
                item(key = "grp-$group") {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Box(modifier = Modifier.size(7.dp).background(AccentPurple, CircleShape))
                        LtrText("$group (${groupParts.size})", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
                items(groupParts, key = { it.id }) { part ->
                    SparePartCard(
                        part = part,
                        profile = part.serialProfileId?.let { id -> profiles.firstOrNull { it.id == id } },
                        serials = serials.filter { it.partId == part.id },
                        canReceive = canReceive,
                        canManage = canManage,
                        onIssue = onIssue,
                        onReceive = onReceive,
                        onEdit = { editing = part; showForm = true },
                        onOpenSerialNumbers = onOpenSerialNumbers,
                        onDelete = { deleteTarget = part }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("آخر حركات المخزون")
            }
            if (transactions.isEmpty()) {
                item { EmptyState("لا توجد حركات مخزون") }
            }
            items(transactions, key = { it.id }) { transaction ->
                TransactionCard(transaction = transaction, partNumber = parts.firstOrNull { it.id == transaction.partId }?.partNumber)
            }
        }
    }

    if (showForm) {
        PartFormSheet(initial = editing, profiles = profiles, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف القطعة",
            text = "هل تريد حذف ${target.partNumber} - ${target.name}؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun SparePartCard(
    part: SparePartEntity,
    profile: SerialNumberProfileEntity?,
    serials: List<SerialNumberEntity>,
    canReceive: Boolean,
    canManage: Boolean,
    onIssue: (SparePartEntity, Int) -> Unit,
    onReceive: (SparePartEntity, Int) -> Unit,
    onEdit: () -> Unit,
    onOpenSerialNumbers: () -> Unit,
    onDelete: () -> Unit
) {
    val lowStock = part.onHandQty <= part.minQty
    val serialInStock = serials.count { it.status == "InStock" }
    var moveMode by remember { mutableStateOf<String?>(null) } // "issue" | "receive"
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(part.partNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(part.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (lowStock) "منخفض" else "متوفر", statusTone(if (lowStock) "stopped" else "running"))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            InfoRow("المعدة", part.equipmentGroup)
            InfoRow("الكمية", "${part.onHandQty} ${part.unit}")
            InfoRow("الحد الأدنى", "${part.minQty} ${part.unit}")
            InfoRow("الموقع", part.location)
            InfoRow("آخر سعر", "%.2f".format(part.lastPrice))
            InfoRow("قيمة المخزون", money(part.onHandQty * part.lastPrice))
            if (part.serializationActive) {
                InfoRow("ملف التتبع", profile?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                InfoRow("الوحدات المتسلسلة في المخزون", serialInStock.toString())
                if (serialInStock != part.onHandQty) {
                    StatusBadge("اختلاف بين الكمية والوحدات", statusTone("stopped"))
                }
                Button(onClick = onOpenSerialNumbers, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إدارة الأرقام التسلسلية")
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { moveMode = "issue" }, enabled = part.onHandQty > 0, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("صرف")
                    }
                    if (canReceive) {
                        Button(onClick = { moveMode = "receive" }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استلام")
                        }
                    }
                }
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }

    if (!part.serializationActive) moveMode?.let { mode ->
        val isIssue = mode == "issue"
        QuantityDialog(
            title = if (isIssue) "صرف ${part.partNumber}" else "استلام ${part.partNumber}",
            label = if (isIssue) "الكمية المصروفة (المتوفر ${part.onHandQty})" else "الكمية المستلمة",
            maxValue = if (isIssue) part.onHandQty else null,
            onConfirm = { qty ->
                if (isIssue) onIssue(part, qty) else onReceive(part, qty)
                moveMode = null
            },
            onDismiss = { moveMode = null }
        )
    }
}

@Composable
private fun QuantityDialog(
    title: String,
    label: String,
    maxValue: Int?,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("1") }
    val qty = text.toIntOrNull()
    val valid = qty != null && qty > 0 && (maxValue == null || qty <= maxValue)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { c -> c.isDigit() } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { TextButton(enabled = valid, onClick = { onConfirm(qty!!) }) { Text("تأكيد") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
private fun TransactionCard(transaction: InventoryTransactionEntity, partNumber: String?) {
    val isIssue = transaction.transactionType == "Issue"
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(
                icon = if (isIssue) Icons.Filled.Bolt else Icons.Filled.Add,
                tint = if (isIssue) StatusStopped else StatusRunning,
                container = if (isIssue) StatusStoppedContainer else StatusRunningContainer,
                size = 40
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isIssue) "صرف ${transaction.quantity}" else "استلام ${transaction.quantity}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                LtrText(partNumber ?: "Part #${transaction.partId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${transaction.createdAt} • ${transaction.createdBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (transaction.serialNumbers.isNotBlank()) {
                    Text("الأرقام: ${transaction.serialNumbers}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            if (transaction.workOrderId != null) {
                StatusBadge("أمر #${transaction.workOrderId}", statusTone("info"))
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Reports
// ---------------------------------------------------------------------------

@Composable
private fun ReportsScreen(
    innerPadding: PaddingValues,
    stats: DashboardStats,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    parts: List<SparePartEntity>,
    pmItems: List<PreventiveMaintenanceEntity>
) {
    val today = DateStrings.today()
    val soon = DateStrings.daysFromToday(30)
    val openCost = workOrders.filter { it.status != "Closed" }.sumOf { it.estimatedCost }
    val closed = workOrders.count { it.status == "Closed" }
    val lowStock = parts.filter { it.onHandQty <= it.minQty }
    val duePm = pmItems.filter { DateStrings.isDueOrOverdue(it.nextDueAt) }
    val underWarranty = assets.filter { it.isUnderWarranty(today) }
    val expiringSoon = assets.filter { it.warrantyEnd.isNotBlank() && it.warrantyEnd in today..soon }
    val totalCost = workOrders.sumOf { it.totalCost() }
    val laborCost = workOrders.sumOf { it.laborCost() }
    val partsCost = workOrders.sumOf { it.partsCost }
    val assetName = assets.associate { it.id to it.code }
    val topCostAssets = workOrders.groupBy { it.assetId }
        .mapValues { (_, list) -> list.sumOf { it.totalCost() } }
        .entries.sortedByDescending { it.value }
        .take(3)
    val failures = workOrders.filter { it.isFailure }
    val totalDowntime = failures.sumOf { it.downtimeHours }
    val mttr = if (failures.isNotEmpty()) totalDowntime / failures.size else 0.0
    val openWos = workOrders.filter { it.status != "Closed" }
    val overdueWos = openWos.count { DateStrings.isDueOrOverdue(it.dueAt) }
    val pendingApprovals = workOrders.count { it.approvalStatus == "Pending" }
    val windowHours = (assets.size.coerceAtLeast(1)) * 30.0 * 24.0
    val availability = ((windowHours - totalDowntime) / windowHours * 100.0).coerceIn(0.0, 100.0)
    val context = LocalContext.current
    val reportText = buildReportText(
        assets = stats.assets, openWo = stats.openWorkOrders, closed = closed,
        totalCost = totalCost, laborCost = laborCost, partsCost = partsCost, openCost = openCost,
        availability = availability, failures = failures.size, downtime = totalDowntime, mttr = mttr,
        overdue = overdueWos, pendingApprovals = pendingApprovals, duePm = duePm.size,
        lowStock = lowStock.size, underWarranty = underWarranty.size, expiringSoon = expiringSoon.size
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionHeader("التقارير والتحليلات")
                    Text("ملخصات حيّة للتكاليف والتوفر والأعطال.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                OutlinedButton(onClick = { shareText(context, "تقرير الصيانة — الهادي CMMS", reportText) }) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاركة")
                }
            }
        }
        item {
            val seg = listOf(
                ChartSegment("مفتوح", workOrders.count { it.status == "Open" }, AccentBlue),
                ChartSegment("قيد التنفيذ", workOrders.count { it.status == "In Progress" }, AccentOrange),
                ChartSegment("مكتمل فنياً", workOrders.count { it.status == "Technically Completed" }, AccentTeal),
                ChartSegment("مغلق", closed, AccentGreen)
            )
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DonutChart(segments = seg, centerValue = workOrders.size.toString(), centerLabel = "أمر عمل")
                    ChartLegend(seg, modifier = Modifier.weight(1f))
                }
            }
        }
        item {
            val maxC = listOf(laborCost, partsCost, openCost, 1.0).max()
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RingGauge(percent = availability.toFloat(), color = AccentGreen, centerLabel = "التوفّر")
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader("التكاليف")
                        BarMeter("عمالة", (laborCost / maxC).toFloat(), AccentBlue, money(laborCost))
                        BarMeter("قطع غيار", (partsCost / maxC).toFloat(), AccentPurple, money(partsCost))
                        BarMeter("تقديرية مفتوحة", (openCost / maxC).toFloat(), AccentOrange, money(openCost))
                    }
                }
            }
        }
        item {
            ReportCard("التوفّر والأعطال", listOf(
                "نسبة التوفّر (30 يوم): ${"%.1f".format(availability)}%",
                "عدد الأعطال: ${failures.size}",
                "إجمالي زمن التوقف: ${"%.1f".format(totalDowntime)} ساعة",
                "متوسط زمن الإصلاح MTTR: ${"%.1f".format(mttr)} ساعة"
            ))
        }
        item {
            ReportCard("حالة أوامر العمل", listOf(
                "مفتوحة حالياً: ${openWos.size}",
                "متأخرة عن الاستحقاق: $overdueWos",
                "بانتظار الاعتماد: $pendingApprovals",
                "مغلقة: $closed"
            ))
        }
        item {
            ReportCard("ملخص الصيانة", listOf(
                "إجمالي الأصول: ${stats.assets}",
                "أوامر العمل المفتوحة: ${stats.openWorkOrders}",
                "أوامر العمل المغلقة: $closed",
                "تكلفة تقديرية مفتوحة: ${"%.2f".format(openCost)}"
            ))
        }
        item {
            ReportCard("التكاليف", listOf(
                "إجمالي تكلفة الصيانة: ${money(totalCost)}",
                "تكلفة العمالة: ${money(laborCost)}",
                "تكلفة قطع الغيار: ${money(partsCost)}",
                "تكلفة تقديرية مفتوحة: ${money(openCost)}"
            ))
        }
        if (topCostAssets.isNotEmpty()) {
            item {
                val maxCost = (topCostAssets.maxOfOrNull { it.value } ?: 1.0).coerceAtLeast(1.0)
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader("الأصول الأعلى تكلفة")
                        topCostAssets.forEach {
                            BarMeter(assetName[it.key] ?: "#${it.key}", (it.value / maxCost).toFloat(), AccentRed, money(it.value))
                        }
                    }
                }
            }
        }
        item {
            ReportCard("الصيانة الدورية", listOf(
                "مهام PM المستحقة: ${duePm.size}",
                "أقرب مهمة: ${duePm.firstOrNull()?.title ?: "لا يوجد"}"
            ))
        }
        item {
            ReportCard("المخزون", listOf(
                "قطع تحت الحد الأدنى: ${lowStock.size}",
                "أول قطعة ناقصة: ${lowStock.firstOrNull()?.partNumber ?: "لا يوجد"}"
            ))
        }
        item {
            ReportCard("الضمان", listOf(
                "أصول ضمن الضمان: ${underWarranty.size}",
                "ضمانات تنتهي خلال 30 يوم: ${expiringSoon.size}",
                "الأقرب انتهاءً: ${expiringSoon.minByOrNull { it.warrantyEnd }?.let { "${it.code} (${it.warrantyEnd})" } ?: "لا يوجد"}"
            ))
        }
    }
}

@Composable
private fun ReportCard(title: String, lines: List<String>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(title)
            lines.forEach { line -> Text("• $line", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

// ---------------------------------------------------------------------------
// Audit (governance) + Admin
// ---------------------------------------------------------------------------

@Composable
private fun AuditScreen(innerPadding: PaddingValues, auditLog: List<AuditLogEntity>) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(query, auditLog) {
        if (query.isBlank()) auditLog else auditLog.filter {
            val q = query.lowercase(Locale.getDefault())
            it.details.lowercase(Locale.getDefault()).contains(q) ||
                it.performedBy.lowercase(Locale.getDefault()).contains(q) ||
                it.action.lowercase(Locale.getDefault()).contains(q)
        }
    }
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("سجل التدقيق")
            Text("تتبّع كامل لكل إجراء: من فعل ماذا ومتى.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { SearchField(query = query, onChange = { query = it }, placeholder = "بحث في السجل (إجراء/مستخدم/تفاصيل)…") }
        item {
            Text("عرض ${filtered.size} من ${auditLog.size} سجل", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (filtered.isEmpty()) {
            item { EmptyState("لا توجد سجلات مطابقة", Icons.Filled.History) }
        }
        items(filtered, key = { it.id }) { log -> AuditLogCard(log) }
    }
}

@Composable
private fun AuditLogCard(log: AuditLogEntity) {
    val (icon, color) = auditVisual(log.action)
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconBubble(icon, color, color.copy(alpha = 0.14f), 40)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(log.details, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                Text("${log.performedBy} • ${log.createdAt}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(auditActionLabel(log.action), statusTone(log.action))
        }
    }
}

private fun auditActionLabel(action: String): String = when (action) {
    "Create" -> "إنشاء"; "Update" -> "تعديل"; "Delete" -> "حذف"; "Status" -> "حالة"
    "Approval" -> "اعتماد"; "Login" -> "دخول"; "Issue" -> "صرف"; "Receive" -> "استلام"
    "Confirm", "PartialConfirm" -> "تأكيد"; "Complete" -> "تنفيذ"; "Movement" -> "حركة"
    "Reading" -> "قراءة"; "Attach" -> "إرفاق"; "Import" -> "استيراد"; "Generate" -> "توليد"
    "Seed" -> "تهيئة"; else -> action
}

private fun auditVisual(action: String): Pair<ImageVector, Color> = when (action) {
    "Create", "Generate" -> Icons.Filled.Add to AccentGreen
    "Delete" -> Icons.Filled.Delete to AccentRed
    "Approval" -> Icons.Filled.FactCheck to AccentTeal
    "Status", "Update" -> Icons.Filled.Edit to AccentBlue
    "Login" -> Icons.Filled.Verified to AccentNavy
    "Issue", "Receive" -> Icons.Filled.Inventory2 to AccentPurple
    "Confirm", "PartialConfirm", "Complete" -> Icons.Filled.CheckCircle to AccentGreen
    "Movement" -> Icons.Filled.SwapHoriz to AccentBlue
    "Reading" -> Icons.Filled.Speed to AccentPurple
    "Attach" -> Icons.Filled.PhotoCamera to AccentTeal
    "Import" -> Icons.Filled.UploadFile to AccentGreen
    else -> Icons.Filled.History to AccentOrange
}

@Composable
private fun AdminScreen(
    innerPadding: PaddingValues,
    users: List<UserEntity>,
    currentUser: UserEntity?,
    onAddTechnician: () -> Unit,
    onResetSampleData: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onRunReminders: () -> Unit,
    onSave: (UserEntity) -> Unit,
    onSetActive: (UserEntity, Boolean) -> Unit,
    onDelete: (UserEntity) -> Unit
) {
    if (currentUser?.isAdmin != true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) { EmptyState("هذه الصفحة للمدير فقط", Icons.Filled.AdminPanelSettings) }
        return
    }

    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<UserEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<UserEntity?>(null) }
    var showRestoreConfirm by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { AddButton("مستخدم جديد") { editing = null; showForm = true } }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onAddTechnician, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("فني سريع")
                    }
                    OutlinedButton(onClick = onResetSampleData, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إعادة تعيين")
                    }
                }
            }
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconBubble(Icons.Filled.Backup, AccentTeal, AccentTeal.copy(alpha = 0.14f), 38)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("النسخ الاحتياطي والاستعادة", fontWeight = FontWeight.Bold)
                                Text("احفظ كل البيانات في ملف، أو استعدها على جهاز آخر.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = onExportBackup, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Save, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("نسخة احتياطية")
                            }
                            OutlinedButton(onClick = { showRestoreConfirm = true }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Restore, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("استعادة")
                            }
                        }
                        Text(
                            "تنبيه: الاستعادة تستبدل كل البيانات الحالية بمحتوى الملف.",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconBubble(Icons.Filled.NotificationsActive, AccentOrange, AccentOrange.copy(alpha = 0.14f), 38)
                            Column(modifier = Modifier.weight(1f)) {
                                Text("تذكيرات الصيانة", fontWeight = FontWeight.Bold)
                                Text("فحص يومي تلقائي ينبّهك بالمستحقات والمتأخرات.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        OutlinedButton(onClick = onRunReminders, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.NotificationsActive, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("فحص التذكيرات الآن")
                        }
                    }
                }
            }
            item {
                val admins = users.count { it.isAdmin }
                val supervisors = users.count { it.role.equals("Supervisor", ignoreCase = true) }
                val techs = users.size - admins - supervisors
                val seg = listOf(
                    ChartSegment("مدراء", admins, AccentRed),
                    ChartSegment("مشرفون", supervisors, AccentOrange),
                    ChartSegment("فنيون", techs.coerceAtLeast(0), AccentBlue)
                )
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DonutChart(segments = seg, centerValue = users.size.toString(), centerLabel = "مستخدم")
                        ChartLegend(seg, modifier = Modifier.weight(1f))
                    }
                }
            }
            item { SectionHeader("المستخدمون (${users.size})") }
            items(users, key = { it.id }) { user ->
                UserCard(
                    user = user,
                    isSelf = user.id == currentUser.id,
                    onEdit = { editing = user; showForm = true },
                    onToggleActive = { onSetActive(user, !user.isActive) },
                    onDelete = { deleteTarget = user }
                )
            }
        }
    }

    if (showForm) {
        UserFormSheet(initial = editing, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف المستخدم",
            text = "هل تريد حذف ${target.name} (@${target.username})؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
    if (showRestoreConfirm) {
        ConfirmDialog(
            title = "استعادة نسخة احتياطية",
            text = "سيتم استبدال جميع البيانات الحالية بمحتوى الملف الذي ستختاره. هل تريد المتابعة؟",
            onConfirm = { showRestoreConfirm = false; onImportBackup() },
            onDismiss = { showRestoreConfirm = false }
        )
    }
}

@Composable
private fun UserCard(
    user: UserEntity,
    isSelf: Boolean,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                CircleAvatar(initials = user.initials, size = 42)
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontWeight = FontWeight.Bold)
                    LtrText("@${user.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (user.isActive) roleLabel(user.role) else "معطّل", statusTone(if (!user.isActive) "neutral" else if (user.isAdmin) "info" else "running"))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعديل")
                }
                if (!isSelf) {
                    OutlinedButton(onClick = onToggleActive, modifier = Modifier.weight(1f)) {
                        Text(if (user.isActive) "تعطيل" else "تفعيل")
                    }
                    TextButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                        Text("حذف", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Meters & readings
// ---------------------------------------------------------------------------

@Composable
private fun MetersScreen(
    innerPadding: PaddingValues,
    points: List<MeasuringPointEntity>,
    readings: List<MeasurementReadingEntity>,
    assetMap: Map<Long, AssetEntity>,
    assets: List<AssetEntity>,
    canManage: Boolean,
    onSavePoint: (MeasuringPointEntity) -> Unit,
    onDeletePoint: (MeasuringPointEntity) -> Unit,
    onAddReading: (MeasuringPointEntity, Double, String) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<MeasuringPointEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<MeasuringPointEntity?>(null) }
    var readingTarget by remember { mutableStateOf<MeasuringPointEntity?>(null) }
    val grouped = points.groupBy { it.assetId }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("نقاط القياس")
                Text("تابع أداء الأصول وسجّل القراءات.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (points.isNotEmpty()) {
                item {
                    val over = points.count { it.upperLimit != null && it.lastReading > it.upperLimit }
                    val seg = listOf(
                        ChartSegment("ضمن الحد", points.size - over, AccentGreen),
                        ChartSegment("متجاوزة الحد", over, AccentRed)
                    )
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            DonutChart(segments = seg, centerValue = points.size.toString(), centerLabel = "نقطة")
                            ChartLegend(seg, modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            if (canManage) {
                item { AddButton("نقطة قياس جديدة") { editing = null; showForm = true } }
            }
            if (points.isEmpty()) {
                item { EmptyState("لا توجد نقاط قياس", Icons.Filled.Speed) }
            }
            grouped.forEach { (assetId, assetPoints) ->
                val asset = assetMap[assetId]
                item { SectionHeader(asset?.let { "${it.code} • ${it.name}" } ?: "Asset #$assetId") }
                items(assetPoints, key = { it.id }) { point ->
                    MeterCard(
                        point = point,
                        recentReadings = readings.filter { it.pointId == point.id }.take(6),
                        canManage = canManage,
                        onAddReading = { readingTarget = point },
                        onEdit = { editing = point; showForm = true },
                        onDelete = { deleteTarget = point }
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("آخر القراءات")
            }
            if (readings.isEmpty()) {
                item { EmptyState("لا توجد قراءات بعد") }
            }
            items(readings.take(30), key = { it.id }) { reading ->
                val point = points.firstOrNull { it.id == reading.pointId }
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        IconBubble(Icons.Filled.Speed, AccentPurple, AccentPurple.copy(alpha = 0.14f), 40)
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${point?.name ?: "Point #${reading.pointId}"}: ${reading.value} ${point?.unit ?: ""}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                            Text("${reading.createdAt} • ${reading.createdBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
            }
        }
    }

    if (showForm) {
        MeterFormSheet(initial = editing, assets = assets, onDismiss = { showForm = false }, onSave = { onSavePoint(it); showForm = false })
    }
    readingTarget?.let { target ->
        ReadingDialog(point = target, onSubmit = { v, note -> onAddReading(target, v, note); readingTarget = null }, onDismiss = { readingTarget = null })
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف نقطة القياس",
            text = "هل تريد حذف \"${target.name}\"؟",
            onConfirm = { onDeletePoint(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun MeterCard(
    point: MeasuringPointEntity,
    recentReadings: List<MeasurementReadingEntity>,
    canManage: Boolean,
    onAddReading: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val overLimit = point.upperLimit != null && point.lastReading > point.upperLimit
    // recentReadings come newest-first; the previous reading is index 1.
    val previous = recentReadings.getOrNull(1)?.value
    val delta = previous?.let { point.lastReading - it }
    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(point.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            "${point.lastReading} ${point.unit}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (overLimit) StatusStopped else MaterialTheme.colorScheme.primary
                        )
                        if (delta != null && delta != 0.0) {
                            val up = delta > 0
                            Icon(
                                if (up) Icons.Filled.TrendingUp else Icons.Filled.TrendingDown,
                                contentDescription = null,
                                tint = if (up) AccentGreen else AccentRed,
                                modifier = Modifier.size(18.dp)
                            )
                            LtrText("${if (up) "+" else ""}${"%.1f".format(delta)}", style = MaterialTheme.typography.labelMedium, color = if (up) AccentGreen else AccentRed)
                        }
                    }
                }
                StatusBadge(if (point.isCounter) "عداد" else "قراءة", statusTone("info"))
            }
            if (point.upperLimit != null) {
                InfoRow("الحد الأعلى", "${point.upperLimit} ${point.unit}")
            }
            InfoRow("آخر تحديث", point.lastReadingAt)
            if (recentReadings.size >= 2) {
                Text("اتجاه آخر القراءات", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Sparkline(
                    values = recentReadings.take(8).reversed().map { it.value.toFloat() },
                    color = if (overLimit) AccentRed else AccentPurple
                )
            }
            if (overLimit) {
                Text("تجاوز الحد الأعلى!", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onAddReading, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تسجيل قراءة")
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

// ---------------------------------------------------------------------------
// Functional locations
// ---------------------------------------------------------------------------

/** Depth-first ordering of the location tree, returning each node with its depth. */
private fun orderedLocations(all: List<FunctionalLocationEntity>): List<Pair<FunctionalLocationEntity, Int>> {
    val byParent = all.groupBy { it.parentId }
    val result = mutableListOf<Pair<FunctionalLocationEntity, Int>>()
    val placed = mutableSetOf<Long>()
    fun visit(parentId: Long?, depth: Int) {
        byParent[parentId]?.sortedBy { it.code }?.forEach { loc ->
            if (placed.add(loc.id)) {
                result += loc to depth
                visit(loc.id, depth + 1)
            }
        }
    }
    visit(null, 0)
    all.filter { it.id !in placed }.sortedBy { it.code }.forEach { result += it to 0 }
    return result
}

@Composable
private fun WarehousesScreen(
    innerPadding: PaddingValues,
    warehouses: List<WarehouseEntity>,
    parts: List<SparePartEntity>,
    canManage: Boolean,
    onSave: (WarehouseEntity) -> Unit,
    onDelete: (WarehouseEntity) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<WarehouseEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<WarehouseEntity?>(null) }

    val partCounts = remember(parts) { parts.groupBy { it.location } }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SectionHeader("المستودعات والمخازن")
                Text("سجّل المخازن وأمناء العهدة واربط قطع الغيار بها.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canManage) {
                item { AddButton("مستودع جديد") { editing = null; showForm = true } }
            }
            if (warehouses.isEmpty()) {
                item { EmptyState("لا توجد مستودعات", Icons.Filled.Warehouse) }
            }
            items(warehouses, key = { it.id }) { warehouse ->
                WarehouseCard(
                    warehouse = warehouse,
                    partCount = (partCounts[warehouse.code]?.size ?: 0) + (partCounts[warehouse.name]?.size ?: 0),
                    canManage = canManage,
                    onEdit = { editing = warehouse; showForm = true },
                    onDelete = { deleteTarget = warehouse }
                )
            }
        }
    }

    if (showForm) {
        WarehouseFormSheet(
            initial = editing,
            existing = warehouses,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف المستودع",
            text = "هل تريد حذف ${target.code} - ${target.name}؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun WarehouseCard(
    warehouse: WarehouseEntity,
    partCount: Int,
    canManage: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val active = warehouse.status.equals("Active", ignoreCase = true)
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconBubble(Icons.Filled.Warehouse, AccentPurple, AccentPurple.copy(alpha = 0.14f), 38)
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(warehouse.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(warehouse.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (active) "نشط" else "متوقف", statusTone(if (active) "active" else "neutral"))
            }
            if (warehouse.location.isNotBlank()) InfoRow("الموقع", warehouse.location)
            if (warehouse.keeper.isNotBlank()) InfoRow("أمين المخزن", warehouse.keeper)
            if (warehouse.phone.isNotBlank()) InfoRow("الهاتف", warehouse.phone)
            if (warehouse.notes.isNotBlank()) InfoRow("ملاحظات", warehouse.notes)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(warehouseTypeLabel(warehouse.type), statusTone("info"))
                StatusBadge("قطع: $partCount", statusTone("neutral"))
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

private fun warehouseTypeLabel(type: String): String = when (type) {
    "Main" -> "رئيسي"
    "Spare" -> "قطع غيار"
    "Tools" -> "عدد وأدوات"
    "Consumables" -> "مواد استهلاكية"
    "Scrap" -> "خردة/تالف"
    else -> type
}

@Composable
private fun LocationsScreen(
    innerPadding: PaddingValues,
    locations: List<FunctionalLocationEntity>,
    assets: List<AssetEntity>,
    canManage: Boolean,
    onSave: (FunctionalLocationEntity) -> Unit,
    onDelete: (FunctionalLocationEntity) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<FunctionalLocationEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<FunctionalLocationEntity?>(null) }
    var detailId by remember { mutableStateOf<Long?>(null) }

    val detail = detailId?.let { id -> locations.firstOrNull { it.id == id } }
    if (detail != null) {
        BackHandler { detailId = null }
        LocationDetailScreen(
            innerPadding = innerPadding,
            location = detail,
            children = locations.filter { it.parentId == detail.id },
            assets = assets.filter { it.locationId == detail.id },
            onBack = { detailId = null },
            onOpenChild = { detailId = it }
        )
        return
    }

    val ordered = remember(locations) { orderedLocations(locations) }
    val assetCounts = remember(assets) { assets.groupBy { it.locationId } }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SectionHeader("شجرة المواقع الفنية")
                Text("نظّم الأصول حسب المصنع والخط والمنطقة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canManage) {
                item { AddButton("موقع فني جديد") { editing = null; showForm = true } }
            }
            if (ordered.isEmpty()) {
                item { EmptyState("لا توجد مواقع فنية", Icons.Filled.AccountTree) }
            }
            items(ordered, key = { it.first.id }) { (loc, depth) ->
                LocationCard(
                    location = loc,
                    depth = depth,
                    assetCount = assetCounts[loc.id]?.size ?: 0,
                    childCount = locations.count { it.parentId == loc.id },
                    canManage = canManage,
                    onOpen = { detailId = loc.id },
                    onEdit = { editing = loc; showForm = true },
                    onDelete = { deleteTarget = loc }
                )
            }
        }
    }

    if (showForm) {
        LocationFormSheet(initial = editing, allLocations = locations, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف الموقع الفني",
            text = "هل تريد حذف ${target.code} - ${target.name}؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun LocationCard(
    location: FunctionalLocationEntity,
    depth: Int,
    assetCount: Int,
    childCount: Int,
    canManage: Boolean,
    onOpen: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = (depth * 16).dp)
            .clickable(onClick = onOpen),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconBubble(Icons.Filled.AccountTree, AccentGreen, AccentGreen.copy(alpha = 0.14f), 38)
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(location.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LtrText(location.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge("أصول: $assetCount", statusTone("info"))
                StatusBadge("فرعية: $childCount", statusTone("neutral"))
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

@Composable
private fun LocationDetailScreen(
    innerPadding: PaddingValues,
    location: FunctionalLocationEntity,
    children: List<FunctionalLocationEntity>,
    assets: List<AssetEntity>,
    onBack: () -> Unit,
    onOpenChild: (Long) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                        .clickable(onClick = onBack),
                    contentAlignment = Alignment.Center
                ) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "رجوع") }
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(location.code, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    LtrText(location.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(location.status, statusTone(location.status))
            }
        }
        if (location.description.isNotBlank()) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Text(location.description, modifier = Modifier.padding(16.dp), style = MaterialTheme.typography.bodyMedium)
                }
            }
        }

        item { SectionHeader("المواقع الفرعية (${children.size})") }
        if (children.isEmpty()) {
            item { EmptyState("لا توجد مواقع فرعية") }
        }
        items(children, key = { "c-${it.id}" }) { child ->
            ElevatedCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onOpenChild(child.id) },
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    IconBubble(Icons.Filled.AccountTree, AccentGreen, AccentGreen.copy(alpha = 0.14f), 36)
                    Column(modifier = Modifier.weight(1f)) {
                        LtrText(child.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        LtrText(child.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }

        item { SectionHeader("الأصول في هذا الموقع (${assets.size})") }
        if (assets.isEmpty()) {
            item { EmptyState("لا توجد أصول مرتبطة بهذا الموقع") }
        }
        items(assets, key = { "a-${it.id}" }) { asset ->
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        LtrText(asset.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        LtrText(asset.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    StatusBadge(asset.status, statusTone(asset.status))
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// CAPA
// ---------------------------------------------------------------------------

@Composable
private fun CapaScreen(
    innerPadding: PaddingValues,
    items: List<CapaEntity>,
    assets: List<AssetEntity>,
    assetMap: Map<Long, AssetEntity>,
    canManage: Boolean,
    defaultAssignee: String,
    onSave: (CapaEntity) -> Unit,
    onUpdateStatus: (CapaEntity, String) -> Unit,
    onDelete: (CapaEntity) -> Unit
) {
    val filters = listOf("All", "Open", "In Progress", "Closed")
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<CapaEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<CapaEntity?>(null) }
    val filtered = remember(selectedFilter, items) {
        if (selectedFilter == "All") items else items.filter { it.status == selectedFilter }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("الإجراءات التصحيحية والوقائية")
                Text("إجراءات لمعالجة الأعطال ومنع تكرارها.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                val today = DateStrings.today()
                val open = items.count { it.status == "Open" }
                val inProg = items.count { it.status == "In Progress" }
                val closed = items.count { it.status == "Closed" }
                val seg = listOf(
                    ChartSegment("مفتوح", open, AccentBlue),
                    ChartSegment("قيد التنفيذ", inProg, AccentOrange),
                    ChartSegment("مغلق", closed, AccentGreen)
                )
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DonutChart(segments = seg, centerValue = items.size.toString(), centerLabel = "إجراء")
                        ChartLegend(seg, modifier = Modifier.weight(1f))
                    }
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    filters.forEach { f ->
                        FilterChip(selected = selectedFilter == f, onClick = { selectedFilter = f }, label = { Text(f) })
                    }
                }
            }
            if (canManage) {
                item { AddButton("إجراء CAPA جديد") { editing = null; showForm = true } }
            }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد إجراءات", Icons.Filled.FactCheck) }
            }
            items(filtered, key = { it.id }) { capa ->
                CapaCard(
                    capa = capa,
                    asset = capa.assetId?.let { assetMap[it] },
                    canManage = canManage,
                    onUpdateStatus = onUpdateStatus,
                    onEdit = { editing = capa; showForm = true },
                    onDelete = { deleteTarget = capa }
                )
            }
        }
    }

    if (showForm) {
        CapaFormSheet(
            initial = editing,
            assets = assets,
            defaultAssignee = defaultAssignee,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف الإجراء",
            text = "هل تريد حذف \"${target.title}\"؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun CapaCard(
    capa: CapaEntity,
    asset: AssetEntity?,
    canManage: Boolean,
    onUpdateStatus: (CapaEntity, String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(capa.code, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(capa.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                StatusBadge(capa.status, statusTone(capa.status))
            }
            Text(capa.description, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(if (capa.type == "Preventive") "وقائي" else "تصحيحي", statusTone(if (capa.type == "Preventive") "info" else "warning"))
                StatusBadge(capa.priority, priorityTone(capa.priority))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            if (asset != null) InfoRow("الأصل", "${asset.code} • ${asset.name}")
            InfoRow("المسؤول", capa.assignedTo)
            InfoRow("الاستحقاق", capa.dueAt)
            if (capa.status != "Closed" && capa.dueAt < DateStrings.today()) {
                Text("متأخر عن تاريخ الاستحقاق", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
            }
            if (capa.status != "Closed") {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    if (capa.status == "Open") {
                        OutlinedButton(onClick = { onUpdateStatus(capa, "In Progress") }, modifier = Modifier.weight(1f)) { Text("بدء") }
                    }
                    Button(onClick = { onUpdateStatus(capa, "Closed") }, modifier = Modifier.weight(1f)) { Text("إغلاق") }
                }
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

// ---------------------------------------------------------------------------
// Failure analysis (MTTR / MTBF)
// ---------------------------------------------------------------------------

private data class FailureStat(
    val assetId: Long,
    val failures: Int,
    val mttrHours: Double,
    val mtbfDays: Double?
)

@Composable
private fun FailureAnalysisScreen(
    innerPadding: PaddingValues,
    workOrders: List<WorkOrderEntity>,
    assetMap: Map<Long, AssetEntity>
) {
    val failures = remember(workOrders) { workOrders.filter { it.isFailure } }
    val stats = remember(failures) {
        failures.groupBy { it.assetId }.map { (assetId, list) ->
            val downtimes = list.map { it.downtimeHours }.filter { it > 0.0 }
            val mttr = if (downtimes.isEmpty()) 0.0 else downtimes.average()
            val sorted = list.map { it.createdAt }.sorted()
            val mtbf = if (sorted.size >= 2) {
                val gaps = sorted.zipWithNext { a, b -> DateStrings.daysBetween(a, b).toDouble() }
                gaps.average()
            } else null
            FailureStat(assetId, list.size, mttr, mtbf)
        }.sortedByDescending { it.failures }
    }
    val overallMttr = remember(failures) {
        val d = failures.map { it.downtimeHours }.filter { it > 0.0 }
        if (d.isEmpty()) 0.0 else d.average()
    }
    val palette = listOf(AccentRed, AccentOrange, AccentBlue, AccentPurple, AccentTeal, AccentNavy)
    val maxMttr = (stats.maxOfOrNull { it.mttrHours } ?: 1.0).coerceAtLeast(0.1)
    val maxMtbf = (stats.mapNotNull { it.mtbfDays }.maxOrNull() ?: 1.0).coerceAtLeast(0.1)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("ملخص الموثوقية")
            Text("MTTR = متوسط زمن الإصلاح • MTBF = متوسط الزمن بين الأعطال.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                KpiTile("إجمالي الأعطال", failures.size.toString(), AccentRed, Modifier.weight(1f))
                KpiTile("MTTR (ساعة)", "%.1f".format(overallMttr), AccentOrange, Modifier.weight(1f))
                KpiTile("أصول متأثرة", stats.size.toString(), AccentBlue, Modifier.weight(1f))
            }
        }

        if (stats.isNotEmpty()) {
            item {
                val seg = stats.take(6).mapIndexed { i, s ->
                    ChartSegment(assetMap[s.assetId]?.code ?: "#${s.assetId}", s.failures, palette[i % palette.size])
                }
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DonutChart(segments = seg, centerValue = failures.size.toString(), centerLabel = "عطل")
                        ChartLegend(seg, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item { SectionHeader("حسب الأصل") }
        if (stats.isEmpty()) {
            item { EmptyState("لا توجد أعطال مسجّلة بعد", Icons.Filled.TrendingUp) }
        }
        items(stats, key = { it.assetId }) { stat ->
            val asset = assetMap[stat.assetId]
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            LtrText(asset?.code ?: "Asset #${stat.assetId}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                            LtrText(asset?.name ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        StatusBadge("أعطال: ${stat.failures}", statusTone(if (stat.failures >= 3) "stopped" else "warning"))
                    }
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                    BarMeter("MTTR", (stat.mttrHours / maxMttr).toFloat(), AccentOrange, "%.1f ساعة".format(stat.mttrHours))
                    BarMeter("MTBF", ((stat.mtbfDays ?: 0.0) / maxMtbf).toFloat(), AccentGreen, stat.mtbfDays?.let { "%.0f يوم".format(it) } ?: "—")
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Shared
// ---------------------------------------------------------------------------

/** Formats a monetary amount with thousands separators and a currency suffix. */
private fun money(value: Double): String = "%,.0f ر.س".format(value)

/** Shares a plain-text report through the Android share sheet (email, WhatsApp, notes…). */
private fun shareText(context: Context, subject: String, body: String) {
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, subject)
        putExtra(Intent.EXTRA_TEXT, body)
    }
    context.startActivity(Intent.createChooser(intent, subject))
}

/** Builds the shareable maintenance report body from the dashboard figures. */
private fun buildReportText(
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
private fun SearchField(query: String, onChange: (String) -> Unit, placeholder: String) {
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
private fun AddButton(label: String, onClick: () -> Unit) {
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
private fun EditDeleteRow(onEdit: () -> Unit, onDelete: () -> Unit) {
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

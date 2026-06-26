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
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.ShoppingCart
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
import com.alhadi.cmms.viewmodel.*
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

internal enum class MoreRoute { Notifications, Inventory, SerialNumbers, Reports, Audit, Admin, PreventiveMaintenance, TaskLists, Meters, Locations, Warehouses, OrgUnits, Suppliers, PurchaseOrders, Capa, Failures }

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
    val serialNumbers by viewModel.serialNumbers.collectAsStateWithLifecycle()
    val serialNumberMovements by viewModel.serialNumberMovements.collectAsStateWithLifecycle()
    val spareParts by viewModel.spareParts.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val locations by viewModel.functionalLocations.collectAsStateWithLifecycle()
    val orgUnits by viewModel.orgUnits.collectAsStateWithLifecycle()
    val assetDocuments by viewModel.assetDocuments.collectAsStateWithLifecycle()
    val assetCharacteristics by viewModel.assetCharacteristics.collectAsStateWithLifecycle()
    val assetBomHeaders by viewModel.assetBomHeaders.collectAsStateWithLifecycle()
    val assetBom by viewModel.assetBom.collectAsStateWithLifecycle()
    val assetMovements by viewModel.assetMovements.collectAsStateWithLifecycle()
    val assetStatusHistory by viewModel.assetStatusHistory.collectAsStateWithLifecycle()
    val pmChecklist by viewModel.pmChecklist.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val workOrderOperations by viewModel.workOrderOperations.collectAsStateWithLifecycle()
    val workOrderConfirmations by viewModel.workOrderConfirmations.collectAsStateWithLifecycle()
    val workOrderPhotos by viewModel.workOrderPhotos.collectAsStateWithLifecycle()
    val workOrderHistory by viewModel.workOrderHistory.collectAsStateWithLifecycle()
    val workOrderMaterials by viewModel.workOrderMaterials.collectAsStateWithLifecycle()
    val workPermits by viewModel.workPermits.collectAsStateWithLifecycle()
    val taskLists by viewModel.taskLists.collectAsStateWithLifecycle()
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
                        history = workOrderHistory,
                        currentUser = currentUser,
                        parts = spareParts,
                        transactions = transactions,
                        plannedMaterials = workOrderMaterials,
                        bomHeaders = assetBomHeaders,
                        bom = assetBom,
                        canManage = canManage,
                        defaultAssignee = actorName,
                        onSavePlannedMaterial = viewModel::savePlannedMaterial,
                        onIssuePlannedMaterial = viewModel::issuePlannedMaterial,
                        onDeletePlannedMaterial = viewModel::deletePlannedMaterial,
                        onIssueMaterial = viewModel::issuePartToWorkOrder,
                        onExportPdf = { order ->
                            pendingPdfOrder = order
                            pdfExportLauncher.launch("WO-${order.id}-${DateStrings.today()}.pdf")
                        },
                        onSave = viewModel::saveWorkOrder,
                        onDelete = viewModel::deleteWorkOrder,
                        onCancel = viewModel::cancelWorkOrder,
                        onReopen = viewModel::reopenWorkOrder,
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
                        statusHistory = assetStatusHistory,
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

                    BottomTab.More -> MoreRouteContent(
                        route = moreRoute,
                        innerPadding = innerPadding,
                        viewModel = viewModel,
                        isAdmin = isAdmin,
                        canManage = canManage,
                        actorName = actorName,
                        onNavigate = { moreRoute = it },
                        onImportBundled = { viewModel.importBundledKit(appContext) },
                        onPickExcel = { excelPicker.launch(arrayOf("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", "application/octet-stream", "*/*")) },
                        onExportBackup = { backupExportLauncher.launch("alhadi-cmms-backup-${DateStrings.today()}.json") },
                        onImportBackup = { backupImportLauncher.launch(arrayOf("application/json", "text/plain", "*/*")) },
                        onRunReminders = { Reminders.runNow(appContext) }
                    )
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
        MoreRoute.Suppliers -> ScreenMeta("الموردون", "بيانات الموردين والمشتريات", Icons.Filled.LocalShipping, AccentBrown)
        MoreRoute.PurchaseOrders -> ScreenMeta("أوامر الشراء", "إنشاء واعتماد ومتابعة المشتريات", Icons.Filled.ShoppingCart, AccentBrown)
        MoreRoute.OrgUnits -> ScreenMeta("الوحدات التنظيمية", "الشركات والمصانع ومراكز العمل والتكلفة", Icons.Filled.CorporateFare, AccentNavy)
        MoreRoute.Capa -> ScreenMeta("الإجراءات CAPA", "إجراءات تصحيحية ووقائية", Icons.Filled.FactCheck, AccentOrange)
        MoreRoute.Failures -> ScreenMeta("تحليل الأعطال", "MTTR و MTBF وتكرار الأعطال", Icons.Filled.TrendingUp, AccentRed)
    }
}


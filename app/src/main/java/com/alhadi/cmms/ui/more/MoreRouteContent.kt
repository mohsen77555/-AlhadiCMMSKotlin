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
import com.alhadi.cmms.data.WorkOrderAuthority
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun MoreRouteContent(
    route: MoreRoute?,
    innerPadding: PaddingValues,
    viewModel: CmmsViewModel,
    isAdmin: Boolean,
    canManage: Boolean,
    actorName: String,
    onNavigate: (MoreRoute?) -> Unit,
    onImportBundled: () -> Unit,
    onPickExcel: () -> Unit,
    onExportBackup: () -> Unit,
    onImportBackup: () -> Unit,
    onRunReminders: () -> Unit
) {
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
    val suppliers by viewModel.suppliers.collectAsStateWithLifecycle()
    val purchaseOrders by viewModel.purchaseOrders.collectAsStateWithLifecycle()
    val purchaseOrderLines by viewModel.purchaseOrderLines.collectAsStateWithLifecycle()
    val assetInstallations by viewModel.assetInstallations.collectAsStateWithLifecycle()
    val orgUnits by viewModel.orgUnits.collectAsStateWithLifecycle()
    val capaActions by viewModel.capaActions.collectAsStateWithLifecycle()
    val pmChecklist by viewModel.pmChecklist.collectAsStateWithLifecycle()
    val notifications by viewModel.notifications.collectAsStateWithLifecycle()
    val taskLists by viewModel.taskLists.collectAsStateWithLifecycle()
    val taskListOperations by viewModel.taskListOperations.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val assetMap = assets.associateBy { it.id }
    when (route) {
                        null -> MoreGrid(
                            innerPadding = innerPadding,
                            isAdmin = isAdmin,
                            canManage = canManage,
                            onOpen = { onNavigate(it) },
                            onImportBundled = onImportBundled,
                            onPickExcel = onPickExcel,
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
                            onOpenSerialNumbers = { onNavigate(MoreRoute.SerialNumbers) },
                            onIssue = viewModel::issuePart,
                            onReceive = viewModel::receivePart,
                            onCycleCount = viewModel::cycleCountPart,
                            onReorder = viewModel::createReorderPurchaseOrders,
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
                        MoreRoute.Audit -> if (WorkOrderAuthority.canViewAudit(currentUser)) {
                            AuditScreen(innerPadding = innerPadding, auditLog = auditLog)
                        } else {
                            EmptyState("لا تملك صلاحية عرض سجل التدقيق (Admin فقط) — WO-AUTH-009")
                        }
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
                            installations = assetInstallations,
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
                        MoreRoute.Suppliers -> SuppliersScreen(
                            innerPadding = innerPadding,
                            suppliers = suppliers,
                            canManage = canManage,
                            onSave = viewModel::saveSupplier,
                            onDelete = viewModel::deleteSupplier
                        )
                        MoreRoute.PurchaseOrders -> PurchaseOrdersScreen(
                            innerPadding = innerPadding,
                            orders = purchaseOrders,
                            lines = purchaseOrderLines,
                            suppliers = suppliers,
                            parts = spareParts,
                            canManage = canManage,
                            onSaveOrder = viewModel::savePurchaseOrder,
                            onCancelOrder = viewModel::cancelPurchaseOrder,
                            onSetStatus = viewModel::setPurchaseOrderStatus,
                            onSaveLine = viewModel::savePurchaseOrderLine,
                            onDeleteLine = viewModel::deletePurchaseOrderLine,
                            onReceiveLine = viewModel::receivePurchaseOrderLine
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
                            onExportBackup = onExportBackup,
                            onImportBackup = onImportBackup,
                            onRunReminders = onRunReminders,
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

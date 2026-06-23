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
// Work orders
// ---------------------------------------------------------------------------

@Composable
internal fun WorkOrdersScreen(
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


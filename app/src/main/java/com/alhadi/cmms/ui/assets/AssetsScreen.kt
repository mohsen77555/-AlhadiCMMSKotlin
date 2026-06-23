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
import com.alhadi.cmms.viewmodel.*
import com.alhadi.cmms.viewmodel.DashboardStats
import java.util.Locale
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Assets
// ---------------------------------------------------------------------------

@Composable
internal fun AssetsScreen(
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
    orgUnits: List<OrgUnitEntity>,
    canManage: Boolean,
    isAdmin: Boolean,
    defaultAssignee: String,
    onSave: (AssetEntity) -> Unit,
    onDelete: (AssetEntity) -> Unit,
    onChangeStatus: (AssetEntity, String, String) -> Unit,
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
            orgUnits = orgUnits,
            canManage = canManage,
            isAdmin = isAdmin,
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
                    val stopped = assets.count { it.status == "Stopped" || it.status == "Retired" || it.status == "Disposed" }
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
        AssetFormSheet(initial = editing, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false }, locations = locations, allAssets = assets, orgUnits = orgUnits, canOverrideSerial = isAdmin, hasLinkedParts = editing?.let { e -> bomItems.any { it.assetId == e.id } } ?: false)
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
internal fun AssetCard(
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


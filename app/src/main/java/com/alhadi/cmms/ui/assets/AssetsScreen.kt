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

@Composable
internal fun AssetDetailScreen(
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
    orgUnits: List<OrgUnitEntity>,
    canManage: Boolean,
    isAdmin: Boolean,
    defaultAssignee: String,
    onBack: () -> Unit,
    onOpenAsset: (Long) -> Unit,
    onSaveAsset: (AssetEntity) -> Unit,
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
    val hasWarranty = asset.warrantyEnd.isNotBlank() || asset.warrantyType.isNotBlank() ||
        asset.warrantyReference.isNotBlank() || asset.warrantyCounterType.isNotBlank() ||
        asset.warrantyProvider.isNotBlank()
    val parent = asset.parentAssetId?.let { id -> allAssets.firstOrNull { it.id == id } }
    val children = allAssets.filter { it.parentAssetId == asset.id }
    var showEdit by remember { mutableStateOf(false) }
    var showStatus by remember { mutableStateOf(false) }
    var showWoForm by remember { mutableStateOf(false) }
    var showMoveForm by remember { mutableStateOf(false) }
    val lifecycle = listOf("Draft", "Active", "Running", "Warning", "Stopped", "Under Maintenance", "Standby", "Retired", "Disposed")
    val retired = asset.status.equals("Retired", ignoreCase = true) || asset.status.equals("Disposed", ignoreCase = true)
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
                    InfoRow("نوع التركيب", if (asset.mobility.equals("Mobile", ignoreCase = true)) "متنقّل" else "ثابت / مركّب")
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
                        if (asset.orgOverrideReason.isNotBlank()) InfoRow("سبب تجاوز الموروث", asset.orgOverrideReason)
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

        val technicalSpecs = listOf(
            "بلد المنشأ" to asset.countryOfOrigin,
            "نوع الإنشاء" to asset.constructionType,
            "مجموعة المواصفات" to asset.technicalSpecGroup,
            "السعة" to asset.capacity,
            "القدرة" to asset.power,
            "الجهد" to asset.voltage,
            "التيار" to asset.current,
            "التردد" to asset.frequency,
            "السرعة" to asset.speed,
            "الضغط" to asset.pressure,
            "معدل التدفق" to asset.flowRate,
            "نطاق الحرارة" to asset.temperatureRange,
            "الوزن" to asset.weight,
            "الأبعاد" to asset.dimensions,
            "المادة" to asset.material,
            "معيار التصميم" to asset.designStandard
        ).filter { it.second.isNotBlank() }
        val sameConstruction = if (asset.constructionType.isNotBlank()) {
            allAssets.count { it.id != asset.id && it.constructionType.equals(asset.constructionType, ignoreCase = true) }
        } else 0
        if (technicalSpecs.isNotEmpty() || asset.nameplateData.isNotBlank() || asset.requiresSerialTracking) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("المواصفات الفنية (لوحة الصنع)")
                        if (asset.requiresSerialTracking) InfoRow("تتبع فردي", "مطلوب رقم تسلسلي")
                        technicalSpecs.forEach { (label, value) -> InfoRow(label, value) }
                        if (asset.nameplateData.isNotBlank()) InfoRow("بيانات لوحة الصنع", asset.nameplateData)
                        // AST-TECH-005: construction type links assets that share components/characteristics.
                        if (sameConstruction > 0) InfoRow("أصول بنفس نوع الإنشاء", "$sameConstruction أصل")
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
                        if (asset.warrantyProvider.isNotBlank()) InfoRow("الجهة", asset.warrantyProvider)
                        if (asset.warrantyStart.isNotBlank()) InfoRow("من", asset.warrantyStart)
                        if (asset.warrantyEnd.isNotBlank()) InfoRow("إلى", asset.warrantyEnd)
                        if (asset.warrantyType.isNotBlank()) InfoRow("النوع", warrantyTypeLabelUi(asset.warrantyType))
                        if (asset.warrantyCategory.isNotBlank()) InfoRow("الفئة", asset.warrantyCategory)
                        if (asset.warrantyReference.isNotBlank()) InfoRow("مرجع العقد", asset.warrantyReference)
                        // AST-WAR-002: counter-based coverage.
                        if (asset.warrantyCounterType.isNotBlank()) {
                            InfoRow("عدّاد الضمان", "${warrantyCounterTypeLabelUi(asset.warrantyCounterType)} • حد ${formatLinearNumber(asset.warrantyCounterLimit)}")
                        }
                        val warrantyKinds = buildList {
                            if (asset.vendorWarranty) add("مورّد")
                            if (asset.manufacturerWarranty) add("مُصنّع")
                            if (asset.customerWarranty) add("عميل")
                        }
                        if (warrantyKinds.isNotEmpty()) InfoRow("نوع الجهة", warrantyKinds.joinToString("، "))
                        if (asset.coveredServices.isNotBlank()) InfoRow("مشمول", asset.coveredServices)
                        if (asset.excludedServices.isNotBlank()) InfoRow("مستثنى", asset.excludedServices)
                        if (asset.warrantyTerms.isNotBlank()) InfoRow("الشروط", asset.warrantyTerms)
                        if (asset.warrantyContact.isNotBlank()) InfoRow("جهة الاتصال", asset.warrantyContact)
                        // AST-WAR-006: warranty document linked to the asset card.
                        if (asset.warrantyDocument.isNotBlank()) InfoRow("مستند الضمان", asset.warrantyDocument)
                        if (asset.warrantyClaimRequired) InfoRow("حالة المطالبة", warrantyClaimStatusLabelUi(asset.warrantyClaimStatus))
                        // AST-WAR-007: assets sharing the same warranty reference.
                        if (asset.warrantyReference.isNotBlank()) {
                            val shared = allAssets.count { it.id != asset.id && it.warrantyReference.equals(asset.warrantyReference, ignoreCase = true) }
                            if (shared > 0) InfoRow("أصول أخرى بنفس الضمان", "$shared أصل")
                        }
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
                        "الأصل ${if (asset.status.equals("Disposed", ignoreCase = true)) "مُستبعَد" else "متقاعد"} — لا يمكن إنشاء أوامر عمل جديدة عليه (AST: Create Work Order).",
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
        AssetFormSheet(initial = asset, onDismiss = { showEdit = false }, onSave = { onSaveAsset(it); showEdit = false }, locations = locations, allAssets = allAssets, orgUnits = orgUnits, canOverrideSerial = isAdmin, hasLinkedParts = bomItems.any { it.assetId == asset.id })
    }
    if (showStatus) {
        StatusPickerDialog(
            current = asset.status,
            options = lifecycle,
            isAdmin = isAdmin,
            onPick = { status, reason -> onChangeStatus(asset, status, reason); showStatus = false },
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


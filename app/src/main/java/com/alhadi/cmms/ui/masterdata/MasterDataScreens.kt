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
// Functional locations
// ---------------------------------------------------------------------------

/** Depth-first ordering of the location tree, returning each node with its depth. */
internal fun orderedLocations(all: List<FunctionalLocationEntity>): List<Pair<FunctionalLocationEntity, Int>> {
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

internal val orgUnitTypeOrder = listOf("Company", "Plant", "Department", "WorkCenter", "PlannerGroup", "CostCenter")

internal fun orgUnitTypeLabel(type: String): String = when (type) {
    "Company" -> "شركة"
    "Plant" -> "مصنع / موقع"
    "Department" -> "قسم"
    "WorkCenter" -> "مركز عمل"
    "PlannerGroup" -> "مجموعة تخطيط"
    "CostCenter" -> "مركز تكلفة"
    else -> type
}

@Composable
internal fun OrgUnitsScreen(
    innerPadding: PaddingValues,
    units: List<OrgUnitEntity>,
    assets: List<AssetEntity>,
    canManage: Boolean,
    onSave: (OrgUnitEntity) -> Unit,
    onDelete: (OrgUnitEntity) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<OrgUnitEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<OrgUnitEntity?>(null) }
    val unitsById = remember(units) { units.associateBy { it.id } }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                SectionHeader("الوحدات التنظيمية")
                Text("الشركات والمصانع والأقسام ومراكز العمل ومجموعات التخطيط ومراكز التكلفة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canManage) {
                item { AddButton("وحدة تنظيمية جديدة") { editing = null; showForm = true } }
            }
            if (units.isEmpty()) {
                item { EmptyState("لا توجد وحدات تنظيمية", Icons.Filled.CorporateFare) }
            }
            orgUnitTypeOrder.forEach { type ->
                val group = units.filter { it.type == type }
                if (group.isNotEmpty()) {
                    item { SectionHeader("${orgUnitTypeLabel(type)} (${group.size})") }
                    items(group, key = { it.id }) { unit ->
                        OrgUnitCard(
                            unit = unit,
                            parentName = unit.parentId?.let { unitsById[it] }?.let { "${it.code} • ${it.name}" },
                            canManage = canManage,
                            onEdit = { editing = unit; showForm = true },
                            onDelete = { deleteTarget = unit }
                        )
                    }
                }
            }
        }
    }

    if (showForm) {
        OrgUnitFormSheet(
            initial = editing,
            existing = units,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف الوحدة التنظيمية",
            text = "هل تريد حذف ${orgUnitTypeLabel(target.type)} ${target.code} - ${target.name}؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
internal fun OrgUnitCard(
    unit: OrgUnitEntity,
    parentName: String?,
    canManage: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val active = unit.isActive
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconBubble(Icons.Filled.CorporateFare, AccentNavy, AccentNavy.copy(alpha = 0.14f), 38)
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(unit.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(unit.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (active) "نشط" else "متوقف", statusTone(if (active) "active" else "neutral"))
            }
            if (parentName != null) InfoRow("ينتمي إلى", parentName)
            if (unit.notes.isNotBlank()) InfoRow("ملاحظات", unit.notes)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(orgUnitTypeLabel(unit.type), statusTone("info"))
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

@Composable
internal fun WarehousesScreen(
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
        val linkedParts = (partCounts[target.code]?.size ?: 0) + (partCounts[target.name]?.size ?: 0)
        if (linkedParts > 0) {
            // Integrity guard: never orphan spare parts by deleting their warehouse.
            AlertDialog(
                onDismissRequest = { deleteTarget = null },
                confirmButton = {
                    TextButton(onClick = { deleteTarget = null }) { Text("حسناً") }
                },
                title = { Text("لا يمكن حذف المستودع") },
                text = { Text("يرتبط بهذا المستودع $linkedParts قطعة غيار. انقل القطع إلى مستودع آخر أولاً، أو اجعل المستودع \"متوقفاً\" بدل حذفه.") }
            )
        } else {
            ConfirmDialog(
                title = "حذف المستودع",
                text = "هل تريد حذف ${target.code} - ${target.name}؟",
                onConfirm = { onDelete(target); deleteTarget = null },
                onDismiss = { deleteTarget = null }
            )
        }
    }
}

@Composable
internal fun WarehouseCard(
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

internal fun warrantyTypeLabelUi(type: String): String = when (type) {
    "Standard" -> "قياسي"
    "Extended" -> "ممتد"
    "Service Contract" -> "عقد خدمة"
    "AMC" -> "عقد صيانة سنوي"
    else -> type
}

internal fun warrantyCounterTypeLabelUi(type: String): String = when (type) {
    "Hours" -> "ساعات تشغيل"
    "Km" -> "كيلومترات"
    "Cycles" -> "دورات"
    "Production" -> "إنتاج"
    else -> type
}

internal fun warrantyClaimStatusLabelUi(status: String): String = when (status) {
    "", "None" -> "لا يوجد"
    "Submitted" -> "مُقدّمة"
    "UnderReview" -> "قيد المراجعة"
    "Approved" -> "مقبولة"
    "Rejected" -> "مرفوضة"
    else -> status
}

internal fun warehouseTypeLabel(type: String): String = when (type) {
    "Main" -> "رئيسي"
    "Spare" -> "قطع غيار"
    "Tools" -> "عدد وأدوات"
    "Consumables" -> "مواد استهلاكية"
    "Scrap" -> "خردة/تالف"
    else -> type
}

@Composable
internal fun LocationsScreen(
    innerPadding: PaddingValues,
    locations: List<FunctionalLocationEntity>,
    assets: List<AssetEntity>,
    orgUnits: List<OrgUnitEntity>,
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
        LocationFormSheet(initial = editing, allLocations = locations, orgUnits = orgUnits, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
    }
    deleteTarget?.let { target ->
        val linkedAssets = assets.count { it.locationId == target.id }
        val childLocations = locations.count { it.parentId == target.id }
        if (linkedAssets > 0 || childLocations > 0) {
            // FLOC-008: never delete a location that still has assets or child locations — deactivate instead.
            AlertDialog(
                onDismissRequest = { deleteTarget = null },
                confirmButton = { TextButton(onClick = { deleteTarget = null }) { Text("حسناً") } },
                title = { Text("لا يمكن حذف الموقع الفني") },
                text = { Text("يرتبط بهذا الموقع $linkedAssets أصل و $childLocations موقع فرعي. انقلها أولاً أو اجعل الموقع \"غير نشط\" بدل حذفه.") }
            )
        } else {
            ConfirmDialog(
                title = "حذف الموقع الفني",
                text = "هل تريد حذف ${target.code} - ${target.name}؟",
                onConfirm = { onDelete(target); deleteTarget = null },
                onDismiss = { deleteTarget = null }
            )
        }
    }
}

@Composable
internal fun LocationCard(
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
internal fun LocationDetailScreen(
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


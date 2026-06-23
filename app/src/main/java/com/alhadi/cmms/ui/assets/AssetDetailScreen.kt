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
    val parent = asset.parentAssetId?.let { id -> allAssets.firstOrNull { it.id == id } }
    val children = allAssets.filter { it.parentAssetId == asset.id }
    var showEdit by remember { mutableStateOf(false) }
    var showStatus by remember { mutableStateOf(false) }
    var showWoForm by remember { mutableStateOf(false) }
    var showMoveForm by remember { mutableStateOf(false) }
    val lifecycle = listOf("Draft", "Active", "Running", "Warning", "Stopped", "Under Maintenance", "Standby", "Retired", "Disposed")
    val retired = asset.status.equals("Retired", ignoreCase = true) || asset.status.equals("Disposed", ignoreCase = true)
    val constructionDate = listOf(asset.constructionYear, asset.constructionMonth)
        .filter { it.isNotBlank() }
        .joinToString(" / ")
    val resolvedCharacteristics = resolveAssetCharacteristics(asset, allAssets, characteristics)
    val directCharacteristics = resolvedCharacteristics.filterNot { it.inherited }
    val inheritedCharacteristics = resolvedCharacteristics.filter { it.inherited }
    val characteristicGroups = resolvedCharacteristics.groupBy { it.resolvedClassName }

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

        assetBasicInfoCard(asset, parent, locationLabel, constructionDate)

        assetLinearCard(asset)

        item {
            AssetSerialSection(
                asset = asset,
                serials = serials,
                movements = serialMovements,
                parts = spareParts
            )
        }

        assetOrgContactCards(asset)

        assetGovernanceCards(asset, allAssets, workOrders)

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

        assetQrCard(asset)

        assetWarrantyCard(asset, allAssets, underWarranty)

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

        assetSubAssetsSection(parent, children, onOpenAsset)
        assetWorkOrdersSection(asset, workOrders, onUpdateWorkOrderStatus)
        assetPmSection(pmItems)
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

        assetMovementsSection(movements)
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


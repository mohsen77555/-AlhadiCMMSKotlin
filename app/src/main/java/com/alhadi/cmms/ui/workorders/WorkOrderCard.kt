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
import com.alhadi.cmms.data.entity.WorkOrderHistoryEntity
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

@Composable
internal fun WorkOrderCard(
    workOrder: WorkOrderEntity,
    asset: AssetEntity?,
    operations: List<WorkOrderOperationEntity>,
    confirmations: List<WorkOrderConfirmationEntity>,
    photos: List<WorkOrderPhotoEntity>,
    permits: List<WorkPermitEntity>,
    history: List<WorkOrderHistoryEntity> = emptyList(),
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
    var showHistory by remember { mutableStateOf(false) }
    var showAddOp by remember { mutableStateOf(false) }
    var showAddPermit by remember { mutableStateOf(false) }
    var confirmTarget by remember { mutableStateOf<WorkOrderOperationEntity?>(null) }
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

            WorkOrderOperationsSection(
                operations = operations,
                confirmations = confirmations,
                canManage = canManage,
                expanded = showOperations,
                onToggleExpand = { showOperations = !showOperations },
                onSetOperationStatus = onSetOperationStatus,
                onDeleteOperation = onDeleteOperation,
                onConfirm = { confirmTarget = it },
                onAddOperation = { showAddOp = true }
            )

            WorkOrderMaterialsSection(
                materials = materials,
                partMap = partMap,
                canManage = canManage,
                workOrderStatus = workOrder.status,
                onIssueMaterial = { showMaterialPicker = true }
            )

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
                WorkOrderEvidenceSection(
                    photos = photos,
                    canManage = canManage,
                    onCapture = {
                        val file = ImageStore.createCaptureFile(context, workOrder.id)
                        pendingPhotoPath = file.absolutePath
                        cameraLauncher.launch(ImageStore.uriFor(context, file))
                    },
                    onDeletePhoto = onDeletePhoto
                )
            }

            if (workOrder.status != "Closed" && (workOrder.requiresPermit || permits.isNotEmpty())) {
                WorkOrderPermitsSection(
                    permits = permits,
                    requiresPermit = workOrder.requiresPermit,
                    canManage = canManage,
                    onAddPermit = { showAddPermit = true },
                    onSetPermitStatus = onSetPermitStatus,
                    onDeletePermit = onDeletePermit
                )
            }

            WorkOrderStatusActions(
                workOrder = workOrder,
                operations = operations,
                blocked = blocked,
                pending = pending,
                permitBlocked = permitBlocked,
                hasEvidence = hasEvidence,
                onUpdateStatus = onUpdateStatus
            )
            if (history.isNotEmpty()) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Row(
                    modifier = Modifier.fillMaxWidth().clickable { showHistory = !showHistory },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Filled.History, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentBrown)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("السجل (${history.size})", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    Icon(if (showHistory) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null)
                }
                if (showHistory) {
                    history.forEach { h ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            Text("${workOrderHistoryFieldLabel(h.field)}: ${h.oldValue.ifBlank { "—" }} ← ${h.newValue.ifBlank { "—" }}", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            Text("${h.actor} • ${h.changedAt}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
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

internal fun workOrderHistoryFieldLabel(field: String): String = when (field) {
    "status" -> "الحالة"
    "approvalStatus" -> "الاعتماد"
    "priority" -> "الأولوية"
    else -> field
}


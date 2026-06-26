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
import com.alhadi.cmms.data.entity.WorkOrderMaterialEntity
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

@Composable
internal fun WorkOrderOperationsSection(
    operations: List<WorkOrderOperationEntity>,
    confirmations: List<WorkOrderConfirmationEntity>,
    canManage: Boolean,
    expanded: Boolean,
    onToggleExpand: () -> Unit,
    onSetOperationStatus: (WorkOrderOperationEntity, String) -> Unit,
    onDeleteOperation: (WorkOrderOperationEntity) -> Unit,
    onConfirm: (WorkOrderOperationEntity) -> Unit,
    onAddOperation: () -> Unit
) {
    val confirmedOps = operations.count { it.status == "Confirmed" }
            Row(
                modifier = Modifier.fillMaxWidth().clickable { onToggleExpand() },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.AutoMirrored.Filled.List, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentBlue)
                Spacer(modifier = Modifier.width(6.dp))
                Text("العمليات ($confirmedOps/${operations.size} مؤكدة)", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                Icon(if (expanded) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown, contentDescription = null)
            }
            if (expanded) {
                if (operations.isEmpty()) {
                    Text("لا توجد عمليات. أضِف خطوات التنفيذ.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                operations.sortedWith(compareBy({ it.sequence }, { it.operationNumber })).forEach { op ->
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
                                Button(onClick = { onConfirm(op) }, modifier = Modifier.weight(1f)) { Text("تأكيد", style = MaterialTheme.typography.labelMedium) }
                                IconButton(onClick = { onDeleteOperation(op) }) {
                                    Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
                if (canManage) {
                    OutlinedButton(onClick = { onAddOperation() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة عملية")
                    }
                }
            }
}

@Composable
internal fun WorkOrderMaterialsSection(
    materials: List<InventoryTransactionEntity>,
    partMap: Map<Long, SparePartEntity>,
    canManage: Boolean,
    workOrderStatus: String,
    onIssueMaterial: () -> Unit
) {
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
                if (canManage && workOrderStatus != "Closed") {
                    OutlinedButton(onClick = { onIssueMaterial() }, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("صرف قطعة للأمر")
                    }
                }
}

@Composable
internal fun WorkOrderPlannedMaterialsSection(
    planned: List<WorkOrderMaterialEntity>,
    canManage: Boolean,
    workOrderStatus: String,
    onAddPlanned: () -> Unit,
    onIssuePlanned: (WorkOrderMaterialEntity) -> Unit,
    onDeletePlanned: (WorkOrderMaterialEntity) -> Unit
) {
                val plannedCost = planned.sumOf { it.plannedTotal }
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Checklist, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentBlue)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("المواد المخطّطة (${planned.size})", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    if (plannedCost > 0) Text(money(plannedCost), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = AccentBlue)
                }
                planned.forEach { material ->
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "${material.issuedQty}/${material.plannedQty}",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            color = if (material.isFullyIssued) AccentGreen else AccentOrange
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            LtrText(material.partNumber.ifBlank { material.description }, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                            if (material.partNumber.isNotBlank()) Text(material.description, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        if (canManage && workOrderStatus != "Closed") {
                            if (!material.isFullyIssued && material.partId != null) {
                                TextButton(onClick = { onIssuePlanned(material) }) { Text("صرف", color = AccentPurple, style = MaterialTheme.typography.labelSmall) }
                            }
                            if (material.issuedQty == 0) {
                                TextButton(onClick = { onDeletePlanned(material) }) { Text("حذف", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
                            }
                        }
                    }
                }
                if (canManage && workOrderStatus != "Closed") {
                    OutlinedButton(onClick = onAddPlanned, modifier = Modifier.fillMaxWidth()) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة مادة مخطّطة")
                    }
                }
}

@Composable
internal fun WorkOrderEvidenceSection(
    photos: List<WorkOrderPhotoEntity>,
    canManage: Boolean,
    onCapture: () -> Unit,
    onDeletePhoto: (WorkOrderPhotoEntity) -> Unit
) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.PhotoCamera, contentDescription = null, modifier = Modifier.size(18.dp), tint = AccentTeal)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("أدلة التنفيذ (${photos.size})", fontWeight = FontWeight.Medium, modifier = Modifier.weight(1f))
                    if (canManage) {
                        TextButton(onClick = {
                            onCapture()
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

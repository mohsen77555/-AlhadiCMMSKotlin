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
// Preventive maintenance (Supervision tab)
// ---------------------------------------------------------------------------

@Composable
internal fun PreventiveMaintenanceScreen(
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
internal fun PreventiveMaintenanceCard(
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


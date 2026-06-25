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
// Maintenance notifications (بلاغات)
// ---------------------------------------------------------------------------

internal fun notificationStatusLabel(status: String): String = when (status) {
    "New" -> "جديد"
    "Screened" -> "تمت المراجعة"
    "Approved" -> "معتمد"
    "Rejected" -> "مرفوض"
    "OrderCreated" -> "تحوّل لأمر"
    "Closed" -> "مغلق"
    else -> status
}

internal fun roleLabel(role: String): String = when (role.lowercase(Locale.getDefault())) {
    "admin" -> "مدير"
    "supervisor" -> "مشرف"
    "technician" -> "فني"
    else -> role
}

internal fun workOrderStatusLabel(status: String): String = when (status) {
    "Draft" -> "مسودة"
    "Open" -> "مفتوح"
    "In Progress" -> "قيد التنفيذ"
    "Technically Completed" -> "مكتمل فنياً"
    "Closed" -> "مغلق"
    "Cancelled" -> "ملغى"
    else -> status
}

internal fun notificationStatusTone(status: String) = when (status) {
    "Approved", "OrderCreated" -> statusTone("running")
    "Rejected", "Closed" -> statusTone("stopped")
    "Screened" -> statusTone("scheduled")
    else -> statusTone("overdue")
}

@Composable
internal fun NotificationsScreen(
    innerPadding: PaddingValues,
    notifications: List<MaintenanceNotificationEntity>,
    assets: List<AssetEntity>,
    assetMap: Map<Long, AssetEntity>,
    canManage: Boolean,
    onSave: (MaintenanceNotificationEntity) -> Unit,
    onSetStatus: (MaintenanceNotificationEntity, String) -> Unit,
    onCreateOrder: (MaintenanceNotificationEntity) -> Unit,
    onDelete: (MaintenanceNotificationEntity) -> Unit
) {
    val filters = listOf("All", "New", "Screened", "Approved", "OrderCreated", "Closed")
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<MaintenanceNotificationEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<MaintenanceNotificationEntity?>(null) }
    val filtered = remember(selectedFilter, notifications) {
        if (selectedFilter == "All") notifications else notifications.filter { it.status == selectedFilter }
    }
    val openCount = notifications.count { it.status == "New" || it.status == "Screened" }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                SectionHeader("بلاغات الصيانة")
                Text("نقطة بداية كل عمل صيانة — تُراجع وتُعتمد ثم تتحول إلى أوامر عمل.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                val seg = listOf(
                    ChartSegment("جديدة/مراجعة", notifications.count { it.status == "New" || it.status == "Screened" }, AccentOrange),
                    ChartSegment("معتمدة", notifications.count { it.status == "Approved" }, AccentTeal),
                    ChartSegment("تحوّلت لأمر", notifications.count { it.status == "OrderCreated" }, AccentGreen),
                    ChartSegment("مرفوضة/مغلقة", notifications.count { it.status == "Rejected" || it.status == "Closed" }, AccentRed)
                )
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DonutChart(segments = seg, centerValue = notifications.size.toString(), centerLabel = "بلاغ")
                        ChartLegend(seg, modifier = Modifier.weight(1f))
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
                    filters.forEach { f ->
                        FilterChip(selected = selectedFilter == f, onClick = { selectedFilter = f }, label = { Text(if (f == "All") "الكل" else notificationStatusLabel(f)) })
                    }
                }
            }
            item { AddButton("بلاغ جديد") { editing = null; showForm = true } }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد بلاغات", Icons.Filled.NotificationsActive) }
            }
            items(filtered, key = { it.id }) { ntf ->
                NotificationCard(
                    notification = ntf,
                    asset = ntf.assetId?.let { assetMap[it] },
                    canManage = canManage,
                    onSetStatus = onSetStatus,
                    onCreateOrder = onCreateOrder,
                    onEdit = { editing = ntf; showForm = true },
                    onDelete = { deleteTarget = ntf }
                )
            }
        }
    }

    if (showForm) {
        NotificationFormSheet(
            initial = editing,
            assets = assets,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف البلاغ",
            text = "هل تريد حذف \"${target.title}\"؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
internal fun NotificationCard(
    notification: MaintenanceNotificationEntity,
    asset: AssetEntity?,
    canManage: Boolean,
    onSetStatus: (MaintenanceNotificationEntity, String) -> Unit,
    onCreateOrder: (MaintenanceNotificationEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(notification.number, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                    Text(notification.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                }
                StatusBadge(notificationStatusLabel(notification.status), notificationStatusTone(notification.status))
            }
            Text(notification.description, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(notification.type, statusTone("info"))
                StatusBadge(notification.priority, priorityTone(notification.priority))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            if (asset != null) InfoRow("الأصل", "${asset.code} • ${asset.name}")
            if (asset?.isLinearAsset == true && notification.hasLinearReference()) {
                InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, notification.linearStartPoint, notification.linearEndPoint, notification.linearMarker, notification.linearHorizontalOffset, notification.linearVerticalOffset))
            }
            if (notification.damageCode.isNotBlank()) InfoRow("كود الضرر", notification.damageCode)
            if (notification.causeCode.isNotBlank()) InfoRow("كود السبب", notification.causeCode)
            InfoRow("المُبلِّغ", notification.reportedBy)
            if (notification.requiredEnd.isNotBlank()) InfoRow("مطلوب الإنجاز قبل", notification.requiredEnd)
            if (notification.linkedOrderId != null) {
                StatusBadge("أمر عمل #${notification.linkedOrderId}", statusTone("running"))
            }
            if (canManage && notification.status != "Closed" && notification.status != "OrderCreated") {
                when (notification.status) {
                    "New" -> {
                        OutlinedButton(onClick = { onSetStatus(notification, "Screened") }, modifier = Modifier.fillMaxWidth()) { Text("مراجعة البلاغ") }
                    }
                    "Screened" -> {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { onSetStatus(notification, "Approved") }, modifier = Modifier.weight(1f)) { Text("اعتماد") }
                            OutlinedButton(onClick = { onSetStatus(notification, "Rejected") }, modifier = Modifier.weight(1f)) { Text("رفض") }
                        }
                    }
                    "Approved" -> {
                        Button(onClick = { onCreateOrder(notification) }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Assignment, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("إنشاء أمر عمل")
                        }
                    }
                    "Rejected" -> {
                        OutlinedButton(onClick = { onSetStatus(notification, "Closed") }, modifier = Modifier.fillMaxWidth()) { Text("إغلاق البلاغ") }
                    }
                }
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}


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
// Dashboard
// ---------------------------------------------------------------------------

@Composable
internal fun DashboardScreen(
    innerPadding: PaddingValues,
    stats: DashboardStats,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    parts: List<SparePartEntity>,
    pmItems: List<PreventiveMaintenanceEntity>,
    notifications: List<MaintenanceNotificationEntity>,
    onReports: () -> Unit,
    onGovernance: () -> Unit,
    onOpenTab: (BottomTab) -> Unit,
    onOpenMore: (MoreRoute) -> Unit
) {
    val today = DateStrings.today()
    val soon = DateStrings.daysFromToday(30)
    val criticalAssets = assets.count { it.status != "Running" }
    val overdue = workOrders.count { it.status != "Closed" && it.dueAt < today }
    val inProgress = workOrders.count { it.status == "In Progress" }
    val urgent = workOrders.count { it.priority == "Critical" || it.priority == "High" }
    val assigned = workOrders.count { it.assignedTo.isNotBlank() }
    val governance = if (workOrders.isEmpty()) 100 else (assigned * 100 / workOrders.size)
    val pendingApprovals = workOrders.filter { it.approvalStatus == "Pending" }
    val expiringWarranty = assets.filter { it.warrantyEnd.isNotBlank() && it.warrantyEnd in today..soon }
    val totalCost = workOrders.sumOf { it.totalCost() }
    val failures = workOrders.filter { it.isFailure }
    val downtime = failures.sumOf { it.downtimeHours }
    val windowHours = (assets.size.coerceAtLeast(1)) * 30.0 * 24.0
    val availability = ((windowHours - downtime) / windowHours * 100.0).coerceIn(0.0, 100.0)
    val woOpen = workOrders.count { it.status == "Open" }
    val woProgress = workOrders.count { it.status == "In Progress" }
    val woTech = workOrders.count { it.status == "Technically Completed" }
    val woClosed = workOrders.count { it.status == "Closed" }
    val statusSegments = listOf(
        ChartSegment("مفتوح", woOpen, AccentBlue),
        ChartSegment("قيد التنفيذ", woProgress, AccentOrange),
        ChartSegment("مكتمل فنياً", woTech, AccentTeal),
        ChartSegment("مغلق", woClosed, AccentGreen)
    )
    val laborCost = workOrders.sumOf { it.laborCost() }
    val partsCostTotal = workOrders.sumOf { it.partsCost }
    val maxCost = listOf(laborCost, partsCostTotal, 1.0).max()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { DotSectionTitle("واجهة الإدارة السريعة", AccentOrange) }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                KpiTile("أصول حرجة", criticalAssets.toString(), AccentRed, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.Assets) })
                KpiTile("CAPA", stats.capa.toString(), AccentOrange, Modifier.weight(1f), onClick = { onOpenMore(MoreRoute.Capa) })
                KpiTile("متأخرة", overdue.toString(), MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
                KpiTile("أوامر مفتوحة", stats.openWorkOrders.toString(), AccentBlue, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                BigActionButton("التقارير", AccentBlue, Modifier.weight(1f), onReports)
                BigActionButton("حوكمة $governance%", AccentNavy, Modifier.weight(1f), onGovernance)
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    SectionHeader("توزيع أوامر العمل")
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        DonutChart(
                            segments = statusSegments,
                            centerValue = workOrders.size.toString(),
                            centerLabel = "أمر عمل"
                        )
                        ChartLegend(statusSegments, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RingGauge(percent = availability.toFloat(), color = AccentGreen, centerLabel = "التوفّر")
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader("الأداء والتكاليف")
                        BarMeter("تكلفة العمالة", (laborCost / maxCost).toFloat(), AccentBlue, money(laborCost))
                        BarMeter("تكلفة قطع الغيار", (partsCostTotal / maxCost).toFloat(), AccentPurple, money(partsCostTotal))
                        InfoRow("زمن التوقف", "${"%.0f".format(downtime)} ساعة")
                    }
                }
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                KpiTile("مفتوح", stats.openWorkOrders.toString(), AccentBlue, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
                KpiTile("قيد التنفيذ", inProgress.toString(), AccentOrange, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
                KpiTile("متأخر", overdue.toString(), AccentRed, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
                KpiTile("طارئ", urgent.toString(), AccentPurple, Modifier.weight(1f), onClick = { onOpenTab(BottomTab.WorkOrders) })
            }
        }

        item { DotSectionTitle("يحتاج انتباهك", AccentRed) }

        val warningAssets = assets.filter { it.status != "Running" }.take(4)
        val lowStockParts = parts.filter { it.onHandQty <= it.minQty }.take(4)
        val duePm = pmItems.filter { DateStrings.isDueOrOverdue(it.nextDueAt) }.take(4)
        val openNotifications = notifications.filter {
            (it.status == "New" || it.status == "Screened") && (it.priority == "Critical" || it.priority == "High")
        }.take(4)

        if (warningAssets.isEmpty() && lowStockParts.isEmpty() && duePm.isEmpty() &&
            pendingApprovals.isEmpty() && expiringWarranty.isEmpty() && openNotifications.isEmpty()
        ) {
            item { CalmCard() }
        } else {
            items(openNotifications, key = { "nt-${it.id}" }) { ntf ->
                AlertRow(Icons.Filled.NotificationsActive, AccentRed, "${ntf.number} • ${ntf.title}", "بلاغ ${notificationStatusLabel(ntf.status)} • ${ntf.priority}", onClick = { onOpenMore(MoreRoute.Notifications) })
            }
            items(pendingApprovals.take(4), key = { "ap-${it.id}" }) { wo ->
                AlertRow(Icons.Filled.FactCheck, AccentPurple, wo.title, "بانتظار اعتماد المشرف", onClick = { onOpenTab(BottomTab.WorkOrders) })
            }
            items(warningAssets, key = { "a-${it.id}" }) { asset ->
                AlertRow(Icons.Filled.Warning, statusTone(asset.status).content, "${asset.code} • ${asset.name}", "الحالة: ${asset.status} • ${asset.location}", onClick = { onOpenTab(BottomTab.Assets) })
            }
            items(lowStockParts, key = { "p-${it.id}" }) { part ->
                AlertRow(Icons.Filled.Inventory2, AccentRed, "${part.partNumber} • ${part.name}", "المتوفر ${part.onHandQty} • الحد الأدنى ${part.minQty}", onClick = { onOpenMore(MoreRoute.Inventory) })
            }
            items(duePm, key = { "m-${it.id}" }) { pm ->
                AlertRow(Icons.Filled.EventRepeat, AccentOrange, pm.title, "مستحقة بتاريخ ${pm.nextDueAt}", onClick = { onOpenTab(BottomTab.Supervision) })
            }
            items(expiringWarranty.take(4), key = { "w-${it.id}" }) { asset ->
                AlertRow(Icons.Filled.Verified, AccentTeal, "${asset.code} • ${asset.name}", "ينتهي الضمان بتاريخ ${asset.warrantyEnd}", onClick = { onOpenTab(BottomTab.Assets) })
            }
        }
    }
}

@Composable
internal fun MetricColumn(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
        Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun DotSectionTitle(text: String, dot: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.size(8.dp).background(dot, CircleShape))
    }
}

@Composable
internal fun KpiTile(label: String, value: String, color: Color, modifier: Modifier = Modifier, onClick: (() -> Unit)? = null) {
    ElevatedCard(
        modifier = modifier.height(92.dp).then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
internal fun BigActionButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = color)
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
internal fun CalmCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconBubble(Icons.Filled.CheckCircle, StatusRunning, StatusRunningContainer, 40)
            Column {
                Text("لا شيء عاجل", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text("كل شيء تحت السيطرة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
internal fun AlertRow(icon: ImageVector, tint: Color, title: String, body: String, onClick: (() -> Unit)? = null) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth().then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(icon, tint, tint.copy(alpha = 0.14f), 40)
            Column(modifier = Modifier.weight(1f)) {
                LtrText(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (onClick != null) {
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}


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
// Reports
// ---------------------------------------------------------------------------

@Composable
internal fun ReportsScreen(
    innerPadding: PaddingValues,
    stats: DashboardStats,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    parts: List<SparePartEntity>,
    pmItems: List<PreventiveMaintenanceEntity>
) {
    val today = DateStrings.today()
    val soon = DateStrings.daysFromToday(30)
    val openCost = workOrders.filter { it.status != "Closed" }.sumOf { it.estimatedCost }
    val closed = workOrders.count { it.status == "Closed" }
    val lowStock = parts.filter { it.onHandQty <= it.minQty }
    val duePm = pmItems.filter { DateStrings.isDueOrOverdue(it.nextDueAt) }
    val underWarranty = assets.filter { it.isUnderWarranty(today) }
    val expiringSoon = assets.filter { it.warrantyEnd.isNotBlank() && it.warrantyEnd in today..soon }
    val totalCost = workOrders.sumOf { it.totalCost() }
    val laborCost = workOrders.sumOf { it.laborCost() }
    val partsCost = workOrders.sumOf { it.partsCost }
    val assetName = assets.associate { it.id to it.code }
    val topCostAssets = workOrders.groupBy { it.assetId }
        .mapValues { (_, list) -> list.sumOf { it.totalCost() } }
        .entries.sortedByDescending { it.value }
        .take(3)
    val failures = workOrders.filter { it.isFailure }
    val totalDowntime = failures.sumOf { it.downtimeHours }
    val mttr = if (failures.isNotEmpty()) totalDowntime / failures.size else 0.0
    val openWos = workOrders.filter { it.status != "Closed" }
    val overdueWos = openWos.count { DateStrings.isDueOrOverdue(it.dueAt) }
    val pendingApprovals = workOrders.count { it.approvalStatus == "Pending" }
    val windowHours = (assets.size.coerceAtLeast(1)) * 30.0 * 24.0
    val availability = ((windowHours - totalDowntime) / windowHours * 100.0).coerceIn(0.0, 100.0)
    val context = LocalContext.current
    val reportText = buildReportText(
        assets = stats.assets, openWo = stats.openWorkOrders, closed = closed,
        totalCost = totalCost, laborCost = laborCost, partsCost = partsCost, openCost = openCost,
        availability = availability, failures = failures.size, downtime = totalDowntime, mttr = mttr,
        overdue = overdueWos, pendingApprovals = pendingApprovals, duePm = duePm.size,
        lowStock = lowStock.size, underWarranty = underWarranty.size, expiringSoon = expiringSoon.size
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    SectionHeader("التقارير والتحليلات")
                    Text("ملخصات حيّة للتكاليف والتوفر والأعطال.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                OutlinedButton(onClick = { shareText(context, "تقرير الصيانة — الهادي CMMS", reportText) }) {
                    Icon(Icons.Filled.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("مشاركة")
                }
            }
        }
        item {
            val seg = listOf(
                ChartSegment("مفتوح", workOrders.count { it.status == "Open" }, AccentBlue),
                ChartSegment("قيد التنفيذ", workOrders.count { it.status == "In Progress" }, AccentOrange),
                ChartSegment("مكتمل فنياً", workOrders.count { it.status == "Technically Completed" }, AccentTeal),
                ChartSegment("مغلق", closed, AccentGreen)
            )
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    DonutChart(segments = seg, centerValue = workOrders.size.toString(), centerLabel = "أمر عمل")
                    ChartLegend(seg, modifier = Modifier.weight(1f))
                }
            }
        }
        item {
            val maxC = listOf(laborCost, partsCost, openCost, 1.0).max()
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Row(modifier = Modifier.padding(16.dp).fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    RingGauge(percent = availability.toFloat(), color = AccentGreen, centerLabel = "التوفّر")
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader("التكاليف")
                        BarMeter("عمالة", (laborCost / maxC).toFloat(), AccentBlue, money(laborCost))
                        BarMeter("قطع غيار", (partsCost / maxC).toFloat(), AccentPurple, money(partsCost))
                        BarMeter("تقديرية مفتوحة", (openCost / maxC).toFloat(), AccentOrange, money(openCost))
                    }
                }
            }
        }
        item {
            ReportCard("التوفّر والأعطال", listOf(
                "نسبة التوفّر (30 يوم): ${"%.1f".format(availability)}%",
                "عدد الأعطال: ${failures.size}",
                "إجمالي زمن التوقف: ${"%.1f".format(totalDowntime)} ساعة",
                "متوسط زمن الإصلاح MTTR: ${"%.1f".format(mttr)} ساعة"
            ))
        }
        item {
            ReportCard("حالة أوامر العمل", listOf(
                "مفتوحة حالياً: ${openWos.size}",
                "متأخرة عن الاستحقاق: $overdueWos",
                "بانتظار الاعتماد: $pendingApprovals",
                "مغلقة: $closed"
            ))
        }
        item {
            ReportCard("ملخص الصيانة", listOf(
                "إجمالي الأصول: ${stats.assets}",
                "أوامر العمل المفتوحة: ${stats.openWorkOrders}",
                "أوامر العمل المغلقة: $closed",
                "تكلفة تقديرية مفتوحة: ${"%.2f".format(openCost)}"
            ))
        }
        item {
            ReportCard("التكاليف", listOf(
                "إجمالي تكلفة الصيانة: ${money(totalCost)}",
                "تكلفة العمالة: ${money(laborCost)}",
                "تكلفة قطع الغيار: ${money(partsCost)}",
                "تكلفة تقديرية مفتوحة: ${money(openCost)}"
            ))
        }
        if (topCostAssets.isNotEmpty()) {
            item {
                val maxCost = (topCostAssets.maxOfOrNull { it.value } ?: 1.0).coerceAtLeast(1.0)
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        SectionHeader("الأصول الأعلى تكلفة")
                        topCostAssets.forEach {
                            BarMeter(assetName[it.key] ?: "#${it.key}", (it.value / maxCost).toFloat(), AccentRed, money(it.value))
                        }
                    }
                }
            }
        }
        item {
            ReportCard("الصيانة الدورية", listOf(
                "مهام PM المستحقة: ${duePm.size}",
                "أقرب مهمة: ${duePm.firstOrNull()?.title ?: "لا يوجد"}"
            ))
        }
        item {
            ReportCard("المخزون", listOf(
                "قطع تحت الحد الأدنى: ${lowStock.size}",
                "أول قطعة ناقصة: ${lowStock.firstOrNull()?.partNumber ?: "لا يوجد"}"
            ))
        }
        item {
            ReportCard("الضمان", listOf(
                "أصول ضمن الضمان: ${underWarranty.size}",
                "ضمانات تنتهي خلال 30 يوم: ${expiringSoon.size}",
                "الأقرب انتهاءً: ${expiringSoon.minByOrNull { it.warrantyEnd }?.let { "${it.code} (${it.warrantyEnd})" } ?: "لا يوجد"}"
            ))
        }
    }
}

@Composable
internal fun ReportCard(title: String, lines: List<String>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            SectionHeader(title)
            lines.forEach { line -> Text("• $line", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}


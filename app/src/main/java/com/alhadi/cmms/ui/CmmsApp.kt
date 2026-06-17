package com.alhadi.cmms.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
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
import com.alhadi.cmms.viewmodel.CmmsViewModel
import com.alhadi.cmms.viewmodel.DashboardStats
import java.util.Locale
import kotlinx.coroutines.launch

// ---------------------------------------------------------------------------
// Navigation model
// ---------------------------------------------------------------------------

private enum class BottomTab(val label: String, val icon: ImageVector, val accent: Color) {
    Home("الرئيسية", Icons.Filled.Home, AccentNavy),
    WorkOrders("أوامر العمل", Icons.Filled.Assignment, AccentBlue),
    Supervision("الإشراف", Icons.Filled.Verified, AccentTeal),
    Assets("الأصول", Icons.Filled.PrecisionManufacturing, AccentGreen),
    More("المزيد", Icons.Filled.GridView, AccentBrown)
}

private enum class MoreRoute { Inventory, Reports, Audit, Admin, PreventiveMaintenance }

private data class ScreenMeta(
    val title: String,
    val subtitle: String,
    val icon: ImageVector,
    val accent: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CmmsApp(viewModel: CmmsViewModel) {
    val stats by viewModel.dashboardStats.collectAsStateWithLifecycle()
    val assets by viewModel.assets.collectAsStateWithLifecycle()
    val workOrders by viewModel.workOrders.collectAsStateWithLifecycle()
    val preventiveMaintenance by viewModel.preventiveMaintenance.collectAsStateWithLifecycle()
    val spareParts by viewModel.spareParts.collectAsStateWithLifecycle()
    val transactions by viewModel.transactions.collectAsStateWithLifecycle()
    val users by viewModel.users.collectAsStateWithLifecycle()
    val auditLog by viewModel.auditLog.collectAsStateWithLifecycle()
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    var selectedTab by rememberSaveable { mutableStateOf(BottomTab.Home) }
    var moreRoute by rememberSaveable { mutableStateOf<MoreRoute?>(null) }
    val actorName = currentUser?.name ?: "Unassigned"

    val isAdmin = currentUser?.isAdmin == true
    val canManage = currentUser?.canManage == true

    BackHandler(enabled = selectedTab == BottomTab.More && moreRoute != null) {
        moreRoute = null
    }

    LaunchedEffect(message) {
        val text = message
        if (!text.isNullOrBlank()) {
            snackbarHostState.showSnackbar(text)
            viewModel.clearMessage()
        }
    }

    val meta = screenMeta(selectedTab, moreRoute)

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                AppHeader(
                    meta = meta,
                    user = currentUser,
                    openWorkOrders = stats.openWorkOrders,
                    assetsCount = stats.assets,
                    showBack = selectedTab == BottomTab.More && moreRoute != null,
                    onBack = { moreRoute = null },
                    onLogout = viewModel::logout,
                    onSchedule = { selectedTab = BottomTab.Supervision },
                    onMaintenance = { selectedTab = BottomTab.Supervision },
                    onAlerts = { selectedTab = BottomTab.Home }
                )
            },
            bottomBar = {
                AppBottomBar(
                    selected = selectedTab,
                    isAdmin = isAdmin,
                    onSelect = {
                        selectedTab = it
                        if (it != BottomTab.More) moreRoute = null
                    }
                )
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { innerPadding ->
            val assetMap = assets.associateBy { it.id }
            AnimatedContent(
                targetState = meta.title,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen"
            ) {
                when (selectedTab) {
                    BottomTab.Home -> DashboardScreen(
                        innerPadding = innerPadding,
                        stats = stats,
                        assets = assets,
                        workOrders = workOrders,
                        parts = spareParts,
                        pmItems = preventiveMaintenance,
                        onReports = { selectedTab = BottomTab.More; moreRoute = MoreRoute.Reports },
                        onGovernance = { selectedTab = BottomTab.More; moreRoute = MoreRoute.Audit }
                    )

                    BottomTab.WorkOrders -> WorkOrdersScreen(
                        innerPadding = innerPadding,
                        workOrders = workOrders,
                        assets = assets,
                        assetMap = assetMap,
                        canManage = canManage,
                        defaultAssignee = actorName,
                        onSave = viewModel::saveWorkOrder,
                        onDelete = viewModel::deleteWorkOrder,
                        onUpdateStatus = viewModel::updateWorkOrderStatus
                    )

                    BottomTab.Supervision -> PreventiveMaintenanceScreen(
                        innerPadding = innerPadding,
                        pmItems = preventiveMaintenance,
                        assets = assets,
                        assetMap = assetMap,
                        canManage = canManage,
                        onSave = viewModel::savePreventiveMaintenance,
                        onDelete = viewModel::deletePreventiveMaintenance,
                        onDone = viewModel::markPreventiveMaintenanceDone
                    )

                    BottomTab.Assets -> AssetsScreen(
                        innerPadding = innerPadding,
                        assets = assets,
                        workOrders = workOrders,
                        pmItems = preventiveMaintenance,
                        canManage = canManage,
                        defaultAssignee = actorName,
                        onSave = viewModel::saveAsset,
                        onDelete = viewModel::deleteAsset,
                        onChangeStatus = viewModel::changeAssetStatus,
                        onSaveWorkOrder = viewModel::saveWorkOrder,
                        onUpdateWorkOrderStatus = viewModel::updateWorkOrderStatus
                    )

                    BottomTab.More -> when (moreRoute) {
                        null -> MoreGrid(
                            innerPadding = innerPadding,
                            isAdmin = isAdmin,
                            onOpen = { moreRoute = it },
                            onLogout = viewModel::logout
                        )
                        MoreRoute.Inventory -> InventoryScreen(
                            innerPadding = innerPadding,
                            parts = spareParts,
                            transactions = transactions,
                            canReceive = canManage,
                            canManage = canManage,
                            onIssue = viewModel::issuePart,
                            onReceive = viewModel::receivePart,
                            onSave = viewModel::savePart,
                            onDelete = viewModel::deletePart
                        )
                        MoreRoute.Reports -> ReportsScreen(
                            innerPadding = innerPadding,
                            stats = stats,
                            workOrders = workOrders,
                            parts = spareParts,
                            pmItems = preventiveMaintenance
                        )
                        MoreRoute.Audit -> AuditScreen(innerPadding = innerPadding, auditLog = auditLog)
                        MoreRoute.Admin -> AdminScreen(
                            innerPadding = innerPadding,
                            users = users,
                            currentUser = currentUser,
                            onAddTechnician = viewModel::addTechnician,
                            onResetSampleData = viewModel::resetSampleData,
                            onSave = viewModel::saveUser,
                            onSetActive = viewModel::setUserActive,
                            onDelete = viewModel::deleteUser
                        )
                        MoreRoute.PreventiveMaintenance -> PreventiveMaintenanceScreen(
                            innerPadding = innerPadding,
                            pmItems = preventiveMaintenance,
                            assets = assets,
                            assetMap = assetMap,
                            canManage = canManage,
                            onSave = viewModel::savePreventiveMaintenance,
                            onDelete = viewModel::deletePreventiveMaintenance,
                            onDone = viewModel::markPreventiveMaintenanceDone
                        )
                    }
                }
            }
        }

    }
}

private fun screenMeta(tab: BottomTab, route: MoreRoute?): ScreenMeta = when (tab) {
    BottomTab.Home -> ScreenMeta("الرئيسية", "لوحة المؤشرات والتنبيهات", Icons.Filled.Home, AccentNavy)
    BottomTab.WorkOrders -> ScreenMeta("أوامر العمل", "إنشاء، إصدار، متابعة، وإغلاق", Icons.Filled.Assignment, AccentBlue)
    BottomTab.Supervision -> ScreenMeta("الإشراف والصيانة", "الصيانة الدورية والمتابعة", Icons.Filled.Verified, AccentTeal)
    BottomTab.Assets -> ScreenMeta("الأصول", "سجل الأصول والمعدات", Icons.Filled.PrecisionManufacturing, AccentGreen)
    BottomTab.More -> when (route) {
        null -> ScreenMeta("المزيد", "كل الوحدات والإعدادات", Icons.Filled.GridView, AccentBrown)
        MoreRoute.Inventory -> ScreenMeta("المخزون", "قطع الغيار والحركات", Icons.Filled.Inventory2, AccentPurple)
        MoreRoute.Reports -> ScreenMeta("التقارير", "مؤشرات وتصدير وتحليلات", Icons.Filled.Analytics, AccentBlue)
        MoreRoute.Audit -> ScreenMeta("سجل الحوكمة", "من فعل ماذا ومتى", Icons.Filled.History, AccentRed)
        MoreRoute.Admin -> ScreenMeta("الإدارة", "المستخدمون والصلاحيات", Icons.Filled.AdminPanelSettings, AccentOrange)
        MoreRoute.PreventiveMaintenance -> ScreenMeta("الصيانة الدورية", "جدول المهام الوقائية", Icons.Filled.EventRepeat, AccentTeal)
    }
}

// ---------------------------------------------------------------------------
// Header
// ---------------------------------------------------------------------------

@Composable
private fun AppHeader(
    meta: ScreenMeta,
    user: UserEntity?,
    openWorkOrders: Int,
    assetsCount: Int,
    showBack: Boolean,
    onBack: () -> Unit,
    onLogout: () -> Unit,
    onSchedule: () -> Unit,
    onMaintenance: () -> Unit,
    onAlerts: () -> Unit
) {
    Surface(
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 3.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Title row: section icon (right) + title block + production badge (left).
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(meta.accent, RoundedCornerShape(14.dp))
                        .clickable(enabled = showBack, onClick = onBack),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (showBack) Icons.AutoMirrored.Filled.ArrowBack else meta.icon,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        "Alhadi CMMS",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.SemiBold
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            meta.title,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .background(meta.accent, CircleShape)
                        )
                    }
                    Text(
                        meta.subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Column(horizontalAlignment = Alignment.End) {
                    ProductionBadge()
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        user?.role ?: "محلي",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Status chips row.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SyncCard()
                StatCard("أمر مفتوح", openWorkOrders.toString(), StatusInfo)
                StatCard("أصل", assetsCount.toString(), StatusRunning)
            }

            // Quick action pills row.
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickPill("محرك الجدولة", Icons.Filled.Schedule, onSchedule)
                QuickPill("إدارة الصيانة", Icons.Filled.Build, onMaintenance)
                QuickPill("التنبيهات", Icons.Filled.NotificationsActive, onAlerts)
                QuickPill("تسجيل الخروج", Icons.AutoMirrored.Filled.Logout, onLogout)
            }
        }
    }
}

@Composable
private fun ProductionBadge() {
    Surface(shape = CircleShape, color = StatusRunningContainer) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Box(modifier = Modifier.size(7.dp).background(StatusRunning, CircleShape))
            Text(
                "Production",
                style = MaterialTheme.typography.labelMedium,
                color = StatusRunning,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun SyncCard() {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = StatusRunningContainer
    ) {
        Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)) {
            Text(
                "حالة المزامنة",
                style = MaterialTheme.typography.labelMedium,
                color = StatusRunning,
                fontWeight = FontWeight.Bold
            )
            Text(
                "محلي • جاهز للعمل بدون إنترنت",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun StatCard(label: String, value: String, color: Color) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = color)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun QuickPill(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(
        shape = CircleShape,
        color = MaterialTheme.colorScheme.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Medium)
        }
    }
}

// ---------------------------------------------------------------------------
// Bottom navigation (custom, filled-square selected icon)
// ---------------------------------------------------------------------------

@Composable
private fun AppBottomBar(selected: BottomTab, isAdmin: Boolean, onSelect: (BottomTab) -> Unit) {
    Surface(color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            BottomTab.entries.forEach { tab ->
                val isSel = tab == selected
                Column(
                    modifier = Modifier
                        .clickable { onSelect(tab) }
                        .padding(horizontal = 4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(
                                if (isSel) tab.accent else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            tab.icon,
                            contentDescription = tab.label,
                            tint = if (isSel) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Text(
                        tab.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isSel) tab.accent else MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Dashboard
// ---------------------------------------------------------------------------

@Composable
private fun DashboardScreen(
    innerPadding: PaddingValues,
    stats: DashboardStats,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    parts: List<SparePartEntity>,
    pmItems: List<PreventiveMaintenanceEntity>,
    onReports: () -> Unit,
    onGovernance: () -> Unit
) {
    val today = DateStrings.today()
    val criticalAssets = assets.count { it.status != "Running" }
    val overdue = workOrders.count { it.status != "Closed" && it.dueAt < today }
    val inProgress = workOrders.count { it.status == "In Progress" }
    val urgent = workOrders.count { it.priority == "Critical" || it.priority == "High" }
    val assigned = workOrders.count { it.assignedTo.isNotBlank() }
    val governance = if (workOrders.isEmpty()) 100 else (assigned * 100 / workOrders.size)

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
                KpiTile("أصول حرجة", criticalAssets.toString(), AccentRed, Modifier.weight(1f))
                KpiTile("CAPA", "0", AccentOrange, Modifier.weight(1f))
                KpiTile("متأخرة", overdue.toString(), MaterialTheme.colorScheme.onSurfaceVariant, Modifier.weight(1f))
                KpiTile("أوامر مفتوحة", stats.openWorkOrders.toString(), AccentBlue, Modifier.weight(1f))
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                BigActionButton("التقارير", AccentBlue, Modifier.weight(1f), onReports)
                BigActionButton("حوكمة $governance%", AccentNavy, Modifier.weight(1f), onGovernance)
            }
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                KpiTile("مفتوح", stats.openWorkOrders.toString(), AccentBlue, Modifier.weight(1f))
                KpiTile("قيد التنفيذ", inProgress.toString(), AccentOrange, Modifier.weight(1f))
                KpiTile("متأخر", overdue.toString(), AccentRed, Modifier.weight(1f))
                KpiTile("طارئ", urgent.toString(), AccentPurple, Modifier.weight(1f))
            }
        }

        item { DotSectionTitle("يحتاج انتباهك", AccentRed) }

        val warningAssets = assets.filter { it.status != "Running" }.take(4)
        val lowStockParts = parts.filter { it.onHandQty <= it.minQty }.take(4)
        val duePm = pmItems.filter { DateStrings.isDueOrOverdue(it.nextDueAt) }.take(4)

        if (warningAssets.isEmpty() && lowStockParts.isEmpty() && duePm.isEmpty()) {
            item { CalmCard() }
        } else {
            items(warningAssets, key = { "a-${it.id}" }) { asset ->
                AlertRow(Icons.Filled.Warning, statusTone(asset.status).content, "${asset.code} • ${asset.name}", "الحالة: ${asset.status} • ${asset.location}")
            }
            items(lowStockParts, key = { "p-${it.id}" }) { part ->
                AlertRow(Icons.Filled.Inventory2, AccentRed, "${part.partNumber} • ${part.name}", "المتوفر ${part.onHandQty} • الحد الأدنى ${part.minQty}")
            }
            items(duePm, key = { "m-${it.id}" }) { pm ->
                AlertRow(Icons.Filled.EventRepeat, AccentOrange, pm.title, "مستحقة بتاريخ ${pm.nextDueAt}")
            }
        }
    }
}

@Composable
private fun DotSectionTitle(text: String, dot: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Box(modifier = Modifier.size(8.dp).background(dot, CircleShape))
    }
}

@Composable
private fun KpiTile(label: String, value: String, color: Color, modifier: Modifier = Modifier) {
    ElevatedCard(
        modifier = modifier.height(92.dp),
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
private fun BigActionButton(label: String, color: Color, modifier: Modifier = Modifier, onClick: () -> Unit) {
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
private fun CalmCard() {
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
private fun AlertRow(icon: ImageVector, tint: Color, title: String, body: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
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
        }
    }
}

// ---------------------------------------------------------------------------
// "More" grid
// ---------------------------------------------------------------------------

@Composable
private fun MoreGrid(
    innerPadding: PaddingValues,
    isAdmin: Boolean,
    onOpen: (MoreRoute) -> Unit,
    onLogout: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("المخزون", "قطع الغيار والحركات", Icons.Filled.Inventory2, AccentPurple, Modifier.weight(1f)) { onOpen(MoreRoute.Inventory) }
                ModuleCard("التقارير", "مؤشرات وتحليلات", Icons.Filled.Analytics, AccentBlue, Modifier.weight(1f)) { onOpen(MoreRoute.Reports) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("الصيانة الدورية", "جدول المهام الوقائية", Icons.Filled.EventRepeat, AccentTeal, Modifier.weight(1f)) { onOpen(MoreRoute.PreventiveMaintenance) }
                ModuleCard("سجل الحوكمة", "من فعل ماذا ومتى", Icons.Filled.History, AccentRed, Modifier.weight(1f)) { onOpen(MoreRoute.Audit) }
            }
        }
        if (isAdmin) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    ModuleCard("الإدارة", "المستخدمون والصلاحيات", Icons.Filled.AdminPanelSettings, AccentOrange, Modifier.weight(1f)) { onOpen(MoreRoute.Admin) }
                    ModuleCard("تسجيل الخروج", "إنهاء الجلسة الحالية", Icons.AutoMirrored.Filled.Logout, AccentNavy, Modifier.weight(1f)) { onLogout() }
                }
            }
        } else {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    ModuleCard("تسجيل الخروج", "إنهاء الجلسة الحالية", Icons.AutoMirrored.Filled.Logout, AccentNavy, Modifier.weight(1f)) { onLogout() }
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun ModuleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accent: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .height(132.dp)
            .clickable(onClick = onClick),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .width(5.dp)
                    .fillMaxHeight()
                    .background(accent)
            )
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    IconBubble(icon, accent, accent.copy(alpha = 0.14f), 40)
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Assets
// ---------------------------------------------------------------------------

@Composable
private fun AssetsScreen(
    innerPadding: PaddingValues,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    pmItems: List<PreventiveMaintenanceEntity>,
    canManage: Boolean,
    defaultAssignee: String,
    onSave: (AssetEntity) -> Unit,
    onDelete: (AssetEntity) -> Unit,
    onChangeStatus: (AssetEntity, String) -> Unit,
    onSaveWorkOrder: (WorkOrderEntity) -> Unit,
    onUpdateWorkOrderStatus: (WorkOrderEntity, String) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<AssetEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<AssetEntity?>(null) }
    var detailId by remember { mutableStateOf<Long?>(null) }

    // Detail view (re-resolved from the live list so edits/status changes reflect).
    val detailAsset = detailId?.let { id -> assets.firstOrNull { it.id == id } }
    if (detailAsset != null) {
        BackHandler { detailId = null }
        AssetDetailScreen(
            innerPadding = innerPadding,
            asset = detailAsset,
            workOrders = workOrders.filter { it.assetId == detailAsset.id },
            pmItems = pmItems.filter { it.assetId == detailAsset.id },
            canManage = canManage,
            defaultAssignee = defaultAssignee,
            onBack = { detailId = null },
            onSaveAsset = onSave,
            onChangeStatus = onChangeStatus,
            onSaveWorkOrder = onSaveWorkOrder,
            onUpdateWorkOrderStatus = onUpdateWorkOrderStatus
        )
        return
    }

    val filtered = remember(query, assets) {
        if (query.isBlank()) assets else assets.filter { asset ->
            val q = query.lowercase(Locale.getDefault())
            asset.code.lowercase(Locale.getDefault()).contains(q) ||
                asset.name.lowercase(Locale.getDefault()).contains(q) ||
                asset.groupName.lowercase(Locale.getDefault()).contains(q) ||
                asset.location.lowercase(Locale.getDefault()).contains(q)
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
            item { SearchField(query = query, onChange = { query = it }, placeholder = "بحث: RM-01 أو Rollermill") }
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
        AssetFormSheet(initial = editing, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
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
private fun AssetCard(
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
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(asset.code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LtrText(asset.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(asset.status, statusTone(asset.status))
                Icon(Icons.AutoMirrored.Filled.KeyboardArrowLeft, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            InfoRow("المجموعة", asset.groupName)
            InfoRow("الموقع", asset.location)
            InfoRow("الأهمية", asset.criticality)
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

@Composable
private fun AssetDetailScreen(
    innerPadding: PaddingValues,
    asset: AssetEntity,
    workOrders: List<WorkOrderEntity>,
    pmItems: List<PreventiveMaintenanceEntity>,
    canManage: Boolean,
    defaultAssignee: String,
    onBack: () -> Unit,
    onSaveAsset: (AssetEntity) -> Unit,
    onChangeStatus: (AssetEntity, String) -> Unit,
    onSaveWorkOrder: (WorkOrderEntity) -> Unit,
    onUpdateWorkOrderStatus: (WorkOrderEntity, String) -> Unit
) {
    var showEdit by remember { mutableStateOf(false) }
    var showStatus by remember { mutableStateOf(false) }
    var showWoForm by remember { mutableStateOf(false) }
    val lifecycle = listOf("Running", "Warning", "Stopped", "Under Maintenance", "Standby", "Retired")
    val retired = asset.status.equals("Retired", ignoreCase = true)

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
                    SectionHeader("المعلومات")
                    InfoRow("المجموعة", asset.groupName)
                    InfoRow("الموقع", asset.location)
                    InfoRow("الشركة/الموديل", "${asset.manufacturer} • ${asset.model}")
                    InfoRow("الأهمية", asset.criticality)
                    InfoRow("تاريخ التركيب", asset.installedAt)
                    InfoRow("آخر فحص", asset.lastInspectionAt)
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
                if (retired) {
                    Text(
                        "الأصل متقاعد — لا يمكن إنشاء أوامر عمل جديدة عليه.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    AddButton("أمر عمل لهذا الأصل") { showWoForm = true }
                }
            }
        }

        item { SectionHeader("أوامر العمل المرتبطة (${workOrders.size})") }
        if (workOrders.isEmpty()) {
            item { EmptyState("لا توجد أوامر عمل لهذا الأصل") }
        }
        items(workOrders, key = { "wo-${it.id}" }) { wo ->
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Text(wo.title, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), style = MaterialTheme.typography.titleSmall)
                        StatusBadge(wo.status, statusTone(wo.status))
                    }
                    Text("الاستحقاق: ${wo.dueAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (canManage && wo.status != "Closed") {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            if (wo.status == "Open") {
                                OutlinedButton(onClick = { onUpdateWorkOrderStatus(wo, "In Progress") }, modifier = Modifier.weight(1f)) { Text("بدء") }
                            }
                            Button(onClick = { onUpdateWorkOrderStatus(wo, "Closed") }, modifier = Modifier.weight(1f)) { Text("إغلاق") }
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
    }

    if (showEdit) {
        AssetFormSheet(initial = asset, onDismiss = { showEdit = false }, onSave = { onSaveAsset(it); showEdit = false })
    }
    if (showStatus) {
        StatusPickerDialog(
            current = asset.status,
            options = lifecycle,
            onPick = { onChangeStatus(asset, it); showStatus = false },
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

// ---------------------------------------------------------------------------
// Work orders
// ---------------------------------------------------------------------------

@Composable
private fun WorkOrdersScreen(
    innerPadding: PaddingValues,
    workOrders: List<WorkOrderEntity>,
    assets: List<AssetEntity>,
    assetMap: Map<Long, AssetEntity>,
    canManage: Boolean,
    defaultAssignee: String,
    onSave: (WorkOrderEntity) -> Unit,
    onDelete: (WorkOrderEntity) -> Unit,
    onUpdateStatus: (WorkOrderEntity, String) -> Unit
) {
    val statusFilters = listOf("All", "Open", "In Progress", "Closed")
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    var query by rememberSaveable { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<WorkOrderEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<WorkOrderEntity?>(null) }
    val filtered = remember(selectedFilter, query, workOrders) {
        workOrders.filter { wo ->
            (selectedFilter == "All" || wo.status == selectedFilter) &&
                (query.isBlank() || wo.title.lowercase(Locale.getDefault()).contains(query.lowercase(Locale.getDefault())))
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SearchField(query = query, onChange = { query = it }, placeholder = "بحث في أوامر العمل…") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    statusFilters.forEach { filter ->
                        FilterChip(selected = selectedFilter == filter, onClick = { selectedFilter = filter }, label = { Text(filter) })
                    }
                }
            }
            if (canManage) {
                item { AddButton("أمر عمل جديد") { editing = null; showForm = true } }
            }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد أوامر عمل هنا.", Icons.Filled.Assignment) }
            }
            items(filtered, key = { it.id }) { workOrder ->
                WorkOrderCard(
                    workOrder = workOrder,
                    asset = assetMap[workOrder.assetId],
                    canManage = canManage,
                    onUpdateStatus = onUpdateStatus,
                    onEdit = { editing = workOrder; showForm = true },
                    onDelete = { deleteTarget = workOrder }
                )
            }
        }
    }

    if (showForm) {
        WorkOrderFormSheet(
            initial = editing,
            assets = assets,
            defaultAssignee = defaultAssignee,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف أمر العمل",
            text = "هل تريد حذف \"${target.title}\"؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun WorkOrderCard(
    workOrder: WorkOrderEntity,
    asset: AssetEntity?,
    canManage: Boolean,
    onUpdateStatus: (WorkOrderEntity, String) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                StatusBadge(workOrder.status, statusTone(workOrder.status))
            }
            Text(workOrder.description, style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                StatusBadge(workOrder.priority, priorityTone(workOrder.priority))
                AssistChip(onClick = {}, label = { Text(workOrder.assignedTo, maxLines = 1) })
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            InfoRow("تاريخ الإنشاء", workOrder.createdAt)
            InfoRow("تاريخ الاستحقاق", workOrder.dueAt)
            InfoRow("التكلفة التقديرية", "%.2f".format(workOrder.estimatedCost))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                if (workOrder.status == "Open") {
                    OutlinedButton(onClick = { onUpdateStatus(workOrder, "In Progress") }, modifier = Modifier.weight(1f)) { Text("بدء") }
                }
                if (workOrder.status != "Closed") {
                    Button(onClick = { onUpdateStatus(workOrder, "Closed") }, modifier = Modifier.weight(1f)) { Text("إغلاق") }
                }
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

// ---------------------------------------------------------------------------
// Preventive maintenance (Supervision tab)
// ---------------------------------------------------------------------------

@Composable
private fun PreventiveMaintenanceScreen(
    innerPadding: PaddingValues,
    pmItems: List<PreventiveMaintenanceEntity>,
    assets: List<AssetEntity>,
    assetMap: Map<Long, AssetEntity>,
    canManage: Boolean,
    onSave: (PreventiveMaintenanceEntity) -> Unit,
    onDelete: (PreventiveMaintenanceEntity) -> Unit,
    onDone: (PreventiveMaintenanceEntity) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<PreventiveMaintenanceEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<PreventiveMaintenanceEntity?>(null) }

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
                    onDone = onDone,
                    onEdit = { editing = item; showForm = true },
                    onDelete = { deleteTarget = item }
                )
            }
        }
    }

    if (showForm) {
        PmFormSheet(initial = editing, assets = assets, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
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
private fun PreventiveMaintenanceCard(
    item: PreventiveMaintenanceEntity,
    asset: AssetEntity?,
    canManage: Boolean,
    onDone: (PreventiveMaintenanceEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val due = DateStrings.isDueOrOverdue(item.nextDueAt)
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
            Button(onClick = { onDone(item) }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("تم التنفيذ")
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

// ---------------------------------------------------------------------------
// Inventory
// ---------------------------------------------------------------------------

@Composable
private fun InventoryScreen(
    innerPadding: PaddingValues,
    parts: List<SparePartEntity>,
    transactions: List<InventoryTransactionEntity>,
    canReceive: Boolean,
    canManage: Boolean,
    onIssue: (SparePartEntity) -> Unit,
    onReceive: (SparePartEntity) -> Unit,
    onSave: (SparePartEntity) -> Unit,
    onDelete: (SparePartEntity) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<SparePartEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<SparePartEntity?>(null) }
    val filtered = remember(query, parts) {
        if (query.isBlank()) parts else parts.filter { part ->
            val q = query.lowercase(Locale.getDefault())
            part.partNumber.lowercase(Locale.getDefault()).contains(q) ||
                part.name.lowercase(Locale.getDefault()).contains(q) ||
                part.equipmentGroup.lowercase(Locale.getDefault()).contains(q)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SearchField(query = query, onChange = { query = it }, placeholder = "بحث: BRG-6205 أو Sensor") }
            if (canManage) {
                item { AddButton("قطعة غيار جديدة") { editing = null; showForm = true } }
            }
            item { SectionHeader("قطع الغيار") }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد قطع غيار مطابقة", Icons.Filled.Inventory2) }
            }
            items(filtered, key = { it.id }) { part ->
                SparePartCard(
                    part = part,
                    canReceive = canReceive,
                    canManage = canManage,
                    onIssue = onIssue,
                    onReceive = onReceive,
                    onEdit = { editing = part; showForm = true },
                    onDelete = { deleteTarget = part }
                )
            }
            item {
                Spacer(modifier = Modifier.height(8.dp))
                SectionHeader("آخر حركات المخزون")
            }
            if (transactions.isEmpty()) {
                item { EmptyState("لا توجد حركات مخزون") }
            }
            items(transactions, key = { it.id }) { transaction -> TransactionCard(transaction = transaction) }
        }
    }

    if (showForm) {
        PartFormSheet(initial = editing, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف القطعة",
            text = "هل تريد حذف ${target.partNumber} - ${target.name}؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun SparePartCard(
    part: SparePartEntity,
    canReceive: Boolean,
    canManage: Boolean,
    onIssue: (SparePartEntity) -> Unit,
    onReceive: (SparePartEntity) -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val lowStock = part.onHandQty <= part.minQty
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(part.partNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(part.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (lowStock) "منخفض" else "متوفر", statusTone(if (lowStock) "stopped" else "running"))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            InfoRow("المعدة", part.equipmentGroup)
            InfoRow("الكمية", "${part.onHandQty} ${part.unit}")
            InfoRow("الحد الأدنى", "${part.minQty} ${part.unit}")
            InfoRow("الموقع", part.location)
            InfoRow("آخر سعر", "%.2f".format(part.lastPrice))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { onIssue(part) }, modifier = Modifier.weight(1f)) { Text("صرف -1") }
                if (canReceive) {
                    Button(onClick = { onReceive(part) }, modifier = Modifier.weight(1f)) { Text("استلام +1") }
                }
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

@Composable
private fun TransactionCard(transaction: InventoryTransactionEntity) {
    val isIssue = transaction.transactionType == "Issue"
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(
                icon = if (isIssue) Icons.Filled.Bolt else Icons.Filled.Add,
                tint = if (isIssue) StatusStopped else StatusRunning,
                container = if (isIssue) StatusStoppedContainer else StatusRunningContainer,
                size = 40
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isIssue) "صرف ${transaction.quantity}" else "استلام ${transaction.quantity}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                LtrText("Part #${transaction.partId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${transaction.createdAt} • ${transaction.createdBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Reports
// ---------------------------------------------------------------------------

@Composable
private fun ReportsScreen(
    innerPadding: PaddingValues,
    stats: DashboardStats,
    workOrders: List<WorkOrderEntity>,
    parts: List<SparePartEntity>,
    pmItems: List<PreventiveMaintenanceEntity>
) {
    val openCost = workOrders.filter { it.status != "Closed" }.sumOf { it.estimatedCost }
    val closed = workOrders.count { it.status == "Closed" }
    val lowStock = parts.filter { it.onHandQty <= it.minQty }
    val duePm = pmItems.filter { DateStrings.isDueOrOverdue(it.nextDueAt) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("تقارير مختصرة")
            Text("جاهزة للتوسعة لاحقًا لتصدير PDF أو Excel.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    }
}

@Composable
private fun ReportCard(title: String, lines: List<String>) {
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

// ---------------------------------------------------------------------------
// Audit (governance) + Admin
// ---------------------------------------------------------------------------

@Composable
private fun AuditScreen(innerPadding: PaddingValues, auditLog: List<AuditLogEntity>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("سجل التدقيق")
            Text("تتبّع كامل لكل إجراء: من فعل ماذا ومتى.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        if (auditLog.isEmpty()) {
            item { EmptyState("لا توجد سجلات بعد", Icons.Filled.History) }
        }
        items(auditLog, key = { it.id }) { log -> AuditLogCard(log) }
    }
}

@Composable
private fun AuditLogCard(log: AuditLogEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(log.details, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                StatusBadge(log.action, statusTone(log.action))
            }
            Text("${log.performedBy} • ${log.createdAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AdminScreen(
    innerPadding: PaddingValues,
    users: List<UserEntity>,
    currentUser: UserEntity?,
    onAddTechnician: () -> Unit,
    onResetSampleData: () -> Unit,
    onSave: (UserEntity) -> Unit,
    onSetActive: (UserEntity, Boolean) -> Unit,
    onDelete: (UserEntity) -> Unit
) {
    if (currentUser?.isAdmin != true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) { EmptyState("هذه الصفحة للمدير فقط", Icons.Filled.AdminPanelSettings) }
        return
    }

    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<UserEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<UserEntity?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { AddButton("مستخدم جديد") { editing = null; showForm = true } }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    Button(onClick = onAddTechnician, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("فني سريع")
                    }
                    OutlinedButton(onClick = onResetSampleData, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("إعادة تعيين")
                    }
                }
            }
            item { SectionHeader("المستخدمون (${users.size})") }
            items(users, key = { it.id }) { user ->
                UserCard(
                    user = user,
                    isSelf = user.id == currentUser.id,
                    onEdit = { editing = user; showForm = true },
                    onToggleActive = { onSetActive(user, !user.isActive) },
                    onDelete = { deleteTarget = user }
                )
            }
        }
    }

    if (showForm) {
        UserFormSheet(initial = editing, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف المستخدم",
            text = "هل تريد حذف ${target.name} (@${target.username})؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
private fun UserCard(
    user: UserEntity,
    isSelf: Boolean,
    onEdit: () -> Unit,
    onToggleActive: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), verticalAlignment = Alignment.CenterVertically) {
                CircleAvatar(initials = user.initials, size = 42)
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.name, fontWeight = FontWeight.Bold)
                    LtrText("@${user.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (user.isActive) user.role else "معطّل", statusTone(if (!user.isActive) "neutral" else if (user.isAdmin) "info" else "running"))
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("تعديل")
                }
                if (!isSelf) {
                    OutlinedButton(onClick = onToggleActive, modifier = Modifier.weight(1f)) {
                        Text(if (user.isActive) "تعطيل" else "تفعيل")
                    }
                    TextButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
                        Text("حذف", color = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Shared
// ---------------------------------------------------------------------------

@Composable
private fun SearchField(query: String, onChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = query,
        onValueChange = onChange,
        leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
        placeholder = { Text(placeholder) },
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun AddButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp),
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AccentOrange)
    ) {
        Icon(Icons.Filled.Add, contentDescription = null, tint = Color.White)
        Spacer(modifier = Modifier.width(8.dp))
        Text(label, color = Color.White, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
    }
}

@Composable
private fun EditDeleteRow(onEdit: () -> Unit, onDelete: () -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("تعديل")
        }
        TextButton(onClick = onDelete, modifier = Modifier.weight(1f)) {
            Icon(Icons.Filled.Delete, contentDescription = null, modifier = Modifier.size(18.dp), tint = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.width(6.dp))
            Text("حذف", color = MaterialTheme.colorScheme.error)
        }
    }
}

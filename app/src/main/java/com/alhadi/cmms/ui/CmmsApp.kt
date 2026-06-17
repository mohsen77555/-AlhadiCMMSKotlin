package com.alhadi.cmms.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.graphics.Brush
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
import com.alhadi.cmms.ui.theme.BrandIndigo
import com.alhadi.cmms.ui.theme.BrandTeal
import com.alhadi.cmms.ui.theme.StatusInfo
import com.alhadi.cmms.ui.theme.StatusInfoContainer
import com.alhadi.cmms.ui.theme.StatusRunning
import com.alhadi.cmms.ui.theme.StatusRunningContainer
import com.alhadi.cmms.ui.theme.StatusStopped
import com.alhadi.cmms.ui.theme.StatusStoppedContainer
import com.alhadi.cmms.ui.theme.StatusTone
import com.alhadi.cmms.ui.theme.StatusWarning
import com.alhadi.cmms.ui.theme.StatusWarningContainer
import com.alhadi.cmms.ui.theme.priorityTone
import com.alhadi.cmms.ui.theme.statusTone
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.viewmodel.CmmsViewModel
import com.alhadi.cmms.viewmodel.DashboardStats
import kotlinx.coroutines.launch
import java.util.Locale

private enum class AppSection(
    val arabicTitle: String,
    val icon: ImageVector
) {
    Dashboard("الرئيسية", Icons.Filled.Dashboard),
    Assets("الأصول", Icons.Filled.PrecisionManufacturing),
    WorkOrders("أوامر العمل", Icons.Filled.Assignment),
    PreventiveMaintenance("الصيانة", Icons.Filled.EventRepeat),
    Inventory("المخزون", Icons.Filled.Inventory2),
    Reports("التقارير", Icons.Filled.Analytics),
    Admin("الإدارة", Icons.Filled.AdminPanelSettings)
}

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
    var selectedSection by rememberSaveable { mutableStateOf(AppSection.Dashboard) }
    var showCreateSheet by rememberSaveable { mutableStateOf(false) }

    val visibleSections = remember(currentUser) {
        AppSection.entries.filter { section ->
            section != AppSection.Admin || currentUser?.isAdmin == true
        }
    }

    LaunchedEffect(visibleSections) {
        if (selectedSection !in visibleSections) selectedSection = AppSection.Dashboard
    }

    LaunchedEffect(message) {
        val text = message
        if (!text.isNullOrBlank()) {
            snackbarHostState.showSnackbar(text)
            viewModel.clearMessage()
        }
    }

    val canManage = currentUser?.canManage == true

    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(selectedSection.arabicTitle, fontWeight = FontWeight.Bold)
                            Text(
                                text = "Alhadi CMMS",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    },
                    navigationIcon = {
                        Box(modifier = Modifier.padding(start = 12.dp)) {
                            CircleAvatar(initials = currentUser?.initials ?: "?", size = 38)
                        }
                    },
                    actions = {
                        IconButton(onClick = viewModel::logout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "تسجيل الخروج")
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            },
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    visibleSections.forEach { section ->
                        NavigationBarItem(
                            selected = selectedSection == section,
                            onClick = { selectedSection = section },
                            icon = { Icon(section.icon, contentDescription = section.arabicTitle) },
                            label = {
                                Text(
                                    section.arabicTitle,
                                    maxLines = 1,
                                    style = MaterialTheme.typography.labelSmall
                                )
                            }
                        )
                    }
                }
            },
            floatingActionButton = {
                if (selectedSection == AppSection.WorkOrders && canManage) {
                    ExtendedFloatingActionButton(
                        onClick = { showCreateSheet = true },
                        icon = { Icon(Icons.Filled.Add, contentDescription = null) },
                        text = { Text("أمر عمل") }
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.background
        ) { innerPadding ->
            val assetMap = assets.associateBy { it.id }
            AnimatedContent(
                targetState = selectedSection,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "section"
            ) { section ->
                when (section) {
                    AppSection.Dashboard -> DashboardScreen(
                        innerPadding = innerPadding,
                        userName = currentUser?.name ?: "",
                        stats = stats,
                        assets = assets,
                        workOrders = workOrders,
                        parts = spareParts,
                        pmItems = preventiveMaintenance
                    )

                    AppSection.Assets -> AssetsScreen(innerPadding = innerPadding, assets = assets)

                    AppSection.WorkOrders -> WorkOrdersScreen(
                        innerPadding = innerPadding,
                        workOrders = workOrders,
                        assetMap = assetMap,
                        onUpdateStatus = viewModel::updateWorkOrderStatus
                    )

                    AppSection.PreventiveMaintenance -> PreventiveMaintenanceScreen(
                        innerPadding = innerPadding,
                        pmItems = preventiveMaintenance,
                        assetMap = assetMap,
                        onDone = viewModel::markPreventiveMaintenanceDone
                    )

                    AppSection.Inventory -> InventoryScreen(
                        innerPadding = innerPadding,
                        parts = spareParts,
                        transactions = transactions,
                        canReceive = canManage,
                        onIssue = viewModel::issuePart,
                        onReceive = viewModel::receivePart
                    )

                    AppSection.Reports -> ReportsScreen(
                        innerPadding = innerPadding,
                        stats = stats,
                        workOrders = workOrders,
                        parts = spareParts,
                        pmItems = preventiveMaintenance
                    )

                    AppSection.Admin -> AdminScreen(
                        innerPadding = innerPadding,
                        users = users,
                        auditLog = auditLog,
                        currentUser = currentUser,
                        onAddTechnician = viewModel::addTechnician,
                        onResetSampleData = viewModel::resetSampleData
                    )
                }
            }
        }

        if (showCreateSheet) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            val scope = rememberCoroutineScope()
            CreateWorkOrderSheet(
                assets = assets,
                sheetState = sheetState,
                onDismiss = { showCreateSheet = false },
                onCreate = { assetId, title, desc, priority, dueDays ->
                    viewModel.createWorkOrder(
                        assetId = assetId,
                        title = title,
                        description = desc,
                        priority = priority,
                        assignedTo = currentUser?.name ?: "Unassigned",
                        dueAt = DateStrings.daysFromToday(dueDays),
                        estimatedCost = 0.0
                    )
                    scope.launch { sheetState.hide() }
                    showCreateSheet = false
                }
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Dashboard
// ---------------------------------------------------------------------------

@Composable
private fun DashboardScreen(
    innerPadding: PaddingValues,
    userName: String,
    stats: DashboardStats,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    parts: List<SparePartEntity>,
    pmItems: List<PreventiveMaintenanceEntity>
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item { HeroHeader(userName = userName, openWorkOrders = stats.openWorkOrders, duePm = stats.duePm) }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    KpiCard("الأصول", stats.assets.toString(), "Assets", Icons.Filled.PrecisionManufacturing, StatusInfo, StatusInfoContainer, Modifier.weight(1f))
                    KpiCard("أوامر مفتوحة", stats.openWorkOrders.toString(), "Open WO", Icons.Filled.Assignment, StatusWarning, StatusWarningContainer, Modifier.weight(1f))
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    KpiCard("صيانة مستحقة", stats.duePm.toString(), "Due PM", Icons.Filled.EventRepeat, StatusStopped, StatusStoppedContainer, Modifier.weight(1f))
                    KpiCard("نقص مخزون", stats.lowStock.toString(), "Low Stock", Icons.Filled.Inventory2, StatusRunning, StatusRunningContainer, Modifier.weight(1f))
                }
            }
        }

        item { SectionHeader("تنبيهات مهمة") }

        val warningAssets = assets.filter { it.status != "Running" }.take(4)
        val lowStockParts = parts.filter { it.onHandQty <= it.minQty }.take(4)
        val duePm = pmItems.filter { DateStrings.isDueOrOverdue(it.nextDueAt) }.take(4)

        if (warningAssets.isEmpty() && lowStockParts.isEmpty() && duePm.isEmpty()) {
            item { EmptyState("لا توجد تنبيهات حاليًا — كل شيء يعمل بشكل جيد", Icons.Filled.CheckCircle) }
        } else {
            items(warningAssets, key = { "asset-${it.id}" }) { asset ->
                AlertCard(
                    icon = Icons.Filled.Warning,
                    tone = statusTone(asset.status),
                    title = "${asset.code} • ${asset.name}",
                    body = "الحالة: ${asset.status} • الموقع: ${asset.location}"
                )
            }
            items(lowStockParts, key = { "part-${it.id}" }) { part ->
                AlertCard(
                    icon = Icons.Filled.Inventory2,
                    tone = statusTone("stopped"),
                    title = "${part.partNumber} • ${part.name}",
                    body = "المتوفر ${part.onHandQty} ${part.unit} • الحد الأدنى ${part.minQty}"
                )
            }
            items(duePm, key = { "pm-${it.id}" }) { pm ->
                AlertCard(
                    icon = Icons.Filled.EventRepeat,
                    tone = statusTone("warning"),
                    title = pm.title,
                    body = "مستحقة بتاريخ ${pm.nextDueAt}"
                )
            }
        }

        item { SectionHeader("آخر أوامر العمل") }
        if (workOrders.isEmpty()) {
            item { EmptyState("لا توجد أوامر عمل") }
        }
        items(workOrders.take(5), key = { "wo-${it.id}" }) { workOrder ->
            CompactWorkOrderCard(workOrder)
        }
    }
}

@Composable
private fun HeroHeader(userName: String, openWorkOrders: Int, duePm: Int) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(listOf(BrandTeal, BrandIndigo)))
                .padding(20.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (userName.isBlank()) "مرحباً" else "مرحباً، $userName",
                    style = MaterialTheme.typography.titleLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "لديك $openWorkOrders أمر عمل مفتوح و $duePm مهمة صيانة مستحقة.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    tint: Color,
    container: Color,
    modifier: Modifier = Modifier
) {
    ElevatedCard(
        modifier = modifier.height(148.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            IconBubble(icon = icon, tint = tint, container = container)
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text(title, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            LtrText(subtitle, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun AlertCard(icon: ImageVector, tone: StatusTone, title: String, body: String) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(icon = icon, tint = tone.content, container = tone.container, size = 40)
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                LtrText(title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                Text(body, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Assets
// ---------------------------------------------------------------------------

@Composable
private fun AssetsScreen(innerPadding: PaddingValues, assets: List<AssetEntity>) {
    var query by rememberSaveable { mutableStateOf("") }
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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SearchField(query = query, onChange = { query = it }, placeholder = "بحث: RM-01 أو Rollermill")
        }

        if (filtered.isEmpty()) {
            item { EmptyState("لا توجد أصول مطابقة للبحث", Icons.Filled.Search) }
        }

        grouped.forEach { (group, groupAssets) ->
            item { SectionHeader("$group (${groupAssets.size})") }
            items(groupAssets, key = { it.id }) { asset -> AssetCard(asset) }
        }
    }
}

@Composable
private fun AssetCard(asset: AssetEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(asset.code, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LtrText(asset.name, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(asset.status, statusTone(asset.status))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            InfoRow("المجموعة", asset.groupName)
            InfoRow("الموقع", asset.location)
            InfoRow("الشركة/الموديل", "${asset.manufacturer} • ${asset.model}")
            InfoRow("الأهمية", asset.criticality)
            InfoRow("آخر فحص", asset.lastInspectionAt)
        }
    }
}

// ---------------------------------------------------------------------------
// Work orders
// ---------------------------------------------------------------------------

@Composable
private fun WorkOrdersScreen(
    innerPadding: PaddingValues,
    workOrders: List<WorkOrderEntity>,
    assetMap: Map<Long, AssetEntity>,
    onUpdateStatus: (WorkOrderEntity, String) -> Unit
) {
    val statusFilters = listOf("All", "Open", "In Progress", "Closed")
    var selectedFilter by rememberSaveable { mutableStateOf("All") }
    val filtered = remember(selectedFilter, workOrders) {
        if (selectedFilter == "All") workOrders else workOrders.filter { it.status == selectedFilter }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                statusFilters.forEach { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = { Text(filter) }
                    )
                }
            }
        }

        if (filtered.isEmpty()) {
            item { EmptyState("لا توجد أوامر عمل", Icons.Filled.Assignment) }
        }

        items(filtered, key = { it.id }) { workOrder ->
            WorkOrderCard(
                workOrder = workOrder,
                asset = assetMap[workOrder.assetId],
                onUpdateStatus = onUpdateStatus
            )
        }
    }
}

@Composable
private fun WorkOrderCard(
    workOrder: WorkOrderEntity,
    asset: AssetEntity?,
    onUpdateStatus: (WorkOrderEntity, String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(workOrder.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LtrText(
                        asset?.let { "${it.code} • ${it.name}" } ?: "Asset #${workOrder.assetId}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
                    OutlinedButton(
                        onClick = { onUpdateStatus(workOrder, "In Progress") },
                        modifier = Modifier.weight(1f)
                    ) { Text("بدء") }
                }
                if (workOrder.status != "Closed") {
                    Button(
                        onClick = { onUpdateStatus(workOrder, "Closed") },
                        modifier = Modifier.weight(1f)
                    ) { Text("إغلاق") }
                }
            }
        }
    }
}

@Composable
private fun CompactWorkOrderCard(workOrder: WorkOrderEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(workOrder.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(workOrder.status, statusTone(workOrder.status))
                StatusBadge(workOrder.priority, priorityTone(workOrder.priority))
            }
            Text(
                "الاستحقاق: ${workOrder.dueAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateWorkOrderSheet(
    assets: List<AssetEntity>,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onCreate: (assetId: Long, title: String, description: String, priority: String, dueInDays: Int) -> Unit
) {
    var title by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }
    var priority by rememberSaveable { mutableStateOf("Medium") }
    var dueDays by rememberSaveable { mutableStateOf(3) }
    var selectedAsset by remember { mutableStateOf(assets.firstOrNull()) }
    var assetMenuOpen by remember { mutableStateOf(false) }

    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("إنشاء أمر عمل جديد", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("عنوان أمر العمل") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("الوصف") },
                minLines = 2,
                modifier = Modifier.fillMaxWidth()
            )

            Text("الأصل", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Box {
                OutlinedButton(onClick = { assetMenuOpen = true }, modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = selectedAsset?.let { "${it.code} • ${it.name}" } ?: "اختر أصلاً",
                        modifier = Modifier.weight(1f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
                }
                DropdownMenu(expanded = assetMenuOpen, onDismissRequest = { assetMenuOpen = false }) {
                    assets.forEach { asset ->
                        DropdownMenuItem(
                            text = { Text("${asset.code} • ${asset.name}") },
                            onClick = {
                                selectedAsset = asset
                                assetMenuOpen = false
                            }
                        )
                    }
                }
            }

            Text("الأولوية", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Low", "Medium", "High", "Critical").forEach { p ->
                    FilterChip(selected = priority == p, onClick = { priority = p }, label = { Text(p) })
                }
            }

            Text("الاستحقاق خلال (أيام): $dueDays", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1, 3, 7, 14, 30).forEach { d ->
                    FilterChip(selected = dueDays == d, onClick = { dueDays = d }, label = { Text(d.toString()) })
                }
            }

            Button(
                onClick = {
                    val assetId = selectedAsset?.id
                    if (assetId != null) onCreate(assetId, title, description, priority, dueDays)
                },
                enabled = selectedAsset != null && title.isNotBlank(),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("حفظ أمر العمل", style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Preventive maintenance
// ---------------------------------------------------------------------------

@Composable
private fun PreventiveMaintenanceScreen(
    innerPadding: PaddingValues,
    pmItems: List<PreventiveMaintenanceEntity>,
    assetMap: Map<Long, AssetEntity>,
    onDone: (PreventiveMaintenanceEntity) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("جدول الصيانة الدورية")
            Text(
                "المهام مرتبة حسب أقرب تاريخ استحقاق.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (pmItems.isEmpty()) {
            item { EmptyState("لا توجد مهام صيانة دورية", Icons.Filled.EventRepeat) }
        }

        items(pmItems, key = { it.id }) { item ->
            PreventiveMaintenanceCard(item = item, asset = assetMap[item.assetId], onDone = onDone)
        }
    }
}

@Composable
private fun PreventiveMaintenanceCard(
    item: PreventiveMaintenanceEntity,
    asset: AssetEntity?,
    onDone: (PreventiveMaintenanceEntity) -> Unit
) {
    val due = DateStrings.isDueOrOverdue(item.nextDueAt)
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(item.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    LtrText(
                        asset?.let { "${it.code} • ${it.name}" } ?: "Asset #${item.assetId}",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
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
    onIssue: (SparePartEntity) -> Unit,
    onReceive: (SparePartEntity) -> Unit
) {
    var query by rememberSaveable { mutableStateOf("") }
    val filtered = remember(query, parts) {
        if (query.isBlank()) parts else parts.filter { part ->
            val q = query.lowercase(Locale.getDefault())
            part.partNumber.lowercase(Locale.getDefault()).contains(q) ||
                part.name.lowercase(Locale.getDefault()).contains(q) ||
                part.equipmentGroup.lowercase(Locale.getDefault()).contains(q)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SearchField(query = query, onChange = { query = it }, placeholder = "بحث: BRG-6205 أو Sensor")
        }

        item { SectionHeader("قطع الغيار") }
        if (filtered.isEmpty()) {
            item { EmptyState("لا توجد قطع غيار مطابقة", Icons.Filled.Inventory2) }
        }
        items(filtered, key = { it.id }) { part ->
            SparePartCard(part = part, canReceive = canReceive, onIssue = onIssue, onReceive = onReceive)
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            SectionHeader("آخر حركات المخزون")
        }
        if (transactions.isEmpty()) {
            item { EmptyState("لا توجد حركات مخزون") }
        }
        items(transactions, key = { it.id }) { transaction ->
            TransactionCard(transaction = transaction)
        }
    }
}

@Composable
private fun SparePartCard(
    part: SparePartEntity,
    canReceive: Boolean,
    onIssue: (SparePartEntity) -> Unit,
    onReceive: (SparePartEntity) -> Unit
) {
    val lowStock = part.onHandQty <= part.minQty
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                OutlinedButton(onClick = { onIssue(part) }, modifier = Modifier.weight(1f)) {
                    Text("صرف -1")
                }
                if (canReceive) {
                    Button(onClick = { onReceive(part) }, modifier = Modifier.weight(1f)) {
                        Text("استلام +1")
                    }
                }
            }
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
                Text(
                    "${transaction.createdAt} • ${transaction.createdBy}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
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
            Text(
                "هذه الشاشة جاهزة للتوسعة لاحقًا لتصدير PDF أو Excel.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            ReportCard(
                title = "ملخص الصيانة",
                lines = listOf(
                    "إجمالي الأصول: ${stats.assets}",
                    "أوامر العمل المفتوحة: ${stats.openWorkOrders}",
                    "أوامر العمل المغلقة: $closed",
                    "تكلفة تقديرية مفتوحة: ${"%.2f".format(openCost)}"
                )
            )
        }
        item {
            ReportCard(
                title = "الصيانة الدورية",
                lines = listOf(
                    "مهام PM المستحقة: ${duePm.size}",
                    "أقرب مهمة: ${duePm.firstOrNull()?.title ?: "لا يوجد"}"
                )
            )
        }
        item {
            ReportCard(
                title = "المخزون",
                lines = listOf(
                    "قطع تحت الحد الأدنى: ${lowStock.size}",
                    "أول قطعة ناقصة: ${lowStock.firstOrNull()?.partNumber ?: "لا يوجد"}"
                )
            )
        }
    }
}

@Composable
private fun ReportCard(title: String, lines: List<String>) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SectionHeader(title)
            lines.forEach { line -> Text("• $line", style = MaterialTheme.typography.bodyMedium) }
        }
    }
}

// ---------------------------------------------------------------------------
// Admin + audit log (governance)
// ---------------------------------------------------------------------------

@Composable
private fun AdminScreen(
    innerPadding: PaddingValues,
    users: List<UserEntity>,
    auditLog: List<AuditLogEntity>,
    currentUser: UserEntity?,
    onAddTechnician: () -> Unit,
    onResetSampleData: () -> Unit
) {
    if (currentUser?.isAdmin != true) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            EmptyState("هذه الصفحة للمدير فقط", Icons.Filled.AdminPanelSettings)
        }
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("إعدادات المدير")
            Text(
                "صفحة الإدارة تظهر فقط للمستخدمين بدور Admin.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = onAddTechnician, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إضافة فني")
                }
                OutlinedButton(onClick = onResetSampleData, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Refresh, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("إعادة تعيين")
                }
            }
        }

        item { SectionHeader("المستخدمون (${users.size})") }
        items(users, key = { it.id }) { user -> UserCard(user) }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(
                    Icons.Filled.NotificationsActive,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                SectionHeader("سجل التدقيق (الحوكمة)")
            }
        }
        if (auditLog.isEmpty()) {
            item { EmptyState("لا توجد سجلات بعد") }
        }
        items(auditLog, key = { it.id }) { log -> AuditLogCard(log) }
    }
}

@Composable
private fun UserCard(user: UserEntity) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            CircleAvatar(initials = user.initials, size = 42)
            Column(modifier = Modifier.weight(1f)) {
                Text(user.name, fontWeight = FontWeight.Bold)
                LtrText("@${user.username}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            StatusBadge(user.role, statusTone(if (user.isAdmin) "info" else "neutral"))
        }
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
            Text(
                "${log.performedBy} • ${log.createdAt}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
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
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.fillMaxWidth()
    )
}

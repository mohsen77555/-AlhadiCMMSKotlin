package com.alhadi.cmms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.SerialInstallRequest
import com.alhadi.cmms.data.SerialMasterRequest
import com.alhadi.cmms.data.SerialTransferRequest
import com.alhadi.cmms.data.SerializedIssueRequest
import com.alhadi.cmms.data.SerializedReceiptRequest
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import java.util.Locale

@Composable
internal fun SerialNumbersScreen(
    innerPadding: PaddingValues,
    profiles: List<SerialNumberProfileEntity>,
    serials: List<SerialNumberEntity>,
    movements: List<SerialNumberMovementEntity>,
    parts: List<SparePartEntity>,
    assets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>,
    canManage: Boolean,
    onSaveProfile: (SerialNumberProfileEntity) -> Unit,
    onDeleteProfile: (SerialNumberProfileEntity) -> Unit,
    onCreateMaster: (SerialMasterRequest) -> Unit,
    onReceive: (SerializedReceiptRequest) -> Unit,
    onIssue: (SerializedIssueRequest) -> Unit,
    onTransfer: (SerialTransferRequest) -> Unit,
    onInstall: (SerialInstallRequest) -> Unit,
    onDismantle: (Long, String) -> Unit,
    onReconcile: (Long) -> Unit,
    onDeleteSerial: (SerialNumberEntity) -> Unit
) {
    val serializedParts = remember(parts) { parts.filter { it.serializationActive } }
    val partMap = remember(parts) { parts.associateBy { it.id } }
    val profileMap = remember(profiles) { profiles.associateBy { it.id } }
    val assetMap = remember(assets) { assets.associateBy { it.id } }
    val serialMap = remember(serials) { serials.associateBy { it.id } }

    var view by rememberSaveable { mutableStateOf("Units") }
    var query by rememberSaveable { mutableStateOf("") }
    var statusFilter by rememberSaveable { mutableStateOf("All") }
    var showProfileForm by remember { mutableStateOf(false) }
    var editingProfile by remember { mutableStateOf<SerialNumberProfileEntity?>(null) }
    var deletingProfile by remember { mutableStateOf<SerialNumberProfileEntity?>(null) }
    var showMasterForm by remember { mutableStateOf(false) }
    var receiptPart by remember { mutableStateOf<SparePartEntity?>(null) }
    var issuePart by remember { mutableStateOf<SparePartEntity?>(null) }
    var transferSerial by remember { mutableStateOf<SerialNumberEntity?>(null) }
    var installSerial by remember { mutableStateOf<SerialNumberEntity?>(null) }
    var dismantleSerial by remember { mutableStateOf<SerialNumberEntity?>(null) }
    var deletingSerial by remember { mutableStateOf<SerialNumberEntity?>(null) }

    val filteredSerials = remember(query, statusFilter, serials, partMap, assetMap) {
        val q = query.lowercase(Locale.getDefault())
        serials.filter { serial ->
            val part = partMap[serial.partId]
            val asset = serial.assetId?.let(assetMap::get)
            (statusFilter == "All" || serial.status == statusFilter) &&
                (q.isBlank() ||
                    serial.serialNumber.lowercase(Locale.getDefault()).contains(q) ||
                    part?.partNumber?.lowercase(Locale.getDefault())?.contains(q) == true ||
                    part?.name?.lowercase(Locale.getDefault())?.contains(q) == true ||
                    asset?.code?.lowercase(Locale.getDefault())?.contains(q) == true ||
                    serial.storageLocation.lowercase(Locale.getDefault()).contains(q))
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SerialSectionTitle("الأرقام التسلسلية", "تتبّع كل وحدة من الاستلام حتى التركيب والحركة.")
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                SerialMetric("المسجلة", serials.size.toString(), Modifier.weight(1f))
                SerialMetric("في المخزون", serials.count { it.status == "InStock" }.toString(), Modifier.weight(1f))
                SerialMetric("مركبة", serials.count { it.status == "Installed" }.toString(), Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Units" to "الوحدات", "Profiles" to "ملفات التتبع", "Movements" to "الحركات").forEach { (value, label) ->
                    FilterChip(selected = view == value, onClick = { view = value }, label = { Text(label) })
                }
            }
        }

        when (view) {
            "Profiles" -> {
                if (canManage) {
                    item {
                        Button(onClick = { editingProfile = null; showProfileForm = true }, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Filled.Add, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ملف تتبع جديد")
                        }
                    }
                }
                if (profiles.isEmpty()) item { SerialEmpty("لا توجد ملفات تتبع") }
                items(profiles, key = { "profile-${it.id}" }) { profile ->
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(profile.code, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Text(profile.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                                }
                                SerialBadge(serialStockCheckLabel(profile.stockCheckMode), serialModeColor(profile.stockCheckMode))
                            }
                            if (profile.description.isNotBlank()) Text(profile.description, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                if (profile.requireOnReceipt) SerialBadge("إلزامي عند الاستلام", MaterialTheme.colorScheme.primary)
                                if (profile.requireOnIssue) SerialBadge("إلزامي عند الصرف", MaterialTheme.colorScheme.primary)
                                if (profile.autoCreate) SerialBadge("إنشاء تلقائي", MaterialTheme.colorScheme.tertiary)
                                if (profile.equipmentRequired) SerialBadge("ارتباط بأصل", MaterialTheme.colorScheme.secondary)
                            }
                            if (canManage) {
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    OutlinedButton(onClick = { editingProfile = profile; showProfileForm = true }, modifier = Modifier.weight(1f)) { Text("تعديل") }
                                    TextButton(onClick = { deletingProfile = profile }, modifier = Modifier.weight(1f)) { Text("حذف", color = MaterialTheme.colorScheme.error) }
                                }
                            }
                        }
                    }
                }
            }

            "Movements" -> {
                if (movements.isEmpty()) item { SerialEmpty("لا توجد حركات تسلسلية") }
                items(movements, key = { "movement-${it.id}" }) { movement ->
                    val serial = serialMap[movement.serialId]
                    val part = partMap[movement.partId]
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            Box(modifier = Modifier.size(40.dp).background(MaterialTheme.colorScheme.primaryContainer, CircleShape), contentAlignment = Alignment.Center) {
                                Icon(Icons.Filled.SwapHoriz, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            }
                            Column(modifier = Modifier.weight(1f)) {
                                Text(serialMovementLabel(movement.movementType), fontWeight = FontWeight.Bold)
                                Text(serial?.serialNumber ?: "#${movement.serialId}", color = MaterialTheme.colorScheme.primary)
                                Text(part?.partNumber ?: "قطعة #${movement.partId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("${movement.createdAt} • ${movement.createdBy}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            if (movement.workOrderId != null) SerialBadge("أمر #${movement.workOrderId}", MaterialTheme.colorScheme.tertiary)
                        }
                    }
                }
            }

            else -> {
                if (canManage) {
                    item {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            Button(onClick = { showMasterForm = true }, modifier = Modifier.weight(1f), enabled = serializedParts.isNotEmpty()) {
                                Icon(Icons.Filled.Add, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("إنشاء سجل")
                            }
                            OutlinedButton(onClick = { view = "Profiles" }, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Filled.Build, contentDescription = null)
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("الإعدادات")
                            }
                        }
                    }
                }
                if (serializedParts.isEmpty()) {
                    item { SerialEmpty("فعّل التتبع الفردي في بطاقة إحدى قطع الغيار أولاً") }
                }
                items(serializedParts, key = { "part-${it.id}" }) { part ->
                    val partSerials = serials.filter { it.partId == part.id }
                    val inStock = partSerials.count { it.status == "InStock" }
                    val profile = part.serialProfileId?.let(profileMap::get)
                    val mismatch = inStock != part.onHandQty
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(part.partNumber, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                    Text(part.name, style = MaterialTheme.typography.titleSmall)
                                }
                                SerialBadge(if (mismatch) "اختلاف مخزون" else "متطابق", if (mismatch) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
                            }
                            SerialInfo("الكمية الدفترية", "${part.onHandQty} ${part.unit}")
                            SerialInfo("الوحدات في المخزون", inStock.toString())
                            SerialInfo("ملف التتبع", profile?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                            if (canManage) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                    Button(onClick = { receiptPart = part }, modifier = Modifier.weight(1f)) { Text("استلام") }
                                    OutlinedButton(onClick = { issuePart = part }, modifier = Modifier.weight(1f), enabled = inStock > 0) { Text("صرف") }
                                }
                                if (mismatch && profile?.allowManualStockEdit == true) {
                                    TextButton(onClick = { onReconcile(part.id) }, modifier = Modifier.fillMaxWidth()) { Text("تسوية الكمية حسب الوحدات المتسلسلة") }
                                }
                            }
                        }
                    }
                }

                item {
                    androidx.compose.material3.OutlinedTextField(
                        value = query,
                        onValueChange = { query = it },
                        label = { Text("بحث بالرقم أو القطعة أو الأصل") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                item {
                    Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("All", "Created", "InStock", "Issued", "Installed", "InRepair", "Scrapped").forEach { status ->
                            FilterChip(
                                selected = statusFilter == status,
                                onClick = { statusFilter = status },
                                label = { Text(if (status == "All") "الكل" else serialStatusLabel(status)) }
                            )
                        }
                    }
                }
                if (filteredSerials.isEmpty() && serializedParts.isNotEmpty()) item { SerialEmpty("لا توجد وحدات مطابقة") }
                items(filteredSerials, key = { "serial-${it.id}" }) { serial ->
                    val part = partMap[serial.partId]
                    val asset = serial.assetId?.let(assetMap::get)
                    ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                                Box(modifier = Modifier.size(42.dp).background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
                                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(serial.serialNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                                    Text(part?.let { "${it.partNumber} • ${it.name}" } ?: "قطعة #${serial.partId}", maxLines = 1, overflow = TextOverflow.Ellipsis, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                SerialBadge(serialStatusLabel(serial.status), serialStatusColor(serial.status))
                            }
                            if (serial.status == "InStock") {
                                SerialInfo("المخزون", "${serialStockTypeLabel(serial.stockType)} • ${serial.storageLocation.ifBlank { "بدون موقع" }}")
                                if (serial.plant.isNotBlank()) SerialInfo("الموقع التشغيلي", serial.plant)
                            }
                            if (asset != null) SerialInfo("الأصل", "${asset.code} • ${asset.name}")
                            if (serial.currentWorkOrderId != null) SerialInfo("أمر العمل", "#${serial.currentWorkOrderId}")
                            if (serial.lastMovementAt.isNotBlank()) SerialInfo("آخر حركة", serial.lastMovementAt)
                            if (canManage) {
                                Row(modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    if (serial.status == "InStock") {
                                        OutlinedButton(onClick = { transferSerial = serial }) {
                                            Icon(Icons.Filled.SwapHoriz, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("نقل")
                                        }
                                        Button(onClick = { installSerial = serial }) {
                                            Icon(Icons.Filled.Link, contentDescription = null, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("تركيب")
                                        }
                                    }
                                    if (serial.status == "Installed") {
                                        OutlinedButton(onClick = { dismantleSerial = serial }) { Text("فك من الأصل") }
                                    }
                                    if (serial.status == "Created" || serial.status == "Scrapped") {
                                        IconButton(onClick = { deletingSerial = serial }) {
                                            Icon(Icons.Filled.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showProfileForm) {
        SerialProfileFormSheet(initial = editingProfile, onDismiss = { showProfileForm = false }, onSave = { onSaveProfile(it); showProfileForm = false })
    }
    if (showMasterForm) {
        SerialMasterFormSheet(parts = serializedParts, onDismiss = { showMasterForm = false }, onSave = { onCreateMaster(it); showMasterForm = false })
    }
    receiptPart?.let { part ->
        SerialReceiptFormSheet(part = part, onDismiss = { receiptPart = null }, onSave = { onReceive(it); receiptPart = null })
    }
    issuePart?.let { part ->
        SerialIssueFormSheet(
            part = part,
            serials = serials.filter { it.partId == part.id && it.status == "InStock" },
            workOrders = workOrders.filter { it.status != "Closed" },
            onDismiss = { issuePart = null },
            onSave = { onIssue(it); issuePart = null }
        )
    }
    transferSerial?.let { serial ->
        SerialTransferFormSheet(serial = serial, onDismiss = { transferSerial = null }, onSave = { onTransfer(it); transferSerial = null })
    }
    installSerial?.let { serial ->
        SerialInstallFormSheet(serial = serial, assets = assets, onDismiss = { installSerial = null }, onSave = { onInstall(it); installSerial = null })
    }
    dismantleSerial?.let { serial ->
        ConfirmDialog(
            title = "فك الرقم التسلسلي",
            text = "سيتم فك ${serial.serialNumber} من الأصل دون إعادته تلقائياً إلى المخزون.",
            confirmLabel = "فك",
            onConfirm = { onDismantle(serial.id, "فك من الأصل"); dismantleSerial = null },
            onDismiss = { dismantleSerial = null }
        )
    }
    deletingProfile?.let { profile ->
        ConfirmDialog(
            title = "حذف ملف التتبع",
            text = "هل تريد حذف ${profile.code}؟",
            onConfirm = { onDeleteProfile(profile); deletingProfile = null },
            onDismiss = { deletingProfile = null }
        )
    }
    deletingSerial?.let { serial ->
        ConfirmDialog(
            title = "حذف الرقم التسلسلي",
            text = "هل تريد حذف ${serial.serialNumber}؟",
            onConfirm = { onDeleteSerial(serial); deletingSerial = null },
            onDismiss = { deletingSerial = null }
        )
    }
}

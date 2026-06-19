from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]


def load(relative: str) -> tuple[Path, str]:
    path = ROOT / relative
    return path, path.read_text(encoding="utf-8")


def save(path: Path, text: str) -> None:
    path.write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected one match, found {count}")
    return text.replace(old, new, 1)


ui_dir = ROOT / "app/src/main/java/com/alhadi/cmms/ui"

# -----------------------------------------------------------------------------
# Labels and parsing helpers
# -----------------------------------------------------------------------------
(ui_dir / "SerialNumberUi.kt").write_text('''package com.alhadi.cmms.ui

internal val serialStockTypeOptions = listOf(
    "Unrestricted",
    "Quality",
    "Blocked",
    "Transfer",
    "Transit",
    "Vendor",
    "Customer"
)

internal val serialStockCheckOptions = listOf("None", "Warning", "Block")

internal fun serialStatusLabel(value: String): String = when (value) {
    "Created" -> "مسجل"
    "InStock" -> "في المخزون"
    "Issued" -> "مصروف"
    "Installed" -> "مركب"
    "InRepair" -> "قيد الإصلاح"
    "Scrapped" -> "مستبعد"
    else -> value
}

internal fun serialStockTypeLabel(value: String): String = when (value) {
    "Unrestricted" -> "متاح للاستخدام"
    "Quality" -> "تحت الفحص"
    "Blocked" -> "محظور"
    "Transfer" -> "قيد النقل"
    "Transit" -> "في الطريق"
    "Vendor" -> "لدى المورّد"
    "Customer" -> "لدى العميل"
    "Installed" -> "مركب"
    "Issued" -> "مصروف"
    else -> value.ifBlank { "غير محدد" }
}

internal fun serialStockCheckLabel(value: String): String = when (value) {
    "None" -> "بدون فحص"
    "Warning" -> "سماح مع تحذير"
    "Block" -> "منع عند الاختلاف"
    else -> value
}

internal fun serialMovementLabel(value: String): String = when (value) {
    "Create" -> "إنشاء سجل"
    "Receive" -> "استلام"
    "Issue" -> "صرف"
    "Transfer" -> "نقل"
    "Install" -> "تركيب"
    "Dismantle" -> "فك"
    "Reconcile" -> "تسوية"
    else -> value
}

internal fun parseSerialInput(value: String): List<String> = value
    .replace('،', ',')
    .replace(';', ',')
    .lines()
    .flatMap { it.split(',') }
    .map { it.trim().uppercase() }
    .filter { it.isNotBlank() }
    .distinct()
''', encoding="utf-8")

# -----------------------------------------------------------------------------
# Asset detail serial-number card
# -----------------------------------------------------------------------------
(ui_dir / "AssetSerialSection.kt").write_text('''package com.alhadi.cmms.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SparePartEntity

@Composable
internal fun AssetSerialSection(
    asset: AssetEntity,
    serials: List<SerialNumberEntity>,
    movements: List<SerialNumberMovementEntity>,
    parts: List<SparePartEntity>
) {
    val linked = serials.firstOrNull { it.id == asset.linkedSerialId } ?: serials.firstOrNull { it.assetId == asset.id }
    val part = linked?.let { serial -> parts.firstOrNull { it.id == serial.partId } }
    val history = linked?.let { serial -> movements.filter { it.serialId == serial.id }.take(5) }.orEmpty()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("التتبع الفردي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            when {
                linked != null -> {
                    AssetSerialRow("الرقم التسلسلي", linked.serialNumber)
                    AssetSerialRow("الحالة", serialStatusLabel(linked.status))
                    if (part != null) AssetSerialRow("القطعة المرتبطة", "${part.partNumber} • ${part.name}")
                    if (linked.lastMovementAt.isNotBlank()) AssetSerialRow("آخر حركة", linked.lastMovementAt)
                    if (history.isNotEmpty()) {
                        Text("آخر الحركات", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        history.forEach { movement ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(serialMovementLabel(movement.movementType), fontWeight = FontWeight.Medium)
                                    Text(
                                        "${movement.createdAt} • ${movement.createdBy}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                asset.serialNumber.isNotBlank() -> {
                    AssetSerialRow("الرقم المسجل", asset.serialNumber)
                    Text(
                        "هذا الرقم غير مرتبط بعد بسجل تتبع فردي.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> Text("لا يوجد رقم تسلسلي مرتبط بهذا الأصل.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun AssetSerialRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, modifier = Modifier.weight(1.35f), fontWeight = FontWeight.Medium)
    }
}
''', encoding="utf-8")

# -----------------------------------------------------------------------------
# Main serial-number workspace
# -----------------------------------------------------------------------------
(ui_dir / "SerialNumbersScreen.kt").write_text('''package com.alhadi.cmms.ui

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

@Composable
private fun SerialSectionTitle(title: String, subtitle: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun SerialMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SerialInfo(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, modifier = Modifier.weight(1.4f), fontWeight = FontWeight.Medium)
    }
}

@Composable
private fun SerialBadge(text: String, color: Color) {
    Surface(shape = CircleShape, color = color.copy(alpha = 0.13f)) {
        Text(text, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
private fun SerialEmpty(text: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)) {
        Text(text, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
private fun serialModeColor(mode: String): Color = when (mode) {
    "Block" -> MaterialTheme.colorScheme.error
    "Warning" -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.secondary
}

@Composable
private fun serialStatusColor(status: String): Color = when (status) {
    "InStock" -> MaterialTheme.colorScheme.primary
    "Installed" -> MaterialTheme.colorScheme.tertiary
    "Issued", "InRepair" -> MaterialTheme.colorScheme.secondary
    "Scrapped" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}
''', encoding="utf-8")

# -----------------------------------------------------------------------------
# Forms and spare-part serialization settings
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/Forms.kt")
if "import androidx.compose.foundation.clickable" not in text:
    text = replace_once(text, "import androidx.compose.foundation.layout.Arrangement", "import androidx.compose.foundation.clickable\nimport androidx.compose.foundation.layout.Arrangement", "Forms clickable import")
if "import androidx.compose.material3.Checkbox" not in text:
    text = replace_once(text, "import androidx.compose.material3.Button", "import androidx.compose.material3.Button\nimport androidx.compose.material3.Checkbox", "Forms Checkbox import")
if "import com.alhadi.cmms.data.SerialInstallRequest" not in text:
    text = replace_once(
        text,
        "import com.alhadi.cmms.data.MovementType",
        "import com.alhadi.cmms.data.MovementType\nimport com.alhadi.cmms.data.SerialInstallRequest\nimport com.alhadi.cmms.data.SerialMasterRequest\nimport com.alhadi.cmms.data.SerialTransferRequest\nimport com.alhadi.cmms.data.SerializedIssueRequest\nimport com.alhadi.cmms.data.SerializedReceiptRequest",
        "Forms serial request imports",
    )
if "import com.alhadi.cmms.data.entity.SerialNumberEntity" not in text:
    text = replace_once(
        text,
        "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity",
        "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity\nimport com.alhadi.cmms.data.entity.SerialNumberEntity\nimport com.alhadi.cmms.data.entity.SerialNumberProfileEntity",
        "Forms serial entity imports",
    )

# Part form signature and fields.
text = replace_once(
    text,
    '''internal fun PartFormSheet(initial: SparePartEntity?, onDismiss: () -> Unit, onSave: (SparePartEntity) -> Unit) {''',
    '''internal fun PartFormSheet(
    initial: SparePartEntity?,
    profiles: List<SerialNumberProfileEntity>,
    onDismiss: () -> Unit,
    onSave: (SparePartEntity) -> Unit
) {''',
    "PartFormSheet signature",
)
if "var serializationActive by remember" not in text:
    text = replace_once(
        text,
        '''    var price by remember { mutableStateOf((initial?.lastPrice ?: 0.0).toString()) }''',
        '''    var price by remember { mutableStateOf((initial?.lastPrice ?: 0.0).toString()) }
    var serializationActive by remember { mutableStateOf(initial?.serializationActive ?: false) }
    var serialProfileId by remember { mutableStateOf(initial?.serialProfileId) }''',
        "PartForm serial state",
    )
if 'Text("التتبع الفردي"' not in text[text.find("internal fun PartFormSheet"):text.find("// Work order form")]:
    text = replace_once(
        text,
        '''        LabeledField("آخر سعر", price, { price = it }, numeric = true)
        SaveButton(partNumber.isNotBlank() && name.isNotBlank()) {''',
        '''        LabeledField("آخر سعر", price, { price = it }, numeric = true)
        Text("التتبع الفردي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("تفعيل الأرقام التسلسلية", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = serializationActive, onCheckedChange = { serializationActive = it })
        }
        if (serializationActive) {
            SerialProfileDropdown(profiles, serialProfileId) { serialProfileId = it }
            if (profiles.isEmpty()) Text("أنشئ ملف تتبع من وحدة الأرقام التسلسلية أولاً.", color = MaterialTheme.colorScheme.error)
        }
        SaveButton(partNumber.isNotBlank() && name.isNotBlank() && (!serializationActive || serialProfileId != null)) {''',
        "PartForm serial controls",
    )
if "serializationActive = serializationActive" not in text:
    text = replace_once(
        text,
        '''                    location = location,
                    lastPrice = price.toDoubleOrNull() ?: 0.0
                )''',
        '''                    location = location,
                    lastPrice = price.toDoubleOrNull() ?: 0.0,
                    serializationActive = serializationActive,
                    serialProfileId = if (serializationActive) serialProfileId else null
                )''',
        "PartForm serial constructor",
    )

# Serial forms inserted before work-order forms.
if "internal fun SerialProfileFormSheet" not in text:
    serial_forms = '''

@Composable
private fun SerialProfileDropdown(
    profiles: List<SerialNumberProfileEntity>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val selected = profiles.firstOrNull { it.id == selectedId }
    Column {
        Text("ملف التتبع", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.let { "${it.code} • ${it.name}" } ?: "اختر ملفاً", modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                profiles.forEach { profile ->
                    DropdownMenuItem(text = { Text("${profile.code} • ${profile.name}") }, onClick = { onSelect(profile.id); open = false })
                }
            }
        }
    }
}

@Composable
private fun WorkOrderDropdownOptional(
    workOrders: List<WorkOrderEntity>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val selected = workOrders.firstOrNull { it.id == selectedId }
    Column {
        Text("أمر العمل (اختياري)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.let { "#${it.id} • ${it.title}" } ?: "بدون", modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                DropdownMenuItem(text = { Text("بدون") }, onClick = { onSelect(null); open = false })
                workOrders.forEach { order ->
                    DropdownMenuItem(text = { Text("#${order.id} • ${order.title}") }, onClick = { onSelect(order.id); open = false })
                }
            }
        }
    }
}

@Composable
internal fun SerialProfileFormSheet(
    initial: SerialNumberProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (SerialNumberProfileEntity) -> Unit
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var requireOnReceipt by remember { mutableStateOf(initial?.requireOnReceipt ?: true) }
    var requireOnIssue by remember { mutableStateOf(initial?.requireOnIssue ?: true) }
    var autoCreate by remember { mutableStateOf(initial?.autoCreate ?: true) }
    var equipmentRequired by remember { mutableStateOf(initial?.equipmentRequired ?: false) }
    var stockCheckMode by remember { mutableStateOf(initial?.stockCheckMode ?: "Block") }
    var allowManualStockEdit by remember { mutableStateOf(initial?.allowManualStockEdit ?: false) }
    var equipmentCategory by remember { mutableStateOf(initial?.equipmentCategory ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }

    FormSheet(if (initial == null) "ملف تتبع جديد" else "تعديل ملف التتبع", onDismiss) {
        LabeledField("الكود", code, { code = it })
        LabeledField("الاسم", name, { name = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        OptionDropdown("فحص توافق المخزون", serialStockCheckOptions, stockCheckMode, display = ::serialStockCheckLabel) { stockCheckMode = it }
        LabeledField("فئة الأصل المطلوبة (اختياري)", equipmentCategory, { equipmentCategory = it })
        listOf(
            "إلزام الرقم عند الاستلام" to (requireOnReceipt to { value: Boolean -> requireOnReceipt = value }),
            "إلزام الرقم عند الصرف" to (requireOnIssue to { value: Boolean -> requireOnIssue = value }),
            "إنشاء السجل تلقائياً عند الاستلام" to (autoCreate to { value: Boolean -> autoCreate = value }),
            "يتطلب ارتباطاً بأصل" to (equipmentRequired to { value: Boolean -> equipmentRequired = value }),
            "السماح بالتسوية اليدوية" to (allowManualStockEdit to { value: Boolean -> allowManualStockEdit = value })
        ).forEach { (label, pair) ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(label, modifier = Modifier.weight(1f))
                Switch(checked = pair.first, onCheckedChange = pair.second)
            }
        }
        SaveButton(code.isNotBlank() && name.isNotBlank()) {
            onSave(
                SerialNumberProfileEntity(
                    id = initial?.id ?: 0,
                    code = code,
                    name = name,
                    requireOnReceipt = requireOnReceipt,
                    requireOnIssue = requireOnIssue,
                    autoCreate = autoCreate,
                    equipmentRequired = equipmentRequired,
                    stockCheckMode = stockCheckMode,
                    allowManualStockEdit = allowManualStockEdit,
                    equipmentCategory = equipmentCategory,
                    description = description
                )
            )
        }
    }
}

@Composable
internal fun SerialMasterFormSheet(
    parts: List<SparePartEntity>,
    onDismiss: () -> Unit,
    onSave: (SerialMasterRequest) -> Unit
) {
    var partId by remember { mutableStateOf(parts.firstOrNull()?.id ?: 0L) }
    var serialNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    FormSheet("إنشاء سجل رقم تسلسلي", onDismiss) {
        PartDropdown(parts, partId) { partId = it }
        LabeledField("الرقم التسلسلي", serialNumber, { serialNumber = it })
        LabeledField("ملاحظات", notes, { notes = it }, singleLine = false)
        SaveButton(partId != 0L && serialNumber.isNotBlank()) {
            onSave(SerialMasterRequest(partId = partId, serialNumber = serialNumber, notes = notes))
        }
    }
}

@Composable
internal fun SerialReceiptFormSheet(
    part: SparePartEntity,
    onDismiss: () -> Unit,
    onSave: (SerializedReceiptRequest) -> Unit
) {
    var serialText by remember { mutableStateOf("") }
    var plant by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf(part.location) }
    var stockType by remember { mutableStateOf("Unrestricted") }
    var batch by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val numbers = parseSerialInput(serialText)

    FormSheet("استلام ${part.partNumber}", onDismiss) {
        Text("أدخل رقماً واحداً في كل سطر أو افصل الأرقام بفاصلة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        LabeledField("الأرقام التسلسلية", serialText, { serialText = it }, singleLine = false)
        Text("عدد الوحدات: ${numbers.size}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        LabeledField("الموقع التشغيلي", plant, { plant = it })
        LabeledField("موقع التخزين", storageLocation, { storageLocation = it })
        OptionDropdown("نوع المخزون", serialStockTypeOptions, stockType, display = ::serialStockTypeLabel) { stockType = it }
        LabeledField("الدفعة (اختياري)", batch, { batch = it })
        LabeledField("المورّد (اختياري)", vendor, { vendor = it })
        LabeledField("ملاحظة", note, { note = it }, singleLine = false)
        SaveButton(numbers.isNotEmpty() && storageLocation.isNotBlank()) {
            onSave(SerializedReceiptRequest(part.id, numbers, plant, storageLocation, stockType, batch, vendor, note))
        }
    }
}

@Composable
internal fun SerialIssueFormSheet(
    part: SparePartEntity,
    serials: List<SerialNumberEntity>,
    workOrders: List<WorkOrderEntity>,
    onDismiss: () -> Unit,
    onSave: (SerializedIssueRequest) -> Unit
) {
    var selectedIds by remember(part.id, serials) { mutableStateOf<Set<Long>>(emptySet()) }
    var workOrderId by remember { mutableStateOf<Long?>(null) }
    var note by remember { mutableStateOf("") }
    FormSheet("صرف ${part.partNumber}", onDismiss) {
        Text("حدد الوحدات المطلوب صرفها.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        serials.forEach { serial ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable {
                    selectedIds = if (serial.id in selectedIds) selectedIds - serial.id else selectedIds + serial.id
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = serial.id in selectedIds,
                    onCheckedChange = { checked -> selectedIds = if (checked) selectedIds + serial.id else selectedIds - serial.id }
                )
                Text(serial.serialNumber, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Text(serial.storageLocation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        WorkOrderDropdownOptional(workOrders, workOrderId) { workOrderId = it }
        LabeledField("ملاحظة", note, { note = it }, singleLine = false)
        SaveButton(selectedIds.isNotEmpty()) {
            onSave(SerializedIssueRequest(part.id, selectedIds.toList(), workOrderId, note))
        }
    }
}

@Composable
internal fun SerialTransferFormSheet(
    serial: SerialNumberEntity,
    onDismiss: () -> Unit,
    onSave: (SerialTransferRequest) -> Unit
) {
    var plant by remember { mutableStateOf(serial.plant) }
    var storageLocation by remember { mutableStateOf(serial.storageLocation) }
    var stockType by remember { mutableStateOf(serial.stockType.ifBlank { "Unrestricted" }) }
    var batch by remember { mutableStateOf(serial.batch) }
    var note by remember { mutableStateOf("") }
    FormSheet("نقل ${serial.serialNumber}", onDismiss) {
        LabeledField("الموقع التشغيلي", plant, { plant = it })
        LabeledField("موقع التخزين", storageLocation, { storageLocation = it })
        OptionDropdown("نوع المخزون", serialStockTypeOptions, stockType, display = ::serialStockTypeLabel) { stockType = it }
        LabeledField("الدفعة", batch, { batch = it })
        LabeledField("ملاحظة", note, { note = it }, singleLine = false)
        SaveButton(storageLocation.isNotBlank()) {
            onSave(SerialTransferRequest(serial.id, plant, storageLocation, stockType, batch, note))
        }
    }
}

@Composable
internal fun SerialInstallFormSheet(
    serial: SerialNumberEntity,
    assets: List<AssetEntity>,
    onDismiss: () -> Unit,
    onSave: (SerialInstallRequest) -> Unit
) {
    var assetId by remember { mutableStateOf<Long?>(null) }
    var note by remember { mutableStateOf("") }
    val availableAssets = assets.filter { it.linkedSerialId == null || it.linkedSerialId == serial.id }
    FormSheet("تركيب ${serial.serialNumber}", onDismiss) {
        AssetDropdownOptional(availableAssets, assetId, { assetId = it }, label = "الأصل")
        LabeledField("ملاحظة", note, { note = it }, singleLine = false)
        SaveButton(assetId != null) {
            onSave(SerialInstallRequest(serial.id, assetId!!, note))
        }
    }
}
'''
    text = replace_once(text, "\n// ---------------------------------------------------------------------------\n// Work order form", serial_forms + "\n\n// ---------------------------------------------------------------------------\n// Work order form", "serial forms insertion")
save(path, text)

# -----------------------------------------------------------------------------
# Main application wiring
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/CmmsApp.kt")
if "import com.alhadi.cmms.data.entity.SerialNumberEntity" not in text:
    text = replace_once(
        text,
        "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity",
        "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity\nimport com.alhadi.cmms.data.entity.SerialNumberEntity\nimport com.alhadi.cmms.data.entity.SerialNumberMovementEntity\nimport com.alhadi.cmms.data.entity.SerialNumberProfileEntity",
        "CmmsApp serial imports",
    )
text = replace_once(
    text,
    "private enum class MoreRoute { Notifications, Inventory, Reports, Audit, Admin, PreventiveMaintenance, TaskLists, Meters, Locations, Capa, Failures }",
    "private enum class MoreRoute { Notifications, Inventory, SerialNumbers, Reports, Audit, Admin, PreventiveMaintenance, TaskLists, Meters, Locations, Capa, Failures }",
    "MoreRoute SerialNumbers",
)
if "val serialNumberProfiles by" not in text:
    text = replace_once(
        text,
        '''    val preventiveMaintenance by viewModel.preventiveMaintenance.collectAsStateWithLifecycle()
    val spareParts by viewModel.spareParts.collectAsStateWithLifecycle()''',
        '''    val preventiveMaintenance by viewModel.preventiveMaintenance.collectAsStateWithLifecycle()
    val serialNumberProfiles by viewModel.serialNumberProfiles.collectAsStateWithLifecycle()
    val serialNumbers by viewModel.serialNumbers.collectAsStateWithLifecycle()
    val serialNumberMovements by viewModel.serialNumberMovements.collectAsStateWithLifecycle()
    val spareParts by viewModel.spareParts.collectAsStateWithLifecycle()''',
        "CmmsApp serial state",
    )
if "MoreRoute.SerialNumbers -> ScreenMeta" not in text:
    text = replace_once(
        text,
        '''        MoreRoute.Inventory -> ScreenMeta("المخزون", "قطع الغيار والحركات", Icons.Filled.Inventory2, AccentPurple)''',
        '''        MoreRoute.Inventory -> ScreenMeta("المخزون", "قطع الغيار والحركات", Icons.Filled.Inventory2, AccentPurple)
        MoreRoute.SerialNumbers -> ScreenMeta("الأرقام التسلسلية", "تتبّع الوحدات والحركات والمواقع", Icons.Filled.QrCodeScanner, AccentTeal)''',
        "ScreenMeta SerialNumbers",
    )

# Main screen calls.
if "profiles = serialNumberProfiles" not in text[text.find("MoreRoute.Inventory -> InventoryScreen"):text.find("MoreRoute.Reports -> ReportsScreen")]:
    text = replace_once(
        text,
        '''                        MoreRoute.Inventory -> InventoryScreen(
                            innerPadding = innerPadding,
                            parts = spareParts,
                            transactions = transactions,
                            canReceive = canManage,
                            canManage = canManage,
                            onIssue = viewModel::issuePart,
                            onReceive = viewModel::receivePart,
                            onSave = viewModel::savePart,
                            onDelete = viewModel::deletePart
                        )''',
        '''                        MoreRoute.Inventory -> InventoryScreen(
                            innerPadding = innerPadding,
                            parts = spareParts,
                            profiles = serialNumberProfiles,
                            serials = serialNumbers,
                            transactions = transactions,
                            canReceive = canManage,
                            canManage = canManage,
                            onOpenSerialNumbers = { moreRoute = MoreRoute.SerialNumbers },
                            onIssue = viewModel::issuePart,
                            onReceive = viewModel::receivePart,
                            onSave = viewModel::savePart,
                            onDelete = viewModel::deletePart
                        )
                        MoreRoute.SerialNumbers -> SerialNumbersScreen(
                            innerPadding = innerPadding,
                            profiles = serialNumberProfiles,
                            serials = serialNumbers,
                            movements = serialNumberMovements,
                            parts = spareParts,
                            assets = assets,
                            workOrders = workOrders,
                            canManage = canManage,
                            onSaveProfile = viewModel::saveSerialProfile,
                            onDeleteProfile = viewModel::deleteSerialProfile,
                            onCreateMaster = viewModel::createSerialMaster,
                            onReceive = viewModel::receiveSerializedPart,
                            onIssue = viewModel::issueSerializedPart,
                            onTransfer = viewModel::transferSerialNumber,
                            onInstall = viewModel::installSerialNumber,
                            onDismantle = viewModel::dismantleSerialNumber,
                            onReconcile = viewModel::reconcileSerializedStock,
                            onDeleteSerial = viewModel::deleteSerialNumber
                        )''',
        "Inventory and SerialNumbers routes",
    )

if "serials = serialNumbers" not in text[text.find("BottomTab.Assets -> AssetsScreen"):text.find("BottomTab.More ->")]:
    text = replace_once(
        text,
        '''                        spareParts = spareParts,
                        canManage = canManage,''',
        '''                        spareParts = spareParts,
                        serials = serialNumbers,
                        serialMovements = serialNumberMovements,
                        canManage = canManage,''',
        "AssetsScreen serial arguments",
    )

# More grid card.
if 'ModuleCard("الأرقام التسلسلية"' not in text:
    text = replace_once(
        text,
        '''        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("المواقع الفنية", "هرمية المواقع", Icons.Filled.AccountTree, AccentGreen, Modifier.weight(1f)) { onOpen(MoreRoute.Locations) }''',
        '''        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("الأرقام التسلسلية", "تتبّع الوحدات والحركات", Icons.Filled.QrCodeScanner, AccentTeal, Modifier.weight(1f)) { onOpen(MoreRoute.SerialNumbers) }
                ModuleCard("المواقع الفنية", "هرمية المواقع", Icons.Filled.AccountTree, AccentGreen, Modifier.weight(1f)) { onOpen(MoreRoute.Locations) }
            }
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                ModuleCard("المواقع الفنية", "هرمية المواقع", Icons.Filled.AccountTree, AccentGreen, Modifier.weight(1f)) { onOpen(MoreRoute.Locations) }''',
        "MoreGrid serial module",
    )
    # Remove duplicated locations card from the following row and keep CAPA paired with a spacer.
    text = text.replace(
        '''                ModuleCard("المواقع الفنية", "هرمية المواقع", Icons.Filled.AccountTree, AccentGreen, Modifier.weight(1f)) { onOpen(MoreRoute.Locations) }
                ModuleCard("الإجراءات CAPA", "تصحيحية ووقائية", Icons.Filled.FactCheck, AccentOrange, Modifier.weight(1f)) { onOpen(MoreRoute.Capa) }''',
        '''                ModuleCard("الإجراءات CAPA", "تصحيحية ووقائية", Icons.Filled.FactCheck, AccentOrange, Modifier.weight(1f)) { onOpen(MoreRoute.Capa) }
                Spacer(modifier = Modifier.weight(1f))''',
        1,
    )

# AssetsScreen and detail signatures/data.
assets_start = text.find("private fun AssetsScreen(")
assets_end = text.find("private fun AssetCard", assets_start)
assets_segment = text[assets_start:assets_end]
if "serials: List<SerialNumberEntity>" not in assets_segment:
    assets_segment = assets_segment.replace(
        "    spareParts: List<SparePartEntity>,\n    canManage: Boolean,",
        "    spareParts: List<SparePartEntity>,\n    serials: List<SerialNumberEntity>,\n    serialMovements: List<SerialNumberMovementEntity>,\n    canManage: Boolean,",
        1,
    )
    detail_call_start = assets_segment.find("        AssetDetailScreen(")
    detail_call_end = assets_segment.find("\n        )\n        return", detail_call_start)
    detail_call = assets_segment[detail_call_start:detail_call_end]
    detail_call = detail_call.replace(
        "            spareParts = spareParts,",
        "            spareParts = spareParts,\n            serials = serials,\n            serialMovements = serialMovements,",
        1,
    )
    assets_segment = assets_segment[:detail_call_start] + detail_call + assets_segment[detail_call_end:]
    text = text[:assets_start] + assets_segment + text[assets_end:]

# AssetDetail signature and card.
detail_start = text.find("private fun AssetDetailScreen(")
detail_end = text.find(") {", detail_start)
detail_signature = text[detail_start:detail_end]
if "serials: List<SerialNumberEntity>" not in detail_signature:
    detail_signature = detail_signature.replace(
        "    spareParts: List<SparePartEntity>,\n    locations: List<FunctionalLocationEntity>,",
        "    spareParts: List<SparePartEntity>,\n    serials: List<SerialNumberEntity>,\n    serialMovements: List<SerialNumberMovementEntity>,\n    locations: List<FunctionalLocationEntity>,",
        1,
    )
    text = text[:detail_start] + detail_signature + text[detail_end:]
if "AssetSerialSection(" not in text:
    text = replace_once(
        text,
        '''        if (hasOrganization) {''',
        '''        item {
            AssetSerialSection(
                asset = asset,
                serials = serials,
                movements = serialMovements,
                parts = spareParts
            )
        }

        if (hasOrganization) {''',
        "asset serial detail section",
    )

# Inventory screen signature, card call, and form.
inv_start = text.find("private fun InventoryScreen(")
inv_end = text.find("private fun SparePartCard", inv_start)
inv_segment = text[inv_start:inv_end]
if "profiles: List<SerialNumberProfileEntity>" not in inv_segment:
    inv_segment = inv_segment.replace(
        "    parts: List<SparePartEntity>,\n    transactions: List<InventoryTransactionEntity>,",
        "    parts: List<SparePartEntity>,\n    profiles: List<SerialNumberProfileEntity>,\n    serials: List<SerialNumberEntity>,\n    transactions: List<InventoryTransactionEntity>,",
        1,
    ).replace(
        "    canManage: Boolean,\n    onIssue:",
        "    canManage: Boolean,\n    onOpenSerialNumbers: () -> Unit,\n    onIssue:",
        1,
    )
    inv_segment = inv_segment.replace(
        "                        part = part,\n                        canReceive = canReceive,",
        "                        part = part,\n                        profile = part.serialProfileId?.let { id -> profiles.firstOrNull { it.id == id } },\n                        serials = serials.filter { it.partId == part.id },\n                        canReceive = canReceive,",
        1,
    ).replace(
        "                        onDelete = { deleteTarget = part }",
        "                        onOpenSerialNumbers = onOpenSerialNumbers,\n                        onDelete = { deleteTarget = part }",
        1,
    )
    inv_segment = inv_segment.replace(
        "PartFormSheet(initial = editing, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })",
        "PartFormSheet(initial = editing, profiles = profiles, onDismiss = { showForm = false }, onSave = { onSave(it); showForm = false })",
        1,
    )
    text = text[:inv_start] + inv_segment + text[inv_end:]

# SparePartCard signature and serialized behavior.
spare_start = text.find("private fun SparePartCard(")
spare_end = text.find("private fun QuantityDialog", spare_start)
spare_segment = text[spare_start:spare_end]
if "profile: SerialNumberProfileEntity?" not in spare_segment:
    spare_segment = spare_segment.replace(
        "    part: SparePartEntity,\n    canReceive: Boolean,",
        "    part: SparePartEntity,\n    profile: SerialNumberProfileEntity?,\n    serials: List<SerialNumberEntity>,\n    canReceive: Boolean,",
        1,
    ).replace(
        "    onEdit: () -> Unit,\n    onDelete: () -> Unit",
        "    onEdit: () -> Unit,\n    onOpenSerialNumbers: () -> Unit,\n    onDelete: () -> Unit",
        1,
    )
    spare_segment = spare_segment.replace(
        "    val lowStock = part.onHandQty <= part.minQty",
        "    val lowStock = part.onHandQty <= part.minQty\n    val serialInStock = serials.count { it.status == \"InStock\" }",
        1,
    )
    spare_segment = spare_segment.replace(
        '''            InfoRow("قيمة المخزون", money(part.onHandQty * part.lastPrice))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = { moveMode = "issue" }, enabled = part.onHandQty > 0, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("صرف")
                }
                if (canReceive) {
                    Button(onClick = { moveMode = "receive" }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("استلام")
                    }
                }
            }''',
        '''            InfoRow("قيمة المخزون", money(part.onHandQty * part.lastPrice))
            if (part.serializationActive) {
                InfoRow("ملف التتبع", profile?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                InfoRow("الوحدات المتسلسلة في المخزون", serialInStock.toString())
                if (serialInStock != part.onHandQty) {
                    StatusBadge("اختلاف بين الكمية والوحدات", statusTone("stopped"))
                }
                Button(onClick = onOpenSerialNumbers, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إدارة الأرقام التسلسلية")
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { moveMode = "issue" }, enabled = part.onHandQty > 0, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("صرف")
                    }
                    if (canReceive) {
                        Button(onClick = { moveMode = "receive" }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استلام")
                        }
                    }
                }
            }''',
        1,
    )
    spare_segment = spare_segment.replace("    moveMode?.let { mode ->", "    if (!part.serializationActive) moveMode?.let { mode ->", 1)
    text = text[:spare_start] + spare_segment + text[spare_end:]

# Show serial numbers in inventory transaction cards.
if 'transaction.serialNumbers.isNotBlank()' not in text:
    text = replace_once(
        text,
        '''                Text("${transaction.createdAt} • ${transaction.createdBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }''',
        '''                Text("${transaction.createdAt} • ${transaction.createdBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (transaction.serialNumbers.isNotBlank()) {
                    Text("الأرقام: ${transaction.serialNumbers}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }''',
        "transaction serial numbers",
    )

# Prevent generic issue flow from bypassing serialization.
material_start = text.find("private fun MaterialPickerSheet(")
material_end = text.find("/** Arabic label for a permit type.", material_start)
material_segment = text[material_start:material_end]
if "part.serializationActive" not in material_segment:
    material_segment = material_segment.replace(
        '''            ElevatedCard(
                modifier = Modifier.fillMaxWidth().clickable { onPick(part) },''',
        '''            ElevatedCard(
                modifier = Modifier.fillMaxWidth().then(if (part.serializationActive) Modifier else Modifier.clickable { onPick(part) }),''',
        1,
    ).replace(
        '''                    if (part.id in bomPartIds) StatusBadge("موصى بها", statusTone("info"))
                    StatusBadge("متوفر ${part.onHandQty}", statusTone("running"))''',
        '''                    if (part.serializationActive) {
                        StatusBadge("صرف تسلسلي فقط", statusTone("scheduled"))
                    } else {
                        if (part.id in bomPartIds) StatusBadge("موصى بها", statusTone("info"))
                        StatusBadge("متوفر ${part.onHandQty}", statusTone("running"))
                    }''',
        1,
    )
    text = text[:material_start] + material_segment + text[material_end:]

save(path, text)

print("Serial number management UI stage 5 patch completed successfully.")

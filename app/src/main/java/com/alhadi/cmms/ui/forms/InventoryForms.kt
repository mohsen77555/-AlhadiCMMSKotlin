package com.alhadi.cmms.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.MovementType
import com.alhadi.cmms.data.SerialInstallRequest
import com.alhadi.cmms.data.SerialMasterRequest
import com.alhadi.cmms.data.SerialTransferRequest
import com.alhadi.cmms.data.SerializedIssueRequest
import com.alhadi.cmms.data.SerializedReceiptRequest
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.util.DateStrings

// ---------------------------------------------------------------------------
// Spare part form
// ---------------------------------------------------------------------------

@Composable
internal fun PartFormSheet(
    initial: SparePartEntity?,
    profiles: List<SerialNumberProfileEntity>,
    warehouses: List<WarehouseEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (SparePartEntity) -> Unit
) {
    var partNumber by remember { mutableStateOf(initial?.partNumber ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var group by remember { mutableStateOf(initial?.equipmentGroup ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "pcs") }
    var onHand by remember { mutableStateOf((initial?.onHandQty ?: 0).toString()) }
    var minQty by remember { mutableStateOf((initial?.minQty ?: 0).toString()) }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var price by remember { mutableStateOf((initial?.lastPrice ?: 0.0).toString()) }
    var maxQty by remember { mutableStateOf((initial?.maxQty ?: 0).toString()) }
    var reorderQty by remember { mutableStateOf((initial?.reorderQty ?: 0).toString()) }
    var safetyStock by remember { mutableStateOf((initial?.safetyStock ?: 0).toString()) }
    var leadTimeDays by remember { mutableStateOf((initial?.leadTimeDays ?: 0).toString()) }
    var abcClass by remember { mutableStateOf(initial?.abcClass ?: "") }
    var serializationActive by remember { mutableStateOf(initial?.serializationActive ?: false) }
    var serialProfileId by remember { mutableStateOf(initial?.serialProfileId) }

    FormSheet(if (initial == null) "إضافة قطعة غيار" else "تعديل القطعة", onDismiss) {
        LabeledField("رقم القطعة (Part No.)", partNumber, { partNumber = it })
        LabeledField("الاسم (Name)", name, { name = it })
        LabeledField("مجموعة المعدة", group, { group = it })
        LabeledField("الوحدة (Unit)", unit, { unit = it })
        LabeledField("الكمية المتوفرة", onHand, { onHand = it }, numeric = true)
        LabeledField("الحد الأدنى", minQty, { minQty = it }, numeric = true)
        if (warehouses.isEmpty()) {
            LabeledField("الموقع / المستودع", location, { location = it })
        } else {
            val activeWarehouses = warehouses.filter { it.status.equals("Active", ignoreCase = true) }
            val codes = activeWarehouses.map { it.code }
            // Keep any legacy/free-text value selectable so editing an old part never loses it.
            val options = if (location.isNotBlank() && location !in codes) listOf(location) + codes else codes
            OptionDropdown(
                label = "المستودع",
                options = options,
                selected = location,
                display = { code -> warehouses.firstOrNull { it.code == code }?.let { "${it.code} — ${it.name}" } ?: code }
            ) { location = it }
        }
        LabeledField("آخر سعر", price, { price = it }, numeric = true)

        Text("سياسة إعادة الطلب (الحوكمة)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("الحد الأقصى", maxQty, { maxQty = it }, numeric = true)
        LabeledField("كمية إعادة الطلب", reorderQty, { reorderQty = it }, numeric = true)
        LabeledField("مخزون الأمان", safetyStock, { safetyStock = it }, numeric = true)
        LabeledField("مهلة التوريد (أيام)", leadTimeDays, { leadTimeDays = it }, numeric = true)
        OptionDropdown("تصنيف ABC", listOf("", "A", "B", "C"), abcClass) { abcClass = it }

        Text("التتبع الفردي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("تفعيل الأرقام التسلسلية", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = serializationActive, onCheckedChange = { serializationActive = it })
        }
        if (serializationActive) {
            SerialProfileDropdown(profiles, serialProfileId) { serialProfileId = it }
            if (profiles.isEmpty()) Text("أنشئ ملف تتبع من وحدة الأرقام التسلسلية أولاً.", color = MaterialTheme.colorScheme.error)
        }
        SaveButton(partNumber.isNotBlank() && name.isNotBlank() && (!serializationActive || serialProfileId != null)) {
            onSave(
                SparePartEntity(
                    id = initial?.id ?: 0,
                    partNumber = partNumber.trim(),
                    name = name.trim(),
                    equipmentGroup = group.ifBlank { "General" },
                    unit = unit.ifBlank { "pcs" },
                    onHandQty = onHand.toIntOrNull() ?: 0,
                    minQty = minQty.toIntOrNull() ?: 0,
                    location = location,
                    lastPrice = price.toDoubleOrNull() ?: 0.0,
                    serializationActive = serializationActive,
                    serialProfileId = if (serializationActive) serialProfileId else null,
                    maxQty = maxQty.toIntOrNull() ?: 0,
                    reorderQty = reorderQty.toIntOrNull() ?: 0,
                    safetyStock = safetyStock.toIntOrNull() ?: 0,
                    leadTimeDays = leadTimeDays.toIntOrNull() ?: 0,
                    abcClass = abcClass,
                    preferredSupplierId = initial?.preferredSupplierId
                )
            )
        }
    }
}


@Composable
internal fun SerialProfileDropdown(
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
internal fun WorkOrderDropdownOptional(
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



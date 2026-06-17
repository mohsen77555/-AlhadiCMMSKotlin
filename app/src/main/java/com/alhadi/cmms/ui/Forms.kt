package com.alhadi.cmms.ui

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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.util.DateStrings

// ---------------------------------------------------------------------------
// Reusable building blocks
// ---------------------------------------------------------------------------

@Composable
internal fun ConfirmDialog(
    title: String,
    text: String,
    confirmLabel: String = "حذف",
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title, fontWeight = FontWeight.Bold) },
        text = { Text(text) },
        confirmButton = { TextButton(onClick = onConfirm) { Text(confirmLabel, color = MaterialTheme.colorScheme.error) } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

/** Lets the user pick a new lifecycle status for an asset. */
@Composable
internal fun StatusPickerDialog(
    current: String,
    options: List<String>,
    onPick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تغيير حالة الأصل", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                options.forEach { opt ->
                    TextButton(onClick = { onPick(opt) }, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            (if (opt == current) "• " else "") + opt,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = if (opt == current) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    numeric: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = Modifier.fillMaxWidth()
    )
}

@Composable
private fun OptionDropdown(label: String, options: List<String>, selected: String, onSelect: (String) -> Unit) {
    var open by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected.ifBlank { "اختر…" }, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(text = { Text(opt) }, onClick = { onSelect(opt); open = false })
                }
            }
        }
    }
}

@Composable
private fun AssetDropdown(assets: List<AssetEntity>, selectedId: Long, onSelect: (Long) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val selected = assets.firstOrNull { it.id == selectedId }
    Column {
        Text("الأصل", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    selected?.let { "${it.code} • ${it.name}" } ?: "اختر أصلاً",
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                assets.forEach { asset ->
                    DropdownMenuItem(text = { Text("${asset.code} • ${asset.name}") }, onClick = { onSelect(asset.id); open = false })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FormSheet(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(onDismissRequest = onDismiss, sheetState = sheetState) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(start = 20.dp, end = 20.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            content()
        }
    }
}

@Composable
private fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Text("حفظ", style = MaterialTheme.typography.titleMedium)
    }
}

// ---------------------------------------------------------------------------
// Asset form
// ---------------------------------------------------------------------------

@Composable
internal fun AssetFormSheet(initial: AssetEntity?, onDismiss: () -> Unit, onSave: (AssetEntity) -> Unit) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var group by remember { mutableStateOf(initial?.groupName ?: "") }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var manufacturer by remember { mutableStateOf(initial?.manufacturer ?: "") }
    var model by remember { mutableStateOf(initial?.model ?: "") }
    var status by remember { mutableStateOf(initial?.status ?: "Running") }
    var criticality by remember { mutableStateOf(initial?.criticality ?: "Medium") }

    FormSheet(if (initial == null) "إضافة أصل جديد" else "تعديل الأصل", onDismiss) {
        LabeledField("الكود (Code)", code, { code = it })
        LabeledField("الاسم (Name)", name, { name = it })
        LabeledField("المجموعة (Group)", group, { group = it })
        LabeledField("الموقع (Location)", location, { location = it })
        LabeledField("الشركة المصنّعة", manufacturer, { manufacturer = it })
        LabeledField("الموديل (Model)", model, { model = it })
        OptionDropdown("الحالة", listOf("Running", "Warning", "Stopped"), status) { status = it }
        OptionDropdown("الأهمية", listOf("Low", "Medium", "High", "Critical"), criticality) { criticality = it }
        SaveButton(code.isNotBlank() && name.isNotBlank()) {
            val today = DateStrings.today()
            onSave(
                AssetEntity(
                    id = initial?.id ?: 0,
                    code = code.trim(),
                    name = name.trim(),
                    groupName = group.ifBlank { "General" },
                    location = location,
                    manufacturer = manufacturer,
                    model = model,
                    status = status,
                    criticality = criticality,
                    installedAt = initial?.installedAt ?: today,
                    lastInspectionAt = initial?.lastInspectionAt ?: today
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Spare part form
// ---------------------------------------------------------------------------

@Composable
internal fun PartFormSheet(initial: SparePartEntity?, onDismiss: () -> Unit, onSave: (SparePartEntity) -> Unit) {
    var partNumber by remember { mutableStateOf(initial?.partNumber ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var group by remember { mutableStateOf(initial?.equipmentGroup ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "pcs") }
    var onHand by remember { mutableStateOf((initial?.onHandQty ?: 0).toString()) }
    var minQty by remember { mutableStateOf((initial?.minQty ?: 0).toString()) }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var price by remember { mutableStateOf((initial?.lastPrice ?: 0.0).toString()) }

    FormSheet(if (initial == null) "إضافة قطعة غيار" else "تعديل القطعة", onDismiss) {
        LabeledField("رقم القطعة (Part No.)", partNumber, { partNumber = it })
        LabeledField("الاسم (Name)", name, { name = it })
        LabeledField("مجموعة المعدة", group, { group = it })
        LabeledField("الوحدة (Unit)", unit, { unit = it })
        LabeledField("الكمية المتوفرة", onHand, { onHand = it }, numeric = true)
        LabeledField("الحد الأدنى", minQty, { minQty = it }, numeric = true)
        LabeledField("الموقع (Location)", location, { location = it })
        LabeledField("آخر سعر", price, { price = it }, numeric = true)
        SaveButton(partNumber.isNotBlank() && name.isNotBlank()) {
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
                    lastPrice = price.toDoubleOrNull() ?: 0.0
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Work order form (create + edit)
// ---------------------------------------------------------------------------

@Composable
internal fun WorkOrderFormSheet(
    initial: WorkOrderEntity?,
    assets: List<AssetEntity>,
    defaultAssignee: String,
    onDismiss: () -> Unit,
    onSave: (WorkOrderEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var assetId by remember { mutableStateOf(initial?.assetId ?: assets.firstOrNull()?.id ?: 0L) }
    var priority by remember { mutableStateOf(initial?.priority ?: "Medium") }
    var status by remember { mutableStateOf(initial?.status ?: "Open") }
    var assignedTo by remember { mutableStateOf(initial?.assignedTo ?: defaultAssignee) }
    var cost by remember { mutableStateOf((initial?.estimatedCost ?: 0.0).toString()) }
    var dueDays by remember { mutableStateOf("3") }

    FormSheet(if (initial == null) "إنشاء أمر عمل" else "تعديل أمر العمل", onDismiss) {
        LabeledField("العنوان", title, { title = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        AssetDropdown(assets, assetId) { assetId = it }
        OptionDropdown("الأولوية", listOf("Low", "Medium", "High", "Critical"), priority) { priority = it }
        OptionDropdown("الحالة", listOf("Open", "In Progress", "Closed"), status) { status = it }
        LabeledField("المسؤول", assignedTo, { assignedTo = it })
        LabeledField("التكلفة التقديرية", cost, { cost = it }, numeric = true)
        if (initial == null) {
            LabeledField("الاستحقاق خلال (أيام)", dueDays, { dueDays = it }, numeric = true)
        }
        SaveButton(title.isNotBlank() && assetId != 0L) {
            val today = DateStrings.today()
            val due = initial?.dueAt ?: DateStrings.daysFromToday(dueDays.toIntOrNull() ?: 3)
            onSave(
                WorkOrderEntity(
                    id = initial?.id ?: 0,
                    assetId = assetId,
                    title = title.trim(),
                    description = description,
                    priority = priority,
                    status = status,
                    assignedTo = assignedTo.ifBlank { defaultAssignee },
                    createdAt = initial?.createdAt ?: today,
                    dueAt = due,
                    estimatedCost = cost.toDoubleOrNull() ?: 0.0,
                    closeNotes = initial?.closeNotes ?: ""
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Preventive maintenance form
// ---------------------------------------------------------------------------

@Composable
internal fun PmFormSheet(
    initial: PreventiveMaintenanceEntity?,
    assets: List<AssetEntity>,
    onDismiss: () -> Unit,
    onSave: (PreventiveMaintenanceEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var assetId by remember { mutableStateOf(initial?.assetId ?: assets.firstOrNull()?.id ?: 0L) }
    var frequency by remember { mutableStateOf((initial?.frequencyDays ?: 30).toString()) }
    var duration by remember { mutableStateOf((initial?.estimatedDurationMinutes ?: 60).toString()) }

    FormSheet(if (initial == null) "إضافة صيانة دورية" else "تعديل الصيانة الدورية", onDismiss) {
        LabeledField("عنوان المهمة", title, { title = it })
        AssetDropdown(assets, assetId) { assetId = it }
        LabeledField("التكرار (أيام)", frequency, { frequency = it }, numeric = true)
        LabeledField("المدة المقدرة (دقائق)", duration, { duration = it }, numeric = true)
        SaveButton(title.isNotBlank() && assetId != 0L) {
            val today = DateStrings.today()
            val freq = frequency.toIntOrNull() ?: 30
            onSave(
                PreventiveMaintenanceEntity(
                    id = initial?.id ?: 0,
                    assetId = assetId,
                    title = title.trim(),
                    frequencyDays = freq,
                    lastDoneAt = initial?.lastDoneAt ?: today,
                    nextDueAt = initial?.nextDueAt ?: DateStrings.daysFromToday(freq),
                    status = initial?.status ?: "Scheduled",
                    estimatedDurationMinutes = duration.toIntOrNull() ?: 60
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// User form
// ---------------------------------------------------------------------------

@Composable
internal fun UserFormSheet(initial: UserEntity?, onDismiss: () -> Unit, onSave: (UserEntity) -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var username by remember { mutableStateOf(initial?.username ?: "") }
    var role by remember { mutableStateOf(initial?.role ?: "Technician") }
    var password by remember { mutableStateOf(initial?.password ?: "1234") }
    var active by remember { mutableStateOf(initial?.isActive ?: true) }

    FormSheet(if (initial == null) "إضافة مستخدم" else "تعديل المستخدم", onDismiss) {
        LabeledField("الاسم", name, { name = it })
        LabeledField("اسم المستخدم", username, { username = it })
        OptionDropdown("الدور", listOf("Admin", "Supervisor", "Technician"), role) { role = it }
        LabeledField("كلمة المرور", password, { password = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("مفعّل", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = active, onCheckedChange = { active = it })
        }
        SaveButton(name.isNotBlank() && username.isNotBlank()) {
            onSave(
                UserEntity(
                    id = initial?.id ?: 0,
                    name = name.trim(),
                    username = username.trim(),
                    role = role,
                    isActive = active,
                    password = password.ifBlank { "1234" }
                )
            )
        }
    }
}

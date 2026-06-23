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
    isAdmin: Boolean = false,
    onPick: (String, String) -> Unit,
    onDismiss: () -> Unit
) {
    // States that require a reason (Change Status rule) and those that need approval (Retire/Dispose).
    val reasonStates = setOf("Stopped", "Retired", "Disposed")
    val approvalStates = setOf("Retired", "Disposed")
    var selected by remember { mutableStateOf<String?>(null) }
    var reason by remember { mutableStateOf("") }

    val target = selected
    if (target == null) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("تغيير حالة الأصل", fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    options.forEach { opt ->
                        val needsApproval = opt in approvalStates
                        val blocked = needsApproval && !isAdmin
                        TextButton(onClick = { if (!blocked) selected = opt }, enabled = !blocked, modifier = Modifier.fillMaxWidth()) {
                            Text(
                                (if (opt == current) "• " else "") + opt +
                                    (if (needsApproval) " (يتطلب اعتماد)" else ""),
                                modifier = Modifier.fillMaxWidth(),
                                fontWeight = if (opt == current) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                    if (!isAdmin) {
                        Text("الإيقاف النهائي/الاستبعاد (تقاعد/استبعاد) يتطلب صلاحية اعتماد.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
        )
    } else {
        val needsReason = target in reasonStates
        AlertDialog(
            onDismissRequest = { selected = null },
            title = { Text("تأكيد: $target", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (target in approvalStates) {
                        Text("هذا الإجراء يتطلب اعتماداً وسبباً.", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    if (needsReason) {
                        LabeledField("السبب", reason, { reason = it }, singleLine = false)
                    } else {
                        Text("تأكيد تغيير الحالة إلى $target؟")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { onPick(target, reason.trim()) },
                    enabled = !needsReason || reason.isNotBlank()
                ) { Text("تأكيد") }
            },
            dismissButton = { TextButton(onClick = { selected = null }) { Text("رجوع") } }
        )
    }
}

@Composable
internal fun LabeledField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    numeric: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        enabled = enabled,
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = Modifier.fillMaxWidth()
    )
}

/** A read-only date field (YYYY-MM-DD) backed by a Material date picker instead of manual typing. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DateField(label: String, value: String, onChange: (String) -> Unit) {
    var showPicker by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        singleLine = true,
        trailingIcon = {
            IconButton(onClick = { showPicker = true }) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = "اختيار التاريخ")
            }
        },
        modifier = Modifier.fillMaxWidth()
    )
    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = parseDateMillis(value))
        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { onChange(formatDateMillis(it)) }
                    showPicker = false
                }) { Text("تأكيد") }
            },
            dismissButton = { TextButton(onClick = { showPicker = false }) { Text("إلغاء") } }
        ) {
            DatePicker(state = state)
        }
    }
}

internal fun dateFormatterUtc() =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

internal fun parseDateMillis(date: String): Long? =
    runCatching { dateFormatterUtc().parse(date)?.time }.getOrNull()

internal fun formatDateMillis(millis: Long): String = dateFormatterUtc().format(Date(millis))

@Composable
internal fun OptionDropdown(
    label: String,
    options: List<String>,
    selected: String,
    display: (String) -> String = { it },
    onSelect: (String) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected.ifBlank { "اختر…" }.let(display), modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                options.forEach { opt ->
                    DropdownMenuItem(text = { Text(display(opt)) }, onClick = { onSelect(opt); open = false })
                }
            }
        }
    }
}

@Composable
internal fun AssetDropdown(assets: List<AssetEntity>, selectedId: Long, onSelect: (Long) -> Unit) {
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

/** Optional functional-location picker (with a "none" option). */
@Composable
internal fun LocationDropdown(
    label: String,
    locations: List<FunctionalLocationEntity>,
    selectedId: Long?,
    excludeId: Long? = null,
    onSelect: (Long?) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val selected = locations.firstOrNull { it.id == selectedId }
    val options = locations.filter { it.id != excludeId }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    selected?.let { "${it.code} • ${it.name}" } ?: "بدون",
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                DropdownMenuItem(text = { Text("بدون") }, onClick = { onSelect(null); open = false })
                options.forEach { loc ->
                    DropdownMenuItem(text = { Text("${loc.code} • ${loc.name}") }, onClick = { onSelect(loc.id); open = false })
                }
            }
        }
    }
}

/** Optional asset picker (with a "none" option). */
@Composable
internal fun AssetDropdownOptional(
    assets: List<AssetEntity>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit,
    label: String = "الأصل (اختياري)",
    excludeId: Long? = null
) {
    var open by remember { mutableStateOf(false) }
    val options = assets.filter { it.id != excludeId }
    val selected = assets.firstOrNull { it.id == selectedId }
    Column {
        Text(label, style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    selected?.let { "${it.code} • ${it.name}" } ?: "بدون",
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                DropdownMenuItem(text = { Text("بدون") }, onClick = { onSelect(null); open = false })
                options.forEach { asset ->
                    DropdownMenuItem(text = { Text("${asset.code} • ${asset.name}") }, onClick = { onSelect(asset.id); open = false })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FormSheet(title: String, onDismiss: () -> Unit, content: @Composable () -> Unit) {
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
internal fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
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


@Composable
internal fun LinearMaintenancePositionFields(
    asset: AssetEntity,
    startPoint: String,
    onStartPointChange: (String) -> Unit,
    endPoint: String,
    onEndPointChange: (String) -> Unit,
    marker: String,
    onMarkerChange: (String) -> Unit,
    horizontalOffset: String,
    onHorizontalOffsetChange: (String) -> Unit,
    verticalOffset: String,
    onVerticalOffsetChange: (String) -> Unit
) {
    Text("الموقع على الأصل الخطي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Text(
        "النطاق المتاح: ${linearRangeLabel(asset)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f)) {
            LabeledField("نقطة البداية (${asset.linearUnit})", startPoint, onStartPointChange, numeric = true)
        }
        Box(modifier = Modifier.weight(1f)) {
            LabeledField("نقطة النهاية (${asset.linearUnit})", endPoint, onEndPointChange, numeric = true)
        }
    }
    LabeledField("العلامة المرجعية (اختياري)", marker, onMarkerChange)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f)) {
            LabeledField("إزاحة أفقية (${asset.linearOffsetUnit})", horizontalOffset, onHorizontalOffsetChange, numeric = true)
        }
        Box(modifier = Modifier.weight(1f)) {
            LabeledField("إزاحة رأسية (${asset.linearOffsetUnit})", verticalOffset, onVerticalOffsetChange, numeric = true)
        }
    }
    if (!optionalLinearRangeValid(asset, startPoint, endPoint)) {
        Text("يجب أن يكون النطاق داخل حدود الأصل وأن تكون النهاية أكبر من أو مساوية للبداية.", color = MaterialTheme.colorScheme.error)
    }
}


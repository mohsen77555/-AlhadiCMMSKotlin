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
// Preventive maintenance form
// ---------------------------------------------------------------------------

@Composable
internal fun PmFormSheet(
    initial: PreventiveMaintenanceEntity?,
    assets: List<AssetEntity>,
    taskLists: List<TaskListEntity>,
    measuringPoints: List<MeasuringPointEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (PreventiveMaintenanceEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var assetId by remember { mutableStateOf(initial?.assetId ?: assets.firstOrNull()?.id ?: 0L) }
    var frequency by remember { mutableStateOf((initial?.frequencyDays ?: 30).toString()) }
    var duration by remember { mutableStateOf((initial?.estimatedDurationMinutes ?: 60).toString()) }
    var taskListId by remember { mutableStateOf(initial?.taskListId) }
    var scheduleType by remember { mutableStateOf(initial?.scheduleType ?: "Time") }
    var measuringPointId by remember { mutableStateOf(initial?.measuringPointId) }
    var counterInterval by remember { mutableStateOf((initial?.counterInterval ?: 0.0).toString()) }
    var callHorizonDays by remember { mutableStateOf((initial?.callHorizonDays ?: 0).toString()) }
    var priority by remember { mutableStateOf(initial?.priority ?: "Medium") }
    var floatingSchedule by remember { mutableStateOf(initial?.floatingSchedule ?: true) }
    var planActive by remember { mutableStateOf(initial?.planActive ?: true) }
    var strategy by remember { mutableStateOf(initial?.strategy ?: "") }
    val counterScheduled = scheduleType == "Counter" || scheduleType == "Both"
    val timeScheduled = scheduleType == "Time" || scheduleType == "Both"
    val assetPoints = measuringPoints.filter { it.assetId == assetId }

    FormSheet(if (initial == null) "إضافة صيانة دورية" else "تعديل الصيانة الدورية", onDismiss) {
        LabeledField("عنوان المهمة", title, { title = it })
        AssetDropdown(assets, assetId) { assetId = it }
        LabeledField("المدة المقدرة (دقائق)", duration, { duration = it }, numeric = true)
        if (taskLists.isNotEmpty()) {
            TaskListDropdown(taskLists, taskListId) { taskListId = it }
        }

        Text("أساس الجدولة (الحوكمة)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OptionDropdown("نوع الجدولة", listOf("Time", "Counter", "Both"), scheduleType) { scheduleType = it }
        if (timeScheduled) {
            LabeledField("التكرار (أيام)", frequency, { frequency = it }, numeric = true)
        }
        if (counterScheduled) {
            if (assetPoints.isEmpty()) {
                Text("لا توجد نقاط قياس لهذا الأصل. أضِف عدّاداً أولاً.", color = MaterialTheme.colorScheme.error)
            } else {
                OptionDropdown(
                    label = "العدّاد (نقطة القياس)",
                    options = assetPoints.map { it.id.toString() },
                    selected = measuringPointId?.toString() ?: "",
                    display = { idStr -> assetPoints.firstOrNull { it.id.toString() == idStr }?.let { "${it.name} (${it.lastReading} ${it.unit})" } ?: idStr }
                ) { measuringPointId = it.toLongOrNull() }
            }
            LabeledField("فاصل العدّاد (كل كم وحدة)", counterInterval, { counterInterval = it }, numeric = true)
        }
        OptionDropdown("الأولوية", listOf("Low", "Medium", "High", "Critical"), priority) { priority = it }
        LabeledField("أفق الاستدعاء (أيام قبل الاستحقاق)", callHorizonDays, { callHorizonDays = it }, numeric = true)
        LabeledField("الاستراتيجية", strategy, { strategy = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("جدولة عائمة (من تاريخ التنفيذ)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = floatingSchedule, onCheckedChange = { floatingSchedule = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("الخطة فعّالة", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = planActive, onCheckedChange = { planActive = it })
        }

        val counterValid = !counterScheduled || (measuringPointId != null && (counterInterval.toDoubleOrNull() ?: 0.0) > 0)
        SaveButton(title.isNotBlank() && assetId != 0L && counterValid) {
            val today = DateStrings.today()
            val freq = frequency.toIntOrNull() ?: 30
            val interval = counterInterval.toDoubleOrNull() ?: 0.0
            val basePoint = assetPoints.firstOrNull { it.id == measuringPointId }
            // Project the first counter trigger from the current reading when newly counter-scheduled.
            val nextCounter = when {
                !counterScheduled || interval <= 0 -> initial?.nextCounterReading ?: 0.0
                (initial?.nextCounterReading ?: 0.0) > 0 -> initial!!.nextCounterReading
                else -> (basePoint?.lastReading ?: 0.0) + interval
            }
            onSave(
                PreventiveMaintenanceEntity(
                    id = initial?.id ?: 0,
                    assetId = assetId,
                    title = title.trim(),
                    frequencyDays = freq,
                    lastDoneAt = initial?.lastDoneAt ?: today,
                    nextDueAt = initial?.nextDueAt ?: DateStrings.daysFromToday(freq),
                    status = initial?.status ?: "Scheduled",
                    estimatedDurationMinutes = duration.toIntOrNull() ?: 60,
                    taskListId = taskListId,
                    scheduleType = scheduleType,
                    measuringPointId = if (counterScheduled) measuringPointId else null,
                    counterInterval = interval,
                    lastCounterReading = initial?.lastCounterReading ?: 0.0,
                    nextCounterReading = nextCounter,
                    callHorizonDays = callHorizonDays.toIntOrNull() ?: 0,
                    priority = priority,
                    floatingSchedule = floatingSchedule,
                    planActive = planActive,
                    strategy = strategy.trim()
                )
            )
        }
    }
}

@Composable
internal fun TaskListDropdown(taskLists: List<TaskListEntity>, selectedId: Long?, onSelect: (Long?) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val selected = taskLists.firstOrNull { it.id == selectedId }
    Column {
        Text("قالب العمل (اختياري)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.name ?: "بدون", modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                DropdownMenuItem(text = { Text("بدون") }, onClick = { onSelect(null); open = false })
                taskLists.forEach { tl ->
                    DropdownMenuItem(text = { Text(tl.name) }, onClick = { onSelect(tl.id); open = false })
                }
            }
        }
    }
}

// ---------------------------------------------------------------------------
// Task list forms (قوالب العمل)
// ---------------------------------------------------------------------------

@Composable
internal fun TaskListFormSheet(
    initial: TaskListEntity?,
    onDismiss: () -> Unit,
    onSave: (TaskListEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var workCenter by remember { mutableStateOf(initial?.defaultWorkCenter ?: "Mechanical") }

    FormSheet(if (initial == null) "قالب عمل جديد" else "تعديل القالب", onDismiss) {
        LabeledField("اسم القالب", name, { name = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        OptionDropdown("مركز العمل الافتراضي", listOf("Mechanical", "Electrical", "Instrumentation", "Civil", "External"), workCenter) { workCenter = it }
        SaveButton(name.isNotBlank()) {
            onSave(
                TaskListEntity(
                    id = initial?.id ?: 0,
                    name = name.trim(),
                    description = description.trim(),
                    defaultWorkCenter = workCenter
                )
            )
        }
    }
}

@Composable
internal fun TaskListOperationFormSheet(
    taskListId: Long,
    defaultWorkCenter: String,
    nextNumber: String,
    onDismiss: () -> Unit,
    onSave: (TaskListOperationEntity) -> Unit
) {
    var operationNumber by remember { mutableStateOf(nextNumber) }
    var description by remember { mutableStateOf("") }
    var workCenter by remember { mutableStateOf(defaultWorkCenter.ifBlank { "Mechanical" }) }
    var plannedHours by remember { mutableStateOf("1") }

    FormSheet("إضافة عملية للقالب", onDismiss) {
        LabeledField("رقم العملية", operationNumber, { operationNumber = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        OptionDropdown("مركز العمل", listOf("Mechanical", "Electrical", "Instrumentation", "Civil", "External"), workCenter) { workCenter = it }
        LabeledField("الساعات المخططة", plannedHours, { plannedHours = it }, numeric = true)
        SaveButton(description.isNotBlank()) {
            onSave(
                TaskListOperationEntity(
                    id = 0,
                    taskListId = taskListId,
                    operationNumber = operationNumber.trim().ifBlank { nextNumber },
                    description = description.trim(),
                    workCenter = workCenter,
                    plannedHours = plannedHours.toDoubleOrNull() ?: 0.0
                )
            )
        }
    }
}


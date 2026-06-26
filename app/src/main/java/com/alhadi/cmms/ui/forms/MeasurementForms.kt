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
// User form
// ---------------------------------------------------------------------------

// ---------------------------------------------------------------------------
// Measuring point form + reading dialog
// ---------------------------------------------------------------------------

@Composable
internal fun MeterFormSheet(
    initial: MeasuringPointEntity?,
    assets: List<AssetEntity>,
    onDismiss: () -> Unit,
    onSave: (MeasuringPointEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "") }
    var assetId by remember { mutableStateOf(initial?.assetId ?: assets.firstOrNull()?.id ?: 0L) }
    var isCounter by remember { mutableStateOf(initial?.isCounter ?: false) }
    var limit by remember { mutableStateOf(initial?.upperLimit?.toString() ?: "") }
    var lowerLimit by remember { mutableStateOf(initial?.lowerLimit?.toString() ?: "") }
    var warningMargin by remember { mutableStateOf((initial?.warningMargin ?: 0.0).toString()) }
    var autoNotifyOnAlarm by remember { mutableStateOf(initial?.autoNotifyOnAlarm ?: false) }

    FormSheet(if (initial == null) "إضافة نقطة قياس" else "تعديل نقطة القياس", onDismiss) {
        LabeledField("اسم النقطة", name, { name = it })
        LabeledField("الوحدة (hr / °C / mm/s ...)", unit, { unit = it })
        AssetDropdown(assets, assetId) { assetId = it }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("عداد تراكمي", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isCounter, onCheckedChange = { isCounter = it })
        }
        LabeledField("الحد الأعلى للتنبيه (اختياري)", limit, { limit = it }, numeric = true)
        LabeledField("الحد الأدنى للتنبيه (اختياري)", lowerLimit, { lowerLimit = it }, numeric = true)
        LabeledField("هامش التحذير قبل الحد (0 = معطّل)", warningMargin, { warningMargin = it }, numeric = true)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("إنشاء بلاغ تلقائي عند الإنذار", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = autoNotifyOnAlarm, onCheckedChange = { autoNotifyOnAlarm = it })
        }
        SaveButton(name.isNotBlank() && unit.isNotBlank() && assetId != 0L) {
            val today = DateStrings.today()
            onSave(
                MeasuringPointEntity(
                    id = initial?.id ?: 0,
                    assetId = assetId,
                    name = name.trim(),
                    unit = unit.trim(),
                    isCounter = isCounter,
                    upperLimit = limit.toDoubleOrNull(),
                    lastReading = initial?.lastReading ?: 0.0,
                    lastReadingAt = initial?.lastReadingAt ?: today,
                    lowerLimit = lowerLimit.toDoubleOrNull(),
                    warningMargin = warningMargin.toDoubleOrNull() ?: 0.0,
                    autoNotifyOnAlarm = autoNotifyOnAlarm
                )
            )
        }
    }
}

@Composable
internal fun ReadingDialog(point: MeasuringPointEntity, onSubmit: (Double, String) -> Unit, onDismiss: () -> Unit) {
    var value by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تسجيل قراءة — ${point.name}", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("آخر قراءة: ${point.lastReading} ${point.unit}", style = MaterialTheme.typography.bodySmall)
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it },
                    label = { Text("القراءة (${point.unit})") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(value = note, onValueChange = { note = it }, label = { Text("ملاحظة (اختياري)") }, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            TextButton(
                enabled = value.toDoubleOrNull() != null,
                onClick = { value.toDoubleOrNull()?.let { onSubmit(it, note) } }
            ) { Text("تسجيل") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}


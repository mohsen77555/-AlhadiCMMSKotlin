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
// Asset characteristic form
// ---------------------------------------------------------------------------

@Composable
internal fun CharacteristicFormSheet(
    initial: AssetCharacteristicEntity?,
    assetId: Long,
    defaultClass: String = "",
    onDismiss: () -> Unit,
    onSave: (AssetCharacteristicEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var value by remember { mutableStateOf(initial?.value ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "") }
    var className by remember { mutableStateOf(initial?.className ?: defaultClass.ifBlank { "عام" }) }
    var dataType by remember { mutableStateOf(initial?.dataType ?: "Text") }
    var allowedValues by remember { mutableStateOf(initial?.allowedValues ?: "") }
    var isRequired by remember { mutableStateOf(initial?.isRequired ?: false) }

    val listValues = parseCharacteristicAllowedValues(allowedValues)
    val effectiveValue = when (dataType) {
        "Boolean" -> value.takeIf { it == "true" || it == "false" } ?: "true"
        "List" -> value.takeIf { it in listValues } ?: listValues.firstOrNull().orEmpty()
        else -> value.trim()
    }
    val validValue = when (dataType) {
        "Number" -> effectiveValue.toDoubleOrNull() != null
        "List" -> listValues.isNotEmpty() && effectiveValue.isNotBlank()
        else -> effectiveValue.isNotBlank()
    }

    FormSheet(if (initial == null) "إضافة خاصية" else "تعديل الخاصية", onDismiss) {
        LabeledField("التصنيف", className, { className = it })
        LabeledField("اسم الخاصية", name, { name = it })
        OptionDropdown(
            label = "نوع القيمة",
            options = listOf("Text", "Number", "Boolean", "Date", "List"),
            selected = dataType,
            display = ::characteristicTypeLabel
        ) {
            dataType = it
            if (it != "List") allowedValues = ""
        }
        if (dataType == "List") {
            LabeledField("القيم المتاحة (مفصولة بفاصلة)", allowedValues, { allowedValues = it })
        }
        when (dataType) {
            "Boolean" -> OptionDropdown(
                label = "القيمة",
                options = listOf("true", "false"),
                selected = effectiveValue,
                display = { if (it == "true") "نعم" else "لا" }
            ) { value = it }
            "Date" -> DateField("القيمة", value) { value = it }
            "List" -> if (listValues.isNotEmpty()) {
                OptionDropdown("القيمة", listValues, effectiveValue) { value = it }
            } else {
                LabeledField("القيمة", value, { value = it })
            }
            else -> LabeledField("القيمة", value, { value = it }, numeric = dataType == "Number")
        }
        LabeledField("الوحدة (اختياري)", unit, { unit = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("خاصية إلزامية", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isRequired, onCheckedChange = { isRequired = it })
        }
        SaveButton(name.isNotBlank() && className.isNotBlank() && validValue) {
            onSave(
                AssetCharacteristicEntity(
                    id = initial?.id ?: 0,
                    assetId = assetId,
                    name = name.trim(),
                    value = effectiveValue,
                    unit = unit.trim(),
                    className = className.trim().ifBlank { "عام" },
                    dataType = dataType,
                    allowedValues = allowedValues.trim(),
                    isRequired = isRequired
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Asset document form
// ---------------------------------------------------------------------------

@Composable
internal fun DocumentFormSheet(
    initial: AssetDocumentEntity?,
    assetId: Long,
    onDismiss: () -> Unit,
    onSave: (AssetDocumentEntity) -> Unit
) {
    var type by remember { mutableStateOf(initial?.type ?: "Manual") }
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var reference by remember { mutableStateOf(initial?.reference ?: "") }

    FormSheet(if (initial == null) "إضافة مستند" else "تعديل المستند", onDismiss) {
        OptionDropdown("النوع", listOf("Manual", "Drawing", "Certificate", "Image", "Report", "Other"), type) { type = it }
        LabeledField("العنوان", title, { title = it })
        LabeledField("المرجع (رابط/مسار/ملاحظة)", reference, { reference = it }, singleLine = false)
        SaveButton(title.isNotBlank()) {
            onSave(
                AssetDocumentEntity(
                    id = initial?.id ?: 0,
                    assetId = assetId,
                    type = type,
                    title = title.trim(),
                    reference = reference.trim(),
                    uploadedBy = initial?.uploadedBy ?: "",
                    uploadedAt = initial?.uploadedAt ?: ""
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// PM checklist item form
// ---------------------------------------------------------------------------

@Composable
internal fun ChecklistItemFormSheet(
    pmId: Long,
    nextOrder: Int,
    onDismiss: () -> Unit,
    onSave: (PmChecklistItemEntity) -> Unit
) {
    var text by remember { mutableStateOf("") }

    FormSheet("إضافة بند فحص", onDismiss) {
        LabeledField("نص البند", text, { text = it }, singleLine = false)
        SaveButton(text.isNotBlank()) {
            onSave(
                PmChecklistItemEntity(
                    id = 0,
                    pmId = pmId,
                    text = text.trim(),
                    result = "",
                    note = "",
                    orderIndex = nextOrder
                )
            )
        }
    }
}


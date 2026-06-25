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
// Warehouse form
// ---------------------------------------------------------------------------

@Composable
internal fun WarehouseFormSheet(
    initial: WarehouseEntity?,
    existing: List<WarehouseEntity>,
    onDismiss: () -> Unit,
    onSave: (WarehouseEntity) -> Unit
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var keeper by remember { mutableStateOf(initial?.keeper ?: "") }
    var phone by remember { mutableStateOf(initial?.phone ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: "Main") }
    var status by remember { mutableStateOf(initial?.status ?: "Active") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }

    val trimmedCode = code.trim()
    val duplicateCode = trimmedCode.isNotBlank() && existing.any {
        it.id != initial?.id && it.code.equals(trimmedCode, ignoreCase = true)
    }

    FormSheet(if (initial == null) "إضافة مستودع" else "تعديل المستودع", onDismiss) {
        LabeledField("كود المستودع", code, { code = it })
        if (duplicateCode) Text("هذا الكود مستخدم بالفعل.", color = MaterialTheme.colorScheme.error)
        LabeledField("اسم المستودع", name, { name = it })
        OptionDropdown("النوع", listOf("Main", "Spare", "Tools", "Consumables", "Scrap"), type, display = ::warehouseTypeOption) { type = it }
        LabeledField("الموقع الفعلي", location, { location = it })
        LabeledField("أمين المخزن", keeper, { keeper = it })
        LabeledField("الهاتف", phone, { phone = it })
        OptionDropdown("الحالة", listOf("Active", "Inactive"), status) { status = it }
        LabeledField("ملاحظات", notes, { notes = it }, singleLine = false)
        SaveButton(code.isNotBlank() && name.isNotBlank() && !duplicateCode) {
            onSave(
                WarehouseEntity(
                    id = initial?.id ?: 0,
                    code = trimmedCode,
                    name = name.trim(),
                    location = location.trim(),
                    keeper = keeper.trim(),
                    phone = phone.trim(),
                    type = type,
                    status = status,
                    notes = notes.trim()
                )
            )
        }
    }
}

internal fun mobilityLabel(m: String): String = when (m) {
    "Fixed" -> "ثابت / مركّب"
    "Mobile" -> "متنقّل"
    else -> m
}

internal fun warrantyTypeLabel(type: String): String = when (type) {
    "" -> "غير محدد"
    "Standard" -> "قياسي"
    "Extended" -> "ممتد"
    "Service Contract" -> "عقد خدمة"
    "AMC" -> "عقد صيانة سنوي"
    else -> type
}

internal fun warrantyCounterTypeLabel(type: String): String = when (type) {
    "" -> "بالتاريخ فقط"
    "Hours" -> "ساعات تشغيل"
    "Km" -> "كيلومترات"
    "Cycles" -> "دورات"
    "Production" -> "إنتاج"
    else -> type
}

internal fun warrantyClaimStatusLabel(status: String): String = when (status) {
    "", "None" -> "لا يوجد"
    "Submitted" -> "مُقدّمة"
    "UnderReview" -> "قيد المراجعة"
    "Approved" -> "مقبولة"
    "Rejected" -> "مرفوضة"
    else -> status
}

internal fun warehouseTypeOption(type: String): String = when (type) {
    "Main" -> "رئيسي"
    "Spare" -> "قطع غيار"
    "Tools" -> "عدد وأدوات"
    "Consumables" -> "مواد استهلاكية"
    "Scrap" -> "خردة/تالف"
    else -> type
}

// ---------------------------------------------------------------------------
// Organizational unit form
// ---------------------------------------------------------------------------

@Composable
internal fun OrgUnitFormSheet(
    initial: OrgUnitEntity?,
    existing: List<OrgUnitEntity>,
    defaultType: String = "WorkCenter",
    onDismiss: () -> Unit,
    onSave: (OrgUnitEntity) -> Unit
) {
    var type by remember { mutableStateOf(initial?.type ?: defaultType) }
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var status by remember { mutableStateOf(initial?.status ?: "Active") }
    var parentId by remember { mutableStateOf(initial?.parentId) }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }

    val trimmedCode = code.trim()
    val duplicate = trimmedCode.isNotBlank() && existing.any {
        it.id != initial?.id && it.type == type && it.code.equals(trimmedCode, ignoreCase = true)
    }
    val parentOptions = listOf("") + existing.filter { it.id != initial?.id }.map { it.id.toString() }

    FormSheet(if (initial == null) "إضافة وحدة تنظيمية" else "تعديل الوحدة التنظيمية", onDismiss) {
        OptionDropdown("النوع", listOf("Company", "Site", "Plant", "MaintenancePlant", "PlanningPlant", "Department", "CostCenter", "PlannerGroup", "WorkCenter", "StorageLocation"), type, display = ::orgUnitTypeOption) { type = it }
        LabeledField("الكود", code, { code = it })
        if (duplicate) Text("هذا الكود مستخدم لنفس النوع.", color = MaterialTheme.colorScheme.error)
        LabeledField("الاسم", name, { name = it })
        OptionDropdown(
            label = "الوحدة الأعلى (اختياري)",
            options = parentOptions,
            selected = parentId?.toString() ?: "",
            display = { idStr -> if (idStr.isBlank()) "بدون" else existing.firstOrNull { it.id.toString() == idStr }?.let { "${it.code} • ${it.name}" } ?: idStr }
        ) { parentId = it.toLongOrNull() }
        OptionDropdown("الحالة", listOf("Active", "Inactive"), status) { status = it }
        LabeledField("ملاحظات", notes, { notes = it }, singleLine = false)
        SaveButton(code.isNotBlank() && name.isNotBlank() && !duplicate) {
            onSave(
                OrgUnitEntity(
                    id = initial?.id ?: 0,
                    type = type,
                    code = trimmedCode,
                    name = name.trim(),
                    status = status,
                    parentId = parentId,
                    notes = notes.trim()
                )
            )
        }
    }
}

internal fun orgUnitTypeOption(type: String): String = when (type) {
    "Company" -> "شركة"
    "Site" -> "موقع جغرافي"
    "Plant" -> "مصنع / وحدة تشغيلية"
    "MaintenancePlant" -> "جهة الصيانة"
    "PlanningPlant" -> "جهة التخطيط"
    "Department" -> "قسم"
    "CostCenter" -> "مركز تكلفة"
    "PlannerGroup" -> "مجموعة تخطيط"
    "WorkCenter" -> "مركز عمل"
    "StorageLocation" -> "موقع تخزين"
    else -> type
}


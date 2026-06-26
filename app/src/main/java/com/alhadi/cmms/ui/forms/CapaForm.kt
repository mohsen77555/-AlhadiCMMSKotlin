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
// CAPA form
// ---------------------------------------------------------------------------

@Composable
internal fun CapaFormSheet(
    initial: CapaEntity?,
    assets: List<AssetEntity>,
    defaultAssignee: String,
    onDismiss: () -> Unit,
    onSave: (CapaEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var type by remember { mutableStateOf(initial?.type ?: "Corrective") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var assetId by remember { mutableStateOf(initial?.assetId) }
    var priority by remember { mutableStateOf(initial?.priority ?: "Medium") }
    var status by remember { mutableStateOf(initial?.status ?: "Open") }
    var assignedTo by remember { mutableStateOf(initial?.assignedTo ?: defaultAssignee) }
    var dueDays by remember { mutableStateOf("7") }

    FormSheet(if (initial == null) "إجراء تصحيحي/وقائي جديد" else "تعديل الإجراء", onDismiss) {
        LabeledField("العنوان", title, { title = it })
        OptionDropdown("النوع", listOf("Corrective", "Preventive"), type) { type = it }
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        AssetDropdownOptional(assets, assetId, onSelect = { assetId = it })
        OptionDropdown("الأولوية", listOf("Low", "Medium", "High", "Critical"), priority) { priority = it }
        OptionDropdown("الحالة", listOf("Open", "In Progress", "Closed"), status) { status = it }
        LabeledField("المسؤول", assignedTo, { assignedTo = it })
        if (initial == null) {
            LabeledField("الاستحقاق خلال (أيام)", dueDays, { dueDays = it }, numeric = true)
        }
        SaveButton(title.isNotBlank()) {
            val today = DateStrings.today()
            val due = initial?.dueAt ?: DateStrings.daysFromToday(dueDays.toIntOrNull() ?: 7)
            onSave(
                CapaEntity(
                    id = initial?.id ?: 0,
                    code = initial?.code ?: "",
                    title = title.trim(),
                    type = type,
                    description = description,
                    assetId = assetId,
                    priority = priority,
                    status = status,
                    assignedTo = assignedTo.ifBlank { defaultAssignee },
                    dueAt = due,
                    createdAt = initial?.createdAt ?: today
                )
            )
        }
    }
}

@Composable
internal fun UserFormSheet(
    initial: UserEntity?,
    assetGroups: List<String> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (UserEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var username by remember { mutableStateOf(initial?.username ?: "") }
    var role by remember { mutableStateOf(initial?.role ?: com.alhadi.cmms.data.Roles.TECHNICIAN) }
    var craft by remember { mutableStateOf(initial?.craft ?: "") }
    var assignedGroups by remember { mutableStateOf(initial?.assignedGroups ?: "") }
    // Never pre-fill the stored (hashed) password. Blank means "keep current" when editing.
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var active by remember { mutableStateOf(initial?.isActive ?: true) }
    var email by remember { mutableStateOf(initial?.email ?: "") }
    var phone by remember { mutableStateOf(initial?.phone ?: "") }
    var department by remember { mutableStateOf(initial?.department ?: "") }
    var employeeId by remember { mutableStateOf(initial?.employeeId ?: "") }
    var mustChangePassword by remember { mutableStateOf(initial?.mustChangePassword ?: false) }

    FormSheet(if (initial == null) "إضافة مستخدم" else "تعديل المستخدم", onDismiss) {
        LabeledField("الاسم", name, { name = it })
        LabeledField("اسم المستخدم", username, { username = it })
        OptionDropdown("الدور", com.alhadi.cmms.data.Roles.ALL, role, display = { com.alhadi.cmms.data.Roles.label(it) }) { role = it }
        val roleKind = com.alhadi.cmms.data.roleOf(role)
        if (roleKind == com.alhadi.cmms.data.AppRole.Technician) {
            OptionDropdown("التخصص", com.alhadi.cmms.data.CRAFTS, craft, display = { if (it.isBlank()) "غير محدد" else com.alhadi.cmms.data.craftLabel(it) }) { craft = it }
        }
        if (roleKind == com.alhadi.cmms.data.AppRole.MaintenanceManager || roleKind == com.alhadi.cmms.data.AppRole.Technician) {
            LabeledField("مجموعات الأصول الموكَّلة (افصل بفاصلة)", assignedGroups, { assignedGroups = it })
            if (assetGroups.isNotEmpty()) {
                Text("المجموعات المتاحة: ${assetGroups.joinToString("، ")}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        LabeledField("البريد الإلكتروني", email, { email = it })
        LabeledField("الهاتف", phone, { phone = it })
        LabeledField("القسم", department, { department = it })
        LabeledField("الرقم الوظيفي", employeeId, { employeeId = it })
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text(if (initial == null) "كلمة المرور" else "كلمة مرور جديدة (اتركها فارغة للإبقاء)") },
            singleLine = true,
            visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                IconButton(onClick = { showPassword = !showPassword }) {
                    Icon(if (showPassword) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, contentDescription = "إظهار/إخفاء")
                }
            },
            modifier = Modifier.fillMaxWidth()
        )
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("مفعّل", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = active, onCheckedChange = { active = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("إجبار تغيير كلمة المرور", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = mustChangePassword, onCheckedChange = { mustChangePassword = it })
        }
        SaveButton(name.isNotBlank() && username.isNotBlank()) {
            onSave(
                UserEntity(
                    id = initial?.id ?: 0,
                    name = name.trim(),
                    username = username.trim(),
                    role = role,
                    isActive = active,
                    // Blank is resolved by the repository (kept on edit, defaulted on create).
                    password = password,
                    email = email.trim(),
                    phone = phone.trim(),
                    department = department.trim(),
                    employeeId = employeeId.trim(),
                    mustChangePassword = mustChangePassword,
                    // Preserve governance/login state so editing never wipes it.
                    lastLoginAt = initial?.lastLoginAt ?: "",
                    createdAt = initial?.createdAt ?: "",
                    passwordChangedAt = initial?.passwordChangedAt ?: "",
                    failedLoginCount = initial?.failedLoginCount ?: 0,
                    locked = initial?.locked ?: false,
                    craft = if (com.alhadi.cmms.data.roleOf(role) == com.alhadi.cmms.data.AppRole.Technician) craft else "",
                    assignedGroups = assignedGroups.trim()
                )
            )
        }
    }
}

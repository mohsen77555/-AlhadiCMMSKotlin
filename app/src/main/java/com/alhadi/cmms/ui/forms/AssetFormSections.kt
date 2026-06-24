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

// ------------------------------------------------------------------------
// Self-contained AssetForm sections (no validation flags) — moved out of AssetForm.kt
// ------------------------------------------------------------------------

@Composable
internal fun assetClassificationSection(state: AssetFormState) {
    with(state) {
        Text("التصنيف", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("التصنيف القياسي", standardClass, { standardClass = it })
        LabeledField("فئة المعدّة", equipmentCategory, { equipmentCategory = it })
        LabeledField("صنف الأصل", assetClass, { assetClass = it })
        LabeledField("الصنف الفرعي", assetSubclass, { assetSubclass = it })
        LabeledField("نوع الإنشاء المشترك", constructionType, { constructionType = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("توريث خصائص الأصل الأب", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = inheritParentCharacteristics, onCheckedChange = { inheritParentCharacteristics = it })
        }
    }
}

@Composable
internal fun assetSafetySection(state: AssetFormState) {
    with(state) {
        Text("السلامة والامتثال", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("أصل حرج للسلامة", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = safetyCritical, onCheckedChange = { safetyCritical = it })
        }
        OptionDropdown("مستوى المخاطر", listOf("", "Low", "Medium", "High", "Critical"), riskLevel) { riskLevel = it }
        LabeledField("التصاريح المطلوبة", requiredPermits, { requiredPermits = it }, singleLine = false)
        LabeledField("تعليمات السلامة", safetyInstructions, { safetyInstructions = it }, singleLine = false)
        LabeledField("معدات الوقاية المطلوبة (PPE)", ppeRequired, { ppeRequired = it }, singleLine = false)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("يتطلب عزل الطاقة", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isolationRequired, onCheckedChange = { isolationRequired = it })
        }
        LabeledField("متطلبات الامتثال", complianceRequirements, { complianceRequirements = it }, singleLine = false)
    }
}

@Composable
internal fun assetFinancialSection(state: AssetFormState) {
    with(state) {
        Text("المعلومات المالية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("رقم الأصل المالي", assetNumber, { assetNumber = it })
        LabeledField("المورّد", supplier, { supplier = it })
        LabeledField("أمر الشراء", purchaseOrder, { purchaseOrder = it })
        LabeledField("تكلفة الشراء", purchaseCost, { purchaseCost = it }, numeric = true)
        DateField("تاريخ الاقتناء", acquiredAt) { acquiredAt = it }
        OptionDropdown("الحالة المالية", listOf("", "Active", "Capitalized", "Depreciated", "Disposed", "Written Off"), financialStatus) { financialStatus = it }
        LabeledField("القيمة الدفترية", bookValue, { bookValue = it }, numeric = true)
        DateField("تاريخ الرسملة", capitalizationAt) { capitalizationAt = it }
    }
}

@Composable
internal fun assetContactSection(state: AssetFormState) {
    with(state) {
        Text("جهة الاتصال والعنوان", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("اسم الجهة أو الشخص", partnerName, { partnerName = it })
        OptionDropdown(
            label = "صفة الجهة",
            options = assetPartnerRoleOptions,
            selected = partnerRole,
            display = ::assetPartnerRoleLabel
        ) { partnerRole = it }
        LabeledField("رقم الهاتف", partnerPhone, { partnerPhone = it })
        LabeledField("البريد الإلكتروني", partnerEmail, { partnerEmail = it })
        LabeledField("العنوان", addressLine, { addressLine = it }, singleLine = false)
        LabeledField("المدينة", city, { city = it })
        LabeledField("الدولة", country, { country = it })
    }
}

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
    var type by remember { mutableStateOf(initial?.type ?: "Corrective") }
    var assignedTo by remember { mutableStateOf(initial?.assignedTo ?: defaultAssignee) }
    var cost by remember { mutableStateOf((initial?.estimatedCost ?: 0.0).toString()) }
    var dueDays by remember { mutableStateOf("3") }
    var isFailure by remember { mutableStateOf(initial?.isFailure ?: false) }
    var downtime by remember { mutableStateOf((initial?.downtimeHours ?: 0.0).toString()) }
    var failureCode by remember { mutableStateOf(initial?.failureCode ?: "") }
    var failureCause by remember { mutableStateOf(initial?.failureCause ?: "") }
    var failureEffect by remember { mutableStateOf(initial?.failureEffect ?: "") }
    var rootCause by remember { mutableStateOf(initial?.rootCause ?: "") }
    var plannedStart by remember { mutableStateOf(initial?.plannedStart ?: "") }
    var laborHours by remember { mutableStateOf((initial?.laborHours ?: 0.0).toString()) }
    var laborRate by remember { mutableStateOf((initial?.laborRate ?: 0.0).toString()) }
    var partsCost by remember { mutableStateOf((initial?.partsCost ?: 0.0).toString()) }
    var requiresPermit by remember { mutableStateOf(initial?.requiresPermit ?: false) }
    var linearStartPoint by remember { mutableStateOf(initial?.linearStartPoint?.let(::formatLinearNumber) ?: "") }
    var linearEndPoint by remember { mutableStateOf(initial?.linearEndPoint?.let(::formatLinearNumber) ?: "") }
    var linearMarker by remember { mutableStateOf(initial?.linearMarker ?: "") }
    var linearHorizontalOffset by remember { mutableStateOf(initial?.linearHorizontalOffset?.let(::formatLinearNumber) ?: "") }
    var linearVerticalOffset by remember { mutableStateOf(initial?.linearVerticalOffset?.let(::formatLinearNumber) ?: "") }
    var repairType by remember { mutableStateOf(initial?.repairType ?: "") }
    var warrantyReviewed by remember { mutableStateOf(initial?.warrantyReviewed ?: false) }
    var warrantyReviewResult by remember { mutableStateOf(initial?.warrantyReviewResult ?: "") }
    val selectedAsset = assets.firstOrNull { it.id == assetId }
    // AST-WAR-008/009: warranty decision & cost-policy gate for under-warranty assets.
    val underWarranty = selectedAsset?.isUnderWarranty(DateStrings.today()) == true
    val totalInternalCost = (cost.toDoubleOrNull() ?: 0.0) +
        (laborHours.toDoubleOrNull() ?: 0.0) * (laborRate.toDoubleOrNull() ?: 0.0) +
        (partsCost.toDoubleOrNull() ?: 0.0)
    val repairTypeValid = !underWarranty || repairType.isNotBlank()
    val warrantyCostBlocked = WARRANTY_REVIEW_POLICY && underWarranty &&
        repairType == "Internal" && !warrantyReviewed && totalInternalCost > 0.0
    val linearReferenceValid = selectedAsset?.let { asset ->
        !asset.isLinearAsset || (
            optionalLinearRangeValid(asset, linearStartPoint, linearEndPoint) &&
                optionalLinearNumberValid(linearHorizontalOffset) && optionalLinearNumberValid(linearVerticalOffset)
        )
    } ?: true

    FormSheet(if (initial == null) "إنشاء أمر عمل" else "تعديل أمر العمل", onDismiss) {
        LabeledField("العنوان", title, { title = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        AssetDropdown(assets, assetId) { assetId = it }
        // AST-WAR-001: warn when opening a work order on an asset that is still under warranty.
        if (selectedAsset?.isUnderWarranty(DateStrings.today()) == true) {
            Surface(
                color = MaterialTheme.colorScheme.tertiaryContainer,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Verified, contentDescription = null, tint = MaterialTheme.colorScheme.onTertiaryContainer)
                    Column {
                        Text("AST-WAR-001: هذا الأصل ضمن الضمان", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onTertiaryContainer)
                        Text(
                            "راجع الضمان قبل تحميل تكلفة داخلية — قد يكون الإصلاح مطالبة ضمان." +
                                (selectedAsset.warrantyReference.takeIf { it.isNotBlank() }?.let { " (مرجع: $it)" } ?: ""),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            }
        }
        if (selectedAsset?.isLinearAsset == true) {
            LinearMaintenancePositionFields(
                asset = selectedAsset,
                startPoint = linearStartPoint,
                onStartPointChange = { linearStartPoint = it },
                endPoint = linearEndPoint,
                onEndPointChange = { linearEndPoint = it },
                marker = linearMarker,
                onMarkerChange = { linearMarker = it },
                horizontalOffset = linearHorizontalOffset,
                onHorizontalOffsetChange = { linearHorizontalOffset = it },
                verticalOffset = linearVerticalOffset,
                onVerticalOffsetChange = { linearVerticalOffset = it }
            )
        }
        OptionDropdown("نوع الأمر", listOf("Corrective", "Preventive", "Breakdown", "Calibration", "Inspection", "Improvement"), type, display = ::workOrderTypeLabel) { type = it }
        OptionDropdown("الأولوية", listOf("Low", "Medium", "High", "Critical"), priority) { priority = it }
        OptionDropdown("الحالة", listOf("Open", "In Progress", "Technically Completed", "Closed"), status) { status = it }
        LabeledField("المسؤول", assignedTo, { assignedTo = it })
        LabeledField("التكلفة التقديرية", cost, { cost = it }, numeric = true)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { LabeledField("ساعات العمالة", laborHours, { laborHours = it }, numeric = true) }
            Box(modifier = Modifier.weight(1f)) { LabeledField("أجر الساعة", laborRate, { laborRate = it }, numeric = true) }
        }
        LabeledField("تكلفة قطع الغيار", partsCost, { partsCost = it }, numeric = true)
        if (underWarranty) {
            Text("قرار الضمان", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            // AST-WAR-008: must choose internal repair vs warranty claim.
            OptionDropdown("نوع الإصلاح", listOf("", "Internal", "WarrantyClaim"), repairType, display = ::repairTypeLabel) { repairType = it }
            if (!repairTypeValid) Text("AST-WAR-008: حدّد نوع الإصلاح (داخلي أو مطالبة ضمان).", color = MaterialTheme.colorScheme.error)
            if (repairType.isNotBlank()) {
                // AST-WAR-010: capture the warranty review outcome (saved to asset history).
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Text("تمت مراجعة الضمان", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                    Switch(checked = warrantyReviewed, onCheckedChange = { warrantyReviewed = it })
                }
                if (warrantyReviewed) {
                    LabeledField("نتيجة مراجعة الضمان", warrantyReviewResult, { warrantyReviewResult = it }, singleLine = false)
                }
            }
            if (warrantyCostBlocked) {
                Text("AST-WAR-009: لا يمكن تحميل تكلفة داخلية قبل مراجعة الضمان. راجع الضمان أو حوّلها لمطالبة ضمان.", color = MaterialTheme.colorScheme.error)
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("عطل (Breakdown)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isFailure, onCheckedChange = { isFailure = it })
        }
        if (isFailure || type == "Breakdown") {
            LabeledField("مدة التوقف (ساعات)", downtime, { downtime = it }, numeric = true)
            // WO-FLR-001..004: failure code, cause, effect and (optional) root cause.
            LabeledField("رمز العطل (Failure Code)", failureCode, { failureCode = it })
            LabeledField("سبب العطل (Cause)", failureCause, { failureCause = it }, singleLine = false)
            LabeledField("أثر العطل (Effect)", failureEffect, { failureEffect = it }, singleLine = false)
            LabeledField("السبب الجذري (Root Cause)", rootCause, { rootCause = it }, singleLine = false)
        }
        // WO-PLAN-001: planned start date.
        LabeledField("تاريخ البدء المخطط", plannedStart, { plannedStart = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("يتطلّب تصريح عمل (خطر)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = requiresPermit, onCheckedChange = { requiresPermit = it })
        }
        if (initial == null) {
            LabeledField("الاستحقاق خلال (أيام)", dueDays, { dueDays = it }, numeric = true)
        }
        SaveButton(title.isNotBlank() && assetId != 0L && linearReferenceValid && repairTypeValid && !warrantyCostBlocked) {
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
                    closeNotes = initial?.closeNotes ?: "",
                    isFailure = isFailure,
                    downtimeHours = if (isFailure) downtime.toDoubleOrNull() ?: 0.0 else 0.0,
                    laborHours = laborHours.toDoubleOrNull() ?: 0.0,
                    laborRate = laborRate.toDoubleOrNull() ?: 0.0,
                    partsCost = partsCost.toDoubleOrNull() ?: 0.0,
                    approvalStatus = initial?.approvalStatus ?: "NotRequired",
                    approvedBy = initial?.approvedBy ?: "",
                    requiresPermit = requiresPermit,
                    linearStartPoint = if (selectedAsset?.isLinearAsset == true) linearStartPoint.toDoubleOrNull() else null,
                    linearEndPoint = if (selectedAsset?.isLinearAsset == true) linearEndPoint.toDoubleOrNull() else null,
                    linearMarker = if (selectedAsset?.isLinearAsset == true) linearMarker.trim() else "",
                    linearHorizontalOffset = if (selectedAsset?.isLinearAsset == true) linearHorizontalOffset.toDoubleOrNull() else null,
                    linearVerticalOffset = if (selectedAsset?.isLinearAsset == true) linearVerticalOffset.toDoubleOrNull() else null,
                    repairType = if (underWarranty) repairType else "",
                    warrantyReviewed = underWarranty && warrantyReviewed,
                    warrantyReviewResult = if (underWarranty && warrantyReviewed) warrantyReviewResult.trim() else "",
                    type = type,
                    // Governance/org snapshot is filled by the repository on creation and preserved on edit (WO-AST-008).
                    companyCode = initial?.companyCode ?: "",
                    siteCode = initial?.siteCode ?: "",
                    plantCode = initial?.plantCode ?: "",
                    maintenancePlantCode = initial?.maintenancePlantCode ?: "",
                    planningPlantCode = initial?.planningPlantCode ?: "",
                    plannerGroup = initial?.plannerGroup ?: "",
                    workCenter = initial?.workCenter ?: "",
                    costCenter = initial?.costCenter ?: "",
                    assetCode = initial?.assetCode ?: "",
                    assetName = initial?.assetName ?: "",
                    functionalLocation = initial?.functionalLocation ?: "",
                    failureCode = failureCode.trim(),
                    failureCause = failureCause.trim(),
                    failureEffect = failureEffect.trim(),
                    rootCause = rootCause.trim(),
                    plannedStart = plannedStart.trim(),
                    cancelledReason = initial?.cancelledReason ?: "",
                    closedAt = initial?.closedAt ?: "",
                    closedBy = initial?.closedBy ?: "",
                    notificationId = initial?.notificationId
                )
            )
        }
    }
}

internal fun workOrderTypeLabel(type: String): String = when (type) {
    "Corrective" -> "تصحيحي"
    "Preventive" -> "وقائي"
    "Breakdown" -> "عطل (Breakdown)"
    "Calibration" -> "معايرة"
    "Inspection" -> "فحص"
    "Improvement" -> "تحسين"
    else -> type
}

internal const val WARRANTY_REVIEW_POLICY = true

internal fun repairTypeLabel(type: String): String = when (type) {
    "" -> "غير محدد"
    "Internal" -> "إصلاح داخلي"
    "WarrantyClaim" -> "مطالبة ضمان"
    else -> type
}

// ---------------------------------------------------------------------------
// Work permit form (تصريح عمل)
// ---------------------------------------------------------------------------

@Composable
internal fun PermitFormSheet(
    orderId: Long,
    onDismiss: () -> Unit,
    onSave: (WorkPermitEntity) -> Unit
) {
    var type by remember { mutableStateOf("LOTO") }
    var hazards by remember { mutableStateOf("") }
    var ppe by remember { mutableStateOf("") }
    var validDays by remember { mutableStateOf("1") }

    FormSheet("إصدار تصريح عمل", onDismiss) {
        OptionDropdown("نوع التصريح", listOf("LOTO", "Hot Work", "Confined Space", "Electrical", "Working at Height", "General"), type) { type = it }
        LabeledField("المخاطر", hazards, { hazards = it }, singleLine = false)
        LabeledField("معدات الوقاية المطلوبة", ppe, { ppe = it }, singleLine = false)
        LabeledField("صلاحية التصريح (أيام)", validDays, { validDays = it }, numeric = true)
        SaveButton(true) {
            onSave(
                WorkPermitEntity(
                    id = 0,
                    orderId = orderId,
                    type = type,
                    hazards = hazards.trim(),
                    ppe = ppe.trim(),
                    status = "Pending",
                    approvedBy = "",
                    validUntil = DateStrings.daysFromToday(validDays.toIntOrNull() ?: 1),
                    createdBy = "",
                    createdAt = ""
                )
            )
        }
    }
}


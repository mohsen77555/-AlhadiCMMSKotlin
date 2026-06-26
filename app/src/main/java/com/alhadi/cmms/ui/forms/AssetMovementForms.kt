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
// Asset movement form (install / transfer / dismantle / retire)
// ---------------------------------------------------------------------------

@Composable
internal fun MovementFormSheet(
    asset: AssetEntity,
    locations: List<FunctionalLocationEntity>,
    onDismiss: () -> Unit,
    onSave: (type: String, locId: Long?, locName: String, notes: String) -> Unit
) {
    var type by remember { mutableStateOf(MovementType.INSTALL) }
    var locId by remember { mutableStateOf<Long?>(asset.locationId) }
    var notes by remember { mutableStateOf("") }
    val needsLocation = type == MovementType.INSTALL || type == MovementType.TRANSFER

    FormSheet("حركة الأصل: ${asset.code}", onDismiss) {
        OptionDropdown(
            "نوع الحركة",
            MovementType.all,
            type,
            display = { MovementType.label(it) },
            onSelect = { type = it }
        )
        if (needsLocation) {
            LocationDropdown("الموقع الوجهة", locations, locId, onSelect = { locId = it })
        }
        LabeledField("ملاحظات", notes, { notes = it }, singleLine = false)
        SaveButton(!needsLocation || locId != null) {
            val name = locations.firstOrNull { it.id == locId }?.name ?: ""
            onSave(type, if (needsLocation) locId else null, if (needsLocation) name else "", notes.trim())
        }
    }
}

// ---------------------------------------------------------------------------
// Operation confirmation form (تأكيد)
// ---------------------------------------------------------------------------

@Composable
internal fun ConfirmationFormSheet(
    operation: WorkOrderOperationEntity,
    isFailureOrder: Boolean,
    onDismiss: () -> Unit,
    onSave: (WorkOrderConfirmationEntity) -> Unit
) {
    val remaining = (operation.plannedHours - operation.actualHours).let { if (it > 0) it else operation.plannedHours }
    var actualWork by remember { mutableStateOf(remaining.toString()) }
    var activityText by remember { mutableStateOf("") }
    var damageFound by remember { mutableStateOf("") }
    var causeFound by remember { mutableStateOf("") }
    var actionTaken by remember { mutableStateOf("") }
    var downtime by remember { mutableStateOf("0") }
    var overtimeHours by remember { mutableStateOf("0") }
    var finalConfirmation by remember { mutableStateOf(true) }

    FormSheet("تأكيد العملية ${operation.operationNumber}", onDismiss) {
        LabeledField("الساعات الفعلية", actualWork, { actualWork = it }, numeric = true)
        LabeledField("ساعات العمل الإضافي (Overtime)", overtimeHours, { overtimeHours = it }, numeric = true)
        LabeledField("وصف العمل المنفذ", activityText, { activityText = it }, singleLine = false)
        if (isFailureOrder) {
            LabeledField("العطل المكتشف", damageFound, { damageFound = it })
            LabeledField("السبب", causeFound, { causeFound = it })
            LabeledField("زمن التوقف (ساعات)", downtime, { downtime = it }, numeric = true)
        }
        LabeledField("الإجراء المتخذ", actionTaken, { actionTaken = it }, singleLine = false)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("تأكيد نهائي (يُغلق العملية)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = finalConfirmation, onCheckedChange = { finalConfirmation = it })
        }
        SaveButton(actualWork.toDoubleOrNull() != null) {
            onSave(
                WorkOrderConfirmationEntity(
                    id = 0,
                    orderId = operation.orderId,
                    operationId = operation.id,
                    technician = "",
                    workDate = "",
                    actualWork = actualWork.toDoubleOrNull() ?: 0.0,
                    activityText = activityText.trim(),
                    damageFound = damageFound.trim(),
                    causeFound = causeFound.trim(),
                    actionTaken = actionTaken.trim(),
                    downtime = downtime.toDoubleOrNull() ?: 0.0,
                    overtimeHours = overtimeHours.toDoubleOrNull() ?: 0.0,
                    finalConfirmation = finalConfirmation,
                    createdAt = ""
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Work order operation form (عملية)
// ---------------------------------------------------------------------------

@Composable
internal fun OperationFormSheet(
    orderId: Long,
    nextNumber: String,
    onDismiss: () -> Unit,
    onSave: (WorkOrderOperationEntity) -> Unit
) {
    var operationNumber by remember { mutableStateOf(nextNumber) }
    var description by remember { mutableStateOf("") }
    var workCenter by remember { mutableStateOf("Mechanical") }
    var plannedHours by remember { mutableStateOf("1") }
    var sequence by remember { mutableStateOf("") }

    FormSheet("إضافة عملية", onDismiss) {
        LabeledField("رقم العملية", operationNumber, { operationNumber = it })
        LabeledField("الترتيب (Sequence)", sequence, { sequence = it }, numeric = true)
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        OptionDropdown("مركز العمل", listOf("Mechanical", "Electrical", "Instrumentation", "Civil", "External"), workCenter) { workCenter = it }
        LabeledField("الساعات المخططة", plannedHours, { plannedHours = it }, numeric = true)
        SaveButton(description.isNotBlank()) {
            onSave(
                WorkOrderOperationEntity(
                    id = 0,
                    orderId = orderId,
                    operationNumber = operationNumber.trim().ifBlank { nextNumber },
                    description = description.trim(),
                    workCenter = workCenter,
                    plannedHours = plannedHours.toDoubleOrNull() ?: 0.0,
                    actualHours = 0.0,
                    requiresConfirmation = true,
                    status = "Open",
                    sequence = sequence.toIntOrNull() ?: (operationNumber.trim().toIntOrNull() ?: 0)
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Maintenance notification form (بلاغ)
// ---------------------------------------------------------------------------

@Composable
internal fun NotificationFormSheet(
    initial: MaintenanceNotificationEntity?,
    assets: List<AssetEntity>,
    onDismiss: () -> Unit,
    onSave: (MaintenanceNotificationEntity) -> Unit
) {
    var type by remember { mutableStateOf(initial?.type ?: "Corrective") }
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var assetId by remember { mutableStateOf(initial?.assetId) }
    var priority by remember { mutableStateOf(initial?.priority ?: "Medium") }
    var damageCode by remember { mutableStateOf(initial?.damageCode ?: "") }
    var causeCode by remember { mutableStateOf(initial?.causeCode ?: "") }
    var effectCode by remember { mutableStateOf(initial?.effectCode ?: "") }
    var requiredEnd by remember { mutableStateOf(initial?.requiredEnd ?: "") }
    var breakdown by remember { mutableStateOf(initial?.breakdown ?: false) }
    var malfunctionStart by remember { mutableStateOf(initial?.malfunctionStart ?: "") }
    var malfunctionEnd by remember { mutableStateOf(initial?.malfunctionEnd ?: "") }
    var linearStartPoint by remember { mutableStateOf(initial?.linearStartPoint?.let(::formatLinearNumber) ?: "") }
    var linearEndPoint by remember { mutableStateOf(initial?.linearEndPoint?.let(::formatLinearNumber) ?: "") }
    var linearMarker by remember { mutableStateOf(initial?.linearMarker ?: "") }
    var linearHorizontalOffset by remember { mutableStateOf(initial?.linearHorizontalOffset?.let(::formatLinearNumber) ?: "") }
    var linearVerticalOffset by remember { mutableStateOf(initial?.linearVerticalOffset?.let(::formatLinearNumber) ?: "") }
    val selectedAsset = assetId?.let { id -> assets.firstOrNull { it.id == id } }
    val linearReferenceValid = selectedAsset?.let { asset ->
        !asset.isLinearAsset || (
            optionalLinearRangeValid(asset, linearStartPoint, linearEndPoint) &&
                optionalLinearNumberValid(linearHorizontalOffset) && optionalLinearNumberValid(linearVerticalOffset)
        )
    } ?: true

    FormSheet(if (initial == null) "بلاغ صيانة جديد" else "تعديل البلاغ", onDismiss) {
        OptionDropdown("النوع", listOf("Corrective", "Breakdown", "Inspection", "Request"), type) { type = it }
        LabeledField("العنوان", title, { title = it })
        LabeledField("وصف المشكلة", description, { description = it }, singleLine = false)
        AssetDropdownOptional(assets, assetId, onSelect = { assetId = it })
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
        OptionDropdown("الأولوية", listOf("Low", "Medium", "High", "Critical"), priority) { priority = it }
        LabeledField("كود الضرر (اختياري)", damageCode, { damageCode = it })
        LabeledField("كود السبب (اختياري)", causeCode, { causeCode = it })
        LabeledField("كود التأثير (اختياري)", effectCode, { effectCode = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("عطل/توقف (Breakdown)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = breakdown, onCheckedChange = { breakdown = it })
        }
        if (breakdown) {
            DateField("بداية العطل", malfunctionStart) { malfunctionStart = it }
            DateField("نهاية العطل (الاستعادة)", malfunctionEnd) { malfunctionEnd = it }
        }
        DateField("مطلوب الإنجاز قبل", requiredEnd) { requiredEnd = it }
        SaveButton(title.isNotBlank() && linearReferenceValid) {
            onSave(
                MaintenanceNotificationEntity(
                    id = initial?.id ?: 0,
                    number = initial?.number ?: "",
                    type = type,
                    title = title.trim(),
                    description = description,
                    assetId = assetId,
                    priority = priority,
                    damageCode = damageCode.trim(),
                    causeCode = causeCode.trim(),
                    reportedBy = initial?.reportedBy ?: "",
                    reportedAt = initial?.reportedAt ?: "",
                    requiredEnd = requiredEnd.trim(),
                    status = initial?.status ?: "New",
                    linkedOrderId = initial?.linkedOrderId,
                    linearStartPoint = if (selectedAsset?.isLinearAsset == true) linearStartPoint.toDoubleOrNull() else null,
                    linearEndPoint = if (selectedAsset?.isLinearAsset == true) linearEndPoint.toDoubleOrNull() else null,
                    linearMarker = if (selectedAsset?.isLinearAsset == true) linearMarker.trim() else "",
                    linearHorizontalOffset = if (selectedAsset?.isLinearAsset == true) linearHorizontalOffset.toDoubleOrNull() else null,
                    linearVerticalOffset = if (selectedAsset?.isLinearAsset == true) linearVerticalOffset.toDoubleOrNull() else null,
                    breakdown = breakdown,
                    effectCode = effectCode.trim(),
                    malfunctionStart = if (breakdown) malfunctionStart.trim() else "",
                    malfunctionEnd = if (breakdown) malfunctionEnd.trim() else "",
                    acknowledgedAt = initial?.acknowledgedAt ?: "",
                    acknowledgedBy = initial?.acknowledgedBy ?: "",
                    closedAt = initial?.closedAt ?: "",
                    closedBy = initial?.closedBy ?: ""
                )
            )
        }
    }
}


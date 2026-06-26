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

@Composable
internal fun LocationFormSheet(
    initial: FunctionalLocationEntity?,
    allLocations: List<FunctionalLocationEntity>,
    orgUnits: List<OrgUnitEntity> = emptyList(),
    onDismiss: () -> Unit,
    onSave: (FunctionalLocationEntity) -> Unit
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var parentId by remember { mutableStateOf(initial?.parentId) }
    var status by remember { mutableStateOf(initial?.status ?: "Active") }
    var plantCode by remember { mutableStateOf(initial?.plantCode ?: "") }
    var workCenterCode by remember { mutableStateOf(initial?.workCenterCode ?: "") }
    var costCenterCode by remember { mutableStateOf(initial?.costCenterCode ?: "") }
    var plannerGroupCode by remember { mutableStateOf(initial?.plannerGroupCode ?: "") }
    var category by remember { mutableStateOf(initial?.category ?: "Standard") }
    var lifecycleStatus by remember { mutableStateOf(initial?.lifecycleStatus ?: "Created") }
    var abcIndicator by remember { mutableStateOf(initial?.abcIndicator ?: "") }
    var sortField by remember { mutableStateOf(initial?.sortField ?: "") }
    var authorizationGroup by remember { mutableStateOf(initial?.authorizationGroup ?: "") }
    var singleInstallation by remember { mutableStateOf(initial?.singleInstallation ?: false) }
    var isReference by remember { mutableStateOf(initial?.isReference ?: false) }
    var referenceCode by remember { mutableStateOf(initial?.referenceCode ?: "") }
    var room by remember { mutableStateOf(initial?.room ?: "") }
    var plantSection by remember { mutableStateOf(initial?.plantSection ?: "") }

    // FLOC-004: a location cannot become its own ancestor — exclude self and all descendants.
    val descendantIds = remember(allLocations, initial?.id) { locationDescendantIds(initial?.id, allLocations) }
    val parentChoices = allLocations.filter { it.id != initial?.id && it.id !in descendantIds }

    // FLOC-040: a new real location can be created from a reference (template) location,
    // copying its classification and organizational assignment.
    val referenceLocations = allLocations.filter { it.isReference && it.id != initial?.id }

    FormSheet(if (initial == null) "إضافة موقع فني" else "تعديل الموقع الفني", onDismiss) {
        if (initial == null && referenceLocations.isNotEmpty()) {
            Text("إنشاء من قالب مرجعي (اختياري)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            OptionDropdown("الموقع المرجعي", listOf("") + referenceLocations.map { it.code }, referenceCode) { selected ->
                referenceCode = selected
                referenceLocations.firstOrNull { it.code == selected }?.let { ref ->
                    category = ref.category
                    abcIndicator = ref.abcIndicator
                    sortField = ref.sortField
                    authorizationGroup = ref.authorizationGroup
                    singleInstallation = ref.singleInstallation
                    plantSection = ref.plantSection
                    plantCode = ref.plantCode
                    workCenterCode = ref.workCenterCode
                    costCenterCode = ref.costCenterCode
                    plannerGroupCode = ref.plannerGroupCode
                    if (description.isBlank()) description = ref.description
                }
            }
        }
        LabeledField("الكود (Code)", code, { code = it })
        LabeledField("الاسم (Name)", name, { name = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        LocationDropdown("الموقع الأعلى (Parent)", parentChoices, parentId, excludeId = initial?.id) { parentId = it }
        OptionDropdown("الحالة", listOf("Active", "Inactive"), status) { status = it }

        Text("التصنيف ودورة الحياة (الحوكمة)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OptionDropdown("الفئة (Category)", listOf("Standard", "Production", "Building", "Utility", "Storage", "Outdoor"), category) { category = it }
        OptionDropdown("حالة دورة الحياة", listOf("Planned", "Created", "Installed", "Inactive"), lifecycleStatus) { lifecycleStatus = it }
        OptionDropdown("مؤشر الأهمية (ABC)", listOf("", "A", "B", "C"), abcIndicator) { abcIndicator = it }
        LabeledField("حقل الفرز (Sort Field)", sortField, { sortField = it })
        LabeledField("مجموعة التفويض", authorizationGroup, { authorizationGroup = it })
        LabeledField("القسم (Plant Section)", plantSection, { plantSection = it })
        LabeledField("الغرفة/الموضع (Room)", room, { room = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("موضع تركيب مفرد (أصل واحد فقط)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = singleInstallation, onCheckedChange = { singleInstallation = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("موقع مرجعي (قالب)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isReference, onCheckedChange = { isReference = it })
        }
        if (!isReference) {
            LabeledField("كود الموقع المرجعي", referenceCode, { referenceCode = it })
        }

        Text("الربط التنظيمي (يورَّث للأصول)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OrgUnitDropdown("المصنع (Plant)", "Plant", orgUnits, plantCode) { plantCode = it }
        OrgUnitDropdown("مركز العمل (Work Center)", "WorkCenter", orgUnits, workCenterCode) { workCenterCode = it }
        OrgUnitDropdown("مركز التكلفة (Cost Center)", "CostCenter", orgUnits, costCenterCode) { costCenterCode = it }
        OrgUnitDropdown("مجموعة التخطيط (Planner Group)", "PlannerGroup", orgUnits, plannerGroupCode) { plannerGroupCode = it }

        SaveButton(code.isNotBlank() && name.isNotBlank()) {
            onSave(
                FunctionalLocationEntity(
                    id = initial?.id ?: 0,
                    code = code.trim(),
                    name = name.trim(),
                    parentId = parentId,
                    description = description,
                    status = status,
                    plantCode = plantCode,
                    workCenterCode = workCenterCode,
                    costCenterCode = costCenterCode,
                    plannerGroupCode = plannerGroupCode,
                    category = category,
                    lifecycleStatus = lifecycleStatus,
                    abcIndicator = abcIndicator,
                    sortField = sortField.trim(),
                    authorizationGroup = authorizationGroup.trim(),
                    singleInstallation = singleInstallation,
                    isReference = isReference,
                    referenceCode = if (isReference) "" else referenceCode.trim(),
                    room = room.trim(),
                    plantSection = plantSection.trim()
                )
            )
        }
    }
}

/** Returns the ids of all descendants of [id] within [all] (for hierarchy cycle prevention). */
internal fun locationDescendantIds(id: Long?, all: List<FunctionalLocationEntity>): Set<Long> {
    if (id == null) return emptySet()
    val result = mutableSetOf<Long>()
    val queue = ArrayDeque<Long>()
    queue.add(id)
    while (queue.isNotEmpty()) {
        val current = queue.removeFirst()
        all.filter { it.parentId == current }.forEach { child ->
            if (result.add(child.id)) queue.add(child.id)
        }
    }
    return result
}


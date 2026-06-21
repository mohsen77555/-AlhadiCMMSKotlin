package com.alhadi.cmms.ui

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
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
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
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.PlannerGroupEntity
import com.alhadi.cmms.data.entity.PlantEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SiteEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.StorageLocationEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkCenterEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
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
    onPick: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("تغيير حالة الأصل", fontWeight = FontWeight.Bold) },
        text = {
            Column {
                options.forEach { opt ->
                    TextButton(onClick = { onPick(opt) }, modifier = Modifier.fillMaxWidth()) {
                        Text(
                            (if (opt == current) "• " else "") + opt,
                            modifier = Modifier.fillMaxWidth(),
                            fontWeight = if (opt == current) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = { TextButton(onClick = onDismiss) { Text("إغلاق") } }
    )
}

@Composable
private fun LabeledField(
    label: String,
    value: String,
    onChange: (String) -> Unit,
    numeric: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        label = { Text(label) },
        singleLine = singleLine,
        keyboardOptions = if (numeric) KeyboardOptions(keyboardType = KeyboardType.Number) else KeyboardOptions.Default,
        modifier = Modifier.fillMaxWidth()
    )
}

/** A read-only date field (YYYY-MM-DD) backed by a Material date picker instead of manual typing. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DateField(label: String, value: String, onChange: (String) -> Unit) {
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

private fun dateFormatterUtc() =
    SimpleDateFormat("yyyy-MM-dd", Locale.US).apply { timeZone = TimeZone.getTimeZone("UTC") }

private fun parseDateMillis(date: String): Long? =
    runCatching { dateFormatterUtc().parse(date)?.time }.getOrNull()

private fun formatDateMillis(millis: Long): String = dateFormatterUtc().format(Date(millis))

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
private fun AssetDropdown(assets: List<AssetEntity>, selectedId: Long, onSelect: (Long) -> Unit) {
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
private fun LocationDropdown(
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
private fun AssetDropdownOptional(
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
private fun SaveButton(enabled: Boolean, onClick: () -> Unit) {
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

// ---------------------------------------------------------------------------
// Asset form
// ---------------------------------------------------------------------------

@Composable
internal fun AssetFormSheet(
    initial: AssetEntity?,
    onDismiss: () -> Unit,
    onSave: (AssetEntity) -> Unit,
    locations: List<FunctionalLocationEntity> = emptyList(),
    allAssets: List<AssetEntity> = emptyList(),
    companies: List<CompanyEntity> = emptyList(),
    sites: List<SiteEntity> = emptyList(),
    plants: List<PlantEntity> = emptyList(),
    workCenters: List<WorkCenterEntity> = emptyList(),
    plannerGroups: List<PlannerGroupEntity> = emptyList(),
    departments: List<DepartmentEntity> = emptyList(),
    costCenters: List<CostCenterEntity> = emptyList(),
    storageLocations: List<StorageLocationEntity> = emptyList()
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var group by remember { mutableStateOf(initial?.groupName ?: "") }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var manufacturer by remember { mutableStateOf(initial?.manufacturer ?: "") }
    var model by remember { mutableStateOf(initial?.model ?: "") }
    var status by remember { mutableStateOf(initial?.status ?: "Running") }
    var criticality by remember { mutableStateOf(initial?.criticality ?: "Medium") }
    var locationId by remember { mutableStateOf(initial?.locationId) }
    var parentAssetId by remember { mutableStateOf(initial?.parentAssetId) }
    var warrantyProvider by remember { mutableStateOf(initial?.warrantyProvider ?: "") }
    var warrantyStart by remember { mutableStateOf(initial?.warrantyStart ?: "") }
    var warrantyEnd by remember { mutableStateOf(initial?.warrantyEnd ?: "") }
    var serialNumber by remember { mutableStateOf(initial?.serialNumber ?: "") }
    var assetTag by remember { mutableStateOf(initial?.assetTag ?: "") }
    var supplier by remember { mutableStateOf(initial?.supplier ?: "") }
    var purchaseOrder by remember { mutableStateOf(initial?.purchaseOrder ?: "") }
    var purchaseCost by remember { mutableStateOf((initial?.purchaseCost ?: 0.0).toString()) }
    var acquiredAt by remember { mutableStateOf(initial?.acquiredAt ?: "") }
    var assetType by remember { mutableStateOf(initial?.assetType ?: "Equipment") }
    var assetCategory by remember { mutableStateOf(initial?.assetCategory ?: "") }
    var equipmentCategory by remember { mutableStateOf(initial?.equipmentCategory ?: "") }
    var objectType by remember { mutableStateOf(initial?.objectType ?: "") }
    var assetClass by remember { mutableStateOf(initial?.assetClass ?: "") }
    var assetSubclass by remember { mutableStateOf(initial?.assetSubclass ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var longDescription by remember { mutableStateOf(initial?.longDescription ?: "") }
    var alternativeLabel by remember { mutableStateOf(initial?.alternativeLabel ?: "") }
    var externalAssetCode by remember { mutableStateOf(initial?.externalAssetCode ?: "") }
    var legacyAssetCode by remember { mutableStateOf(initial?.legacyAssetCode ?: "") }
    var barcode by remember { mutableStateOf(initial?.barcode ?: "") }
    var qrCode by remember { mutableStateOf(initial?.qrCode ?: "") }
    var companyId by remember { mutableStateOf(initial?.companyId ?: "") }
    var companyCode by remember { mutableStateOf(initial?.companyCode ?: "") }
    var companyName by remember { mutableStateOf(initial?.companyName ?: "") }
    var siteId by remember { mutableStateOf(initial?.siteId ?: "") }
    var siteCode by remember { mutableStateOf(initial?.siteCode ?: "") }
    var siteName by remember { mutableStateOf(initial?.siteName ?: "") }
    var plantId by remember { mutableStateOf(initial?.plantId ?: "") }
    var plantCode by remember { mutableStateOf(initial?.plantCode ?: "") }
    var plantName by remember { mutableStateOf(initial?.plantName ?: "") }
    var maintenancePlantId by remember { mutableStateOf(initial?.maintenancePlantId ?: "") }
    var maintenancePlantCode by remember { mutableStateOf(initial?.maintenancePlantCode ?: "") }
    var maintenancePlantName by remember { mutableStateOf(initial?.maintenancePlantName ?: "") }
    var planningPlantId by remember { mutableStateOf(initial?.planningPlantId ?: "") }
    var planningPlantCode by remember { mutableStateOf(initial?.planningPlantCode ?: "") }
    var planningPlantName by remember { mutableStateOf(initial?.planningPlantName ?: "") }
    var workCenterId by remember { mutableStateOf(initial?.workCenterId ?: "") }
    var workCenterCode by remember { mutableStateOf(initial?.workCenterCode ?: "") }
    var workCenterName by remember { mutableStateOf(initial?.workCenterName ?: "") }
    var plannerGroupId by remember { mutableStateOf(initial?.plannerGroupId ?: "") }
    var plannerGroupCode by remember { mutableStateOf(initial?.plannerGroupCode ?: "") }
    var plannerGroupName by remember { mutableStateOf(initial?.plannerGroupName ?: "") }
    var costCenterId by remember { mutableStateOf(initial?.costCenterId ?: "") }
    var costCenterCode by remember { mutableStateOf(initial?.costCenterCode ?: "") }
    var costCenterName by remember { mutableStateOf(initial?.costCenterName ?: "") }
    var departmentId by remember { mutableStateOf(initial?.departmentId ?: "") }
    var departmentCode by remember { mutableStateOf(initial?.departmentCode ?: "") }
    var departmentName by remember { mutableStateOf(initial?.departmentName ?: "") }
    var physicalLocation by remember { mutableStateOf(initial?.physicalLocation ?: "") }
    var building by remember { mutableStateOf(initial?.building ?: "") }
    var floor by remember { mutableStateOf(initial?.floor ?: "") }
    var room by remember { mutableStateOf(initial?.room ?: "") }
    var area by remember { mutableStateOf(initial?.area ?: "") }
    var line by remember { mutableStateOf(initial?.line ?: "") }
    var position by remember { mutableStateOf(initial?.position ?: "") }
    var storageLocationId by remember { mutableStateOf(initial?.storageLocationId ?: "") }
    var storageLocationCode by remember { mutableStateOf(initial?.storageLocationCode ?: "") }
    var storageLocationName by remember { mutableStateOf(initial?.storageLocationName ?: "") }
    var functionalLocationId by remember { mutableStateOf(initial?.functionalLocationId ?: "") }
    var functionalLocationCode by remember { mutableStateOf(initial?.functionalLocationCode ?: "") }
    var functionalLocationName by remember { mutableStateOf(initial?.functionalLocationName ?: "") }
    var functionalLocationPath by remember { mutableStateOf(initial?.functionalLocationPath ?: "") }
    var functionalLocationLevel by remember { mutableStateOf(initial?.functionalLocationLevel ?: 0) }
    var locationInheritanceSource by remember { mutableStateOf(initial?.locationInheritanceSource ?: "") }
    var isLocationInherited by remember { mutableStateOf(initial?.isLocationInherited == true) }
    var isWorkCenterInherited by remember { mutableStateOf(initial?.isWorkCenterInherited == true) }
    var isPlannerGroupInherited by remember { mutableStateOf(initial?.isPlannerGroupInherited == true) }
    var isCostCenterInherited by remember { mutableStateOf(initial?.isCostCenterInherited == true) }
    var isMaintenancePlantInherited by remember { mutableStateOf(initial?.isMaintenancePlantInherited == true) }
    var isPlanningPlantInherited by remember { mutableStateOf(initial?.isPlanningPlantInherited == true) }
    var inheritedFromFunctionalLocationId by remember { mutableStateOf(initial?.inheritedFromFunctionalLocationId ?: "") }
    var manualOverrideReason by remember { mutableStateOf(initial?.manualOverrideReason ?: "") }
    var responsiblePersonId by remember { mutableStateOf(initial?.responsiblePersonId ?: "") }
    var constructionType by remember { mutableStateOf(initial?.constructionType ?: "") }
    var commissioningAt by remember { mutableStateOf(initial?.commissioningAt ?: "") }
    var financialAssetRef by remember { mutableStateOf(initial?.financialAssetRef ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var partners by remember { mutableStateOf(initial?.partners ?: "") }
    var safetyCritical by remember { mutableStateOf(initial?.safetyCritical == true) }
    var riskLevel by remember { mutableStateOf(initial?.riskLevel ?: "") }
    var requiredPermits by remember { mutableStateOf(initial?.requiredPermits ?: "") }
    var safetyInstructions by remember { mutableStateOf(initial?.safetyInstructions ?: "") }
    var ppeRequired by remember { mutableStateOf(initial?.ppeRequired ?: "") }
    var isolationRequired by remember { mutableStateOf(initial?.isolationRequired == true) }
    var complianceRequirements by remember { mutableStateOf(initial?.complianceRequirements ?: "") }
    var financialStatus by remember { mutableStateOf(initial?.financialStatus ?: "") }
    var bookValue by remember { mutableStateOf((initial?.bookValue ?: 0.0).toString()) }
    var capitalizationAt by remember { mutableStateOf(initial?.capitalizationAt ?: "") }
    var linearStartPoint by remember { mutableStateOf(initial?.linearStartPoint ?: "") }
    var linearEndPoint by remember { mutableStateOf(initial?.linearEndPoint ?: "") }
    var linearLength by remember { mutableStateOf((initial?.linearLength ?: 0.0).toString()) }
    var linearUnit by remember { mutableStateOf(initial?.linearUnit ?: "") }
    var linearRoute by remember { mutableStateOf(initial?.linearRoute ?: "") }
    var currentCustodian by remember { mutableStateOf(initial?.currentCustodian ?: "") }
    var currentPhysicalLocation by remember { mutableStateOf(initial?.currentPhysicalLocation ?: "") }
    var lastKnownLocation by remember { mutableStateOf(initial?.lastKnownLocation ?: "") }
    var movementStatus by remember { mutableStateOf(initial?.movementStatus ?: "") }
    var checkedOutTo by remember { mutableStateOf(initial?.checkedOutTo ?: "") }
    var checkedOutAt by remember { mutableStateOf(initial?.checkedOutAt ?: "") }
    var expectedReturnAt by remember { mutableStateOf(initial?.expectedReturnAt ?: "") }
    val formSteps = listOf(
        "Basic" to "الأساسيات",
        "Org" to "التنظيم",
        "Finance" to "المالية",
        "Safety" to "السلامة",
        "Technical" to "فني/متنقل"
    )
    var currentStep by remember { mutableStateOf(formSteps.first().first) }
    fun isStep(key: String): Boolean = currentStep == key
    fun currentStepIndex(): Int = formSteps.indexOfFirst { it.first == currentStep }.coerceAtLeast(0)
    fun activeStatus(status: String): Boolean = !status.equals("Inactive", ignoreCase = true)
    fun masterLabel(id: Long, code: String, name: String): String = listOf(id.toString(), code, name).filter { it.isNotBlank() }.joinToString(" • ")
    fun selectedMasterLabel(id: String, code: String, name: String): String = if (id.isBlank()) "" else listOf(id, code, name).filter { it.isNotBlank() }.joinToString(" • ")
    fun parseMasterId(label: String): Long? = label.substringBefore(" • ").toLongOrNull()

    fun applyFunctionalLocationInheritance(locationEntity: FunctionalLocationEntity) {
        locationId = locationEntity.id
        location = locationEntity.name
        functionalLocationId = locationEntity.id.toString()
        functionalLocationCode = locationEntity.code
        functionalLocationName = locationEntity.name
        functionalLocationPath = locationEntity.path.ifBlank { locationEntity.code }
        functionalLocationLevel = locationEntity.level
        locationInheritanceSource = locationEntity.code
        inheritedFromFunctionalLocationId = locationEntity.id.toString()
        isLocationInherited = true

        if (companyId.isBlank()) companyId = locationEntity.companyId
        if (siteId.isBlank()) siteId = locationEntity.siteId
        if (plantId.isBlank()) plantId = locationEntity.plantId
        if (maintenancePlantId.isBlank() && locationEntity.maintenancePlantId.isNotBlank()) {
            maintenancePlantId = locationEntity.maintenancePlantId
            isMaintenancePlantInherited = true
        }
        if (planningPlantId.isBlank() && locationEntity.planningPlantId.isNotBlank()) {
            planningPlantId = locationEntity.planningPlantId
            isPlanningPlantInherited = true
        }
        if (workCenterId.isBlank() && locationEntity.workCenterId.isNotBlank()) {
            workCenterId = locationEntity.workCenterId
            isWorkCenterInherited = true
        }
        if (plannerGroupId.isBlank() && locationEntity.plannerGroupId.isNotBlank()) {
            plannerGroupId = locationEntity.plannerGroupId
            isPlannerGroupInherited = true
        }
        if (costCenterId.isBlank() && locationEntity.costCenterId.isNotBlank()) {
            costCenterId = locationEntity.costCenterId
            isCostCenterInherited = true
        }
        if (departmentId.isBlank()) departmentId = locationEntity.departmentId
        if (physicalLocation.isBlank()) physicalLocation = locationEntity.physicalLocation
        if (building.isBlank()) building = locationEntity.building
        if (floor.isBlank()) floor = locationEntity.floor
        if (room.isBlank()) room = locationEntity.room
        if (area.isBlank()) area = locationEntity.area
        if (line.isBlank()) line = locationEntity.line
        if (position.isBlank()) position = locationEntity.position
    }

    FormSheet(if (initial == null) "إضافة أصل جديد" else "تعديل الأصل", onDismiss) {
        Text("قسّمنا نموذج الأصل إلى خطوات لتسهيل إدخال البيانات المطلوبة ومراجعتها قبل الحفظ.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            formSteps.forEachIndexed { index, step ->
                val selected = currentStep == step.first
                val label = "${index + 1}. ${step.second}"
                if (selected) {
                    Button(onClick = { currentStep = step.first }, modifier = Modifier.weight(1f)) {
                        Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                } else {
                    OutlinedButton(onClick = { currentStep = step.first }, modifier = Modifier.weight(1f)) {
                        Text(label, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    }
                }
            }
        }
        Text("الخطوة ${currentStepIndex() + 1} من ${formSteps.size}: ${formSteps[currentStepIndex()].second}", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)

        if (isStep("Basic")) {
            Text("الأساسيات والتصنيف", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            LabeledField("الكود (Code)", code, { code = it })
        LabeledField("الاسم (Name)", name, { name = it })
        OptionDropdown("نوع الأصل", listOf("Equipment", "Functional Location", "Linear Asset", "Tool Asset", "Safety Asset", "Utility Asset", "Production Asset", "Mobile Asset", "Serialized Component", "Refurbishable Component"), assetType) { assetType = it }
        LabeledField("فئة الأصل", assetCategory, { assetCategory = it })
        LabeledField("فئة المعدة", equipmentCategory, { equipmentCategory = it })
        LabeledField("نوع الكائن الفني", objectType, { objectType = it })
        LabeledField("تصنيف الأصل", assetClass, { assetClass = it })
        LabeledField("التصنيف الفرعي", assetSubclass, { assetSubclass = it })
        LabeledField("الوصف", description, { description = it })
        LabeledField("الوصف التفصيلي", longDescription, { longDescription = it })
        LabeledField("تسمية بديلة", alternativeLabel, { alternativeLabel = it })
        LabeledField("كود الأصل الخارجي", externalAssetCode, { externalAssetCode = it })
        LabeledField("كود الأصل القديم", legacyAssetCode, { legacyAssetCode = it })
        LabeledField("Barcode", barcode, { barcode = it })
        LabeledField("QR Code", qrCode, { qrCode = it })
        LabeledField("المجموعة (Group)", group, { group = it })
        LabeledField("الموقع النصّي (Location)", location, { location = it })
        if (locations.isNotEmpty()) {
            LocationDropdown("الموقع الفني", locations, locationId) { selectedId ->
                locationId = selectedId
                locations.firstOrNull { it.id == selectedId }?.let(::applyFunctionalLocationInheritance)
            }
            if (locationInheritanceSource.isNotBlank()) {
                Text("تم توريث بيانات التنظيم من الموقع الفني: $locationInheritanceSource", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
            }
        }
        if (allAssets.isNotEmpty()) {
            AssetDropdownOptional(allAssets, parentAssetId, { parentAssetId = it }, label = "الأصل الأب (اختياري)", excludeId = initial?.id)
        }
        LabeledField("الشركة المصنّعة", manufacturer, { manufacturer = it })
        LabeledField("الموديل (Model)", model, { model = it })
        LabeledField("نوع البناء / Construction Type", constructionType, { constructionType = it })
            OptionDropdown("الحالة", listOf("Draft", "Active", "Running", "Standby", "Under Maintenance", "Breakdown", "Stopped", "Out of Service", "In Storage", "Sent to Vendor", "Refurbishment", "Retired", "Disposed", "Inactive"), status) { status = it }
            OptionDropdown("الأهمية", listOf("Low", "Medium", "High", "Critical"), criticality) { criticality = it }
        }

        if (isStep("Org")) {
            Text("التنظيم والمسؤولية", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            if (companies.isNotEmpty()) {
                val options = listOf("") + companies.filter { activeStatus(it.status) }.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("الشركة", options, selectedMasterLabel(companyId, companyCode, companyName)) { selected ->
                    parseMasterId(selected)?.let { id -> companies.firstOrNull { it.id == id } }?.let { company ->
                        companyId = company.id.toString(); companyCode = company.code; companyName = company.name
                    } ?: run { companyId = ""; companyCode = ""; companyName = "" }
                }
            } else {
                LabeledField("الشركة", companyId, { companyId = it })
                LabeledField("كود الشركة", companyCode, { companyCode = it })
                LabeledField("اسم الشركة", companyName, { companyName = it })
            }

            val companyLong = companyId.toLongOrNull()
            val siteOptionsSource = sites.filter { activeStatus(it.status) && (companyLong == null || it.companyId == companyLong) }
            if (siteOptionsSource.isNotEmpty()) {
                val options = listOf("") + siteOptionsSource.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("الموقع العام", options, selectedMasterLabel(siteId, siteCode, siteName)) { selected ->
                    parseMasterId(selected)?.let { id -> sites.firstOrNull { it.id == id } }?.let { site ->
                        siteId = site.id.toString(); siteCode = site.code; siteName = site.name
                        companies.firstOrNull { it.id == site.companyId }?.let { company ->
                            companyId = company.id.toString(); companyCode = company.code; companyName = company.name
                        }
                    } ?: run { siteId = ""; siteCode = ""; siteName = "" }
                }
            } else {
                LabeledField("الموقع العام", siteId, { siteId = it })
                LabeledField("كود الموقع العام", siteCode, { siteCode = it })
                LabeledField("اسم الموقع العام", siteName, { siteName = it })
            }

            val siteLong = siteId.toLongOrNull()
            val plantOptionsSource = plants.filter { activeStatus(it.status) && (companyLong == null || it.companyId == companyLong) && (siteLong == null || it.siteId == siteLong) }
            if (plantOptionsSource.isNotEmpty()) {
                val options = listOf("") + plantOptionsSource.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("المصنع / الموقع التشغيلي", options, selectedMasterLabel(plantId, plantCode, plantName)) { selected ->
                    parseMasterId(selected)?.let { id -> plants.firstOrNull { it.id == id } }?.let { plant ->
                        plantId = plant.id.toString(); plantCode = plant.code; plantName = plant.name
                        if (maintenancePlantId.isBlank()) { maintenancePlantId = plant.id.toString(); maintenancePlantCode = plant.code; maintenancePlantName = plant.name }
                        if (planningPlantId.isBlank()) { planningPlantId = plant.id.toString(); planningPlantCode = plant.code; planningPlantName = plant.name }
                    } ?: run { plantId = ""; plantCode = ""; plantName = "" }
                }
            } else {
                LabeledField("المصنع / الموقع التشغيلي", plantId, { plantId = it })
                LabeledField("كود المصنع", plantCode, { plantCode = it })
                LabeledField("اسم المصنع", plantName, { plantName = it })
            }

            val plantLong = plantId.toLongOrNull()
            val planningPlantLong = planningPlantId.toLongOrNull() ?: plantLong
            if (plantOptionsSource.isNotEmpty()) {
                val options = listOf("") + plantOptionsSource.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("موقع الصيانة", options, selectedMasterLabel(maintenancePlantId, maintenancePlantCode, maintenancePlantName)) { selected ->
                    parseMasterId(selected)?.let { id -> plants.firstOrNull { it.id == id } }?.let { plant ->
                        maintenancePlantId = plant.id.toString(); maintenancePlantCode = plant.code; maintenancePlantName = plant.name
                    } ?: run { maintenancePlantId = ""; maintenancePlantCode = ""; maintenancePlantName = "" }
                }
                OptionDropdown("موقع التخطيط", options, selectedMasterLabel(planningPlantId, planningPlantCode, planningPlantName)) { selected ->
                    parseMasterId(selected)?.let { id -> plants.firstOrNull { it.id == id } }?.let { plant ->
                        planningPlantId = plant.id.toString(); planningPlantCode = plant.code; planningPlantName = plant.name
                    } ?: run { planningPlantId = ""; planningPlantCode = ""; planningPlantName = "" }
                }
            } else {
                LabeledField("موقع الصيانة", maintenancePlantId, { maintenancePlantId = it })
                LabeledField("موقع التخطيط", planningPlantId, { planningPlantId = it })
            }

            val workCenterOptionsSource = workCenters.filter { activeStatus(it.status) && (plantLong == null || it.plantId == plantLong) }
            if (workCenterOptionsSource.isNotEmpty()) {
                val options = listOf("") + workCenterOptionsSource.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("مركز العمل", options, selectedMasterLabel(workCenterId, workCenterCode, workCenterName)) { selected ->
                    parseMasterId(selected)?.let { id -> workCenters.firstOrNull { it.id == id } }?.let { wc ->
                        workCenterId = wc.id.toString(); workCenterCode = wc.code; workCenterName = wc.name
                    } ?: run { workCenterId = ""; workCenterCode = ""; workCenterName = "" }
                }
            } else {
                LabeledField("مركز العمل", workCenterId, { workCenterId = it })
            }

            val plannerOptionsSource = plannerGroups.filter { activeStatus(it.status) && (planningPlantLong == null || it.planningPlantId == planningPlantLong) }
            if (plannerOptionsSource.isNotEmpty()) {
                val options = listOf("") + plannerOptionsSource.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("مجموعة التخطيط", options, selectedMasterLabel(plannerGroupId, plannerGroupCode, plannerGroupName)) { selected ->
                    parseMasterId(selected)?.let { id -> plannerGroups.firstOrNull { it.id == id } }?.let { group ->
                        plannerGroupId = group.id.toString(); plannerGroupCode = group.code; plannerGroupName = group.name
                    } ?: run { plannerGroupId = ""; plannerGroupCode = ""; plannerGroupName = "" }
                }
            } else {
                LabeledField("مجموعة التخطيط", plannerGroupId, { plannerGroupId = it })
            }

            val departmentOptionsSource = departments.filter { activeStatus(it.status) && (companyLong == null || it.companyId == companyLong) && (siteLong == null || it.siteId == siteLong) }
            if (departmentOptionsSource.isNotEmpty()) {
                val options = listOf("") + departmentOptionsSource.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("الإدارة المالكة", options, selectedMasterLabel(departmentId, departmentCode, departmentName)) { selected ->
                    parseMasterId(selected)?.let { id -> departments.firstOrNull { it.id == id } }?.let { dep ->
                        departmentId = dep.id.toString(); departmentCode = dep.code; departmentName = dep.name
                    } ?: run { departmentId = ""; departmentCode = ""; departmentName = "" }
                }
            } else {
                LabeledField("الإدارة المالكة", departmentId, { departmentId = it })
                LabeledField("كود القسم", departmentCode, { departmentCode = it })
                LabeledField("اسم القسم", departmentName, { departmentName = it })
            }

            val departmentLong = departmentId.toLongOrNull()
            val costCenterOptionsSource = costCenters.filter { activeStatus(it.status) && (companyLong == null || it.companyId == companyLong) && (departmentLong == null || it.departmentId == departmentLong) }
            if (costCenterOptionsSource.isNotEmpty()) {
                val options = listOf("") + costCenterOptionsSource.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("مركز التكلفة", options, selectedMasterLabel(costCenterId, costCenterCode, costCenterName)) { selected ->
                    parseMasterId(selected)?.let { id -> costCenters.firstOrNull { it.id == id } }?.let { cc ->
                        costCenterId = cc.id.toString(); costCenterCode = cc.code; costCenterName = cc.name
                    } ?: run { costCenterId = ""; costCenterCode = ""; costCenterName = "" }
                }
            } else {
                LabeledField("مركز التكلفة", costCenterId, { costCenterId = it })
                LabeledField("كود مركز التكلفة", costCenterCode, { costCenterCode = it })
                LabeledField("اسم مركز التكلفة", costCenterName, { costCenterName = it })
            }
            LabeledField("المسؤول", responsiblePersonId, { responsiblePersonId = it })
            Text("المكان الفعلي", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        LabeledField("المكان الفعلي", physicalLocation, { physicalLocation = it })
        LabeledField("المبنى", building, { building = it })
        LabeledField("الدور", floor, { floor = it })
        LabeledField("الغرفة", room, { room = it })
        LabeledField("المنطقة", area, { area = it })
        LabeledField("الخط", line, { line = it })
        LabeledField("الموضع", position, { position = it })
            val storageOptionsSource = storageLocations.filter { activeStatus(it.status) && (plantLong == null || it.plantId == plantLong) }
            if (storageOptionsSource.isNotEmpty()) {
                val options = listOf("") + storageOptionsSource.map { masterLabel(it.id, it.code, it.name) }
                OptionDropdown("موقع التخزين", options, selectedMasterLabel(storageLocationId, storageLocationCode, storageLocationName)) { selected ->
                    parseMasterId(selected)?.let { id -> storageLocations.firstOrNull { it.id == id } }?.let { storage ->
                        storageLocationId = storage.id.toString(); storageLocationCode = storage.code; storageLocationName = storage.name
                    } ?: run { storageLocationId = ""; storageLocationCode = ""; storageLocationName = "" }
                }
            } else {
                LabeledField("موقع التخزين", storageLocationId, { storageLocationId = it })
                LabeledField("كود موقع التخزين", storageLocationCode, { storageLocationCode = it })
                LabeledField("اسم موقع التخزين", storageLocationName, { storageLocationName = it })
            }
        if (isWorkCenterInherited || isPlannerGroupInherited || isCostCenterInherited || isMaintenancePlantInherited || isPlanningPlantInherited) {
            Text(
                listOfNotNull(
                    "الموقع".takeIf { isLocationInherited },
                    "مركز العمل".takeIf { isWorkCenterInherited },
                    "مجموعة التخطيط".takeIf { isPlannerGroupInherited },
                    "مركز التكلفة".takeIf { isCostCenterInherited },
                    "موقع الصيانة".takeIf { isMaintenancePlantInherited },
                    "موقع التخطيط".takeIf { isPlanningPlantInherited }
                ).joinToString(prefix = "Inherited: "),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
            LabeledField("سبب التجاوز اليدوي للوراثة", manualOverrideReason, { manualOverrideReason = it })
            LabeledField("الشركاء والمسؤولون", partners, { partners = it })
        }

        if (isStep("Finance")) {
            Text("الهوية والمعلومات المالية (اختياري)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        LabeledField("الرقم التسلسلي (Serial)", serialNumber, { serialNumber = it })
        LabeledField("وسم الأصل (Asset Tag)", assetTag, { assetTag = it })
        LabeledField("المورّد (Supplier)", supplier, { supplier = it })
        LabeledField("أمر الشراء (PO)", purchaseOrder, { purchaseOrder = it })
        LabeledField("تكلفة الشراء", purchaseCost, { purchaseCost = it }, numeric = true)
        DateField("تاريخ الاقتناء", acquiredAt) { acquiredAt = it }
        DateField("تاريخ التشغيل / Commissioning", commissioningAt) { commissioningAt = it }
        LabeledField("مرجع الأصل المالي", financialAssetRef, { financialAssetRef = it })
        LabeledField("الحالة المالية", financialStatus, { financialStatus = it })
        LabeledField("القيمة الدفترية", bookValue, { bookValue = it }, numeric = true)
        DateField("تاريخ الرسملة", capitalizationAt) { capitalizationAt = it }
            Text("الضمان (اختياري)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            LabeledField("جهة الضمان", warrantyProvider, { warrantyProvider = it })
            DateField("بداية الضمان", warrantyStart) { warrantyStart = it }
            DateField("نهاية الضمان", warrantyEnd) { warrantyEnd = it }
        }

        if (isStep("Safety")) {
            Text("السلامة والامتثال", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        OptionDropdown("حرج للسلامة", listOf("No", "Yes"), if (safetyCritical) "Yes" else "No") { safetyCritical = it == "Yes" }
        LabeledField("مستوى الخطر", riskLevel, { riskLevel = it })
        LabeledField("التصاريح المطلوبة", requiredPermits, { requiredPermits = it })
        LabeledField("تعليمات السلامة", safetyInstructions, { safetyInstructions = it })
        LabeledField("معدات الوقاية PPE", ppeRequired, { ppeRequired = it })
            OptionDropdown("يتطلب عزل", listOf("No", "Yes"), if (isolationRequired) "Yes" else "No") { isolationRequired = it == "Yes" }
            LabeledField("متطلبات الامتثال", complianceRequirements, { complianceRequirements = it })
        }

        if (isStep("Technical")) {
            Text("بيانات الأصل الخطي (إن وجدت)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        LabeledField("نقطة البداية", linearStartPoint, { linearStartPoint = it })
        LabeledField("نقطة النهاية", linearEndPoint, { linearEndPoint = it })
        LabeledField("الطول", linearLength, { linearLength = it }, numeric = true)
        LabeledField("وحدة الطول", linearUnit, { linearUnit = it })
        LabeledField("المسار", linearRoute, { linearRoute = it })
        Text("بيانات الأصل المتنقل (إن وجدت)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        LabeledField("مسؤول العهدة الحالي", currentCustodian, { currentCustodian = it })
        LabeledField("المكان الفعلي الحالي", currentPhysicalLocation, { currentPhysicalLocation = it })
        LabeledField("آخر موقع معروف", lastKnownLocation, { lastKnownLocation = it })
        OptionDropdown("حالة الحركة", listOf("", "Available", "Checked Out", "In Transit", "Lost", "Unknown"), movementStatus) { movementStatus = it }
        LabeledField("مصروف إلى", checkedOutTo, { checkedOutTo = it })
        DateField("تاريخ الصرف", checkedOutAt) { checkedOutAt = it }
            DateField("تاريخ الإرجاع المتوقع", expectedReturnAt) { expectedReturnAt = it }
            LabeledField("ملاحظات", notes, { notes = it })
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedButton(
                enabled = currentStepIndex() > 0,
                onClick = { currentStep = formSteps[currentStepIndex() - 1].first },
                modifier = Modifier.weight(1f)
            ) { Text("السابق") }
            Button(
                enabled = currentStepIndex() < formSteps.lastIndex,
                onClick = { currentStep = formSteps[currentStepIndex() + 1].first },
                modifier = Modifier.weight(1f)
            ) { Text("التالي") }
        }
        SaveButton(code.isNotBlank() && name.isNotBlank() && assetType.isNotBlank() && assetCategory.isNotBlank()) {
            val today = DateStrings.today()
            onSave(
                AssetEntity(
                    id = initial?.id ?: 0,
                    code = code.trim(),
                    name = name.trim(),
                    groupName = group.ifBlank { "General" },
                    location = location,
                    manufacturer = manufacturer,
                    model = model,
                    status = status,
                    criticality = criticality,
                    installedAt = initial?.installedAt ?: today,
                    lastInspectionAt = initial?.lastInspectionAt ?: today,
                    locationId = locationId,
                    warrantyProvider = warrantyProvider.trim(),
                    warrantyStart = warrantyStart.trim(),
                    warrantyEnd = warrantyEnd.trim(),
                    parentAssetId = parentAssetId,
                    serialNumber = serialNumber.trim(),
                    assetTag = assetTag.trim(),
                    supplier = supplier.trim(),
                    purchaseOrder = purchaseOrder.trim(),
                    purchaseCost = purchaseCost.toDoubleOrNull() ?: 0.0,
                    acquiredAt = acquiredAt.trim(),
                    assetType = assetType,
                    assetCategory = assetCategory.trim(),
                    equipmentCategory = equipmentCategory.trim(),
                    objectType = objectType.trim(),
                    assetClass = assetClass.trim(),
                    assetSubclass = assetSubclass.trim(),
                    description = description.trim(),
                    longDescription = longDescription.trim(),
                    alternativeLabel = alternativeLabel.trim(),
                    externalAssetCode = externalAssetCode.trim(),
                    legacyAssetCode = legacyAssetCode.trim(),
                    barcode = barcode.trim(),
                    qrCode = qrCode.trim(),
                    companyId = companyId.trim(),
                    companyCode = companyCode.trim(),
                    companyName = companyName.trim(),
                    siteId = siteId.trim(),
                    siteCode = siteCode.trim(),
                    siteName = siteName.trim(),
                    plantId = plantId.trim(),
                    plantCode = plantCode.trim(),
                    plantName = plantName.trim(),
                    maintenancePlantId = maintenancePlantId.trim(),
                    maintenancePlantCode = maintenancePlantCode.trim(),
                    maintenancePlantName = maintenancePlantName.trim(),
                    planningPlantId = planningPlantId.trim(),
                    planningPlantCode = planningPlantCode.trim(),
                    planningPlantName = planningPlantName.trim(),
                    workCenterId = workCenterId.trim(),
                    workCenterCode = workCenterCode.trim(),
                    workCenterName = workCenterName.trim(),
                    plannerGroupId = plannerGroupId.trim(),
                    plannerGroupCode = plannerGroupCode.trim(),
                    plannerGroupName = plannerGroupName.trim(),
                    costCenterId = costCenterId.trim(),
                    costCenterCode = costCenterCode.trim(),
                    costCenterName = costCenterName.trim(),
                    departmentId = departmentId.trim(),
                    departmentCode = departmentCode.trim(),
                    departmentName = departmentName.trim(),
                    functionalLocationId = functionalLocationId.trim(),
                    functionalLocationCode = functionalLocationCode.trim(),
                    functionalLocationName = functionalLocationName.trim(),
                    functionalLocationPath = functionalLocationPath.trim(),
                    functionalLocationLevel = functionalLocationLevel,
                    physicalLocation = physicalLocation.trim(),
                    building = building.trim(),
                    floor = floor.trim(),
                    room = room.trim(),
                    area = area.trim(),
                    line = line.trim(),
                    position = position.trim(),
                    storageLocationId = storageLocationId.trim(),
                    storageLocationCode = storageLocationCode.trim(),
                    storageLocationName = storageLocationName.trim(),
                    locationInheritanceSource = locationInheritanceSource.trim(),
                    isLocationInherited = isLocationInherited,
                    isWorkCenterInherited = isWorkCenterInherited,
                    isPlannerGroupInherited = isPlannerGroupInherited,
                    isCostCenterInherited = isCostCenterInherited,
                    isMaintenancePlantInherited = isMaintenancePlantInherited,
                    isPlanningPlantInherited = isPlanningPlantInherited,
                    inheritedFromFunctionalLocationId = inheritedFromFunctionalLocationId.trim(),
                    manualOverrideReason = manualOverrideReason.trim(),
                    responsiblePersonId = responsiblePersonId.trim(),
                    constructionType = constructionType.trim(),
                    commissioningAt = commissioningAt.trim(),
                    financialAssetRef = financialAssetRef.trim(),
                    notes = notes.trim(),
                    partners = partners.trim(),
                    safetyCritical = safetyCritical,
                    riskLevel = riskLevel.trim(),
                    requiredPermits = requiredPermits.trim(),
                    safetyInstructions = safetyInstructions.trim(),
                    ppeRequired = ppeRequired.trim(),
                    isolationRequired = isolationRequired,
                    complianceRequirements = complianceRequirements.trim(),
                    financialStatus = financialStatus.trim(),
                    bookValue = bookValue.toDoubleOrNull() ?: 0.0,
                    capitalizationAt = capitalizationAt.trim(),
                    linearStartPoint = linearStartPoint.trim(),
                    linearEndPoint = linearEndPoint.trim(),
                    linearLength = linearLength.toDoubleOrNull() ?: 0.0,
                    linearUnit = linearUnit.trim(),
                    linearRoute = linearRoute.trim(),
                    currentCustodian = currentCustodian.trim(),
                    currentPhysicalLocation = currentPhysicalLocation.trim(),
                    lastKnownLocation = lastKnownLocation.trim(),
                    movementStatus = movementStatus.trim(),
                    checkedOutTo = checkedOutTo.trim(),
                    checkedOutAt = checkedOutAt.trim(),
                    expectedReturnAt = expectedReturnAt.trim()
                )
            )
        }
    }
}

@Composable
internal fun LocationFormSheet(
    initial: FunctionalLocationEntity?,
    allLocations: List<FunctionalLocationEntity>,
    onDismiss: () -> Unit,
    onSave: (FunctionalLocationEntity) -> Unit
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var parentId by remember { mutableStateOf(initial?.parentId) }
    var status by remember { mutableStateOf(initial?.status ?: "Active") }

    FormSheet(if (initial == null) "إضافة موقع فني" else "تعديل الموقع الفني", onDismiss) {
        LabeledField("الكود (Code)", code, { code = it })
        LabeledField("الاسم (Name)", name, { name = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        LocationDropdown("الموقع الأعلى (Parent)", allLocations, parentId, excludeId = initial?.id) { parentId = it }
        OptionDropdown("الحالة", listOf("Active", "Inactive"), status) { status = it }
        SaveButton(code.isNotBlank() && name.isNotBlank()) {
            onSave(
                FunctionalLocationEntity(
                    id = initial?.id ?: 0,
                    code = code.trim(),
                    name = name.trim(),
                    parentId = parentId,
                    description = description,
                    status = status
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Spare part form
// ---------------------------------------------------------------------------

@Composable
internal fun PartFormSheet(initial: SparePartEntity?, onDismiss: () -> Unit, onSave: (SparePartEntity) -> Unit) {
    var partNumber by remember { mutableStateOf(initial?.partNumber ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var group by remember { mutableStateOf(initial?.equipmentGroup ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "pcs") }
    var onHand by remember { mutableStateOf((initial?.onHandQty ?: 0).toString()) }
    var minQty by remember { mutableStateOf((initial?.minQty ?: 0).toString()) }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var price by remember { mutableStateOf((initial?.lastPrice ?: 0.0).toString()) }

    FormSheet(if (initial == null) "إضافة قطعة غيار" else "تعديل القطعة", onDismiss) {
        LabeledField("رقم القطعة (Part No.)", partNumber, { partNumber = it })
        LabeledField("الاسم (Name)", name, { name = it })
        LabeledField("مجموعة المعدة", group, { group = it })
        LabeledField("الوحدة (Unit)", unit, { unit = it })
        LabeledField("الكمية المتوفرة", onHand, { onHand = it }, numeric = true)
        LabeledField("الحد الأدنى", minQty, { minQty = it }, numeric = true)
        LabeledField("الموقع (Location)", location, { location = it })
        LabeledField("آخر سعر", price, { price = it }, numeric = true)
        SaveButton(partNumber.isNotBlank() && name.isNotBlank()) {
            onSave(
                SparePartEntity(
                    id = initial?.id ?: 0,
                    partNumber = partNumber.trim(),
                    name = name.trim(),
                    equipmentGroup = group.ifBlank { "General" },
                    unit = unit.ifBlank { "pcs" },
                    onHandQty = onHand.toIntOrNull() ?: 0,
                    minQty = minQty.toIntOrNull() ?: 0,
                    location = location,
                    lastPrice = price.toDoubleOrNull() ?: 0.0
                )
            )
        }
    }
}

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
    var assignedTo by remember { mutableStateOf(initial?.assignedTo ?: defaultAssignee) }
    var cost by remember { mutableStateOf((initial?.estimatedCost ?: 0.0).toString()) }
    var dueDays by remember { mutableStateOf("3") }
    var isFailure by remember { mutableStateOf(initial?.isFailure ?: false) }
    var downtime by remember { mutableStateOf((initial?.downtimeHours ?: 0.0).toString()) }
    var laborHours by remember { mutableStateOf((initial?.laborHours ?: 0.0).toString()) }
    var laborRate by remember { mutableStateOf((initial?.laborRate ?: 0.0).toString()) }
    var partsCost by remember { mutableStateOf((initial?.partsCost ?: 0.0).toString()) }
    var requiresPermit by remember { mutableStateOf(initial?.requiresPermit ?: false) }

    FormSheet(if (initial == null) "إنشاء أمر عمل" else "تعديل أمر العمل", onDismiss) {
        LabeledField("العنوان", title, { title = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        AssetDropdown(assets, assetId) { assetId = it }
        OptionDropdown("الأولوية", listOf("Low", "Medium", "High", "Critical"), priority) { priority = it }
        OptionDropdown("الحالة", listOf("Open", "In Progress", "Technically Completed", "Closed"), status) { status = it }
        LabeledField("المسؤول", assignedTo, { assignedTo = it })
        LabeledField("التكلفة التقديرية", cost, { cost = it }, numeric = true)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { LabeledField("ساعات العمالة", laborHours, { laborHours = it }, numeric = true) }
            Box(modifier = Modifier.weight(1f)) { LabeledField("أجر الساعة", laborRate, { laborRate = it }, numeric = true) }
        }
        LabeledField("تكلفة قطع الغيار", partsCost, { partsCost = it }, numeric = true)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("عطل (Breakdown)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isFailure, onCheckedChange = { isFailure = it })
        }
        if (isFailure) {
            LabeledField("مدة التوقف (ساعات)", downtime, { downtime = it }, numeric = true)
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("يتطلّب تصريح عمل (خطر)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = requiresPermit, onCheckedChange = { requiresPermit = it })
        }
        if (initial == null) {
            LabeledField("الاستحقاق خلال (أيام)", dueDays, { dueDays = it }, numeric = true)
        }
        SaveButton(title.isNotBlank() && assetId != 0L) {
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
                    requiresPermit = requiresPermit
                )
            )
        }
    }
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

// ---------------------------------------------------------------------------
// Preventive maintenance form
// ---------------------------------------------------------------------------

@Composable
internal fun PmFormSheet(
    initial: PreventiveMaintenanceEntity?,
    assets: List<AssetEntity>,
    taskLists: List<TaskListEntity>,
    onDismiss: () -> Unit,
    onSave: (PreventiveMaintenanceEntity) -> Unit
) {
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var assetId by remember { mutableStateOf(initial?.assetId ?: assets.firstOrNull()?.id ?: 0L) }
    var frequency by remember { mutableStateOf((initial?.frequencyDays ?: 30).toString()) }
    var duration by remember { mutableStateOf((initial?.estimatedDurationMinutes ?: 60).toString()) }
    var taskListId by remember { mutableStateOf(initial?.taskListId) }

    FormSheet(if (initial == null) "إضافة صيانة دورية" else "تعديل الصيانة الدورية", onDismiss) {
        LabeledField("عنوان المهمة", title, { title = it })
        AssetDropdown(assets, assetId) { assetId = it }
        LabeledField("التكرار (أيام)", frequency, { frequency = it }, numeric = true)
        LabeledField("المدة المقدرة (دقائق)", duration, { duration = it }, numeric = true)
        if (taskLists.isNotEmpty()) {
            TaskListDropdown(taskLists, taskListId) { taskListId = it }
        }
        SaveButton(title.isNotBlank() && assetId != 0L) {
            val today = DateStrings.today()
            val freq = frequency.toIntOrNull() ?: 30
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
                    taskListId = taskListId
                )
            )
        }
    }
}

@Composable
private fun TaskListDropdown(taskLists: List<TaskListEntity>, selectedId: Long?, onSelect: (Long?) -> Unit) {
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

    FormSheet(if (initial == null) "إضافة نقطة قياس" else "تعديل نقطة القياس", onDismiss) {
        LabeledField("اسم النقطة", name, { name = it })
        LabeledField("الوحدة (hr / °C / mm/s ...)", unit, { unit = it })
        AssetDropdown(assets, assetId) { assetId = it }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("عداد تراكمي", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isCounter, onCheckedChange = { isCounter = it })
        }
        LabeledField("الحد الأعلى للتنبيه (اختياري)", limit, { limit = it }, numeric = true)
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
                    lastReadingAt = initial?.lastReadingAt ?: today
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

// ---------------------------------------------------------------------------
// Asset BOM form
// ---------------------------------------------------------------------------

@Composable
private fun PartDropdown(parts: List<SparePartEntity>, selectedId: Long, onSelect: (Long) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val selected = parts.firstOrNull { it.id == selectedId }
    Column {
        Text("القطعة", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    selected?.let { "${it.partNumber} • ${it.name}" } ?: "اختر قطعة",
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                parts.forEach { part ->
                    DropdownMenuItem(text = { Text("${part.partNumber} • ${part.name}") }, onClick = { onSelect(part.id); open = false })
                }
            }
        }
    }
}

@Composable
internal fun BomFormSheet(
    initial: AssetBomItemEntity?,
    assetId: Long,
    parts: List<SparePartEntity>,
    onDismiss: () -> Unit,
    onSave: (AssetBomItemEntity) -> Unit
) {
    var partId by remember { mutableStateOf(initial?.partId ?: parts.firstOrNull()?.id ?: 0L) }
    var quantity by remember { mutableStateOf((initial?.quantity ?: 1).toString()) }

    FormSheet(if (initial == null) "إضافة بند مكوّنات" else "تعديل البند", onDismiss) {
        PartDropdown(parts, partId) { partId = it }
        LabeledField("الكمية", quantity, { quantity = it }, numeric = true)
        SaveButton(partId != 0L && (quantity.toIntOrNull() ?: 0) > 0) {
            onSave(
                AssetBomItemEntity(
                    id = initial?.id ?: 0,
                    assetId = assetId,
                    partId = partId,
                    quantity = quantity.toIntOrNull() ?: 1
                )
            )
        }
    }
}

// ---------------------------------------------------------------------------
// Asset characteristic form
// ---------------------------------------------------------------------------

@Composable
internal fun CharacteristicFormSheet(
    initial: AssetCharacteristicEntity?,
    assetId: Long,
    onDismiss: () -> Unit,
    onSave: (AssetCharacteristicEntity) -> Unit
) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var value by remember { mutableStateOf(initial?.value ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "") }

    FormSheet(if (initial == null) "إضافة خاصية" else "تعديل الخاصية", onDismiss) {
        LabeledField("اسم الخاصية", name, { name = it })
        LabeledField("القيمة", value, { value = it })
        LabeledField("الوحدة (اختياري)", unit, { unit = it })
        SaveButton(name.isNotBlank() && value.isNotBlank()) {
            onSave(
                AssetCharacteristicEntity(
                    id = initial?.id ?: 0,
                    assetId = assetId,
                    name = name.trim(),
                    value = value.trim(),
                    unit = unit.trim()
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

// ---------------------------------------------------------------------------
// Asset movement form (install / transfer / dismantle / retire)
// ---------------------------------------------------------------------------

@Composable
internal fun MovementFormSheet(
    asset: AssetEntity,
    locations: List<FunctionalLocationEntity>,
    onDismiss: () -> Unit,
    onSave: (type: String, locId: Long?, locName: String, notes: String, approvedBy: String, attachment: String) -> Unit
) {
    var type by remember { mutableStateOf(MovementType.INSTALL) }
    var locId by remember { mutableStateOf<Long?>(asset.locationId) }
    var notes by remember { mutableStateOf("") }
    var approvedBy by remember { mutableStateOf("") }
    var attachment by remember { mutableStateOf("") }
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
        if (type == MovementType.TRANSFER) {
            LabeledField("اعتمد بواسطة", approvedBy, { approvedBy = it })
            LabeledField("مرفق / مرجع الموافقة", attachment, { attachment = it })
        }
        SaveButton((!needsLocation || locId != null) && (type != MovementType.TRANSFER || notes.isNotBlank())) {
            val name = locations.firstOrNull { it.id == locId }?.name ?: ""
            onSave(type, if (needsLocation) locId else null, if (needsLocation) name else "", notes.trim(), approvedBy.trim(), attachment.trim())
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
    var finalConfirmation by remember { mutableStateOf(true) }

    FormSheet("تأكيد العملية ${operation.operationNumber}", onDismiss) {
        LabeledField("الساعات الفعلية", actualWork, { actualWork = it }, numeric = true)
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

    FormSheet("إضافة عملية", onDismiss) {
        LabeledField("رقم العملية", operationNumber, { operationNumber = it })
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
                    status = "Open"
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
    var requiredEnd by remember { mutableStateOf(initial?.requiredEnd ?: "") }

    FormSheet(if (initial == null) "بلاغ صيانة جديد" else "تعديل البلاغ", onDismiss) {
        OptionDropdown("النوع", listOf("Corrective", "Breakdown", "Inspection", "Request"), type) { type = it }
        LabeledField("العنوان", title, { title = it })
        LabeledField("وصف المشكلة", description, { description = it }, singleLine = false)
        AssetDropdownOptional(assets, assetId, onSelect = { assetId = it })
        OptionDropdown("الأولوية", listOf("Low", "Medium", "High", "Critical"), priority) { priority = it }
        LabeledField("كود الضرر (اختياري)", damageCode, { damageCode = it })
        LabeledField("كود السبب (اختياري)", causeCode, { causeCode = it })
        DateField("مطلوب الإنجاز قبل", requiredEnd) { requiredEnd = it }
        SaveButton(title.isNotBlank()) {
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
                    linkedOrderId = initial?.linkedOrderId
                )
            )
        }
    }
}

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
internal fun UserFormSheet(initial: UserEntity?, onDismiss: () -> Unit, onSave: (UserEntity) -> Unit) {
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var username by remember { mutableStateOf(initial?.username ?: "") }
    var role by remember { mutableStateOf(initial?.role ?: "Technician") }
    // Never pre-fill the stored (hashed) password. Blank means "keep current" when editing.
    var password by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var active by remember { mutableStateOf(initial?.isActive ?: true) }

    FormSheet(if (initial == null) "إضافة مستخدم" else "تعديل المستخدم", onDismiss) {
        LabeledField("الاسم", name, { name = it })
        LabeledField("اسم المستخدم", username, { username = it })
        OptionDropdown("الدور", listOf("Admin", "Supervisor", "Technician"), role) { role = it }
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
        SaveButton(name.isNotBlank() && username.isNotBlank()) {
            onSave(
                UserEntity(
                    id = initial?.id ?: 0,
                    name = name.trim(),
                    username = username.trim(),
                    role = role,
                    isActive = active,
                    // Blank is resolved by the repository (kept on edit, defaulted on create).
                    password = password
                )
            )
        }
    }
}

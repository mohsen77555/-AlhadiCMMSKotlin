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
private fun OptionDropdown(
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


@Composable
private fun LinearMaintenancePositionFields(
    asset: AssetEntity,
    startPoint: String,
    onStartPointChange: (String) -> Unit,
    endPoint: String,
    onEndPointChange: (String) -> Unit,
    marker: String,
    onMarkerChange: (String) -> Unit,
    horizontalOffset: String,
    onHorizontalOffsetChange: (String) -> Unit,
    verticalOffset: String,
    onVerticalOffsetChange: (String) -> Unit
) {
    Text("الموقع على الأصل الخطي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
    Text(
        "النطاق المتاح: ${linearRangeLabel(asset)}",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f)) {
            LabeledField("نقطة البداية (${asset.linearUnit})", startPoint, onStartPointChange, numeric = true)
        }
        Box(modifier = Modifier.weight(1f)) {
            LabeledField("نقطة النهاية (${asset.linearUnit})", endPoint, onEndPointChange, numeric = true)
        }
    }
    LabeledField("العلامة المرجعية (اختياري)", marker, onMarkerChange)
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Box(modifier = Modifier.weight(1f)) {
            LabeledField("إزاحة أفقية (${asset.linearOffsetUnit})", horizontalOffset, onHorizontalOffsetChange, numeric = true)
        }
        Box(modifier = Modifier.weight(1f)) {
            LabeledField("إزاحة رأسية (${asset.linearOffsetUnit})", verticalOffset, onVerticalOffsetChange, numeric = true)
        }
    }
    if (!optionalLinearRangeValid(asset, startPoint, endPoint)) {
        Text("يجب أن يكون النطاق داخل حدود الأصل وأن تكون النهاية أكبر من أو مساوية للبداية.", color = MaterialTheme.colorScheme.error)
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
    allAssets: List<AssetEntity> = emptyList()
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
    var category by remember { mutableStateOf(initial?.category ?: "Machine") }
    var objectType by remember { mutableStateOf(initial?.objectType ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var maintenancePlant by remember { mutableStateOf(initial?.maintenancePlant ?: "") }
    var planningPlant by remember { mutableStateOf(initial?.planningPlant ?: "") }
    var plannerGroup by remember { mutableStateOf(initial?.plannerGroup ?: "") }
    var mainWorkCenter by remember { mutableStateOf(initial?.mainWorkCenter ?: "") }
    var productionWorkCenter by remember { mutableStateOf(initial?.productionWorkCenter ?: "") }
    var costCenter by remember { mutableStateOf(initial?.costCenter ?: "") }
    var responsiblePerson by remember { mutableStateOf(initial?.responsiblePerson ?: "") }
    var assetNumber by remember { mutableStateOf(initial?.assetNumber ?: "") }
    var constructionYear by remember { mutableStateOf(initial?.constructionYear ?: "") }
    var constructionMonth by remember { mutableStateOf(initial?.constructionMonth ?: "") }
    var startupDate by remember { mutableStateOf(initial?.startupDate ?: "") }
    var partnerName by remember { mutableStateOf(initial?.partnerName ?: "") }
    var partnerRole by remember { mutableStateOf(initial?.partnerRole ?: "") }
    var partnerPhone by remember { mutableStateOf(initial?.partnerPhone ?: "") }
    var partnerEmail by remember { mutableStateOf(initial?.partnerEmail ?: "") }
    var addressLine by remember { mutableStateOf(initial?.addressLine ?: "") }
    var city by remember { mutableStateOf(initial?.city ?: "") }
    var country by remember { mutableStateOf(initial?.country ?: "") }
    var standardClass by remember { mutableStateOf(initial?.standardClass ?: "") }
    var constructionType by remember { mutableStateOf(initial?.constructionType ?: "") }
    var inheritParentCharacteristics by remember { mutableStateOf(initial?.inheritParentCharacteristics ?: true) }
    var isLinearAsset by remember { mutableStateOf(initial?.isLinearAsset ?: false) }
    var linearStartPoint by remember { mutableStateOf(formatLinearNumber(initial?.linearStartPoint ?: 0.0)) }
    var linearEndPoint by remember { mutableStateOf(formatLinearNumber(initial?.linearEndPoint ?: 0.0)) }
    var linearUnit by remember { mutableStateOf(initial?.linearUnit ?: "km") }
    var linearReferencePattern by remember { mutableStateOf(initial?.linearReferencePattern ?: "") }
    var linearRouteCode by remember { mutableStateOf(initial?.linearRouteCode ?: "") }
    var linearStartMarker by remember { mutableStateOf(initial?.linearStartMarker ?: "") }
    var linearEndMarker by remember { mutableStateOf(initial?.linearEndMarker ?: "") }
    var linearStartMarkerDistance by remember { mutableStateOf(formatLinearNumber(initial?.linearStartMarkerDistance ?: 0.0)) }
    var linearEndMarkerDistance by remember { mutableStateOf(formatLinearNumber(initial?.linearEndMarkerDistance ?: 0.0)) }
    var linearMarkerUnit by remember { mutableStateOf(initial?.linearMarkerUnit ?: "km") }
    var linearHorizontalOffset by remember { mutableStateOf(formatLinearNumber(initial?.linearHorizontalOffset ?: 0.0)) }
    var linearVerticalOffset by remember { mutableStateOf(formatLinearNumber(initial?.linearVerticalOffset ?: 0.0)) }
    var linearOffsetUnit by remember { mutableStateOf(initial?.linearOffsetUnit ?: "m") }
    var linearDirection by remember { mutableStateOf(initial?.linearDirection ?: "Both") }
    var networkObjectCode by remember { mutableStateOf(initial?.networkObjectCode ?: "") }
    var networkObjectType by remember { mutableStateOf(initial?.networkObjectType ?: "") }
    var networkRelation by remember { mutableStateOf(initial?.networkRelation ?: "") }
    var networkAttributes by remember { mutableStateOf(initial?.networkAttributes ?: "") }
    var linearStartLatitude by remember { mutableStateOf(initial?.linearStartLatitude?.let(::formatLinearNumber) ?: "") }
    var linearStartLongitude by remember { mutableStateOf(initial?.linearStartLongitude?.let(::formatLinearNumber) ?: "") }
    var linearEndLatitude by remember { mutableStateOf(initial?.linearEndLatitude?.let(::formatLinearNumber) ?: "") }
    var linearEndLongitude by remember { mutableStateOf(initial?.linearEndLongitude?.let(::formatLinearNumber) ?: "") }

    val linearStartValue = linearStartPoint.toDoubleOrNull()
    val linearEndValue = linearEndPoint.toDoubleOrNull()
    val linearRangeValid = !isLinearAsset || (linearStartValue != null && linearEndValue != null && linearEndValue > linearStartValue)
    val markerDistancesValid = !isLinearAsset ||
        ((linearStartMarkerDistance.toDoubleOrNull() ?: -1.0) >= 0.0 && (linearEndMarkerDistance.toDoubleOrNull() ?: -1.0) >= 0.0)
    val coordinatesValid = !isLinearAsset || listOf(
        linearStartLatitude to (-90.0..90.0),
        linearEndLatitude to (-90.0..90.0),
        linearStartLongitude to (-180.0..180.0),
        linearEndLongitude to (-180.0..180.0)
    ).all { (value, range) -> value.isBlank() || value.toDoubleOrNull()?.let { it in range } == true }

    FormSheet(if (initial == null) "إضافة أصل جديد" else "تعديل الأصل", onDismiss) {
        Text("البيانات الأساسية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("كود الأصل", code, { code = it })
        LabeledField("اسم الأصل", name, { name = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        OptionDropdown(
            label = "فئة الأصل",
            options = assetCategoryOptions,
            selected = category,
            display = ::assetCategoryLabel
        ) { category = it }
        LabeledField("نوع الأصل (مثال: مضخة، محرك، ناقل)", objectType, { objectType = it })
        LabeledField("المجموعة", group, { group = it })
        LabeledField("الموقع النصّي", location, { location = it })
        if (locations.isNotEmpty()) {
            LocationDropdown("الموقع الفني", locations, locationId) { locationId = it }
        }
        if (allAssets.isNotEmpty()) {
            AssetDropdownOptional(allAssets, parentAssetId, { parentAssetId = it }, label = "الأصل الأب (اختياري)", excludeId = initial?.id)
        }

        Text("التصنيف", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("التصنيف القياسي", standardClass, { standardClass = it })
        LabeledField("نوع الإنشاء المشترك", constructionType, { constructionType = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("توريث خصائص الأصل الأب", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = inheritParentCharacteristics, onCheckedChange = { inheritParentCharacteristics = it })
        }

        Text("الأصل الخطي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("تفعيل البيانات الخطية", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isLinearAsset, onCheckedChange = { isLinearAsset = it })
        }
        if (isLinearAsset) {
            LabeledField("رمز المسار / الخط", linearRouteCode, { linearRouteCode = it })
            LabeledField("نمط المرجع الخطي", linearReferencePattern, { linearReferencePattern = it })
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { LabeledField("نقطة البداية", linearStartPoint, { linearStartPoint = it }, numeric = true) }
                Box(modifier = Modifier.weight(1f)) { LabeledField("نقطة النهاية", linearEndPoint, { linearEndPoint = it }, numeric = true) }
            }
            OptionDropdown("وحدة القياس", linearUnitOptions, linearUnit, display = ::linearUnitLabel) { linearUnit = it }
            if (linearRangeValid) {
                Text(
                    "الطول: ${formatLinearNumber((linearEndValue ?: 0.0) - (linearStartValue ?: 0.0))} $linearUnit",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text("يجب أن تكون نقطة النهاية أكبر من نقطة البداية.", color = MaterialTheme.colorScheme.error)
            }

            Text("العلامات المرجعية", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { LabeledField("علامة البداية", linearStartMarker, { linearStartMarker = it }) }
                Box(modifier = Modifier.weight(1f)) { LabeledField("علامة النهاية", linearEndMarker, { linearEndMarker = it }) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { LabeledField("المسافة من علامة البداية", linearStartMarkerDistance, { linearStartMarkerDistance = it }, numeric = true) }
                Box(modifier = Modifier.weight(1f)) { LabeledField("المسافة إلى علامة النهاية", linearEndMarkerDistance, { linearEndMarkerDistance = it }, numeric = true) }
            }
            OptionDropdown("وحدة مسافة العلامات", linearUnitOptions, linearMarkerUnit, display = ::linearUnitLabel) { linearMarkerUnit = it }

            Text("الإزاحات والاتجاه", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { LabeledField("الإزاحة الأفقية", linearHorizontalOffset, { linearHorizontalOffset = it }, numeric = true) }
                Box(modifier = Modifier.weight(1f)) { LabeledField("الإزاحة الرأسية", linearVerticalOffset, { linearVerticalOffset = it }, numeric = true) }
            }
            OptionDropdown("وحدة الإزاحة", linearOffsetUnitOptions, linearOffsetUnit, display = ::linearUnitLabel) { linearOffsetUnit = it }
            OptionDropdown("اتجاه الأصل", linearDirectionOptions, linearDirection, display = ::linearDirectionLabel) { linearDirection = it }

            Text("الإحداثيات (اختياري)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { LabeledField("خط عرض البداية", linearStartLatitude, { linearStartLatitude = it }, numeric = true) }
                Box(modifier = Modifier.weight(1f)) { LabeledField("خط طول البداية", linearStartLongitude, { linearStartLongitude = it }, numeric = true) }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                Box(modifier = Modifier.weight(1f)) { LabeledField("خط عرض النهاية", linearEndLatitude, { linearEndLatitude = it }, numeric = true) }
                Box(modifier = Modifier.weight(1f)) { LabeledField("خط طول النهاية", linearEndLongitude, { linearEndLongitude = it }, numeric = true) }
            }
            if (!coordinatesValid) Text("تحقق من صحة الإحداثيات.", color = MaterialTheme.colorScheme.error)

            Text("ربط الشبكة", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
            LabeledField("رمز كائن الشبكة", networkObjectCode, { networkObjectCode = it })
            OptionDropdown("نوع كائن الشبكة", networkObjectTypeOptions, networkObjectType, display = ::networkObjectTypeLabel) { networkObjectType = it }
            OptionDropdown("نوع العلاقة", networkRelationOptions, networkRelation, display = ::networkRelationLabel) { networkRelation = it }
            LabeledField("سمات الشبكة", networkAttributes, { networkAttributes = it }, singleLine = false)
        }

        Text("البيانات الفنية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("الشركة المصنّعة", manufacturer, { manufacturer = it })
        LabeledField("الموديل", model, { model = it })
        LabeledField("سنة الصنع", constructionYear, { constructionYear = it }, numeric = true)
        LabeledField("شهر الصنع", constructionMonth, { constructionMonth = it }, numeric = true)
        DateField("تاريخ بدء التشغيل", startupDate) { startupDate = it }
        OptionDropdown("الحالة", listOf("Running", "Warning", "Stopped", "Under Maintenance", "Standby", "Retired"), status) { status = it }
        OptionDropdown("الأهمية", listOf("Low", "Medium", "High", "Critical"), criticality) { criticality = it }

        Text("التنظيم والمسؤولية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("مصنع الصيانة", maintenancePlant, { maintenancePlant = it })
        LabeledField("مصنع التخطيط", planningPlant, { planningPlant = it })
        LabeledField("مجموعة المخططين", plannerGroup, { plannerGroup = it })
        LabeledField("مركز العمل الرئيسي", mainWorkCenter, { mainWorkCenter = it })
        LabeledField("مركز عمل الإنتاج", productionWorkCenter, { productionWorkCenter = it })
        LabeledField("مركز التكلفة", costCenter, { costCenter = it })
        LabeledField("الشخص المسؤول", responsiblePerson, { responsiblePerson = it })

        Text("الهوية والمعلومات المالية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("الرقم التسلسلي", serialNumber, { serialNumber = it })
        LabeledField("وسم الأصل", assetTag, { assetTag = it })
        LabeledField("رقم الأصل المالي", assetNumber, { assetNumber = it })
        LabeledField("المورّد", supplier, { supplier = it })
        LabeledField("أمر الشراء", purchaseOrder, { purchaseOrder = it })
        LabeledField("تكلفة الشراء", purchaseCost, { purchaseCost = it }, numeric = true)
        DateField("تاريخ الاقتناء", acquiredAt) { acquiredAt = it }

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

        Text("الضمان", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("جهة الضمان", warrantyProvider, { warrantyProvider = it })
        DateField("بداية الضمان", warrantyStart) { warrantyStart = it }
        DateField("نهاية الضمان", warrantyEnd) { warrantyEnd = it }
        SaveButton(code.isNotBlank() && name.isNotBlank() && linearRangeValid && markerDistancesValid && coordinatesValid) {
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
                    category = category,
                    objectType = objectType.trim(),
                    description = description.trim(),
                    maintenancePlant = maintenancePlant.trim(),
                    planningPlant = planningPlant.trim(),
                    plannerGroup = plannerGroup.trim(),
                    mainWorkCenter = mainWorkCenter.trim(),
                    productionWorkCenter = productionWorkCenter.trim(),
                    costCenter = costCenter.trim(),
                    responsiblePerson = responsiblePerson.trim(),
                    assetNumber = assetNumber.trim(),
                    constructionYear = constructionYear.trim(),
                    constructionMonth = constructionMonth.trim(),
                    startupDate = startupDate.trim(),
                    partnerName = partnerName.trim(),
                    partnerRole = partnerRole,
                    partnerPhone = partnerPhone.trim(),
                    partnerEmail = partnerEmail.trim(),
                    addressLine = addressLine.trim(),
                    city = city.trim(),
                    country = country.trim(),
                    standardClass = standardClass.trim(),
                    constructionType = constructionType.trim(),
                    inheritParentCharacteristics = inheritParentCharacteristics,
                    isLinearAsset = isLinearAsset,
                    linearStartPoint = if (isLinearAsset) linearStartValue ?: 0.0 else 0.0,
                    linearEndPoint = if (isLinearAsset) linearEndValue ?: 0.0 else 0.0,
                    linearUnit = linearUnit,
                    linearReferencePattern = if (isLinearAsset) linearReferencePattern.trim() else "",
                    linearRouteCode = if (isLinearAsset) linearRouteCode.trim() else "",
                    linearStartMarker = if (isLinearAsset) linearStartMarker.trim() else "",
                    linearEndMarker = if (isLinearAsset) linearEndMarker.trim() else "",
                    linearStartMarkerDistance = if (isLinearAsset) linearStartMarkerDistance.toDoubleOrNull() ?: 0.0 else 0.0,
                    linearEndMarkerDistance = if (isLinearAsset) linearEndMarkerDistance.toDoubleOrNull() ?: 0.0 else 0.0,
                    linearMarkerUnit = linearMarkerUnit,
                    linearHorizontalOffset = if (isLinearAsset) linearHorizontalOffset.toDoubleOrNull() ?: 0.0 else 0.0,
                    linearVerticalOffset = if (isLinearAsset) linearVerticalOffset.toDoubleOrNull() ?: 0.0 else 0.0,
                    linearOffsetUnit = linearOffsetUnit,
                    linearDirection = linearDirection,
                    networkObjectCode = if (isLinearAsset) networkObjectCode.trim() else "",
                    networkObjectType = if (isLinearAsset) networkObjectType else "",
                    networkRelation = if (isLinearAsset) networkRelation else "",
                    networkAttributes = if (isLinearAsset) networkAttributes.trim() else "",
                    linearStartLatitude = if (isLinearAsset) linearStartLatitude.toDoubleOrNull() else null,
                    linearStartLongitude = if (isLinearAsset) linearStartLongitude.toDoubleOrNull() else null,
                    linearEndLatitude = if (isLinearAsset) linearEndLatitude.toDoubleOrNull() else null,
                    linearEndLongitude = if (isLinearAsset) linearEndLongitude.toDoubleOrNull() else null
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
internal fun PartFormSheet(
    initial: SparePartEntity?,
    profiles: List<SerialNumberProfileEntity>,
    onDismiss: () -> Unit,
    onSave: (SparePartEntity) -> Unit
) {
    var partNumber by remember { mutableStateOf(initial?.partNumber ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var group by remember { mutableStateOf(initial?.equipmentGroup ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "pcs") }
    var onHand by remember { mutableStateOf((initial?.onHandQty ?: 0).toString()) }
    var minQty by remember { mutableStateOf((initial?.minQty ?: 0).toString()) }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var price by remember { mutableStateOf((initial?.lastPrice ?: 0.0).toString()) }
    var serializationActive by remember { mutableStateOf(initial?.serializationActive ?: false) }
    var serialProfileId by remember { mutableStateOf(initial?.serialProfileId) }

    FormSheet(if (initial == null) "إضافة قطعة غيار" else "تعديل القطعة", onDismiss) {
        LabeledField("رقم القطعة (Part No.)", partNumber, { partNumber = it })
        LabeledField("الاسم (Name)", name, { name = it })
        LabeledField("مجموعة المعدة", group, { group = it })
        LabeledField("الوحدة (Unit)", unit, { unit = it })
        LabeledField("الكمية المتوفرة", onHand, { onHand = it }, numeric = true)
        LabeledField("الحد الأدنى", minQty, { minQty = it }, numeric = true)
        LabeledField("الموقع (Location)", location, { location = it })
        LabeledField("آخر سعر", price, { price = it }, numeric = true)
        Text("التتبع الفردي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("تفعيل الأرقام التسلسلية", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = serializationActive, onCheckedChange = { serializationActive = it })
        }
        if (serializationActive) {
            SerialProfileDropdown(profiles, serialProfileId) { serialProfileId = it }
            if (profiles.isEmpty()) Text("أنشئ ملف تتبع من وحدة الأرقام التسلسلية أولاً.", color = MaterialTheme.colorScheme.error)
        }
        SaveButton(partNumber.isNotBlank() && name.isNotBlank() && (!serializationActive || serialProfileId != null)) {
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
                    lastPrice = price.toDoubleOrNull() ?: 0.0,
                    serializationActive = serializationActive,
                    serialProfileId = if (serializationActive) serialProfileId else null
                )
            )
        }
    }
}


@Composable
private fun SerialProfileDropdown(
    profiles: List<SerialNumberProfileEntity>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val selected = profiles.firstOrNull { it.id == selectedId }
    Column {
        Text("ملف التتبع", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.let { "${it.code} • ${it.name}" } ?: "اختر ملفاً", modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                profiles.forEach { profile ->
                    DropdownMenuItem(text = { Text("${profile.code} • ${profile.name}") }, onClick = { onSelect(profile.id); open = false })
                }
            }
        }
    }
}

@Composable
private fun WorkOrderDropdownOptional(
    workOrders: List<WorkOrderEntity>,
    selectedId: Long?,
    onSelect: (Long?) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val selected = workOrders.firstOrNull { it.id == selectedId }
    Column {
        Text("أمر العمل (اختياري)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(selected?.let { "#${it.id} • ${it.title}" } ?: "بدون", modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                DropdownMenuItem(text = { Text("بدون") }, onClick = { onSelect(null); open = false })
                workOrders.forEach { order ->
                    DropdownMenuItem(text = { Text("#${order.id} • ${order.title}") }, onClick = { onSelect(order.id); open = false })
                }
            }
        }
    }
}

@Composable
internal fun SerialProfileFormSheet(
    initial: SerialNumberProfileEntity?,
    onDismiss: () -> Unit,
    onSave: (SerialNumberProfileEntity) -> Unit
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var requireOnReceipt by remember { mutableStateOf(initial?.requireOnReceipt ?: true) }
    var requireOnIssue by remember { mutableStateOf(initial?.requireOnIssue ?: true) }
    var autoCreate by remember { mutableStateOf(initial?.autoCreate ?: true) }
    var equipmentRequired by remember { mutableStateOf(initial?.equipmentRequired ?: false) }
    var stockCheckMode by remember { mutableStateOf(initial?.stockCheckMode ?: "Block") }
    var allowManualStockEdit by remember { mutableStateOf(initial?.allowManualStockEdit ?: false) }
    var equipmentCategory by remember { mutableStateOf(initial?.equipmentCategory ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }

    FormSheet(if (initial == null) "ملف تتبع جديد" else "تعديل ملف التتبع", onDismiss) {
        LabeledField("الكود", code, { code = it })
        LabeledField("الاسم", name, { name = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        OptionDropdown("فحص توافق المخزون", serialStockCheckOptions, stockCheckMode, display = ::serialStockCheckLabel) { stockCheckMode = it }
        LabeledField("فئة الأصل المطلوبة (اختياري)", equipmentCategory, { equipmentCategory = it })
        listOf(
            "إلزام الرقم عند الاستلام" to (requireOnReceipt to { value: Boolean -> requireOnReceipt = value }),
            "إلزام الرقم عند الصرف" to (requireOnIssue to { value: Boolean -> requireOnIssue = value }),
            "إنشاء السجل تلقائياً عند الاستلام" to (autoCreate to { value: Boolean -> autoCreate = value }),
            "يتطلب ارتباطاً بأصل" to (equipmentRequired to { value: Boolean -> equipmentRequired = value }),
            "السماح بالتسوية اليدوية" to (allowManualStockEdit to { value: Boolean -> allowManualStockEdit = value })
        ).forEach { (label, pair) ->
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text(label, modifier = Modifier.weight(1f))
                Switch(checked = pair.first, onCheckedChange = pair.second)
            }
        }
        SaveButton(code.isNotBlank() && name.isNotBlank()) {
            onSave(
                SerialNumberProfileEntity(
                    id = initial?.id ?: 0,
                    code = code,
                    name = name,
                    requireOnReceipt = requireOnReceipt,
                    requireOnIssue = requireOnIssue,
                    autoCreate = autoCreate,
                    equipmentRequired = equipmentRequired,
                    stockCheckMode = stockCheckMode,
                    allowManualStockEdit = allowManualStockEdit,
                    equipmentCategory = equipmentCategory,
                    description = description
                )
            )
        }
    }
}

@Composable
internal fun SerialMasterFormSheet(
    parts: List<SparePartEntity>,
    onDismiss: () -> Unit,
    onSave: (SerialMasterRequest) -> Unit
) {
    var partId by remember { mutableStateOf(parts.firstOrNull()?.id ?: 0L) }
    var serialNumber by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    FormSheet("إنشاء سجل رقم تسلسلي", onDismiss) {
        PartDropdown(parts, partId) { partId = it }
        LabeledField("الرقم التسلسلي", serialNumber, { serialNumber = it })
        LabeledField("ملاحظات", notes, { notes = it }, singleLine = false)
        SaveButton(partId != 0L && serialNumber.isNotBlank()) {
            onSave(SerialMasterRequest(partId = partId, serialNumber = serialNumber, notes = notes))
        }
    }
}

@Composable
internal fun SerialReceiptFormSheet(
    part: SparePartEntity,
    onDismiss: () -> Unit,
    onSave: (SerializedReceiptRequest) -> Unit
) {
    var serialText by remember { mutableStateOf("") }
    var plant by remember { mutableStateOf("") }
    var storageLocation by remember { mutableStateOf(part.location) }
    var stockType by remember { mutableStateOf("Unrestricted") }
    var batch by remember { mutableStateOf("") }
    var vendor by remember { mutableStateOf("") }
    var note by remember { mutableStateOf("") }
    val numbers = parseSerialInput(serialText)

    FormSheet("استلام ${part.partNumber}", onDismiss) {
        Text("أدخل رقماً واحداً في كل سطر أو افصل الأرقام بفاصلة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        LabeledField("الأرقام التسلسلية", serialText, { serialText = it }, singleLine = false)
        Text("عدد الوحدات: ${numbers.size}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        LabeledField("الموقع التشغيلي", plant, { plant = it })
        LabeledField("موقع التخزين", storageLocation, { storageLocation = it })
        OptionDropdown("نوع المخزون", serialStockTypeOptions, stockType, display = ::serialStockTypeLabel) { stockType = it }
        LabeledField("الدفعة (اختياري)", batch, { batch = it })
        LabeledField("المورّد (اختياري)", vendor, { vendor = it })
        LabeledField("ملاحظة", note, { note = it }, singleLine = false)
        SaveButton(numbers.isNotEmpty() && storageLocation.isNotBlank()) {
            onSave(SerializedReceiptRequest(part.id, numbers, plant, storageLocation, stockType, batch, vendor, note))
        }
    }
}

@Composable
internal fun SerialIssueFormSheet(
    part: SparePartEntity,
    serials: List<SerialNumberEntity>,
    workOrders: List<WorkOrderEntity>,
    onDismiss: () -> Unit,
    onSave: (SerializedIssueRequest) -> Unit
) {
    var selectedIds by remember(part.id, serials) { mutableStateOf<Set<Long>>(emptySet()) }
    var workOrderId by remember { mutableStateOf<Long?>(null) }
    var note by remember { mutableStateOf("") }
    FormSheet("صرف ${part.partNumber}", onDismiss) {
        Text("حدد الوحدات المطلوب صرفها.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        serials.forEach { serial ->
            Row(
                modifier = Modifier.fillMaxWidth().clickable {
                    selectedIds = if (serial.id in selectedIds) selectedIds - serial.id else selectedIds + serial.id
                },
                verticalAlignment = Alignment.CenterVertically
            ) {
                Checkbox(
                    checked = serial.id in selectedIds,
                    onCheckedChange = { checked -> selectedIds = if (checked) selectedIds + serial.id else selectedIds - serial.id }
                )
                Text(serial.serialNumber, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium)
                Text(serial.storageLocation, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        WorkOrderDropdownOptional(workOrders, workOrderId) { workOrderId = it }
        LabeledField("ملاحظة", note, { note = it }, singleLine = false)
        SaveButton(selectedIds.isNotEmpty()) {
            onSave(SerializedIssueRequest(part.id, selectedIds.toList(), workOrderId, note))
        }
    }
}

@Composable
internal fun SerialTransferFormSheet(
    serial: SerialNumberEntity,
    onDismiss: () -> Unit,
    onSave: (SerialTransferRequest) -> Unit
) {
    var plant by remember { mutableStateOf(serial.plant) }
    var storageLocation by remember { mutableStateOf(serial.storageLocation) }
    var stockType by remember { mutableStateOf(serial.stockType.ifBlank { "Unrestricted" }) }
    var batch by remember { mutableStateOf(serial.batch) }
    var note by remember { mutableStateOf("") }
    FormSheet("نقل ${serial.serialNumber}", onDismiss) {
        LabeledField("الموقع التشغيلي", plant, { plant = it })
        LabeledField("موقع التخزين", storageLocation, { storageLocation = it })
        OptionDropdown("نوع المخزون", serialStockTypeOptions, stockType, display = ::serialStockTypeLabel) { stockType = it }
        LabeledField("الدفعة", batch, { batch = it })
        LabeledField("ملاحظة", note, { note = it }, singleLine = false)
        SaveButton(storageLocation.isNotBlank()) {
            onSave(SerialTransferRequest(serial.id, plant, storageLocation, stockType, batch, note))
        }
    }
}

@Composable
internal fun SerialInstallFormSheet(
    serial: SerialNumberEntity,
    assets: List<AssetEntity>,
    onDismiss: () -> Unit,
    onSave: (SerialInstallRequest) -> Unit
) {
    var assetId by remember { mutableStateOf<Long?>(null) }
    var note by remember { mutableStateOf("") }
    val availableAssets = assets.filter { it.linkedSerialId == null || it.linkedSerialId == serial.id }
    FormSheet("تركيب ${serial.serialNumber}", onDismiss) {
        AssetDropdownOptional(availableAssets, assetId, { assetId = it }, label = "الأصل")
        LabeledField("ملاحظة", note, { note = it }, singleLine = false)
        SaveButton(assetId != null) {
            onSave(SerialInstallRequest(serial.id, assetId!!, note))
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
    var linearStartPoint by remember { mutableStateOf(initial?.linearStartPoint?.let(::formatLinearNumber) ?: "") }
    var linearEndPoint by remember { mutableStateOf(initial?.linearEndPoint?.let(::formatLinearNumber) ?: "") }
    var linearMarker by remember { mutableStateOf(initial?.linearMarker ?: "") }
    var linearHorizontalOffset by remember { mutableStateOf(initial?.linearHorizontalOffset?.let(::formatLinearNumber) ?: "") }
    var linearVerticalOffset by remember { mutableStateOf(initial?.linearVerticalOffset?.let(::formatLinearNumber) ?: "") }
    val selectedAsset = assets.firstOrNull { it.id == assetId }
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
        SaveButton(title.isNotBlank() && assetId != 0L && linearReferenceValid) {
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
                    linearVerticalOffset = if (selectedAsset?.isLinearAsset == true) linearVerticalOffset.toDoubleOrNull() else null
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


@Composable
private fun BomParentDropdown(
    items: List<AssetBomItemEntity>,
    selectedId: Long?,
    excludeId: Long?,
    parts: List<SparePartEntity>,
    assets: List<AssetEntity>,
    onSelect: (Long?) -> Unit
) {
    var open by remember { mutableStateOf(false) }
    val partMap = parts.associateBy { it.id }
    val assetMap = assets.associateBy { it.id }
    val options = items.filter { it.id != excludeId }
    val selected = items.firstOrNull { it.id == selectedId }
    Column {
        Text("البند الأب (اختياري)", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
        Box {
            OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) {
                Text(
                    selected?.let { "${it.itemNumber} • ${bomItemObjectLabel(it, partMap, assetMap)}" } ?: "بدون",
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Icon(Icons.Filled.ArrowDropDown, contentDescription = null)
            }
            DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                DropdownMenuItem(text = { Text("بدون") }, onClick = { onSelect(null); open = false })
                options.forEach { item ->
                    DropdownMenuItem(
                        text = { Text("${item.itemNumber} • ${bomItemObjectLabel(item, partMap, assetMap)}") },
                        onClick = { onSelect(item.id); open = false }
                    )
                }
            }
        }
    }
}

@Composable
internal fun BomHeaderFormSheet(
    initial: AssetBomHeaderEntity?,
    asset: AssetEntity,
    onDismiss: () -> Unit,
    onSave: (AssetBomHeaderEntity) -> Unit
) {
    var code by remember { mutableStateOf(initial?.code ?: "${asset.code}-MAIN") }
    var name by remember { mutableStateOf(initial?.name ?: "قائمة الصيانة الرئيسية") }
    var category by remember { mutableStateOf(initial?.category ?: "Asset") }
    var usage by remember { mutableStateOf(initial?.usage ?: "Maintenance") }
    var alternative by remember { mutableStateOf(initial?.alternative ?: "01") }
    var status by remember { mutableStateOf(initial?.status ?: "Active") }
    var validFrom by remember { mutableStateOf(initial?.validFrom ?: "") }
    var validTo by remember { mutableStateOf(initial?.validTo ?: "") }
    var revision by remember { mutableStateOf(initial?.revision ?: "A") }
    var assignmentType by remember { mutableStateOf(initial?.assignmentType ?: "Direct") }
    var constructionType by remember { mutableStateOf(initial?.constructionType ?: asset.constructionType) }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    val datesValid = validFrom.isBlank() || validTo.isBlank() || validFrom <= validTo
    val assignmentValid = assignmentType == "Direct" || constructionType.isNotBlank()

    FormSheet(if (initial == null) "إنشاء قائمة مكونات" else "تعديل قائمة المكونات", onDismiss) {
        LabeledField("كود القائمة", code, { code = it })
        LabeledField("اسم القائمة", name, { name = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        OptionDropdown("الفئة", bomCategoryOptions, category, display = ::bomCategoryLabel) { category = it }
        OptionDropdown("الاستخدام", bomUsageOptions, usage, display = ::bomUsageLabel) { usage = it }
        OptionDropdown("طريقة التعيين", bomAssignmentOptions, assignmentType, display = ::bomAssignmentLabel) { assignmentType = it }
        if (assignmentType == "Indirect") {
            LabeledField("نوع الإنشاء المشترك", constructionType, { constructionType = it })
            Text("ستظهر هذه القائمة لكل أصل يحمل نوع الإنشاء نفسه.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { LabeledField("رقم البديل", alternative, { alternative = it }) }
            Box(modifier = Modifier.weight(1f)) { LabeledField("المراجعة", revision, { revision = it }) }
        }
        OptionDropdown("الحالة", bomHeaderStatusOptions, status, display = ::bomStatusLabel) { status = it }
        DateField("صالحة من", validFrom) { validFrom = it }
        DateField("صالحة إلى", validTo) { validTo = it }
        if (!datesValid) Text("تاريخ البداية يجب ألا يتجاوز تاريخ النهاية.", color = MaterialTheme.colorScheme.error)
        SaveButton(code.isNotBlank() && name.isNotBlank() && datesValid && assignmentValid) {
            onSave(
                AssetBomHeaderEntity(
                    id = initial?.id ?: 0,
                    assetId = if (assignmentType == "Direct") asset.id else null,
                    code = code.trim(),
                    name = name.trim(),
                    category = category,
                    usage = usage,
                    alternative = alternative.trim().ifBlank { "01" },
                    status = status,
                    validFrom = validFrom,
                    validTo = validTo,
                    revision = revision.trim(),
                    assignmentType = assignmentType,
                    constructionType = if (assignmentType == "Indirect") constructionType.trim() else "",
                    description = description.trim()
                )
            )
        }
    }
}

@Composable
internal fun BomItemFormSheet(
    initial: AssetBomItemEntity?,
    header: AssetBomHeaderEntity,
    currentAsset: AssetEntity,
    parts: List<SparePartEntity>,
    allAssets: List<AssetEntity>,
    existingItems: List<AssetBomItemEntity>,
    onDismiss: () -> Unit,
    onSave: (AssetBomItemEntity) -> Unit
) {
    val suggestedNumber = ((existingItems.maxOfOrNull { it.itemNumber } ?: 0) + 10).coerceAtLeast(10)
    var itemNumber by remember { mutableStateOf((initial?.itemNumber ?: suggestedNumber).toString()) }
    var itemCategory by remember { mutableStateOf(initial?.itemCategory ?: "Stock") }
    var partId by remember { mutableStateOf(initial?.partId ?: parts.firstOrNull()?.id ?: 0L) }
    var assemblyAssetId by remember { mutableStateOf(initial?.assemblyAssetId) }
    var quantity by remember { mutableStateOf((initial?.quantity ?: 1).toString()) }
    var status by remember { mutableStateOf(initial?.status ?: "Active") }
    var validFrom by remember { mutableStateOf(initial?.validFrom ?: "") }
    var validTo by remember { mutableStateOf(initial?.validTo ?: "") }
    var isCritical by remember { mutableStateOf(initial?.isCritical ?: false) }
    var useInOrders by remember { mutableStateOf(initial?.useInOrders ?: true) }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }
    var parentItemId by remember { mutableStateOf(initial?.parentItemId) }
    var alternativeGroup by remember { mutableStateOf(initial?.alternativeGroup ?: "") }
    var isAlternative by remember { mutableStateOf(initial?.isAlternative ?: false) }

    val datesValid = validFrom.isBlank() || validTo.isBlank() || validFrom <= validTo
    val numberValid = (itemNumber.toIntOrNull() ?: 0) > 0
    val quantityValid = (quantity.toIntOrNull() ?: 0) > 0
    val objectValid = when (itemCategory) {
        "Stock", "NonStock" -> partId != 0L
        "Assembly" -> assemblyAssetId != null && assemblyAssetId != currentAsset.id
        "Text" -> notes.isNotBlank()
        else -> false
    }

    FormSheet(if (initial == null) "إضافة بند مكونات" else "تعديل بند المكونات", onDismiss) {
        Text("${header.code} • ${header.name}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        LabeledField("رقم البند", itemNumber, { itemNumber = it }, numeric = true)
        OptionDropdown("فئة البند", bomItemCategoryOptions, itemCategory, display = ::bomItemCategoryLabel) { itemCategory = it }
        when (itemCategory) {
            "Stock", "NonStock" -> if (parts.isNotEmpty()) {
                PartDropdown(parts, partId) { partId = it }
            } else {
                Text("لا توجد قطع غيار في الدليل.", color = MaterialTheme.colorScheme.error)
            }
            "Assembly" -> AssetDropdownOptional(
                assets = allAssets,
                selectedId = assemblyAssetId,
                onSelect = { assemblyAssetId = it },
                label = "تجميعة الصيانة",
                excludeId = currentAsset.id
            )
            "Text" -> LabeledField("وصف العنصر الهيكلي", notes, { notes = it }, singleLine = false)
        }
        BomParentDropdown(existingItems, parentItemId, initial?.id, parts, allAssets) { parentItemId = it }
        LabeledField("الكمية", quantity, { quantity = it }, numeric = true)
        OptionDropdown("الحالة", bomItemStatusOptions, status, display = ::bomStatusLabel) { status = it }
        DateField("صالح من", validFrom) { validFrom = it }
        DateField("صالح إلى", validTo) { validTo = it }
        if (!datesValid) Text("تاريخ البداية يجب ألا يتجاوز تاريخ النهاية.", color = MaterialTheme.colorScheme.error)
        if (itemCategory != "Text") {
            LabeledField("ملاحظات", notes, { notes = it }, singleLine = false)
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("بند حرج", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isCritical, onCheckedChange = { isCritical = it })
        }
        if (itemCategory == "Stock" || itemCategory == "NonStock") {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                Text("متاح للتخطيط في أوامر العمل", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
                Switch(checked = useInOrders, onCheckedChange = { useInOrders = it })
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("مكوّن بديل", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isAlternative, onCheckedChange = { isAlternative = it })
        }
        if (isAlternative) LabeledField("مجموعة البدائل", alternativeGroup, { alternativeGroup = it })

        SaveButton(numberValid && quantityValid && objectValid && datesValid && (!isAlternative || alternativeGroup.isNotBlank())) {
            onSave(
                AssetBomItemEntity(
                    id = initial?.id ?: 0,
                    assetId = header.assetId ?: 0,
                    partId = if (itemCategory == "Stock" || itemCategory == "NonStock") partId else 0,
                    quantity = quantity.toIntOrNull() ?: 1,
                    headerId = header.id,
                    itemNumber = itemNumber.toIntOrNull() ?: suggestedNumber,
                    itemCategory = itemCategory,
                    status = status,
                    validFrom = validFrom,
                    validTo = validTo,
                    isCritical = isCritical,
                    useInOrders = if (itemCategory == "Stock" || itemCategory == "NonStock") useInOrders else false,
                    notes = notes.trim(),
                    parentItemId = parentItemId,
                    assemblyAssetId = if (itemCategory == "Assembly") assemblyAssetId else null,
                    alternativeGroup = if (isAlternative) alternativeGroup.trim() else "",
                    isAlternative = isAlternative
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
                    linearVerticalOffset = if (selectedAsset?.isLinearAsset == true) linearVerticalOffset.toDoubleOrNull() else null
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

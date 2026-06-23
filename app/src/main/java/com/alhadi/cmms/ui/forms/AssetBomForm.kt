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
// Asset BOM form
// ---------------------------------------------------------------------------

@Composable
internal fun PartDropdown(parts: List<SparePartEntity>, selectedId: Long, onSelect: (Long) -> Unit) {
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
internal fun BomParentDropdown(
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



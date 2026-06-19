from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


def load(relative: str) -> tuple[Path, str]:
    path = ROOT / relative
    return path, path.read_text(encoding="utf-8")


def save(path: Path, text: str) -> None:
    path.write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected one match, found {count}")
    return text.replace(old, new, 1)


def replace_range(text: str, start_marker: str, end_marker: str, replacement: str, label: str) -> str:
    start = text.find(start_marker)
    if start < 0:
        raise RuntimeError(f"{label}: start marker not found")
    end = text.find(end_marker, start)
    if end < 0:
        raise RuntimeError(f"{label}: end marker not found")
    return text[:start] + replacement + text[end:]


ui_dir = ROOT / "app/src/main/java/com/alhadi/cmms/ui"

# -----------------------------------------------------------------------------
# Shared labels, assignment resolution and order relevance
# -----------------------------------------------------------------------------
(ui_dir / "AssetBom.kt").write_text('''package com.alhadi.cmms.ui

import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.SparePartEntity

internal val bomCategoryOptions = listOf("Asset", "Location", "Material")
internal val bomUsageOptions = listOf("Maintenance", "Engineering", "Production", "Costing", "Universal")
internal val bomAssignmentOptions = listOf("Direct", "Indirect")
internal val bomHeaderStatusOptions = listOf("Active", "Inactive", "Blocked")
internal val bomItemCategoryOptions = listOf("Stock", "NonStock", "Assembly", "Text")
internal val bomItemStatusOptions = listOf("Active", "Inactive", "Blocked")

internal fun bomCategoryLabel(value: String): String = when (value) {
    "Asset" -> "أصل"
    "Location" -> "موقع فني"
    "Material" -> "نوع إنشاء مشترك"
    else -> value
}

internal fun bomUsageLabel(value: String): String = when (value) {
    "Maintenance" -> "صيانة"
    "Engineering" -> "هندسة وتصميم"
    "Production" -> "إنتاج"
    "Costing" -> "تكاليف"
    "Universal" -> "استخدام عام"
    else -> value
}

internal fun bomAssignmentLabel(value: String): String = when (value) {
    "Indirect" -> "مشترك حسب نوع الإنشاء"
    else -> "تعيين مباشر"
}

internal fun bomStatusLabel(value: String): String = when (value) {
    "Active" -> "نشطة"
    "Inactive" -> "غير نشطة"
    "Blocked" -> "محظورة"
    else -> value
}

internal fun bomItemCategoryLabel(value: String): String = when (value) {
    "Stock" -> "قطعة مخزنية"
    "NonStock" -> "قطعة غير مخزنية"
    "Assembly" -> "تجميعة صيانة"
    "Text" -> "عنصر هيكلي / نصي"
    else -> value
}

internal fun resolveAssetBomHeaders(
    asset: AssetEntity,
    headers: List<AssetBomHeaderEntity>
): List<AssetBomHeaderEntity> = headers
    .filter { header ->
        when (header.assignmentType) {
            "Indirect" -> asset.constructionType.isNotBlank() &&
                header.constructionType.equals(asset.constructionType, ignoreCase = true)
            else -> header.assetId == asset.id
        }
    }
    .sortedWith(compareBy({ it.code }, { it.alternative }))

internal fun bomHeaderUsableInOrder(header: AssetBomHeaderEntity, today: String): Boolean =
    header.isActiveOn(today) && (header.usage == "Maintenance" || header.usage == "Universal")

internal fun bomItemUsableInOrder(item: AssetBomItemEntity, today: String): Boolean =
    item.isMaterialItem() && item.partId != 0L && item.useInOrders && item.isActiveOn(today)

internal fun bomItemObjectLabel(
    item: AssetBomItemEntity,
    partsById: Map<Long, SparePartEntity>,
    assetsById: Map<Long, AssetEntity>
): String = when (item.itemCategory) {
    "Assembly" -> item.assemblyAssetId?.let { id ->
        assetsById[id]?.let { "${it.code} • ${it.name}" }
    } ?: "تجميعة غير محددة"
    "Text" -> item.notes.ifBlank { "عنصر هيكلي" }
    else -> partsById[item.partId]?.let { "${it.partNumber} • ${it.name}" } ?: "قطعة #${item.partId}"
}

internal fun bomItemDepth(item: AssetBomItemEntity, allItems: List<AssetBomItemEntity>): Int {
    val byId = allItems.associateBy { it.id }
    val visited = mutableSetOf<Long>()
    var depth = 0
    var parentId = item.parentItemId
    while (parentId != null && visited.add(parentId) && depth < 8) {
        depth += 1
        parentId = byId[parentId]?.parentItemId
    }
    return depth
}
''', encoding="utf-8")


# -----------------------------------------------------------------------------
# Full component-list panel inside the asset 360 view
# -----------------------------------------------------------------------------
(ui_dir / "AssetBomSection.kt").write_text('''package com.alhadi.cmms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.SparePartEntity

@Composable
internal fun AssetBomSection(
    asset: AssetEntity,
    allAssets: List<AssetEntity>,
    headers: List<AssetBomHeaderEntity>,
    items: List<AssetBomItemEntity>,
    parts: List<SparePartEntity>,
    canManage: Boolean,
    onSaveHeader: (AssetBomHeaderEntity) -> Unit,
    onDeleteHeader: (AssetBomHeaderEntity) -> Unit,
    onSaveItem: (AssetBomItemEntity) -> Unit,
    onDeleteItem: (AssetBomItemEntity) -> Unit
) {
    val resolvedHeaders = remember(asset, headers) { resolveAssetBomHeaders(asset, headers) }
    val partMap = remember(parts) { parts.associateBy { it.id } }
    val assetMap = remember(allAssets) { allAssets.associateBy { it.id } }

    var showHeaderForm by remember(asset.id) { mutableStateOf(false) }
    var editingHeader by remember(asset.id) { mutableStateOf<AssetBomHeaderEntity?>(null) }
    var deletingHeader by remember(asset.id) { mutableStateOf<AssetBomHeaderEntity?>(null) }
    var itemHeader by remember(asset.id) { mutableStateOf<AssetBomHeaderEntity?>(null) }
    var editingItem by remember(asset.id) { mutableStateOf<AssetBomItemEntity?>(null) }
    var deletingItem by remember(asset.id) { mutableStateOf<AssetBomItemEntity?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("قوائم المكوّنات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "هيكل التجميعات وقطع الغيار المستخدمة في تخطيط أعمال الصيانة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (canManage) {
                OutlinedButton(onClick = { editingHeader = null; showHeaderForm = true }) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("قائمة")
                }
            }
        }

        if (resolvedHeaders.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Text("لا توجد قائمة مكوّنات مرتبطة بهذا الأصل.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        resolvedHeaders.forEach { header ->
            val headerItems = items.filter { it.headerId == header.id }.sortedWith(compareBy({ it.itemNumber }, { it.id }))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(header.code, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(header.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            if (header.description.isNotBlank()) {
                                Text(header.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        BomTag(bomStatusLabel(header.status), if (header.status == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BomTag(bomCategoryLabel(header.category), MaterialTheme.colorScheme.secondary)
                        BomTag(bomUsageLabel(header.usage), MaterialTheme.colorScheme.primary)
                        BomTag(bomAssignmentLabel(header.assignmentType), MaterialTheme.colorScheme.tertiary)
                        BomTag("بديل ${header.alternative}", MaterialTheme.colorScheme.secondary)
                        if (header.revision.isNotBlank()) BomTag("مراجعة ${header.revision}", MaterialTheme.colorScheme.tertiary)
                    }

                    if (header.assignmentType == "Indirect" && header.constructionType.isNotBlank()) {
                        Text("نوع الإنشاء المشترك: ${header.constructionType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (header.validFrom.isNotBlank() || header.validTo.isNotBlank()) {
                        Text("الصلاحية: ${header.validFrom.ifBlank { "غير محدد" }} — ${header.validTo.ifBlank { "مفتوحة" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (canManage) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { itemHeader = header; editingItem = null },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("بند")
                            }
                            IconButton(onClick = { editingHeader = header; showHeaderForm = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "تعديل")
                            }
                            IconButton(onClick = { deletingHeader = header }) {
                                Icon(Icons.Filled.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    if (headerItems.isEmpty()) {
                        Text("لا توجد بنود في هذه القائمة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    headerItems.forEach { item ->
                        val depth = bomItemDepth(item, headerItems)
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(start = (depth * 14).dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (item.status == "Active") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text("${item.itemNumber}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        bomItemObjectLabel(item, partMap, assetMap),
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text("×${item.quantity}", fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BomTag(bomItemCategoryLabel(item.itemCategory), MaterialTheme.colorScheme.secondary)
                                    BomTag(bomStatusLabel(item.status), if (item.status == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    if (item.isCritical) BomTag("حرجة", MaterialTheme.colorScheme.error)
                                    if (item.useInOrders) BomTag("تظهر في أوامر العمل", MaterialTheme.colorScheme.tertiary)
                                    if (item.isAlternative) BomTag("بديل ${item.alternativeGroup}", MaterialTheme.colorScheme.secondary)
                                }
                                if (item.validFrom.isNotBlank() || item.validTo.isNotBlank()) {
                                    Text("الصلاحية: ${item.validFrom.ifBlank { "غير محدد" }} — ${item.validTo.ifBlank { "مفتوحة" }}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (item.notes.isNotBlank() && item.itemCategory != "Text") {
                                    Text(item.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (canManage) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        TextButton(
                                            onClick = { itemHeader = header; editingItem = item },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("تعديل") }
                                        TextButton(
                                            onClick = { deletingItem = item },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("حذف", color = MaterialTheme.colorScheme.error) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showHeaderForm) {
        BomHeaderFormSheet(
            initial = editingHeader,
            asset = asset,
            onDismiss = { showHeaderForm = false },
            onSave = { onSaveHeader(it); showHeaderForm = false }
        )
    }
    itemHeader?.let { header ->
        BomItemFormSheet(
            initial = editingItem,
            header = header,
            currentAsset = asset,
            parts = parts,
            allAssets = allAssets,
            existingItems = items.filter { it.headerId == header.id },
            onDismiss = { itemHeader = null; editingItem = null },
            onSave = { onSaveItem(it); itemHeader = null; editingItem = null }
        )
    }
    deletingHeader?.let { header ->
        ConfirmDialog(
            title = "حذف قائمة المكونات",
            text = "سيتم حذف ${header.code} وجميع بنودها. هل تريد المتابعة؟",
            onConfirm = { onDeleteHeader(header); deletingHeader = null },
            onDismiss = { deletingHeader = null }
        )
    }
    deletingItem?.let { item ->
        ConfirmDialog(
            title = "حذف بند المكونات",
            text = "هل تريد حذف البند رقم ${item.itemNumber}؟",
            onConfirm = { onDeleteItem(item); deletingItem = null },
            onDismiss = { deletingItem = null }
        )
    }
}

@Composable
private fun BomTag(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.13f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}
''', encoding="utf-8")


# -----------------------------------------------------------------------------
# Forms
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/Forms.kt")
if "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.AssetBomItemEntity", "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity\nimport com.alhadi.cmms.data.entity.AssetBomItemEntity", "forms header import")

if "var constructionType by remember" not in text:
    text = replace_once(
        text,
        '    var standardClass by remember { mutableStateOf(initial?.standardClass ?: "") }',
        '    var standardClass by remember { mutableStateOf(initial?.standardClass ?: "") }\n    var constructionType by remember { mutableStateOf(initial?.constructionType ?: "") }',
        "asset construction type state",
    )
    text = replace_once(
        text,
        '''        LabeledField("التصنيف القياسي", standardClass, { standardClass = it })''',
        '''        LabeledField("التصنيف القياسي", standardClass, { standardClass = it })
        LabeledField("نوع الإنشاء المشترك", constructionType, { constructionType = it })''',
        "asset construction type field",
    )
    text = replace_once(
        text,
        '''                    standardClass = standardClass.trim(),
                    inheritParentCharacteristics = inheritParentCharacteristics,''',
        '''                    standardClass = standardClass.trim(),
                    constructionType = constructionType.trim(),
                    inheritParentCharacteristics = inheritParentCharacteristics,''',
        "asset construction type constructor",
    )

if "internal fun BomHeaderFormSheet" not in text:
    new_forms = '''

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
'''
    text = replace_once(
        text,
        "\n// ---------------------------------------------------------------------------\n// Asset characteristic form",
        new_forms + "\n\n// ---------------------------------------------------------------------------\n// Asset characteristic form",
        "new component-list forms",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Main application wiring, asset detail panel and order recommendations
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/CmmsApp.kt")
if "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.AssetBomItemEntity", "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity\nimport com.alhadi.cmms.data.entity.AssetBomItemEntity", "app header import")
if "val assetBomHeaders by" not in text:
    text = replace_once(text, "    val assetCharacteristics by viewModel.assetCharacteristics.collectAsStateWithLifecycle()\n    val assetBom by viewModel.assetBom.collectAsStateWithLifecycle()", "    val assetCharacteristics by viewModel.assetCharacteristics.collectAsStateWithLifecycle()\n    val assetBomHeaders by viewModel.assetBomHeaders.collectAsStateWithLifecycle()\n    val assetBom by viewModel.assetBom.collectAsStateWithLifecycle()", "app header state")

if "bomHeaders = assetBomHeaders" not in text:
    text = replace_once(text, "                        bom = assetBom,\n                        canManage = canManage,", "                        bomHeaders = assetBomHeaders,\n                        bom = assetBom,\n                        canManage = canManage,", "work orders header argument")
    text = replace_once(text, "                        characteristics = assetCharacteristics,\n                        bomItems = assetBom,", "                        characteristics = assetCharacteristics,\n                        bomHeaders = assetBomHeaders,\n                        bomItems = assetBom,", "assets header argument")
    text = replace_once(text, "                        onSaveBom = viewModel::saveBomItem,\n                        onDeleteBom = viewModel::deleteBomItem,", "                        onSaveBomHeader = viewModel::saveBomHeader,\n                        onDeleteBomHeader = viewModel::deleteBomHeader,\n                        onSaveBom = viewModel::saveBomItem,\n                        onDeleteBom = viewModel::deleteBomItem,", "assets header callbacks")

# AssetsScreen signature and detail call.
if "bomHeaders: List<AssetBomHeaderEntity>" not in text[text.find("private fun AssetsScreen"):text.find("private fun AssetCard")]:
    text = replace_once(text, "    characteristics: List<AssetCharacteristicEntity>,\n    bomItems: List<AssetBomItemEntity>,", "    characteristics: List<AssetCharacteristicEntity>,\n    bomHeaders: List<AssetBomHeaderEntity>,\n    bomItems: List<AssetBomItemEntity>,", "assets screen header parameter")
    text = replace_once(text, "    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,\n    onSaveBom: (AssetBomItemEntity) -> Unit,", "    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,\n    onSaveBomHeader: (AssetBomHeaderEntity) -> Unit,\n    onDeleteBomHeader: (AssetBomHeaderEntity) -> Unit,\n    onSaveBom: (AssetBomItemEntity) -> Unit,", "assets screen header callbacks")
    text = replace_once(text, "            characteristics = characteristics.filter { it.assetId == detailAsset.id },\n            bomItems = bomItems.filter { it.assetId == detailAsset.id },", "            characteristics = characteristics,\n            bomHeaders = bomHeaders,\n            bomItems = bomItems,", "asset detail header data")
    text = replace_once(text, "            onDeleteCharacteristic = onDeleteCharacteristic,\n            onSaveBom = onSaveBom,", "            onDeleteCharacteristic = onDeleteCharacteristic,\n            onSaveBomHeader = onSaveBomHeader,\n            onDeleteBomHeader = onDeleteBomHeader,\n            onSaveBom = onSaveBom,", "asset detail header callbacks")

# AssetDetailScreen signature.
detail_start = text.find("private fun AssetDetailScreen(")
detail_end = text.find(") {", detail_start)
detail_signature = text[detail_start:detail_end]
if "bomHeaders: List<AssetBomHeaderEntity>" not in detail_signature:
    detail_signature_new = detail_signature.replace(
        "    characteristics: List<AssetCharacteristicEntity>,\n    bomItems: List<AssetBomItemEntity>,",
        "    characteristics: List<AssetCharacteristicEntity>,\n    bomHeaders: List<AssetBomHeaderEntity>,\n    bomItems: List<AssetBomItemEntity>,",
    ).replace(
        "    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,\n    onSaveBom: (AssetBomItemEntity) -> Unit,",
        "    onDeleteCharacteristic: (AssetCharacteristicEntity) -> Unit,\n    onSaveBomHeader: (AssetBomHeaderEntity) -> Unit,\n    onDeleteBomHeader: (AssetBomHeaderEntity) -> Unit,\n    onSaveBom: (AssetBomItemEntity) -> Unit,",
    )
    text = text[:detail_start] + detail_signature_new + text[detail_end:]

# Replace the former flat item list with the structured panel.
old_bom_start = '''        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("قائمة المكوّنات BOM (${bomItems.size})")'''
old_bom_end = '''

        item {
            val qr = rememberQrBitmap("ALHADI:${asset.code}")'''
if old_bom_start in text:
    new_bom = '''        item {
            AssetBomSection(
                asset = asset,
                allAssets = allAssets,
                headers = bomHeaders,
                items = bomItems,
                parts = spareParts,
                canManage = canManage,
                onSaveHeader = onSaveBomHeader,
                onDeleteHeader = onDeleteBomHeader,
                onSaveItem = onSaveBom,
                onDeleteItem = onDeleteBom
            )
        }'''
    text = replace_range(text, old_bom_start, old_bom_end, new_bom + old_bom_end, "structured component-list panel")
else:
    raise RuntimeError("old component-list UI block not found")

# Show construction type in asset details and search.
if 'InfoRow("نوع الإنشاء", asset.constructionType)' not in text:
    text = replace_once(text, '''                    if (asset.objectType.isNotBlank()) InfoRow("نوع الأصل", asset.objectType)''', '''                    if (asset.objectType.isNotBlank()) InfoRow("نوع الأصل", asset.objectType)
                    if (asset.constructionType.isNotBlank()) InfoRow("نوع الإنشاء", asset.constructionType)''', "asset construction type detail")
if "asset.constructionType.lowercase" not in text:
    text = replace_once(text, '''                asset.standardClass.lowercase(Locale.getDefault()).contains(q) ||''', '''                asset.standardClass.lowercase(Locale.getDefault()).contains(q) ||
                asset.constructionType.lowercase(Locale.getDefault()).contains(q) ||''', "asset construction type search")

# Work order screen receives headers and uses only active maintenance-relevant items.
work_start = text.find("private fun WorkOrdersScreen(")
work_end = text.find(") {", work_start)
work_signature = text[work_start:work_end]
if "bomHeaders: List<AssetBomHeaderEntity>" not in work_signature:
    work_signature_new = work_signature.replace(
        "    transactions: List<InventoryTransactionEntity>,\n    bom: List<AssetBomItemEntity>,",
        "    transactions: List<InventoryTransactionEntity>,\n    bomHeaders: List<AssetBomHeaderEntity>,\n    bom: List<AssetBomItemEntity>,",
    )
    text = text[:work_start] + work_signature_new + text[work_end:]

if "val today = DateStrings.today()" not in text[work_start:text.find("Box(modifier", work_start)]:
    text = replace_once(text, "    val partMap = remember(parts) { parts.associateBy { it.id } }\n    val statusFilters", "    val partMap = remember(parts) { parts.associateBy { it.id } }\n    val today = DateStrings.today()\n    val statusFilters", "work order current date")

old_recommendation = '''                    bomPartIds = bom.filter { it.assetId == workOrder.assetId }.map { it.partId }.toSet(),'''
if old_recommendation in text:
    new_recommendation = '''                    bomPartIds = assetMap[workOrder.assetId]?.let { orderAsset ->
                        val activeHeaderIds = resolveAssetBomHeaders(orderAsset, bomHeaders)
                            .filter { bomHeaderUsableInOrder(it, today) }
                            .mapTo(mutableSetOf()) { it.id }
                        bom.filter { it.headerId in activeHeaderIds && bomItemUsableInOrder(it, today) }
                            .mapTo(mutableSetOf()) { it.partId }
                    } ?: emptySet(),'''
    text = text.replace(old_recommendation, new_recommendation, 1)
else:
    raise RuntimeError("work order component recommendation expression not found")

text = text.replace('StatusBadge("ضمن BOM", statusTone("info"))', 'StatusBadge("موصى بها", statusTone("info"))', 1)
save(path, text)

print("Structured maintenance component-list UI stage 4 patch completed successfully.")

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


# -----------------------------------------------------------------------------
# Asset classification controls
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/AssetEntity.kt")
if "val standardClass: String" not in text:
    text = replace_once(
        text,
        '''    @ColumnInfo(defaultValue = "''")
    val country: String = ""
) {''',
        '''    @ColumnInfo(defaultValue = "''")
    val country: String = "",
    @ColumnInfo(defaultValue = "''")
    val standardClass: String = "",
    @ColumnInfo(defaultValue = "1")
    val inheritParentCharacteristics: Boolean = true
) {''',
        "AssetEntity classification fields",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Rich characteristic definition
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/AssetCharacteristicEntity.kt")
if "import androidx.room.ColumnInfo" not in text:
    text = replace_once(
        text,
        "import androidx.room.Entity",
        "import androidx.room.ColumnInfo\nimport androidx.room.Entity",
        "AssetCharacteristicEntity ColumnInfo import",
    )
if "val className: String" not in text:
    text = replace_once(
        text,
        '''    val name: String,
    val value: String,
    val unit: String
)''',
        '''    val name: String,
    val value: String,
    val unit: String,
    @ColumnInfo(defaultValue = "'عام'")
    val className: String = "عام",
    @ColumnInfo(defaultValue = "'Text'")
    val dataType: String = "Text",
    @ColumnInfo(defaultValue = "''")
    val allowedValues: String = "",
    @ColumnInfo(defaultValue = "0")
    val isRequired: Boolean = false
)''',
        "AssetCharacteristicEntity definition fields",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Database migrations: repair 22->23 and add 23->24
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/AppDatabase.kt")
if "version = 24" not in text:
    text = replace_once(text, "version = 23", "version = 24", "database version 24")
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/DbMigrations.kt")
start = text.find("object DbMigrations {")
end_marker = "\n}\n\n/** Tiny helper"
end = text.find(end_marker, start)
if start < 0 or end < 0:
    raise RuntimeError("DbMigrations object markers not found")
end += 2
migration_object = '''object DbMigrations {

    /** Adds extended asset identity, organization, partner and address fields. */
    val MIGRATION_22_23 = object : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN category TEXT NOT NULL DEFAULT 'Machine'",
                "ALTER TABLE assets ADD COLUMN objectType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN description TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN maintenancePlant TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN planningPlant TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN plannerGroup TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN mainWorkCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN productionWorkCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN costCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN responsiblePerson TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN assetNumber TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN constructionYear TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN constructionMonth TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN startupDate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partnerName TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partnerRole TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partnerPhone TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partnerEmail TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN addressLine TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN city TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN country TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    /** Adds classification metadata and characteristic inheritance controls. */
    val MIGRATION_23_24 = object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN standardClass TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN inheritParentCharacteristics INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE asset_characteristics ADD COLUMN className TEXT NOT NULL DEFAULT 'عام'",
                "ALTER TABLE asset_characteristics ADD COLUMN dataType TEXT NOT NULL DEFAULT 'Text'",
                "ALTER TABLE asset_characteristics ADD COLUMN allowedValues TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN isRequired INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24)
}'''
text = text[:start] + migration_object + text[end:]
save(path, text)


# -----------------------------------------------------------------------------
# Classification resolver and display helpers
# -----------------------------------------------------------------------------
classification_path = ROOT / "app/src/main/java/com/alhadi/cmms/ui/AssetClassification.kt"
classification_path.write_text('''package com.alhadi.cmms.ui

import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetEntity

internal data class ResolvedAssetCharacteristic(
    val item: AssetCharacteristicEntity,
    val sourceAsset: AssetEntity,
    val inherited: Boolean
) {
    val resolvedClassName: String
        get() = item.className.trim().ifBlank { sourceAsset.standardClass.trim().ifBlank { "عام" } }

    val identityKey: String
        get() = "${resolvedClassName.lowercase()}::${item.name.trim().lowercase()}"
}

/**
 * Resolves the asset's own characteristics plus values inherited from its nearest ancestors.
 * A direct value on the child overrides an inherited value with the same class and name.
 */
internal fun resolveAssetCharacteristics(
    asset: AssetEntity,
    allAssets: List<AssetEntity>,
    allCharacteristics: List<AssetCharacteristicEntity>
): List<ResolvedAssetCharacteristic> {
    val direct = allCharacteristics
        .filter { it.assetId == asset.id }
        .map { ResolvedAssetCharacteristic(it, asset, inherited = false) }

    if (!asset.inheritParentCharacteristics) {
        return direct.sortedWith(characteristicOrdering())
    }

    val result = direct.toMutableList()
    val occupied = direct.mapTo(mutableSetOf()) { it.identityKey }
    val assetsById = allAssets.associateBy { it.id }
    val characteristicsByAsset = allCharacteristics.groupBy { it.assetId }
    val visited = mutableSetOf<Long>()
    var parentId = asset.parentAssetId

    while (parentId != null && visited.add(parentId)) {
        val parent = assetsById[parentId] ?: break
        characteristicsByAsset[parent.id].orEmpty().forEach { item ->
            val resolved = ResolvedAssetCharacteristic(item, parent, inherited = true)
            if (occupied.add(resolved.identityKey)) result += resolved
        }
        parentId = parent.parentAssetId
    }

    return result.sortedWith(characteristicOrdering())
}

private fun characteristicOrdering() =
    compareBy<ResolvedAssetCharacteristic>({ it.resolvedClassName.lowercase() }, { it.inherited }, { it.item.name.lowercase() })

internal fun characteristicTypeLabel(value: String): String = when (value) {
    "Number" -> "رقم"
    "Boolean" -> "نعم / لا"
    "Date" -> "تاريخ"
    "List" -> "قائمة"
    else -> "نص"
}

internal fun parseCharacteristicAllowedValues(value: String): List<String> = value
    .replace('،', ',')
    .split(',')
    .map { it.trim() }
    .filter { it.isNotBlank() }
    .distinct()

internal fun characteristicDisplayValue(item: AssetCharacteristicEntity): String {
    val base = when (item.dataType) {
        "Boolean" -> when (item.value.lowercase()) {
            "true", "1", "yes" -> "نعم"
            "false", "0", "no" -> "لا"
            else -> item.value
        }
        else -> item.value
    }
    return listOf(base, item.unit).filter { it.isNotBlank() }.joinToString(" ")
}
''', encoding="utf-8")


# -----------------------------------------------------------------------------
# Asset form: standard class and inheritance toggle
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/Forms.kt")
if "var standardClass by remember" not in text:
    anchor = '    var country by remember { mutableStateOf(initial?.country ?: "") }'
    extra = '''
    var standardClass by remember { mutableStateOf(initial?.standardClass ?: "") }
    var inheritParentCharacteristics by remember { mutableStateOf(initial?.inheritParentCharacteristics ?: true) }'''
    text = replace_once(text, anchor, anchor + extra, "asset classification form state")

if 'Text("التصنيف", style = MaterialTheme.typography.titleMedium' not in text:
    anchor = '''        if (allAssets.isNotEmpty()) {
            AssetDropdownOptional(allAssets, parentAssetId, { parentAssetId = it }, label = "الأصل الأب (اختياري)", excludeId = initial?.id)
        }

        Text("البيانات الفنية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)'''
    replacement = '''        if (allAssets.isNotEmpty()) {
            AssetDropdownOptional(allAssets, parentAssetId, { parentAssetId = it }, label = "الأصل الأب (اختياري)", excludeId = initial?.id)
        }

        Text("التصنيف", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("التصنيف القياسي", standardClass, { standardClass = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("توريث خصائص الأصل الأب", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = inheritParentCharacteristics, onCheckedChange = { inheritParentCharacteristics = it })
        }

        Text("البيانات الفنية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)'''
    text = replace_once(text, anchor, replacement, "asset classification form controls")

if "standardClass = standardClass.trim()" not in text:
    text = replace_once(
        text,
        '''                    city = city.trim(),
                    country = country.trim()
                )''',
        '''                    city = city.trim(),
                    country = country.trim(),
                    standardClass = standardClass.trim(),
                    inheritParentCharacteristics = inheritParentCharacteristics
                )''',
        "asset classification constructor fields",
    )

# Replace the simple characteristic form with a typed, class-aware form.
function_start = text.find("@Composable\ninternal fun CharacteristicFormSheet(")
function_end_marker = "\n\n// ---------------------------------------------------------------------------\n// Asset document form"
function_end = text.find(function_end_marker, function_start)
if function_start < 0 or function_end < 0:
    raise RuntimeError("CharacteristicFormSheet markers not found")
new_characteristic_form = '''@Composable
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
}'''
text = text[:function_start] + new_characteristic_form + text[function_end:]
save(path, text)


# -----------------------------------------------------------------------------
# Asset list and detail: classification search and inherited characteristics
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/CmmsApp.kt")
text = text.replace(
    "characteristics = characteristics.filter { it.assetId == detailAsset.id },",
    "characteristics = characteristics,",
    1,
)
text = text.replace(
    "val filtered = remember(query, assets) {",
    "val filtered = remember(query, assets, characteristics) {",
    1,
)

if "resolveAssetCharacteristics(asset, assets, characteristics).any" not in text:
    text = replace_once(
        text,
        '''                asset.city.lowercase(Locale.getDefault()).contains(q) ||
                asset.country.lowercase(Locale.getDefault()).contains(q)''',
        '''                asset.city.lowercase(Locale.getDefault()).contains(q) ||
                asset.country.lowercase(Locale.getDefault()).contains(q) ||
                asset.standardClass.lowercase(Locale.getDefault()).contains(q) ||
                resolveAssetCharacteristics(asset, assets, characteristics).any { resolved ->
                    resolved.resolvedClassName.lowercase(Locale.getDefault()).contains(q) ||
                        resolved.item.name.lowercase(Locale.getDefault()).contains(q) ||
                        resolved.item.value.lowercase(Locale.getDefault()).contains(q) ||
                        resolved.item.unit.lowercase(Locale.getDefault()).contains(q)
                }''',
        "classification search",
    )

if "asset.standardClass.isNotBlank()" not in text:
    text = replace_once(
        text,
        '''                if (asset.objectType.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(asset.objectType, maxLines = 1) })
                }''',
        '''                if (asset.objectType.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(asset.objectType, maxLines = 1) })
                }
                if (asset.standardClass.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(asset.standardClass, maxLines = 1) })
                }''',
        "asset card standard class",
    )

if "val resolvedCharacteristics = resolveAssetCharacteristics" not in text:
    anchor = '''    val constructionDate = listOf(asset.constructionYear, asset.constructionMonth)
        .filter { it.isNotBlank() }
        .joinToString(" / ")'''
    extra = '''
    val resolvedCharacteristics = resolveAssetCharacteristics(asset, allAssets, characteristics)
    val directCharacteristics = resolvedCharacteristics.filterNot { it.inherited }
    val inheritedCharacteristics = resolvedCharacteristics.filter { it.inherited }
    val characteristicGroups = resolvedCharacteristics.groupBy { it.resolvedClassName }'''
    text = replace_once(text, anchor, anchor + extra, "resolved characteristic state")

classification_start = '''        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("الخصائص الفنية (${characteristics.size})")'''
classification_end = '''

        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("قائمة المكوّنات BOM (${bomItems.size})")'''
if classification_start in text:
    new_block = '''        item {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                SectionHeader("التصنيف والخصائص (${resolvedCharacteristics.size})")
                Spacer(modifier = Modifier.weight(1f))
                if (canManage) {
                    OutlinedButton(onClick = { editingChar = null; showCharForm = true }) {
                        Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("إضافة")
                    }
                }
            }
        }
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    InfoRow("التصنيف القياسي", asset.standardClass.ifBlank { "غير محدد" })
                    InfoRow("توريث خصائص الأصل الأب", if (asset.inheritParentCharacteristics) "مفعّل" else "متوقف")
                    InfoRow("الخصائص المباشرة", directCharacteristics.size.toString())
                    if (inheritedCharacteristics.isNotEmpty()) {
                        InfoRow("الخصائص الموروثة", inheritedCharacteristics.size.toString())
                    }
                }
            }
        }
        if (resolvedCharacteristics.isEmpty()) {
            item { EmptyState("لا توجد خصائص مسجّلة") }
        }
        characteristicGroups.forEach { (className, classItems) ->
            item {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    SectionHeader("$className (${classItems.size})")
                    Spacer(modifier = Modifier.weight(1f))
                    if (asset.standardClass.isNotBlank() && className.equals(asset.standardClass, ignoreCase = true)) {
                        StatusBadge("قياسي", statusTone("info"))
                    }
                }
            }
            items(classItems, key = { resolved -> "char-${resolved.sourceAsset.id}-${resolved.item.id}-${resolved.inherited}" }) { resolved ->
                val ch = resolved.item
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(ch.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
                            LtrText(characteristicDisplayValue(ch), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            StatusBadge(characteristicTypeLabel(ch.dataType), statusTone("info"))
                            if (ch.isRequired) StatusBadge("إلزامية", statusTone("overdue"))
                            if (resolved.inherited) {
                                StatusBadge("موروثة من ${resolved.sourceAsset.code}", statusTone("scheduled"))
                            }
                        }
                        if (canManage && !resolved.inherited) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                OutlinedButton(onClick = { editingChar = ch; showCharForm = true }, modifier = Modifier.weight(1f)) {
                                    Text("تعديل")
                                }
                                TextButton(onClick = { deleteChar = ch }, modifier = Modifier.weight(1f)) {
                                    Text("حذف", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }'''
    text = replace_range(text, classification_start, classification_end, new_block, "classification detail section")

if "defaultClass = asset.standardClass" not in text:
    text = replace_once(
        text,
        '''        CharacteristicFormSheet(
            initial = editingChar,
            assetId = asset.id,
            onDismiss = { showCharForm = false },''',
        '''        CharacteristicFormSheet(
            initial = editingChar,
            assetId = asset.id,
            defaultClass = asset.standardClass,
            onDismiss = { showCharForm = false },''',
        "characteristic form default class",
    )
save(path, text)

print("Asset classification and inheritance stage 2 patch completed successfully.")

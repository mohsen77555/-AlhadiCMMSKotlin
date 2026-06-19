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


def edit_section(text: str, start_marker: str, end_marker: str, editor, label: str) -> str:
    start = text.find(start_marker)
    if start < 0:
        raise RuntimeError(f"{label}: start marker not found")
    end = text.find(end_marker, start)
    if end < 0:
        raise RuntimeError(f"{label}: end marker not found")
    section = text[start:end]
    updated = editor(section)
    return text[:start] + updated + text[end:]


# -----------------------------------------------------------------------------
# Asset master: linear reference, markers, offsets, coordinates, and network link
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/AssetEntity.kt")
if "val isLinearAsset: Boolean" not in text:
    text = replace_once(
        text,
        '''    @ColumnInfo(defaultValue = "1")
    val inheritParentCharacteristics: Boolean = true
) {''',
        '''    @ColumnInfo(defaultValue = "1")
    val inheritParentCharacteristics: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    val isLinearAsset: Boolean = false,
    @ColumnInfo(defaultValue = "0")
    val linearStartPoint: Double = 0.0,
    @ColumnInfo(defaultValue = "0")
    val linearEndPoint: Double = 0.0,
    @ColumnInfo(defaultValue = "'km'")
    val linearUnit: String = "km",
    @ColumnInfo(defaultValue = "''")
    val linearReferencePattern: String = "",
    @ColumnInfo(defaultValue = "''")
    val linearRouteCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val linearStartMarker: String = "",
    @ColumnInfo(defaultValue = "''")
    val linearEndMarker: String = "",
    @ColumnInfo(defaultValue = "0")
    val linearStartMarkerDistance: Double = 0.0,
    @ColumnInfo(defaultValue = "0")
    val linearEndMarkerDistance: Double = 0.0,
    @ColumnInfo(defaultValue = "'km'")
    val linearMarkerUnit: String = "km",
    @ColumnInfo(defaultValue = "0")
    val linearHorizontalOffset: Double = 0.0,
    @ColumnInfo(defaultValue = "0")
    val linearVerticalOffset: Double = 0.0,
    @ColumnInfo(defaultValue = "'m'")
    val linearOffsetUnit: String = "m",
    @ColumnInfo(defaultValue = "'Both'")
    val linearDirection: String = "Both",
    @ColumnInfo(defaultValue = "''")
    val networkObjectCode: String = "",
    @ColumnInfo(defaultValue = "''")
    val networkObjectType: String = "",
    @ColumnInfo(defaultValue = "''")
    val networkRelation: String = "",
    @ColumnInfo(defaultValue = "''")
    val networkAttributes: String = "",
    val linearStartLatitude: Double? = null,
    val linearStartLongitude: Double? = null,
    val linearEndLatitude: Double? = null,
    val linearEndLongitude: Double? = null
) {''',
        "AssetEntity linear fields",
    )
    text = replace_once(
        text,
        '''    fun isUnderWarranty(today: String): Boolean =
        warrantyEnd.isNotBlank() && today <= warrantyEnd && (warrantyStart.isBlank() || warrantyStart <= today)''',
        '''    fun isUnderWarranty(today: String): Boolean =
        warrantyEnd.isNotBlank() && today <= warrantyEnd && (warrantyStart.isBlank() || warrantyStart <= today)

    fun hasValidLinearRange(): Boolean = !isLinearAsset || linearEndPoint > linearStartPoint

    fun linearLength(): Double =
        if (isLinearAsset && linearEndPoint >= linearStartPoint) linearEndPoint - linearStartPoint else 0.0

    fun containsLinearRange(start: Double, end: Double): Boolean =
        isLinearAsset && start <= end && start >= linearStartPoint && end <= linearEndPoint''',
        "AssetEntity linear helpers",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Optional linear location on maintenance work and notifications
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/WorkOrderEntity.kt")
if "import androidx.room.ColumnInfo" not in text:
    text = replace_once(text, "import androidx.room.Entity", "import androidx.room.ColumnInfo\nimport androidx.room.Entity", "WorkOrder ColumnInfo import")
if "val linearStartPoint: Double?" not in text:
    text = replace_once(
        text,
        '''    val approvedBy: String = "",
    val requiresPermit: Boolean = false
) {''',
        '''    val approvedBy: String = "",
    val requiresPermit: Boolean = false,
    val linearStartPoint: Double? = null,
    val linearEndPoint: Double? = null,
    @ColumnInfo(defaultValue = "''")
    val linearMarker: String = "",
    val linearHorizontalOffset: Double? = null,
    val linearVerticalOffset: Double? = null
) {''',
        "WorkOrder linear fields",
    )
    text = replace_once(
        text,
        '''    fun isBlockedByApproval(): Boolean = approvalStatus == "Pending" || approvalStatus == "Rejected"''',
        '''    fun isBlockedByApproval(): Boolean = approvalStatus == "Pending" || approvalStatus == "Rejected"

    fun hasLinearReference(): Boolean =
        linearStartPoint != null || linearEndPoint != null || linearMarker.isNotBlank() ||
            linearHorizontalOffset != null || linearVerticalOffset != null''',
        "WorkOrder linear helper",
    )
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/MaintenanceNotificationEntity.kt")
if "import androidx.room.ColumnInfo" not in text:
    text = replace_once(text, "import androidx.room.Entity", "import androidx.room.ColumnInfo\nimport androidx.room.Entity", "Notification ColumnInfo import")
if "val linearStartPoint: Double?" not in text:
    text = replace_once(
        text,
        '''    val status: String,
    val linkedOrderId: Long? = null
)''',
        '''    val status: String,
    val linkedOrderId: Long? = null,
    val linearStartPoint: Double? = null,
    val linearEndPoint: Double? = null,
    @ColumnInfo(defaultValue = "''")
    val linearMarker: String = "",
    val linearHorizontalOffset: Double? = null,
    val linearVerticalOffset: Double? = null
) {
    fun hasLinearReference(): Boolean =
        linearStartPoint != null || linearEndPoint != null || linearMarker.isNotBlank() ||
            linearHorizontalOffset != null || linearVerticalOffset != null
}''',
        "Notification linear fields",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Database migration 24 -> 25
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/AppDatabase.kt")
if "version = 25" not in text:
    text = replace_once(text, "version = 24", "version = 25", "database version 25")
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/DbMigrations.kt")
if "MIGRATION_24_25" not in text:
    migration = '''

    /** Adds linear asset master data and optional linear references in maintenance processing. */
    val MIGRATION_24_25 = object : Migration(24, 25) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN isLinearAsset INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearStartPoint REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearEndPoint REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearUnit TEXT NOT NULL DEFAULT 'km'",
                "ALTER TABLE assets ADD COLUMN linearReferencePattern TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearRouteCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearStartMarker TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearEndMarker TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearStartMarkerDistance REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearEndMarkerDistance REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearMarkerUnit TEXT NOT NULL DEFAULT 'km'",
                "ALTER TABLE assets ADD COLUMN linearHorizontalOffset REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearVerticalOffset REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearOffsetUnit TEXT NOT NULL DEFAULT 'm'",
                "ALTER TABLE assets ADD COLUMN linearDirection TEXT NOT NULL DEFAULT 'Both'",
                "ALTER TABLE assets ADD COLUMN networkObjectCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN networkObjectType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN networkRelation TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN networkAttributes TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearStartLatitude REAL",
                "ALTER TABLE assets ADD COLUMN linearStartLongitude REAL",
                "ALTER TABLE assets ADD COLUMN linearEndLatitude REAL",
                "ALTER TABLE assets ADD COLUMN linearEndLongitude REAL",
                "ALTER TABLE work_orders ADD COLUMN linearStartPoint REAL",
                "ALTER TABLE work_orders ADD COLUMN linearEndPoint REAL",
                "ALTER TABLE work_orders ADD COLUMN linearMarker TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE work_orders ADD COLUMN linearHorizontalOffset REAL",
                "ALTER TABLE work_orders ADD COLUMN linearVerticalOffset REAL",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearStartPoint REAL",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearEndPoint REAL",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearMarker TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearHorizontalOffset REAL",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearVerticalOffset REAL"
            )
        }
    }'''
    text = replace_once(
        text,
        "\n\n    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24)",
        migration + "\n\n    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25)",
        "linear migration registration",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Linear display, validation and labels
# -----------------------------------------------------------------------------
linear_helper = ROOT / "app/src/main/java/com/alhadi/cmms/ui/AssetLinear.kt"
linear_helper.write_text('''package com.alhadi.cmms.ui

import com.alhadi.cmms.data.entity.AssetEntity
import java.util.Locale

internal val linearUnitOptions = listOf("m", "km", "ft", "mi")
internal val linearOffsetUnitOptions = listOf("mm", "cm", "m", "ft")
internal val linearDirectionOptions = listOf("Both", "Forward", "Reverse", "Clockwise", "CounterClockwise")
internal val networkObjectTypeOptions = listOf("", "Route", "Segment", "Node", "Pipeline", "Road", "Track", "Cable", "Circuit", "Other")
internal val networkRelationOptions = listOf("", "BelongsTo", "ConnectsTo", "Feeds", "Crosses", "Parallel", "Contains", "Other")

internal fun linearUnitLabel(value: String): String = when (value) {
    "m" -> "متر"
    "km" -> "كيلومتر"
    "ft" -> "قدم"
    "mi" -> "ميل"
    "mm" -> "ملم"
    "cm" -> "سم"
    else -> value
}

internal fun linearDirectionLabel(value: String): String = when (value) {
    "Forward" -> "اتجاه أمامي"
    "Reverse" -> "اتجاه عكسي"
    "Clockwise" -> "مع عقارب الساعة"
    "CounterClockwise" -> "عكس عقارب الساعة"
    else -> "الاتجاهان"
}

internal fun networkObjectTypeLabel(value: String): String = when (value) {
    "Route" -> "مسار"
    "Segment" -> "مقطع"
    "Node" -> "عقدة"
    "Pipeline" -> "خط أنابيب"
    "Road" -> "طريق"
    "Track" -> "سكة"
    "Cable" -> "كابل"
    "Circuit" -> "دائرة"
    "Other" -> "أخرى"
    else -> "بدون"
}

internal fun networkRelationLabel(value: String): String = when (value) {
    "BelongsTo" -> "يتبع"
    "ConnectsTo" -> "يتصل بـ"
    "Feeds" -> "يغذّي"
    "Crosses" -> "يتقاطع مع"
    "Parallel" -> "موازٍ لـ"
    "Contains" -> "يحتوي"
    "Other" -> "أخرى"
    else -> "بدون"
}

internal fun formatLinearNumber(value: Double): String {
    if (!value.isFinite()) return "—"
    return String.format(Locale.US, "%.3f", value).trimEnd('0').trimEnd('.')
}

internal fun linearRangeLabel(asset: AssetEntity): String =
    if (!asset.isLinearAsset || !asset.hasValidLinearRange()) {
        "غير محدد"
    } else {
        "${formatLinearNumber(asset.linearStartPoint)} – ${formatLinearNumber(asset.linearEndPoint)} ${asset.linearUnit}"
    }

internal fun linearMaintenancePositionLabel(
    asset: AssetEntity,
    startPoint: Double?,
    endPoint: Double?,
    marker: String,
    horizontalOffset: Double?,
    verticalOffset: Double?
): String {
    val parts = mutableListOf<String>()
    if (startPoint != null && endPoint != null) {
        parts += "${formatLinearNumber(startPoint)} – ${formatLinearNumber(endPoint)} ${asset.linearUnit}"
    }
    if (marker.isNotBlank()) parts += "العلامة: $marker"
    val offsets = buildList {
        if (horizontalOffset != null) add("أفقي ${formatLinearNumber(horizontalOffset)} ${asset.linearOffsetUnit}")
        if (verticalOffset != null) add("رأسي ${formatLinearNumber(verticalOffset)} ${asset.linearOffsetUnit}")
    }
    if (offsets.isNotEmpty()) parts += offsets.joinToString("، ")
    return parts.joinToString(" • ").ifBlank { "غير محدد" }
}

internal fun optionalLinearRangeValid(asset: AssetEntity, startText: String, endText: String): Boolean {
    if (startText.isBlank() && endText.isBlank()) return true
    val start = startText.toDoubleOrNull() ?: return false
    val end = endText.toDoubleOrNull() ?: return false
    return asset.containsLinearRange(start, end)
}

internal fun optionalLinearNumberValid(value: String): Boolean = value.isBlank() || value.toDoubleOrNull() != null
''', encoding="utf-8")


# -----------------------------------------------------------------------------
# Forms: asset master and maintenance linear locations
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/Forms.kt")

if "private fun LinearMaintenancePositionFields(" not in text:
    helper = '''

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
'''
    text = replace_once(
        text,
        "\n// ---------------------------------------------------------------------------\n// Asset form",
        helper + "\n// ---------------------------------------------------------------------------\n// Asset form",
        "linear maintenance form helper",
    )

# Asset form segment

def edit_asset_form(segment: str) -> str:
    if "var isLinearAsset by remember" not in segment:
        segment = replace_once(
            segment,
            '    var inheritParentCharacteristics by remember { mutableStateOf(initial?.inheritParentCharacteristics ?: true) }',
            '''    var inheritParentCharacteristics by remember { mutableStateOf(initial?.inheritParentCharacteristics ?: true) }
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
    ).all { (value, range) -> value.isBlank() || value.toDoubleOrNull()?.let { it in range } == true }''',
            "asset linear state",
        )

    if 'Text("الأصل الخطي", style = MaterialTheme.typography.titleMedium' not in segment:
        segment = replace_once(
            segment,
            '''        Text("البيانات الفنية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)''',
            '''        Text("الأصل الخطي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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

        Text("البيانات الفنية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)''',
            "asset linear form section",
        )

    segment = segment.replace(
        "SaveButton(code.isNotBlank() && name.isNotBlank())",
        "SaveButton(code.isNotBlank() && name.isNotBlank() && linearRangeValid && markerDistancesValid && coordinatesValid)",
        1,
    )

    if "isLinearAsset = isLinearAsset" not in segment:
        segment = replace_once(
            segment,
            '''                    standardClass = standardClass.trim(),
                    inheritParentCharacteristics = inheritParentCharacteristics''',
            '''                    standardClass = standardClass.trim(),
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
                    linearEndLongitude = if (isLinearAsset) linearEndLongitude.toDoubleOrNull() else null''',
            "asset linear constructor fields",
        )
    return segment


text = edit_section(
    text,
    "internal fun AssetFormSheet(",
    "internal fun LocationFormSheet(",
    edit_asset_form,
    "AssetFormSheet",
)

# Work order form segment

def edit_work_order_form(segment: str) -> str:
    if "var linearStartPoint by remember" not in segment:
        segment = replace_once(
            segment,
            '    var requiresPermit by remember { mutableStateOf(initial?.requiresPermit ?: false) }',
            '''    var requiresPermit by remember { mutableStateOf(initial?.requiresPermit ?: false) }
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
    } ?: true''',
            "work order linear state",
        )
    if "LinearMaintenancePositionFields(" not in segment:
        segment = replace_once(
            segment,
            '''        AssetDropdown(assets, assetId) { assetId = it }
        OptionDropdown("الأولوية",''',
            '''        AssetDropdown(assets, assetId) { assetId = it }
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
        OptionDropdown("الأولوية",''',
            "work order linear controls",
        )
    segment = segment.replace(
        "SaveButton(title.isNotBlank() && assetId != 0L)",
        "SaveButton(title.isNotBlank() && assetId != 0L && linearReferenceValid)",
        1,
    )
    if "linearMarker = if (selectedAsset?.isLinearAsset" not in segment:
        segment = replace_once(
            segment,
            '''                    approvedBy = initial?.approvedBy ?: "",
                    requiresPermit = requiresPermit''',
            '''                    approvedBy = initial?.approvedBy ?: "",
                    requiresPermit = requiresPermit,
                    linearStartPoint = if (selectedAsset?.isLinearAsset == true) linearStartPoint.toDoubleOrNull() else null,
                    linearEndPoint = if (selectedAsset?.isLinearAsset == true) linearEndPoint.toDoubleOrNull() else null,
                    linearMarker = if (selectedAsset?.isLinearAsset == true) linearMarker.trim() else "",
                    linearHorizontalOffset = if (selectedAsset?.isLinearAsset == true) linearHorizontalOffset.toDoubleOrNull() else null,
                    linearVerticalOffset = if (selectedAsset?.isLinearAsset == true) linearVerticalOffset.toDoubleOrNull() else null''',
            "work order linear constructor fields",
        )
    return segment


text = edit_section(
    text,
    "internal fun WorkOrderFormSheet(",
    "internal fun PermitFormSheet(",
    edit_work_order_form,
    "WorkOrderFormSheet",
)

# Notification form segment

def edit_notification_form(segment: str) -> str:
    if "var linearStartPoint by remember" not in segment:
        segment = replace_once(
            segment,
            '    var requiredEnd by remember { mutableStateOf(initial?.requiredEnd ?: "") }',
            '''    var requiredEnd by remember { mutableStateOf(initial?.requiredEnd ?: "") }
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
    } ?: true''',
            "notification linear state",
        )
    if "LinearMaintenancePositionFields(" not in segment:
        segment = replace_once(
            segment,
            '''        AssetDropdownOptional(assets, assetId, onSelect = { assetId = it })
        OptionDropdown("الأولوية",''',
            '''        AssetDropdownOptional(assets, assetId, onSelect = { assetId = it })
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
        OptionDropdown("الأولوية",''',
            "notification linear controls",
        )
    segment = segment.replace("SaveButton(title.isNotBlank())", "SaveButton(title.isNotBlank() && linearReferenceValid)", 1)
    if "linearMarker = if (selectedAsset?.isLinearAsset" not in segment:
        segment = replace_once(
            segment,
            '''                    status = initial?.status ?: "New",
                    linkedOrderId = initial?.linkedOrderId''',
            '''                    status = initial?.status ?: "New",
                    linkedOrderId = initial?.linkedOrderId,
                    linearStartPoint = if (selectedAsset?.isLinearAsset == true) linearStartPoint.toDoubleOrNull() else null,
                    linearEndPoint = if (selectedAsset?.isLinearAsset == true) linearEndPoint.toDoubleOrNull() else null,
                    linearMarker = if (selectedAsset?.isLinearAsset == true) linearMarker.trim() else "",
                    linearHorizontalOffset = if (selectedAsset?.isLinearAsset == true) linearHorizontalOffset.toDoubleOrNull() else null,
                    linearVerticalOffset = if (selectedAsset?.isLinearAsset == true) linearVerticalOffset.toDoubleOrNull() else null''',
            "notification linear constructor fields",
        )
    return segment


text = edit_section(
    text,
    "internal fun NotificationFormSheet(",
    "internal fun CapaFormSheet(",
    edit_notification_form,
    "NotificationFormSheet",
)
save(path, text)


# -----------------------------------------------------------------------------
# Domain validation and transfer to work orders
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/CmmsRepository.kt")
if "private fun validateLinearAssetMaster" not in text:
    validation = '''

    private fun validateLinearAssetMaster(asset: AssetEntity) {
        if (!asset.isLinearAsset) return
        require(asset.linearEndPoint > asset.linearStartPoint) {
            "يجب أن تكون نقطة نهاية الأصل الخطي أكبر من نقطة البداية"
        }
        require(asset.linearStartMarkerDistance >= 0.0 && asset.linearEndMarkerDistance >= 0.0) {
            "مسافات العلامات المرجعية لا يمكن أن تكون سالبة"
        }
        asset.linearStartLatitude?.let { require(it in -90.0..90.0) { "خط عرض البداية غير صالح" } }
        asset.linearEndLatitude?.let { require(it in -90.0..90.0) { "خط عرض النهاية غير صالح" } }
        asset.linearStartLongitude?.let { require(it in -180.0..180.0) { "خط طول البداية غير صالح" } }
        asset.linearEndLongitude?.let { require(it in -180.0..180.0) { "خط طول النهاية غير صالح" } }
    }

    private suspend fun validateLinearMaintenanceReference(
        assetId: Long?,
        startPoint: Double?,
        endPoint: Double?,
        marker: String,
        horizontalOffset: Double?,
        verticalOffset: Double?
    ) {
        val hasReference = startPoint != null || endPoint != null || marker.isNotBlank() ||
            horizontalOffset != null || verticalOffset != null
        if (!hasReference) return

        val id = assetId ?: throw IllegalStateException("يجب تحديد أصل للموقع الخطي")
        val asset = assetDao.getAssetById(id) ?: throw IllegalStateException("الأصل المحدد غير موجود")
        if (!asset.isLinearAsset) throw IllegalStateException("الأصل المحدد غير مفعّل كأصل خطي")
        val start = startPoint ?: throw IllegalStateException("أدخل نقطة بداية الموقع الخطي")
        val end = endPoint ?: throw IllegalStateException("أدخل نقطة نهاية الموقع الخطي")
        if (!asset.containsLinearRange(start, end)) {
            throw IllegalStateException("الموقع الخطي خارج نطاق الأصل ${asset.linearStartPoint} – ${asset.linearEndPoint} ${asset.linearUnit}")
        }
    }'''
    text = replace_once(
        text,
        "\n    // ---------------------------------------------------------------------\n    // CRUD — Assets",
        validation + "\n\n    // ---------------------------------------------------------------------\n    // CRUD — Assets",
        "repository linear validation helpers",
    )

text = replace_once(
    text,
    '''    suspend fun saveAsset(asset: AssetEntity, actor: String = "System") {
        val isNew = asset.id == 0L''',
    '''    suspend fun saveAsset(asset: AssetEntity, actor: String = "System") {
        validateLinearAssetMaster(asset)
        val isNew = asset.id == 0L''',
    "asset linear validation call",
)

text = replace_once(
    text,
    '''    suspend fun saveWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
        val isNew = workOrder.id == 0L''',
    '''    suspend fun saveWorkOrder(workOrder: WorkOrderEntity, actor: String = "System") {
        validateLinearMaintenanceReference(
            workOrder.assetId,
            workOrder.linearStartPoint,
            workOrder.linearEndPoint,
            workOrder.linearMarker,
            workOrder.linearHorizontalOffset,
            workOrder.linearVerticalOffset
        )
        val isNew = workOrder.id == 0L''',
    "work order linear validation call",
)

text = replace_once(
    text,
    '''    suspend fun saveNotification(notification: MaintenanceNotificationEntity, actor: String = "System") {
        val isNew = notification.id == 0L''',
    '''    suspend fun saveNotification(notification: MaintenanceNotificationEntity, actor: String = "System") {
        validateLinearMaintenanceReference(
            notification.assetId,
            notification.linearStartPoint,
            notification.linearEndPoint,
            notification.linearMarker,
            notification.linearHorizontalOffset,
            notification.linearVerticalOffset
        )
        val isNew = notification.id == 0L''',
    "notification linear validation call",
)

text = replace_once(
    text,
    '''    ) {
        database.withTransaction {
            val now = DateStrings.today()
            val orderId = workOrderDao.insertWorkOrder(''',
    '''    ) {
        validateLinearMaintenanceReference(
            notification.assetId,
            notification.linearStartPoint,
            notification.linearEndPoint,
            notification.linearMarker,
            notification.linearHorizontalOffset,
            notification.linearVerticalOffset
        )
        database.withTransaction {
            val now = DateStrings.today()
            val orderId = workOrderDao.insertWorkOrder(''',
    "notification conversion validation",
)

text = replace_once(
    text,
    '''                    isFailure = notification.type == "Breakdown",
                    approvalStatus = if (notification.priority == "Critical") "Pending" else "NotRequired"''',
    '''                    isFailure = notification.type == "Breakdown",
                    approvalStatus = if (notification.priority == "Critical") "Pending" else "NotRequired",
                    linearStartPoint = notification.linearStartPoint,
                    linearEndPoint = notification.linearEndPoint,
                    linearMarker = notification.linearMarker,
                    linearHorizontalOffset = notification.linearHorizontalOffset,
                    linearVerticalOffset = notification.linearVerticalOffset''',
    "notification linear transfer",
)
save(path, text)


# -----------------------------------------------------------------------------
# UI cards, search and asset detail
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/CmmsApp.kt")

if "asset.linearRouteCode.lowercase" not in text:
    text = replace_once(
        text,
        '''                asset.standardClass.lowercase(Locale.getDefault()).contains(q) ||
                resolveAssetCharacteristics''',
        '''                asset.standardClass.lowercase(Locale.getDefault()).contains(q) ||
                asset.linearRouteCode.lowercase(Locale.getDefault()).contains(q) ||
                asset.linearReferencePattern.lowercase(Locale.getDefault()).contains(q) ||
                asset.linearStartMarker.lowercase(Locale.getDefault()).contains(q) ||
                asset.linearEndMarker.lowercase(Locale.getDefault()).contains(q) ||
                asset.networkObjectCode.lowercase(Locale.getDefault()).contains(q) ||
                asset.networkAttributes.lowercase(Locale.getDefault()).contains(q) ||
                resolveAssetCharacteristics''',
        "linear asset search",
    )

if 'StatusBadge("أصل خطي"' not in text:
    text = replace_once(
        text,
        '''                if (asset.standardClass.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(asset.standardClass, maxLines = 1) })
                }''',
        '''                if (asset.standardClass.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(asset.standardClass, maxLines = 1) })
                }
                if (asset.isLinearAsset) {
                    StatusBadge("أصل خطي", statusTone("info"))
                }''',
        "linear asset card badge",
    )

if "val hasLinearData = asset.isLinearAsset" not in text:
    text = replace_once(
        text,
        '''    val characteristicGroups = resolvedCharacteristics.groupBy { it.resolvedClassName }''',
        '''    val characteristicGroups = resolvedCharacteristics.groupBy { it.resolvedClassName }
    val hasLinearData = asset.isLinearAsset''',
        "linear asset detail state",
    )

if 'SectionHeader("البيانات الخطية")' not in text:
    linear_card = '''

        if (hasLinearData) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            SectionHeader("البيانات الخطية")
                            Spacer(modifier = Modifier.weight(1f))
                            StatusBadge("${formatLinearNumber(asset.linearLength())} ${asset.linearUnit}", statusTone("info"))
                        }
                        InfoRow("النطاق", linearRangeLabel(asset))
                        if (asset.linearRouteCode.isNotBlank()) InfoRow("رمز المسار / الخط", asset.linearRouteCode)
                        if (asset.linearReferencePattern.isNotBlank()) InfoRow("نمط المرجع", asset.linearReferencePattern)
                        InfoRow("الاتجاه", linearDirectionLabel(asset.linearDirection))
                        if (asset.linearStartMarker.isNotBlank()) {
                            InfoRow("علامة البداية", "${asset.linearStartMarker} • ${formatLinearNumber(asset.linearStartMarkerDistance)} ${asset.linearMarkerUnit}")
                        }
                        if (asset.linearEndMarker.isNotBlank()) {
                            InfoRow("علامة النهاية", "${asset.linearEndMarker} • ${formatLinearNumber(asset.linearEndMarkerDistance)} ${asset.linearMarkerUnit}")
                        }
                        if (asset.linearHorizontalOffset != 0.0) InfoRow("الإزاحة الأفقية", "${formatLinearNumber(asset.linearHorizontalOffset)} ${asset.linearOffsetUnit}")
                        if (asset.linearVerticalOffset != 0.0) InfoRow("الإزاحة الرأسية", "${formatLinearNumber(asset.linearVerticalOffset)} ${asset.linearOffsetUnit}")
                        if (asset.linearStartLatitude != null && asset.linearStartLongitude != null) {
                            InfoRow("إحداثيات البداية", "${formatLinearNumber(asset.linearStartLatitude)}، ${formatLinearNumber(asset.linearStartLongitude)}")
                        }
                        if (asset.linearEndLatitude != null && asset.linearEndLongitude != null) {
                            InfoRow("إحداثيات النهاية", "${formatLinearNumber(asset.linearEndLatitude)}، ${formatLinearNumber(asset.linearEndLongitude)}")
                        }
                        if (asset.networkObjectCode.isNotBlank()) InfoRow("كائن الشبكة", asset.networkObjectCode)
                        if (asset.networkObjectType.isNotBlank()) InfoRow("نوع كائن الشبكة", networkObjectTypeLabel(asset.networkObjectType))
                        if (asset.networkRelation.isNotBlank()) InfoRow("العلاقة", networkRelationLabel(asset.networkRelation))
                        if (asset.networkAttributes.isNotBlank()) InfoRow("سمات الشبكة", asset.networkAttributes)
                    }
                }
            }
        }'''
    text = replace_once(
        text,
        "\n\n        if (hasOrganization) {",
        linear_card + "\n\n        if (hasOrganization) {",
        "linear asset detail card",
    )

if 'InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, wo.linearStartPoint' not in text:
    text = replace_once(
        text,
        '''                    Text("الاستحقاق: ${wo.dueAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (wo.approvalStatus == "Pending") {''',
        '''                    Text("الاستحقاق: ${wo.dueAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    if (asset.isLinearAsset && wo.hasLinearReference()) {
                        InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, wo.linearStartPoint, wo.linearEndPoint, wo.linearMarker, wo.linearHorizontalOffset, wo.linearVerticalOffset))
                    }
                    if (wo.approvalStatus == "Pending") {''',
        "asset detail work order linear position",
    )

if 'InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, notification.linearStartPoint' not in text:
    text = replace_once(
        text,
        '''            if (asset != null) InfoRow("الأصل", "${asset.code} • ${asset.name}")
            if (notification.damageCode.isNotBlank())''',
        '''            if (asset != null) InfoRow("الأصل", "${asset.code} • ${asset.name}")
            if (asset?.isLinearAsset == true && notification.hasLinearReference()) {
                InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, notification.linearStartPoint, notification.linearEndPoint, notification.linearMarker, notification.linearHorizontalOffset, notification.linearVerticalOffset))
            }
            if (notification.damageCode.isNotBlank())''',
        "notification linear position card",
    )

if 'InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, workOrder.linearStartPoint' not in text:
    text = replace_once(
        text,
        '''            InfoRow("تاريخ الاستحقاق", workOrder.dueAt)
            InfoRow("التكلفة التقديرية", "%.2f".format(workOrder.estimatedCost))''',
        '''            InfoRow("تاريخ الاستحقاق", workOrder.dueAt)
            InfoRow("التكلفة التقديرية", "%.2f".format(workOrder.estimatedCost))
            if (asset?.isLinearAsset == true && workOrder.hasLinearReference()) {
                InfoRow("الموقع الخطي", linearMaintenancePositionLabel(asset, workOrder.linearStartPoint, workOrder.linearEndPoint, workOrder.linearMarker, workOrder.linearHorizontalOffset, workOrder.linearVerticalOffset))
            }''',
        "work order linear position card",
    )

if "wo.linearMarker.lowercase" not in text:
    text = replace_once(
        text,
        '''                    wo.title.lowercase(Locale.getDefault()).contains(q) ||
                    (asset?.code?.lowercase(Locale.getDefault())?.contains(q) == true) ||''',
        '''                    wo.title.lowercase(Locale.getDefault()).contains(q) ||
                    wo.linearMarker.lowercase(Locale.getDefault()).contains(q) ||
                    (asset?.linearRouteCode?.lowercase(Locale.getDefault())?.contains(q) == true) ||
                    (asset?.networkObjectCode?.lowercase(Locale.getDefault())?.contains(q) == true) ||
                    (asset?.code?.lowercase(Locale.getDefault())?.contains(q) == true) ||''',
        "work order linear search",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Printable work order includes the linear position
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/util/PdfExporter.kt")
if 'pager.kv("الموقع الخطي"' not in text:
    text = replace_once(
        text,
        '''        pager.kv("تاريخ الاستحقاق", order.dueAt)
        pager.kv("الاعتماد", arabicApproval(order.approvalStatus))''',
        '''        pager.kv("تاريخ الاستحقاق", order.dueAt)
        pager.kv("الاعتماد", arabicApproval(order.approvalStatus))
        if (asset?.isLinearAsset == true && order.hasLinearReference()) {
            pager.kv("الموقع الخطي", linearPositionText(order, asset))
        }''',
        "PDF linear position row",
    )
    text = replace_once(
        text,
        '''    private fun money(value: Double): String = "%,.2f ر.س".format(value)''',
        '''    private fun money(value: Double): String = "%,.2f ر.س".format(value)

    private fun linearPositionText(order: WorkOrderEntity, asset: AssetEntity): String {
        val parts = mutableListOf<String>()
        if (order.linearStartPoint != null && order.linearEndPoint != null) {
            parts += "${linearNumber(order.linearStartPoint)} – ${linearNumber(order.linearEndPoint)} ${asset.linearUnit}"
        }
        if (order.linearMarker.isNotBlank()) parts += "العلامة: ${order.linearMarker}"
        if (order.linearHorizontalOffset != null) parts += "إزاحة أفقية ${linearNumber(order.linearHorizontalOffset)} ${asset.linearOffsetUnit}"
        if (order.linearVerticalOffset != null) parts += "إزاحة رأسية ${linearNumber(order.linearVerticalOffset)} ${asset.linearOffsetUnit}"
        return parts.joinToString(" • ").ifBlank { "غير محدد" }
    }

    private fun linearNumber(value: Double): String =
        "%.3f".format(java.util.Locale.US, value).trimEnd('0').trimEnd('.')''',
        "PDF linear helpers",
    )
save(path, text)

print("Linear asset management stage 3 patch completed successfully.")

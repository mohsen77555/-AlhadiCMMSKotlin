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


def replace_section(text: str, start_marker: str, end_marker: str, replacement: str, label: str) -> str:
    start = text.find(start_marker)
    if start < 0:
        raise RuntimeError(f"{label}: start marker not found")
    end = text.find(end_marker, start)
    if end < 0:
        raise RuntimeError(f"{label}: end marker not found")
    end += len(end_marker)
    return text[:start] + replacement + text[end:]


# -----------------------------------------------------------------------------
# Asset entity
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/AssetEntity.kt")
if "import androidx.room.ColumnInfo" not in text:
    text = replace_once(
        text,
        "import androidx.room.Entity",
        "import androidx.room.ColumnInfo\nimport androidx.room.Entity",
        "AssetEntity ColumnInfo import",
    )

if "val category: String" not in text:
    text = replace_once(
        text,
        '''    val purchaseCost: Double = 0.0,
    val acquiredAt: String = ""
) {''',
        '''    val purchaseCost: Double = 0.0,
    val acquiredAt: String = "",
    @ColumnInfo(defaultValue = "'Machine'")
    val category: String = "Machine",
    @ColumnInfo(defaultValue = "''")
    val objectType: String = "",
    @ColumnInfo(defaultValue = "''")
    val description: String = "",
    @ColumnInfo(defaultValue = "''")
    val maintenancePlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val planningPlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val plannerGroup: String = "",
    @ColumnInfo(defaultValue = "''")
    val mainWorkCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val productionWorkCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val costCenter: String = "",
    @ColumnInfo(defaultValue = "''")
    val responsiblePerson: String = "",
    @ColumnInfo(defaultValue = "''")
    val assetNumber: String = "",
    @ColumnInfo(defaultValue = "''")
    val constructionYear: String = "",
    @ColumnInfo(defaultValue = "''")
    val constructionMonth: String = "",
    @ColumnInfo(defaultValue = "''")
    val startupDate: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerName: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerRole: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerPhone: String = "",
    @ColumnInfo(defaultValue = "''")
    val partnerEmail: String = "",
    @ColumnInfo(defaultValue = "''")
    val addressLine: String = "",
    @ColumnInfo(defaultValue = "''")
    val city: String = "",
    @ColumnInfo(defaultValue = "''")
    val country: String = ""
) {''',
        "AssetEntity extended fields",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Database version and migration
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/AppDatabase.kt")
if "version = 23" not in text:
    text = replace_once(text, "version = 22", "version = 23", "database version")
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/DbMigrations.kt")
if "MIGRATION_22_23" not in text:
    start = text.find("object DbMigrations {")
    end_marker = "\n}\n\n/** Tiny helper"
    end = text.find(end_marker, start)
    if start < 0 or end < 0:
        raise RuntimeError("DbMigrations object markers not found")
    end += 2
    migration_object = '''object DbMigrations {

    /** Adds the first group of extended asset fields without losing existing records. */
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

    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23)
}'''
    text = text[:start] + migration_object + text[end:]
save(path, text)


# -----------------------------------------------------------------------------
# Stable codes with Arabic display labels
# -----------------------------------------------------------------------------
labels_path = ROOT / "app/src/main/java/com/alhadi/cmms/ui/AssetMasterLabels.kt"
labels_path.write_text('''package com.alhadi.cmms.ui

internal val assetCategoryOptions = listOf(
    "Machine",
    "Vehicle",
    "Tool",
    "ProductionResource",
    "CustomerAsset",
    "Facility",
    "Assembly",
    "Other"
)

internal fun assetCategoryLabel(value: String): String = when (value) {
    "Machine" -> "آلة / معدة"
    "Vehicle" -> "مركبة"
    "Tool" -> "أداة"
    "ProductionResource" -> "مورد إنتاج"
    "CustomerAsset" -> "أصل عميل"
    "Facility" -> "مرفق"
    "Assembly" -> "تجميعة"
    "Other" -> "أخرى"
    else -> value.ifBlank { "غير محدد" }
}

internal val assetPartnerRoleOptions = listOf(
    "",
    "Manufacturer",
    "Vendor",
    "ServiceProvider",
    "Operator",
    "Owner",
    "Responsible"
)

internal fun assetPartnerRoleLabel(value: String): String = when (value) {
    "Manufacturer" -> "الشركة المصنّعة"
    "Vendor" -> "المورّد"
    "ServiceProvider" -> "مقدم الخدمة"
    "Operator" -> "المشغّل"
    "Owner" -> "المالك"
    "Responsible" -> "المسؤول"
    else -> "بدون"
}
''', encoding="utf-8")


# -----------------------------------------------------------------------------
# Asset create/edit form
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/Forms.kt")
if "var category by remember" not in text:
    anchor = '    var acquiredAt by remember { mutableStateOf(initial?.acquiredAt ?: "") }'
    variables = '''
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
    var country by remember { mutableStateOf(initial?.country ?: "") }'''
    text = replace_once(text, anchor, anchor + variables, "asset form state")

if 'Text("البيانات الأساسية", style = MaterialTheme.typography.titleMedium' not in text:
    new_form = '''        Text("البيانات الأساسية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
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
        DateField("نهاية الضمان", warrantyEnd) { warrantyEnd = it }'''
    text = replace_section(
        text,
        '        LabeledField("الكود (Code)", code, { code = it })',
        '        DateField("نهاية الضمان", warrantyEnd) { warrantyEnd = it }',
        new_form,
        "asset form fields",
    )

if "category = category," not in text:
    old = '                    acquiredAt = acquiredAt.trim()'
    new = '''                    acquiredAt = acquiredAt.trim(),
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
                    country = country.trim()'''
    text = replace_once(text, old, new, "asset constructor fields")
save(path, text)


# -----------------------------------------------------------------------------
# Asset list, search and 360-degree detail view
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/ui/CmmsApp.kt")
if "asset.description.lowercase(Locale.getDefault()).contains(q)" not in text:
    old = '                asset.assetTag.lowercase(Locale.getDefault()).contains(q)'
    new = '''                asset.assetTag.lowercase(Locale.getDefault()).contains(q) ||
                asset.description.lowercase(Locale.getDefault()).contains(q) ||
                asset.category.lowercase(Locale.getDefault()).contains(q) ||
                asset.objectType.lowercase(Locale.getDefault()).contains(q) ||
                asset.maintenancePlant.lowercase(Locale.getDefault()).contains(q) ||
                asset.planningPlant.lowercase(Locale.getDefault()).contains(q) ||
                asset.plannerGroup.lowercase(Locale.getDefault()).contains(q) ||
                asset.mainWorkCenter.lowercase(Locale.getDefault()).contains(q) ||
                asset.costCenter.lowercase(Locale.getDefault()).contains(q) ||
                asset.responsiblePerson.lowercase(Locale.getDefault()).contains(q) ||
                asset.assetNumber.lowercase(Locale.getDefault()).contains(q) ||
                asset.partnerName.lowercase(Locale.getDefault()).contains(q) ||
                asset.city.lowercase(Locale.getDefault()).contains(q) ||
                asset.country.lowercase(Locale.getDefault()).contains(q)'''
    text = replace_once(text, old, new, "asset search fields")

if "assetCategoryLabel(asset.category)" not in text:
    target = '                AssistChip(onClick = {}, label = { Text(asset.location, maxLines = 1) })'
    target_at = text.find(target)
    if target_at < 0:
        raise RuntimeError("asset card location chip not found")
    start = text.rfind('            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {', 0, target_at)
    end_marker = '            if (canManage) EditDeleteRow(onEdit, onDelete)'
    end = text.find(end_marker, target_at)
    if start < 0 or end < 0:
        raise RuntimeError("asset card block markers not found")
    end += len(end_marker)
    card_block = '''            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatusBadge(asset.criticality, priorityTone(asset.criticality))
                AssistChip(onClick = {}, label = { Text(assetCategoryLabel(asset.category), maxLines = 1) })
                if (asset.objectType.isNotBlank()) {
                    AssistChip(onClick = {}, label = { Text(asset.objectType, maxLines = 1) })
                }
            }
            if (asset.location.isNotBlank()) {
                Text(asset.location, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)'''
    text = text[:start] + card_block + text[end:]

if "val hasOrganization = listOf(" not in text:
    anchor = '    val retired = asset.status.equals("Retired", ignoreCase = true)'
    details_state = '''
    val hasOrganization = listOf(
        asset.maintenancePlant,
        asset.planningPlant,
        asset.plannerGroup,
        asset.mainWorkCenter,
        asset.productionWorkCenter,
        asset.costCenter,
        asset.responsiblePerson
    ).any { it.isNotBlank() }
    val hasPartner = listOf(asset.partnerName, asset.partnerRole, asset.partnerPhone, asset.partnerEmail).any { it.isNotBlank() }
    val hasAddress = listOf(asset.addressLine, asset.city, asset.country).any { it.isNotBlank() }
    val constructionDate = listOf(asset.constructionYear, asset.constructionMonth)
        .filter { it.isNotBlank() }
        .joinToString(" / ")'''
    text = replace_once(text, anchor, anchor + details_state, "asset detail state")

if 'SectionHeader("المعلومات الأساسية")' not in text:
    info_block = '''                    SectionHeader("المعلومات الأساسية")
                    if (asset.description.isNotBlank()) InfoRow("الوصف", asset.description)
                    InfoRow("فئة الأصل", assetCategoryLabel(asset.category))
                    if (asset.objectType.isNotBlank()) InfoRow("نوع الأصل", asset.objectType)
                    InfoRow("المجموعة", asset.groupName)
                    InfoRow("الموقع", asset.location.ifBlank { "غير محدد" })
                    InfoRow("الموقع الفني", locationLabel)
                    InfoRow("الأصل الأب", parent?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                    InfoRow("الشركة/الموديل", listOf(asset.manufacturer, asset.model).filter { it.isNotBlank() }.joinToString(" • ").ifBlank { "غير محدد" })
                    if (asset.serialNumber.isNotBlank()) InfoRow("الرقم التسلسلي", asset.serialNumber)
                    if (asset.assetTag.isNotBlank()) InfoRow("وسم الأصل", asset.assetTag)
                    if (asset.assetNumber.isNotBlank()) InfoRow("رقم الأصل المالي", asset.assetNumber)
                    InfoRow("الأهمية", asset.criticality)
                    if (constructionDate.isNotBlank()) InfoRow("سنة / شهر الصنع", constructionDate)
                    InfoRow("تاريخ التركيب", asset.installedAt)
                    if (asset.startupDate.isNotBlank()) InfoRow("تاريخ بدء التشغيل", asset.startupDate)
                    InfoRow("آخر فحص", asset.lastInspectionAt)'''
    text = replace_section(
        text,
        '                    SectionHeader("المعلومات")',
        '                    InfoRow("آخر فحص", asset.lastInspectionAt)',
        info_block,
        "asset basic detail card",
    )

if 'SectionHeader("جهة الاتصال والعنوان")' not in text:
    marker = '''        item {
            val laborTotal = workOrders.sumOf { it.laborCost() }'''
    extra_cards = '''        if (hasOrganization) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("التنظيم والمسؤولية")
                        if (asset.maintenancePlant.isNotBlank()) InfoRow("مصنع الصيانة", asset.maintenancePlant)
                        if (asset.planningPlant.isNotBlank()) InfoRow("مصنع التخطيط", asset.planningPlant)
                        if (asset.plannerGroup.isNotBlank()) InfoRow("مجموعة المخططين", asset.plannerGroup)
                        if (asset.mainWorkCenter.isNotBlank()) InfoRow("مركز العمل الرئيسي", asset.mainWorkCenter)
                        if (asset.productionWorkCenter.isNotBlank()) InfoRow("مركز عمل الإنتاج", asset.productionWorkCenter)
                        if (asset.costCenter.isNotBlank()) InfoRow("مركز التكلفة", asset.costCenter)
                        if (asset.responsiblePerson.isNotBlank()) InfoRow("الشخص المسؤول", asset.responsiblePerson)
                    }
                }
            }
        }

        if (hasPartner || hasAddress) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("جهة الاتصال والعنوان")
                        if (asset.partnerName.isNotBlank()) InfoRow("الجهة أو الشخص", asset.partnerName)
                        if (asset.partnerRole.isNotBlank()) InfoRow("الصفة", assetPartnerRoleLabel(asset.partnerRole))
                        if (asset.partnerPhone.isNotBlank()) InfoRow("الهاتف", asset.partnerPhone)
                        if (asset.partnerEmail.isNotBlank()) InfoRow("البريد الإلكتروني", asset.partnerEmail)
                        if (asset.addressLine.isNotBlank()) InfoRow("العنوان", asset.addressLine)
                        if (asset.city.isNotBlank()) InfoRow("المدينة", asset.city)
                        if (asset.country.isNotBlank()) InfoRow("الدولة", asset.country)
                    }
                }
            }
        }

'''
    text = replace_once(text, marker, extra_cards + marker, "asset organization and partner cards")
save(path, text)

print("Asset master stage 1 patch completed successfully.")

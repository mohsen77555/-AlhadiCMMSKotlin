from pathlib import Path


def replace_function(text: str, signature: str, replacement: str) -> str:
    start = text.index(signature)
    brace = text.index("{", start)
    depth = 0
    index = brace
    while index < len(text):
        char = text[index]
        if char == "{":
            depth += 1
        elif char == "}":
            depth -= 1
            if depth == 0:
                end = index + 1
                return text[:start] + replacement + text[end:]
        index += 1
    raise RuntimeError(f"Could not find end of function: {signature}")


def patch_repository() -> None:
    path = Path("app/src/main/java/com/alhadi/cmms/data/CmmsRepository.kt")
    text = path.read_text(encoding="utf-8")

    if "import com.alhadi.cmms.domain.governance.AssetGovernance" not in text:
        text = text.replace(
            "import com.alhadi.cmms.util.PasswordHasher\n",
            "import com.alhadi.cmms.util.PasswordHasher\n"
            "import com.alhadi.cmms.domain.governance.AssetGovernance\n"
            "import com.alhadi.cmms.domain.governance.BackupGovernance\n",
        )

    text = text.replace("appDbVersion = 22,", "appDbVersion = 26,")

    decode_line = "val bundle = backupJson.decodeFromString(BackupBundle.serializer(), content)"
    if "BackupGovernance.requireValidForRestore(bundle)" not in text:
        text = text.replace(
            decode_line,
            decode_line + "\n        BackupGovernance.requireValidForRestore(bundle)",
        )

    save_asset = '''    suspend fun saveAsset(asset: AssetEntity, actor: String = "System") {
        val isNew = asset.id == 0L
        val existing = if (isNew) null else assetDao.getAssetById(asset.id)
        if (existing != null && existing.lifecycleStatus != asset.lifecycleStatus) {
            AssetGovernance.requireLifecycleTransition(existing.lifecycleStatus, asset.lifecycleStatus)
        }

        val now = DateStrings.now()
        val score = AssetGovernance.calculateCriticalityScore(
            safety = asset.criticalitySafetyImpact,
            production = asset.criticalityProductionImpact,
            environment = asset.criticalityEnvironmentalImpact,
            service = asset.criticalityServiceImpact,
            financial = asset.criticalityFinancialImpact
        )
        val rating = AssetGovernance.criticalityRating(score)
        val assessmentChanged = existing == null ||
            existing.criticalitySafetyImpact != asset.criticalitySafetyImpact ||
            existing.criticalityProductionImpact != asset.criticalityProductionImpact ||
            existing.criticalityEnvironmentalImpact != asset.criticalityEnvironmentalImpact ||
            existing.criticalityServiceImpact != asset.criticalityServiceImpact ||
            existing.criticalityFinancialImpact != asset.criticalityFinancialImpact

        val governed = asset.copy(
            status = asset.operationalStatus.ifBlank { asset.status },
            operationalStatus = asset.operationalStatus.ifBlank { asset.status },
            criticality = rating,
            criticalityScore = score,
            criticalityAssessedAt = if (assessmentChanged) now else asset.criticalityAssessedAt,
            criticalityAssessedBy = if (assessmentChanged) actor else asset.criticalityAssessedBy,
            createdBy = if (isNew) actor else asset.createdBy,
            createdAt = if (isNew) now else asset.createdAt,
            updatedBy = actor,
            updatedAt = now
        )
        assetDao.insertAsset(governed)
        recordAudit(
            if (isNew) "Create" else "Update",
            "Asset",
            "${if (isNew) "إضافة" else "تعديل"} أصل: ${asset.code} • أهمية $rating ($score)",
            actor
        )
    }'''
    text = replace_function(text, "    suspend fun saveAsset(", save_asset)

    change_status = '''    suspend fun changeAssetStatus(asset: AssetEntity, status: String, actor: String = "System") {
        val now = DateStrings.now()
        assetDao.insertAsset(
            asset.copy(
                status = status,
                operationalStatus = status,
                updatedBy = actor,
                updatedAt = now
            )
        )
        recordAudit("Status", "Asset", "تغيير الحالة التشغيلية ${asset.code} من ${asset.effectiveOperationalStatus()} إلى $status", actor)
    }'''
    text = replace_function(text, "    suspend fun changeAssetStatus(", change_status)

    save_location = '''    suspend fun saveFunctionalLocation(location: FunctionalLocationEntity, actor: String = "System") {
        val isNew = location.id == 0L
        if (!isNew && location.parentId == location.id) {
            throw IllegalStateException("لا يمكن أن يكون الموقع أباً لنفسه")
        }
        if (!isNew && location.referenceLocationId == location.id) {
            throw IllegalStateException("لا يمكن أن يكون الموقع مرجعاً لنفسه")
        }

        val all = locationDao.dumpAll()
        var cursor = location.parentId
        val visited = mutableSetOf<Long>()
        while (cursor != null) {
            if (!isNew && cursor == location.id) {
                throw IllegalStateException("العلاقة المحددة تنشئ دورة في شجرة المواقع")
            }
            if (!visited.add(cursor)) {
                throw IllegalStateException("تم اكتشاف دورة في شجرة المواقع الفنية")
            }
            cursor = all.firstOrNull { it.id == cursor }?.parentId
        }

        val now = DateStrings.now()
        val governed = location.copy(
            referenceLocationId = if (location.isReference) null else location.referenceLocationId,
            createdAt = if (isNew) now else location.createdAt,
            updatedAt = now
        )
        locationDao.insert(governed)
        recordAudit(if (isNew) "Create" else "Update", "Location", "${if (isNew) "إضافة" else "تعديل"} موقع فني: ${location.code}", actor)
    }'''
    text = replace_function(text, "    suspend fun saveFunctionalLocation(", save_location)

    movement = '''    suspend fun performAssetMovement(
        asset: AssetEntity,
        eventType: String,
        toLocationId: Long?,
        toLocationName: String,
        notes: String,
        actor: String = "System"
    ) {
        database.withTransaction {
            val now = DateStrings.now()
            val targetLifecycle = when (eventType) {
                MovementType.INSTALL -> when (asset.lifecycleStatus) {
                    "Draft", "Acquired", "InStorage" -> "Installed"
                    else -> asset.lifecycleStatus
                }
                MovementType.DISMANTLE, MovementType.RETIRE -> "Decommissioned"
                else -> asset.lifecycleStatus
            }
            if (targetLifecycle != asset.lifecycleStatus) {
                AssetGovernance.requireLifecycleTransition(asset.lifecycleStatus, targetLifecycle)
            }
            if ((eventType == MovementType.DISMANTLE || eventType == MovementType.RETIRE) && notes.isBlank()) {
                throw IllegalStateException("سبب الفك أو التقاعد مطلوب")
            }

            val operational = when (eventType) {
                MovementType.INSTALL, MovementType.TRANSFER -> "Running"
                MovementType.DISMANTLE -> "Standby"
                MovementType.RETIRE -> "Retired"
                else -> asset.effectiveOperationalStatus()
            }
            val updated = when (eventType) {
                MovementType.INSTALL, MovementType.TRANSFER -> asset.copy(
                    locationId = toLocationId,
                    location = toLocationName.ifBlank { asset.location },
                    status = operational,
                    operationalStatus = operational,
                    lifecycleStatus = targetLifecycle,
                    updatedBy = actor,
                    updatedAt = now
                )
                MovementType.DISMANTLE -> asset.copy(
                    locationId = null,
                    status = operational,
                    operationalStatus = operational,
                    lifecycleStatus = targetLifecycle,
                    updatedBy = actor,
                    updatedAt = now
                )
                MovementType.RETIRE -> asset.copy(
                    status = operational,
                    operationalStatus = operational,
                    lifecycleStatus = targetLifecycle,
                    updatedBy = actor,
                    updatedAt = now
                )
                else -> asset
            }
            assetDao.insertAsset(updated)
            movementDao.insert(
                AssetMovementEntity(
                    assetId = asset.id,
                    eventType = eventType,
                    fromLocationId = asset.locationId,
                    toLocationId = if (eventType == MovementType.DISMANTLE || eventType == MovementType.RETIRE) null else toLocationId,
                    fromLocationName = asset.location,
                    toLocationName = if (eventType == MovementType.DISMANTLE || eventType == MovementType.RETIRE) "" else toLocationName,
                    notes = notes,
                    performedBy = actor,
                    occurredAt = now,
                    reason = notes,
                    previousLifecycleStatus = asset.lifecycleStatus,
                    newLifecycleStatus = targetLifecycle,
                    approvedBy = if (AssetGovernance.requiresApproval(targetLifecycle)) actor else "",
                    referenceType = "AssetMovement"
                )
            )
            recordAudit(
                "Movement",
                "Asset",
                "${MovementType.label(eventType)} للأصل: ${asset.code} • ${asset.lifecycleStatus} → $targetLifecycle",
                actor
            )
        }
    }'''
    text = replace_function(text, "    suspend fun performAssetMovement(", movement)

    path.write_text(text, encoding="utf-8")


def patch_forms() -> None:
    path = Path("app/src/main/java/com/alhadi/cmms/ui/Forms.kt")
    text = path.read_text(encoding="utf-8")

    if "import com.alhadi.cmms.domain.governance.AssetGovernance" not in text:
        text = text.replace(
            "import com.alhadi.cmms.data.entity.WorkPermitEntity\n",
            "import com.alhadi.cmms.data.entity.WorkPermitEntity\n"
            "import com.alhadi.cmms.domain.governance.AssetGovernance\n",
        )

    asset_form = '''@Composable
internal fun AssetFormSheet(
    initial: AssetEntity?,
    onDismiss: () -> Unit,
    onSave: (AssetEntity) -> Unit,
    locations: List<FunctionalLocationEntity> = emptyList(),
    allAssets: List<AssetEntity> = emptyList()
) {
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var assetType by remember { mutableStateOf(initial?.assetType ?: "Equipment") }
    var assetCategory by remember { mutableStateOf(initial?.assetCategory ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var group by remember { mutableStateOf(initial?.groupName ?: "") }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var locationId by remember { mutableStateOf(initial?.locationId) }
    var parentAssetId by remember { mutableStateOf(initial?.parentAssetId) }

    var organizationCode by remember { mutableStateOf(initial?.organizationCode ?: "") }
    var plantCode by remember { mutableStateOf(initial?.plantCode ?: "") }
    var workCenter by remember { mutableStateOf(initial?.maintenanceWorkCenter ?: "") }
    var planningGroup by remember { mutableStateOf(initial?.planningGroup ?: "") }
    var costCenter by remember { mutableStateOf(initial?.costCenter ?: "") }
    var ownerDepartment by remember { mutableStateOf(initial?.ownerDepartment ?: "") }
    var responsiblePerson by remember { mutableStateOf(initial?.responsiblePerson ?: "") }

    var manufacturer by remember { mutableStateOf(initial?.manufacturer ?: "") }
    var model by remember { mutableStateOf(initial?.model ?: "") }
    var manufacturingYear by remember { mutableStateOf(initial?.manufacturingYear?.toString() ?: "") }
    var serialNumber by remember { mutableStateOf(initial?.serialNumber ?: "") }
    var assetTag by remember { mutableStateOf(initial?.assetTag ?: "") }

    var lifecycleStatus by remember { mutableStateOf(initial?.lifecycleStatus ?: "Draft") }
    var operationalStatus by remember { mutableStateOf(initial?.effectiveOperationalStatus() ?: "Standby") }
    var healthStatus by remember { mutableStateOf(initial?.healthStatus ?: "Good") }

    var safetyImpact by remember { mutableStateOf((initial?.criticalitySafetyImpact ?: 1).toString()) }
    var productionImpact by remember { mutableStateOf((initial?.criticalityProductionImpact ?: 1).toString()) }
    var environmentImpact by remember { mutableStateOf((initial?.criticalityEnvironmentalImpact ?: 1).toString()) }
    var serviceImpact by remember { mutableStateOf((initial?.criticalityServiceImpact ?: 1).toString()) }
    var financialImpact by remember { mutableStateOf((initial?.criticalityFinancialImpact ?: 1).toString()) }

    var installedAt by remember { mutableStateOf(initial?.installedAt ?: "") }
    var lastInspectionAt by remember { mutableStateOf(initial?.lastInspectionAt ?: "") }
    var purchaseDate by remember { mutableStateOf(initial?.purchaseDate ?: "") }
    var commissioningDate by remember { mutableStateOf(initial?.commissioningDate ?: "") }
    var acquiredAt by remember { mutableStateOf(initial?.acquiredAt ?: "") }

    var supplier by remember { mutableStateOf(initial?.supplier ?: "") }
    var purchaseOrder by remember { mutableStateOf(initial?.purchaseOrder ?: "") }
    var purchaseCost by remember { mutableStateOf((initial?.purchaseCost ?: 0.0).toString()) }
    var financialAssetRef by remember { mutableStateOf(initial?.financialAssetRef ?: "") }
    var warrantyProvider by remember { mutableStateOf(initial?.warrantyProvider ?: "") }
    var warrantyStart by remember { mutableStateOf(initial?.warrantyStart ?: "") }
    var warrantyEnd by remember { mutableStateOf(initial?.warrantyEnd ?: "") }

    val impactOptions = listOf("1", "2", "3", "4", "5")
    val score = runCatching {
        AssetGovernance.calculateCriticalityScore(
            safetyImpact.toInt(),
            productionImpact.toInt(),
            environmentImpact.toInt(),
            serviceImpact.toInt(),
            financialImpact.toInt()
        )
    }.getOrDefault(5)
    val criticality = runCatching { AssetGovernance.criticalityRating(score) }.getOrDefault("Low")

    FormSheet(if (initial == null) "إضافة أصل جديد" else "تعديل الأصل", onDismiss) {
        Text("هوية الأصل", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("الكود (Code)", code, { code = it })
        LabeledField("الاسم (Name)", name, { name = it })
        OptionDropdown("نوع الأصل", listOf("Equipment", "Vehicle", "Tool", "Facility", "Instrument", "IT", "Other"), assetType) { assetType = it }
        LabeledField("تصنيف الأصل", assetCategory, { assetCategory = it })
        LabeledField("المجموعة", group, { group = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)

        Text("الموقع والهيكل", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("الموقع النصّي", location, { location = it })
        if (locations.isNotEmpty()) LocationDropdown("الموقع الفني", locations, locationId) { locationId = it }
        if (allAssets.isNotEmpty()) AssetDropdownOptional(allAssets, parentAssetId, { parentAssetId = it }, label = "الأصل الأب (اختياري)", excludeId = initial?.id)

        Text("البيانات التنظيمية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("المنظمة / الشركة", organizationCode, { organizationCode = it })
        LabeledField("المصنع", plantCode, { plantCode = it })
        LabeledField("مركز عمل الصيانة", workCenter, { workCenter = it })
        LabeledField("مجموعة التخطيط", planningGroup, { planningGroup = it })
        LabeledField("مركز التكلفة", costCenter, { costCenter = it })
        LabeledField("الإدارة المالكة", ownerDepartment, { ownerDepartment = it })
        LabeledField("المسؤول عن الأصل", responsiblePerson, { responsiblePerson = it })

        Text("المصنّع ودورة الحياة", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("الشركة المصنّعة", manufacturer, { manufacturer = it })
        LabeledField("الموديل", model, { model = it })
        LabeledField("سنة التصنيع", manufacturingYear, { manufacturingYear = it }, numeric = true)
        LabeledField("الرقم التسلسلي", serialNumber, { serialNumber = it })
        LabeledField("وسم الأصل", assetTag, { assetTag = it })
        OptionDropdown("حالة دورة الحياة", listOf("Draft", "Acquired", "InStorage", "Installed", "InService", "Standby", "Decommissioned", "Disposed"), lifecycleStatus) { lifecycleStatus = it }
        OptionDropdown("الحالة التشغيلية", listOf("Running", "Stopped", "Standby", "Under Maintenance", "Out of Service", "Retired"), operationalStatus) { operationalStatus = it }
        OptionDropdown("الحالة الصحية", listOf("Good", "Warning", "Critical", "Failed"), healthStatus) { healthStatus = it }
        DateField("تاريخ التركيب", installedAt) { installedAt = it }
        DateField("تاريخ بدء التشغيل", commissioningDate) { commissioningDate = it }
        DateField("آخر فحص", lastInspectionAt) { lastInspectionAt = it }

        Text("تقييم الأهمية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OptionDropdown("تأثير السلامة", impactOptions, safetyImpact) { safetyImpact = it }
        OptionDropdown("تأثير الإنتاج", impactOptions, productionImpact) { productionImpact = it }
        OptionDropdown("تأثير البيئة", impactOptions, environmentImpact) { environmentImpact = it }
        OptionDropdown("تأثير الخدمة", impactOptions, serviceImpact) { serviceImpact = it }
        OptionDropdown("التأثير المالي", impactOptions, financialImpact) { financialImpact = it }
        Text("النتيجة: $criticality ($score/25)", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold)

        Text("المعلومات المالية والضمان", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("المورّد", supplier, { supplier = it })
        LabeledField("أمر الشراء", purchaseOrder, { purchaseOrder = it })
        LabeledField("مرجع الأصل المالي", financialAssetRef, { financialAssetRef = it })
        LabeledField("تكلفة الشراء", purchaseCost, { purchaseCost = it }, numeric = true)
        DateField("تاريخ الشراء", purchaseDate) { purchaseDate = it }
        DateField("تاريخ الاقتناء", acquiredAt) { acquiredAt = it }
        LabeledField("جهة الضمان", warrantyProvider, { warrantyProvider = it })
        DateField("بداية الضمان", warrantyStart) { warrantyStart = it }
        DateField("نهاية الضمان", warrantyEnd) { warrantyEnd = it }

        SaveButton(code.isNotBlank() && name.isNotBlank()) {
            val today = DateStrings.today()
            onSave(
                AssetEntity(
                    id = initial?.id ?: 0,
                    code = code.trim(),
                    name = name.trim(),
                    groupName = group.ifBlank { "General" },
                    location = location.trim(),
                    manufacturer = manufacturer.trim(),
                    model = model.trim(),
                    status = operationalStatus,
                    criticality = criticality,
                    installedAt = installedAt.ifBlank { initial?.installedAt ?: today },
                    lastInspectionAt = lastInspectionAt.ifBlank { initial?.lastInspectionAt ?: today },
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
                    description = description.trim(),
                    organizationCode = organizationCode.trim(),
                    plantCode = plantCode.trim(),
                    maintenanceWorkCenter = workCenter.trim(),
                    planningGroup = planningGroup.trim(),
                    costCenter = costCenter.trim(),
                    ownerDepartment = ownerDepartment.trim(),
                    responsiblePerson = responsiblePerson.trim(),
                    manufacturingYear = manufacturingYear.toIntOrNull(),
                    purchaseDate = purchaseDate.trim(),
                    commissioningDate = commissioningDate.trim(),
                    financialAssetRef = financialAssetRef.trim(),
                    lifecycleStatus = lifecycleStatus,
                    operationalStatus = operationalStatus,
                    healthStatus = healthStatus,
                    criticalitySafetyImpact = safetyImpact.toIntOrNull()?.coerceIn(1, 5) ?: 1,
                    criticalityProductionImpact = productionImpact.toIntOrNull()?.coerceIn(1, 5) ?: 1,
                    criticalityEnvironmentalImpact = environmentImpact.toIntOrNull()?.coerceIn(1, 5) ?: 1,
                    criticalityServiceImpact = serviceImpact.toIntOrNull()?.coerceIn(1, 5) ?: 1,
                    criticalityFinancialImpact = financialImpact.toIntOrNull()?.coerceIn(1, 5) ?: 1,
                    criticalityScore = score,
                    criticalityAssessedAt = initial?.criticalityAssessedAt ?: "",
                    criticalityAssessedBy = initial?.criticalityAssessedBy ?: "",
                    createdBy = initial?.createdBy ?: "",
                    createdAt = initial?.createdAt ?: "",
                    updatedBy = initial?.updatedBy ?: "",
                    updatedAt = initial?.updatedAt ?: ""
                )
            )
        }
    }
}'''
    text = replace_function(text, "@Composable\ninternal fun AssetFormSheet(", asset_form)

    location_form = '''@Composable
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
    var organizationCode by remember { mutableStateOf(initial?.organizationCode ?: "") }
    var plantCode by remember { mutableStateOf(initial?.plantCode ?: "") }
    var category by remember { mutableStateOf(initial?.locationCategory ?: "") }
    var costCenter by remember { mutableStateOf(initial?.costCenterCode ?: "") }
    var workCenter by remember { mutableStateOf(initial?.workCenterCode ?: "") }
    var referenceLocationId by remember { mutableStateOf(initial?.referenceLocationId) }
    var isReference by remember { mutableStateOf(initial?.isReference ?: false) }

    FormSheet(if (initial == null) "إضافة موقع فني" else "تعديل الموقع الفني", onDismiss) {
        LabeledField("الكود (Code)", code, { code = it })
        LabeledField("الاسم (Name)", name, { name = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        LocationDropdown("الموقع الأعلى (Parent)", allLocations, parentId, excludeId = initial?.id) { parentId = it }
        OptionDropdown("الحالة", listOf("Active", "Inactive"), status) { status = it }
        LabeledField("المنظمة / الشركة", organizationCode, { organizationCode = it })
        LabeledField("المصنع", plantCode, { plantCode = it })
        LabeledField("فئة الموقع", category, { category = it })
        LabeledField("مركز التكلفة", costCenter, { costCenter = it })
        LabeledField("مركز العمل", workCenter, { workCenter = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("موقع مرجعي", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isReference, onCheckedChange = { isReference = it; if (it) referenceLocationId = null })
        }
        if (!isReference) {
            LocationDropdown("الموقع المرجعي (اختياري)", allLocations, referenceLocationId, excludeId = initial?.id) { referenceLocationId = it }
        }
        SaveButton(code.isNotBlank() && name.isNotBlank()) {
            onSave(
                FunctionalLocationEntity(
                    id = initial?.id ?: 0,
                    code = code.trim(),
                    name = name.trim(),
                    parentId = parentId,
                    description = description.trim(),
                    status = status,
                    organizationCode = organizationCode.trim(),
                    plantCode = plantCode.trim(),
                    locationCategory = category.trim(),
                    costCenterCode = costCenter.trim(),
                    workCenterCode = workCenter.trim(),
                    referenceLocationId = if (isReference) null else referenceLocationId,
                    isReference = isReference,
                    createdAt = initial?.createdAt ?: "",
                    updatedAt = initial?.updatedAt ?: ""
                )
            )
        }
    }
}'''
    text = replace_function(text, "@Composable\ninternal fun LocationFormSheet(", location_form)

    movement_form = '''@Composable
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
    val requiresReason = type == MovementType.DISMANTLE || type == MovementType.RETIRE

    FormSheet("حركة الأصل: ${asset.code}", onDismiss) {
        Text("دورة الحياة الحالية: ${asset.lifecycleStatus}", style = MaterialTheme.typography.bodySmall)
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
        LabeledField(if (requiresReason) "السبب (إلزامي)" else "السبب / الملاحظات", notes, { notes = it }, singleLine = false)
        SaveButton((!needsLocation || locId != null) && (!requiresReason || notes.isNotBlank())) {
            val name = locations.firstOrNull { it.id == locId }?.name ?: ""
            onSave(type, if (needsLocation) locId else null, if (needsLocation) name else "", notes.trim())
        }
    }
}'''
    text = replace_function(text, "@Composable\ninternal fun MovementFormSheet(", movement_form)

    path.write_text(text, encoding="utf-8")


def patch_app_ui() -> None:
    path = Path("app/src/main/java/com/alhadi/cmms/ui/CmmsApp.kt")
    text = path.read_text(encoding="utf-8")

    text = text.replace(
        "asset.assetTag.lowercase(Locale.getDefault()).contains(q)",
        "asset.assetTag.lowercase(Locale.getDefault()).contains(q) ||\n"
        "                asset.assetType.lowercase(Locale.getDefault()).contains(q) ||\n"
        "                asset.assetCategory.lowercase(Locale.getDefault()).contains(q) ||\n"
        "                asset.organizationCode.lowercase(Locale.getDefault()).contains(q) ||\n"
        "                asset.plantCode.lowercase(Locale.getDefault()).contains(q)",
    )

    text = text.replace("val tone = statusTone(asset.status)", "val tone = statusTone(asset.effectiveOperationalStatus())")
    text = text.replace("StatusBadge(asset.status, tone)", "StatusBadge(asset.effectiveOperationalStatus(), tone)")
    text = text.replace(
        "AssistChip(onClick = {}, label = { Text(asset.location, maxLines = 1) })",
        "AssistChip(onClick = {}, label = { Text(asset.location, maxLines = 1) })\n"
        "                if (asset.assetCategory.isNotBlank()) AssistChip(onClick = {}, label = { Text(asset.assetCategory, maxLines = 1) })",
    )
    text = text.replace(
        "val retired = asset.status.equals(\"Retired\", ignoreCase = true)",
        "val retired = asset.lifecycleStatus == \"Decommissioned\" || asset.lifecycleStatus == \"Disposed\" || asset.status.equals(\"Retired\", ignoreCase = true)",
    )
    text = text.replace(
        "StatusBadge(asset.status, statusTone(asset.status))",
        "StatusBadge(asset.effectiveOperationalStatus(), statusTone(asset.effectiveOperationalStatus()))",
    )

    old_info = '''                    SectionHeader("المعلومات")
                    InfoRow("المجموعة", asset.groupName)
                    InfoRow("الموقع", asset.location)
                    InfoRow("الموقع الفني", locationLabel)
                    InfoRow("الأصل الأب", parent?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                    InfoRow("الشركة/الموديل", "${asset.manufacturer} • ${asset.model}")
                    if (asset.serialNumber.isNotBlank()) InfoRow("الرقم التسلسلي", asset.serialNumber)
                    if (asset.assetTag.isNotBlank()) InfoRow("وسم الأصل", asset.assetTag)
                    InfoRow("الأهمية", asset.criticality)
                    InfoRow("تاريخ التركيب", asset.installedAt)
                    InfoRow("آخر فحص", asset.lastInspectionAt)'''
    new_info = '''                    SectionHeader("بطاقة الأصل والحوكمة")
                    InfoRow("نوع الأصل", asset.assetType)
                    if (asset.assetCategory.isNotBlank()) InfoRow("التصنيف", asset.assetCategory)
                    InfoRow("المجموعة", asset.groupName)
                    if (asset.description.isNotBlank()) InfoRow("الوصف", asset.description)
                    InfoRow("الموقع", asset.location)
                    InfoRow("الموقع الفني", locationLabel)
                    InfoRow("الأصل الأب", parent?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                    InfoRow("دورة الحياة", asset.lifecycleStatus)
                    InfoRow("الحالة التشغيلية", asset.effectiveOperationalStatus())
                    InfoRow("الحالة الصحية", asset.healthStatus)
                    InfoRow("الشركة/الموديل", "${asset.manufacturer} • ${asset.model}")
                    asset.manufacturingYear?.let { InfoRow("سنة التصنيع", it.toString()) }
                    if (asset.serialNumber.isNotBlank()) InfoRow("الرقم التسلسلي", asset.serialNumber)
                    if (asset.assetTag.isNotBlank()) InfoRow("وسم الأصل", asset.assetTag)
                    InfoRow("الأهمية", "${asset.criticality} (${asset.criticalityScore}/25)")
                    if (asset.organizationCode.isNotBlank()) InfoRow("المنظمة", asset.organizationCode)
                    if (asset.plantCode.isNotBlank()) InfoRow("المصنع", asset.plantCode)
                    if (asset.maintenanceWorkCenter.isNotBlank()) InfoRow("مركز العمل", asset.maintenanceWorkCenter)
                    if (asset.planningGroup.isNotBlank()) InfoRow("مجموعة التخطيط", asset.planningGroup)
                    if (asset.costCenter.isNotBlank()) InfoRow("مركز التكلفة", asset.costCenter)
                    if (asset.ownerDepartment.isNotBlank()) InfoRow("الإدارة المالكة", asset.ownerDepartment)
                    if (asset.responsiblePerson.isNotBlank()) InfoRow("المسؤول", asset.responsiblePerson)
                    InfoRow("تاريخ التركيب", asset.installedAt)
                    if (asset.commissioningDate.isNotBlank()) InfoRow("بدء التشغيل", asset.commissioningDate)
                    InfoRow("آخر فحص", asset.lastInspectionAt)'''
    if old_info in text:
        text = text.replace(old_info, new_info)

    text = text.replace(
        "asset.purchaseCost > 0.0 || asset.acquiredAt.isNotBlank()",
        "asset.purchaseCost > 0.0 || asset.acquiredAt.isNotBlank() || asset.purchaseDate.isNotBlank() ||\n"
        "            asset.financialAssetRef.isNotBlank()",
    )
    text = text.replace(
        "if (asset.acquiredAt.isNotBlank()) InfoRow(\"تاريخ الاقتناء\", asset.acquiredAt)",
        "if (asset.purchaseDate.isNotBlank()) InfoRow(\"تاريخ الشراء\", asset.purchaseDate)\n"
        "                        if (asset.acquiredAt.isNotBlank()) InfoRow(\"تاريخ الاقتناء\", asset.acquiredAt)\n"
        "                        if (asset.financialAssetRef.isNotBlank()) InfoRow(\"مرجع الأصل المالي\", asset.financialAssetRef)",
    )

    movement_note = "if (mv.notes.isNotBlank()) Text(mv.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)"
    if "mv.previousLifecycleStatus" not in text:
        text = text.replace(
            movement_note,
            movement_note + "\n"
            "                        if (mv.previousLifecycleStatus.isNotBlank() || mv.newLifecycleStatus.isNotBlank()) {\n"
            "                            Text(\"دورة الحياة: ${mv.previousLifecycleStatus.ifBlank { \"—\" }} → ${mv.newLifecycleStatus.ifBlank { \"—\" }}\", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)\n"
            "                        }\n"
            "                        if (mv.approvedBy.isNotBlank()) Text(\"اعتمدها: ${mv.approvedBy}\", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)",
        )

    path.write_text(text, encoding="utf-8")


patch_repository()
patch_forms()
patch_app_ui()
print("Applied App2 stages 11-15 implementation patch.")

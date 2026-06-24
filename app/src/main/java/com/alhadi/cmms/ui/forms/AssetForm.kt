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
// Asset form
// ---------------------------------------------------------------------------


@Composable
internal fun AssetFormSheet(
    initial: AssetEntity?,
    onDismiss: () -> Unit,
    onSave: (AssetEntity) -> Unit,
    locations: List<FunctionalLocationEntity> = emptyList(),
    allAssets: List<AssetEntity> = emptyList(),
    orgUnits: List<OrgUnitEntity> = emptyList(),
    canOverrideSerial: Boolean = false,
    hasLinkedParts: Boolean = false
) {
    val s = remember { AssetFormState(initial) }

    with(s) {

    // --- Asset technical governance rules (AST-TECH-001..005) ---
    val isCritical = criticality.equals("Critical", ignoreCase = true)
    // AST-TECH-001: critical assets must carry basic technical data.
    val tech001Valid = !isCritical ||
        (manufacturer.isNotBlank() && model.isNotBlank() &&
            listOf(power, capacity, voltage, nameplateData).any { it.isNotBlank() })
    // AST-TECH-002: serial number required when individual tracking is enabled.
    val tech002Valid = !requiresSerialTracking || serialNumber.isNotBlank()
    // AST-TECH-004: model required when the asset has model-linked spare parts.
    val tech004Valid = !hasLinkedParts || model.isNotBlank()
    // AST-TECH-003: serial locked once a commissioning/installation date exists, unless overridden.
    val serialDateExists = (initial?.installedAt?.isNotBlank() == true) ||
        (initial?.startupDate?.isNotBlank() == true)
    val serialLocked = initial != null && serialDateExists && !canOverrideSerial

    // --- Asset organizational governance (AST-ORG-* / AST-ORG-SAVE-*) ---
    // Activation = saving as any operational (non-draft) status.
    val activating = !status.equals("Draft", ignoreCase = true)
    val isMobile = mobility.equals("Mobile", ignoreCase = true)
    val selectedLoc = locations.firstOrNull { it.id == locationId }
    val org001Company = !activating || company.isNotBlank()              // SAVE-001
    val org002Plant = !activating || maintenancePlant.isNotBlank()       // SAVE-002
    val org003Location = !activating ||                                  // SAVE-003 / ORG-002 / ORG-003
        (if (isMobile) location.isNotBlank() else locationId != null)
    val org004WorkCenter = !activating || !isCritical || mainWorkCenter.isNotBlank()  // SAVE-004
    val org005Planner = !activating || !isCritical || plannerGroup.isNotBlank()       // SAVE-005
    val org006CostCenter = !activating || !incursOperatingCost || costCenter.isNotBlank() // SAVE-006
    // SAVE-007: an active asset cannot reference an inactive functional location.
    val org007FlActive = !activating || selectedLoc == null || selectedLoc.status.equals("Active", ignoreCase = true)
    // SAVE-008/009/010: an active asset cannot reference an inactive Work Center / Planner Group / Cost Center.
    fun orgRefInactive(unitType: String, value: String): Boolean =
        value.isNotBlank() && orgUnits.any { it.type == unitType && it.code.equals(value, ignoreCase = true) && !it.isActive }
    val org008WcActive = !activating || !orgRefInactive("WorkCenter", mainWorkCenter)      // SAVE-008
    val org009PgActive = !activating || !orgRefInactive("PlannerGroup", plannerGroup)      // SAVE-009
    val org010CcActive = !activating || !orgRefInactive("CostCenter", costCenter)          // SAVE-010
    // AST-ORG-SAVE-012: overriding a value inherited from the functional location needs a reason.
    fun overridesInherited(flValue: String, assetValue: String): Boolean =
        flValue.isNotBlank() && assetValue.isNotBlank() && !assetValue.equals(flValue, ignoreCase = true)
    val hasOrgOverride = selectedLoc != null && (
        overridesInherited(selectedLoc.plantCode, maintenancePlant) ||
            overridesInherited(selectedLoc.workCenterCode, mainWorkCenter) ||
            overridesInherited(selectedLoc.costCenterCode, costCenter) ||
            overridesInherited(selectedLoc.plannerGroupCode, plannerGroup)
        )
    val org012OverrideReason = !hasOrgOverride || orgOverrideReason.isNotBlank()
    val orgValid = org001Company && org002Plant && org003Location &&
        org004WorkCenter && org005Planner && org006CostCenter && org007FlActive &&
        org008WcActive && org009PgActive && org010CcActive && org012OverrideReason

    // AST-ORG-009: when a functional location is chosen, suggest its plant / work center /
    // cost center / planner group into any blank asset field (inherited defaults).
    LaunchedEffect(locationId) {
        val loc = locations.firstOrNull { it.id == locationId } ?: return@LaunchedEffect
        if (maintenancePlant.isBlank() && loc.plantCode.isNotBlank()) maintenancePlant = loc.plantCode
        if (mainWorkCenter.isBlank() && loc.workCenterCode.isNotBlank()) mainWorkCenter = loc.workCenterCode
        if (costCenter.isBlank() && loc.costCenterCode.isNotBlank()) costCenter = loc.costCenterCode
        if (plannerGroup.isBlank() && loc.plannerGroupCode.isNotBlank()) plannerGroup = loc.plannerGroupCode
    }

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
            if (initial == null) {
                LocationDropdown("الموقع الفني", locations, locationId) { locationId = it }
            } else {
                // AST-ORG-007/008 & SAVE-011: location changes for an existing asset must go via a Transfer movement.
                val locName = locations.firstOrNull { it.id == locationId }?.let { "${it.code} • ${it.name}" } ?: "غير محدد"
                LabeledField("الموقع الفني", locName, {}, enabled = false)
                Text("AST-ORG-008: لتغيير الموقع الفني استخدم حركة نقل (Transfer) من بطاقة الأصل.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
            }
        }
        if (allAssets.isNotEmpty()) {
            AssetDropdownOptional(allAssets, parentAssetId, { parentAssetId = it }, label = "الأصل الأب (اختياري)", excludeId = initial?.id)
        }

        assetClassificationSection(s)

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
        OptionDropdown("الحالة", listOf("Draft", "Active", "Running", "Warning", "Stopped", "Under Maintenance", "Standby", "Retired", "Disposed"), status) { status = it }
        OptionDropdown("الأهمية", listOf("Low", "Medium", "High", "Critical"), criticality) { criticality = it }
        if (!tech004Valid) Text("AST-TECH-004: الموديل مطلوب لوجود قطع غيار مرتبطة بالموديل.", color = MaterialTheme.colorScheme.error)

        Text("المواصفات الفنية (لوحة الصنع)", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        if (isCritical) {
            Text("AST-TECH-001: الأصل الحرج يجب أن يحتوي بيانات فنية أساسية (الشركة المصنّعة، الموديل، وأحد بيانات لوحة الصنع).",
                color = if (tech001Valid) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall)
        }
        LabeledField("بلد المنشأ", countryOfOrigin, { countryOfOrigin = it })
        LabeledField("بيانات لوحة الصنع", nameplateData, { nameplateData = it }, singleLine = false)
        LabeledField("نوع الإنشاء (Construction Type)", constructionType, { constructionType = it })
        LabeledField("مجموعة المواصفات الفنية", technicalSpecGroup, { technicalSpecGroup = it })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { LabeledField("السعة (Capacity)", capacity, { capacity = it }) }
            Box(modifier = Modifier.weight(1f)) { LabeledField("القدرة (Power)", power, { power = it }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { LabeledField("الجهد (Voltage)", voltage, { voltage = it }) }
            Box(modifier = Modifier.weight(1f)) { LabeledField("التيار (Current)", current, { current = it }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { LabeledField("التردد (Frequency)", frequency, { frequency = it }) }
            Box(modifier = Modifier.weight(1f)) { LabeledField("السرعة (Speed)", speed, { speed = it }) }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { LabeledField("الضغط (Pressure)", pressure, { pressure = it }) }
            Box(modifier = Modifier.weight(1f)) { LabeledField("معدل التدفق (Flow Rate)", flowRate, { flowRate = it }) }
        }
        LabeledField("نطاق درجة الحرارة", temperatureRange, { temperatureRange = it })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.weight(1f)) { LabeledField("الوزن (Weight)", weight, { weight = it }) }
            Box(modifier = Modifier.weight(1f)) { LabeledField("الأبعاد (Dimensions)", dimensions, { dimensions = it }) }
        }
        LabeledField("المادة (Material)", material, { material = it })
        LabeledField("معيار التصميم (Design Standard)", designStandard, { designStandard = it })

        Text("التنظيم والمسؤولية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        OptionDropdown("نوع التركيب", listOf("Fixed", "Mobile"), mobility, display = ::mobilityLabel) { mobility = it }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("أصل ذو تكلفة تشغيلية", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = incursOperatingCost, onCheckedChange = { incursOperatingCost = it })
        }
        OrgUnitDropdown("الشركة (Company)", "Company", orgUnits, company) { company = it }
        if (!org001Company) Text("AST-ORG-SAVE-001: الأصل النشط يتطلب Company.", color = MaterialTheme.colorScheme.error)
        LabeledField("الموقع/المنشأة", site, { site = it })
        OrgUnitDropdown("المصنع (Plant)", "Plant", orgUnits, maintenancePlant) { maintenancePlant = it }
        InheritedCaption(selectedLoc != null && maintenancePlant.isNotBlank() && maintenancePlant.equals(selectedLoc.plantCode, ignoreCase = true))
        if (!org002Plant) Text("AST-ORG-SAVE-002: الأصل النشط يتطلب Plant.", color = MaterialTheme.colorScheme.error)
        LabeledField("مصنع التخطيط", planningPlant, { planningPlant = it })
        OrgUnitDropdown("مجموعة المخططين (Planner Group)", "PlannerGroup", orgUnits, plannerGroup) { plannerGroup = it }
        InheritedCaption(selectedLoc != null && plannerGroup.isNotBlank() && plannerGroup.equals(selectedLoc.plannerGroupCode, ignoreCase = true))
        if (!org005Planner) Text("AST-ORG-SAVE-005: الأصل الحرج يتطلب Planner Group.", color = MaterialTheme.colorScheme.error)
        if (!org009PgActive) Text("AST-ORG-SAVE-009: لا يمكن ربط Planner Group غير نشط بأصل نشط.", color = MaterialTheme.colorScheme.error)
        OrgUnitDropdown("مركز العمل الرئيسي (Work Center)", "WorkCenter", orgUnits, mainWorkCenter) { mainWorkCenter = it }
        InheritedCaption(selectedLoc != null && mainWorkCenter.isNotBlank() && mainWorkCenter.equals(selectedLoc.workCenterCode, ignoreCase = true))
        if (!org004WorkCenter) Text("AST-ORG-SAVE-004: الأصل الحرج يتطلب Work Center.", color = MaterialTheme.colorScheme.error)
        if (!org008WcActive) Text("AST-ORG-SAVE-008: لا يمكن ربط Work Center غير نشط بأصل نشط.", color = MaterialTheme.colorScheme.error)
        LabeledField("مركز عمل الإنتاج", productionWorkCenter, { productionWorkCenter = it })
        OrgUnitDropdown("مركز التكلفة (Cost Center)", "CostCenter", orgUnits, costCenter) { costCenter = it }
        InheritedCaption(selectedLoc != null && costCenter.isNotBlank() && costCenter.equals(selectedLoc.costCenterCode, ignoreCase = true))
        if (!org006CostCenter) Text("AST-ORG-SAVE-006: الأصل ذو التكلفة التشغيلية يتطلب Cost Center.", color = MaterialTheme.colorScheme.error)
        if (!org010CcActive) Text("AST-ORG-SAVE-010: لا يمكن ربط Cost Center غير نشط بأصل نشط.", color = MaterialTheme.colorScheme.error)
        if (hasOrgOverride) {
            LabeledField("سبب تجاوز البيانات الموروثة (Override)", orgOverrideReason, { orgOverrideReason = it }, singleLine = false)
            if (!org012OverrideReason) Text("AST-ORG-SAVE-012: أدخل سبب تجاوز القيم الموروثة من الموقع الفني.", color = MaterialTheme.colorScheme.error)
        }
        if (!org003Location) Text(
            if (isMobile) "AST-ORG-003: الأصل المتنقل يتطلب تحديد مكانه الحالي (الموقع النصّي)."
            else "AST-ORG-SAVE-003: الأصل المركّب يتطلب موقعاً فنياً.",
            color = MaterialTheme.colorScheme.error
        )
        if (!org007FlActive) Text("AST-ORG-SAVE-007: لا يمكن ربط موقع فني غير نشط بأصل نشط.", color = MaterialTheme.colorScheme.error)
        LabeledField("الشخص المسؤول", responsiblePerson, { responsiblePerson = it })

        Text("الهوية والترميز", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("يتطلب تتبعاً فردياً (رقم تسلسلي إلزامي)", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = requiresSerialTracking, onCheckedChange = { requiresSerialTracking = it })
        }
        LabeledField("الرقم التسلسلي", serialNumber, { serialNumber = it }, enabled = !serialLocked)
        if (serialLocked) {
            Text("AST-TECH-003: لا يمكن تعديل الرقم التسلسلي بعد تسجيل تاريخ التشغيل إلا بصلاحية خاصة.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        }
        if (!tech002Valid) Text("AST-TECH-002: الرقم التسلسلي مطلوب لأن الأصل يتطلب تتبعاً فردياً.", color = MaterialTheme.colorScheme.error)
        LabeledField("وسم الأصل", assetTag, { assetTag = it })
        LabeledField("التسمية البديلة", alternativeLabel, { alternativeLabel = it })
        LabeledField("الوصف المطوّل", longDescription, { longDescription = it }, singleLine = false)
        LabeledField("الكود الخارجي", externalAssetCode, { externalAssetCode = it })
        LabeledField("الكود القديم (Legacy)", legacyAssetCode, { legacyAssetCode = it })
        LabeledField("الباركود", barcode, { barcode = it })
        LabeledField("رمز QR", qrCode, { qrCode = it })

        assetSafetySection(s)

        assetFinancialSection(s)

        assetContactSection(s)

        Text("الضمان", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("جهة الضمان", warrantyProvider, { warrantyProvider = it })
        // AST-WAR-003: warranty dates are locked once set, unless the user can override (admin).
        val warrantyDatesLocked = initial != null &&
            (initial.warrantyStart.isNotBlank() || initial.warrantyEnd.isNotBlank()) && !canOverrideSerial
        if (warrantyDatesLocked) {
            LabeledField("بداية الضمان", warrantyStart, {}, enabled = false)
            LabeledField("نهاية الضمان", warrantyEnd, {}, enabled = false)
            Text("AST-WAR-003: لا يمكن تعديل تواريخ الضمان إلا بصلاحية خاصة.", color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodySmall)
        } else {
            DateField("بداية الضمان", warrantyStart) { warrantyStart = it }
            DateField("نهاية الضمان", warrantyEnd) { warrantyEnd = it }
        }
        LabeledField("مرجع/رقم عقد الضمان", warrantyReference, { warrantyReference = it })
        OptionDropdown("نوع الضمان", listOf("", "Standard", "Extended", "Service Contract", "AMC"), warrantyType, display = ::warrantyTypeLabel) { warrantyType = it }
        LabeledField("فئة الضمان", warrantyCategory, { warrantyCategory = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("ضمان المورّد", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = vendorWarranty, onCheckedChange = { vendorWarranty = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("ضمان المُصنّع", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = manufacturerWarranty, onCheckedChange = { manufacturerWarranty = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("ضمان العميل", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = customerWarranty, onCheckedChange = { customerWarranty = it })
        }
        // AST-WAR-002: coverage by date and/or counter.
        OptionDropdown("نوع عدّاد الضمان", listOf("", "Hours", "Km", "Cycles", "Production"), warrantyCounterType, display = ::warrantyCounterTypeLabel) { warrantyCounterType = it }
        if (warrantyCounterType.isNotBlank()) {
            LabeledField("حد العدّاد", warrantyCounterLimit, { warrantyCounterLimit = it }, numeric = true)
        }
        LabeledField("الشروط والأحكام", warrantyTerms, { warrantyTerms = it }, singleLine = false)
        LabeledField("الخدمات المشمولة", coveredServices, { coveredServices = it }, singleLine = false)
        LabeledField("الخدمات المستثناة", excludedServices, { excludedServices = it }, singleLine = false)
        LabeledField("جهة اتصال الضمان", warrantyContact, { warrantyContact = it })
        // AST-WAR-006: warranty document linked to the asset card.
        LabeledField("مستند الضمان (مرجع/رابط)", warrantyDocument, { warrantyDocument = it })
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("يتطلب مطالبة ضمان", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = warrantyClaimRequired, onCheckedChange = { warrantyClaimRequired = it })
        }
        if (warrantyClaimRequired) {
            OptionDropdown("حالة المطالبة", listOf("", "None", "Submitted", "UnderReview", "Approved", "Rejected"), warrantyClaimStatus, display = ::warrantyClaimStatusLabel) { warrantyClaimStatus = it }
        }
        if (!tech001Valid) Text("AST-TECH-001: أكمل البيانات الفنية الأساسية للأصل الحرج قبل الحفظ.", color = MaterialTheme.colorScheme.error)
        val today = DateStrings.today()
        // Save Draft: allow persisting an incomplete asset as a draft (skips mandatory rules).
        OutlinedButton(
            onClick = { onSave(toAssetEntity(initial, "Draft", today, serialLocked, warrantyDatesLocked, hasOrgOverride, linearStartValue, linearEndValue)) },
            enabled = code.isNotBlank() && name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("حفظ كمسودة") }
        // Activate / Save: only after mandatory data (technical + organizational rules) is complete.
        SaveButton(
            code.isNotBlank() && name.isNotBlank() && linearRangeValid && markerDistancesValid && coordinatesValid &&
                tech001Valid && tech002Valid && tech004Valid && orgValid
        ) {
            onSave(toAssetEntity(initial, if (status.equals("Draft", ignoreCase = true)) "Active" else status, today, serialLocked, warrantyDatesLocked, hasOrgOverride, linearStartValue, linearEndValue))
        }
    }
    }
}


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
    var code by remember { mutableStateOf(initial?.code ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var group by remember { mutableStateOf(initial?.groupName ?: "") }
    var location by remember { mutableStateOf(initial?.location ?: "") }
    var manufacturer by remember { mutableStateOf(initial?.manufacturer ?: "") }
    var model by remember { mutableStateOf(initial?.model ?: "") }
    var status by remember { mutableStateOf(initial?.status ?: "Running") }
    var criticality by remember { mutableStateOf(initial?.criticality ?: "Medium") }
    var locationId by remember { mutableStateOf(initial?.locationId) }
    var mobility by remember { mutableStateOf(initial?.mobility ?: "Fixed") }
    var incursOperatingCost by remember { mutableStateOf(initial?.incursOperatingCost ?: false) }
    var orgOverrideReason by remember { mutableStateOf(initial?.orgOverrideReason ?: "") }
    var parentAssetId by remember { mutableStateOf(initial?.parentAssetId) }
    var warrantyProvider by remember { mutableStateOf(initial?.warrantyProvider ?: "") }
    var warrantyStart by remember { mutableStateOf(initial?.warrantyStart ?: "") }
    var warrantyEnd by remember { mutableStateOf(initial?.warrantyEnd ?: "") }
    var warrantyType by remember { mutableStateOf(initial?.warrantyType ?: "") }
    var warrantyCategory by remember { mutableStateOf(initial?.warrantyCategory ?: "") }
    var warrantyTerms by remember { mutableStateOf(initial?.warrantyTerms ?: "") }
    var coveredServices by remember { mutableStateOf(initial?.coveredServices ?: "") }
    var excludedServices by remember { mutableStateOf(initial?.excludedServices ?: "") }
    var warrantyCounterType by remember { mutableStateOf(initial?.warrantyCounterType ?: "") }
    var warrantyCounterLimit by remember { mutableStateOf((initial?.warrantyCounterLimit ?: 0.0).toString()) }
    var warrantyClaimRequired by remember { mutableStateOf(initial?.warrantyClaimRequired ?: false) }
    var warrantyClaimStatus by remember { mutableStateOf(initial?.warrantyClaimStatus ?: "") }
    var warrantyContact by remember { mutableStateOf(initial?.warrantyContact ?: "") }
    var warrantyDocument by remember { mutableStateOf(initial?.warrantyDocument ?: "") }
    var vendorWarranty by remember { mutableStateOf(initial?.vendorWarranty ?: false) }
    var manufacturerWarranty by remember { mutableStateOf(initial?.manufacturerWarranty ?: false) }
    var customerWarranty by remember { mutableStateOf(initial?.customerWarranty ?: false) }
    var warrantyReference by remember { mutableStateOf(initial?.warrantyReference ?: "") }
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
    // Asset identity governance
    var longDescription by remember { mutableStateOf(initial?.longDescription ?: "") }
    var alternativeLabel by remember { mutableStateOf(initial?.alternativeLabel ?: "") }
    var externalAssetCode by remember { mutableStateOf(initial?.externalAssetCode ?: "") }
    var legacyAssetCode by remember { mutableStateOf(initial?.legacyAssetCode ?: "") }
    var barcode by remember { mutableStateOf(initial?.barcode ?: "") }
    var qrCode by remember { mutableStateOf(initial?.qrCode ?: "") }
    var equipmentCategory by remember { mutableStateOf(initial?.equipmentCategory ?: "") }
    var assetClass by remember { mutableStateOf(initial?.assetClass ?: "") }
    var assetSubclass by remember { mutableStateOf(initial?.assetSubclass ?: "") }
    var company by remember { mutableStateOf(initial?.company ?: "") }
    var site by remember { mutableStateOf(initial?.site ?: "") }
    var safetyCritical by remember { mutableStateOf(initial?.safetyCritical ?: false) }
    var riskLevel by remember { mutableStateOf(initial?.riskLevel ?: "") }
    var requiredPermits by remember { mutableStateOf(initial?.requiredPermits ?: "") }
    var safetyInstructions by remember { mutableStateOf(initial?.safetyInstructions ?: "") }
    var ppeRequired by remember { mutableStateOf(initial?.ppeRequired ?: "") }
    var isolationRequired by remember { mutableStateOf(initial?.isolationRequired ?: false) }
    var complianceRequirements by remember { mutableStateOf(initial?.complianceRequirements ?: "") }
    var financialStatus by remember { mutableStateOf(initial?.financialStatus ?: "") }
    var bookValue by remember { mutableStateOf((initial?.bookValue ?: 0.0).toString()) }
    var capitalizationAt by remember { mutableStateOf(initial?.capitalizationAt ?: "") }
    // Manufacturing & technical specifications
    var countryOfOrigin by remember { mutableStateOf(initial?.countryOfOrigin ?: "") }
    var nameplateData by remember { mutableStateOf(initial?.nameplateData ?: "") }
    var capacity by remember { mutableStateOf(initial?.capacity ?: "") }
    var power by remember { mutableStateOf(initial?.power ?: "") }
    var voltage by remember { mutableStateOf(initial?.voltage ?: "") }
    var current by remember { mutableStateOf(initial?.current ?: "") }
    var frequency by remember { mutableStateOf(initial?.frequency ?: "") }
    var speed by remember { mutableStateOf(initial?.speed ?: "") }
    var pressure by remember { mutableStateOf(initial?.pressure ?: "") }
    var flowRate by remember { mutableStateOf(initial?.flowRate ?: "") }
    var temperatureRange by remember { mutableStateOf(initial?.temperatureRange ?: "") }
    var weight by remember { mutableStateOf(initial?.weight ?: "") }
    var dimensions by remember { mutableStateOf(initial?.dimensions ?: "") }
    var material by remember { mutableStateOf(initial?.material ?: "") }
    var designStandard by remember { mutableStateOf(initial?.designStandard ?: "") }
    var technicalSpecGroup by remember { mutableStateOf(initial?.technicalSpecGroup ?: "") }
    var requiresSerialTracking by remember { mutableStateOf(initial?.requiresSerialTracking ?: false) }

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

        Text("التصنيف", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("التصنيف القياسي", standardClass, { standardClass = it })
        LabeledField("فئة المعدّة", equipmentCategory, { equipmentCategory = it })
        LabeledField("صنف الأصل", assetClass, { assetClass = it })
        LabeledField("الصنف الفرعي", assetSubclass, { assetSubclass = it })
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

        Text("السلامة والامتثال", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("أصل حرج للسلامة", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = safetyCritical, onCheckedChange = { safetyCritical = it })
        }
        OptionDropdown("مستوى المخاطر", listOf("", "Low", "Medium", "High", "Critical"), riskLevel) { riskLevel = it }
        LabeledField("التصاريح المطلوبة", requiredPermits, { requiredPermits = it }, singleLine = false)
        LabeledField("تعليمات السلامة", safetyInstructions, { safetyInstructions = it }, singleLine = false)
        LabeledField("معدات الوقاية المطلوبة (PPE)", ppeRequired, { ppeRequired = it }, singleLine = false)
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("يتطلب عزل الطاقة", modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            Switch(checked = isolationRequired, onCheckedChange = { isolationRequired = it })
        }
        LabeledField("متطلبات الامتثال", complianceRequirements, { complianceRequirements = it }, singleLine = false)

        Text("المعلومات المالية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("رقم الأصل المالي", assetNumber, { assetNumber = it })
        LabeledField("المورّد", supplier, { supplier = it })
        LabeledField("أمر الشراء", purchaseOrder, { purchaseOrder = it })
        LabeledField("تكلفة الشراء", purchaseCost, { purchaseCost = it }, numeric = true)
        DateField("تاريخ الاقتناء", acquiredAt) { acquiredAt = it }
        OptionDropdown("الحالة المالية", listOf("", "Active", "Capitalized", "Depreciated", "Disposed", "Written Off"), financialStatus) { financialStatus = it }
        LabeledField("القيمة الدفترية", bookValue, { bookValue = it }, numeric = true)
        DateField("تاريخ الرسملة", capitalizationAt) { capitalizationAt = it }

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
        val buildAsset: (String) -> AssetEntity = { statusValue ->
                AssetEntity(
                    id = initial?.id ?: 0,
                    code = code.trim(),
                    name = name.trim(),
                    groupName = group.ifBlank { "General" },
                    location = location,
                    manufacturer = manufacturer,
                    model = model,
                    status = statusValue,
                    criticality = criticality,
                    installedAt = initial?.installedAt ?: today,
                    lastInspectionAt = initial?.lastInspectionAt ?: today,
                    locationId = locationId,
                    warrantyProvider = warrantyProvider.trim(),
                    parentAssetId = parentAssetId,
                    serialNumber = if (serialLocked) (initial?.serialNumber ?: "") else serialNumber.trim(),
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
                    linearEndLongitude = if (isLinearAsset) linearEndLongitude.toDoubleOrNull() else null,
                    longDescription = longDescription.trim(),
                    alternativeLabel = alternativeLabel.trim(),
                    externalAssetCode = externalAssetCode.trim(),
                    legacyAssetCode = legacyAssetCode.trim(),
                    barcode = barcode.trim(),
                    qrCode = qrCode.trim(),
                    equipmentCategory = equipmentCategory.trim(),
                    assetClass = assetClass.trim(),
                    assetSubclass = assetSubclass.trim(),
                    company = company.trim(),
                    site = site.trim(),
                    safetyCritical = safetyCritical,
                    riskLevel = riskLevel,
                    requiredPermits = requiredPermits.trim(),
                    safetyInstructions = safetyInstructions.trim(),
                    ppeRequired = ppeRequired.trim(),
                    isolationRequired = isolationRequired,
                    complianceRequirements = complianceRequirements.trim(),
                    financialStatus = financialStatus,
                    bookValue = bookValue.toDoubleOrNull() ?: 0.0,
                    capitalizationAt = capitalizationAt.trim(),
                    countryOfOrigin = countryOfOrigin.trim(),
                    nameplateData = nameplateData.trim(),
                    capacity = capacity.trim(),
                    power = power.trim(),
                    voltage = voltage.trim(),
                    current = current.trim(),
                    frequency = frequency.trim(),
                    speed = speed.trim(),
                    pressure = pressure.trim(),
                    flowRate = flowRate.trim(),
                    temperatureRange = temperatureRange.trim(),
                    weight = weight.trim(),
                    dimensions = dimensions.trim(),
                    material = material.trim(),
                    designStandard = designStandard.trim(),
                    technicalSpecGroup = technicalSpecGroup.trim(),
                    requiresSerialTracking = requiresSerialTracking,
                    warrantyType = warrantyType,
                    warrantyCategory = warrantyCategory.trim(),
                    warrantyTerms = warrantyTerms.trim(),
                    coveredServices = coveredServices.trim(),
                    excludedServices = excludedServices.trim(),
                    warrantyCounterType = warrantyCounterType,
                    warrantyCounterLimit = warrantyCounterLimit.toDoubleOrNull() ?: 0.0,
                    warrantyClaimRequired = warrantyClaimRequired,
                    warrantyClaimStatus = if (warrantyClaimRequired) warrantyClaimStatus else "",
                    warrantyContact = warrantyContact.trim(),
                    warrantyDocument = warrantyDocument.trim(),
                    vendorWarranty = vendorWarranty,
                    manufacturerWarranty = manufacturerWarranty,
                    customerWarranty = customerWarranty,
                    warrantyReference = warrantyReference.trim(),
                    // AST-WAR-003: preserve locked dates when the user cannot override them.
                    warrantyStart = if (warrantyDatesLocked) (initial?.warrantyStart ?: "") else warrantyStart.trim(),
                    warrantyEnd = if (warrantyDatesLocked) (initial?.warrantyEnd ?: "") else warrantyEnd.trim(),
                    mobility = mobility,
                    incursOperatingCost = incursOperatingCost,
                    orgOverrideReason = if (hasOrgOverride) orgOverrideReason.trim() else ""
                )
        }
        // Save Draft: allow persisting an incomplete asset as a draft (skips mandatory rules).
        OutlinedButton(
            onClick = { onSave(buildAsset("Draft")) },
            enabled = code.isNotBlank() && name.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) { Text("حفظ كمسودة") }
        // Activate / Save: only after mandatory data (technical + organizational rules) is complete.
        SaveButton(
            code.isNotBlank() && name.isNotBlank() && linearRangeValid && markerDistancesValid && coordinatesValid &&
                tech001Valid && tech002Valid && tech004Valid && orgValid
        ) {
            onSave(buildAsset(if (status.equals("Draft", ignoreCase = true)) "Active" else status))
        }
    }
}


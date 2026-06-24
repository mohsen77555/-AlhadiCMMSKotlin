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

    val warrantyDatesLocked = initial != null &&
        (initial.warrantyStart.isNotBlank() || initial.warrantyEnd.isNotBlank()) && !canOverrideSerial

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

        assetLinearSection(s, linearRangeValid, linearStartValue, linearEndValue, coordinatesValid)

        assetTechnicalSection(s, tech004Valid)

        assetNameplateSection(s, isCritical, tech001Valid)

        assetOrgSection(s, orgUnits, selectedLoc, isMobile, org001Company, org002Plant, org003Location, org004WorkCenter, org005Planner, org006CostCenter, org007FlActive, org008WcActive, org009PgActive, org010CcActive, org012OverrideReason, hasOrgOverride)

        assetIdentitySection(s, serialLocked, tech002Valid)

        assetSafetySection(s)

        assetFinancialSection(s)

        assetContactSection(s)

        assetWarrantySection(s, warrantyDatesLocked)
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


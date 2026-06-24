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

// ------------------------------------------------------------------------
// Self-contained AssetForm sections (no validation flags) — moved out of AssetForm.kt
// ------------------------------------------------------------------------

@Composable
internal fun assetClassificationSection(state: AssetFormState) {
    with(state) {
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
    }
}

@Composable
internal fun assetSafetySection(state: AssetFormState) {
    with(state) {
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
    }
}

@Composable
internal fun assetFinancialSection(state: AssetFormState) {
    with(state) {
        Text("المعلومات المالية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("رقم الأصل المالي", assetNumber, { assetNumber = it })
        LabeledField("المورّد", supplier, { supplier = it })
        LabeledField("أمر الشراء", purchaseOrder, { purchaseOrder = it })
        LabeledField("تكلفة الشراء", purchaseCost, { purchaseCost = it }, numeric = true)
        DateField("تاريخ الاقتناء", acquiredAt) { acquiredAt = it }
        OptionDropdown("الحالة المالية", listOf("", "Active", "Capitalized", "Depreciated", "Disposed", "Written Off"), financialStatus) { financialStatus = it }
        LabeledField("القيمة الدفترية", bookValue, { bookValue = it }, numeric = true)
        DateField("تاريخ الرسملة", capitalizationAt) { capitalizationAt = it }
    }
}

@Composable
internal fun assetContactSection(state: AssetFormState) {
    with(state) {
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
    }
}

@Composable
internal fun assetLinearSection(
    state: AssetFormState,
    linearRangeValid: Boolean,
    linearStartValue: Double?,
    linearEndValue: Double?,
    coordinatesValid: Boolean
) {
    with(state) {
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
    }
}

@Composable
internal fun assetTechnicalSection(
    state: AssetFormState,
    tech004Valid: Boolean
) {
    with(state) {
        Text("البيانات الفنية", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("الشركة المصنّعة", manufacturer, { manufacturer = it })
        LabeledField("الموديل", model, { model = it })
        LabeledField("سنة الصنع", constructionYear, { constructionYear = it }, numeric = true)
        LabeledField("شهر الصنع", constructionMonth, { constructionMonth = it }, numeric = true)
        DateField("تاريخ بدء التشغيل", startupDate) { startupDate = it }
        OptionDropdown("الحالة", listOf("Draft", "Active", "Running", "Warning", "Stopped", "Under Maintenance", "Standby", "Retired", "Disposed"), status) { status = it }
        OptionDropdown("الأهمية", listOf("Low", "Medium", "High", "Critical"), criticality) { criticality = it }
        if (!tech004Valid) Text("AST-TECH-004: الموديل مطلوب لوجود قطع غيار مرتبطة بالموديل.", color = MaterialTheme.colorScheme.error)
    }
}

@Composable
internal fun assetNameplateSection(
    state: AssetFormState,
    isCritical: Boolean,
    tech001Valid: Boolean
) {
    with(state) {
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
    }
}

@Composable
internal fun assetIdentitySection(
    state: AssetFormState,
    serialLocked: Boolean,
    tech002Valid: Boolean
) {
    with(state) {
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
    }
}

@Composable
internal fun assetWarrantySection(
    state: AssetFormState,
    warrantyDatesLocked: Boolean
) {
    with(state) {
        Text("الضمان", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
        LabeledField("جهة الضمان", warrantyProvider, { warrantyProvider = it })
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
    }
}

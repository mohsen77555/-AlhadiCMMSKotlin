package com.alhadi.cmms.ui

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AccountTree
import androidx.compose.material.icons.filled.Warehouse
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Analytics
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Backup
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Checklist
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material.icons.filled.FactCheck
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.PrecisionManufacturing
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.alhadi.cmms.data.MovementType
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.notify.Reminders
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.ui.theme.AccentBlue
import com.alhadi.cmms.ui.theme.AccentBrown
import com.alhadi.cmms.ui.theme.AccentGreen
import com.alhadi.cmms.ui.theme.AccentNavy
import com.alhadi.cmms.ui.theme.AccentOrange
import com.alhadi.cmms.ui.theme.AccentPurple
import com.alhadi.cmms.ui.theme.AccentRed
import com.alhadi.cmms.ui.theme.AccentTeal
import com.alhadi.cmms.ui.theme.StatusInfo
import com.alhadi.cmms.ui.theme.StatusRunning
import com.alhadi.cmms.ui.theme.StatusRunningContainer
import com.alhadi.cmms.ui.theme.StatusStopped
import com.alhadi.cmms.ui.theme.StatusStoppedContainer
import com.alhadi.cmms.ui.theme.priorityTone
import com.alhadi.cmms.ui.theme.statusTone
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.util.ImageStore
import com.alhadi.cmms.viewmodel.*
import com.alhadi.cmms.viewmodel.DashboardStats
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyListScope

/**
 * Read-only asset governance cards (identity codes, safety/compliance, technical
 * specs, costs, financial) — extracted from AssetDetailScreen as a LazyListScope
 * extension so the detail screen stays small.
 */

// ------------------------------------------------------------------------
// Org-contact & warranty cards (moved out of AssetGovernanceCards.kt)
// ------------------------------------------------------------------------

internal fun LazyListScope.assetOrgContactCards(asset: AssetEntity) {
    val hasOrganization = listOf(
        asset.company, asset.site, asset.maintenancePlant, asset.planningPlant,
        asset.plannerGroup, asset.mainWorkCenter, asset.productionWorkCenter,
        asset.costCenter, asset.responsiblePerson
    ).any { it.isNotBlank() }
    val hasPartner = listOf(asset.partnerName, asset.partnerRole, asset.partnerPhone, asset.partnerEmail).any { it.isNotBlank() }
    val hasAddress = listOf(asset.addressLine, asset.city, asset.country).any { it.isNotBlank() }
        if (hasOrganization) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("التنظيم والمسؤولية")
                        if (asset.company.isNotBlank()) InfoRow("الشركة", asset.company)
                        if (asset.site.isNotBlank()) InfoRow("الموقع/المنشأة", asset.site)
                        if (asset.maintenancePlant.isNotBlank()) InfoRow("مصنع الصيانة", asset.maintenancePlant)
                        if (asset.planningPlant.isNotBlank()) InfoRow("مصنع التخطيط", asset.planningPlant)
                        if (asset.plannerGroup.isNotBlank()) InfoRow("مجموعة المخططين", asset.plannerGroup)
                        if (asset.mainWorkCenter.isNotBlank()) InfoRow("مركز العمل الرئيسي", asset.mainWorkCenter)
                        if (asset.productionWorkCenter.isNotBlank()) InfoRow("مركز عمل الإنتاج", asset.productionWorkCenter)
                        if (asset.costCenter.isNotBlank()) InfoRow("مركز التكلفة", asset.costCenter)
                        if (asset.responsiblePerson.isNotBlank()) InfoRow("الشخص المسؤول", asset.responsiblePerson)
                        if (asset.orgOverrideReason.isNotBlank()) InfoRow("سبب تجاوز الموروث", asset.orgOverrideReason)
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
}

internal fun LazyListScope.assetWarrantyCard(asset: AssetEntity, allAssets: List<AssetEntity>, underWarranty: Boolean) {
    val hasWarranty = asset.warrantyEnd.isNotBlank() || asset.warrantyType.isNotBlank() ||
        asset.warrantyReference.isNotBlank() || asset.warrantyCounterType.isNotBlank() ||
        asset.warrantyProvider.isNotBlank()
        if (hasWarranty) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            SectionHeader("الضمان")
                            Spacer(modifier = Modifier.weight(1f))
                            StatusBadge(if (underWarranty) "ضمن الضمان" else "منتهٍ", statusTone(if (underWarranty) "running" else "stopped"))
                        }
                        if (asset.warrantyProvider.isNotBlank()) InfoRow("الجهة", asset.warrantyProvider)
                        if (asset.warrantyStart.isNotBlank()) InfoRow("من", asset.warrantyStart)
                        if (asset.warrantyEnd.isNotBlank()) InfoRow("إلى", asset.warrantyEnd)
                        if (asset.warrantyType.isNotBlank()) InfoRow("النوع", warrantyTypeLabelUi(asset.warrantyType))
                        if (asset.warrantyCategory.isNotBlank()) InfoRow("الفئة", asset.warrantyCategory)
                        if (asset.warrantyReference.isNotBlank()) InfoRow("مرجع العقد", asset.warrantyReference)
                        // AST-WAR-002: counter-based coverage.
                        if (asset.warrantyCounterType.isNotBlank()) {
                            InfoRow("عدّاد الضمان", "${warrantyCounterTypeLabelUi(asset.warrantyCounterType)} • حد ${formatLinearNumber(asset.warrantyCounterLimit)}")
                        }
                        val warrantyKinds = buildList {
                            if (asset.vendorWarranty) add("مورّد")
                            if (asset.manufacturerWarranty) add("مُصنّع")
                            if (asset.customerWarranty) add("عميل")
                        }
                        if (warrantyKinds.isNotEmpty()) InfoRow("نوع الجهة", warrantyKinds.joinToString("، "))
                        if (asset.coveredServices.isNotBlank()) InfoRow("مشمول", asset.coveredServices)
                        if (asset.excludedServices.isNotBlank()) InfoRow("مستثنى", asset.excludedServices)
                        if (asset.warrantyTerms.isNotBlank()) InfoRow("الشروط", asset.warrantyTerms)
                        if (asset.warrantyContact.isNotBlank()) InfoRow("جهة الاتصال", asset.warrantyContact)
                        // AST-WAR-006: warranty document linked to the asset card.
                        if (asset.warrantyDocument.isNotBlank()) InfoRow("مستند الضمان", asset.warrantyDocument)
                        if (asset.warrantyClaimRequired) InfoRow("حالة المطالبة", warrantyClaimStatusLabelUi(asset.warrantyClaimStatus))
                        // AST-WAR-007: assets sharing the same warranty reference.
                        if (asset.warrantyReference.isNotBlank()) {
                            val shared = allAssets.count { it.id != asset.id && it.warrantyReference.equals(asset.warrantyReference, ignoreCase = true) }
                            if (shared > 0) InfoRow("أصول أخرى بنفس الضمان", "$shared أصل")
                        }
                    }
                }
            }
        }
}

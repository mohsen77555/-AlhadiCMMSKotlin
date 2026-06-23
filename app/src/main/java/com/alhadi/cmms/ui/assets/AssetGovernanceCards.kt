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
import com.alhadi.cmms.viewmodel.CmmsViewModel
import com.alhadi.cmms.viewmodel.DashboardStats
import java.util.Locale
import kotlinx.coroutines.launch
import androidx.compose.foundation.lazy.LazyListScope

/**
 * Read-only asset governance cards (identity codes, safety/compliance, technical
 * specs, costs, financial) — extracted from AssetDetailScreen as a LazyListScope
 * extension so the detail screen stays small.
 */
internal fun LazyListScope.assetGovernanceCards(
    asset: AssetEntity,
    allAssets: List<AssetEntity>,
    workOrders: List<WorkOrderEntity>
) {
    val hasIdentityCodes = listOf(
        asset.alternativeLabel,
        asset.externalAssetCode,
        asset.legacyAssetCode,
        asset.barcode,
        asset.qrCode
    ).any { it.isNotBlank() }
    val hasSafety = asset.safetyCritical || asset.isolationRequired || listOf(
        asset.riskLevel,
        asset.requiredPermits,
        asset.safetyInstructions,
        asset.ppeRequired,
        asset.complianceRequirements
    ).any { it.isNotBlank() }
        if (hasIdentityCodes) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("الهوية والترميز")
                        if (asset.alternativeLabel.isNotBlank()) InfoRow("التسمية البديلة", asset.alternativeLabel)
                        if (asset.externalAssetCode.isNotBlank()) InfoRow("الكود الخارجي", asset.externalAssetCode)
                        if (asset.legacyAssetCode.isNotBlank()) InfoRow("الكود القديم", asset.legacyAssetCode)
                        if (asset.barcode.isNotBlank()) InfoRow("الباركود", asset.barcode)
                        if (asset.qrCode.isNotBlank()) InfoRow("رمز QR", asset.qrCode)
                    }
                }
            }
        }

        if (hasSafety) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("السلامة والامتثال")
                        InfoRow("أصل حرج للسلامة", if (asset.safetyCritical) "نعم" else "لا")
                        if (asset.riskLevel.isNotBlank()) InfoRow("مستوى المخاطر", asset.riskLevel)
                        InfoRow("يتطلب عزل الطاقة", if (asset.isolationRequired) "نعم" else "لا")
                        if (asset.requiredPermits.isNotBlank()) InfoRow("التصاريح المطلوبة", asset.requiredPermits)
                        if (asset.ppeRequired.isNotBlank()) InfoRow("معدات الوقاية (PPE)", asset.ppeRequired)
                        if (asset.safetyInstructions.isNotBlank()) InfoRow("تعليمات السلامة", asset.safetyInstructions)
                        if (asset.complianceRequirements.isNotBlank()) InfoRow("متطلبات الامتثال", asset.complianceRequirements)
                    }
                }
            }
        }

        val technicalSpecs = listOf(
            "بلد المنشأ" to asset.countryOfOrigin,
            "نوع الإنشاء" to asset.constructionType,
            "مجموعة المواصفات" to asset.technicalSpecGroup,
            "السعة" to asset.capacity,
            "القدرة" to asset.power,
            "الجهد" to asset.voltage,
            "التيار" to asset.current,
            "التردد" to asset.frequency,
            "السرعة" to asset.speed,
            "الضغط" to asset.pressure,
            "معدل التدفق" to asset.flowRate,
            "نطاق الحرارة" to asset.temperatureRange,
            "الوزن" to asset.weight,
            "الأبعاد" to asset.dimensions,
            "المادة" to asset.material,
            "معيار التصميم" to asset.designStandard
        ).filter { it.second.isNotBlank() }
        val sameConstruction = if (asset.constructionType.isNotBlank()) {
            allAssets.count { it.id != asset.id && it.constructionType.equals(asset.constructionType, ignoreCase = true) }
        } else 0
        if (technicalSpecs.isNotEmpty() || asset.nameplateData.isNotBlank() || asset.requiresSerialTracking) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("المواصفات الفنية (لوحة الصنع)")
                        if (asset.requiresSerialTracking) InfoRow("تتبع فردي", "مطلوب رقم تسلسلي")
                        technicalSpecs.forEach { (label, value) -> InfoRow(label, value) }
                        if (asset.nameplateData.isNotBlank()) InfoRow("بيانات لوحة الصنع", asset.nameplateData)
                        // AST-TECH-005: construction type links assets that share components/characteristics.
                        if (sameConstruction > 0) InfoRow("أصول بنفس نوع الإنشاء", "$sameConstruction أصل")
                    }
                }
            }
        }

        item {
            val laborTotal = workOrders.sumOf { it.laborCost() }
            val partsTotal = workOrders.sumOf { it.partsCost }
            val grandTotal = workOrders.sumOf { it.totalCost() }
            val closedCount = workOrders.count { it.status == "Closed" }
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        SectionHeader("التكاليف")
                        Spacer(modifier = Modifier.weight(1f))
                        StatusBadge(money(grandTotal), statusTone("info"))
                    }
                    InfoRow("إجمالي التكلفة", money(grandTotal))
                    InfoRow("تكلفة العمالة", money(laborTotal))
                    InfoRow("تكلفة قطع الغيار", money(partsTotal))
                    InfoRow("أوامر العمل", "${workOrders.size} (مغلقة: $closedCount)")
                }
            }
        }

        val hasFinancial = asset.supplier.isNotBlank() || asset.purchaseOrder.isNotBlank() ||
            asset.purchaseCost > 0.0 || asset.acquiredAt.isNotBlank() ||
            asset.financialStatus.isNotBlank() || asset.bookValue > 0.0 || asset.capitalizationAt.isNotBlank()
        if (hasFinancial) {
            item {
                ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        SectionHeader("المعلومات المالية")
                        if (asset.supplier.isNotBlank()) InfoRow("المورّد", asset.supplier)
                        if (asset.purchaseOrder.isNotBlank()) InfoRow("أمر الشراء", asset.purchaseOrder)
                        if (asset.purchaseCost > 0.0) InfoRow("تكلفة الشراء", money(asset.purchaseCost))
                        if (asset.acquiredAt.isNotBlank()) InfoRow("تاريخ الاقتناء", asset.acquiredAt)
                        if (asset.financialStatus.isNotBlank()) InfoRow("الحالة المالية", asset.financialStatus)
                        if (asset.bookValue > 0.0) InfoRow("القيمة الدفترية", money(asset.bookValue))
                        if (asset.capitalizationAt.isNotBlank()) InfoRow("تاريخ الرسملة", asset.capitalizationAt)
                    }
                }
            }
        }
}

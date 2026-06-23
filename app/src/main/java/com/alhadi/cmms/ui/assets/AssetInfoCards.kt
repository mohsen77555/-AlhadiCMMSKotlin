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

/** Linear asset data card (range, markers, offsets, coordinates, network links). */
internal fun LazyListScope.assetLinearCard(asset: AssetEntity) {
    val hasLinearData = asset.isLinearAsset
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
        }
}

/** QR-code card for the asset (scannable ALHADI:<code>). */
internal fun LazyListScope.assetQrCard(asset: AssetEntity) {
        item {
            val qr = rememberQrBitmap("ALHADI:${asset.code}")
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SectionHeader("رمز QR للأصل")
                    if (qr != null) {
                        Image(
                            bitmap = qr,
                            contentDescription = "QR ${asset.code}",
                            modifier = Modifier
                                .size(180.dp)
                                .background(Color.White, RoundedCornerShape(8.dp))
                                .padding(8.dp)
                        )
                    }
                    LtrText("ALHADI:${asset.code}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("امسح الرمز للوصول إلى بطاقة الأصل.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
}

/** Asset basic-information card (category, location, parent, model, dates...). */
internal fun LazyListScope.assetBasicInfoCard(
    asset: AssetEntity,
    parent: AssetEntity?,
    locationLabel: String,
    constructionDate: String
) {
        item {
            ElevatedCard(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    SectionHeader("المعلومات الأساسية")
                    if (asset.description.isNotBlank()) InfoRow("الوصف", asset.description)
                    InfoRow("فئة الأصل", assetCategoryLabel(asset.category))
                    if (asset.objectType.isNotBlank()) InfoRow("نوع الأصل", asset.objectType)
                    if (asset.constructionType.isNotBlank()) InfoRow("نوع الإنشاء", asset.constructionType)
                    InfoRow("المجموعة", asset.groupName)
                    InfoRow("الموقع", asset.location.ifBlank { "غير محدد" })
                    InfoRow("الموقع الفني", locationLabel)
                    InfoRow("الأصل الأب", parent?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                    InfoRow("الشركة/الموديل", listOf(asset.manufacturer, asset.model).filter { it.isNotBlank() }.joinToString(" • ").ifBlank { "غير محدد" })
                    if (asset.serialNumber.isNotBlank()) InfoRow("الرقم التسلسلي", asset.serialNumber)
                    if (asset.assetTag.isNotBlank()) InfoRow("وسم الأصل", asset.assetTag)
                    if (asset.assetNumber.isNotBlank()) InfoRow("رقم الأصل المالي", asset.assetNumber)
                    InfoRow("الأهمية", asset.criticality)
                    InfoRow("نوع التركيب", if (asset.mobility.equals("Mobile", ignoreCase = true)) "متنقّل" else "ثابت / مركّب")
                    if (constructionDate.isNotBlank()) InfoRow("سنة / شهر الصنع", constructionDate)
                    InfoRow("تاريخ التركيب", asset.installedAt)
                    if (asset.startupDate.isNotBlank()) InfoRow("تاريخ بدء التشغيل", asset.startupDate)
                    InfoRow("آخر فحص", asset.lastInspectionAt)
                }
            }
        }
}

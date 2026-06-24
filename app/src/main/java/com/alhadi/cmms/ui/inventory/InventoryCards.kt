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

// ---------------------------------------------------------------------------
// Inventory
// ---------------------------------------------------------------------------

// ------------------------------------------------------------------------
// Inventory cards & dialogs (moved out of InventoryScreen.kt)
// ------------------------------------------------------------------------

@Composable
internal fun SparePartCard(
    part: SparePartEntity,
    profile: SerialNumberProfileEntity?,
    serials: List<SerialNumberEntity>,
    warehouseLabel: String,
    canReceive: Boolean,
    canManage: Boolean,
    onIssue: (SparePartEntity, Int) -> Unit,
    onReceive: (SparePartEntity, Int) -> Unit,
    onEdit: () -> Unit,
    onOpenSerialNumbers: () -> Unit,
    onDelete: () -> Unit
) {
    val lowStock = part.onHandQty <= part.minQty
    val serialInStock = serials.count { it.status == "InStock" }
    var moveMode by remember { mutableStateOf<String?>(null) } // "issue" | "receive"
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(part.partNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(part.name, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(if (lowStock) "منخفض" else "متوفر", statusTone(if (lowStock) "stopped" else "running"))
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            InfoRow("المعدة", part.equipmentGroup)
            InfoRow("الكمية", "${part.onHandQty} ${part.unit}")
            InfoRow("الحد الأدنى", "${part.minQty} ${part.unit}")
            InfoRow("المستودع", warehouseLabel.ifBlank { "غير محدد" })
            InfoRow("آخر سعر", "%.2f".format(part.lastPrice))
            InfoRow("قيمة المخزون", money(part.onHandQty * part.lastPrice))
            if (part.serializationActive) {
                InfoRow("ملف التتبع", profile?.let { "${it.code} • ${it.name}" } ?: "غير محدد")
                InfoRow("الوحدات المتسلسلة في المخزون", serialInStock.toString())
                if (serialInStock != part.onHandQty) {
                    StatusBadge("اختلاف بين الكمية والوحدات", statusTone("stopped"))
                }
                Button(onClick = onOpenSerialNumbers, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.QrCodeScanner, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("إدارة الأرقام التسلسلية")
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(onClick = { moveMode = "issue" }, enabled = part.onHandQty > 0, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Filled.Bolt, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("صرف")
                    }
                    if (canReceive) {
                        Button(onClick = { moveMode = "receive" }, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("استلام")
                        }
                    }
                }
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }

    if (!part.serializationActive) moveMode?.let { mode ->
        val isIssue = mode == "issue"
        QuantityDialog(
            title = if (isIssue) "صرف ${part.partNumber}" else "استلام ${part.partNumber}",
            label = if (isIssue) "الكمية المصروفة (المتوفر ${part.onHandQty})" else "الكمية المستلمة",
            maxValue = if (isIssue) part.onHandQty else null,
            onConfirm = { qty ->
                if (isIssue) onIssue(part, qty) else onReceive(part, qty)
                moveMode = null
            },
            onDismiss = { moveMode = null }
        )
    }
}

@Composable
internal fun QuantityDialog(
    title: String,
    label: String,
    maxValue: Int?,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit
) {
    var text by remember { mutableStateOf("1") }
    val qty = text.toIntOrNull()
    val valid = qty != null && qty > 0 && (maxValue == null || qty <= maxValue)
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                OutlinedTextField(
                    value = text,
                    onValueChange = { text = it.filter { c -> c.isDigit() } },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = { TextButton(enabled = valid, onClick = { onConfirm(qty!!) }) { Text("تأكيد") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
internal fun TransactionCard(transaction: InventoryTransactionEntity, partNumber: String?) {
    val isIssue = transaction.transactionType == "Issue"
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBubble(
                icon = if (isIssue) Icons.Filled.Bolt else Icons.Filled.Add,
                tint = if (isIssue) StatusStopped else StatusRunning,
                container = if (isIssue) StatusStoppedContainer else StatusRunningContainer,
                size = 40
            )
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = if (isIssue) "صرف ${transaction.quantity}" else "استلام ${transaction.quantity}",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall
                )
                LtrText(partNumber ?: "Part #${transaction.partId}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("${transaction.createdAt} • ${transaction.createdBy}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (transaction.serialNumbers.isNotBlank()) {
                    Text("الأرقام: ${transaction.serialNumbers}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, maxLines = 2, overflow = TextOverflow.Ellipsis)
                }
            }
            if (transaction.workOrderId != null) {
                StatusBadge("أمر #${transaction.workOrderId}", statusTone("info"))
            }
        }
    }
}

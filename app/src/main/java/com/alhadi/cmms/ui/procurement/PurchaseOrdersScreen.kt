package com.alhadi.cmms.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.ui.theme.AccentBrown
import com.alhadi.cmms.ui.theme.statusTone

@Composable
internal fun PurchaseOrdersScreen(
    innerPadding: PaddingValues,
    orders: List<PurchaseOrderEntity>,
    lines: List<PurchaseOrderLineEntity>,
    suppliers: List<SupplierEntity>,
    parts: List<SparePartEntity>,
    canManage: Boolean,
    onSaveOrder: (PurchaseOrderEntity) -> Unit,
    onCancelOrder: (PurchaseOrderEntity, String) -> Unit,
    onSetStatus: (PurchaseOrderEntity, String) -> Unit,
    onSaveLine: (PurchaseOrderLineEntity) -> Unit,
    onDeleteLine: (PurchaseOrderLineEntity) -> Unit,
    onReceiveLine: (PurchaseOrderLineEntity, Int) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<PurchaseOrderEntity?>(null) }
    var lineFormFor by remember { mutableStateOf<Long?>(null) }
    var cancelTarget by remember { mutableStateOf<PurchaseOrderEntity?>(null) }
    var selectedStatus by remember { mutableStateOf("All") }

    val filtered = orders.filter { selectedStatus == "All" || it.status == selectedStatus }
    val filters = listOf("All") + purchaseOrderStatusOrder

    Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            SectionHeader("أوامر الشراء")
            Text("أنشئ أوامر شراء للموردين، أضِف البنود، واعتمدها ثم تابع استلامها.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()).padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            filters.forEach { f ->
                val count = if (f == "All") orders.size else orders.count { it.status == f }
                FilterChip(
                    selected = selectedStatus == f,
                    onClick = { selectedStatus = f },
                    label = { Text(if (f == "All") "الكل (${orders.size})" else "${purchaseOrderStatusLabel(f)}${if (count > 0) " ($count)" else ""}") }
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (canManage) {
                item { AddButton("أمر شراء جديد") { editing = null; showForm = true } }
            }
            if (filtered.isEmpty()) {
                item { EmptyState("لا توجد أوامر شراء", Icons.Filled.ShoppingCart) }
            }
            items(filtered, key = { it.id }) { order ->
                PurchaseOrderCard(
                    order = order,
                    lines = lines.filter { it.poId == order.id },
                    canManage = canManage,
                    onAddLine = { lineFormFor = order.id },
                    onDeleteLine = onDeleteLine,
                    onReceiveLine = onReceiveLine,
                    onSetStatus = onSetStatus,
                    onEdit = { editing = order; showForm = true },
                    onCancel = { cancelTarget = order }
                )
            }
        }
    }

    if (showForm) {
        PurchaseOrderFormSheet(
            initial = editing,
            suppliers = suppliers,
            onDismiss = { showForm = false },
            onSave = { onSaveOrder(it); showForm = false }
        )
    }
    lineFormFor?.let { poId ->
        PurchaseOrderLineFormSheet(
            poId = poId,
            parts = parts,
            onDismiss = { lineFormFor = null },
            onSave = { onSaveLine(it); lineFormFor = null }
        )
    }
    cancelTarget?.let { target ->
        var reason by remember(target.id) { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { cancelTarget = null },
            title = { Text("إلغاء أمر الشراء") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("لا يُحذف أمر الشراء — يُلغى ويبقى في السجل. اكتب سبب الإلغاء:")
                    OutlinedTextField(value = reason, onValueChange = { reason = it }, label = { Text("السبب") }, modifier = Modifier.fillMaxWidth())
                }
            },
            confirmButton = { TextButton(enabled = reason.isNotBlank(), onClick = { onCancelOrder(target, reason.trim()); cancelTarget = null }) { Text("إلغاء الأمر") } },
            dismissButton = { TextButton(onClick = { cancelTarget = null }) { Text("تراجع") } }
        )
    }
}

@Composable
internal fun PurchaseOrderCard(
    order: PurchaseOrderEntity,
    lines: List<PurchaseOrderLineEntity>,
    canManage: Boolean,
    onAddLine: () -> Unit,
    onDeleteLine: (PurchaseOrderLineEntity) -> Unit,
    onReceiveLine: (PurchaseOrderLineEntity, Int) -> Unit,
    onSetStatus: (PurchaseOrderEntity, String) -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit
) {
    val terminal = order.status == "Closed" || order.status == "Cancelled"
    val receiving = order.status == "Ordered" || order.status == "PartiallyReceived"
    var receiveTarget by remember { mutableStateOf<PurchaseOrderLineEntity?>(null) }
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(order.poNumber, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(order.supplierName.ifBlank { "مورّد #${order.supplierId}" }, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(purchaseOrderStatusLabel(order.status), statusTone(purchaseOrderStatusTone(order.status)))
            }
            if (order.expectedDate.isNotBlank()) InfoRow("التسليم المتوقع", order.expectedDate)
            InfoRow("الإجمالي", "%.2f %s".format(order.totalAmount, order.currency))
            if (order.cancelledReason.isNotBlank()) InfoRow("سبب الإلغاء", order.cancelledReason)

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
            Text("البنود (${lines.size})", fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
            lines.forEach { line ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                    Text("×${line.quantity}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium, color = AccentBrown)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(line.description, style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Medium)
                        Text("%.2f × %d = %.2f".format(line.unitPrice, line.quantity, line.lineTotal), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        if (line.receivedQty > 0 || receiving) {
                            val tone = if (line.isFullyReceived) statusTone("running").content else statusTone("info").content
                            Text("مستلم ${line.receivedQty}/${line.quantity}", style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Medium, color = tone)
                        }
                    }
                    if (canManage && order.status == "Draft") {
                        TextButton(onClick = { onDeleteLine(line) }) { Text("حذف", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.labelSmall) }
                    }
                    if (canManage && receiving && !line.isFullyReceived) {
                        TextButton(onClick = { receiveTarget = line }) { Text("استلام", color = AccentBrown, style = MaterialTheme.typography.labelSmall) }
                    }
                }
            }
            if (canManage && (order.status == "Draft" || order.status == "Approved")) {
                OutlinedButton(onClick = onAddLine, modifier = Modifier.fillMaxWidth()) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Text(" إضافة بند")
                }
            }

            if (canManage && !terminal) {
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    when (order.status) {
                        "Draft" -> Button(onClick = { onSetStatus(order, "Approved") }, enabled = lines.isNotEmpty(), modifier = Modifier.weight(1f)) { Text("اعتماد") }
                        "Approved" -> Button(onClick = { onSetStatus(order, "Ordered") }, modifier = Modifier.weight(1f)) { Text("إرسال للمورّد") }
                        "Ordered", "PartiallyReceived" -> Button(
                            onClick = { lines.filter { !it.isFullyReceived }.forEach { onReceiveLine(it, it.quantity - it.receivedQty) } },
                            enabled = lines.any { !it.isFullyReceived },
                            modifier = Modifier.weight(1f)
                        ) { Text("استلام الكل") }
                        "Received" -> Button(onClick = { onSetStatus(order, "Closed") }, modifier = Modifier.weight(1f)) { Text("إغلاق") }
                    }
                    OutlinedButton(onClick = onCancel, modifier = Modifier.weight(1f)) { Text("إلغاء") }
                }
                if (order.status == "Draft") {
                    OutlinedButton(onClick = onEdit, modifier = Modifier.fillMaxWidth()) { Text("تعديل الرأس") }
                }
            }
        }
    }

    receiveTarget?.let { line ->
        val remaining = line.quantity - line.receivedQty
        var qty by remember(line.id) { mutableStateOf(remaining.toString()) }
        val entered = qty.toIntOrNull() ?: 0
        AlertDialog(
            onDismissRequest = { receiveTarget = null },
            title = { Text("استلام بند") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(line.description, fontWeight = FontWeight.Medium)
                    Text("المتبقّي للاستلام: $remaining", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    OutlinedTextField(value = qty, onValueChange = { qty = it.filter { c -> c.isDigit() } }, label = { Text("الكمية المستلمة") }, modifier = Modifier.fillMaxWidth())
                    if (line.partId != null) {
                        Text("سيُحدَّث رصيد المخزون تلقائياً عند الاستلام.", style = MaterialTheme.typography.labelSmall, color = AccentBrown)
                    }
                }
            },
            confirmButton = {
                TextButton(enabled = entered in 1..remaining, onClick = { onReceiveLine(line, entered); receiveTarget = null }) { Text("تأكيد الاستلام") }
            },
            dismissButton = { TextButton(onClick = { receiveTarget = null }) { Text("إلغاء") } }
        )
    }
}

internal fun purchaseOrderStatusTone(status: String): String = when (status) {
    "Draft" -> "neutral"
    "Approved", "Ordered" -> "scheduled"
    "PartiallyReceived" -> "info"
    "Received", "Closed" -> "running"
    "Cancelled" -> "stopped"
    else -> "info"
}

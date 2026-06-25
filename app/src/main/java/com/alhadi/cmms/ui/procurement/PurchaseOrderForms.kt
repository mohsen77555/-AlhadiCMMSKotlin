package com.alhadi.cmms.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.util.DateStrings

internal val purchaseOrderStatusOrder = listOf("Draft", "Approved", "Ordered", "PartiallyReceived", "Received", "Closed", "Cancelled")

internal fun purchaseOrderStatusLabel(status: String): String = when (status) {
    "Draft" -> "مسودة"
    "Approved" -> "معتمد"
    "Ordered" -> "تم الطلب"
    "PartiallyReceived" -> "استلام جزئي"
    "Received" -> "مستلم"
    "Closed" -> "مغلق"
    "Cancelled" -> "ملغى"
    else -> status
}

@Composable
internal fun PurchaseOrderFormSheet(
    initial: PurchaseOrderEntity?,
    suppliers: List<SupplierEntity>,
    onDismiss: () -> Unit,
    onSave: (PurchaseOrderEntity) -> Unit
) {
    val activeSuppliers = suppliers.filter { it.isActive || it.id == initial?.supplierId }
    var poNumber by remember { mutableStateOf(initial?.poNumber ?: "PO-${DateStrings.today().replace("-", "")}") }
    var supplierId by remember { mutableStateOf(initial?.supplierId ?: activeSuppliers.firstOrNull()?.id ?: 0L) }
    var orderDate by remember { mutableStateOf(initial?.orderDate ?: DateStrings.today()) }
    var expectedDate by remember { mutableStateOf(initial?.expectedDate ?: DateStrings.daysFromToday(7)) }
    var currency by remember { mutableStateOf(initial?.currency ?: "SAR") }
    var warehouse by remember { mutableStateOf(initial?.warehouse ?: "") }
    var notes by remember { mutableStateOf(initial?.notes ?: "") }

    val supplierOptions = activeSuppliers.map { it.id.toString() }

    FormSheet(if (initial == null) "إضافة أمر شراء" else "تعديل أمر الشراء", onDismiss) {
        LabeledField("رقم أمر الشراء", poNumber, { poNumber = it })
        if (supplierOptions.isEmpty()) {
            Text("أضِف مورّداً نشطاً أولاً من شاشة الموردين.", color = MaterialTheme.colorScheme.error)
        } else {
            OptionDropdown(
                label = "المورّد",
                options = supplierOptions,
                selected = supplierId.toString(),
                display = { idStr -> activeSuppliers.firstOrNull { it.id.toString() == idStr }?.let { "${it.code} • ${it.name}" } ?: idStr }
            ) { supplierId = it.toLongOrNull() ?: 0L }
        }
        DateField("تاريخ الأمر", orderDate) { orderDate = it }
        DateField("تاريخ التسليم المتوقع", expectedDate) { expectedDate = it }
        LabeledField("العملة", currency, { currency = it })
        LabeledField("مستودع الاستلام", warehouse, { warehouse = it })
        LabeledField("ملاحظات", notes, { notes = it }, singleLine = false)
        SaveButton(poNumber.isNotBlank() && supplierId != 0L) {
            onSave(
                PurchaseOrderEntity(
                    id = initial?.id ?: 0,
                    poNumber = poNumber.trim(),
                    supplierId = supplierId,
                    supplierName = initial?.supplierName ?: "",
                    status = initial?.status ?: "Draft",
                    orderDate = orderDate.trim(),
                    expectedDate = expectedDate.trim(),
                    currency = currency.trim().ifBlank { "SAR" },
                    totalAmount = initial?.totalAmount ?: 0.0,
                    warehouse = warehouse.trim(),
                    notes = notes.trim(),
                    createdBy = initial?.createdBy ?: "",
                    approvedBy = initial?.approvedBy ?: "",
                    cancelledReason = initial?.cancelledReason ?: ""
                )
            )
        }
    }
}

@Composable
internal fun PurchaseOrderLineFormSheet(
    poId: Long,
    parts: List<SparePartEntity>,
    onDismiss: () -> Unit,
    onSave: (PurchaseOrderLineEntity) -> Unit
) {
    var partId by remember { mutableStateOf<Long?>(null) }
    var partNumber by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("1") }
    var unitPrice by remember { mutableStateOf("0") }

    val partOptions = listOf("") + parts.map { it.id.toString() }

    FormSheet("إضافة بند شراء", onDismiss) {
        if (parts.isNotEmpty()) {
            OptionDropdown(
                label = "قطعة من المخزون (اختياري)",
                options = partOptions,
                selected = partId?.toString() ?: "",
                display = { idStr ->
                    if (idStr.isBlank()) "بند يدوي" else parts.firstOrNull { it.id.toString() == idStr }?.let { "${it.partNumber} • ${it.name}" } ?: idStr
                }
            ) { idStr ->
                partId = idStr.toLongOrNull()
                parts.firstOrNull { it.id.toString() == idStr }?.let { p ->
                    partNumber = p.partNumber
                    description = p.name
                    unitPrice = p.lastPrice.toString()
                }
            }
        }
        LabeledField("رقم القطعة", partNumber, { partNumber = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        LabeledField("الكمية", quantity, { quantity = it }, numeric = true)
        LabeledField("سعر الوحدة", unitPrice, { unitPrice = it }, numeric = true)
        SaveButton(description.isNotBlank() && (quantity.toIntOrNull() ?: 0) > 0) {
            onSave(
                PurchaseOrderLineEntity(
                    id = 0,
                    poId = poId,
                    partId = partId,
                    partNumber = partNumber.trim(),
                    description = description.trim(),
                    quantity = quantity.toIntOrNull() ?: 1,
                    unitPrice = unitPrice.toDoubleOrNull() ?: 0.0,
                    receivedQty = 0
                )
            )
        }
    }
}

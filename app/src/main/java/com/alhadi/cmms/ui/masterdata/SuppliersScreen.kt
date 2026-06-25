package com.alhadi.cmms.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.ui.theme.AccentBrown
import com.alhadi.cmms.ui.theme.statusTone

internal val supplierCategoryOrder = listOf("All", "Parts", "Services", "Both")

internal fun supplierCategoryLabel(category: String): String = when (category) {
    "All" -> "الكل"
    "Parts" -> "قطع غيار"
    "Services" -> "خدمات"
    "Both" -> "قطع وخدمات"
    else -> category
}

internal fun supplierStatusLabel(status: String): String = when (status) {
    "Active" -> "نشط"
    "Inactive" -> "متوقف"
    "Blacklisted" -> "محظور"
    else -> status
}

@Composable
internal fun SuppliersScreen(
    innerPadding: PaddingValues,
    suppliers: List<SupplierEntity>,
    canManage: Boolean,
    onSave: (SupplierEntity) -> Unit,
    onDelete: (SupplierEntity) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }
    var editing by remember { mutableStateOf<SupplierEntity?>(null) }
    var deleteTarget by remember { mutableStateOf<SupplierEntity?>(null) }
    var selectedCategory by rememberSaveable { mutableStateOf("All") }

    val filtered = suppliers.filter { selectedCategory == "All" || it.category == selectedCategory }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding)
    ) {
        Column(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            SectionHeader("الموردون")
            Text("سجّل الموردين وبيانات التواصل والتصنيف لاستخدامهم في أوامر الشراء.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            supplierCategoryOrder.forEach { category ->
                val count = if (category == "All") suppliers.size else suppliers.count { it.category == category }
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { selectedCategory = category },
                    label = { Text(if (count > 0) "${supplierCategoryLabel(category)} ($count)" else supplierCategoryLabel(category)) }
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (canManage) {
                item { AddButton("مورّد جديد") { editing = null; showForm = true } }
            }
            if (filtered.isEmpty()) {
                item { EmptyState("لا يوجد موردون", Icons.Filled.LocalShipping) }
            }
            items(filtered, key = { it.id }) { supplier ->
                SupplierCard(
                    supplier = supplier,
                    canManage = canManage,
                    onEdit = { editing = supplier; showForm = true },
                    onDelete = { deleteTarget = supplier }
                )
            }
        }
    }

    if (showForm) {
        SupplierFormSheet(
            initial = editing,
            existing = suppliers,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
    deleteTarget?.let { target ->
        ConfirmDialog(
            title = "حذف المورّد",
            text = "هل تريد حذف ${target.code} - ${target.name}؟",
            onConfirm = { onDelete(target); deleteTarget = null },
            onDismiss = { deleteTarget = null }
        )
    }
}

@Composable
internal fun SupplierCard(
    supplier: SupplierEntity,
    canManage: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                IconBubble(Icons.Filled.LocalShipping, AccentBrown, AccentBrown.copy(alpha = 0.14f), 38)
                Column(modifier = Modifier.weight(1f)) {
                    LtrText(supplier.code, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(supplier.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                StatusBadge(supplierStatusLabel(supplier.status), statusTone(if (supplier.isActive) "active" else "neutral"))
            }
            if (supplier.contactPerson.isNotBlank()) InfoRow("جهة الاتصال", supplier.contactPerson)
            if (supplier.phone.isNotBlank()) InfoRow("الهاتف", supplier.phone)
            if (supplier.email.isNotBlank()) InfoRow("البريد", supplier.email)
            if (supplier.paymentTerms.isNotBlank()) InfoRow("شروط الدفع", supplier.paymentTerms)
            if (supplier.notes.isNotBlank()) InfoRow("ملاحظات", supplier.notes)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusBadge(supplierCategoryLabel(supplier.category), statusTone("info"))
                if (supplier.rating > 0) StatusBadge("التقييم: ${supplier.rating}/5", statusTone("neutral"))
            }
            if (canManage) EditDeleteRow(onEdit, onDelete)
        }
    }
}

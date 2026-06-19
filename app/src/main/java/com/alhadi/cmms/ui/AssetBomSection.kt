package com.alhadi.cmms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.SparePartEntity

@Composable
internal fun AssetBomSection(
    asset: AssetEntity,
    allAssets: List<AssetEntity>,
    headers: List<AssetBomHeaderEntity>,
    items: List<AssetBomItemEntity>,
    parts: List<SparePartEntity>,
    canManage: Boolean,
    onSaveHeader: (AssetBomHeaderEntity) -> Unit,
    onDeleteHeader: (AssetBomHeaderEntity) -> Unit,
    onSaveItem: (AssetBomItemEntity) -> Unit,
    onDeleteItem: (AssetBomItemEntity) -> Unit
) {
    val resolvedHeaders = remember(asset, headers) { resolveAssetBomHeaders(asset, headers) }
    val partMap = remember(parts) { parts.associateBy { it.id } }
    val assetMap = remember(allAssets) { allAssets.associateBy { it.id } }

    var showHeaderForm by remember(asset.id) { mutableStateOf(false) }
    var editingHeader by remember(asset.id) { mutableStateOf<AssetBomHeaderEntity?>(null) }
    var deletingHeader by remember(asset.id) { mutableStateOf<AssetBomHeaderEntity?>(null) }
    var itemHeader by remember(asset.id) { mutableStateOf<AssetBomHeaderEntity?>(null) }
    var editingItem by remember(asset.id) { mutableStateOf<AssetBomItemEntity?>(null) }
    var deletingItem by remember(asset.id) { mutableStateOf<AssetBomItemEntity?>(null) }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text("قوائم المكوّنات", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(
                    "هيكل التجميعات وقطع الغيار المستخدمة في تخطيط أعمال الصيانة.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (canManage) {
                OutlinedButton(onClick = { editingHeader = null; showHeaderForm = true }) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("قائمة")
                }
            }
        }

        if (resolvedHeaders.isEmpty()) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)
            ) {
                Text("لا توجد قائمة مكوّنات مرتبطة بهذا الأصل.", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }

        resolvedHeaders.forEach { header ->
            val headerItems = items.filter { it.headerId == header.id }.sortedWith(compareBy({ it.itemNumber }, { it.id }))
            ElevatedCard(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Filled.Inventory2, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(header.code, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                            Text(header.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            if (header.description.isNotBlank()) {
                                Text(header.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        BomTag(bomStatusLabel(header.status), if (header.status == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        BomTag(bomCategoryLabel(header.category), MaterialTheme.colorScheme.secondary)
                        BomTag(bomUsageLabel(header.usage), MaterialTheme.colorScheme.primary)
                        BomTag(bomAssignmentLabel(header.assignmentType), MaterialTheme.colorScheme.tertiary)
                        BomTag("بديل ${header.alternative}", MaterialTheme.colorScheme.secondary)
                        if (header.revision.isNotBlank()) BomTag("مراجعة ${header.revision}", MaterialTheme.colorScheme.tertiary)
                    }

                    if (header.assignmentType == "Indirect" && header.constructionType.isNotBlank()) {
                        Text("نوع الإنشاء المشترك: ${header.constructionType}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    if (header.validFrom.isNotBlank() || header.validTo.isNotBlank()) {
                        Text("الصلاحية: ${header.validFrom.ifBlank { "غير محدد" }} — ${header.validTo.ifBlank { "مفتوحة" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }

                    if (canManage) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                            OutlinedButton(
                                onClick = { itemHeader = header; editingItem = null },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("بند")
                            }
                            IconButton(onClick = { editingHeader = header; showHeaderForm = true }) {
                                Icon(Icons.Filled.Edit, contentDescription = "تعديل")
                            }
                            IconButton(onClick = { deletingHeader = header }) {
                                Icon(Icons.Filled.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f))
                    if (headerItems.isEmpty()) {
                        Text("لا توجد بنود في هذه القائمة.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    headerItems.forEach { item ->
                        val depth = bomItemDepth(item, headerItems)
                        Surface(
                            modifier = Modifier.fillMaxWidth().padding(start = (depth * 14).dp),
                            shape = RoundedCornerShape(12.dp),
                            color = if (item.status == "Active") MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.20f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                                    Text("${item.itemNumber}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        bomItemObjectLabel(item, partMap, assetMap),
                                        modifier = Modifier.weight(1f),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text("×${item.quantity}", fontWeight = FontWeight.Bold)
                                }
                                Row(
                                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    BomTag(bomItemCategoryLabel(item.itemCategory), MaterialTheme.colorScheme.secondary)
                                    BomTag(bomStatusLabel(item.status), if (item.status == "Active") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error)
                                    if (item.isCritical) BomTag("حرجة", MaterialTheme.colorScheme.error)
                                    if (item.useInOrders) BomTag("تظهر في أوامر العمل", MaterialTheme.colorScheme.tertiary)
                                    if (item.isAlternative) BomTag("بديل ${item.alternativeGroup}", MaterialTheme.colorScheme.secondary)
                                }
                                if (item.validFrom.isNotBlank() || item.validTo.isNotBlank()) {
                                    Text("الصلاحية: ${item.validFrom.ifBlank { "غير محدد" }} — ${item.validTo.ifBlank { "مفتوحة" }}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (item.notes.isNotBlank() && item.itemCategory != "Text") {
                                    Text(item.notes, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                if (canManage) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                                        TextButton(
                                            onClick = { itemHeader = header; editingItem = item },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("تعديل") }
                                        TextButton(
                                            onClick = { deletingItem = item },
                                            modifier = Modifier.weight(1f)
                                        ) { Text("حذف", color = MaterialTheme.colorScheme.error) }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showHeaderForm) {
        BomHeaderFormSheet(
            initial = editingHeader,
            asset = asset,
            onDismiss = { showHeaderForm = false },
            onSave = { onSaveHeader(it); showHeaderForm = false }
        )
    }
    itemHeader?.let { header ->
        BomItemFormSheet(
            initial = editingItem,
            header = header,
            currentAsset = asset,
            parts = parts,
            allAssets = allAssets,
            existingItems = items.filter { it.headerId == header.id },
            onDismiss = { itemHeader = null; editingItem = null },
            onSave = { onSaveItem(it); itemHeader = null; editingItem = null }
        )
    }
    deletingHeader?.let { header ->
        ConfirmDialog(
            title = "حذف قائمة المكونات",
            text = "سيتم حذف ${header.code} وجميع بنودها. هل تريد المتابعة؟",
            onConfirm = { onDeleteHeader(header); deletingHeader = null },
            onDismiss = { deletingHeader = null }
        )
    }
    deletingItem?.let { item ->
        ConfirmDialog(
            title = "حذف بند المكونات",
            text = "هل تريد حذف البند رقم ${item.itemNumber}؟",
            onConfirm = { onDeleteItem(item); deletingItem = null },
            onDismiss = { deletingItem = null }
        )
    }
}

@Composable
private fun BomTag(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = 0.13f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = color,
            fontWeight = FontWeight.SemiBold
        )
    }
}

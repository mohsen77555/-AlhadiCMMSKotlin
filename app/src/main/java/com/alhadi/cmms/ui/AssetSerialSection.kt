package com.alhadi.cmms.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SparePartEntity

@Composable
internal fun AssetSerialSection(
    asset: AssetEntity,
    serials: List<SerialNumberEntity>,
    movements: List<SerialNumberMovementEntity>,
    parts: List<SparePartEntity>
) {
    val linked = serials.firstOrNull { it.id == asset.linkedSerialId } ?: serials.firstOrNull { it.assetId == asset.id }
    val part = linked?.let { serial -> parts.firstOrNull { it.id == serial.partId } }
    val history = linked?.let { serial -> movements.filter { it.serialId == serial.id }.take(5) }.orEmpty()

    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("التتبع الفردي", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            when {
                linked != null -> {
                    AssetSerialRow("الرقم التسلسلي", linked.serialNumber)
                    AssetSerialRow("الحالة", serialStatusLabel(linked.status))
                    if (part != null) AssetSerialRow("القطعة المرتبطة", "${part.partNumber} • ${part.name}")
                    if (linked.lastMovementAt.isNotBlank()) AssetSerialRow("آخر حركة", linked.lastMovementAt)
                    if (history.isNotEmpty()) {
                        Text("آخر الحركات", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold)
                        history.forEach { movement ->
                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.42f),
                                shape = MaterialTheme.shapes.medium
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(serialMovementLabel(movement.movementType), fontWeight = FontWeight.Medium)
                                    Text(
                                        "${movement.createdAt} • ${movement.createdBy}",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
                asset.serialNumber.isNotBlank() -> {
                    AssetSerialRow("الرقم المسجل", asset.serialNumber)
                    Text(
                        "هذا الرقم غير مرتبط بعد بسجل تتبع فردي.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                else -> Text("لا يوجد رقم تسلسلي مرتبط بهذا الأصل.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun AssetSerialRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, modifier = Modifier.weight(1.35f), fontWeight = FontWeight.Medium)
    }
}

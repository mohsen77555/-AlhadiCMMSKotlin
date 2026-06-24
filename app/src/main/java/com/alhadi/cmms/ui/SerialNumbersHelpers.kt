package com.alhadi.cmms.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
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
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.SerialInstallRequest
import com.alhadi.cmms.data.SerialMasterRequest
import com.alhadi.cmms.data.SerialTransferRequest
import com.alhadi.cmms.data.SerializedIssueRequest
import com.alhadi.cmms.data.SerializedReceiptRequest
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import java.util.Locale

// ------------------------------------------------------------------------
// Serial-number screen helper widgets (moved out of SerialNumbersScreen.kt)
// ------------------------------------------------------------------------

@Composable
internal fun SerialSectionTitle(title: String, subtitle: String) {
    Column {
        Text(title, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun SerialMetric(label: String, value: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surface) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
internal fun SerialInfo(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, modifier = Modifier.weight(1.4f), fontWeight = FontWeight.Medium)
    }
}

@Composable
internal fun SerialBadge(text: String, color: Color) {
    Surface(shape = CircleShape, color = color.copy(alpha = 0.13f)) {
        Text(text, modifier = Modifier.padding(horizontal = 9.dp, vertical = 4.dp), color = color, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
internal fun SerialEmpty(text: String) {
    Surface(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f)) {
        Text(text, modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}

@Composable
internal fun serialModeColor(mode: String): Color = when (mode) {
    "Block" -> MaterialTheme.colorScheme.error
    "Warning" -> MaterialTheme.colorScheme.tertiary
    else -> MaterialTheme.colorScheme.secondary
}

@Composable
internal fun serialStatusColor(status: String): Color = when (status) {
    "InStock" -> MaterialTheme.colorScheme.primary
    "Installed" -> MaterialTheme.colorScheme.tertiary
    "Issued", "InRepair" -> MaterialTheme.colorScheme.secondary
    "Scrapped" -> MaterialTheme.colorScheme.error
    else -> MaterialTheme.colorScheme.onSurfaceVariant
}

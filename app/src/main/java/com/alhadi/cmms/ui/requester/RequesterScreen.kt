package com.alhadi.cmms.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity

/**
 * Service-requester screen ("طلباتي"): a tailored experience for the Requester role. The user
 * raises a maintenance request and tracks the status of their own requests — read-only beyond that.
 */
@Composable
internal fun RequesterScreen(
    innerPadding: PaddingValues,
    myRequests: List<MaintenanceNotificationEntity>,
    assets: List<AssetEntity>,
    assetMap: Map<Long, AssetEntity>,
    onSave: (MaintenanceNotificationEntity) -> Unit
) {
    var showForm by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            SectionHeader("طلباتي")
            Text("أنشئ طلب صيانة وتابع حالته حتى الإغلاق.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { AddButton("طلب صيانة جديد") { showForm = true } }
        if (myRequests.isEmpty()) {
            item { EmptyState("لا توجد طلبات بعد", Icons.Filled.NotificationsActive) }
        }
        items(myRequests, key = { it.id }) { request ->
            NotificationCard(
                notification = request,
                asset = request.assetId?.let { assetMap[it] },
                canManage = false,
                onSetStatus = { _, _ -> },
                onCreateOrder = { },
                onEdit = { },
                onDelete = { }
            )
        }
    }

    if (showForm) {
        NotificationFormSheet(
            initial = null,
            assets = assets,
            onDismiss = { showForm = false },
            onSave = { onSave(it); showForm = false }
        )
    }
}

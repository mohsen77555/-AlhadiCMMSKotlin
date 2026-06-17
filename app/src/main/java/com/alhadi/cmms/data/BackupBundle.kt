package com.alhadi.cmms.data

import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
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
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import kotlinx.serialization.Serializable

/**
 * A complete, portable snapshot of every table in the database. Serialized to JSON for
 * backup/restore so the user's maintenance record survives device loss and can be moved
 * between devices manually. New tables MUST be added here (with a default of emptyList so
 * older backups still restore).
 */
@Serializable
data class BackupBundle(
    val formatVersion: Int = CURRENT_FORMAT_VERSION,
    val appDbVersion: Int = 0,
    val exportedAt: String = "",
    val assets: List<AssetEntity> = emptyList(),
    val workOrders: List<WorkOrderEntity> = emptyList(),
    val preventiveMaintenance: List<PreventiveMaintenanceEntity> = emptyList(),
    val spareParts: List<SparePartEntity> = emptyList(),
    val inventoryTransactions: List<InventoryTransactionEntity> = emptyList(),
    val users: List<UserEntity> = emptyList(),
    val auditLog: List<AuditLogEntity> = emptyList(),
    val measuringPoints: List<MeasuringPointEntity> = emptyList(),
    val measurementReadings: List<MeasurementReadingEntity> = emptyList(),
    val functionalLocations: List<FunctionalLocationEntity> = emptyList(),
    val capa: List<CapaEntity> = emptyList(),
    val assetDocuments: List<AssetDocumentEntity> = emptyList(),
    val assetCharacteristics: List<AssetCharacteristicEntity> = emptyList(),
    val assetBom: List<AssetBomItemEntity> = emptyList(),
    val assetMovements: List<AssetMovementEntity> = emptyList(),
    val pmChecklist: List<PmChecklistItemEntity> = emptyList(),
    val notifications: List<MaintenanceNotificationEntity> = emptyList(),
    val operations: List<WorkOrderOperationEntity> = emptyList(),
    val confirmations: List<WorkOrderConfirmationEntity> = emptyList(),
    val photos: List<WorkOrderPhotoEntity> = emptyList(),
    val taskLists: List<TaskListEntity> = emptyList(),
    val taskListOperations: List<TaskListOperationEntity> = emptyList(),
    val permits: List<WorkPermitEntity> = emptyList()
) {
    /** Total number of records across all tables — handy for a restore summary. */
    val totalRecords: Int
        get() = assets.size + workOrders.size + preventiveMaintenance.size + spareParts.size +
            inventoryTransactions.size + users.size + auditLog.size + measuringPoints.size +
            measurementReadings.size + functionalLocations.size + capa.size + assetDocuments.size +
            assetCharacteristics.size + assetBom.size + assetMovements.size + pmChecklist.size +
            notifications.size + operations.size + confirmations.size + photos.size +
            taskLists.size + taskListOperations.size + permits.size

    companion object {
        const val CURRENT_FORMAT_VERSION = 1
    }
}

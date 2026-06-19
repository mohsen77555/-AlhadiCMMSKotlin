package com.alhadi.cmms.data

import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetClassEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.AssetPartnerEntity
import com.alhadi.cmms.data.entity.AssetWarrantyEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.MeterReplacementEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WarrantyClaimEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import kotlinx.serialization.Serializable

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
    val meterReplacements: List<MeterReplacementEntity> = emptyList(),
    val functionalLocations: List<FunctionalLocationEntity> = emptyList(),
    val capa: List<CapaEntity> = emptyList(),
    val assetDocuments: List<AssetDocumentEntity> = emptyList(),
    val assetCharacteristics: List<AssetCharacteristicEntity> = emptyList(),
    val assetClasses: List<AssetClassEntity> = emptyList(),
    val assetPartners: List<AssetPartnerEntity> = emptyList(),
    val assetWarranties: List<AssetWarrantyEntity> = emptyList(),
    val warrantyClaims: List<WarrantyClaimEntity> = emptyList(),
    val assetBom: List<AssetBomItemEntity> = emptyList(),
    val assetMovements: List<AssetMovementEntity> = emptyList(),
    val pmChecklist: List<PmChecklistItemEntity> = emptyList(),
    val notifications: List<MaintenanceNotificationEntity> = emptyList(),
    val operations: List<WorkOrderOperationEntity> = emptyList(),
    val confirmations: List<WorkOrderConfirmationEntity> = emptyList(),
    val photos: List<WorkOrderPhotoEntity> = emptyList(),
    val taskLists: List<TaskListEntity> = emptyList(),
    val taskListOperations: List<TaskListOperationEntity> = emptyList(),
    val permits: List<WorkPermitEntity> = emptyList(),
    val purchaseOrders: List<PurchaseOrderEntity> = emptyList(),
    val suppliers: List<SupplierEntity> = emptyList()
) {
    val totalRecords: Int
        get() = assets.size + workOrders.size + preventiveMaintenance.size + spareParts.size +
            inventoryTransactions.size + users.size + auditLog.size + measuringPoints.size +
            measurementReadings.size + meterReplacements.size + functionalLocations.size + capa.size +
            assetDocuments.size + assetCharacteristics.size + assetClasses.size + assetPartners.size +
            assetWarranties.size + warrantyClaims.size + assetBom.size + assetMovements.size +
            pmChecklist.size + notifications.size + operations.size + confirmations.size + photos.size +
            taskLists.size + taskListOperations.size + permits.size + purchaseOrders.size + suppliers.size

    companion object {
        const val CURRENT_FORMAT_VERSION = 1
    }
}

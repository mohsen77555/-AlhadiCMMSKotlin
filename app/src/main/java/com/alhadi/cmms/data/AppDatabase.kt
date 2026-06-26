package com.alhadi.cmms.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alhadi.cmms.data.dao.AssetBomDao
import com.alhadi.cmms.data.dao.AssetBomHeaderDao
import com.alhadi.cmms.data.dao.AssetCharacteristicDao
import com.alhadi.cmms.data.dao.AssetDao
import com.alhadi.cmms.data.dao.AssetDocumentDao
import com.alhadi.cmms.data.dao.AssetMovementDao
import com.alhadi.cmms.data.dao.AuditLogDao
import com.alhadi.cmms.data.dao.CapaDao
import com.alhadi.cmms.data.dao.FunctionalLocationDao
import com.alhadi.cmms.data.dao.InventoryTransactionDao
import com.alhadi.cmms.data.dao.MaintenanceNotificationDao
import com.alhadi.cmms.data.dao.MeasurementDao
import com.alhadi.cmms.data.dao.PmChecklistDao
import com.alhadi.cmms.data.dao.SerialNumberDao
import com.alhadi.cmms.data.dao.PreventiveMaintenanceDao
import com.alhadi.cmms.data.dao.SparePartDao
import com.alhadi.cmms.data.dao.TaskListDao
import com.alhadi.cmms.data.dao.UserDao
import com.alhadi.cmms.data.dao.WorkOrderConfirmationDao
import com.alhadi.cmms.data.dao.WorkOrderDao
import com.alhadi.cmms.data.dao.WorkOrderOperationDao
import com.alhadi.cmms.data.dao.WorkOrderPhotoDao
import com.alhadi.cmms.data.dao.WorkPermitDao
import com.alhadi.cmms.data.dao.WarehouseDao
import com.alhadi.cmms.data.dao.OrgUnitDao
import com.alhadi.cmms.data.dao.WorkOrderHistoryDao
import com.alhadi.cmms.data.dao.SupplierDao
import com.alhadi.cmms.data.dao.PurchaseOrderDao
import com.alhadi.cmms.data.dao.PurchaseOrderLineDao
import com.alhadi.cmms.data.entity.WorkOrderHistoryEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.PurchaseOrderLineEntity
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
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
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity

@Database(
    entities = [
        AssetEntity::class,
        WorkOrderEntity::class,
        PreventiveMaintenanceEntity::class,
        SerialNumberProfileEntity::class,
        SerialNumberEntity::class,
        SerialNumberMovementEntity::class,
        SparePartEntity::class,
        InventoryTransactionEntity::class,
        UserEntity::class,
        AuditLogEntity::class,
        MeasuringPointEntity::class,
        MeasurementReadingEntity::class,
        FunctionalLocationEntity::class,
        CapaEntity::class,
        AssetDocumentEntity::class,
        AssetCharacteristicEntity::class,
        AssetBomHeaderEntity::class,
        AssetBomItemEntity::class,
        AssetMovementEntity::class,
        PmChecklistItemEntity::class,
        MaintenanceNotificationEntity::class,
        WorkOrderOperationEntity::class,
        WorkOrderConfirmationEntity::class,
        WorkOrderPhotoEntity::class,
        TaskListEntity::class,
        TaskListOperationEntity::class,
        WorkPermitEntity::class,
        WarehouseEntity::class,
        OrgUnitEntity::class,
        WorkOrderHistoryEntity::class,
        SupplierEntity::class,
        PurchaseOrderEntity::class,
        PurchaseOrderLineEntity::class
    ],
    version = 44,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun workOrderDao(): WorkOrderDao
    abstract fun preventiveMaintenanceDao(): PreventiveMaintenanceDao
    abstract fun sparePartDao(): SparePartDao
    abstract fun serialNumberDao(): SerialNumberDao
    abstract fun inventoryTransactionDao(): InventoryTransactionDao
    abstract fun userDao(): UserDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun functionalLocationDao(): FunctionalLocationDao
    abstract fun capaDao(): CapaDao
    abstract fun assetDocumentDao(): AssetDocumentDao
    abstract fun assetCharacteristicDao(): AssetCharacteristicDao
    abstract fun assetBomHeaderDao(): AssetBomHeaderDao
    abstract fun assetBomDao(): AssetBomDao
    abstract fun assetMovementDao(): AssetMovementDao
    abstract fun pmChecklistDao(): PmChecklistDao
    abstract fun maintenanceNotificationDao(): MaintenanceNotificationDao
    abstract fun workOrderOperationDao(): WorkOrderOperationDao
    abstract fun workOrderConfirmationDao(): WorkOrderConfirmationDao
    abstract fun workOrderPhotoDao(): WorkOrderPhotoDao
    abstract fun taskListDao(): TaskListDao
    abstract fun workPermitDao(): WorkPermitDao
    abstract fun warehouseDao(): WarehouseDao
    abstract fun orgUnitDao(): OrgUnitDao
    abstract fun workOrderHistoryDao(): WorkOrderHistoryDao
    abstract fun supplierDao(): SupplierDao
    abstract fun purchaseOrderDao(): PurchaseOrderDao
    abstract fun purchaseOrderLineDao(): PurchaseOrderLineDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "alhadi_cmms.db"
                )
                    // Real maintenance data must survive app upgrades: apply explicit migrations.
                    // A missing upgrade path now fails loudly (forcing a migration) instead of
                    // silently wiping the user's records.
                    .addMigrations(*DbMigrations.ALL)
                    // Only a downgrade (installing an older build over a newer DB) resets data.
                    .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

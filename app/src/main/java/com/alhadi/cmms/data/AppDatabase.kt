package com.alhadi.cmms.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alhadi.cmms.data.dao.AssetBomDao
import com.alhadi.cmms.data.dao.AssetCharacteristicDao
import com.alhadi.cmms.data.dao.AssetClassDao
import com.alhadi.cmms.data.dao.AssetDao
import com.alhadi.cmms.data.dao.AssetDocumentDao
import com.alhadi.cmms.data.dao.AssetFinancialDao
import com.alhadi.cmms.data.dao.AssetMovementDao
import com.alhadi.cmms.data.dao.AssetPartnerDao
import com.alhadi.cmms.data.dao.AssetWarrantyDao
import com.alhadi.cmms.data.dao.AuditLogDao
import com.alhadi.cmms.data.dao.CapaDao
import com.alhadi.cmms.data.dao.DataQualityDao
import com.alhadi.cmms.data.dao.FunctionalLocationDao
import com.alhadi.cmms.data.dao.InventoryTransactionDao
import com.alhadi.cmms.data.dao.MaintenanceNotificationDao
import com.alhadi.cmms.data.dao.MeasurementDao
import com.alhadi.cmms.data.dao.PmChecklistDao
import com.alhadi.cmms.data.dao.PreventiveMaintenanceDao
import com.alhadi.cmms.data.dao.PurchaseOrderDao
import com.alhadi.cmms.data.dao.SerializedItemDao
import com.alhadi.cmms.data.dao.SparePartDao
import com.alhadi.cmms.data.dao.SupplierDao
import com.alhadi.cmms.data.dao.TaskListDao
import com.alhadi.cmms.data.dao.TrashDao
import com.alhadi.cmms.data.dao.UserDao
import com.alhadi.cmms.data.dao.WorkOrderConfirmationDao
import com.alhadi.cmms.data.dao.WorkOrderDao
import com.alhadi.cmms.data.dao.WorkOrderOperationDao
import com.alhadi.cmms.data.dao.WorkOrderPhotoDao
import com.alhadi.cmms.data.dao.WorkPermitDao
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetBomRevisionEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetClassEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetFinancialRecordEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.AssetPartnerEntity
import com.alhadi.cmms.data.entity.AssetWarrantyEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.BomAlternativeEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.DataQualityIssueEntity
import com.alhadi.cmms.data.entity.FinancialPostingEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.ImportBatchEntity
import com.alhadi.cmms.data.entity.ImportIssueEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.MeterReplacementEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.PurchaseOrderEntity
import com.alhadi.cmms.data.entity.SerialMovementEntity
import com.alhadi.cmms.data.entity.SerializedItemEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.SupplierEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.TrashEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WarrantyClaimEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity

@Database(
    entities = [
        AssetEntity::class,
        WorkOrderEntity::class,
        PreventiveMaintenanceEntity::class,
        SparePartEntity::class,
        InventoryTransactionEntity::class,
        UserEntity::class,
        AuditLogEntity::class,
        MeasuringPointEntity::class,
        MeasurementReadingEntity::class,
        MeterReplacementEntity::class,
        FunctionalLocationEntity::class,
        CapaEntity::class,
        AssetDocumentEntity::class,
        AssetCharacteristicEntity::class,
        AssetClassEntity::class,
        AssetPartnerEntity::class,
        AssetWarrantyEntity::class,
        WarrantyClaimEntity::class,
        AssetBomItemEntity::class,
        AssetBomRevisionEntity::class,
        BomAlternativeEntity::class,
        SerializedItemEntity::class,
        SerialMovementEntity::class,
        AssetFinancialRecordEntity::class,
        FinancialPostingEntity::class,
        ImportBatchEntity::class,
        ImportIssueEntity::class,
        DataQualityIssueEntity::class,
        AssetMovementEntity::class,
        PmChecklistItemEntity::class,
        MaintenanceNotificationEntity::class,
        WorkOrderOperationEntity::class,
        WorkOrderConfirmationEntity::class,
        WorkOrderPhotoEntity::class,
        TaskListEntity::class,
        TaskListOperationEntity::class,
        WorkPermitEntity::class,
        TrashEntity::class,
        PurchaseOrderEntity::class,
        SupplierEntity::class
    ],
    version = 28,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun assetDao(): AssetDao
    abstract fun workOrderDao(): WorkOrderDao
    abstract fun preventiveMaintenanceDao(): PreventiveMaintenanceDao
    abstract fun sparePartDao(): SparePartDao
    abstract fun inventoryTransactionDao(): InventoryTransactionDao
    abstract fun userDao(): UserDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun measurementDao(): MeasurementDao
    abstract fun functionalLocationDao(): FunctionalLocationDao
    abstract fun capaDao(): CapaDao
    abstract fun assetDocumentDao(): AssetDocumentDao
    abstract fun assetCharacteristicDao(): AssetCharacteristicDao
    abstract fun assetClassDao(): AssetClassDao
    abstract fun assetPartnerDao(): AssetPartnerDao
    abstract fun assetWarrantyDao(): AssetWarrantyDao
    abstract fun assetBomDao(): AssetBomDao
    abstract fun serializedItemDao(): SerializedItemDao
    abstract fun assetFinancialDao(): AssetFinancialDao
    abstract fun dataQualityDao(): DataQualityDao
    abstract fun assetMovementDao(): AssetMovementDao
    abstract fun pmChecklistDao(): PmChecklistDao
    abstract fun maintenanceNotificationDao(): MaintenanceNotificationDao
    abstract fun workOrderOperationDao(): WorkOrderOperationDao
    abstract fun workOrderConfirmationDao(): WorkOrderConfirmationDao
    abstract fun workOrderPhotoDao(): WorkOrderPhotoDao
    abstract fun taskListDao(): TaskListDao
    abstract fun workPermitDao(): WorkPermitDao
    abstract fun trashDao(): TrashDao
    abstract fun purchaseOrderDao(): PurchaseOrderDao
    abstract fun supplierDao(): SupplierDao

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
                    .addMigrations(*DbMigrations.ALL, DbMigration28.MIGRATION_27_28)
                    .fallbackToDestructiveMigrationOnDowngrade(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

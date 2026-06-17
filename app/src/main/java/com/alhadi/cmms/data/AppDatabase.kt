package com.alhadi.cmms.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.alhadi.cmms.data.dao.AssetDao
import com.alhadi.cmms.data.dao.AuditLogDao
import com.alhadi.cmms.data.dao.InventoryTransactionDao
import com.alhadi.cmms.data.dao.MeasurementDao
import com.alhadi.cmms.data.dao.PreventiveMaintenanceDao
import com.alhadi.cmms.data.dao.SparePartDao
import com.alhadi.cmms.data.dao.UserDao
import com.alhadi.cmms.data.dao.WorkOrderDao
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity

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
        MeasurementReadingEntity::class
    ],
    version = 3,
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
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

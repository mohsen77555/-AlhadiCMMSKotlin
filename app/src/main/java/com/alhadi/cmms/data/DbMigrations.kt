package com.alhadi.cmms.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for [AppDatabase].
 *
 * IMPORTANT (data safety): the database is the user's real maintenance record. It must NOT be
 * wiped on app upgrades. Every schema change must ship an explicit migration.
 */
object DbMigrations {

    /** v22 -> v23: adds the recycle-bin (soft delete) `trash` table. */
    val MIGRATION_22_23 = object : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `trash` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`entityType` TEXT NOT NULL, " +
                    "`entityId` INTEGER NOT NULL, " +
                    "`label` TEXT NOT NULL, " +
                    "`payload` TEXT NOT NULL, " +
                    "`deletedAt` TEXT NOT NULL, " +
                    "`deletedBy` TEXT NOT NULL)"
            )
        }
    }

    /** v23 -> v24: adds the procurement (`purchase_orders`) table. */
    val MIGRATION_23_24 = object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `purchase_orders` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`number` TEXT NOT NULL, " +
                    "`status` TEXT NOT NULL, " +
                    "`partId` INTEGER, " +
                    "`itemName` TEXT NOT NULL, " +
                    "`quantity` INTEGER NOT NULL, " +
                    "`unitPrice` REAL NOT NULL, " +
                    "`supplier` TEXT NOT NULL, " +
                    "`workOrderId` INTEGER, " +
                    "`requestedBy` TEXT NOT NULL, " +
                    "`createdAt` TEXT NOT NULL, " +
                    "`neededBy` TEXT NOT NULL, " +
                    "`receivedAt` TEXT NOT NULL, " +
                    "`notes` TEXT NOT NULL)"
            )
        }
    }

    /** v24 -> v25: adds the `suppliers` table. */
    val MIGRATION_24_25 = object : Migration(24, 25) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `suppliers` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`name` TEXT NOT NULL, " +
                    "`contactPerson` TEXT NOT NULL, " +
                    "`phone` TEXT NOT NULL, " +
                    "`email` TEXT NOT NULL, " +
                    "`address` TEXT NOT NULL, " +
                    "`notes` TEXT NOT NULL)"
            )
        }
    }

    /**
     * v25 -> v26: governed asset master data, functional-location governance,
     * criticality assessment, and richer asset lifecycle history.
     */
    val MIGRATION_25_26 = object : Migration(25, 26) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN assetType TEXT NOT NULL DEFAULT 'Equipment'",
                "ALTER TABLE assets ADD COLUMN assetCategory TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN description TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN organizationCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN plantCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN maintenanceWorkCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN planningGroup TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN costCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN ownerDepartment TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN responsiblePerson TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN manufacturingYear INTEGER",
                "ALTER TABLE assets ADD COLUMN purchaseDate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN commissioningDate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN financialAssetRef TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN lifecycleStatus TEXT NOT NULL DEFAULT 'InService'",
                "ALTER TABLE assets ADD COLUMN operationalStatus TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN healthStatus TEXT NOT NULL DEFAULT 'Good'",
                "ALTER TABLE assets ADD COLUMN criticalitySafetyImpact INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE assets ADD COLUMN criticalityProductionImpact INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE assets ADD COLUMN criticalityEnvironmentalImpact INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE assets ADD COLUMN criticalityServiceImpact INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE assets ADD COLUMN criticalityFinancialImpact INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE assets ADD COLUMN criticalityScore INTEGER NOT NULL DEFAULT 5",
                "ALTER TABLE assets ADD COLUMN criticalityAssessedAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN criticalityAssessedBy TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN createdBy TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN createdAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN updatedBy TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN updatedAt TEXT NOT NULL DEFAULT ''",

                "ALTER TABLE functional_locations ADD COLUMN organizationCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN plantCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN locationCategory TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN costCenterCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN workCenterCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN referenceLocationId INTEGER",
                "ALTER TABLE functional_locations ADD COLUMN isReference INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE functional_locations ADD COLUMN createdAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN updatedAt TEXT NOT NULL DEFAULT ''",

                "ALTER TABLE asset_movements ADD COLUMN reason TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN previousLifecycleStatus TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN newLifecycleStatus TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN approvedBy TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN referenceType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN referenceId INTEGER"
            )
        }
    }

    /** All migrations, in order. Append new migration objects as the schema evolves. */
    val ALL: Array<Migration> = arrayOf(
        MIGRATION_22_23,
        MIGRATION_23_24,
        MIGRATION_24_25,
        MIGRATION_25_26
    )
}

/** Tiny helper so migration SQL reads a little cleaner. */
internal fun SupportSQLiteDatabase.exec(vararg statements: String) {
    statements.forEach { execSQL(it) }
}

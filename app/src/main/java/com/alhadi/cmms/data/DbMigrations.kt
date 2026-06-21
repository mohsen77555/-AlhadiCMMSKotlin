package com.alhadi.cmms.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations for [AppDatabase].
 *
 * IMPORTANT (data safety): the database is the user's real maintenance record. It must NOT be
 * wiped on app upgrades. Therefore the builder no longer uses destructive migration on upgrade —
 * every schema change MUST ship a [Migration] here.
 *
 * Process for any future schema change:
 *  1. Edit the entity/DAO.
 *  2. Bump `version` in [AppDatabase] by 1 (e.g. 22 -> 23).
 *  3. Add a `Migration(old, new)` below that applies the exact SQL change, then register it in [ALL].
 *  4. Build once; the exported schema JSON under `app/schemas` updates. Commit it.
 *
 * Common patterns:
 *  - Add a nullable column:  ALTER TABLE x ADD COLUMN c TEXT
 *  - Add a NOT NULL column:  ALTER TABLE x ADD COLUMN c INTEGER NOT NULL DEFAULT 0
 *  - Add an index:           CREATE INDEX IF NOT EXISTS idx_x_c ON x(c)
 *
 * Example (kept for reference; uncomment and adapt when needed):
 *
 *   val MIGRATION_22_23 = object : Migration(22, 23) {
 *       override fun migrate(db: SupportSQLiteDatabase) {
 *           db.execSQL("ALTER TABLE spare_parts ADD COLUMN reorderQty INTEGER NOT NULL DEFAULT 0")
 *       }
 *   }
 */
object DbMigrations {
    val MIGRATION_22_23 = object : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN assetType TEXT NOT NULL DEFAULT 'Equipment'",
                "ALTER TABLE assets ADD COLUMN assetCategory TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN objectType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN description TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN companyId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN siteId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN plantId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN maintenancePlantId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN planningPlantId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN workCenterId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN plannerGroupId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN costCenterId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN departmentId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN responsiblePersonId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN constructionType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN commissioningAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN financialAssetRef TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN notes TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partners TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN safetyCritical INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN riskLevel TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN requiredPermits TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN safetyInstructions TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN ppeRequired TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN isolationRequired INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN complianceRequirements TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN financialStatus TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN bookValue REAL NOT NULL DEFAULT 0.0",
                "ALTER TABLE assets ADD COLUMN capitalizationAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearStartPoint TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearEndPoint TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearLength REAL NOT NULL DEFAULT 0.0",
                "ALTER TABLE assets ADD COLUMN linearUnit TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearRoute TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    val MIGRATION_23_24 = object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN equipmentCategory TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN assetClass TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN assetSubclass TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN longDescription TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN alternativeLabel TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN externalAssetCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN legacyAssetCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN barcode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN qrCode TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    val MIGRATION_24_25 = object : Migration(24, 25) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "CREATE TABLE IF NOT EXISTS companies (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'Active', createdBy TEXT NOT NULL DEFAULT 'System', createdAt TEXT NOT NULL DEFAULT '', updatedBy TEXT NOT NULL DEFAULT '', updatedAt TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_companies_code ON companies(code)",
                "CREATE TABLE IF NOT EXISTS sites (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, companyId INTEGER NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, physicalAddress TEXT NOT NULL DEFAULT '', status TEXT NOT NULL DEFAULT 'Active', createdBy TEXT NOT NULL DEFAULT 'System', createdAt TEXT NOT NULL DEFAULT '', updatedBy TEXT NOT NULL DEFAULT '', updatedAt TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_sites_companyId_code ON sites(companyId, code)",
                "CREATE TABLE IF NOT EXISTS plants (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, companyId INTEGER NOT NULL, siteId INTEGER, code TEXT NOT NULL, name TEXT NOT NULL, plantType TEXT NOT NULL DEFAULT 'Operational', status TEXT NOT NULL DEFAULT 'Active', createdBy TEXT NOT NULL DEFAULT 'System', createdAt TEXT NOT NULL DEFAULT '', updatedBy TEXT NOT NULL DEFAULT '', updatedAt TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_plants_companyId_code ON plants(companyId, code)",
                "CREATE INDEX IF NOT EXISTS index_plants_siteId ON plants(siteId)",
                "CREATE TABLE IF NOT EXISTS work_centers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, plantId INTEGER NOT NULL, discipline TEXT NOT NULL DEFAULT '', capacityType TEXT NOT NULL DEFAULT '', defaultHourlyRate REAL NOT NULL DEFAULT 0.0, status TEXT NOT NULL DEFAULT 'Active', createdBy TEXT NOT NULL DEFAULT 'System', createdAt TEXT NOT NULL DEFAULT '', updatedBy TEXT NOT NULL DEFAULT '', updatedAt TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_work_centers_plantId_code ON work_centers(plantId, code)",
                "CREATE TABLE IF NOT EXISTS planner_groups (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, planningPlantId INTEGER NOT NULL, discipline TEXT NOT NULL DEFAULT '', responsibleUserId INTEGER, status TEXT NOT NULL DEFAULT 'Active', createdBy TEXT NOT NULL DEFAULT 'System', createdAt TEXT NOT NULL DEFAULT '', updatedBy TEXT NOT NULL DEFAULT '', updatedAt TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_planner_groups_planningPlantId_code ON planner_groups(planningPlantId, code)",
                "CREATE TABLE IF NOT EXISTS departments (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, companyId INTEGER NOT NULL, siteId INTEGER, managerUserId INTEGER, status TEXT NOT NULL DEFAULT 'Active', createdBy TEXT NOT NULL DEFAULT 'System', createdAt TEXT NOT NULL DEFAULT '', updatedBy TEXT NOT NULL DEFAULT '', updatedAt TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_departments_companyId_code ON departments(companyId, code)",
                "CREATE INDEX IF NOT EXISTS index_departments_siteId ON departments(siteId)",
                "CREATE TABLE IF NOT EXISTS cost_centers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, companyId INTEGER NOT NULL, departmentId INTEGER, validFrom TEXT NOT NULL DEFAULT '', validTo TEXT NOT NULL DEFAULT '', status TEXT NOT NULL DEFAULT 'Active', createdBy TEXT NOT NULL DEFAULT 'System', createdAt TEXT NOT NULL DEFAULT '', updatedBy TEXT NOT NULL DEFAULT '', updatedAt TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_cost_centers_companyId_code ON cost_centers(companyId, code)",
                "CREATE INDEX IF NOT EXISTS index_cost_centers_departmentId ON cost_centers(departmentId)",
                "CREATE TABLE IF NOT EXISTS storage_locations (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, plantId INTEGER NOT NULL, storageType TEXT NOT NULL DEFAULT 'General', status TEXT NOT NULL DEFAULT 'Active', createdBy TEXT NOT NULL DEFAULT 'System', createdAt TEXT NOT NULL DEFAULT '', updatedBy TEXT NOT NULL DEFAULT '', updatedAt TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_storage_locations_plantId_code ON storage_locations(plantId, code)",
                "ALTER TABLE functional_locations ADD COLUMN companyId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN siteId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN plantId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN maintenancePlantId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN planningPlantId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN workCenterId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN plannerGroupId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN departmentId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN costCenterId TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN physicalLocation TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN building TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN floor TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN room TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN area TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN line TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN position TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN level INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE functional_locations ADD COLUMN path TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN createdBy TEXT NOT NULL DEFAULT 'System'",
                "ALTER TABLE functional_locations ADD COLUMN createdAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN updatedBy TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN updatedAt TEXT NOT NULL DEFAULT ''"
            )
            db.exec(*assetOrgColumns())
        }
    }

    val MIGRATION_25_26 = object : Migration(25, 26) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN currentCustodian TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN currentPhysicalLocation TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN lastKnownLocation TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN movementStatus TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN checkedOutTo TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN checkedOutAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN expectedReturnAt TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    val MIGRATION_26_27 = object : Migration(26, 27) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE asset_movements ADD COLUMN oldPlant TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN newPlant TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN oldWorkCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN newWorkCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN oldCostCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN newCostCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN transferReason TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN approvedBy TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_movements ADD COLUMN attachment TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    /** All migrations, in order. Append new `Migration` objects here as the schema evolves. */
    val ALL: Array<Migration> = arrayOf(
        MIGRATION_22_23,
        MIGRATION_23_24,
        MIGRATION_24_25,
        MIGRATION_25_26,
        MIGRATION_26_27
    )

    private fun assetOrgColumns(): Array<String> = arrayOf(
        "ALTER TABLE assets ADD COLUMN companyCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN companyName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN siteCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN siteName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN plantCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN plantName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN maintenancePlantCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN maintenancePlantName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN planningPlantCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN planningPlantName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN workCenterCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN workCenterName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN plannerGroupCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN plannerGroupName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN costCenterCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN costCenterName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN departmentCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN departmentName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN functionalLocationId TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN functionalLocationCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN functionalLocationName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN functionalLocationPath TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN functionalLocationLevel INTEGER NOT NULL DEFAULT 0",
        "ALTER TABLE assets ADD COLUMN physicalLocation TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN building TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN floor TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN room TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN area TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN line TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN position TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN gpsLatitude TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN gpsLongitude TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN storageLocationId TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN storageLocationCode TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN storageLocationName TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN locationInheritanceSource TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN isLocationInherited INTEGER NOT NULL DEFAULT 0",
        "ALTER TABLE assets ADD COLUMN isWorkCenterInherited INTEGER NOT NULL DEFAULT 0",
        "ALTER TABLE assets ADD COLUMN isPlannerGroupInherited INTEGER NOT NULL DEFAULT 0",
        "ALTER TABLE assets ADD COLUMN isCostCenterInherited INTEGER NOT NULL DEFAULT 0",
        "ALTER TABLE assets ADD COLUMN isMaintenancePlantInherited INTEGER NOT NULL DEFAULT 0",
        "ALTER TABLE assets ADD COLUMN isPlanningPlantInherited INTEGER NOT NULL DEFAULT 0",
        "ALTER TABLE assets ADD COLUMN inheritedFromFunctionalLocationId TEXT NOT NULL DEFAULT ''",
        "ALTER TABLE assets ADD COLUMN manualOverrideReason TEXT NOT NULL DEFAULT ''"
    )
}

/** Tiny helper so migration SQL reads a little cleaner. */
internal fun SupportSQLiteDatabase.exec(vararg statements: String) {
    statements.forEach { execSQL(it) }
}

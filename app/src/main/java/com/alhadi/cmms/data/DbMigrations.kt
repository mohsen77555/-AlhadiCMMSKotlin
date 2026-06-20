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

    /** All migrations, in order. Append new `Migration` objects here as the schema evolves. */
    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24)
}

/** Tiny helper so migration SQL reads a little cleaner. */
internal fun SupportSQLiteDatabase.exec(vararg statements: String) {
    statements.forEach { execSQL(it) }
}

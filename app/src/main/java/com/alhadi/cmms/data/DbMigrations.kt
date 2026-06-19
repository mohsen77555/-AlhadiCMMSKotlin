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

    /** Adds extended asset identity, organization, partner and address fields. */
    val MIGRATION_22_23 = object : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN category TEXT NOT NULL DEFAULT 'Machine'",
                "ALTER TABLE assets ADD COLUMN objectType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN description TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN maintenancePlant TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN planningPlant TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN plannerGroup TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN mainWorkCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN productionWorkCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN costCenter TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN responsiblePerson TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN assetNumber TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN constructionYear TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN constructionMonth TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN startupDate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partnerName TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partnerRole TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partnerPhone TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN partnerEmail TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN addressLine TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN city TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN country TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    /** Adds classification metadata and characteristic inheritance controls. */
    val MIGRATION_23_24 = object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN standardClass TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN inheritParentCharacteristics INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE asset_characteristics ADD COLUMN className TEXT NOT NULL DEFAULT 'عام'",
                "ALTER TABLE asset_characteristics ADD COLUMN dataType TEXT NOT NULL DEFAULT 'Text'",
                "ALTER TABLE asset_characteristics ADD COLUMN allowedValues TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN isRequired INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    /** Adds linear asset master data and optional linear references in maintenance processing. */
    val MIGRATION_24_25 = object : Migration(24, 25) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN isLinearAsset INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearStartPoint REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearEndPoint REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearUnit TEXT NOT NULL DEFAULT 'km'",
                "ALTER TABLE assets ADD COLUMN linearReferencePattern TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearRouteCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearStartMarker TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearEndMarker TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearStartMarkerDistance REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearEndMarkerDistance REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearMarkerUnit TEXT NOT NULL DEFAULT 'km'",
                "ALTER TABLE assets ADD COLUMN linearHorizontalOffset REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearVerticalOffset REAL NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN linearOffsetUnit TEXT NOT NULL DEFAULT 'm'",
                "ALTER TABLE assets ADD COLUMN linearDirection TEXT NOT NULL DEFAULT 'Both'",
                "ALTER TABLE assets ADD COLUMN networkObjectCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN networkObjectType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN networkRelation TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN networkAttributes TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN linearStartLatitude REAL",
                "ALTER TABLE assets ADD COLUMN linearStartLongitude REAL",
                "ALTER TABLE assets ADD COLUMN linearEndLatitude REAL",
                "ALTER TABLE assets ADD COLUMN linearEndLongitude REAL",
                "ALTER TABLE work_orders ADD COLUMN linearStartPoint REAL",
                "ALTER TABLE work_orders ADD COLUMN linearEndPoint REAL",
                "ALTER TABLE work_orders ADD COLUMN linearMarker TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE work_orders ADD COLUMN linearHorizontalOffset REAL",
                "ALTER TABLE work_orders ADD COLUMN linearVerticalOffset REAL",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearStartPoint REAL",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearEndPoint REAL",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearMarker TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearHorizontalOffset REAL",
                "ALTER TABLE maintenance_notifications ADD COLUMN linearVerticalOffset REAL"
            )
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25)
}

/** Tiny helper so migration SQL reads a little cleaner. */
internal fun SupportSQLiteDatabase.exec(vararg statements: String) {
    statements.forEach { execSQL(it) }
}

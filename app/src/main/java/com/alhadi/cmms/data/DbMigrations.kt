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
     * All migrations, in order. Append new `Migration` objects here as the schema evolves.
     */
    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25)
}

/** Tiny helper so migration SQL reads a little cleaner. */
internal fun SupportSQLiteDatabase.exec(vararg statements: String) {
    statements.forEach { execSQL(it) }
}

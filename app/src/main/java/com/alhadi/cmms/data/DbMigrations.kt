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

    /** Adds structured component-list headers, alternatives, hierarchy and item controls. */
    val MIGRATION_25_26 = object : Migration(25, 26) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN constructionType TEXT NOT NULL DEFAULT ''",
                "CREATE TABLE IF NOT EXISTS asset_bom_headers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, assetId INTEGER, code TEXT NOT NULL, name TEXT NOT NULL, category TEXT NOT NULL DEFAULT 'Asset', usage TEXT NOT NULL DEFAULT 'Maintenance', alternative TEXT NOT NULL DEFAULT '01', status TEXT NOT NULL DEFAULT 'Active', validFrom TEXT NOT NULL DEFAULT '', validTo TEXT NOT NULL DEFAULT '', revision TEXT NOT NULL DEFAULT '', assignmentType TEXT NOT NULL DEFAULT 'Direct', constructionType TEXT NOT NULL DEFAULT '', description TEXT NOT NULL DEFAULT '')",
                "CREATE INDEX IF NOT EXISTS index_asset_bom_headers_assetId ON asset_bom_headers(assetId)",
                "CREATE INDEX IF NOT EXISTS index_asset_bom_headers_constructionType ON asset_bom_headers(constructionType)",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_asset_bom_headers_code_alternative ON asset_bom_headers(code, alternative)",
                "ALTER TABLE asset_bom_items ADD COLUMN headerId INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE asset_bom_items ADD COLUMN itemNumber INTEGER NOT NULL DEFAULT 10",
                "ALTER TABLE asset_bom_items ADD COLUMN itemCategory TEXT NOT NULL DEFAULT 'Stock'",
                "ALTER TABLE asset_bom_items ADD COLUMN status TEXT NOT NULL DEFAULT 'Active'",
                "ALTER TABLE asset_bom_items ADD COLUMN validFrom TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_bom_items ADD COLUMN validTo TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_bom_items ADD COLUMN isCritical INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE asset_bom_items ADD COLUMN useInOrders INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE asset_bom_items ADD COLUMN notes TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_bom_items ADD COLUMN parentItemId INTEGER",
                "ALTER TABLE asset_bom_items ADD COLUMN assemblyAssetId INTEGER",
                "ALTER TABLE asset_bom_items ADD COLUMN alternativeGroup TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_bom_items ADD COLUMN isAlternative INTEGER NOT NULL DEFAULT 0",
                "CREATE INDEX IF NOT EXISTS index_asset_bom_items_headerId ON asset_bom_items(headerId)",
                "CREATE INDEX IF NOT EXISTS index_asset_bom_items_parentItemId ON asset_bom_items(parentItemId)",
                "CREATE INDEX IF NOT EXISTS index_asset_bom_items_assemblyAssetId ON asset_bom_items(assemblyAssetId)",
                "INSERT INTO asset_bom_headers (assetId, code, name, category, usage, alternative, status, validFrom, validTo, revision, assignmentType, constructionType, description) SELECT assetId, 'BOM-' || assetId, 'قائمة الصيانة الرئيسية', 'Asset', 'Maintenance', '01', 'Active', '', '', 'A', 'Direct', '', '' FROM asset_bom_items GROUP BY assetId",
                "UPDATE asset_bom_items SET headerId = COALESCE((SELECT h.id FROM asset_bom_headers h WHERE h.assetId = asset_bom_items.assetId AND h.assignmentType = 'Direct' ORDER BY h.id LIMIT 1), 0), itemNumber = id * 10"
            )
        }
    }

    /** Adds serial-number profiles, individual units, stock fields, and movement history. */
    val MIGRATION_26_27 = object : Migration(26, 27) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE spare_parts ADD COLUMN serializationActive INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE spare_parts ADD COLUMN serialProfileId INTEGER",
                "ALTER TABLE assets ADD COLUMN linkedSerialId INTEGER",
                "ALTER TABLE assets ADD COLUMN serializedPartId INTEGER",
                "ALTER TABLE inventory_transactions ADD COLUMN serialNumbers TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE inventory_transactions ADD COLUMN stockType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE inventory_transactions ADD COLUMN storageLocation TEXT NOT NULL DEFAULT ''",
                "CREATE TABLE IF NOT EXISTS serial_number_profiles (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, requireOnReceipt INTEGER NOT NULL DEFAULT 1, requireOnIssue INTEGER NOT NULL DEFAULT 1, autoCreate INTEGER NOT NULL DEFAULT 1, equipmentRequired INTEGER NOT NULL DEFAULT 0, stockCheckMode TEXT NOT NULL DEFAULT 'Block', allowManualStockEdit INTEGER NOT NULL DEFAULT 0, equipmentCategory TEXT NOT NULL DEFAULT '', description TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_serial_number_profiles_code ON serial_number_profiles(code)",
                "CREATE TABLE IF NOT EXISTS serial_numbers (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, partId INTEGER NOT NULL, serialNumber TEXT NOT NULL, profileId INTEGER, assetId INTEGER, currentWorkOrderId INTEGER, status TEXT NOT NULL DEFAULT 'Created', stockType TEXT NOT NULL DEFAULT '', plant TEXT NOT NULL DEFAULT '', storageLocation TEXT NOT NULL DEFAULT '', batch TEXT NOT NULL DEFAULT '', vendor TEXT NOT NULL DEFAULT '', customer TEXT NOT NULL DEFAULT '', salesOrder TEXT NOT NULL DEFAULT '', specialStock TEXT NOT NULL DEFAULT '', createdAt TEXT NOT NULL DEFAULT '', lastMovementAt TEXT NOT NULL DEFAULT '', notes TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_serial_numbers_partId_serialNumber ON serial_numbers(partId, serialNumber)",
                "CREATE INDEX IF NOT EXISTS index_serial_numbers_profileId ON serial_numbers(profileId)",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_serial_numbers_assetId ON serial_numbers(assetId)",
                "CREATE INDEX IF NOT EXISTS index_serial_numbers_currentWorkOrderId ON serial_numbers(currentWorkOrderId)",
                "CREATE INDEX IF NOT EXISTS index_serial_numbers_status ON serial_numbers(status)",
                "CREATE INDEX IF NOT EXISTS index_serial_numbers_storageLocation ON serial_numbers(storageLocation)",
                "CREATE TABLE IF NOT EXISTS serial_number_movements (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, serialId INTEGER NOT NULL, partId INTEGER NOT NULL, workOrderId INTEGER, movementType TEXT NOT NULL, fromStatus TEXT NOT NULL DEFAULT '', toStatus TEXT NOT NULL DEFAULT '', fromPlant TEXT NOT NULL DEFAULT '', toPlant TEXT NOT NULL DEFAULT '', fromStorageLocation TEXT NOT NULL DEFAULT '', toStorageLocation TEXT NOT NULL DEFAULT '', fromStockType TEXT NOT NULL DEFAULT '', toStockType TEXT NOT NULL DEFAULT '', createdAt TEXT NOT NULL, createdBy TEXT NOT NULL, note TEXT NOT NULL DEFAULT '')",
                "CREATE INDEX IF NOT EXISTS index_serial_number_movements_serialId ON serial_number_movements(serialId)",
                "CREATE INDEX IF NOT EXISTS index_serial_number_movements_partId ON serial_number_movements(partId)",
                "CREATE INDEX IF NOT EXISTS index_serial_number_movements_workOrderId ON serial_number_movements(workOrderId)",
                "CREATE INDEX IF NOT EXISTS index_serial_number_movements_createdAt ON serial_number_movements(createdAt)"
            )
        }
    }

    /** Completes asset identity governance: secondary identifiers, classification,
     *  organizational hierarchy, safety/compliance, and financial valuation. */
    val MIGRATION_27_28 = object : Migration(27, 28) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN longDescription TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN alternativeLabel TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN externalAssetCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN legacyAssetCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN barcode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN qrCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN equipmentCategory TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN assetClass TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN assetSubclass TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN company TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN site TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN safetyCritical INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN riskLevel TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN requiredPermits TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN safetyInstructions TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN ppeRequired TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN isolationRequired INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN complianceRequirements TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN financialStatus TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN bookValue REAL NOT NULL DEFAULT 0.0",
                "ALTER TABLE assets ADD COLUMN capitalizationAt TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    /** Adds the standalone warehouses (stores) master table. */
    val MIGRATION_28_29 = object : Migration(28, 29) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "CREATE TABLE IF NOT EXISTS warehouses (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, location TEXT NOT NULL DEFAULT '', keeper TEXT NOT NULL DEFAULT '', phone TEXT NOT NULL DEFAULT '', type TEXT NOT NULL DEFAULT 'Main', status TEXT NOT NULL DEFAULT 'Active', notes TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_warehouses_code ON warehouses(code)"
            )
        }
    }

    /** Adds manufacturing & technical specification (nameplate) fields to assets. */
    val MIGRATION_29_30 = object : Migration(29, 30) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN countryOfOrigin TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN nameplateData TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN capacity TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN power TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN voltage TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN current TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN frequency TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN speed TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN pressure TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN flowRate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN temperatureRange TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN weight TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN dimensions TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN material TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN designStandard TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN technicalSpecGroup TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN requiresSerialTracking INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    /** Adds warranty governance fields (AST-WAR-*) to assets. */
    val MIGRATION_30_31 = object : Migration(30, 31) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN warrantyType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN warrantyCategory TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN warrantyTerms TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN coveredServices TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN excludedServices TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN warrantyCounterType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN warrantyCounterLimit REAL NOT NULL DEFAULT 0.0",
                "ALTER TABLE assets ADD COLUMN warrantyClaimRequired INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN warrantyClaimStatus TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN warrantyContact TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN warrantyDocument TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE assets ADD COLUMN vendorWarranty INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN manufacturerWarranty INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN customerWarranty INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE assets ADD COLUMN warrantyReference TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    /** Adds work-order warranty governance fields (AST-WAR-008..010). */
    val MIGRATION_31_32 = object : Migration(31, 32) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE work_orders ADD COLUMN repairType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE work_orders ADD COLUMN warrantyReviewed INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE work_orders ADD COLUMN warrantyReviewResult TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    /** Adds organizational governance fields (AST-ORG-*) to assets. */
    val MIGRATION_32_33 = object : Migration(32, 33) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN mobility TEXT NOT NULL DEFAULT 'Fixed'",
                "ALTER TABLE assets ADD COLUMN incursOperatingCost INTEGER NOT NULL DEFAULT 0"
            )
        }
    }

    /** Adds the generic organizational-units master table (Company/Plant/Work Center/Cost Center/Planner Group/Department). */
    val MIGRATION_33_34 = object : Migration(33, 34) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "CREATE TABLE IF NOT EXISTS org_units (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, type TEXT NOT NULL DEFAULT 'WorkCenter', code TEXT NOT NULL, name TEXT NOT NULL, status TEXT NOT NULL DEFAULT 'Active', parentId INTEGER, notes TEXT NOT NULL DEFAULT '')",
                "CREATE UNIQUE INDEX IF NOT EXISTS index_org_units_type_code ON org_units(type, code)"
            )
        }
    }

    /** Adds organizational-unit references (plant/work center/cost center/planner group) to functional locations. */
    val MIGRATION_34_35 = object : Migration(34, 35) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE functional_locations ADD COLUMN plantCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN workCenterCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN costCenterCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN plannerGroupCode TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    /** Adds the org-inheritance override reason (AST-ORG-SAVE-012) to assets. */
    val MIGRATION_35_36 = object : Migration(35, 36) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec("ALTER TABLE assets ADD COLUMN orgOverrideReason TEXT NOT NULL DEFAULT ''")
        }
    }

    /** Adds the full organizational-governance fields (companies/sites/work centers/...) to org_units. */
    val MIGRATION_36_37 = object : Migration(36, 37) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE org_units ADD COLUMN shortName TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN legalName TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN taxNumber TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN commercialRegistration TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN country TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN region TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN city TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN address TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN phone TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN email TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN website TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN latitude REAL",
                "ALTER TABLE org_units ADD COLUMN longitude REAL",
                "ALTER TABLE org_units ADD COLUMN capacity TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN supervisor TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE org_units ADD COLUMN manager TEXT NOT NULL DEFAULT ''"
            )
        }
    }

    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25, MIGRATION_25_26, MIGRATION_26_27, MIGRATION_27_28, MIGRATION_28_29, MIGRATION_29_30, MIGRATION_30_31, MIGRATION_31_32, MIGRATION_32_33, MIGRATION_33_34, MIGRATION_34_35, MIGRATION_35_36, MIGRATION_36_37)
}

/** Tiny helper so migration SQL reads a little cleaner. */
internal fun SupportSQLiteDatabase.exec(vararg statements: String) {
    statements.forEach { execSQL(it) }
}

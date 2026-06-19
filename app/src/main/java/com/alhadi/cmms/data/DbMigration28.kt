package com.alhadi.cmms.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

object DbMigration28 {
    val MIGRATION_27_28 = object : Migration(27, 28) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE asset_bom_items ADD COLUMN revisionId INTEGER",
                "ALTER TABLE asset_bom_items ADD COLUMN position TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_bom_items ADD COLUMN componentType TEXT NOT NULL DEFAULT 'SparePart'",
                "ALTER TABLE asset_bom_items ADD COLUMN quantityPerAsset REAL NOT NULL DEFAULT 1.0",
                "ALTER TABLE asset_bom_items ADD COLUMN unit TEXT NOT NULL DEFAULT 'pcs'",
                "ALTER TABLE asset_bom_items ADD COLUMN isCritical INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE asset_bom_items ADD COLUMN validFrom TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_bom_items ADD COLUMN validTo TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_bom_items ADD COLUMN source TEXT NOT NULL DEFAULT 'Manual'",
                "ALTER TABLE asset_bom_items ADD COLUMN notes TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_bom_items ADD COLUMN isActive INTEGER NOT NULL DEFAULT 1",
                "CREATE INDEX IF NOT EXISTS index_asset_bom_items_revisionId ON asset_bom_items(revisionId)",

                "ALTER TABLE spare_parts ADD COLUMN isSerialized INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE spare_parts ADD COLUMN requiresBatch INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE spare_parts ADD COLUMN manufacturerPartNumber TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE spare_parts ADD COLUMN valuationClass TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE spare_parts ADD COLUMN currency TEXT NOT NULL DEFAULT 'SAR'",
                "ALTER TABLE spare_parts ADD COLUMN standardPrice REAL NOT NULL DEFAULT 0.0",
                "ALTER TABLE spare_parts ADD COLUMN lastCountedAt TEXT NOT NULL DEFAULT ''",

                "ALTER TABLE inventory_transactions ADD COLUMN serializedItemId INTEGER",
                "ALTER TABLE inventory_transactions ADD COLUMN batchNumber TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE inventory_transactions ADD COLUMN referenceNumber TEXT NOT NULL DEFAULT ''",
                "CREATE INDEX IF NOT EXISTS index_inventory_transactions_serializedItemId ON inventory_transactions(serializedItemId)",

                "ALTER TABLE functional_locations ADD COLUMN inheritFromParent INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE functional_locations ADD COLUMN inheritFromReference INTEGER NOT NULL DEFAULT 1",
                "ALTER TABLE functional_locations ADD COLUMN defaultOwnerDepartment TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN defaultPlanningGroup TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN defaultCriticality TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN defaultAssetType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE functional_locations ADD COLUMN defaultMaintenanceStrategy TEXT NOT NULL DEFAULT ''",
                "CREATE INDEX IF NOT EXISTS index_functional_locations_referenceLocationId ON functional_locations(referenceLocationId)"
            )

            db.execSQL("CREATE TABLE IF NOT EXISTS asset_bom_revisions (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, assetId INTEGER NOT NULL, revisionCode TEXT NOT NULL, status TEXT NOT NULL, effectiveFrom TEXT NOT NULL, effectiveTo TEXT NOT NULL, changeReason TEXT NOT NULL, approvedBy TEXT NOT NULL, approvedAt TEXT NOT NULL, createdBy TEXT NOT NULL, createdAt TEXT NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asset_bom_revisions_assetId ON asset_bom_revisions(assetId)")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_asset_bom_revisions_assetId_revisionCode ON asset_bom_revisions(assetId, revisionCode)")

            db.execSQL("CREATE TABLE IF NOT EXISTS bom_alternatives (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, bomItemId INTEGER NOT NULL, alternatePartId INTEGER NOT NULL, priority INTEGER NOT NULL, interchangeability TEXT NOT NULL, validFrom TEXT NOT NULL, validTo TEXT NOT NULL, notes TEXT NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_bom_alternatives_bomItemId ON bom_alternatives(bomItemId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_bom_alternatives_alternatePartId ON bom_alternatives(alternatePartId)")

            db.execSQL("CREATE TABLE IF NOT EXISTS serialized_items (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, serialNumber TEXT NOT NULL, itemType TEXT NOT NULL, assetId INTEGER, partId INTEGER, currentAssetId INTEGER, status TEXT NOT NULL, currentLocation TEXT NOT NULL, batchNumber TEXT NOT NULL, manufacturerSerial TEXT NOT NULL, installedAt TEXT NOT NULL, removedAt TEXT NOT NULL, warrantyEnd TEXT NOT NULL, currentWorkOrderId INTEGER, notes TEXT NOT NULL, createdAt TEXT NOT NULL, updatedAt TEXT NOT NULL)")
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_serialized_items_serialNumber ON serialized_items(serialNumber)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_serialized_items_assetId ON serialized_items(assetId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_serialized_items_partId ON serialized_items(partId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_serialized_items_currentAssetId ON serialized_items(currentAssetId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_serialized_items_status ON serialized_items(status)")

            db.execSQL("CREATE TABLE IF NOT EXISTS serial_movements (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, serializedItemId INTEGER NOT NULL, movementType TEXT NOT NULL, fromAssetId INTEGER, toAssetId INTEGER, fromLocation TEXT NOT NULL, toLocation TEXT NOT NULL, workOrderId INTEGER, occurredAt TEXT NOT NULL, performedBy TEXT NOT NULL, note TEXT NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_serial_movements_serializedItemId ON serial_movements(serializedItemId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_serial_movements_workOrderId ON serial_movements(workOrderId)")

            db.execSQL("CREATE TABLE IF NOT EXISTS asset_financial_records (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, assetId INTEGER NOT NULL, fixedAssetNumber TEXT NOT NULL, companyCode TEXT NOT NULL, ledger TEXT NOT NULL, costCenter TEXT NOT NULL, capitalizationDate TEXT NOT NULL, acquisitionValue REAL NOT NULL, currency TEXT NOT NULL, depreciationMethod TEXT NOT NULL, usefulLifeMonths INTEGER NOT NULL, accumulatedDepreciation REAL NOT NULL, netBookValue REAL NOT NULL, sourceSystem TEXT NOT NULL, syncStatus TEXT NOT NULL, lastSyncedAt TEXT NOT NULL, notes TEXT NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asset_financial_records_assetId ON asset_financial_records(assetId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asset_financial_records_fixedAssetNumber ON asset_financial_records(fixedAssetNumber)")

            db.execSQL("CREATE TABLE IF NOT EXISTS financial_postings (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, assetId INTEGER NOT NULL, workOrderId INTEGER, purchaseOrderId INTEGER, costCategory TEXT NOT NULL, amount REAL NOT NULL, currency TEXT NOT NULL, externalDocumentNumber TEXT NOT NULL, postingDate TEXT NOT NULL, sourceSystem TEXT NOT NULL, status TEXT NOT NULL, notes TEXT NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_financial_postings_assetId ON financial_postings(assetId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_financial_postings_workOrderId ON financial_postings(workOrderId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_financial_postings_purchaseOrderId ON financial_postings(purchaseOrderId)")

            db.execSQL("CREATE TABLE IF NOT EXISTS import_batches (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, fileName TEXT NOT NULL, sourceType TEXT NOT NULL, status TEXT NOT NULL, totalRows INTEGER NOT NULL, acceptedRows INTEGER NOT NULL, rejectedRows INTEGER NOT NULL, startedAt TEXT NOT NULL, completedAt TEXT NOT NULL, actor TEXT NOT NULL, checksum TEXT NOT NULL, summary TEXT NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_import_batches_status ON import_batches(status)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_import_batches_startedAt ON import_batches(startedAt)")

            db.execSQL("CREATE TABLE IF NOT EXISTS import_issues (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, batchId INTEGER NOT NULL, sheetName TEXT NOT NULL, rowNumber INTEGER NOT NULL, fieldName TEXT NOT NULL, severity TEXT NOT NULL, code TEXT NOT NULL, message TEXT NOT NULL, rawValue TEXT NOT NULL, status TEXT NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_import_issues_batchId ON import_issues(batchId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_import_issues_severity ON import_issues(severity)")

            db.execSQL("CREATE TABLE IF NOT EXISTS data_quality_issues (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, ruleCode TEXT NOT NULL, severity TEXT NOT NULL, entityType TEXT NOT NULL, entityId INTEGER, fieldName TEXT NOT NULL, message TEXT NOT NULL, status TEXT NOT NULL, detectedAt TEXT NOT NULL, resolvedAt TEXT NOT NULL, resolvedBy TEXT NOT NULL)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_data_quality_issues_status ON data_quality_issues(status)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_data_quality_issues_severity ON data_quality_issues(severity)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_data_quality_issues_entityType_entityId ON data_quality_issues(entityType, entityId)")
        }
    }
}

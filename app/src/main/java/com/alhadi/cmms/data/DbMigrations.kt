package com.alhadi.cmms.data

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/** Explicit, non-destructive Room migrations for real maintenance data. */
object DbMigrations {
    val MIGRATION_22_23 = object : Migration(22, 23) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `trash` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                    "`entityType` TEXT NOT NULL, `entityId` INTEGER NOT NULL, " +
                    "`label` TEXT NOT NULL, `payload` TEXT NOT NULL, " +
                    "`deletedAt` TEXT NOT NULL, `deletedBy` TEXT NOT NULL)"
            )
        }
    }

    val MIGRATION_23_24 = object : Migration(23, 24) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `purchase_orders` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `number` TEXT NOT NULL, " +
                    "`status` TEXT NOT NULL, `partId` INTEGER, `itemName` TEXT NOT NULL, " +
                    "`quantity` INTEGER NOT NULL, `unitPrice` REAL NOT NULL, `supplier` TEXT NOT NULL, " +
                    "`workOrderId` INTEGER, `requestedBy` TEXT NOT NULL, `createdAt` TEXT NOT NULL, " +
                    "`neededBy` TEXT NOT NULL, `receivedAt` TEXT NOT NULL, `notes` TEXT NOT NULL)"
            )
        }
    }

    val MIGRATION_24_25 = object : Migration(24, 25) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.execSQL(
                "CREATE TABLE IF NOT EXISTS `suppliers` (" +
                    "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, " +
                    "`contactPerson` TEXT NOT NULL, `phone` TEXT NOT NULL, `email` TEXT NOT NULL, " +
                    "`address` TEXT NOT NULL, `notes` TEXT NOT NULL)"
            )
        }
    }

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

    /** Stages 16-20: partners, warranty, classification, documents, and measurements. */
    val MIGRATION_26_27 = object : Migration(26, 27) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "ALTER TABLE assets ADD COLUMN assetClassId INTEGER",
                "CREATE INDEX IF NOT EXISTS index_assets_assetClassId ON assets(assetClassId)",

                "ALTER TABLE asset_characteristics ADD COLUMN characteristicCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN dataType TEXT NOT NULL DEFAULT 'Text'",
                "ALTER TABLE asset_characteristics ADD COLUMN isRequired INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE asset_characteristics ADD COLUMN allowedValues TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN source TEXT NOT NULL DEFAULT 'Manual'",
                "ALTER TABLE asset_characteristics ADD COLUMN isInherited INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE asset_characteristics ADD COLUMN changedAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN changedBy TEXT NOT NULL DEFAULT ''",
                "CREATE INDEX IF NOT EXISTS index_asset_characteristics_characteristicCode ON asset_characteristics(characteristicCode)",

                "ALTER TABLE asset_documents ADD COLUMN description TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN version TEXT NOT NULL DEFAULT '1.0'",
                "ALTER TABLE asset_documents ADD COLUMN status TEXT NOT NULL DEFAULT 'Current'",
                "ALTER TABLE asset_documents ADD COLUMN documentDate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN expiryDate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN supersedesDocumentId INTEGER",
                "ALTER TABLE asset_documents ADD COLUMN categoryCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN checksum TEXT NOT NULL DEFAULT ''",
                "CREATE INDEX IF NOT EXISTS index_asset_documents_supersedesDocumentId ON asset_documents(supersedesDocumentId)",

                "ALTER TABLE measuring_points ADD COLUMN pointCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measuring_points ADD COLUMN measurementType TEXT NOT NULL DEFAULT 'Measurement'",
                "ALTER TABLE measuring_points ADD COLUMN lowerLimit REAL",
                "ALTER TABLE measuring_points ADD COLUMN lowerWarningLimit REAL",
                "ALTER TABLE measuring_points ADD COLUMN upperWarningLimit REAL",
                "ALTER TABLE measuring_points ADD COLUMN status TEXT NOT NULL DEFAULT 'Active'",
                "ALTER TABLE measuring_points ADD COLUMN functionalLocationId INTEGER",
                "ALTER TABLE measuring_points ADD COLUMN sourceType TEXT NOT NULL DEFAULT 'Manual'",
                "ALTER TABLE measuring_points ADD COLUMN meterSerial TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measuring_points ADD COLUMN createdAt TEXT NOT NULL DEFAULT ''",
                "CREATE INDEX IF NOT EXISTS index_measuring_points_pointCode ON measuring_points(pointCode)",

                "ALTER TABLE measurement_readings ADD COLUMN readingUnit TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measurement_readings ADD COLUMN referenceTime TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measurement_readings ADD COLUMN source TEXT NOT NULL DEFAULT 'Manual'",
                "ALTER TABLE measurement_readings ADD COLUMN processingStatus TEXT NOT NULL DEFAULT 'Accepted'",
                "ALTER TABLE measurement_readings ADD COLUMN additionalInfo TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measurement_readings ADD COLUMN resultData TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measurement_readings ADD COLUMN actionRequired INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE measurement_readings ADD COLUMN correctedFromReadingId INTEGER",
                "CREATE INDEX IF NOT EXISTS index_measurement_readings_referenceTime ON measurement_readings(referenceTime)"
            )

            db.execSQL(
                "CREATE TABLE IF NOT EXISTS asset_partners (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, assetId INTEGER NOT NULL, " +
                    "partnerRole TEXT NOT NULL, partnerName TEXT NOT NULL, organization TEXT NOT NULL, " +
                    "phone TEXT NOT NULL, email TEXT NOT NULL, validFrom TEXT NOT NULL, validTo TEXT NOT NULL, " +
                    "isPrimary INTEGER NOT NULL, notes TEXT NOT NULL, createdBy TEXT NOT NULL, createdAt TEXT NOT NULL)"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asset_partners_assetId ON asset_partners(assetId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asset_partners_partnerRole ON asset_partners(partnerRole)")

            db.execSQL(
                "CREATE TABLE IF NOT EXISTS asset_warranties (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, assetId INTEGER NOT NULL, " +
                    "warrantyType TEXT NOT NULL, provider TEXT NOT NULL, startDate TEXT NOT NULL, endDate TEXT NOT NULL, " +
                    "counterLimit REAL, counterUnit TEXT NOT NULL, terms TEXT NOT NULL, coveredServices TEXT NOT NULL, " +
                    "excludedServices TEXT NOT NULL, documentId INTEGER, claimContact TEXT NOT NULL, status TEXT NOT NULL, " +
                    "createdBy TEXT NOT NULL, createdAt TEXT NOT NULL)"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asset_warranties_assetId ON asset_warranties(assetId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asset_warranties_status ON asset_warranties(status)")

            db.execSQL(
                "CREATE TABLE IF NOT EXISTS warranty_claims (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, warrantyId INTEGER NOT NULL, assetId INTEGER NOT NULL, " +
                    "claimNumber TEXT NOT NULL, status TEXT NOT NULL, openedAt TEXT NOT NULL, closedAt TEXT NOT NULL, " +
                    "description TEXT NOT NULL, contact TEXT NOT NULL, estimatedValue REAL NOT NULL, approvedValue REAL NOT NULL, " +
                    "linkedWorkOrderId INTEGER, resolution TEXT NOT NULL, createdBy TEXT NOT NULL)"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_warranty_claims_warrantyId ON warranty_claims(warrantyId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_warranty_claims_assetId ON warranty_claims(assetId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_warranty_claims_status ON warranty_claims(status)")

            db.execSQL(
                "CREATE TABLE IF NOT EXISTS asset_classes (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, code TEXT NOT NULL, name TEXT NOT NULL, " +
                    "description TEXT NOT NULL, assetType TEXT NOT NULL, isActive INTEGER NOT NULL, " +
                    "createdAt TEXT NOT NULL, updatedAt TEXT NOT NULL)"
            )
            db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_asset_classes_code ON asset_classes(code)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_asset_classes_assetType ON asset_classes(assetType)")

            db.execSQL(
                "CREATE TABLE IF NOT EXISTS meter_replacements (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, pointId INTEGER NOT NULL, assetId INTEGER NOT NULL, " +
                    "oldMeterSerial TEXT NOT NULL, newMeterSerial TEXT NOT NULL, finalOldReading REAL NOT NULL, " +
                    "initialNewReading REAL NOT NULL, replacedAt TEXT NOT NULL, replacedBy TEXT NOT NULL, reason TEXT NOT NULL)"
            )
            db.execSQL("CREATE INDEX IF NOT EXISTS index_meter_replacements_pointId ON meter_replacements(pointId)")
            db.execSQL("CREATE INDEX IF NOT EXISTS index_meter_replacements_assetId ON meter_replacements(assetId)")
        }
    }

    val ALL: Array<Migration> = arrayOf(
        MIGRATION_22_23,
        MIGRATION_23_24,
        MIGRATION_24_25,
        MIGRATION_25_26,
        MIGRATION_26_27
    )
}

internal fun SupportSQLiteDatabase.exec(vararg statements: String) {
    statements.forEach { execSQL(it) }
}

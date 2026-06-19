from pathlib import Path

ROOT = Path('.')


def replace_function(text: str, signature: str, replacement: str) -> str:
    start = text.index(signature)
    brace = text.index('{', start)
    depth = 0
    i = brace
    while i < len(text):
        if text[i] == '{':
            depth += 1
        elif text[i] == '}':
            depth -= 1
            if depth == 0:
                return text[:start] + replacement + text[i + 1:]
        i += 1
    raise RuntimeError(f'Unable to locate end of {signature}')


def insert_before(text: str, marker: str, addition: str) -> str:
    if addition.strip() in text:
        return text
    return text.replace(marker, addition + marker, 1)


def write(path: str, content: str) -> None:
    target = ROOT / path
    target.parent.mkdir(parents=True, exist_ok=True)
    target.write_text(content, encoding='utf-8')


def write_new_entities_and_daos() -> None:
    write('app/src/main/java/com/alhadi/cmms/data/entity/AssetPartnerEntity.kt', '''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "asset_partners",
    indices = [Index(value = ["assetId"]), Index(value = ["assetId", "partnerRole"])]
)
@Serializable
data class AssetPartnerEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetId: Long,
    val partnerRole: String,
    val partnerName: String,
    val organization: String = "",
    val contactPerson: String = "",
    val phone: String = "",
    val email: String = "",
    val validFrom: String = "",
    val validTo: String = "",
    @ColumnInfo(defaultValue = "0") val isPrimary: Boolean = false,
    @ColumnInfo(defaultValue = "0") val inheritedFromLocation: Boolean = false,
    val notes: String = "",
    val createdAt: String = "",
    val createdBy: String = ""
)
''')
    write('app/src/main/java/com/alhadi/cmms/data/entity/AssetWarrantyEntity.kt', '''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "asset_warranties", indices = [Index(value = ["assetId"])])
@Serializable
data class AssetWarrantyEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetId: Long,
    val warrantyType: String = "Standard",
    val provider: String,
    val startDate: String = "",
    val endDate: String = "",
    val terms: String = "",
    @ColumnInfo(defaultValue = "0") val claimRequired: Boolean = false,
    val claimContact: String = "",
    val coveredServices: String = "",
    val excludedServices: String = "",
    val counterLimit: Double? = null,
    val counterUnit: String = "",
    val status: String = "Active",
    val documentId: Long? = null,
    val createdAt: String = "",
    val createdBy: String = ""
) {
    fun isActiveOn(date: String): Boolean = status == "Active" &&
        (startDate.isBlank() || date >= startDate) &&
        (endDate.isBlank() || date <= endDate)
}
''')
    write('app/src/main/java/com/alhadi/cmms/data/entity/WarrantyClaimEntity.kt', '''package com.alhadi.cmms.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "warranty_claims",
    indices = [Index(value = ["warrantyId"]), Index(value = ["assetId"]), Index(value = ["claimNumber"], unique = true)]
)
@Serializable
data class WarrantyClaimEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val warrantyId: Long,
    val assetId: Long,
    val claimNumber: String,
    val claimDate: String,
    val status: String = "Draft",
    val description: String,
    val claimedAmount: Double = 0.0,
    val resolution: String = "",
    val documentId: Long? = null,
    val createdBy: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)
''')
    write('app/src/main/java/com/alhadi/cmms/data/entity/AssetClassificationEntity.kt', '''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(
    tableName = "asset_classifications",
    indices = [Index(value = ["assetId"]), Index(value = ["assetId", "classCode"], unique = true)]
)
@Serializable
data class AssetClassificationEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetId: Long,
    val classCode: String,
    val className: String,
    @ColumnInfo(defaultValue = "0") val isPrimary: Boolean = false,
    @ColumnInfo(defaultValue = "0") val isInherited: Boolean = false,
    val source: String = "Manual",
    val assignedAt: String = "",
    val assignedBy: String = ""
)
''')

    write('app/src/main/java/com/alhadi/cmms/data/dao/AssetPartnerDao.kt', '''package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetPartnerEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetPartnerDao {
    @Query("SELECT * FROM asset_partners ORDER BY assetId, isPrimary DESC, partnerRole, partnerName")
    fun observeAll(): Flow<List<AssetPartnerEntity>>
    @Query("SELECT * FROM asset_partners ORDER BY id") suspend fun dumpAll(): List<AssetPartnerEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: AssetPartnerEntity): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(items: List<AssetPartnerEntity>)
    @Query("DELETE FROM asset_partners WHERE id = :id") suspend fun deleteById(id: Long)
    @Query("DELETE FROM asset_partners") suspend fun deleteAll()
}
''')
    write('app/src/main/java/com/alhadi/cmms/data/dao/AssetWarrantyDao.kt', '''package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetWarrantyEntity
import com.alhadi.cmms.data.entity.WarrantyClaimEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetWarrantyDao {
    @Query("SELECT * FROM asset_warranties ORDER BY assetId, endDate DESC") fun observeWarranties(): Flow<List<AssetWarrantyEntity>>
    @Query("SELECT * FROM warranty_claims ORDER BY claimDate DESC, id DESC") fun observeClaims(): Flow<List<WarrantyClaimEntity>>
    @Query("SELECT * FROM asset_warranties ORDER BY id") suspend fun dumpWarranties(): List<AssetWarrantyEntity>
    @Query("SELECT * FROM warranty_claims ORDER BY id") suspend fun dumpClaims(): List<WarrantyClaimEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertWarranty(item: AssetWarrantyEntity): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertClaim(item: WarrantyClaimEntity): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertWarranties(items: List<AssetWarrantyEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertClaims(items: List<WarrantyClaimEntity>)
    @Query("DELETE FROM asset_warranties WHERE id = :id") suspend fun deleteWarranty(id: Long)
    @Query("DELETE FROM warranty_claims WHERE id = :id") suspend fun deleteClaim(id: Long)
    @Query("DELETE FROM warranty_claims") suspend fun deleteAllClaims()
    @Query("DELETE FROM asset_warranties") suspend fun deleteAllWarranties()
    @Query("SELECT COUNT(*) FROM warranty_claims") suspend fun claimCount(): Int
}
''')
    write('app/src/main/java/com/alhadi/cmms/data/dao/AssetClassificationDao.kt', '''package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.alhadi.cmms.data.entity.AssetClassificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetClassificationDao {
    @Query("SELECT * FROM asset_classifications ORDER BY assetId, isPrimary DESC, classCode")
    fun observeAll(): Flow<List<AssetClassificationEntity>>
    @Query("SELECT * FROM asset_classifications ORDER BY id") suspend fun dumpAll(): List<AssetClassificationEntity>
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insert(item: AssetClassificationEntity): Long
    @Insert(onConflict = OnConflictStrategy.REPLACE) suspend fun insertAll(items: List<AssetClassificationEntity>)
    @Query("DELETE FROM asset_classifications WHERE id = :id") suspend fun deleteById(id: Long)
    @Query("DELETE FROM asset_classifications") suspend fun deleteAll()
}
''')


def patch_existing_entities() -> None:
    write('app/src/main/java/com/alhadi/cmms/data/entity/AssetCharacteristicEntity.kt', '''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "asset_characteristics", indices = [Index(value = ["assetId"])])
@Serializable
data class AssetCharacteristicEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetId: Long,
    val name: String,
    val value: String,
    val unit: String = "",
    @ColumnInfo(defaultValue = "''") val characteristicCode: String = "",
    @ColumnInfo(defaultValue = "''") val classCode: String = "",
    @ColumnInfo(defaultValue = "'Text'") val dataType: String = "Text",
    @ColumnInfo(defaultValue = "''") val allowedValues: String = "",
    @ColumnInfo(defaultValue = "0") val isMandatory: Boolean = false,
    @ColumnInfo(defaultValue = "0") val isInherited: Boolean = false,
    @ColumnInfo(defaultValue = "'Manual'") val source: String = "Manual",
    @ColumnInfo(defaultValue = "''") val changedAt: String = "",
    @ColumnInfo(defaultValue = "''") val changedBy: String = ""
)
''')
    write('app/src/main/java/com/alhadi/cmms/data/entity/AssetDocumentEntity.kt', '''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "asset_documents", indices = [Index(value = ["assetId"])])
@Serializable
data class AssetDocumentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetId: Long,
    val type: String,
    val title: String,
    val reference: String,
    val uploadedBy: String,
    val uploadedAt: String,
    @ColumnInfo(defaultValue = "''") val description: String = "",
    @ColumnInfo(defaultValue = "'1.0'") val version: String = "1.0",
    @ColumnInfo(defaultValue = "'Current'") val status: String = "Current",
    @ColumnInfo(defaultValue = "''") val documentDate: String = "",
    @ColumnInfo(defaultValue = "''") val expiryDate: String = "",
    val supersedesDocumentId: Long? = null,
    @ColumnInfo(defaultValue = "''") val fileName: String = "",
    @ColumnInfo(defaultValue = "''") val mimeType: String = ""
)
''')
    write('app/src/main/java/com/alhadi/cmms/data/entity/MeasuringPointEntity.kt', '''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "measuring_points", indices = [Index(value = ["assetId"])])
@Serializable
data class MeasuringPointEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val assetId: Long,
    val name: String,
    val unit: String,
    val isCounter: Boolean,
    val upperLimit: Double? = null,
    val lastReading: Double = 0.0,
    val lastReadingAt: String = "",
    @ColumnInfo(defaultValue = "''") val pointCode: String = "",
    @ColumnInfo(defaultValue = "'Condition'") val measurementType: String = "Condition",
    val lowerLimit: Double? = null,
    val lowerWarningLimit: Double? = null,
    val upperWarningLimit: Double? = null,
    @ColumnInfo(defaultValue = "'Active'") val status: String = "Active",
    @ColumnInfo(defaultValue = "'Manual'") val source: String = "Manual",
    val functionalLocationId: Long? = null
)
''')
    write('app/src/main/java/com/alhadi/cmms/data/entity/MeasurementReadingEntity.kt', '''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

@Entity(tableName = "measurement_readings", indices = [Index(value = ["pointId"]), Index(value = ["assetId"])])
@Serializable
data class MeasurementReadingEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val pointId: Long,
    val assetId: Long,
    val value: Double,
    val createdAt: String,
    val createdBy: String,
    val note: String = "",
    @ColumnInfo(defaultValue = "''") val readingUnit: String = "",
    @ColumnInfo(defaultValue = "''") val referenceTime: String = "",
    @ColumnInfo(defaultValue = "'Manual'") val source: String = "Manual",
    @ColumnInfo(defaultValue = "'Accepted'") val processingStatus: String = "Accepted",
    @ColumnInfo(defaultValue = "''") val additionalInfo: String = "",
    @ColumnInfo(defaultValue = "''") val resultData: String = "",
    @ColumnInfo(defaultValue = "0") val actionRequired: Boolean = false,
    val correctedFromReadingId: Long? = null
)
''')


def patch_database() -> None:
    path = ROOT / 'app/src/main/java/com/alhadi/cmms/data/AppDatabase.kt'
    text = path.read_text(encoding='utf-8')
    if 'AssetPartnerEntity::class' in text and 'version = 27' in text:
        return
    imports = [
        ('import com.alhadi.cmms.data.dao.AssetBomDao\n', 'import com.alhadi.cmms.data.dao.AssetBomDao\nimport com.alhadi.cmms.data.dao.AssetClassificationDao\nimport com.alhadi.cmms.data.dao.AssetPartnerDao\nimport com.alhadi.cmms.data.dao.AssetWarrantyDao\n'),
        ('import com.alhadi.cmms.data.entity.AssetBomItemEntity\n', 'import com.alhadi.cmms.data.entity.AssetBomItemEntity\nimport com.alhadi.cmms.data.entity.AssetClassificationEntity\nimport com.alhadi.cmms.data.entity.AssetPartnerEntity\nimport com.alhadi.cmms.data.entity.AssetWarrantyEntity\n'),
        ('import com.alhadi.cmms.data.entity.WorkPermitEntity\n', 'import com.alhadi.cmms.data.entity.WorkPermitEntity\nimport com.alhadi.cmms.data.entity.WarrantyClaimEntity\n'),
    ]
    for old, new in imports:
        text = text.replace(old, new, 1)
    text = text.replace('        SupplierEntity::class\n', '        SupplierEntity::class,\n        AssetPartnerEntity::class,\n        AssetWarrantyEntity::class,\n        WarrantyClaimEntity::class,\n        AssetClassificationEntity::class\n', 1)
    text = text.replace('    version = 26,', '    version = 27,', 1)
    text = text.replace('    abstract fun supplierDao(): SupplierDao\n', '    abstract fun supplierDao(): SupplierDao\n    abstract fun assetPartnerDao(): AssetPartnerDao\n    abstract fun assetWarrantyDao(): AssetWarrantyDao\n    abstract fun assetClassificationDao(): AssetClassificationDao\n', 1)
    path.write_text(text, encoding='utf-8')


def patch_migrations() -> None:
    path = ROOT / 'app/src/main/java/com/alhadi/cmms/data/DbMigrations.kt'
    text = path.read_text(encoding='utf-8')
    if 'MIGRATION_26_27' in text:
        return
    migration = '''
    /** v26 -> v27: partners, warranties, classification, document versions, and advanced measurement metadata. */
    val MIGRATION_26_27 = object : Migration(26, 27) {
        override fun migrate(db: SupportSQLiteDatabase) {
            db.exec(
                "CREATE TABLE IF NOT EXISTS `asset_partners` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `assetId` INTEGER NOT NULL, `partnerRole` TEXT NOT NULL, `partnerName` TEXT NOT NULL, `organization` TEXT NOT NULL, `contactPerson` TEXT NOT NULL, `phone` TEXT NOT NULL, `email` TEXT NOT NULL, `validFrom` TEXT NOT NULL, `validTo` TEXT NOT NULL, `isPrimary` INTEGER NOT NULL DEFAULT 0, `inheritedFromLocation` INTEGER NOT NULL DEFAULT 0, `notes` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `createdBy` TEXT NOT NULL)",
                "CREATE INDEX IF NOT EXISTS `index_asset_partners_assetId` ON `asset_partners` (`assetId`)",
                "CREATE INDEX IF NOT EXISTS `index_asset_partners_assetId_partnerRole` ON `asset_partners` (`assetId`, `partnerRole`)",
                "CREATE TABLE IF NOT EXISTS `asset_warranties` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `assetId` INTEGER NOT NULL, `warrantyType` TEXT NOT NULL, `provider` TEXT NOT NULL, `startDate` TEXT NOT NULL, `endDate` TEXT NOT NULL, `terms` TEXT NOT NULL, `claimRequired` INTEGER NOT NULL DEFAULT 0, `claimContact` TEXT NOT NULL, `coveredServices` TEXT NOT NULL, `excludedServices` TEXT NOT NULL, `counterLimit` REAL, `counterUnit` TEXT NOT NULL, `status` TEXT NOT NULL, `documentId` INTEGER, `createdAt` TEXT NOT NULL, `createdBy` TEXT NOT NULL)",
                "CREATE INDEX IF NOT EXISTS `index_asset_warranties_assetId` ON `asset_warranties` (`assetId`)",
                "CREATE TABLE IF NOT EXISTS `warranty_claims` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `warrantyId` INTEGER NOT NULL, `assetId` INTEGER NOT NULL, `claimNumber` TEXT NOT NULL, `claimDate` TEXT NOT NULL, `status` TEXT NOT NULL, `description` TEXT NOT NULL, `claimedAmount` REAL NOT NULL, `resolution` TEXT NOT NULL, `documentId` INTEGER, `createdBy` TEXT NOT NULL, `createdAt` TEXT NOT NULL, `updatedAt` TEXT NOT NULL)",
                "CREATE INDEX IF NOT EXISTS `index_warranty_claims_warrantyId` ON `warranty_claims` (`warrantyId`)",
                "CREATE INDEX IF NOT EXISTS `index_warranty_claims_assetId` ON `warranty_claims` (`assetId`)",
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_warranty_claims_claimNumber` ON `warranty_claims` (`claimNumber`)",
                "CREATE TABLE IF NOT EXISTS `asset_classifications` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `assetId` INTEGER NOT NULL, `classCode` TEXT NOT NULL, `className` TEXT NOT NULL, `isPrimary` INTEGER NOT NULL DEFAULT 0, `isInherited` INTEGER NOT NULL DEFAULT 0, `source` TEXT NOT NULL, `assignedAt` TEXT NOT NULL, `assignedBy` TEXT NOT NULL)",
                "CREATE INDEX IF NOT EXISTS `index_asset_classifications_assetId` ON `asset_classifications` (`assetId`)",
                "CREATE UNIQUE INDEX IF NOT EXISTS `index_asset_classifications_assetId_classCode` ON `asset_classifications` (`assetId`, `classCode`)",
                "ALTER TABLE asset_characteristics ADD COLUMN characteristicCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN classCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN dataType TEXT NOT NULL DEFAULT 'Text'",
                "ALTER TABLE asset_characteristics ADD COLUMN allowedValues TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN isMandatory INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE asset_characteristics ADD COLUMN isInherited INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE asset_characteristics ADD COLUMN source TEXT NOT NULL DEFAULT 'Manual'",
                "ALTER TABLE asset_characteristics ADD COLUMN changedAt TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_characteristics ADD COLUMN changedBy TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN description TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN version TEXT NOT NULL DEFAULT '1.0'",
                "ALTER TABLE asset_documents ADD COLUMN status TEXT NOT NULL DEFAULT 'Current'",
                "ALTER TABLE asset_documents ADD COLUMN documentDate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN expiryDate TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN supersedesDocumentId INTEGER",
                "ALTER TABLE asset_documents ADD COLUMN fileName TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE asset_documents ADD COLUMN mimeType TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measuring_points ADD COLUMN pointCode TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measuring_points ADD COLUMN measurementType TEXT NOT NULL DEFAULT 'Condition'",
                "ALTER TABLE measuring_points ADD COLUMN lowerLimit REAL",
                "ALTER TABLE measuring_points ADD COLUMN lowerWarningLimit REAL",
                "ALTER TABLE measuring_points ADD COLUMN upperWarningLimit REAL",
                "ALTER TABLE measuring_points ADD COLUMN status TEXT NOT NULL DEFAULT 'Active'",
                "ALTER TABLE measuring_points ADD COLUMN source TEXT NOT NULL DEFAULT 'Manual'",
                "ALTER TABLE measuring_points ADD COLUMN functionalLocationId INTEGER",
                "ALTER TABLE measurement_readings ADD COLUMN readingUnit TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measurement_readings ADD COLUMN referenceTime TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measurement_readings ADD COLUMN source TEXT NOT NULL DEFAULT 'Manual'",
                "ALTER TABLE measurement_readings ADD COLUMN processingStatus TEXT NOT NULL DEFAULT 'Accepted'",
                "ALTER TABLE measurement_readings ADD COLUMN additionalInfo TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measurement_readings ADD COLUMN resultData TEXT NOT NULL DEFAULT ''",
                "ALTER TABLE measurement_readings ADD COLUMN actionRequired INTEGER NOT NULL DEFAULT 0",
                "ALTER TABLE measurement_readings ADD COLUMN correctedFromReadingId INTEGER"
            )
        }
    }

'''
    text = text.replace('    /** All migrations, in order.', migration + '    /** All migrations, in order.', 1)
    text = text.replace('        MIGRATION_25_26\n', '        MIGRATION_25_26,\n        MIGRATION_26_27\n', 1)
    path.write_text(text, encoding='utf-8')


def patch_backup_bundle() -> None:
    path = ROOT / 'app/src/main/java/com/alhadi/cmms/data/BackupBundle.kt'
    text = path.read_text(encoding='utf-8')
    if 'assetPartners: List<AssetPartnerEntity>' in text:
        return
    text = text.replace('import com.alhadi.cmms.data.entity.AssetBomItemEntity\n', 'import com.alhadi.cmms.data.entity.AssetBomItemEntity\nimport com.alhadi.cmms.data.entity.AssetClassificationEntity\nimport com.alhadi.cmms.data.entity.AssetPartnerEntity\nimport com.alhadi.cmms.data.entity.AssetWarrantyEntity\n', 1)
    text = text.replace('import com.alhadi.cmms.data.entity.WorkPermitEntity\n', 'import com.alhadi.cmms.data.entity.WorkPermitEntity\nimport com.alhadi.cmms.data.entity.WarrantyClaimEntity\n', 1)
    text = text.replace('    val suppliers: List<SupplierEntity> = emptyList()\n', '    val suppliers: List<SupplierEntity> = emptyList(),\n    val assetPartners: List<AssetPartnerEntity> = emptyList(),\n    val assetWarranties: List<AssetWarrantyEntity> = emptyList(),\n    val warrantyClaims: List<WarrantyClaimEntity> = emptyList(),\n    val assetClassifications: List<AssetClassificationEntity> = emptyList()\n', 1)
    text = text.replace('            taskLists.size + taskListOperations.size + permits.size + purchaseOrders.size + suppliers.size', '            taskLists.size + taskListOperations.size + permits.size + purchaseOrders.size + suppliers.size +\n            assetPartners.size + assetWarranties.size + warrantyClaims.size + assetClassifications.size', 1)
    path.write_text(text, encoding='utf-8')


def patch_document_dao() -> None:
    path = ROOT / 'app/src/main/java/com/alhadi/cmms/data/dao/AssetDocumentDao.kt'
    text = path.read_text(encoding='utf-8')
    if 'updateStatus' in text:
        return
    marker = '    @Query("DELETE FROM asset_documents WHERE id = :id")'
    addition = '    @Query("SELECT * FROM asset_documents WHERE id = :id LIMIT 1")\n    suspend fun getById(id: Long): AssetDocumentEntity?\n\n    @Query("UPDATE asset_documents SET status = :status WHERE id = :id")\n    suspend fun updateStatus(id: Long, status: String)\n\n'
    text = text.replace(marker, addition + marker, 1)
    path.write_text(text, encoding='utf-8')


def patch_repository() -> None:
    path = ROOT / 'app/src/main/java/com/alhadi/cmms/data/CmmsRepository.kt'
    text = path.read_text(encoding='utf-8')
    if 'val assetPartners:' in text and 'saveAssetPartner' in text:
        return
    text = text.replace('import com.alhadi.cmms.data.entity.AssetBomItemEntity\n', 'import com.alhadi.cmms.data.entity.AssetBomItemEntity\nimport com.alhadi.cmms.data.entity.AssetClassificationEntity\nimport com.alhadi.cmms.data.entity.AssetPartnerEntity\nimport com.alhadi.cmms.data.entity.AssetWarrantyEntity\n', 1)
    text = text.replace('import com.alhadi.cmms.data.entity.WorkPermitEntity\n', 'import com.alhadi.cmms.data.entity.WorkPermitEntity\nimport com.alhadi.cmms.data.entity.WarrantyClaimEntity\n', 1)
    text = text.replace('    private val supplierDao = database.supplierDao()\n', '    private val supplierDao = database.supplierDao()\n    private val partnerDao = database.assetPartnerDao()\n    private val warrantyDao = database.assetWarrantyDao()\n    private val classificationDao = database.assetClassificationDao()\n', 1)
    text = text.replace('    val suppliers: Flow<List<SupplierEntity>> = supplierDao.observeSuppliers()\n', '    val suppliers: Flow<List<SupplierEntity>> = supplierDao.observeSuppliers()\n    val assetPartners: Flow<List<AssetPartnerEntity>> = partnerDao.observeAll()\n    val assetWarranties: Flow<List<AssetWarrantyEntity>> = warrantyDao.observeWarranties()\n    val warrantyClaims: Flow<List<WarrantyClaimEntity>> = warrantyDao.observeClaims()\n    val assetClassifications: Flow<List<AssetClassificationEntity>> = classificationDao.observeAll()\n', 1)
    text = text.replace('            suppliers = supplierDao.dumpAll()\n', '            suppliers = supplierDao.dumpAll(),\n            assetPartners = partnerDao.dumpAll(),\n            assetWarranties = warrantyDao.dumpWarranties(),\n            warrantyClaims = warrantyDao.dumpClaims(),\n            assetClassifications = classificationDao.dumpAll()\n', 1)
    text = text.replace('            supplierDao.deleteAll()\n', '            classificationDao.deleteAll()\n            warrantyDao.deleteAllClaims()\n            warrantyDao.deleteAllWarranties()\n            partnerDao.deleteAll()\n            supplierDao.deleteAll()\n', 1)
    text = text.replace('            supplierDao.insertAll(bundle.suppliers)\n', '            supplierDao.insertAll(bundle.suppliers)\n            partnerDao.insertAll(bundle.assetPartners)\n            warrantyDao.insertWarranties(bundle.assetWarranties)\n            warrantyDao.insertClaims(bundle.warrantyClaims)\n            classificationDao.insertAll(bundle.assetClassifications)\n', 1)

    methods = '''    // ---------------------------------------------------------------------
    // Asset partners, warranties, claims, and classifications (stages 16-18)
    // ---------------------------------------------------------------------

    suspend fun saveAssetPartner(item: AssetPartnerEntity, actor: String = "System") {
        val isNew = item.id == 0L
        partnerDao.insert(item.copy(createdAt = item.createdAt.ifBlank { DateStrings.now() }, createdBy = item.createdBy.ifBlank { actor }))
        recordAudit(if (isNew) "Create" else "Update", "AssetPartner", "${if (isNew) "إضافة" else "تعديل"} شريك ${item.partnerRole}: ${item.partnerName}", actor)
    }

    suspend fun deleteAssetPartner(item: AssetPartnerEntity, actor: String = "System") {
        partnerDao.deleteById(item.id)
        recordAudit("Delete", "AssetPartner", "حذف شريك ${item.partnerRole}: ${item.partnerName}", actor)
    }

    suspend fun saveAssetWarranty(item: AssetWarrantyEntity, actor: String = "System") {
        val isNew = item.id == 0L
        warrantyDao.insertWarranty(item.copy(createdAt = item.createdAt.ifBlank { DateStrings.now() }, createdBy = item.createdBy.ifBlank { actor }))
        recordAudit(if (isNew) "Create" else "Update", "Warranty", "${if (isNew) "إضافة" else "تعديل"} ضمان: ${item.provider}", actor)
    }

    suspend fun deleteAssetWarranty(item: AssetWarrantyEntity, actor: String = "System") {
        warrantyDao.deleteWarranty(item.id)
        recordAudit("Delete", "Warranty", "حذف ضمان: ${item.provider}", actor)
    }

    suspend fun saveWarrantyClaim(item: WarrantyClaimEntity, actor: String = "System") {
        val isNew = item.id == 0L
        val now = DateStrings.now()
        val number = if (isNew && item.claimNumber.isBlank()) "CLM-%05d".format(warrantyDao.claimCount() + 1) else item.claimNumber
        warrantyDao.insertClaim(item.copy(claimNumber = number, createdBy = item.createdBy.ifBlank { actor }, createdAt = item.createdAt.ifBlank { now }, updatedAt = now))
        recordAudit(if (isNew) "Create" else "Update", "WarrantyClaim", "${if (isNew) "إنشاء" else "تعديل"} مطالبة: $number", actor)
    }

    suspend fun deleteWarrantyClaim(item: WarrantyClaimEntity, actor: String = "System") {
        warrantyDao.deleteClaim(item.id)
        recordAudit("Delete", "WarrantyClaim", "حذف مطالبة: ${item.claimNumber}", actor)
    }

    suspend fun saveAssetClassification(item: AssetClassificationEntity, actor: String = "System") {
        val isNew = item.id == 0L
        classificationDao.insert(item.copy(assignedAt = item.assignedAt.ifBlank { DateStrings.now() }, assignedBy = item.assignedBy.ifBlank { actor }))
        recordAudit(if (isNew) "Create" else "Update", "AssetClassification", "${if (isNew) "إسناد" else "تعديل"} تصنيف ${item.classCode}: ${item.className}", actor)
    }

    suspend fun deleteAssetClassification(item: AssetClassificationEntity, actor: String = "System") {
        classificationDao.deleteById(item.id)
        recordAudit("Delete", "AssetClassification", "حذف تصنيف ${item.classCode}", actor)
    }

'''
    text = text.replace('    // ---------------------------------------------------------------------\n    // Asset documents', methods + '    // ---------------------------------------------------------------------\n    // Asset documents', 1)

    save_doc = '''    suspend fun saveAssetDocument(doc: AssetDocumentEntity, actor: String = "System") {
        val isNew = doc.id == 0L
        database.withTransaction {
            doc.supersedesDocumentId?.let { previousId ->
                if (previousId != doc.id) documentDao.updateStatus(previousId, "Superseded")
            }
            val toSave = if (isNew) doc.copy(
                uploadedBy = actor,
                uploadedAt = DateStrings.now(),
                fileName = doc.fileName.ifBlank { java.io.File(doc.reference).name }
            ) else doc
            documentDao.insert(toSave)
            recordAudit(if (isNew) "Create" else "Update", "Document", "${if (isNew) "إضافة" else "تعديل"} مستند: ${doc.title} v${doc.version}", actor)
        }
    }'''
    text = replace_function(text, '    suspend fun saveAssetDocument(', save_doc)

    save_characteristic = '''    suspend fun saveCharacteristic(item: AssetCharacteristicEntity, actor: String = "System") {
        val isNew = item.id == 0L
        val now = DateStrings.now()
        characteristicDao.insert(item.copy(changedAt = now, changedBy = actor))
        recordAudit(if (isNew) "Create" else "Update", "Characteristic", "${if (isNew) "إضافة" else "تعديل"} خاصية ${item.characteristicCode.ifBlank { item.name }}", actor)
    }'''
    text = replace_function(text, '    suspend fun saveCharacteristic(', save_characteristic)

    add_reading = '''    suspend fun addReading(point: MeasuringPointEntity, value: Double, note: String, actor: String = "System"): String? {
        if (point.status != "Active") return "نقطة القياس غير نشطة"
        if (point.isCounter && value < point.lastReading) {
            return "لا يمكن أن تقل قراءة العداد التراكمي عن ${point.lastReading}"
        }
        val now = DateStrings.now()
        val lowerAlarm = point.lowerLimit?.let { value < it } == true
        val upperAlarm = point.upperLimit?.let { value > it } == true
        val lowerWarning = point.lowerWarningLimit?.let { value < it } == true
        val upperWarning = point.upperWarningLimit?.let { value > it } == true
        val actionRequired = lowerAlarm || upperAlarm || lowerWarning || upperWarning
        val result = when {
            lowerAlarm -> "BelowLowerLimit"
            upperAlarm -> "AboveUpperLimit"
            lowerWarning -> "BelowWarning"
            upperWarning -> "AboveWarning"
            else -> "Normal"
        }
        database.withTransaction {
            measurementDao.insertReading(
                MeasurementReadingEntity(
                    pointId = point.id,
                    assetId = point.assetId,
                    value = value,
                    createdAt = now,
                    createdBy = actor,
                    note = note,
                    readingUnit = point.unit,
                    referenceTime = now,
                    source = point.source,
                    processingStatus = "Accepted",
                    resultData = result,
                    actionRequired = actionRequired
                )
            )
            measurementDao.updateLastReading(point.id, value, now)
            recordAudit("Reading", "Meter", "قراءة ${point.pointCode.ifBlank { point.name }}: $value ${point.unit} • $result", actor)
        }
        return if (actionRequired) "تنبيه: القراءة خارج المجال الطبيعي ($result)" else null
    }'''
    text = replace_function(text, '    suspend fun addReading(', add_reading)
    path.write_text(text, encoding='utf-8')


def patch_forms() -> None:
    path = ROOT / 'app/src/main/java/com/alhadi/cmms/ui/Forms.kt'
    text = path.read_text(encoding='utf-8')

    char_form = '''@Composable
internal fun CharacteristicFormSheet(
    initial: AssetCharacteristicEntity?,
    assetId: Long,
    onDismiss: () -> Unit,
    onSave: (AssetCharacteristicEntity) -> Unit
) {
    var code by remember { mutableStateOf(initial?.characteristicCode ?: "") }
    var classCode by remember { mutableStateOf(initial?.classCode ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var value by remember { mutableStateOf(initial?.value ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "") }
    var dataType by remember { mutableStateOf(initial?.dataType ?: "Text") }
    var allowedValues by remember { mutableStateOf(initial?.allowedValues ?: "") }
    var mandatory by remember { mutableStateOf(initial?.isMandatory ?: false) }
    var inherited by remember { mutableStateOf(initial?.isInherited ?: false) }
    var source by remember { mutableStateOf(initial?.source ?: "Manual") }

    FormSheet(if (initial == null) "إضافة خاصية" else "تعديل الخاصية", onDismiss) {
        LabeledField("كود الخاصية", code, { code = it })
        LabeledField("كود التصنيف", classCode, { classCode = it })
        LabeledField("اسم الخاصية", name, { name = it })
        OptionDropdown("نوع البيانات", listOf("Text", "Number", "Date", "Boolean", "List"), dataType) { dataType = it }
        if (dataType == "List") LabeledField("القيم المسموحة (مفصولة بفاصلة)", allowedValues, { allowedValues = it })
        LabeledField("القيمة", value, { value = it })
        LabeledField("الوحدة", unit, { unit = it })
        OptionDropdown("المصدر", listOf("Manual", "Inherited", "Imported", "System"), source) { source = it }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("إلزامية", modifier = Modifier.weight(1f)); Switch(checked = mandatory, onCheckedChange = { mandatory = it })
        }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("موروثة", modifier = Modifier.weight(1f)); Switch(checked = inherited, onCheckedChange = { inherited = it })
        }
        SaveButton(name.isNotBlank() && value.isNotBlank()) {
            onSave(AssetCharacteristicEntity(
                id = initial?.id ?: 0,
                assetId = assetId,
                name = name.trim(), value = value.trim(), unit = unit.trim(),
                characteristicCode = code.trim(), classCode = classCode.trim(), dataType = dataType,
                allowedValues = allowedValues.trim(), isMandatory = mandatory, isInherited = inherited,
                source = source, changedAt = initial?.changedAt ?: "", changedBy = initial?.changedBy ?: ""
            ))
        }
    }
}'''
    text = replace_function(text, '@Composable\ninternal fun CharacteristicFormSheet(', char_form)

    doc_form = '''@Composable
internal fun DocumentFormSheet(
    initial: AssetDocumentEntity?,
    assetId: Long,
    onDismiss: () -> Unit,
    onSave: (AssetDocumentEntity) -> Unit
) {
    var type by remember { mutableStateOf(initial?.type ?: "Manual") }
    var title by remember { mutableStateOf(initial?.title ?: "") }
    var description by remember { mutableStateOf(initial?.description ?: "") }
    var reference by remember { mutableStateOf(initial?.reference ?: "") }
    var version by remember { mutableStateOf(initial?.version ?: "1.0") }
    var status by remember { mutableStateOf(initial?.status ?: "Current") }
    var documentDate by remember { mutableStateOf(initial?.documentDate ?: "") }
    var expiryDate by remember { mutableStateOf(initial?.expiryDate ?: "") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var attaching by remember { mutableStateOf(false) }
    var attachedName by remember { mutableStateOf(initial?.fileName ?: initial?.reference?.let { File(it).name } ?: "") }
    val picker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            attaching = true
            scope.launch {
                runCatching {
                    val name = ImageStore.queryDisplayName(context, uri)
                    val imported = withContext(Dispatchers.IO) { ImageStore.importToFiles(context, "asset_docs", uri, name) }
                    reference = imported
                    attachedName = name ?: File(imported).name
                }
                attaching = false
            }
        }
    }

    FormSheet(if (initial == null || initial.id == 0L) "إضافة مستند" else "تعديل المستند", onDismiss) {
        OptionDropdown("النوع", listOf("Manual", "Drawing", "Certificate", "Image", "Report", "Warranty", "Other"), type) { type = it }
        LabeledField("العنوان", title, { title = it })
        LabeledField("الوصف", description, { description = it }, singleLine = false)
        LabeledField("الإصدار", version, { version = it })
        OptionDropdown("الحالة", listOf("Draft", "Current", "Superseded", "Expired", "Archived"), status) { status = it }
        DateField("تاريخ المستند", documentDate) { documentDate = it }
        DateField("تاريخ الانتهاء", expiryDate) { expiryDate = it }
        Button(onClick = { picker.launch(arrayOf("image/*", "application/pdf", "*/*")) }, enabled = !attaching, modifier = Modifier.fillMaxWidth()) {
            Icon(Icons.Filled.AttachFile, contentDescription = null, modifier = Modifier.size(18.dp)); Spacer(modifier = Modifier.width(6.dp)); Text(if (attaching) "جارٍ الإرفاق…" else "إرفاق ملف من الجهاز")
        }
        if (attachedName.isNotBlank()) Text("الملف: $attachedName", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary)
        LabeledField("أو مرجع (رابط/ملاحظة)", reference, { reference = it }, singleLine = false)
        SaveButton(title.isNotBlank()) {
            onSave(AssetDocumentEntity(
                id = initial?.id ?: 0, assetId = assetId, type = type, title = title.trim(), reference = reference.trim(),
                uploadedBy = initial?.uploadedBy ?: "", uploadedAt = initial?.uploadedAt ?: "", description = description.trim(),
                version = version.trim().ifBlank { "1.0" }, status = status, documentDate = documentDate,
                expiryDate = expiryDate, supersedesDocumentId = initial?.supersedesDocumentId,
                fileName = attachedName, mimeType = initial?.mimeType ?: ""
            ))
        }
    }
}'''
    text = replace_function(text, '@Composable\ninternal fun DocumentFormSheet(', doc_form)

    meter_form = '''@Composable
internal fun MeterFormSheet(
    initial: MeasuringPointEntity?,
    assets: List<AssetEntity>,
    onDismiss: () -> Unit,
    onSave: (MeasuringPointEntity) -> Unit
) {
    var code by remember { mutableStateOf(initial?.pointCode ?: "") }
    var name by remember { mutableStateOf(initial?.name ?: "") }
    var unit by remember { mutableStateOf(initial?.unit ?: "") }
    var assetId by remember { mutableStateOf(initial?.assetId ?: assets.firstOrNull()?.id ?: 0L) }
    var isCounter by remember { mutableStateOf(initial?.isCounter ?: false) }
    var measurementType by remember { mutableStateOf(initial?.measurementType ?: "Condition") }
    var lowerLimit by remember { mutableStateOf(initial?.lowerLimit?.toString() ?: "") }
    var lowerWarning by remember { mutableStateOf(initial?.lowerWarningLimit?.toString() ?: "") }
    var upperWarning by remember { mutableStateOf(initial?.upperWarningLimit?.toString() ?: "") }
    var upperLimit by remember { mutableStateOf(initial?.upperLimit?.toString() ?: "") }
    var status by remember { mutableStateOf(initial?.status ?: "Active") }
    var source by remember { mutableStateOf(initial?.source ?: "Manual") }

    FormSheet(if (initial == null) "إضافة نقطة قياس" else "تعديل نقطة القياس", onDismiss) {
        LabeledField("كود نقطة القياس", code, { code = it })
        LabeledField("اسم النقطة", name, { name = it })
        LabeledField("الوحدة (hr / °C / mm/s ...)", unit, { unit = it })
        AssetDropdown(assets, assetId) { assetId = it }
        OptionDropdown("نوع القياس", listOf("Counter", "Condition", "Inspection", "Consumption", "Quality"), measurementType) { measurementType = it; isCounter = it == "Counter" }
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
            Text("عداد تراكمي", modifier = Modifier.weight(1f)); Switch(checked = isCounter, onCheckedChange = { isCounter = it })
        }
        LabeledField("الحد الأدنى الحرج", lowerLimit, { lowerLimit = it }, numeric = true)
        LabeledField("حد التحذير الأدنى", lowerWarning, { lowerWarning = it }, numeric = true)
        LabeledField("حد التحذير الأعلى", upperWarning, { upperWarning = it }, numeric = true)
        LabeledField("الحد الأعلى الحرج", upperLimit, { upperLimit = it }, numeric = true)
        OptionDropdown("الحالة", listOf("Active", "Inactive", "OutOfService"), status) { status = it }
        OptionDropdown("مصدر القراءة", listOf("Manual", "Sensor", "Imported", "Calculated"), source) { source = it }
        SaveButton(name.isNotBlank() && unit.isNotBlank() && assetId != 0L) {
            val today = DateStrings.today()
            onSave(MeasuringPointEntity(
                id = initial?.id ?: 0, assetId = assetId, name = name.trim(), unit = unit.trim(), isCounter = isCounter,
                upperLimit = upperLimit.toDoubleOrNull(), lastReading = initial?.lastReading ?: 0.0,
                lastReadingAt = initial?.lastReadingAt ?: today, pointCode = code.trim(), measurementType = measurementType,
                lowerLimit = lowerLimit.toDoubleOrNull(), lowerWarningLimit = lowerWarning.toDoubleOrNull(),
                upperWarningLimit = upperWarning.toDoubleOrNull(), status = status, source = source,
                functionalLocationId = initial?.functionalLocationId
            ))
        }
    }
}'''
    text = replace_function(text, '@Composable\ninternal fun MeterFormSheet(', meter_form)
    path.write_text(text, encoding='utf-8')


def write_governance_screen() -> None:
    write('app/src/main/java/com/alhadi/cmms/ui/AssetGovernanceScreen.kt', '''package com.alhadi.cmms.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.weight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.alhadi.cmms.data.AppDatabase
import com.alhadi.cmms.data.CmmsRepository
import com.alhadi.cmms.data.entity.AssetClassificationEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetPartnerEntity
import com.alhadi.cmms.data.entity.AssetWarrantyEntity
import com.alhadi.cmms.data.entity.WarrantyClaimEntity
import com.alhadi.cmms.util.DateStrings
import kotlinx.coroutines.launch

private enum class GovernanceSection(val label: String) {
    Partners("الشركاء"), Warranties("الضمانات"), Claims("المطالبات"), Classes("التصنيفات")
}

@Composable
internal fun AssetGovernanceScreen(innerPadding: PaddingValues, assets: List<AssetEntity>, actor: String) {
    val context = LocalContext.current
    val repository = remember(context) { CmmsRepository(AppDatabase.getDatabase(context)) }
    val partners by repository.assetPartners.collectAsState(initial = emptyList())
    val warranties by repository.assetWarranties.collectAsState(initial = emptyList())
    val claims by repository.warrantyClaims.collectAsState(initial = emptyList())
    val classifications by repository.assetClassifications.collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()
    var assetId by remember { mutableStateOf(assets.firstOrNull()?.id ?: 0L) }
    var section by remember { mutableStateOf(GovernanceSection.Partners) }
    var form by remember { mutableStateOf<String?>(null) }
    var editPartner by remember { mutableStateOf<AssetPartnerEntity?>(null) }
    var editWarranty by remember { mutableStateOf<AssetWarrantyEntity?>(null) }
    var editClaim by remember { mutableStateOf<WarrantyClaimEntity?>(null) }
    var editClass by remember { mutableStateOf<AssetClassificationEntity?>(null) }

    val selectedAsset = assets.firstOrNull { it.id == assetId }
    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(innerPadding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("حوكمة علاقات الأصل والضمان والتصنيف", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Text("كل سجل مرتبط بأصل محدد ويحفظ منفذ وتاريخ العملية.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item { GovernanceAssetPicker(assets, assetId) { assetId = it } }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                GovernanceSection.entries.forEach { item ->
                    FilterChip(selected = section == item, onClick = { section = item }, label = { Text(item.label) })
                }
            }
        }
        if (selectedAsset == null) {
            item { Text("أضف أصلاً أولاً لاستخدام وحدة الحوكمة.") }
        } else {
            item {
                Button(onClick = {
                    when (section) {
                        GovernanceSection.Partners -> { editPartner = null; form = "partner" }
                        GovernanceSection.Warranties -> { editWarranty = null; form = "warranty" }
                        GovernanceSection.Claims -> { editClaim = null; form = "claim" }
                        GovernanceSection.Classes -> { editClass = null; form = "class" }
                    }
                }, modifier = Modifier.fillMaxWidth()) { Icon(Icons.Filled.Add, null); Text(" إضافة ${section.label}") }
            }
            when (section) {
                GovernanceSection.Partners -> items(partners.filter { it.assetId == assetId }, key = { "p-${it.id}" }) { item ->
                    GovernanceCard(item.partnerRole, item.partnerName, listOf(item.organization, item.contactPerson, item.phone, item.email).filter { it.isNotBlank() }.joinToString(" • "), item.isPrimary,
                        onEdit = { editPartner = item; form = "partner" }, onDelete = { scope.launch { repository.deleteAssetPartner(item, actor) } })
                }
                GovernanceSection.Warranties -> items(warranties.filter { it.assetId == assetId }, key = { "w-${it.id}" }) { item ->
                    GovernanceCard(item.warrantyType, item.provider, "${item.startDate} — ${item.endDate} • ${item.status}", item.isActiveOn(DateStrings.today()),
                        onEdit = { editWarranty = item; form = "warranty" }, onDelete = { scope.launch { repository.deleteAssetWarranty(item, actor) } })
                }
                GovernanceSection.Claims -> items(claims.filter { it.assetId == assetId }, key = { "c-${it.id}" }) { item ->
                    GovernanceCard(item.claimNumber, item.description, "${item.claimDate} • ${item.status}", item.status == "Approved" || item.status == "Resolved",
                        onEdit = { editClaim = item; form = "claim" }, onDelete = { scope.launch { repository.deleteWarrantyClaim(item, actor) } })
                }
                GovernanceSection.Classes -> items(classifications.filter { it.assetId == assetId }, key = { "cl-${it.id}" }) { item ->
                    GovernanceCard(item.classCode, item.className, "${item.source}${if (item.isInherited) " • موروث" else ""}", item.isPrimary,
                        onEdit = { editClass = item; form = "class" }, onDelete = { scope.launch { repository.deleteAssetClassification(item, actor) } })
                }
            }
        }
    }

    if (form == "partner") PartnerDialog(editPartner, assetId, onDismiss = { form = null }) { item -> scope.launch { repository.saveAssetPartner(item, actor) }; form = null }
    if (form == "warranty") WarrantyDialog(editWarranty, assetId, onDismiss = { form = null }) { item -> scope.launch { repository.saveAssetWarranty(item, actor) }; form = null }
    if (form == "claim") ClaimDialog(editClaim, assetId, warranties.filter { it.assetId == assetId }, onDismiss = { form = null }) { item -> scope.launch { repository.saveWarrantyClaim(item, actor) }; form = null }
    if (form == "class") ClassificationDialog(editClass, assetId, onDismiss = { form = null }) { item -> scope.launch { repository.saveAssetClassification(item, actor) }; form = null }
}

@Composable
private fun GovernanceAssetPicker(assets: List<AssetEntity>, selectedId: Long, onSelect: (Long) -> Unit) {
    var open by remember { mutableStateOf(false) }
    val selected = assets.firstOrNull { it.id == selectedId }
    Box {
        OutlinedButton(onClick = { open = true }, modifier = Modifier.fillMaxWidth()) { Text(selected?.let { "${it.code} • ${it.name}" } ?: "اختر أصلاً", modifier = Modifier.weight(1f)) }
        DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
            assets.forEach { asset -> DropdownMenuItem(text = { Text("${asset.code} • ${asset.name}") }, onClick = { onSelect(asset.id); open = false }) }
        }
    }
}

@Composable
private fun GovernanceCard(title: String, subtitle: String, details: String, highlighted: Boolean, onEdit: () -> Unit, onDelete: () -> Unit) {
    ElevatedCard(onClick = onEdit, modifier = Modifier.fillMaxWidth(), colors = CardDefaults.elevatedCardColors()) {
        Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.Bold)
                Text(subtitle, style = MaterialTheme.typography.bodyMedium)
                if (details.isNotBlank()) Text(details, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                if (highlighted) Text("رئيسي / فعّال", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
            }
            IconButton(onClick = onDelete) { Icon(Icons.Filled.Delete, contentDescription = "حذف", tint = MaterialTheme.colorScheme.error) }
        }
    }
}

@Composable private fun GovField(label: String, value: String, onChange: (String) -> Unit) = OutlinedTextField(value, onChange, label = { Text(label) }, modifier = Modifier.fillMaxWidth())

@Composable
private fun PartnerDialog(initial: AssetPartnerEntity?, assetId: Long, onDismiss: () -> Unit, onSave: (AssetPartnerEntity) -> Unit) {
    var role by remember { mutableStateOf(initial?.partnerRole ?: "Owner") }; var name by remember { mutableStateOf(initial?.partnerName ?: "") }
    var org by remember { mutableStateOf(initial?.organization ?: "") }; var contact by remember { mutableStateOf(initial?.contactPerson ?: "") }
    var phone by remember { mutableStateOf(initial?.phone ?: "") }; var email by remember { mutableStateOf(initial?.email ?: "") }
    var primary by remember { mutableStateOf(initial?.isPrimary ?: false) }; var notes by remember { mutableStateOf(initial?.notes ?: "") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("شريك الأصل") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        GovField("الدور", role) { role = it }; GovField("الاسم", name) { name = it }; GovField("المنظمة", org) { org = it }; GovField("مسؤول التواصل", contact) { contact = it }; GovField("الهاتف", phone) { phone = it }; GovField("البريد", email) { email = it }; GovField("ملاحظات", notes) { notes = it }
        Row(verticalAlignment = Alignment.CenterVertically) { Text("شريك رئيسي", modifier = Modifier.weight(1f)); Switch(primary, { primary = it }) }
    } }, confirmButton = { TextButton(enabled = role.isNotBlank() && name.isNotBlank(), onClick = { onSave(AssetPartnerEntity(initial?.id ?: 0, assetId, role.trim(), name.trim(), org.trim(), contact.trim(), phone.trim(), email.trim(), isPrimary = primary, notes = notes.trim(), createdAt = initial?.createdAt ?: "", createdBy = initial?.createdBy ?: "")) }) { Text("حفظ") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } })
}

@Composable
private fun WarrantyDialog(initial: AssetWarrantyEntity?, assetId: Long, onDismiss: () -> Unit, onSave: (AssetWarrantyEntity) -> Unit) {
    var type by remember { mutableStateOf(initial?.warrantyType ?: "Standard") }; var provider by remember { mutableStateOf(initial?.provider ?: "") }
    var start by remember { mutableStateOf(initial?.startDate ?: "") }; var end by remember { mutableStateOf(initial?.endDate ?: "") }; var terms by remember { mutableStateOf(initial?.terms ?: "") }; var status by remember { mutableStateOf(initial?.status ?: "Active") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("ضمان الأصل") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { GovField("النوع", type) { type = it }; GovField("الجهة", provider) { provider = it }; GovField("من YYYY-MM-DD", start) { start = it }; GovField("إلى YYYY-MM-DD", end) { end = it }; GovField("الشروط", terms) { terms = it }; GovField("الحالة", status) { status = it } } }, confirmButton = { TextButton(enabled = provider.isNotBlank(), onClick = { onSave(AssetWarrantyEntity(id = initial?.id ?: 0, assetId = assetId, warrantyType = type.trim(), provider = provider.trim(), startDate = start.trim(), endDate = end.trim(), terms = terms.trim(), status = status.trim(), createdAt = initial?.createdAt ?: "", createdBy = initial?.createdBy ?: "")) }) { Text("حفظ") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } })
}

@Composable
private fun ClaimDialog(initial: WarrantyClaimEntity?, assetId: Long, warranties: List<AssetWarrantyEntity>, onDismiss: () -> Unit, onSave: (WarrantyClaimEntity) -> Unit) {
    var warrantyId by remember { mutableStateOf(initial?.warrantyId ?: warranties.firstOrNull()?.id ?: 0L) }; var number by remember { mutableStateOf(initial?.claimNumber ?: "") }; var date by remember { mutableStateOf(initial?.claimDate ?: DateStrings.today()) }; var status by remember { mutableStateOf(initial?.status ?: "Draft") }; var description by remember { mutableStateOf(initial?.description ?: "") }; var amount by remember { mutableStateOf((initial?.claimedAmount ?: 0.0).toString()) }; var resolution by remember { mutableStateOf(initial?.resolution ?: "") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("مطالبة ضمان") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { GovField("معرف الضمان", warrantyId.toString()) { warrantyId = it.toLongOrNull() ?: 0L }; GovField("رقم المطالبة (يُولّد تلقائياً)", number) { number = it }; GovField("التاريخ", date) { date = it }; GovField("الحالة", status) { status = it }; GovField("الوصف", description) { description = it }; GovField("المبلغ", amount) { amount = it }; GovField("الحل", resolution) { resolution = it } } }, confirmButton = { TextButton(enabled = warrantyId > 0 && description.isNotBlank(), onClick = { onSave(WarrantyClaimEntity(initial?.id ?: 0, warrantyId, assetId, number.trim(), date.trim(), status.trim(), description.trim(), amount.toDoubleOrNull() ?: 0.0, resolution.trim(), createdBy = initial?.createdBy ?: "", createdAt = initial?.createdAt ?: "", updatedAt = initial?.updatedAt ?: "")) }) { Text("حفظ") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } })
}

@Composable
private fun ClassificationDialog(initial: AssetClassificationEntity?, assetId: Long, onDismiss: () -> Unit, onSave: (AssetClassificationEntity) -> Unit) {
    var code by remember { mutableStateOf(initial?.classCode ?: "") }; var name by remember { mutableStateOf(initial?.className ?: "") }; var primary by remember { mutableStateOf(initial?.isPrimary ?: false) }; var inherited by remember { mutableStateOf(initial?.isInherited ?: false) }; var source by remember { mutableStateOf(initial?.source ?: "Manual") }
    AlertDialog(onDismissRequest = onDismiss, title = { Text("تصنيف الأصل") }, text = { Column(verticalArrangement = Arrangement.spacedBy(8.dp)) { GovField("كود التصنيف", code) { code = it }; GovField("اسم التصنيف", name) { name = it }; GovField("المصدر", source) { source = it }; Row(verticalAlignment = Alignment.CenterVertically) { Text("أساسي", modifier = Modifier.weight(1f)); Switch(primary, { primary = it }) }; Row(verticalAlignment = Alignment.CenterVertically) { Text("موروث", modifier = Modifier.weight(1f)); Switch(inherited, { inherited = it }) } } }, confirmButton = { TextButton(enabled = code.isNotBlank() && name.isNotBlank(), onClick = { onSave(AssetClassificationEntity(initial?.id ?: 0, assetId, code.trim(), name.trim(), primary, inherited, source.trim(), initial?.assignedAt ?: "", initial?.assignedBy ?: "")) }) { Text("حفظ") } }, dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } })
}
''')


def patch_app() -> None:
    path = ROOT / 'app/src/main/java/com/alhadi/cmms/ui/CmmsApp.kt'
    text = path.read_text(encoding='utf-8')
    if 'MoreRoute.AssetGovernance' in text:
        return
    text = text.replace('private enum class MoreRoute { Notifications, Inventory, Procurement, Suppliers, Reports, Audit, Admin, PreventiveMaintenance, TaskLists, Meters, Locations, Capa, Failures, Trash }', 'private enum class MoreRoute { Notifications, Inventory, Procurement, Suppliers, Reports, Audit, Admin, PreventiveMaintenance, TaskLists, Meters, Locations, AssetGovernance, Capa, Failures, Trash }', 1)
    text = text.replace('    MoreModule(MoreRoute.Locations, "المواقع الفنية", "هرمية المواقع", Icons.Filled.AccountTree, AccentGreen),', '    MoreModule(MoreRoute.Locations, "المواقع الفنية", "هرمية المواقع", Icons.Filled.AccountTree, AccentGreen),\n    MoreModule(MoreRoute.AssetGovernance, "حوكمة الأصول", "الشركاء والضمان والتصنيف", Icons.Filled.Verified, AccentNavy),', 1)
    text = text.replace('            MoreRoute.Locations\n        )', '            MoreRoute.Locations,\n            MoreRoute.AssetGovernance\n        )', 1)
    text = text.replace('        MoreRoute.Locations -> ScreenMeta("المواقع الفنية", "هرمية المواقع والمصانع", Icons.Filled.AccountTree, AccentGreen)', '        MoreRoute.Locations -> ScreenMeta("المواقع الفنية", "هرمية المواقع والمصانع", Icons.Filled.AccountTree, AccentGreen)\n        MoreRoute.AssetGovernance -> ScreenMeta("حوكمة الأصول", "الشركاء والضمان والتصنيف", Icons.Filled.Verified, AccentNavy)', 1)
    anchor = '                        MoreRoute.Locations -> LocationsScreen('
    idx = text.index(anchor)
    next_case = text.index('                        MoreRoute.Capa ->', idx)
    addition = '                        MoreRoute.AssetGovernance -> AssetGovernanceScreen(\n                            innerPadding = innerPadding,\n                            assets = assets,\n                            actor = actorName\n                        )\n'
    text = text[:next_case] + addition + text[next_case:]

    text = text.replace('Text(doc.title, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)', 'Text("${doc.title} • v${doc.version}", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleSmall)', 1)
    text = text.replace('StatusBadge(doc.type, statusTone("info"))', 'StatusBadge(doc.status, statusTone(if (doc.status == "Current") "running" else "info"))', 1)
    text = text.replace('Text("${doc.uploadedBy} • ${doc.uploadedAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)', 'if (doc.description.isNotBlank()) Text(doc.description, style = MaterialTheme.typography.bodySmall)\n                    if (doc.documentDate.isNotBlank() || doc.expiryDate.isNotBlank()) Text("تاريخ: ${doc.documentDate.ifBlank { "—" }} • انتهاء: ${doc.expiryDate.ifBlank { "—" }}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)\n                    Text("${doc.uploadedBy} • ${doc.uploadedAt}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)', 1)
    text = text.replace('Text(ch.name, modifier = Modifier.weight(1f), fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)', 'Column(modifier = Modifier.weight(1f)) {\n                        Text(ch.name, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)\n                        val meta = listOf(ch.characteristicCode, ch.classCode, ch.dataType, ch.source).filter { it.isNotBlank() }.joinToString(" • ")\n                        if (meta.isNotBlank()) Text(meta, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)\n                    }', 1)
    path.write_text(text, encoding='utf-8')


write_new_entities_and_daos()
patch_existing_entities()
patch_database()
patch_migrations()
patch_backup_bundle()
patch_document_dao()
patch_repository()
patch_forms()
write_governance_screen()
patch_app()
print('Applied stages 16-20 to App2.')

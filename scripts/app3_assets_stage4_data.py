from pathlib import Path


ROOT = Path(__file__).resolve().parents[1]


def load(relative: str) -> tuple[Path, str]:
    path = ROOT / relative
    return path, path.read_text(encoding="utf-8")


def save(path: Path, text: str) -> None:
    path.write_text(text, encoding="utf-8")


def replace_once(text: str, old: str, new: str, label: str) -> str:
    count = text.count(old)
    if count != 1:
        raise RuntimeError(f"{label}: expected one match, found {count}")
    return text.replace(old, new, 1)


# -----------------------------------------------------------------------------
# Header entity and DAO
# -----------------------------------------------------------------------------
entity_dir = ROOT / "app/src/main/java/com/alhadi/cmms/data/entity"
dao_dir = ROOT / "app/src/main/java/com/alhadi/cmms/data/dao"

(entity_dir / "AssetBomHeaderEntity.kt").write_text('''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Header and assignment information for a structured maintenance component list. */
@Entity(
    tableName = "asset_bom_headers",
    indices = [
        Index(value = ["assetId"]),
        Index(value = ["constructionType"]),
        Index(value = ["code", "alternative"], unique = true)
    ]
)
@Serializable
data class AssetBomHeaderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val assetId: Long? = null,
    val code: String,
    val name: String,
    @ColumnInfo(defaultValue = "'Asset'")
    val category: String = "Asset",
    @ColumnInfo(defaultValue = "'Maintenance'")
    val usage: String = "Maintenance",
    @ColumnInfo(defaultValue = "'01'")
    val alternative: String = "01",
    @ColumnInfo(defaultValue = "'Active'")
    val status: String = "Active",
    @ColumnInfo(defaultValue = "''")
    val validFrom: String = "",
    @ColumnInfo(defaultValue = "''")
    val validTo: String = "",
    @ColumnInfo(defaultValue = "''")
    val revision: String = "",
    @ColumnInfo(defaultValue = "'Direct'")
    val assignmentType: String = "Direct",
    @ColumnInfo(defaultValue = "''")
    val constructionType: String = "",
    @ColumnInfo(defaultValue = "''")
    val description: String = ""
) {
    fun isActiveOn(date: String): Boolean =
        status == "Active" && (validFrom.isBlank() || validFrom <= date) && (validTo.isBlank() || validTo >= date)

    fun hasValidDates(): Boolean = validFrom.isBlank() || validTo.isBlank() || validFrom <= validTo
}
''', encoding="utf-8")

(dao_dir / "AssetBomHeaderDao.kt").write_text('''package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AssetBomHeaderDao {
    @Query("SELECT * FROM asset_bom_headers ORDER BY code ASC, alternative ASC, id ASC")
    fun observeHeaders(): Flow<List<AssetBomHeaderEntity>>

    @Query("SELECT * FROM asset_bom_headers WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AssetBomHeaderEntity?

    @Query("SELECT * FROM asset_bom_headers WHERE assetId = :assetId ORDER BY code ASC, alternative ASC")
    suspend fun headersForAsset(assetId: Long): List<AssetBomHeaderEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(header: AssetBomHeaderEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAll(headers: List<AssetBomHeaderEntity>)

    @Update
    suspend fun update(header: AssetBomHeaderEntity)

    @Query("DELETE FROM asset_bom_headers WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_bom_headers")
    suspend fun deleteAll()
}
''', encoding="utf-8")


# -----------------------------------------------------------------------------
# Extend asset and component item entities
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/AssetEntity.kt")
if "val constructionType: String" not in text:
    text = replace_once(
        text,
        '''    @ColumnInfo(defaultValue = "''")
    val standardClass: String = "",''',
        '''    @ColumnInfo(defaultValue = "''")
    val standardClass: String = "",
    @ColumnInfo(defaultValue = "''")
    val constructionType: String = "",''',
        "asset construction type",
    )
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/AssetBomItemEntity.kt")
if "import androidx.room.ColumnInfo" not in text:
    text = replace_once(text, "import androidx.room.Entity", "import androidx.room.ColumnInfo\nimport androidx.room.Entity", "component item ColumnInfo import")
text = replace_once(
    text,
    '''    indices = [Index(value = ["assetId"]), Index(value = ["partId"])]''',
    '''    indices = [
        Index(value = ["assetId"]),
        Index(value = ["partId"]),
        Index(value = ["headerId"]),
        Index(value = ["parentItemId"]),
        Index(value = ["assemblyAssetId"])
    ]''',
    "component item indices",
)
if "val headerId: Long" not in text:
    text = replace_once(
        text,
        '''    val assetId: Long,
    val partId: Long,
    val quantity: Int
)''',
        '''    val assetId: Long,
    val partId: Long,
    val quantity: Int,
    @ColumnInfo(defaultValue = "0")
    val headerId: Long = 0,
    @ColumnInfo(defaultValue = "10")
    val itemNumber: Int = 10,
    @ColumnInfo(defaultValue = "'Stock'")
    val itemCategory: String = "Stock",
    @ColumnInfo(defaultValue = "'Active'")
    val status: String = "Active",
    @ColumnInfo(defaultValue = "''")
    val validFrom: String = "",
    @ColumnInfo(defaultValue = "''")
    val validTo: String = "",
    @ColumnInfo(defaultValue = "0")
    val isCritical: Boolean = false,
    @ColumnInfo(defaultValue = "1")
    val useInOrders: Boolean = true,
    @ColumnInfo(defaultValue = "''")
    val notes: String = "",
    val parentItemId: Long? = null,
    val assemblyAssetId: Long? = null,
    @ColumnInfo(defaultValue = "''")
    val alternativeGroup: String = "",
    @ColumnInfo(defaultValue = "0")
    val isAlternative: Boolean = false
) {
    fun isActiveOn(date: String): Boolean =
        status == "Active" && (validFrom.isBlank() || validFrom <= date) && (validTo.isBlank() || validTo >= date)

    fun hasValidDates(): Boolean = validFrom.isBlank() || validTo.isBlank() || validFrom <= validTo

    fun isMaterialItem(): Boolean = itemCategory == "Stock" || itemCategory == "NonStock"
}''',
        "component item extended fields",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Component item DAO
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/dao/AssetBomDao.kt")
text = replace_once(
    text,
    '@Query("SELECT * FROM asset_bom_items ORDER BY id ASC")',
    '@Query("SELECT * FROM asset_bom_items ORDER BY headerId ASC, itemNumber ASC, id ASC")',
    "component item ordering",
)
if "suspend fun getById" not in text:
    text = replace_once(
        text,
        '''    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AssetBomItemEntity): Long''',
        '''    @Query("SELECT * FROM asset_bom_items WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): AssetBomItemEntity?

    @Query("SELECT * FROM asset_bom_items WHERE headerId = :headerId ORDER BY itemNumber ASC, id ASC")
    suspend fun itemsForHeader(headerId: Long): List<AssetBomItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: AssetBomItemEntity): Long''',
        "component item lookup queries",
    )
if "deleteForHeader" not in text:
    text = replace_once(
        text,
        '''    @Query("DELETE FROM asset_bom_items WHERE id = :id")
    suspend fun deleteById(id: Long)''',
        '''    @Query("UPDATE asset_bom_items SET parentItemId = NULL WHERE parentItemId = :parentItemId")
    suspend fun clearParent(parentItemId: Long)

    @Query("DELETE FROM asset_bom_items WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM asset_bom_items WHERE headerId = :headerId")
    suspend fun deleteForHeader(headerId: Long)''',
        "component item delete queries",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Database registration and migration 25 -> 26
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/AppDatabase.kt")
if "import com.alhadi.cmms.data.dao.AssetBomHeaderDao" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.dao.AssetBomDao", "import com.alhadi.cmms.data.dao.AssetBomDao\nimport com.alhadi.cmms.data.dao.AssetBomHeaderDao", "header DAO import")
if "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.AssetBomItemEntity", "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity\nimport com.alhadi.cmms.data.entity.AssetBomItemEntity", "header entity import")
if "AssetBomHeaderEntity::class" not in text:
    text = replace_once(text, "        AssetCharacteristicEntity::class,\n        AssetBomItemEntity::class,", "        AssetCharacteristicEntity::class,\n        AssetBomHeaderEntity::class,\n        AssetBomItemEntity::class,", "header entity registration")
if "version = 26" not in text:
    text = replace_once(text, "version = 25", "version = 26", "database version 26")
if "abstract fun assetBomHeaderDao" not in text:
    text = replace_once(text, "    abstract fun assetBomDao(): AssetBomDao", "    abstract fun assetBomHeaderDao(): AssetBomHeaderDao\n    abstract fun assetBomDao(): AssetBomDao", "header DAO registration")
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/DbMigrations.kt")
if "MIGRATION_25_26" not in text:
    migration = '''

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
    }'''
    text = replace_once(
        text,
        "\n\n    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25)",
        migration + "\n\n    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25, MIGRATION_25_26)",
        "migration 25 to 26 registration",
    )
save(path, text)


# -----------------------------------------------------------------------------
# Backup bundle
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/BackupBundle.kt")
if "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.AssetBomItemEntity", "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity\nimport com.alhadi.cmms.data.entity.AssetBomItemEntity", "backup header import")
if "val assetBomHeaders" not in text:
    text = replace_once(text, "    val assetCharacteristics: List<AssetCharacteristicEntity> = emptyList(),\n    val assetBom: List<AssetBomItemEntity> = emptyList(),", "    val assetCharacteristics: List<AssetCharacteristicEntity> = emptyList(),\n    val assetBomHeaders: List<AssetBomHeaderEntity> = emptyList(),\n    val assetBom: List<AssetBomItemEntity> = emptyList(),", "backup header field")
    text = replace_once(text, "            assetCharacteristics.size + assetBom.size + assetMovements.size + pmChecklist.size +", "            assetCharacteristics.size + assetBomHeaders.size + assetBom.size + assetMovements.size + pmChecklist.size +", "backup total records")
    text = replace_once(text, "        const val CURRENT_FORMAT_VERSION = 1", "        const val CURRENT_FORMAT_VERSION = 2", "backup format version")
save(path, text)


# -----------------------------------------------------------------------------
# Repository flows, backup, sample data and CRUD
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/CmmsRepository.kt")
if "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.AssetBomItemEntity", "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity\nimport com.alhadi.cmms.data.entity.AssetBomItemEntity", "repository header import")
if "private val bomHeaderDao" not in text:
    text = replace_once(text, "    private val characteristicDao = database.assetCharacteristicDao()\n    private val bomDao = database.assetBomDao()", "    private val characteristicDao = database.assetCharacteristicDao()\n    private val bomHeaderDao = database.assetBomHeaderDao()\n    private val bomDao = database.assetBomDao()", "repository header DAO")
if "val assetBomHeaders" not in text:
    text = replace_once(text, "    val assetCharacteristics: Flow<List<AssetCharacteristicEntity>> = characteristicDao.observeCharacteristics()\n    val assetBom: Flow<List<AssetBomItemEntity>> = bomDao.observeBom()", "    val assetCharacteristics: Flow<List<AssetCharacteristicEntity>> = characteristicDao.observeCharacteristics()\n    val assetBomHeaders: Flow<List<AssetBomHeaderEntity>> = bomHeaderDao.observeHeaders()\n    val assetBom: Flow<List<AssetBomItemEntity>> = bomDao.observeBom()", "repository header flow")

if "private suspend fun restoreBomData" not in text:
    helper = '''

    private suspend fun restoreBomData(
        headers: List<AssetBomHeaderEntity>,
        items: List<AssetBomItemEntity>
    ) {
        val restoredHeaderIds = headers.mapTo(mutableSetOf()) { it.id }
        if (headers.isNotEmpty()) bomHeaderDao.insertAll(headers)

        val assigned = items.filter { it.headerId != 0L && it.headerId in restoredHeaderIds }
        if (assigned.isNotEmpty()) bomDao.insertAll(assigned)

        val legacy = items.filterNot { it in assigned }
        legacy.groupBy { it.assetId }.forEach { (assetId, lines) ->
            val headerId = bomHeaderDao.insert(
                AssetBomHeaderEntity(
                    assetId = assetId.takeIf { it != 0L },
                    code = "RESTORED-$assetId",
                    name = "قائمة مكونات مستعادة",
                    category = "Asset",
                    usage = "Maintenance",
                    alternative = "01",
                    status = "Active",
                    revision = "A",
                    assignmentType = "Direct"
                )
            )
            bomDao.insertAll(
                lines.sortedBy { it.id }.mapIndexed { index, item ->
                    item.copy(headerId = headerId, itemNumber = item.itemNumber.takeIf { it > 0 } ?: (index + 1) * 10)
                }
            )
        }
    }'''
    text = replace_once(text, "\n    /** Serializes every table into a portable JSON backup string. */", helper + "\n\n    /** Serializes every table into a portable JSON backup string. */", "repository restore helper")

text = text.replace("            appDbVersion = 22,", "            appDbVersion = 26,", 1)
if "assetBomHeaders = assetBomHeaders.first()" not in text:
    text = replace_once(text, "            assetCharacteristics = assetCharacteristics.first(),\n            assetBom = assetBom.first(),", "            assetCharacteristics = assetCharacteristics.first(),\n            assetBomHeaders = assetBomHeaders.first(),\n            assetBom = assetBom.first(),", "backup export headers")

# Clear headers after items in restore and sample reset.
text = text.replace("            bomDao.deleteAll()\n            characteristicDao.deleteAll()", "            bomDao.deleteAll()\n            bomHeaderDao.deleteAll()\n            characteristicDao.deleteAll()", 1)
text = text.replace("                bomDao.deleteAll()\n                characteristicDao.deleteAll()", "                bomDao.deleteAll()\n                bomHeaderDao.deleteAll()\n                characteristicDao.deleteAll()", 1)

if "restoreBomData(bundle.assetBomHeaders" not in text:
    text = replace_once(text, "            characteristicDao.insertAll(bundle.assetCharacteristics)\n            bomDao.insertAll(bundle.assetBom)", "            characteristicDao.insertAll(bundle.assetCharacteristics)\n            restoreBomData(bundle.assetBomHeaders, bundle.assetBom)", "backup restore headers")

old_seed = '''            val bomItems = listOf(
                AssetBomItemEntity(1, 7, 1, 4),
                AssetBomItemEntity(2, 1, 2, 2),
                AssetBomItemEntity(3, 2, 3, 6),
                AssetBomItemEntity(4, 10, 5, 1)
            )'''
if old_seed in text:
    new_seed = '''            val bomHeaders = listOf(
                AssetBomHeaderEntity(1, 7, "RM-01-MAIN", "مكونات صيانة المطحنة", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", "قطع الغيار والتجميعات الرئيسية"),
                AssetBomHeaderEntity(2, 1, "BE-101-MAIN", "مكونات المصعد", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", ""),
                AssetBomHeaderEntity(3, 2, "CC-205-MAIN", "مكونات ناقل السلسلة", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", ""),
                AssetBomHeaderEntity(4, 10, "CP-01-MAIN", "مكونات الضاغط", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", ""),
                AssetBomHeaderEntity(5, 4, "SF-030-MAIN", "مكونات مروحة الصومعة", "Asset", "Maintenance", "01", "Active", "", "", "A", "Direct", "", "هيكل متعدد المستويات")
            )

            val bomItems = listOf(
                AssetBomItemEntity(id = 1, assetId = 7, partId = 1, quantity = 4, headerId = 1, itemNumber = 10, itemCategory = "Stock", isCritical = true, notes = "محامل التشغيل الرئيسية"),
                AssetBomItemEntity(id = 2, assetId = 1, partId = 2, quantity = 2, headerId = 2, itemNumber = 10, itemCategory = "Stock", isCritical = true),
                AssetBomItemEntity(id = 3, assetId = 2, partId = 3, quantity = 6, headerId = 3, itemNumber = 10, itemCategory = "Stock"),
                AssetBomItemEntity(id = 4, assetId = 10, partId = 5, quantity = 1, headerId = 4, itemNumber = 10, itemCategory = "Stock"),
                AssetBomItemEntity(id = 5, assetId = 4, partId = 0, quantity = 1, headerId = 5, itemNumber = 10, itemCategory = "Assembly", useInOrders = false, assemblyAssetId = 6, notes = "تجميعة الاستشعار والتحكم"),
                AssetBomItemEntity(id = 6, assetId = 4, partId = 4, quantity = 1, headerId = 5, itemNumber = 20, itemCategory = "Stock", parentItemId = 5, isCritical = true)
            )'''
    text = text.replace(old_seed, new_seed, 1)
else:
    raise RuntimeError("sample component-list block not found")

if "bomHeaderDao.insertAll(bomHeaders)" not in text:
    text = replace_once(text, "            characteristicDao.insertAll(characteristics)\n            bomDao.insertAll(bomItems)", "            characteristicDao.insertAll(characteristics)\n            bomHeaderDao.insertAll(bomHeaders)\n            bomDao.insertAll(bomItems)", "sample header insert")

# Delete direct component lists with their asset.
if "bomHeaderDao.headersForAsset(asset.id)" not in text:
    text = replace_once(
        text,
        '''    suspend fun deleteAsset(asset: AssetEntity, actor: String = "System") {
        assetDao.deleteById(asset.id)
        recordAudit("Delete", "Asset", "حذف أصل: ${asset.code}", actor)
    }''',
        '''    suspend fun deleteAsset(asset: AssetEntity, actor: String = "System") {
        database.withTransaction {
            bomHeaderDao.headersForAsset(asset.id).forEach { header ->
                bomDao.deleteForHeader(header.id)
                bomHeaderDao.deleteById(header.id)
            }
            assetDao.deleteById(asset.id)
            recordAudit("Delete", "Asset", "حذف أصل: ${asset.code}", actor)
        }
    }''',
        "asset component-list cleanup",
    )

old_methods = '''    suspend fun saveBomItem(item: AssetBomItemEntity, actor: String = "System") {
        val isNew = item.id == 0L
        bomDao.insert(item)
        recordAudit(if (isNew) "Create" else "Update", "BOM", "${if (isNew) "إضافة" else "تعديل"} بند مكوّنات (قطعة #${item.partId})", actor)
    }

    suspend fun deleteBomItem(item: AssetBomItemEntity, actor: String = "System") {
        bomDao.deleteById(item.id)
        recordAudit("Delete", "BOM", "حذف بند مكوّنات (قطعة #${item.partId})", actor)
    }'''
if old_methods in text:
    new_methods = '''    suspend fun saveBomHeader(header: AssetBomHeaderEntity, actor: String = "System") {
        require(header.code.isNotBlank()) { "أدخل كود قائمة المكونات" }
        require(header.name.isNotBlank()) { "أدخل اسم قائمة المكونات" }
        require(header.hasValidDates()) { "تاريخ بداية الصلاحية يجب ألا يتجاوز تاريخ النهاية" }
        if (header.assignmentType == "Direct") {
            require(header.assetId != null && header.assetId != 0L) { "حدد الأصل المرتبط بالقائمة" }
        } else {
            require(header.constructionType.isNotBlank()) { "أدخل نوع الإنشاء للتعيين المشترك" }
        }

        val normalized = header.copy(
            assetId = header.assetId.takeIf { header.assignmentType == "Direct" },
            code = header.code.trim().uppercase(),
            name = header.name.trim(),
            alternative = header.alternative.trim().ifBlank { "01" },
            constructionType = header.constructionType.trim(),
            description = header.description.trim()
        )
        val isNew = normalized.id == 0L
        if (isNew) bomHeaderDao.insert(normalized) else bomHeaderDao.update(normalized)
        recordAudit(if (isNew) "Create" else "Update", "BOM", "${if (isNew) "إنشاء" else "تعديل"} قائمة مكونات: ${normalized.code}", actor)
    }

    suspend fun deleteBomHeader(header: AssetBomHeaderEntity, actor: String = "System") {
        database.withTransaction {
            bomDao.deleteForHeader(header.id)
            bomHeaderDao.deleteById(header.id)
            recordAudit("Delete", "BOM", "حذف قائمة مكونات: ${header.code}", actor)
        }
    }

    suspend fun saveBomItem(item: AssetBomItemEntity, actor: String = "System") {
        require(item.headerId != 0L) { "حدد قائمة المكونات" }
        require(item.itemNumber > 0) { "رقم البند يجب أن يكون أكبر من صفر" }
        require(item.quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
        require(item.hasValidDates()) { "تاريخ بداية صلاحية البند يجب ألا يتجاوز تاريخ النهاية" }

        val header = bomHeaderDao.getById(item.headerId)
            ?: throw IllegalStateException("قائمة المكونات المحددة غير موجودة")
        val parent = item.parentItemId?.let { bomDao.getById(it) }
        if (parent != null) require(parent.headerId == item.headerId) { "البند الأب يجب أن يكون ضمن القائمة نفسها" }
        require(item.id == 0L || item.parentItemId != item.id) { "لا يمكن أن يكون البند أباً لنفسه" }

        val normalized = when (item.itemCategory) {
            "Stock", "NonStock" -> {
                require(item.partId != 0L) { "حدد قطعة الغيار" }
                item.copy(assetId = header.assetId ?: 0L, assemblyAssetId = null)
            }
            "Assembly" -> {
                require(item.assemblyAssetId != null && item.assemblyAssetId != 0L) { "حدد تجميعة الصيانة" }
                require(item.assemblyAssetId != header.assetId) { "لا يمكن ربط الأصل بنفسه كتجميعة" }
                item.copy(assetId = header.assetId ?: 0L, partId = 0L, useInOrders = false)
            }
            "Text" -> {
                require(item.notes.isNotBlank()) { "أدخل وصف البند النصي" }
                item.copy(assetId = header.assetId ?: 0L, partId = 0L, assemblyAssetId = null, useInOrders = false)
            }
            else -> throw IllegalArgumentException("فئة بند غير مدعومة")
        }

        val isNew = normalized.id == 0L
        bomDao.insert(normalized)
        recordAudit(if (isNew) "Create" else "Update", "BOM", "${if (isNew) "إضافة" else "تعديل"} بند ${normalized.itemNumber} في ${header.code}", actor)
    }

    suspend fun deleteBomItem(item: AssetBomItemEntity, actor: String = "System") {
        database.withTransaction {
            bomDao.clearParent(item.id)
            bomDao.deleteById(item.id)
            recordAudit("Delete", "BOM", "حذف بند مكونات رقم ${item.itemNumber}", actor)
        }
    }'''
    text = text.replace(old_methods, new_methods, 1)
else:
    raise RuntimeError("component-list repository methods not found")
save(path, text)


# -----------------------------------------------------------------------------
# ViewModel flow and actions
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/viewmodel/CmmsViewModel.kt")
if "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.AssetBomItemEntity", "import com.alhadi.cmms.data.entity.AssetBomHeaderEntity\nimport com.alhadi.cmms.data.entity.AssetBomItemEntity", "view model header import")
if "val assetBomHeaders" not in text:
    text = replace_once(text, "    val assetBom: StateFlow<List<AssetBomItemEntity>> = repository.assetBom", "    val assetBomHeaders: StateFlow<List<AssetBomHeaderEntity>> = repository.assetBomHeaders\n        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())\n\n    val assetBom: StateFlow<List<AssetBomItemEntity>> = repository.assetBom", "view model header flow")
if "fun saveBomHeader" not in text:
    text = replace_once(
        text,
        '''    // ----- Asset BOM -----
    fun saveBomItem(item: AssetBomItemEntity) = launchAction("تم حفظ بند المكوّنات") { repository.saveBomItem(item, actor()) }''',
        '''    // ----- Asset BOM -----
    fun saveBomHeader(header: AssetBomHeaderEntity) = launchAction("تم حفظ قائمة المكونات") { repository.saveBomHeader(header, actor()) }
    fun deleteBomHeader(header: AssetBomHeaderEntity) = launchAction("تم حذف قائمة المكونات") { repository.deleteBomHeader(header, actor()) }
    fun saveBomItem(item: AssetBomItemEntity) = launchAction("تم حفظ بند المكوّنات") { repository.saveBomItem(item, actor()) }''',
        "view model header actions",
    )
save(path, text)

print("Structured maintenance component-list data stage 4 patch completed successfully.")

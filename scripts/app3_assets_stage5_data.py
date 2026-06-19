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


entity_dir = ROOT / "app/src/main/java/com/alhadi/cmms/data/entity"
dao_dir = ROOT / "app/src/main/java/com/alhadi/cmms/data/dao"
data_dir = ROOT / "app/src/main/java/com/alhadi/cmms/data"

# -----------------------------------------------------------------------------
# Serial number master data
# -----------------------------------------------------------------------------
(entity_dir / "SerialNumberProfileEntity.kt").write_text('''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Rules that control creation, stock verification, and movement of serialized parts. */
@Entity(
    tableName = "serial_number_profiles",
    indices = [Index(value = ["code"], unique = true)]
)
@Serializable
data class SerialNumberProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String,
    val name: String,
    @ColumnInfo(defaultValue = "1")
    val requireOnReceipt: Boolean = true,
    @ColumnInfo(defaultValue = "1")
    val requireOnIssue: Boolean = true,
    @ColumnInfo(defaultValue = "1")
    val autoCreate: Boolean = true,
    @ColumnInfo(defaultValue = "0")
    val equipmentRequired: Boolean = false,
    @ColumnInfo(defaultValue = "'Block'")
    val stockCheckMode: String = "Block",
    @ColumnInfo(defaultValue = "0")
    val allowManualStockEdit: Boolean = false,
    @ColumnInfo(defaultValue = "''")
    val equipmentCategory: String = "",
    @ColumnInfo(defaultValue = "''")
    val description: String = ""
)
''', encoding="utf-8")

(entity_dir / "SerialNumberEntity.kt").write_text('''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Individually tracked unit of a serialized spare part or maintainable assembly. */
@Entity(
    tableName = "serial_numbers",
    indices = [
        Index(value = ["partId", "serialNumber"], unique = true),
        Index(value = ["profileId"]),
        Index(value = ["assetId"], unique = true),
        Index(value = ["currentWorkOrderId"]),
        Index(value = ["status"]),
        Index(value = ["storageLocation"])
    ]
)
@Serializable
data class SerialNumberEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val partId: Long,
    val serialNumber: String,
    val profileId: Long? = null,
    val assetId: Long? = null,
    val currentWorkOrderId: Long? = null,
    @ColumnInfo(defaultValue = "'Created'")
    val status: String = "Created",
    @ColumnInfo(defaultValue = "''")
    val stockType: String = "",
    @ColumnInfo(defaultValue = "''")
    val plant: String = "",
    @ColumnInfo(defaultValue = "''")
    val storageLocation: String = "",
    @ColumnInfo(defaultValue = "''")
    val batch: String = "",
    @ColumnInfo(defaultValue = "''")
    val vendor: String = "",
    @ColumnInfo(defaultValue = "''")
    val customer: String = "",
    @ColumnInfo(defaultValue = "''")
    val salesOrder: String = "",
    @ColumnInfo(defaultValue = "''")
    val specialStock: String = "",
    @ColumnInfo(defaultValue = "''")
    val createdAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val lastMovementAt: String = "",
    @ColumnInfo(defaultValue = "''")
    val notes: String = ""
) {
    fun isInStock(): Boolean = status == "InStock"
    fun isInstalled(): Boolean = status == "Installed"
}
''', encoding="utf-8")

(entity_dir / "SerialNumberMovementEntity.kt").write_text('''package com.alhadi.cmms.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable

/** Immutable movement history for one serialized unit. */
@Entity(
    tableName = "serial_number_movements",
    indices = [
        Index(value = ["serialId"]),
        Index(value = ["partId"]),
        Index(value = ["workOrderId"]),
        Index(value = ["createdAt"])
    ]
)
@Serializable
data class SerialNumberMovementEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serialId: Long,
    val partId: Long,
    val workOrderId: Long? = null,
    val movementType: String,
    @ColumnInfo(defaultValue = "''")
    val fromStatus: String = "",
    @ColumnInfo(defaultValue = "''")
    val toStatus: String = "",
    @ColumnInfo(defaultValue = "''")
    val fromPlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val toPlant: String = "",
    @ColumnInfo(defaultValue = "''")
    val fromStorageLocation: String = "",
    @ColumnInfo(defaultValue = "''")
    val toStorageLocation: String = "",
    @ColumnInfo(defaultValue = "''")
    val fromStockType: String = "",
    @ColumnInfo(defaultValue = "''")
    val toStockType: String = "",
    val createdAt: String,
    val createdBy: String,
    @ColumnInfo(defaultValue = "''")
    val note: String = ""
)
''', encoding="utf-8")

(data_dir / "SerialNumberRequests.kt").write_text('''package com.alhadi.cmms.data

data class SerialMasterRequest(
    val partId: Long,
    val serialNumber: String,
    val notes: String = ""
)

data class SerializedReceiptRequest(
    val partId: Long,
    val serialNumbers: List<String>,
    val plant: String,
    val storageLocation: String,
    val stockType: String = "Unrestricted",
    val batch: String = "",
    val vendor: String = "",
    val note: String = ""
)

data class SerializedIssueRequest(
    val partId: Long,
    val serialIds: List<Long>,
    val workOrderId: Long? = null,
    val note: String = ""
)

data class SerialTransferRequest(
    val serialId: Long,
    val plant: String,
    val storageLocation: String,
    val stockType: String,
    val batch: String = "",
    val note: String = ""
)

data class SerialInstallRequest(
    val serialId: Long,
    val assetId: Long,
    val note: String = ""
)
''', encoding="utf-8")

(dao_dir / "SerialNumberDao.kt").write_text('''package com.alhadi.cmms.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SerialNumberDao {
    @Query("SELECT * FROM serial_number_profiles ORDER BY code ASC")
    fun observeProfiles(): Flow<List<SerialNumberProfileEntity>>

    @Query("SELECT * FROM serial_numbers ORDER BY serialNumber ASC")
    fun observeSerialNumbers(): Flow<List<SerialNumberEntity>>

    @Query("SELECT * FROM serial_number_movements ORDER BY createdAt DESC, id DESC LIMIT 250")
    fun observeMovements(): Flow<List<SerialNumberMovementEntity>>

    @Query("SELECT * FROM serial_number_profiles ORDER BY id ASC")
    suspend fun dumpProfiles(): List<SerialNumberProfileEntity>

    @Query("SELECT * FROM serial_numbers ORDER BY id ASC")
    suspend fun dumpSerialNumbers(): List<SerialNumberEntity>

    @Query("SELECT * FROM serial_number_movements ORDER BY id ASC")
    suspend fun dumpMovements(): List<SerialNumberMovementEntity>

    @Query("SELECT * FROM serial_number_profiles WHERE id = :id LIMIT 1")
    suspend fun getProfile(id: Long): SerialNumberProfileEntity?

    @Query("SELECT * FROM serial_numbers WHERE id = :id LIMIT 1")
    suspend fun getSerial(id: Long): SerialNumberEntity?

    @Query("SELECT * FROM serial_numbers WHERE partId = :partId AND serialNumber = :serialNumber LIMIT 1")
    suspend fun findByPartAndNumber(partId: Long, serialNumber: String): SerialNumberEntity?

    @Query("SELECT * FROM serial_numbers WHERE assetId = :assetId LIMIT 1")
    suspend fun findByAsset(assetId: Long): SerialNumberEntity?

    @Query("SELECT * FROM serial_numbers WHERE partId = :partId ORDER BY serialNumber ASC")
    suspend fun serialsForPart(partId: Long): List<SerialNumberEntity>

    @Query("SELECT * FROM serial_numbers WHERE partId = :partId AND status = 'InStock' ORDER BY serialNumber ASC")
    suspend fun availableForPart(partId: Long): List<SerialNumberEntity>

    @Query("SELECT COUNT(*) FROM serial_numbers WHERE partId = :partId AND status = 'InStock'")
    suspend fun countInStock(partId: Long): Int

    @Query("SELECT COUNT(*) FROM serial_numbers WHERE partId = :partId")
    suspend fun countForPart(partId: Long): Int

    @Query("SELECT COUNT(*) FROM serial_numbers WHERE profileId = :profileId")
    suspend fun countForProfile(profileId: Long): Int

    @Query("SELECT COUNT(*) FROM spare_parts WHERE serialProfileId = :profileId")
    suspend fun countPartsUsingProfile(profileId: Long): Int

    @Query("SELECT COUNT(*) FROM serial_numbers WHERE assetId = :assetId")
    suspend fun countForAsset(assetId: Long): Int

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProfile(profile: SerialNumberProfileEntity): Long

    @Update
    suspend fun updateProfile(profile: SerialNumberProfileEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertProfiles(profiles: List<SerialNumberProfileEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSerial(serial: SerialNumberEntity): Long

    @Update
    suspend fun updateSerial(serial: SerialNumberEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertSerials(serials: List<SerialNumberEntity>)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMovement(movement: SerialNumberMovementEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertMovements(movements: List<SerialNumberMovementEntity>)

    @Query("DELETE FROM serial_number_profiles WHERE id = :id")
    suspend fun deleteProfile(id: Long)

    @Query("DELETE FROM serial_number_movements WHERE serialId = :serialId")
    suspend fun deleteMovementsForSerial(serialId: Long)

    @Query("DELETE FROM serial_numbers WHERE id = :id")
    suspend fun deleteSerial(id: Long)

    @Query("DELETE FROM serial_number_movements")
    suspend fun deleteAllMovements()

    @Query("DELETE FROM serial_numbers")
    suspend fun deleteAllSerialNumbers()

    @Query("DELETE FROM serial_number_profiles")
    suspend fun deleteAllProfiles()
}
''', encoding="utf-8")

# -----------------------------------------------------------------------------
# Serial number domain service
# -----------------------------------------------------------------------------
(data_dir / "SerialNumberService.kt").write_text('''package com.alhadi.cmms.data

import androidx.room.withTransaction
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.util.DateStrings
import kotlinx.coroutines.flow.Flow

internal class SerialNumberService(
    private val database: AppDatabase,
    private val audit: suspend (String, String, String, String) -> Unit
) {
    private val serialDao = database.serialNumberDao()
    private val partDao = database.sparePartDao()
    private val assetDao = database.assetDao()
    private val orderDao = database.workOrderDao()
    private val transactionDao = database.inventoryTransactionDao()

    val profiles: Flow<List<SerialNumberProfileEntity>> = serialDao.observeProfiles()
    val serialNumbers: Flow<List<SerialNumberEntity>> = serialDao.observeSerialNumbers()
    val movements: Flow<List<SerialNumberMovementEntity>> = serialDao.observeMovements()

    private fun normalizeNumber(value: String): String = value.trim().uppercase()

    private suspend fun serializedPart(partId: Long): Pair<SparePartEntity, SerialNumberProfileEntity> {
        val part = partDao.getById(partId) ?: throw IllegalStateException("قطعة الغيار المحددة غير موجودة")
        if (!part.serializationActive) throw IllegalStateException("التتبع الفردي غير مفعّل لهذه القطعة")
        val profileId = part.serialProfileId ?: throw IllegalStateException("لم يتم تعيين ملف تتبع للقطعة")
        val profile = serialDao.getProfile(profileId) ?: throw IllegalStateException("ملف التتبع المحدد غير موجود")
        return part to profile
    }

    private suspend fun verifyStock(part: SparePartEntity, profile: SerialNumberProfileEntity, actor: String) {
        if (profile.stockCheckMode == "None") return
        val serialCount = serialDao.countInStock(part.id)
        if (serialCount == part.onHandQty) return
        val message = "عدم تطابق مخزون ${part.partNumber}: الكمية ${part.onHandQty} والوحدات المتسلسلة $serialCount"
        when (profile.stockCheckMode) {
            "Block" -> throw IllegalStateException(message)
            "Warning" -> audit("Warning", "SerialStock", message, actor)
        }
    }

    private suspend fun movement(
        old: SerialNumberEntity?,
        updated: SerialNumberEntity,
        type: String,
        actor: String,
        note: String,
        workOrderId: Long? = updated.currentWorkOrderId
    ) {
        serialDao.insertMovement(
            SerialNumberMovementEntity(
                serialId = updated.id,
                partId = updated.partId,
                workOrderId = workOrderId,
                movementType = type,
                fromStatus = old?.status.orEmpty(),
                toStatus = updated.status,
                fromPlant = old?.plant.orEmpty(),
                toPlant = updated.plant,
                fromStorageLocation = old?.storageLocation.orEmpty(),
                toStorageLocation = updated.storageLocation,
                fromStockType = old?.stockType.orEmpty(),
                toStockType = updated.stockType,
                createdAt = DateStrings.now(),
                createdBy = actor,
                note = note
            )
        )
    }

    private suspend fun clearAssetLink(serial: SerialNumberEntity) {
        val assetId = serial.assetId ?: return
        val asset = assetDao.getAssetById(assetId) ?: return
        if (asset.linkedSerialId == serial.id) {
            assetDao.insertAsset(
                asset.copy(
                    linkedSerialId = null,
                    serializedPartId = null,
                    serialNumber = if (asset.serialNumber.equals(serial.serialNumber, ignoreCase = true)) "" else asset.serialNumber
                )
            )
        }
    }

    suspend fun validatePartChange(part: SparePartEntity) {
        if (part.serializationActive) {
            val profileId = part.serialProfileId ?: throw IllegalStateException("حدد ملف تتبع للأرقام التسلسلية")
            serialDao.getProfile(profileId) ?: throw IllegalStateException("ملف التتبع المحدد غير موجود")
        }
        if (part.id != 0L && !part.serializationActive && serialDao.countForPart(part.id) > 0) {
            throw IllegalStateException("لا يمكن إيقاف التتبع لوجود أرقام تسلسلية مرتبطة بالقطعة")
        }
    }

    suspend fun ensurePartDeletable(partId: Long) {
        if (serialDao.countForPart(partId) > 0) {
            throw IllegalStateException("لا يمكن حذف القطعة قبل معالجة أرقامها التسلسلية")
        }
    }

    suspend fun ensureAssetDeletable(assetId: Long) {
        if (serialDao.countForAsset(assetId) > 0) {
            throw IllegalStateException("لا يمكن حذف الأصل أثناء ارتباط رقم تسلسلي به")
        }
    }

    suspend fun saveProfile(profile: SerialNumberProfileEntity, actor: String) {
        require(profile.code.isNotBlank()) { "أدخل كود ملف التتبع" }
        require(profile.name.isNotBlank()) { "أدخل اسم ملف التتبع" }
        require(profile.stockCheckMode in listOf("None", "Warning", "Block")) { "وضع فحص المخزون غير صالح" }
        val normalized = profile.copy(
            code = profile.code.trim().uppercase(),
            name = profile.name.trim(),
            equipmentCategory = profile.equipmentCategory.trim(),
            description = profile.description.trim()
        )
        if (normalized.id == 0L) serialDao.insertProfile(normalized) else serialDao.updateProfile(normalized)
        audit(if (profile.id == 0L) "Create" else "Update", "SerialProfile", "حفظ ملف تتبع ${normalized.code}", actor)
    }

    suspend fun deleteProfile(profile: SerialNumberProfileEntity, actor: String) {
        if (serialDao.countPartsUsingProfile(profile.id) > 0 || serialDao.countForProfile(profile.id) > 0) {
            throw IllegalStateException("ملف التتبع مستخدم ولا يمكن حذفه")
        }
        serialDao.deleteProfile(profile.id)
        audit("Delete", "SerialProfile", "حذف ملف تتبع ${profile.code}", actor)
    }

    suspend fun createMaster(request: SerialMasterRequest, actor: String) {
        val (part, profile) = serializedPart(request.partId)
        val number = normalizeNumber(request.serialNumber)
        require(number.isNotBlank()) { "أدخل الرقم التسلسلي" }
        if (serialDao.findByPartAndNumber(part.id, number) != null) {
            throw IllegalStateException("الرقم $number مسجل مسبقاً لهذه القطعة")
        }
        database.withTransaction {
            val now = DateStrings.now()
            val id = serialDao.insertSerial(
                SerialNumberEntity(
                    partId = part.id,
                    serialNumber = number,
                    profileId = profile.id,
                    status = "Created",
                    createdAt = now,
                    lastMovementAt = now,
                    notes = request.notes.trim()
                )
            )
            val created = serialDao.getSerial(id) ?: throw IllegalStateException("تعذّر إنشاء سجل الرقم التسلسلي")
            movement(null, created, "Create", actor, request.notes.trim())
            audit("Create", "SerialNumber", "إنشاء الرقم $number للقطعة ${part.partNumber}", actor)
        }
    }

    suspend fun receive(request: SerializedReceiptRequest, actor: String) {
        val (part, profile) = serializedPart(request.partId)
        val numbers = request.serialNumbers.map(::normalizeNumber).filter { it.isNotBlank() }.distinct()
        if (profile.requireOnReceipt && numbers.isEmpty()) throw IllegalStateException("أدخل الأرقام التسلسلية المستلمة")
        require(numbers.isNotEmpty()) { "لا توجد أرقام تسلسلية صالحة" }
        require(request.storageLocation.isNotBlank()) { "حدد موقع التخزين" }
        verifyStock(part, profile, actor)

        database.withTransaction {
            val now = DateStrings.now()
            val updatedRecords = mutableListOf<SerialNumberEntity>()
            numbers.forEach { number ->
                val existing = serialDao.findByPartAndNumber(part.id, number)
                if (existing?.status == "InStock") throw IllegalStateException("الرقم $number موجود بالفعل في المخزون")
                if (existing == null && !profile.autoCreate) {
                    throw IllegalStateException("أنشئ سجل الرقم $number أولاً وفق إعداد ملف التتبع")
                }
                if (existing != null) clearAssetLink(existing)
                val candidate = (existing ?: SerialNumberEntity(
                    partId = part.id,
                    serialNumber = number,
                    profileId = profile.id,
                    createdAt = now
                )).copy(
                    profileId = profile.id,
                    assetId = null,
                    currentWorkOrderId = null,
                    status = "InStock",
                    stockType = request.stockType,
                    plant = request.plant.trim(),
                    storageLocation = request.storageLocation.trim(),
                    batch = request.batch.trim(),
                    vendor = request.vendor.trim(),
                    customer = "",
                    salesOrder = "",
                    specialStock = "",
                    lastMovementAt = now
                )
                val saved = if (existing == null) {
                    val id = serialDao.insertSerial(candidate)
                    serialDao.getSerial(id) ?: throw IllegalStateException("تعذّر إنشاء الرقم $number")
                } else {
                    serialDao.updateSerial(candidate)
                    candidate
                }
                movement(existing, saved, "Receive", actor, request.note)
                updatedRecords += saved
            }
            if (partDao.adjustStockSafe(part.id, updatedRecords.size) == 0) {
                throw IllegalStateException("تعذّر تحديث كمية المخزون")
            }
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = part.id,
                    workOrderId = null,
                    transactionType = "Receive",
                    quantity = updatedRecords.size,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = request.note.ifBlank { "استلام وحدات متسلسلة" },
                    serialNumbers = updatedRecords.joinToString(",") { it.serialNumber },
                    stockType = request.stockType,
                    storageLocation = request.storageLocation.trim()
                )
            )
            audit("Receive", "SerialNumber", "استلام ${updatedRecords.size} وحدة من ${part.partNumber}", actor)
        }
    }

    suspend fun issue(request: SerializedIssueRequest, actor: String) {
        val (part, profile) = serializedPart(request.partId)
        val ids = request.serialIds.distinct()
        if (profile.requireOnIssue && ids.isEmpty()) throw IllegalStateException("حدد الأرقام التسلسلية المصروفة")
        require(ids.isNotEmpty()) { "لم يتم تحديد أي وحدة للصرف" }
        verifyStock(part, profile, actor)

        database.withTransaction {
            val serials = ids.map { id -> serialDao.getSerial(id) ?: throw IllegalStateException("سجل تسلسلي غير موجود") }
            if (serials.any { it.partId != part.id }) throw IllegalStateException("الأرقام المحددة لا تتبع القطعة نفسها")
            if (serials.any { !it.isInStock() }) throw IllegalStateException("بعض الوحدات المحددة ليست في المخزون")
            if (partDao.adjustStockSafe(part.id, -serials.size) == 0) {
                throw IllegalStateException("الكمية المطلوبة أكبر من المتوفر")
            }
            val now = DateStrings.now()
            serials.forEach { old ->
                val updated = old.copy(
                    assetId = null,
                    currentWorkOrderId = request.workOrderId,
                    status = "Issued",
                    stockType = "Issued",
                    storageLocation = "",
                    lastMovementAt = now
                )
                serialDao.updateSerial(updated)
                movement(old, updated, "Issue", actor, request.note, request.workOrderId)
            }
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = part.id,
                    workOrderId = request.workOrderId,
                    transactionType = "Issue",
                    quantity = serials.size,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = request.note.ifBlank { "صرف وحدات متسلسلة" },
                    serialNumbers = serials.joinToString(",") { it.serialNumber },
                    stockType = "Issued",
                    storageLocation = ""
                )
            )
            request.workOrderId?.let { orderId ->
                val order = orderDao.getById(orderId) ?: throw IllegalStateException("أمر العمل المحدد غير موجود")
                orderDao.insertWorkOrder(order.copy(partsCost = order.partsCost + serials.size * part.lastPrice))
            }
            audit("Issue", "SerialNumber", "صرف ${serials.size} وحدة من ${part.partNumber}", actor)
        }
    }

    suspend fun transfer(request: SerialTransferRequest, actor: String) {
        val old = serialDao.getSerial(request.serialId) ?: throw IllegalStateException("الرقم التسلسلي غير موجود")
        val (part, profile) = serializedPart(old.partId)
        if (!old.isInStock()) throw IllegalStateException("يمكن نقل الوحدات الموجودة في المخزون فقط")
        if (request.storageLocation.isBlank()) throw IllegalStateException("حدد موقع التخزين الجديد")
        verifyStock(part, profile, actor)
        database.withTransaction {
            val updated = old.copy(
                plant = request.plant.trim(),
                storageLocation = request.storageLocation.trim(),
                stockType = request.stockType,
                batch = request.batch.trim(),
                lastMovementAt = DateStrings.now()
            )
            serialDao.updateSerial(updated)
            movement(old, updated, "Transfer", actor, request.note)
            audit("Transfer", "SerialNumber", "نقل ${old.serialNumber} إلى ${updated.storageLocation}", actor)
        }
    }

    suspend fun install(request: SerialInstallRequest, actor: String) {
        val old = serialDao.getSerial(request.serialId) ?: throw IllegalStateException("الرقم التسلسلي غير موجود")
        val (part, profile) = serializedPart(old.partId)
        if (old.status != "InStock" && old.status != "Issued" && old.status != "Created") {
            throw IllegalStateException("حالة الرقم التسلسلي لا تسمح بالتركيب")
        }
        val asset = assetDao.getAssetById(request.assetId) ?: throw IllegalStateException("الأصل المحدد غير موجود")
        val currentForAsset = serialDao.findByAsset(asset.id)
        if (currentForAsset != null && currentForAsset.id != old.id) {
            throw IllegalStateException("الأصل مرتبط بالفعل بالرقم ${currentForAsset.serialNumber}")
        }
        if (profile.equipmentCategory.isNotBlank() && asset.category.isNotBlank() &&
            !asset.category.equals(profile.equipmentCategory, ignoreCase = true)
        ) {
            throw IllegalStateException("فئة الأصل لا تطابق فئة ملف التتبع")
        }

        if (old.isInStock()) verifyStock(part, profile, actor)
        database.withTransaction {
            if (old.isInStock() && partDao.adjustStockSafe(part.id, -1) == 0) {
                throw IllegalStateException("تعذّر خصم الوحدة من المخزون")
            }
            val updated = old.copy(
                assetId = asset.id,
                currentWorkOrderId = null,
                status = "Installed",
                stockType = "Installed",
                storageLocation = "",
                lastMovementAt = DateStrings.now()
            )
            serialDao.updateSerial(updated)
            assetDao.insertAsset(
                asset.copy(
                    linkedSerialId = updated.id,
                    serializedPartId = updated.partId,
                    serialNumber = updated.serialNumber
                )
            )
            if (old.isInStock()) {
                transactionDao.insert(
                    InventoryTransactionEntity(
                        partId = part.id,
                        workOrderId = null,
                        transactionType = "Issue",
                        quantity = 1,
                        createdAt = DateStrings.today(),
                        createdBy = actor,
                        note = request.note.ifBlank { "تركيب الوحدة على الأصل ${asset.code}" },
                        serialNumbers = updated.serialNumber,
                        stockType = "Installed",
                        storageLocation = ""
                    )
                )
            }
            movement(old, updated, "Install", actor, request.note)
            audit("Install", "SerialNumber", "تركيب ${updated.serialNumber} على ${asset.code}", actor)
        }
    }

    suspend fun dismantle(serialId: Long, note: String, actor: String) {
        val old = serialDao.getSerial(serialId) ?: throw IllegalStateException("الرقم التسلسلي غير موجود")
        if (!old.isInstalled()) throw IllegalStateException("الرقم غير مركب على أصل")
        database.withTransaction {
            clearAssetLink(old)
            val updated = old.copy(
                assetId = null,
                currentWorkOrderId = null,
                status = "Issued",
                stockType = "Issued",
                storageLocation = "",
                lastMovementAt = DateStrings.now()
            )
            serialDao.updateSerial(updated)
            movement(old, updated, "Dismantle", actor, note)
            audit("Dismantle", "SerialNumber", "فك ${updated.serialNumber} من الأصل", actor)
        }
    }

    suspend fun reconcileStock(partId: Long, actor: String) {
        val (part, profile) = serializedPart(partId)
        if (!profile.allowManualStockEdit) {
            throw IllegalStateException("ملف التتبع لا يسمح بالتسوية اليدوية للمخزون")
        }
        val count = serialDao.countInStock(part.id)
        partDao.setStock(part.id, count)
        audit("Reconcile", "SerialStock", "تسوية ${part.partNumber} إلى $count وحدة متسلسلة", actor)
    }

    suspend fun deleteSerial(serial: SerialNumberEntity, actor: String) {
        if (serial.assetId != null || serial.status !in listOf("Created", "Scrapped")) {
            throw IllegalStateException("يمكن حذف السجل غير المخزني وغير المركب فقط")
        }
        database.withTransaction {
            serialDao.deleteMovementsForSerial(serial.id)
            serialDao.deleteSerial(serial.id)
            audit("Delete", "SerialNumber", "حذف الرقم ${serial.serialNumber}", actor)
        }
    }
}
''', encoding="utf-8")

# -----------------------------------------------------------------------------
# Extend existing entities
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/SparePartEntity.kt")
if "import androidx.room.ColumnInfo" not in text:
    text = replace_once(text, "import androidx.room.Entity", "import androidx.room.ColumnInfo\nimport androidx.room.Entity", "SparePart ColumnInfo import")
if "val serializationActive" not in text:
    text = replace_once(
        text,
        '''    val location: String,
    val lastPrice: Double
)''',
        '''    val location: String,
    val lastPrice: Double,
    @ColumnInfo(defaultValue = "0")
    val serializationActive: Boolean = false,
    val serialProfileId: Long? = null
)''',
        "SparePart serialization fields",
    )
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/AssetEntity.kt")
if "val linkedSerialId" not in text:
    text = replace_once(
        text,
        '''    val linearEndLatitude: Double? = null,
    val linearEndLongitude: Double? = null
) {''',
        '''    val linearEndLatitude: Double? = null,
    val linearEndLongitude: Double? = null,
    val linkedSerialId: Long? = null,
    val serializedPartId: Long? = null
) {''',
        "Asset serial link fields",
    )
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/entity/InventoryTransactionEntity.kt")
if "import androidx.room.ColumnInfo" not in text:
    text = replace_once(text, "import androidx.room.Entity", "import androidx.room.ColumnInfo\nimport androidx.room.Entity", "InventoryTransaction ColumnInfo import")
if "val serialNumbers" not in text:
    text = replace_once(
        text,
        '''    val createdAt: String,
    val createdBy: String,
    val note: String
)''',
        '''    val createdAt: String,
    val createdBy: String,
    val note: String,
    @ColumnInfo(defaultValue = "''")
    val serialNumbers: String = "",
    @ColumnInfo(defaultValue = "''")
    val stockType: String = "",
    @ColumnInfo(defaultValue = "''")
    val storageLocation: String = ""
)''',
        "InventoryTransaction serial fields",
    )
save(path, text)

# -----------------------------------------------------------------------------
# DAO helpers for safe stock and order lookup
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/dao/SparePartDao.kt")
if "suspend fun getById" not in text:
    text = replace_once(
        text,
        '''    @Query("SELECT COUNT(*) FROM spare_parts")
    suspend fun countOnce(): Int''',
        '''    @Query("SELECT COUNT(*) FROM spare_parts")
    suspend fun countOnce(): Int

    @Query("SELECT * FROM spare_parts WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): SparePartEntity?''',
        "SparePart getById",
    )
if "adjustStockSafe" not in text:
    text = replace_once(
        text,
        '''    @Query("UPDATE spare_parts SET onHandQty = onHandQty + :delta WHERE id = :id")
    suspend fun adjustStock(id: Long, delta: Int)''',
        '''    @Query("UPDATE spare_parts SET onHandQty = onHandQty + :delta WHERE id = :id")
    suspend fun adjustStock(id: Long, delta: Int)

    @Query("UPDATE spare_parts SET onHandQty = onHandQty + :delta WHERE id = :id AND onHandQty + :delta >= 0")
    suspend fun adjustStockSafe(id: Long, delta: Int): Int

    @Query("UPDATE spare_parts SET onHandQty = :quantity WHERE id = :id")
    suspend fun setStock(id: Long, quantity: Int)''',
        "SparePart safe stock methods",
    )
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/dao/WorkOrderDao.kt")
if "suspend fun getById" not in text:
    text = replace_once(
        text,
        '''    @Query("SELECT COUNT(*) FROM work_orders")
    suspend fun countOnce(): Int''',
        '''    @Query("SELECT COUNT(*) FROM work_orders")
    suspend fun countOnce(): Int

    @Query("SELECT * FROM work_orders WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): WorkOrderEntity?''',
        "WorkOrder getById",
    )
save(path, text)

# -----------------------------------------------------------------------------
# Database registration and migration 26 -> 27
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/AppDatabase.kt")
if "import com.alhadi.cmms.data.dao.SerialNumberDao" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.dao.PmChecklistDao", "import com.alhadi.cmms.data.dao.PmChecklistDao\nimport com.alhadi.cmms.data.dao.SerialNumberDao", "SerialNumberDao import")
if "import com.alhadi.cmms.data.entity.SerialNumberEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity", "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity\nimport com.alhadi.cmms.data.entity.SerialNumberEntity\nimport com.alhadi.cmms.data.entity.SerialNumberMovementEntity\nimport com.alhadi.cmms.data.entity.SerialNumberProfileEntity", "serial entity imports")
if "SerialNumberProfileEntity::class" not in text:
    text = replace_once(
        text,
        '''        PreventiveMaintenanceEntity::class,
        SparePartEntity::class,''',
        '''        PreventiveMaintenanceEntity::class,
        SerialNumberProfileEntity::class,
        SerialNumberEntity::class,
        SerialNumberMovementEntity::class,
        SparePartEntity::class,''',
        "serial entity registration",
    )
if "version = 27" not in text:
    text = replace_once(text, "version = 26", "version = 27", "database version 27")
if "abstract fun serialNumberDao" not in text:
    text = replace_once(text, "    abstract fun sparePartDao(): SparePartDao", "    abstract fun sparePartDao(): SparePartDao\n    abstract fun serialNumberDao(): SerialNumberDao", "serial DAO registration")
save(path, text)

path, text = load("app/src/main/java/com/alhadi/cmms/data/DbMigrations.kt")
if "MIGRATION_26_27" not in text:
    migration = '''

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
    }'''
    text = replace_once(
        text,
        "\n\n    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25, MIGRATION_25_26)",
        migration + "\n\n    val ALL: Array<Migration> = arrayOf(MIGRATION_22_23, MIGRATION_23_24, MIGRATION_24_25, MIGRATION_25_26, MIGRATION_26_27)",
        "migration 26 to 27 registration",
    )
save(path, text)

# -----------------------------------------------------------------------------
# Backup bundle
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/BackupBundle.kt")
if "import com.alhadi.cmms.data.entity.SerialNumberEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity", "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity\nimport com.alhadi.cmms.data.entity.SerialNumberEntity\nimport com.alhadi.cmms.data.entity.SerialNumberMovementEntity\nimport com.alhadi.cmms.data.entity.SerialNumberProfileEntity", "backup serial imports")
if "val serialNumberProfiles" not in text:
    text = replace_once(
        text,
        '''    val preventiveMaintenance: List<PreventiveMaintenanceEntity> = emptyList(),
    val spareParts: List<SparePartEntity> = emptyList(),''',
        '''    val preventiveMaintenance: List<PreventiveMaintenanceEntity> = emptyList(),
    val serialNumberProfiles: List<SerialNumberProfileEntity> = emptyList(),
    val serialNumbers: List<SerialNumberEntity> = emptyList(),
    val serialNumberMovements: List<SerialNumberMovementEntity> = emptyList(),
    val spareParts: List<SparePartEntity> = emptyList(),''',
        "backup serial fields",
    )
    text = replace_once(
        text,
        '''        get() = assets.size + workOrders.size + preventiveMaintenance.size + spareParts.size +''',
        '''        get() = assets.size + workOrders.size + preventiveMaintenance.size + serialNumberProfiles.size +
            serialNumbers.size + serialNumberMovements.size + spareParts.size +''',
        "backup serial total",
    )
    text = replace_once(text, "        const val CURRENT_FORMAT_VERSION = 2", "        const val CURRENT_FORMAT_VERSION = 3", "backup format 3")
save(path, text)

# -----------------------------------------------------------------------------
# Repository integration
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/data/CmmsRepository.kt")
if "import com.alhadi.cmms.data.entity.SerialNumberEntity" not in text:
    text = replace_once(text, "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity", "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity\nimport com.alhadi.cmms.data.entity.SerialNumberEntity\nimport com.alhadi.cmms.data.entity.SerialNumberMovementEntity\nimport com.alhadi.cmms.data.entity.SerialNumberProfileEntity", "repository serial imports")
if "private val serialDao" not in text:
    text = replace_once(text, "    private val sparePartDao = database.sparePartDao()", "    private val sparePartDao = database.sparePartDao()\n    private val serialDao = database.serialNumberDao()", "repository serial DAO")
if "private val serialService" not in text:
    text = replace_once(
        text,
        '''    private val permitDao = database.workPermitDao()

    val assets:''',
        '''    private val permitDao = database.workPermitDao()
    private val serialService = SerialNumberService(database, ::recordAudit)

    val assets:''',
        "repository serial service",
    )
if "val serialNumberProfiles" not in text:
    text = replace_once(
        text,
        '''    val preventiveMaintenance: Flow<List<PreventiveMaintenanceEntity>> = pmDao.observePreventiveMaintenance()
    val spareParts:''',
        '''    val preventiveMaintenance: Flow<List<PreventiveMaintenanceEntity>> = pmDao.observePreventiveMaintenance()
    val serialNumberProfiles: Flow<List<SerialNumberProfileEntity>> = serialService.profiles
    val serialNumbers: Flow<List<SerialNumberEntity>> = serialService.serialNumbers
    val serialNumberMovements: Flow<List<SerialNumberMovementEntity>> = serialService.movements
    val spareParts:''',
        "repository serial flows",
    )

text = text.replace("            appDbVersion = 26,", "            appDbVersion = 27,", 1)
if "serialNumberProfiles = serialDao.dumpProfiles()" not in text:
    text = replace_once(
        text,
        '''            preventiveMaintenance = preventiveMaintenance.first(),
            spareParts = spareParts.first(),''',
        '''            preventiveMaintenance = preventiveMaintenance.first(),
            serialNumberProfiles = serialDao.dumpProfiles(),
            serialNumbers = serialDao.dumpSerialNumbers(),
            serialNumberMovements = serialDao.dumpMovements(),
            spareParts = spareParts.first(),''',
        "backup export serial data",
    )

# Clear serial tables before parent tables in restore and reset.
if "serialDao.deleteAllMovements()" not in text:
    text = replace_once(
        text,
        '''            auditLogDao.deleteAll()
            permitDao.deleteAll()''',
        '''            auditLogDao.deleteAll()
            serialDao.deleteAllMovements()
            serialDao.deleteAllSerialNumbers()
            serialDao.deleteAllProfiles()
            permitDao.deleteAll()''',
        "backup clear serial data",
    )
    text = replace_once(
        text,
        '''                auditLogDao.deleteAll()
                permitDao.deleteAll()''',
        '''                auditLogDao.deleteAll()
                serialDao.deleteAllMovements()
                serialDao.deleteAllSerialNumbers()
                serialDao.deleteAllProfiles()
                permitDao.deleteAll()''',
        "sample reset serial data",
    )

if "serialDao.insertProfiles(bundle.serialNumberProfiles)" not in text:
    text = replace_once(
        text,
        '''            assetDao.insertAssets(bundle.assets)
            userDao.insertAll(bundle.users)
            locationDao.insertAll(bundle.functionalLocations)
            sparePartDao.insertAll(bundle.spareParts)''',
        '''            assetDao.insertAssets(bundle.assets)
            userDao.insertAll(bundle.users)
            locationDao.insertAll(bundle.functionalLocations)
            if (bundle.serialNumberProfiles.isNotEmpty()) serialDao.insertProfiles(bundle.serialNumberProfiles)
            sparePartDao.insertAll(bundle.spareParts)''',
        "backup restore profiles",
    )
    text = replace_once(
        text,
        '''            workOrderDao.insertWorkOrders(bundle.workOrders)
            pmDao.insertAll(bundle.preventiveMaintenance)''',
        '''            workOrderDao.insertWorkOrders(bundle.workOrders)
            if (bundle.serialNumbers.isNotEmpty()) serialDao.insertSerials(bundle.serialNumbers)
            if (bundle.serialNumberMovements.isNotEmpty()) serialDao.insertMovements(bundle.serialNumberMovements)
            pmDao.insertAll(bundle.preventiveMaintenance)''',
        "backup restore serial records",
    )

# Sample profile and serial records.
if "val serialProfiles = listOf(" not in text:
    text = replace_once(
        text,
        '''            val spareParts = listOf(
                SparePartEntity(1, "BRG-6205", "Bearing 6205 ZZ", "Rollermills", "pcs", 12, 6, "Store A-01", 4.5),
                SparePartEntity(2, "BELT-A45", "V-Belt A45", "Bucket Elevators", "pcs", 3, 5, "Store B-03", 8.0),
                SparePartEntity(3, "CHAIN-16B", "Chain 16B", "Chain Conveyors", "meter", 18, 10, "Store C-01", 12.5),
                SparePartEntity(4, "SENSOR-PNP", "PNP Proximity Sensor", "Sensors", "pcs", 2, 4, "Electrical Cabinet", 18.0),
                SparePartEntity(5, "FILTER-GA37", "Compressor Air Filter", "Compressors", "pcs", 7, 3, "Utility Store", 25.0),
                SparePartEntity(6, "BAG-NEEDLE", "Packing Stitching Needle", "Packing Machines", "pcs", 40, 20, "Packing Store", 0.8)
            )''',
        '''            val serialProfiles = listOf(
                SerialNumberProfileEntity(
                    id = 1,
                    code = "S001",
                    name = "تتبع فردي قياسي",
                    requireOnReceipt = true,
                    requireOnIssue = true,
                    autoCreate = true,
                    equipmentRequired = false,
                    stockCheckMode = "Block",
                    allowManualStockEdit = true,
                    description = "تتبع كامل للاستلام والصرف والموقع"
                )
            )

            val spareParts = listOf(
                SparePartEntity(1, "BRG-6205", "Bearing 6205 ZZ", "Rollermills", "pcs", 12, 6, "Store A-01", 4.5),
                SparePartEntity(2, "BELT-A45", "V-Belt A45", "Bucket Elevators", "pcs", 3, 5, "Store B-03", 8.0),
                SparePartEntity(3, "CHAIN-16B", "Chain 16B", "Chain Conveyors", "meter", 18, 10, "Store C-01", 12.5),
                SparePartEntity(4, "SENSOR-PNP", "PNP Proximity Sensor", "Sensors", "pcs", 2, 4, "Electrical Cabinet", 18.0, serializationActive = true, serialProfileId = 1),
                SparePartEntity(5, "FILTER-GA37", "Compressor Air Filter", "Compressors", "pcs", 7, 3, "Utility Store", 25.0),
                SparePartEntity(6, "BAG-NEEDLE", "Packing Stitching Needle", "Packing Machines", "pcs", 40, 20, "Packing Store", 0.8)
            )

            val sampleSerials = listOf(
                SerialNumberEntity(1, 4, "PNP-0001", 1, status = "InStock", stockType = "Unrestricted", plant = "FAC-01", storageLocation = "Electrical Cabinet", createdAt = today, lastMovementAt = today),
                SerialNumberEntity(2, 4, "PNP-0002", 1, status = "InStock", stockType = "Unrestricted", plant = "FAC-01", storageLocation = "Electrical Cabinet", createdAt = today, lastMovementAt = today)
            )

            val sampleSerialMovements = listOf(
                SerialNumberMovementEntity(1, 1, 4, movementType = "Receive", toStatus = "InStock", toPlant = "FAC-01", toStorageLocation = "Electrical Cabinet", toStockType = "Unrestricted", createdAt = today, createdBy = "System", note = "رصيد افتتاحي"),
                SerialNumberMovementEntity(2, 2, 4, movementType = "Receive", toStatus = "InStock", toPlant = "FAC-01", toStorageLocation = "Electrical Cabinet", toStockType = "Unrestricted", createdAt = today, createdBy = "System", note = "رصيد افتتاحي")
            )''',
        "sample serial master data",
    )

if "serialDao.insertProfiles(serialProfiles)" not in text:
    text = replace_once(
        text,
        '''            pmDao.insertAll(preventiveMaintenance)
            sparePartDao.insertAll(spareParts)
            transactionDao.insertAll(transactions)''',
        '''            pmDao.insertAll(preventiveMaintenance)
            serialDao.insertProfiles(serialProfiles)
            sparePartDao.insertAll(spareParts)
            serialDao.insertSerials(sampleSerials)
            serialDao.insertMovements(sampleSerialMovements)
            transactionDao.insertAll(transactions)''',
        "sample serial inserts",
    )

# Delegate serial-number operations.
if "suspend fun saveSerialProfile" not in text:
    delegates = '''

    // ---------------------------------------------------------------------
    // Serial number profiles, units, stock checks, and movement history
    // ---------------------------------------------------------------------

    suspend fun saveSerialProfile(profile: SerialNumberProfileEntity, actor: String = "System") =
        serialService.saveProfile(profile, actor)

    suspend fun deleteSerialProfile(profile: SerialNumberProfileEntity, actor: String = "System") =
        serialService.deleteProfile(profile, actor)

    suspend fun createSerialMaster(request: SerialMasterRequest, actor: String = "System") =
        serialService.createMaster(request, actor)

    suspend fun receiveSerializedPart(request: SerializedReceiptRequest, actor: String = "System") =
        serialService.receive(request, actor)

    suspend fun issueSerializedPart(request: SerializedIssueRequest, actor: String = "System") =
        serialService.issue(request, actor)

    suspend fun transferSerialNumber(request: SerialTransferRequest, actor: String = "System") =
        serialService.transfer(request, actor)

    suspend fun installSerialNumber(request: SerialInstallRequest, actor: String = "System") =
        serialService.install(request, actor)

    suspend fun dismantleSerialNumber(serialId: Long, note: String = "", actor: String = "System") =
        serialService.dismantle(serialId, note, actor)

    suspend fun reconcileSerializedStock(partId: Long, actor: String = "System") =
        serialService.reconcileStock(partId, actor)

    suspend fun deleteSerialNumber(serial: SerialNumberEntity, actor: String = "System") =
        serialService.deleteSerial(serial, actor)
'''
    text = replace_once(text, "\n    // ---------------------------------------------------------------------\n    // Inventory", delegates + "\n\n    // ---------------------------------------------------------------------\n    // Inventory", "serial delegates")

# Replace generic inventory operations with guarded, positive, fresh-stock operations.
start = text.find('    suspend fun issuePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {')
end_marker = '\n\n    // ---------------------------------------------------------------------\n    // Users'
end = text.find(end_marker, start)
if start < 0 or end < 0:
    raise RuntimeError("inventory methods block markers not found")
new_inventory = '''    suspend fun issuePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {
        require(quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
        val current = sparePartDao.getById(part.id) ?: throw IllegalStateException("قطعة الغيار غير موجودة")
        if (current.serializationActive) throw IllegalStateException("استخدم شاشة الأرقام التسلسلية لصرف هذه القطعة")
        database.withTransaction {
            if (sparePartDao.adjustStockSafe(current.id, -quantity) == 0) {
                throw IllegalStateException("الكمية المطلوبة ($quantity) أكبر من المتوفر (${current.onHandQty})")
            }
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = current.id,
                    workOrderId = null,
                    transactionType = "Issue",
                    quantity = quantity,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "صرف يدوي من شاشة المخزون"
                )
            )
            recordAudit("Issue", "Inventory", "صرف $quantity من ${current.partNumber}", actor)
        }
    }

    suspend fun issuePartToWorkOrder(order: WorkOrderEntity, part: SparePartEntity, quantity: Int, actor: String = "System") {
        require(quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
        val current = sparePartDao.getById(part.id) ?: throw IllegalStateException("قطعة الغيار غير موجودة")
        if (current.serializationActive) throw IllegalStateException("استخدم شاشة الأرقام التسلسلية لصرف هذه القطعة لأمر العمل")
        database.withTransaction {
            if (sparePartDao.adjustStockSafe(current.id, -quantity) == 0) {
                throw IllegalStateException("الكمية المطلوبة ($quantity) أكبر من المتوفر (${current.onHandQty})")
            }
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = current.id,
                    workOrderId = order.id,
                    transactionType = "Issue",
                    quantity = quantity,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "صرف لأمر العمل: ${order.title}"
                )
            )
            workOrderDao.insertWorkOrder(order.copy(partsCost = order.partsCost + quantity * current.lastPrice))
            recordAudit("Issue", "Inventory", "صرف $quantity من ${current.partNumber} لأمر العمل #${order.id}", actor)
        }
    }

    suspend fun receivePart(part: SparePartEntity, quantity: Int = 1, actor: String = "System") {
        require(quantity > 0) { "الكمية يجب أن تكون أكبر من صفر" }
        val current = sparePartDao.getById(part.id) ?: throw IllegalStateException("قطعة الغيار غير موجودة")
        if (current.serializationActive) throw IllegalStateException("استخدم شاشة الأرقام التسلسلية لاستلام هذه القطعة")
        database.withTransaction {
            if (sparePartDao.adjustStockSafe(current.id, quantity) == 0) {
                throw IllegalStateException("تعذّر تحديث كمية المخزون")
            }
            transactionDao.insert(
                InventoryTransactionEntity(
                    partId = current.id,
                    workOrderId = null,
                    transactionType = "Receive",
                    quantity = quantity,
                    createdAt = DateStrings.today(),
                    createdBy = actor,
                    note = "استلام يدوي من شاشة المخزون"
                )
            )
            recordAudit("Receive", "Inventory", "استلام $quantity من ${current.partNumber}", actor)
        }
    }'''
text = text[:start] + new_inventory + text[end:]

# Validate serial configuration when saving/deleting parts and linked assets.
text = replace_once(
    text,
    '''    suspend fun savePart(part: SparePartEntity, actor: String = "System") {
        val isNew = part.id == 0L''',
    '''    suspend fun savePart(part: SparePartEntity, actor: String = "System") {
        serialService.validatePartChange(part)
        val isNew = part.id == 0L''',
    "part serial validation",
)
text = replace_once(
    text,
    '''    suspend fun deletePart(part: SparePartEntity, actor: String = "System") {
        sparePartDao.deleteById(part.id)''',
    '''    suspend fun deletePart(part: SparePartEntity, actor: String = "System") {
        serialService.ensurePartDeletable(part.id)
        sparePartDao.deleteById(part.id)''',
    "part serial deletion guard",
)
text = replace_once(
    text,
    '''    suspend fun deleteAsset(asset: AssetEntity, actor: String = "System") {
        database.withTransaction {''',
    '''    suspend fun deleteAsset(asset: AssetEntity, actor: String = "System") {
        serialService.ensureAssetDeletable(asset.id)
        database.withTransaction {''',
    "asset serial deletion guard",
)
save(path, text)

# -----------------------------------------------------------------------------
# ViewModel flows and actions
# -----------------------------------------------------------------------------
path, text = load("app/src/main/java/com/alhadi/cmms/viewmodel/CmmsViewModel.kt")
if "import com.alhadi.cmms.data.SerialInstallRequest" not in text:
    text = replace_once(
        text,
        "import com.alhadi.cmms.data.CmmsRepository",
        "import com.alhadi.cmms.data.CmmsRepository\nimport com.alhadi.cmms.data.SerialInstallRequest\nimport com.alhadi.cmms.data.SerialMasterRequest\nimport com.alhadi.cmms.data.SerialTransferRequest\nimport com.alhadi.cmms.data.SerializedIssueRequest\nimport com.alhadi.cmms.data.SerializedReceiptRequest",
        "view model serial request imports",
    )
if "import com.alhadi.cmms.data.entity.SerialNumberEntity" not in text:
    text = replace_once(
        text,
        "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity",
        "import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity\nimport com.alhadi.cmms.data.entity.SerialNumberEntity\nimport com.alhadi.cmms.data.entity.SerialNumberMovementEntity\nimport com.alhadi.cmms.data.entity.SerialNumberProfileEntity",
        "view model serial entity imports",
    )
if "val serialNumberProfiles" not in text:
    text = replace_once(
        text,
        '''    val preventiveMaintenance: StateFlow<List<PreventiveMaintenanceEntity>> = repository.preventiveMaintenance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val spareParts:''',
        '''    val preventiveMaintenance: StateFlow<List<PreventiveMaintenanceEntity>> = repository.preventiveMaintenance
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val serialNumberProfiles: StateFlow<List<SerialNumberProfileEntity>> = repository.serialNumberProfiles
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val serialNumbers: StateFlow<List<SerialNumberEntity>> = repository.serialNumbers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val serialNumberMovements: StateFlow<List<SerialNumberMovementEntity>> = repository.serialNumberMovements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    val spareParts:''',
        "view model serial flows",
    )
if "fun saveSerialProfile" not in text:
    actions = '''

    // ----- Serial number management -----
    fun saveSerialProfile(profile: SerialNumberProfileEntity) =
        launchAction("تم حفظ ملف التتبع") { repository.saveSerialProfile(profile, actor()) }

    fun deleteSerialProfile(profile: SerialNumberProfileEntity) =
        launchAction("تم حذف ملف التتبع") { repository.deleteSerialProfile(profile, actor()) }

    fun createSerialMaster(request: SerialMasterRequest) =
        launchAction("تم إنشاء الرقم التسلسلي") { repository.createSerialMaster(request, actor()) }

    fun receiveSerializedPart(request: SerializedReceiptRequest) =
        launchAction("تم استلام الوحدات المتسلسلة") { repository.receiveSerializedPart(request, actor()) }

    fun issueSerializedPart(request: SerializedIssueRequest) =
        launchAction("تم صرف الوحدات المتسلسلة") { repository.issueSerializedPart(request, actor()) }

    fun transferSerialNumber(request: SerialTransferRequest) =
        launchAction("تم نقل الرقم التسلسلي") { repository.transferSerialNumber(request, actor()) }

    fun installSerialNumber(request: SerialInstallRequest) =
        launchAction("تم تركيب الرقم التسلسلي") { repository.installSerialNumber(request, actor()) }

    fun dismantleSerialNumber(serialId: Long, note: String = "") =
        launchAction("تم فك الرقم التسلسلي") { repository.dismantleSerialNumber(serialId, note, actor()) }

    fun reconcileSerializedStock(partId: Long) =
        launchAction("تمت تسوية المخزون المتسلسل") { repository.reconcileSerializedStock(partId, actor()) }

    fun deleteSerialNumber(serial: SerialNumberEntity) =
        launchAction("تم حذف الرقم التسلسلي") { repository.deleteSerialNumber(serial, actor()) }
'''
    text = replace_once(text, "\n    // ----- CRUD: Assets -----", actions + "\n\n    // ----- CRUD: Assets -----", "view model serial actions")
save(path, text)

print("Serial number management data stage 5 patch completed successfully.")

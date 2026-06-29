package com.alhadi.cmms.data

import androidx.room.withTransaction
import com.alhadi.cmms.data.cloud.EntityCloudSync
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
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
        val mv = SerialNumberMovementEntity(
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
        val mvId = serialDao.insertMovement(mv)
        // Every serial state change flows through here, so push both the serial and its movement.
        EntityCloudSync.upsert(EntityCloudSync.Collections.SERIAL_NUMBERS, updated.id.toString(), SerialNumberEntity.serializer(), updated)
        EntityCloudSync.upsert(EntityCloudSync.Collections.SERIAL_MOVEMENTS, mvId.toString(), SerialNumberMovementEntity.serializer(), mv.copy(id = mvId))
    }

    private suspend fun clearAssetLink(serial: SerialNumberEntity) {
        val assetId = serial.assetId ?: return
        val asset = assetDao.getAssetById(assetId) ?: return
        if (asset.linkedSerialId == serial.id) {
            val unlinked = asset.copy(
                linkedSerialId = null,
                serializedPartId = null,
                serialNumber = if (asset.serialNumber.equals(serial.serialNumber, ignoreCase = true)) "" else asset.serialNumber
            )
            assetDao.insertAsset(unlinked)
            EntityCloudSync.upsert(EntityCloudSync.Collections.ASSETS, unlinked.id.toString(), AssetEntity.serializer(), unlinked)
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
        val savedId = if (normalized.id == 0L) serialDao.insertProfile(normalized) else { serialDao.updateProfile(normalized); normalized.id }
        EntityCloudSync.upsert(EntityCloudSync.Collections.SERIAL_PROFILES, savedId.toString(), SerialNumberProfileEntity.serializer(), normalized.copy(id = savedId))
        audit(if (profile.id == 0L) "Create" else "Update", "SerialProfile", "حفظ ملف تتبع ${normalized.code}", actor)
    }

    suspend fun deleteProfile(profile: SerialNumberProfileEntity, actor: String) {
        if (serialDao.countPartsUsingProfile(profile.id) > 0 || serialDao.countForProfile(profile.id) > 0) {
            throw IllegalStateException("ملف التتبع مستخدم ولا يمكن حذفه")
        }
        serialDao.deleteProfile(profile.id)
        EntityCloudSync.remove(EntityCloudSync.Collections.SERIAL_PROFILES, profile.id.toString())
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
            val txn = InventoryTransactionEntity(
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
            val txnId = transactionDao.insert(txn)
            EntityCloudSync.upsert(EntityCloudSync.Collections.INVENTORY_TRANSACTIONS, txnId.toString(), InventoryTransactionEntity.serializer(), txn.copy(id = txnId))
            partDao.getById(part.id)?.let { EntityCloudSync.upsert(EntityCloudSync.Collections.SPARE_PARTS, it.id.toString(), SparePartEntity.serializer(), it) }
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
            val txn = InventoryTransactionEntity(
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
            val txnId = transactionDao.insert(txn)
            EntityCloudSync.upsert(EntityCloudSync.Collections.INVENTORY_TRANSACTIONS, txnId.toString(), InventoryTransactionEntity.serializer(), txn.copy(id = txnId))
            partDao.getById(part.id)?.let { EntityCloudSync.upsert(EntityCloudSync.Collections.SPARE_PARTS, it.id.toString(), SparePartEntity.serializer(), it) }
            request.workOrderId?.let { orderId ->
                val order = orderDao.getById(orderId) ?: throw IllegalStateException("أمر العمل المحدد غير موجود")
                val updatedOrder = order.copy(partsCost = order.partsCost + serials.size * part.lastPrice)
                orderDao.insertWorkOrder(updatedOrder)
                EntityCloudSync.upsert(EntityCloudSync.Collections.WORK_ORDERS, updatedOrder.id.toString(), WorkOrderEntity.serializer(), updatedOrder)
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
            val linkedAsset = asset.copy(
                linkedSerialId = updated.id,
                serializedPartId = updated.partId,
                serialNumber = updated.serialNumber
            )
            assetDao.insertAsset(linkedAsset)
            EntityCloudSync.upsert(EntityCloudSync.Collections.ASSETS, linkedAsset.id.toString(), AssetEntity.serializer(), linkedAsset)
            if (old.isInStock()) {
                val txn = InventoryTransactionEntity(
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
                val txnId = transactionDao.insert(txn)
                EntityCloudSync.upsert(EntityCloudSync.Collections.INVENTORY_TRANSACTIONS, txnId.toString(), InventoryTransactionEntity.serializer(), txn.copy(id = txnId))
                partDao.getById(part.id)?.let { EntityCloudSync.upsert(EntityCloudSync.Collections.SPARE_PARTS, it.id.toString(), SparePartEntity.serializer(), it) }
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
        EntityCloudSync.remove(EntityCloudSync.Collections.SERIAL_NUMBERS, serial.id.toString())
    }
}

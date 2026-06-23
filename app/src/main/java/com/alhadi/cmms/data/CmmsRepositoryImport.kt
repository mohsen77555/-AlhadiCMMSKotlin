package com.alhadi.cmms.data

import androidx.room.withTransaction
import com.alhadi.cmms.data.entity.AssetBomHeaderEntity
import com.alhadi.cmms.data.entity.AssetBomItemEntity
import com.alhadi.cmms.data.entity.AssetCharacteristicEntity
import com.alhadi.cmms.data.entity.AssetDocumentEntity
import com.alhadi.cmms.data.entity.AssetEntity
import com.alhadi.cmms.data.entity.AssetMovementEntity
import com.alhadi.cmms.data.entity.AuditLogEntity
import com.alhadi.cmms.data.entity.CapaEntity
import com.alhadi.cmms.data.entity.FunctionalLocationEntity
import com.alhadi.cmms.data.entity.InventoryTransactionEntity
import com.alhadi.cmms.data.entity.MaintenanceNotificationEntity
import com.alhadi.cmms.data.entity.MeasurementReadingEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import com.alhadi.cmms.data.entity.PmChecklistItemEntity
import com.alhadi.cmms.data.entity.PreventiveMaintenanceEntity
import com.alhadi.cmms.data.entity.SerialNumberEntity
import com.alhadi.cmms.data.entity.SerialNumberMovementEntity
import com.alhadi.cmms.data.entity.SerialNumberProfileEntity
import com.alhadi.cmms.data.entity.SparePartEntity
import com.alhadi.cmms.data.entity.TaskListEntity
import com.alhadi.cmms.data.entity.TaskListOperationEntity
import com.alhadi.cmms.data.entity.UserEntity
import com.alhadi.cmms.data.entity.WorkOrderConfirmationEntity
import com.alhadi.cmms.data.entity.WorkOrderEntity
import com.alhadi.cmms.data.entity.WorkOrderOperationEntity
import com.alhadi.cmms.data.entity.WorkOrderPhotoEntity
import com.alhadi.cmms.data.entity.WorkPermitEntity
import com.alhadi.cmms.data.entity.WarehouseEntity
import com.alhadi.cmms.data.entity.OrgUnitEntity
import com.alhadi.cmms.util.DateStrings
import com.alhadi.cmms.util.PasswordHasher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.serialization.json.Json

    /**
     * Imports a machine maintenance-kit workbook (the FVV template) and populates the relevant
     * modules: asset + characteristics, preventive-maintenance plans, a reusable task list with
     * its operations, the spare-parts catalogue, and a generated work order. Returns a summary.
     */
internal suspend fun CmmsRepository.importMachineKit(sheets: Map<String, List<List<String>>>, actor: String = "System"): String {
        return database.withTransaction {
            val today = DateStrings.today()

            // --- Technical data → asset + characteristics ---
            val tech = sheetByKey(sheets, "البيانات الفنية")
            val techRows = tech.drop(1).filter { it.size >= 2 && it[0].isNotBlank() }
            val model = techRows.firstOrNull { it[0].contains("موديل") || it[0].contains("Model") }
                ?.getOrNull(1)?.trim().orEmpty().ifBlank { "Imported Machine" }
            val code = model.trim().replace(Regex("\\s+"), "-").uppercase().ifBlank { "IMPORT-${System.currentTimeMillis() % 100000}" }

            val existing = assetDao.findByCode(code)
            val asset = (existing ?: AssetEntity(
                id = 0, code = code, name = model, groupName = "Vibro Finishers",
                location = "Milling Floor", manufacturer = "OCRIM", model = model,
                status = "Running", criticality = "High", installedAt = today, lastInspectionAt = today
            )).copy(name = model, manufacturer = "OCRIM", model = model)
            val assetId = if (existing != null) { assetDao.insertAsset(asset); existing.id } else assetDao.insertAsset(asset)

            characteristicDao.deleteForAsset(assetId)
            val characteristics = techRows
                .filter { it.size >= 2 && it[1].isNotBlank() && !it[0].contains("البند") }
                .map { AssetCharacteristicEntity(0, assetId, it[0].trim(), it[1].trim(), "") }
            if (characteristics.isNotEmpty()) characteristicDao.insertAll(characteristics)

            // --- Maintenance plan → PM plans ---
            pmDao.deleteForAsset(assetId)
            val planRows = sheetByKey(sheets, "خطة الصيانة").drop(1).filter { it.size >= 2 && it[1].isNotBlank() }
            var plansCount = 0
            planRows.forEach { row ->
                val freq = frequencyDaysFor(row[0])
                pmDao.insert(
                    PreventiveMaintenanceEntity(
                        id = 0, assetId = assetId, title = row[1].trim().take(120),
                        frequencyDays = freq, lastDoneAt = today,
                        nextDueAt = DateStrings.addDays(today, freq), status = "Scheduled",
                        estimatedDurationMinutes = 30
                    )
                )
                plansCount++
            }

            // --- Procedures → task list + template operations ---
            val procRows = sheetByKey(sheets, "إجراءات الصيانة").drop(1).filter { it.isNotEmpty() && it[0].isNotBlank() }
            val taskListId = taskListDao.insertTaskList(
                TaskListEntity(0, "إجراءات صيانة $model", "مستورد من ملف صيانة الآلة", "Mechanical")
            )
            val templateOps = procRows.mapIndexed { i, row ->
                TaskListOperationEntity(0, taskListId, "%04d".format((i + 1) * 10), row[0].trim().take(120), "Mechanical", 1.0)
            }
            if (templateOps.isNotEmpty()) taskListDao.insertOperations(templateOps)

            // --- Spare parts catalogue ---
            val partRows = sheetByKey(sheets, "قطع الغيار").drop(1)
            var partsCount = 0
            val parts = partRows.mapNotNull { row ->
                val partNumber = row.getOrNull(1)?.trim().orEmpty()
                if (partNumber.isBlank() || partNumber == "—" || partNumber == "Code") return@mapNotNull null
                partsCount++
                SparePartEntity(
                    id = 0, partNumber = partNumber, name = row.getOrNull(2)?.trim().orEmpty(),
                    equipmentGroup = code, unit = "pcs", onHandQty = 0, minQty = 0,
                    location = "Store", lastPrice = 0.0
                )
            }
            if (parts.isNotEmpty()) sparePartDao.insertAll(parts)

            // --- Generate a work order from the imported task list ---
            val orderId = workOrderDao.insertWorkOrder(
                WorkOrderEntity(
                    assetId = assetId, title = "صيانة شاملة — $model",
                    description = "أمر عمل مُولّد من ملف الصيانة المستورد", priority = "Medium",
                    status = "Open", assignedTo = actor, createdAt = today,
                    dueAt = DateStrings.addDays(today, 7), estimatedCost = 0.0
                )
            )
            if (templateOps.isNotEmpty()) {
                operationDao.insertAll(
                    templateOps.map {
                        WorkOrderOperationEntity(
                            orderId = orderId, operationNumber = it.operationNumber,
                            description = it.description, workCenter = it.workCenter,
                            plannedHours = it.plannedHours, requiresConfirmation = true, status = "Open"
                        )
                    }
                )
            }

            recordAudit("Import", "System", "استيراد ملف صيانة: $model", actor)
            "تم استيراد $model — ${characteristics.size} خاصية، $plansCount خطة، $partsCount قطعة، قالب عمل (${templateOps.size} عملية)، وأمر عمل."
        }
    }

    /**
     * Generates a work order from a preventive-maintenance plan, copying the linked
     * task list's template operations into the new order (TLIST-006 / SCH-007).
     */
internal suspend fun CmmsRepository.generateWorkOrderFromPm(pm: PreventiveMaintenanceEntity, actor: String = "System") {
        database.withTransaction {
            val today = DateStrings.today()
            val orderId = workOrderDao.insertWorkOrder(
                WorkOrderEntity(
                    assetId = pm.assetId,
                    title = "صيانة دورية: ${pm.title}",
                    description = "مُولّد تلقائياً من الخطة الوقائية",
                    priority = "Medium",
                    status = "Open",
                    assignedTo = actor,
                    createdAt = today,
                    dueAt = pm.nextDueAt,
                    estimatedCost = 0.0
                )
            )
            val template = pm.taskListId?.let { taskListDao.operationsForList(it) } ?: emptyList()
            if (template.isNotEmpty()) {
                operationDao.insertAll(
                    template.map {
                        WorkOrderOperationEntity(
                            orderId = orderId,
                            operationNumber = it.operationNumber,
                            description = it.description,
                            workCenter = it.workCenter,
                            plannedHours = it.plannedHours,
                            requiresConfirmation = true,
                            status = "Open"
                        )
                    }
                )
            }
            recordAudit("Generate", "WorkOrder", "توليد أمر عمل من الصيانة الدورية: ${pm.title} (${template.size} عملية)", actor)
        }
    }

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

    // ---------------------------------------------------------------------
    // Sample data
    // ---------------------------------------------------------------------

internal suspend fun CmmsRepository.seedSampleData(replace: Boolean = false) {
        database.withTransaction {
            if (replace) {
                auditLogDao.deleteAll()
                serialDao.deleteAllMovements()
                serialDao.deleteAllSerialNumbers()
                serialDao.deleteAllProfiles()
                permitDao.deleteAll()
                taskListDao.deleteAllOperations()
                taskListDao.deleteAllTaskLists()
                photoDao.deleteAll()
                confirmationDao.deleteAll()
                operationDao.deleteAll()
                notificationDao.deleteAll()
                checklistDao.deleteAll()
                movementDao.deleteAll()
                bomDao.deleteAll()
                bomHeaderDao.deleteAll()
                characteristicDao.deleteAll()
                documentDao.deleteAll()
                capaDao.deleteAll()
                locationDao.deleteAll()
                measurementDao.deleteAllReadings()
                measurementDao.deleteAllPoints()
                transactionDao.deleteAll()
                pmDao.deleteAll()
                workOrderDao.deleteAll()
                sparePartDao.deleteAll()
                userDao.deleteAll()
                assetDao.deleteAll()
            }

            if (!replace && assetDao.countOnce() > 0) return@withTransaction

            val today = DateStrings.today()
            val assets = listOf(
                AssetEntity(1, "BE-101", "Bucket Elevator 1", "Bucket Elevators", "Silo Area", "Buhler", "BE-18", "Running", "High", "2020-02-15", DateStrings.daysFromToday(-10)),
                AssetEntity(2, "CC-205", "Chain Conveyor 205", "Chain Conveyors", "Raw Wheat Intake", "Mysilo", "CC-250", "Running", "Medium", "2019-08-20", DateStrings.daysFromToday(-18)),
                AssetEntity(3, "SC-077", "Screw Conveyor 77", "Screw Conveyors", "Bran Line", "Ocrim", "SC-220", "Running", "Medium", "2021-05-03", DateStrings.daysFromToday(-5)),
                AssetEntity(4, "SF-030", "Silo Fan 30", "Silo Fans", "Silo Roof", "WAM", "AX-45", "Stopped", "High", "2018-11-11", DateStrings.daysFromToday(-40)),
                AssetEntity(5, "SG-014", "Silo Gate 14", "Silo Gates", "Silo Bottom", "Custom", "Slide-400", "Running", "Medium", "2022-01-18", DateStrings.daysFromToday(-12), parentAssetId = 1),
                AssetEntity(6, "SN-400", "Level Sensor 400", "Sensors", "Flour Bin", "Siemens", "SITRANS", "Warning", "High", "2023-03-21", DateStrings.daysFromToday(-2), warrantyProvider = "Siemens", warrantyStart = "2023-03-21", warrantyEnd = DateStrings.daysFromToday(60), parentAssetId = 4),
                AssetEntity(7, "RM-01", "Rollermill C1", "Rollermills", "Milling Floor", "Buhler", "Antares", "Running", "Critical", "2017-09-10", DateStrings.daysFromToday(-7), serialNumber = "BUH-ANT-2017-0091", assetTag = "TAG-RM01", supplier = "Buhler AG", purchaseOrder = "PO-2017-114", purchaseCost = 285000.0, acquiredAt = "2017-08-01"),
                AssetEntity(8, "PL-04", "Plansifter 4", "Plansifters", "Milling Floor", "Buhler", "MPA", "Running", "Critical", "2017-10-02", DateStrings.daysFromToday(-21)),
                AssetEntity(9, "PR-02", "Purifier 2", "Purifiers", "Milling Floor", "Golfetto", "Pur-2", "Running", "High", "2018-04-19", DateStrings.daysFromToday(-16)),
                AssetEntity(10, "CP-01", "Air Compressor 1", "Compressors", "Utility Room", "Atlas Copco", "GA-37", "Running", "Critical", "2016-12-12", DateStrings.daysFromToday(-8), serialNumber = "AC-GA37-2016-0455", assetTag = "TAG-CP01", supplier = "Atlas Copco", purchaseOrder = "PO-2016-088", purchaseCost = 96000.0, acquiredAt = "2016-11-20"),
                AssetEntity(11, "PK-01", "Packing Machine 1", "Packing Machines", "Packing Hall", "Fawema", "FA-10", "Warning", "High", "2020-07-07", DateStrings.daysFromToday(-31), warrantyProvider = "Fawema", warrantyStart = "2020-07-07", warrantyEnd = DateStrings.daysFromToday(15)),
                AssetEntity(12, "SCAL-01", "Truck Scale 1", "Scales", "Gate", "Mettler Toledo", "TS-60", "Running", "Medium", "2021-02-09", DateStrings.daysFromToday(-14))
            )

            val users = listOf(
                UserEntity(1, "Mohsen Alhadi", "mohsen", "Admin", true, "admin"),
                UserEntity(2, "Maintenance Supervisor", "supervisor", "Supervisor", true, "1234"),
                UserEntity(3, "Electrical Technician", "electrical", "Technician", true, "1234"),
                UserEntity(4, "Mechanical Technician", "mechanical", "Technician", true, "1234")
            )

            val workOrders = listOf(
                WorkOrderEntity(1, 7, "Check rollermill vibration", "Inspect bearings, belts, and abnormal noise on RM-01.", "High", "Open", "Mechanical Technician", today, DateStrings.daysFromToday(1), 120.0),
                WorkOrderEntity(2, 4, "Silo fan not starting", "Check overload, contactor, motor insulation, and fan impeller.", "High", "In Progress", "Electrical Technician", DateStrings.daysFromToday(-1), today, 250.0, isFailure = true, downtimeHours = 6.0, requiresPermit = true),
                WorkOrderEntity(3, 11, "Packing machine bag sensor alarm", "Clean and align sensor, verify signal in control panel.", "Medium", "Open", "Electrical Technician", today, DateStrings.daysFromToday(2), 1500.0, approvalStatus = "Pending"),
                WorkOrderEntity(4, 2, "Chain conveyor lubrication", "Lubricate chain and inspect tension.", "Low", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-7), DateStrings.daysFromToday(-5), 30.0, "Completed and tested."),
                WorkOrderEntity(5, 4, "Silo fan bearing failure", "Replaced damaged fan bearing.", "High", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-45), DateStrings.daysFromToday(-44), 180.0, "Bearing replaced.", isFailure = true, downtimeHours = 8.0, laborHours = 6.0, laborRate = 25.0, partsCost = 90.0),
                WorkOrderEntity(6, 4, "Silo fan motor overheat", "Cleaned and re-greased motor, fixed ventilation.", "High", "Closed", "Electrical Technician", DateStrings.daysFromToday(-90), DateStrings.daysFromToday(-89), 120.0, "Motor serviced.", isFailure = true, downtimeHours = 5.0),
                WorkOrderEntity(7, 7, "Rollermill jam", "Cleared product jam and inspected rolls.", "Critical", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-30), DateStrings.daysFromToday(-30), 90.0, "Cleared.", isFailure = true, downtimeHours = 3.5),
                WorkOrderEntity(8, 7, "Rollermill belt break", "Replaced broken drive belt.", "Critical", "Closed", "Mechanical Technician", DateStrings.daysFromToday(-75), DateStrings.daysFromToday(-75), 150.0, "Belt replaced.", isFailure = true, downtimeHours = 4.0, laborHours = 3.0, laborRate = 25.0, partsCost = 70.0)
            )

            val preventiveMaintenance = listOf(
                PreventiveMaintenanceEntity(1, 1, "Inspect buckets and belt tension", 30, DateStrings.daysFromToday(-35), DateStrings.daysFromToday(-5), "Due", 90),
                PreventiveMaintenanceEntity(2, 7, "Rollermill weekly inspection", 7, DateStrings.daysFromToday(-6), DateStrings.daysFromToday(1), "Scheduled", 45, taskListId = 1),
                PreventiveMaintenanceEntity(3, 8, "Plansifter sieve and frame inspection", 14, DateStrings.daysFromToday(-18), DateStrings.daysFromToday(-4), "Due", 60),
                PreventiveMaintenanceEntity(4, 10, "Compressor oil and air filter check", 30, DateStrings.daysFromToday(-22), DateStrings.daysFromToday(8), "Scheduled", 50),
                PreventiveMaintenanceEntity(5, 12, "Scale calibration verification", 90, DateStrings.daysFromToday(-70), DateStrings.daysFromToday(20), "Scheduled", 120)
            )

            val serialProfiles = listOf(
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
            )

            val transactions = listOf(
                InventoryTransactionEntity(1, 1, 1, "Issue", 2, DateStrings.daysFromToday(-2), "Mohsen Alhadi", "Issued for RM-01 vibration check."),
                InventoryTransactionEntity(2, 3, 4, "Issue", 1, DateStrings.daysFromToday(-5), "Maintenance Supervisor", "Issued for CC-205 lubrication."),
                InventoryTransactionEntity(3, 5, null, "Receive", 4, DateStrings.daysFromToday(-8), "Store Keeper", "Monthly purchase receipt.")
            )

            val measuringPoints = listOf(
                MeasuringPointEntity(1, 7, "Running Hours", "hr", true, 12000.0, 8450.0, today),
                MeasuringPointEntity(2, 7, "Bearing Vibration", "mm/s", false, 11.0, 6.4, today),
                MeasuringPointEntity(3, 10, "Compressor Hours", "hr", true, 20000.0, 15230.0, today),
                MeasuringPointEntity(4, 4, "Motor Temperature", "°C", false, 80.0, 72.0, today),
                MeasuringPointEntity(5, 1, "Belt Tension", "N", false, 500.0, 410.0, today)
            )

            val capaActions = listOf(
                CapaEntity(1, "CAPA-001", "تكرار اهتزاز مطحنة الأسطوانات", "Corrective", "تحليل السبب الجذري للاهتزاز المتكرر على RM-01 ومعالجته.", 7, "High", "In Progress", "Maintenance Supervisor", DateStrings.daysFromToday(2), DateStrings.daysFromToday(-3)),
                CapaEntity(2, "CAPA-002", "خطة وقائية لمروحة الصومعة", "Preventive", "وضع خطة فحص دوري لمنع تكرار توقف SF-030.", 4, "Medium", "Open", "Electrical Technician", DateStrings.daysFromToday(7), DateStrings.daysFromToday(-1)),
                CapaEntity(3, "CAPA-003", "مراجعة معايرة الميزان", "Corrective", "انحراف في قراءات الميزان يتطلب إجراء تصحيحي وتوثيق.", 12, "Low", "Closed", "Mohsen Alhadi", DateStrings.daysFromToday(-10), DateStrings.daysFromToday(-20))
            )

            val locations = listOf(
                FunctionalLocationEntity(1, "FAC-01", "Flour Mill Factory", null, "المصنع الرئيسي"),
                FunctionalLocationEntity(2, "SILO", "Silo Area", 1, "منطقة الصوامع"),
                FunctionalLocationEntity(3, "MILL", "Milling Floor", 1, "صالة الطحن"),
                FunctionalLocationEntity(4, "PACK", "Packing Hall", 1, "صالة التعبئة"),
                FunctionalLocationEntity(5, "UTIL", "Utility Room", 1, "غرفة المرافق"),
                FunctionalLocationEntity(6, "MILL-RM", "Rollermill Station", 3, "محطة مطاحن الأسطوانات")
            )

            assetDao.insertAssets(assets)
            userDao.insertAll(users.map { it.copy(password = PasswordHasher.hash(it.password)) })
            workOrderDao.insertWorkOrders(workOrders)
            pmDao.insertAll(preventiveMaintenance)
            serialDao.insertProfiles(serialProfiles)
            sparePartDao.insertAll(spareParts)
            serialDao.insertSerials(sampleSerials)
            serialDao.insertMovements(sampleSerialMovements)
            transactionDao.insertAll(transactions)
            val documents = listOf(
                AssetDocumentEntity(1, 7, "Manual", "دليل تشغيل Rollermill", "https://example.com/rm01-manual.pdf", "Mohsen Alhadi", today),
                AssetDocumentEntity(2, 7, "Drawing", "مخطط كهربائي RM-01", "internal://drawings/rm01.dwg", "Maintenance Supervisor", today),
                AssetDocumentEntity(3, 10, "Certificate", "شهادة فحص الضاغط", "https://example.com/cp01-cert.pdf", "Mohsen Alhadi", today)
            )

            val characteristics = listOf(
                AssetCharacteristicEntity(1, 7, "Capacity", "6", "rolls"),
                AssetCharacteristicEntity(2, 7, "Motor Power", "37", "kW"),
                AssetCharacteristicEntity(3, 10, "Working Pressure", "8", "bar"),
                AssetCharacteristicEntity(4, 10, "Air Flow", "6.5", "m3/min"),
                AssetCharacteristicEntity(5, 4, "Voltage", "400", "V")
            )

            val bomHeaders = listOf(
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
            )

            val movements = listOf(
                AssetMovementEntity(1, 7, MovementType.INSTALL, null, 6, "", "Rollermill Station", "تركيب أولي في الموقع", "Mohsen Alhadi", today),
                AssetMovementEntity(2, 10, MovementType.INSTALL, null, 5, "", "Utility Room", "تركيب الضاغط", "Mohsen Alhadi", today),
                AssetMovementEntity(3, 4, MovementType.TRANSFER, 5, 3, "Utility Room", "Milling Floor", "نقل اللوحة الكهربائية", "Maintenance Supervisor", today)
            )

            val checklist = listOf(
                PmChecklistItemEntity(1, 2, "فحص اهتزاز وضجيج الأسطوانات", "", "", 1),
                PmChecklistItemEntity(2, 2, "فحص شدّ السيور", "", "", 2),
                PmChecklistItemEntity(3, 2, "تشحيم المحامل", "", "", 3),
                PmChecklistItemEntity(4, 2, "تنظيف نقاط التغذية", "", "", 4),
                PmChecklistItemEntity(5, 4, "فحص مستوى الزيت", "", "", 1),
                PmChecklistItemEntity(6, 4, "تنظيف/تبديل فلتر الهواء", "", "", 2),
                PmChecklistItemEntity(7, 4, "فحص تسريبات الهواء", "", "", 3)
            )

            val notifications = listOf(
                MaintenanceNotificationEntity(1, "NTF-0001", "Breakdown", "اهتزاز غير طبيعي في المطحنة", "اهتزاز وضجيج عالٍ أثناء التشغيل على RM-01.", 7, "Critical", "Vibration", "Bearing wear", "Mohsen Alhadi", today, DateStrings.daysFromToday(1), "Approved", null),
                MaintenanceNotificationEntity(2, "NTF-0002", "Corrective", "تسريب هواء من الضاغط", "انخفاض ضغط ملحوظ في خط الهواء.", 10, "High", "Leak", "Seal failure", "Maintenance Supervisor", today, DateStrings.daysFromToday(2), "New", null),
                MaintenanceNotificationEntity(3, "NTF-0003", "Inspection", "ملاحظة أثناء جولة الفحص", "صوت خفيف من ناقل السلسلة، يُنصح بالفحص.", 2, "Medium", "Noise", "", "Electrical Technician", today, "", "Screened", null)
            )

            val operations = listOf(
                WorkOrderOperationEntity(1, 1, "0010", "عزل الطاقة وتأمين المعدة", "Mechanical", 0.5, 0.0, true, "Open"),
                WorkOrderOperationEntity(2, 1, "0020", "فحص المحامل والسيور", "Mechanical", 1.5, 0.0, true, "Open"),
                WorkOrderOperationEntity(3, 1, "0030", "إعادة التشغيل والاختبار", "Mechanical", 0.5, 0.0, true, "Open"),
                WorkOrderOperationEntity(4, 2, "0010", "فحص الحمل الزائد والكونتاكتور", "Electrical", 1.0, 1.0, true, "Confirmed"),
                WorkOrderOperationEntity(5, 2, "0020", "فحص عزل المحرك", "Electrical", 1.0, 0.0, true, "In Progress")
            )

            val confirmations = listOf(
                WorkOrderConfirmationEntity(1, 2, 4, "Electrical Technician", today, 1.0, "فحص الحمل الزائد واستبدال الكونتاكتور", "Contactor failure", "Electrical wear", "تم استبدال الكونتاكتور واختبار التشغيل", 2.0, true, today)
            )

            val taskLists = listOf(
                TaskListEntity(1, "فحص أسبوعي للمطاحن", "قالب فحص دوري لمطاحن الأسطوانات", "Mechanical")
            )
            val taskListOps = listOf(
                TaskListOperationEntity(1, 1, "0010", "تنظيف وتفقّد الأسطوانات", "Mechanical", 0.5),
                TaskListOperationEntity(2, 1, "0020", "فحص المحامل والتشحيم", "Mechanical", 1.0),
                TaskListOperationEntity(3, 1, "0030", "فحص شدّ السيور وضبطها", "Mechanical", 0.5)
            )

            val permits = listOf(
                WorkPermitEntity(1, 2, "LOTO", "طاقة كهربائية مخزّنة، أجزاء دوّارة", "قفازات عازلة، نظارة واقية", "Approved", "Maintenance Supervisor", DateStrings.daysFromToday(2), "Mohsen Alhadi", today)
            )

            val warehouses = listOf(
                WarehouseEntity(1, "WH-MAIN", "المستودع الرئيسي", "المبنى الإداري", "Mohsen Alhadi", "", "Main", "Active", "قطع الغيار الرئيسية"),
                WarehouseEntity(2, "WH-SPARE", "مستودع قطع الغيار", "صالة الطحن", "", "", "Spare", "Active", ""),
                WarehouseEntity(3, "WH-TOOLS", "مخزن العدد والأدوات", "ورشة الصيانة", "", "", "Tools", "Active", "")
            )

            val orgUnits = listOf(
                OrgUnitEntity(1, "Company", "ALHADI", "مجموعة الهادي", "Active", null, ""),
                OrgUnitEntity(8, "Site", "SITE-RYD", "موقع الرياض", "Active", 1, ""),
                OrgUnitEntity(2, "Plant", "PLANT-01", "مصنع الطحن الرئيسي", "Active", 8, ""),
                OrgUnitEntity(9, "MaintenancePlant", "MP-01", "جهة صيانة المصنع", "Active", 2, ""),
                OrgUnitEntity(10, "PlanningPlant", "PP-01", "جهة تخطيط الصيانة", "Active", 9, ""),
                OrgUnitEntity(7, "Department", "DEP-MNT", "إدارة الصيانة", "Active", 2, ""),
                OrgUnitEntity(5, "CostCenter", "CC-1000", "مركز تكلفة الصيانة", "Active", 7, ""),
                OrgUnitEntity(6, "PlannerGroup", "PG-01", "مجموعة تخطيط الصيانة", "Active", 10, ""),
                OrgUnitEntity(3, "WorkCenter", "WC-MECH", "مركز العمل الميكانيكي", "Active", 2, ""),
                OrgUnitEntity(4, "WorkCenter", "WC-ELEC", "مركز العمل الكهربائي", "Active", 2, ""),
                OrgUnitEntity(11, "StorageLocation", "SL-01", "مستودع قطع الغيار", "Active", 2, "")
            )

            measurementDao.insertPoints(measuringPoints)
            locationDao.insertAll(locations)
            warehouseDao.insertAll(warehouses)
            orgUnitDao.insertAll(orgUnits)
            capaDao.insertAll(capaActions)
            documentDao.insertAll(documents)
            characteristicDao.insertAll(characteristics)
            bomHeaderDao.insertAll(bomHeaders)
            bomDao.insertAll(bomItems)
            movementDao.insertAll(movements)
            checklistDao.insertAll(checklist)
            notificationDao.insertAll(notifications)
            operationDao.insertAll(operations)
            confirmationDao.insertAll(confirmations)
            taskListDao.insertTaskLists(taskLists)
            taskListDao.insertOperations(taskListOps)
            permitDao.insertAll(permits)
            recordAudit("Seed", "System", "تم تجهيز البيانات التجريبية", "System")
        }
    }


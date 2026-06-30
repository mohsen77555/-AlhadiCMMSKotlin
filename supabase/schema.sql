-- Al-Hadi CMMS — Supabase Postgres schema (generated from Room entities).
-- Phase 0. Apply in the Supabase SQL editor. ids are client-supplied (match Room).

create table if not exists public.asset_bom_headers (
  "id" bigint primary key,
  "assetId" bigint,
  "code" text not null,
  "name" text not null,
  "category" text not null,
  "usage" text not null,
  "alternative" text not null,
  "status" text not null,
  "validFrom" text not null,
  "validTo" text not null,
  "revision" text not null,
  "assignmentType" text not null,
  "constructionType" text not null,
  "description" text not null
);

create table if not exists public.asset_bom_items (
  "id" bigint primary key,
  "assetId" bigint not null,
  "partId" bigint not null,
  "quantity" integer not null,
  "headerId" bigint not null,
  "itemNumber" integer not null,
  "itemCategory" text not null,
  "status" text not null,
  "validFrom" text not null,
  "validTo" text not null,
  "isCritical" boolean not null,
  "useInOrders" boolean not null,
  "notes" text not null,
  "parentItemId" bigint,
  "assemblyAssetId" bigint,
  "alternativeGroup" text not null,
  "isAlternative" boolean not null
);

create table if not exists public.asset_characteristics (
  "id" bigint primary key,
  "assetId" bigint not null,
  "name" text not null,
  "value" text not null,
  "unit" text not null,
  "className" text not null,
  "dataType" text not null,
  "allowedValues" text not null,
  "isRequired" boolean not null
);

create table if not exists public.asset_documents (
  "id" bigint primary key,
  "assetId" bigint not null,
  "type" text not null,
  "title" text not null,
  "reference" text not null,
  "uploadedBy" text not null,
  "uploadedAt" text not null
);

create table if not exists public.assets (
  "id" bigint primary key,
  "code" text not null,
  "name" text not null,
  "groupName" text not null,
  "location" text not null,
  "manufacturer" text not null,
  "model" text not null,
  "status" text not null,
  "criticality" text not null,
  "installedAt" text not null,
  "lastInspectionAt" text not null,
  "locationId" bigint,
  "warrantyProvider" text not null,
  "warrantyStart" text not null,
  "warrantyEnd" text not null,
  "parentAssetId" bigint,
  "serialNumber" text not null,
  "assetTag" text not null,
  "supplier" text not null,
  "purchaseOrder" text not null,
  "purchaseCost" double precision not null,
  "acquiredAt" text not null,
  "category" text not null,
  "objectType" text not null,
  "description" text not null,
  "maintenancePlant" text not null,
  "planningPlant" text not null,
  "plannerGroup" text not null,
  "mainWorkCenter" text not null,
  "productionWorkCenter" text not null,
  "costCenter" text not null,
  "responsiblePerson" text not null,
  "assetNumber" text not null,
  "constructionYear" text not null,
  "constructionMonth" text not null,
  "startupDate" text not null,
  "partnerName" text not null,
  "partnerRole" text not null,
  "partnerPhone" text not null,
  "partnerEmail" text not null,
  "addressLine" text not null,
  "city" text not null,
  "country" text not null,
  "standardClass" text not null,
  "constructionType" text not null,
  "inheritParentCharacteristics" boolean not null,
  "isLinearAsset" boolean not null,
  "linearStartPoint" double precision not null,
  "linearEndPoint" double precision not null,
  "linearUnit" text not null,
  "linearReferencePattern" text not null,
  "linearRouteCode" text not null,
  "linearStartMarker" text not null,
  "linearEndMarker" text not null,
  "linearStartMarkerDistance" double precision not null,
  "linearEndMarkerDistance" double precision not null,
  "linearMarkerUnit" text not null,
  "linearHorizontalOffset" double precision not null,
  "linearVerticalOffset" double precision not null,
  "linearOffsetUnit" text not null,
  "linearDirection" text not null,
  "networkObjectCode" text not null,
  "networkObjectType" text not null,
  "networkRelation" text not null,
  "networkAttributes" text not null,
  "linearStartLatitude" double precision,
  "linearStartLongitude" double precision,
  "linearEndLatitude" double precision,
  "linearEndLongitude" double precision,
  "linkedSerialId" bigint,
  "serializedPartId" bigint,
  "longDescription" text not null,
  "alternativeLabel" text not null,
  "externalAssetCode" text not null,
  "legacyAssetCode" text not null,
  "barcode" text not null,
  "qrCode" text not null,
  "equipmentCategory" text not null,
  "assetClass" text not null,
  "assetSubclass" text not null,
  "company" text not null,
  "site" text not null,
  "safetyCritical" boolean not null,
  "riskLevel" text not null,
  "requiredPermits" text not null,
  "safetyInstructions" text not null,
  "ppeRequired" text not null,
  "isolationRequired" boolean not null,
  "complianceRequirements" text not null,
  "financialStatus" text not null,
  "bookValue" double precision not null,
  "capitalizationAt" text not null,
  "countryOfOrigin" text not null,
  "nameplateData" text not null,
  "capacity" text not null,
  "power" text not null,
  "voltage" text not null,
  "current" text not null,
  "frequency" text not null,
  "speed" text not null,
  "pressure" text not null,
  "flowRate" text not null,
  "temperatureRange" text not null,
  "weight" text not null,
  "dimensions" text not null,
  "material" text not null,
  "designStandard" text not null,
  "technicalSpecGroup" text not null,
  "requiresSerialTracking" boolean not null,
  "warrantyType" text not null,
  "warrantyCategory" text not null,
  "warrantyTerms" text not null,
  "coveredServices" text not null,
  "excludedServices" text not null,
  "warrantyCounterType" text not null,
  "warrantyCounterLimit" double precision not null,
  "warrantyClaimRequired" boolean not null,
  "warrantyClaimStatus" text not null,
  "warrantyContact" text not null,
  "warrantyDocument" text not null,
  "vendorWarranty" boolean not null,
  "manufacturerWarranty" boolean not null,
  "customerWarranty" boolean not null,
  "warrantyReference" text not null,
  "mobility" text not null,
  "incursOperatingCost" boolean not null,
  "orgOverrideReason" text not null
);

create table if not exists public.asset_installations (
  "id" bigint primary key,
  "assetId" bigint not null,
  "locationId" bigint,
  "locationCode" text not null,
  "eventType" text not null,
  "eventDate" text not null,
  "performedBy" text not null,
  "reason" text not null,
  "createdAt" text not null
);

create table if not exists public.asset_movements (
  "id" bigint primary key,
  "assetId" bigint not null,
  "eventType" text not null,
  "fromLocationId" bigint,
  "toLocationId" bigint,
  "fromLocationName" text not null,
  "toLocationName" text not null,
  "notes" text not null,
  "performedBy" text not null,
  "occurredAt" text not null
);

create table if not exists public.asset_status_history (
  "id" bigint primary key,
  "assetId" bigint not null,
  "fromStatus" text not null,
  "toStatus" text not null,
  "reason" text not null,
  "changedBy" text not null,
  "changedAt" text not null
);

create table if not exists public.audit_log (
  "id" bigint primary key,
  "action" text not null,
  "entityType" text not null,
  "details" text not null,
  "performedBy" text not null,
  "createdAt" text not null
);

create table if not exists public.capa_actions (
  "id" bigint primary key,
  "code" text not null,
  "title" text not null,
  "type" text not null,
  "description" text not null,
  "assetId" bigint,
  "priority" text not null,
  "status" text not null,
  "assignedTo" text not null,
  "dueAt" text not null,
  "createdAt" text not null
);

create table if not exists public.functional_locations (
  "id" bigint primary key,
  "code" text not null,
  "name" text not null,
  "parentId" bigint,
  "description" text not null,
  "status" text not null,
  "plantCode" text not null,
  "workCenterCode" text not null,
  "costCenterCode" text not null,
  "plannerGroupCode" text not null,
  "category" text not null,
  "lifecycleStatus" text not null,
  "abcIndicator" text not null,
  "sortField" text not null,
  "authorizationGroup" text not null,
  "singleInstallation" boolean not null,
  "isReference" boolean not null,
  "referenceCode" text not null,
  "room" text not null,
  "plantSection" text not null
);

create table if not exists public.inventory_transactions (
  "id" bigint primary key,
  "partId" bigint not null,
  "workOrderId" bigint,
  "transactionType" text not null,
  "quantity" integer not null,
  "createdAt" text not null,
  "createdBy" text not null,
  "note" text not null,
  "serialNumbers" text not null,
  "stockType" text not null,
  "storageLocation" text not null
);

create table if not exists public.maintenance_notifications (
  "id" bigint primary key,
  "number" text not null,
  "type" text not null,
  "title" text not null,
  "description" text not null,
  "assetId" bigint,
  "priority" text not null,
  "damageCode" text not null,
  "causeCode" text not null,
  "reportedBy" text not null,
  "reportedAt" text not null,
  "requiredEnd" text not null,
  "status" text not null,
  "linkedOrderId" bigint,
  "linearStartPoint" double precision,
  "linearEndPoint" double precision,
  "linearMarker" text not null,
  "linearHorizontalOffset" double precision,
  "linearVerticalOffset" double precision,
  "breakdown" boolean not null,
  "effectCode" text not null,
  "malfunctionStart" text not null,
  "malfunctionEnd" text not null,
  "acknowledgedAt" text not null,
  "acknowledgedBy" text not null,
  "closedAt" text not null,
  "closedBy" text not null
);

create table if not exists public.measurement_readings (
  "id" bigint primary key,
  "pointId" bigint not null,
  "assetId" bigint not null,
  "value" double precision not null,
  "createdAt" text not null,
  "createdBy" text not null,
  "note" text not null,
  "status" text not null
);

create table if not exists public.measuring_points (
  "id" bigint primary key,
  "assetId" bigint not null,
  "name" text not null,
  "unit" text not null,
  "isCounter" boolean not null,
  "upperLimit" double precision,
  "lastReading" double precision not null,
  "lastReadingAt" text not null,
  "lowerLimit" double precision,
  "warningMargin" double precision not null,
  "autoNotifyOnAlarm" boolean not null
);

create table if not exists public.org_units (
  "id" bigint primary key,
  "type" text not null,
  "code" text not null,
  "name" text not null,
  "status" text not null,
  "parentId" bigint,
  "notes" text not null,
  "shortName" text not null,
  "legalName" text not null,
  "taxNumber" text not null,
  "commercialRegistration" text not null,
  "country" text not null,
  "region" text not null,
  "city" text not null,
  "address" text not null,
  "phone" text not null,
  "email" text not null,
  "website" text not null,
  "latitude" double precision,
  "longitude" double precision,
  "capacity" text not null,
  "supervisor" text not null,
  "manager" text not null
);

create table if not exists public.pm_checklist_items (
  "id" bigint primary key,
  "pmId" bigint not null,
  "text" text not null,
  "result" text not null,
  "note" text not null,
  "orderIndex" integer not null
);

create table if not exists public.preventive_maintenance (
  "id" bigint primary key,
  "assetId" bigint not null,
  "title" text not null,
  "frequencyDays" integer not null,
  "lastDoneAt" text not null,
  "nextDueAt" text not null,
  "status" text not null,
  "estimatedDurationMinutes" integer not null,
  "taskListId" bigint,
  "scheduleType" text not null,
  "measuringPointId" bigint,
  "counterInterval" double precision not null,
  "lastCounterReading" double precision not null,
  "nextCounterReading" double precision not null,
  "callHorizonDays" integer not null,
  "priority" text not null,
  "floatingSchedule" boolean not null,
  "planActive" boolean not null,
  "strategy" text not null
);

create table if not exists public.purchase_orders (
  "id" bigint primary key,
  "poNumber" text not null,
  "supplierId" bigint not null,
  "supplierName" text not null,
  "status" text not null,
  "orderDate" text not null,
  "expectedDate" text not null,
  "currency" text not null,
  "totalAmount" double precision not null,
  "warehouse" text not null,
  "notes" text not null,
  "createdBy" text not null,
  "approvedBy" text not null,
  "cancelledReason" text not null
);

create table if not exists public.purchase_order_lines (
  "id" bigint primary key,
  "poId" bigint not null,
  "partId" bigint,
  "partNumber" text not null,
  "description" text not null,
  "quantity" integer not null,
  "unitPrice" double precision not null,
  "receivedQty" integer not null
);

create table if not exists public.serial_numbers (
  "id" bigint primary key,
  "partId" bigint not null,
  "serialNumber" text not null,
  "profileId" bigint,
  "assetId" bigint,
  "currentWorkOrderId" bigint,
  "status" text not null,
  "stockType" text not null,
  "plant" text not null,
  "storageLocation" text not null,
  "batch" text not null,
  "vendor" text not null,
  "customer" text not null,
  "salesOrder" text not null,
  "specialStock" text not null,
  "createdAt" text not null,
  "lastMovementAt" text not null,
  "notes" text not null
);

create table if not exists public.serial_number_movements (
  "id" bigint primary key,
  "serialId" bigint not null,
  "partId" bigint not null,
  "workOrderId" bigint,
  "movementType" text not null,
  "fromStatus" text not null,
  "toStatus" text not null,
  "fromPlant" text not null,
  "toPlant" text not null,
  "fromStorageLocation" text not null,
  "toStorageLocation" text not null,
  "fromStockType" text not null,
  "toStockType" text not null,
  "createdAt" text not null,
  "createdBy" text not null,
  "note" text not null
);

create table if not exists public.serial_number_profiles (
  "id" bigint primary key,
  "code" text not null,
  "name" text not null,
  "requireOnReceipt" boolean not null,
  "requireOnIssue" boolean not null,
  "autoCreate" boolean not null,
  "equipmentRequired" boolean not null,
  "stockCheckMode" text not null,
  "allowManualStockEdit" boolean not null,
  "equipmentCategory" text not null,
  "description" text not null
);

create table if not exists public.spare_parts (
  "id" bigint primary key,
  "partNumber" text not null,
  "name" text not null,
  "equipmentGroup" text not null,
  "unit" text not null,
  "onHandQty" integer not null,
  "minQty" integer not null,
  "location" text not null,
  "lastPrice" double precision not null,
  "serializationActive" boolean not null,
  "serialProfileId" bigint,
  "maxQty" integer not null,
  "reorderQty" integer not null,
  "safetyStock" integer not null,
  "leadTimeDays" integer not null,
  "abcClass" text not null,
  "preferredSupplierId" bigint
);

create table if not exists public.suppliers (
  "id" bigint primary key,
  "code" text not null,
  "name" text not null,
  "contactPerson" text not null,
  "phone" text not null,
  "email" text not null,
  "address" text not null,
  "category" text not null,
  "taxNumber" text not null,
  "paymentTerms" text not null,
  "rating" integer not null,
  "status" text not null,
  "notes" text not null
);

create table if not exists public.task_lists (
  "id" bigint primary key,
  "name" text not null,
  "description" text not null,
  "defaultWorkCenter" text not null
);

create table if not exists public.task_list_operations (
  "id" bigint primary key,
  "taskListId" bigint not null,
  "operationNumber" text not null,
  "description" text not null,
  "workCenter" text not null,
  "plannedHours" double precision not null
);

create table if not exists public.users (
  "id" bigint primary key,
  "name" text not null,
  "username" text not null,
  "role" text not null,
  "isActive" boolean not null,
  "password" text not null,
  "email" text not null,
  "phone" text not null,
  "department" text not null,
  "employeeId" text not null,
  "lastLoginAt" text not null,
  "createdAt" text not null,
  "passwordChangedAt" text not null,
  "mustChangePassword" boolean not null,
  "failedLoginCount" integer not null,
  "locked" boolean not null,
  "craft" text not null,
  "assignedGroups" text not null
);

create table if not exists public.warehouses (
  "id" bigint primary key,
  "code" text not null,
  "name" text not null,
  "location" text not null,
  "keeper" text not null,
  "phone" text not null,
  "type" text not null,
  "status" text not null,
  "notes" text not null
);

create table if not exists public.work_order_confirmations (
  "id" bigint primary key,
  "orderId" bigint not null,
  "operationId" bigint not null,
  "technician" text not null,
  "workDate" text not null,
  "actualWork" double precision not null,
  "activityText" text not null,
  "damageFound" text not null,
  "causeFound" text not null,
  "actionTaken" text not null,
  "downtime" double precision not null,
  "finalConfirmation" boolean not null,
  "createdAt" text not null,
  "overtimeHours" double precision not null
);

create table if not exists public.work_orders (
  "id" bigint primary key,
  "assetId" bigint not null,
  "title" text not null,
  "description" text not null,
  "priority" text not null,
  "status" text not null,
  "assignedTo" text not null,
  "createdAt" text not null,
  "dueAt" text not null,
  "estimatedCost" double precision not null,
  "closeNotes" text not null,
  "isFailure" boolean not null,
  "downtimeHours" double precision not null,
  "laborHours" double precision not null,
  "laborRate" double precision not null,
  "partsCost" double precision not null,
  "approvalStatus" text not null,
  "approvedBy" text not null,
  "requiresPermit" boolean not null,
  "linearStartPoint" double precision,
  "linearEndPoint" double precision,
  "linearMarker" text not null,
  "linearHorizontalOffset" double precision,
  "linearVerticalOffset" double precision,
  "repairType" text not null,
  "warrantyReviewed" boolean not null,
  "warrantyReviewResult" text not null,
  "type" text not null,
  "companyCode" text not null,
  "siteCode" text not null,
  "plantCode" text not null,
  "maintenancePlantCode" text not null,
  "planningPlantCode" text not null,
  "plannerGroup" text not null,
  "workCenter" text not null,
  "costCenter" text not null,
  "assetCode" text not null,
  "assetName" text not null,
  "functionalLocation" text not null,
  "failureCode" text not null,
  "failureCause" text not null,
  "failureEffect" text not null,
  "rootCause" text not null,
  "plannedStart" text not null,
  "cancelledReason" text not null,
  "closedAt" text not null,
  "closedBy" text not null,
  "notificationId" bigint,
  "sourcePmId" bigint
);

create table if not exists public.work_order_history (
  "id" bigint primary key,
  "orderId" bigint not null,
  "field" text not null,
  "oldValue" text not null,
  "newValue" text not null,
  "actor" text not null,
  "changedAt" text not null
);

create table if not exists public.work_order_materials (
  "id" bigint primary key,
  "orderId" bigint not null,
  "partId" bigint,
  "partNumber" text not null,
  "description" text not null,
  "plannedQty" integer not null,
  "issuedQty" integer not null,
  "unitPrice" double precision not null
);

create table if not exists public.work_order_operations (
  "id" bigint primary key,
  "orderId" bigint not null,
  "operationNumber" text not null,
  "description" text not null,
  "workCenter" text not null,
  "plannedHours" double precision not null,
  "actualHours" double precision not null,
  "requiresConfirmation" boolean not null,
  "status" text not null,
  "sequence" integer not null
);

create table if not exists public.work_order_photos (
  "id" bigint primary key,
  "orderId" bigint not null,
  "path" text not null,
  "caption" text not null,
  "addedBy" text not null,
  "addedAt" text not null
);

create table if not exists public.work_permits (
  "id" bigint primary key,
  "orderId" bigint not null,
  "type" text not null,
  "hazards" text not null,
  "ppe" text not null,
  "status" text not null,
  "approvedBy" text not null,
  "validUntil" text not null,
  "createdBy" text not null,
  "createdAt" text not null
);

-- 36 tables generated.

package com.alhadi.cmms.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.alhadi.cmms.data.entity.AssetEntity

/**
 * Mutable holder for every field of the asset add/edit form. Kept as a single
 * state object so the form composable and its sections can share one source of
 * truth instead of threading ~75 value/setter pairs.
 */
internal class AssetFormState(initial: AssetEntity?) {
    var code by mutableStateOf(initial?.code ?: "")
    var name by mutableStateOf(initial?.name ?: "")
    var group by mutableStateOf(initial?.groupName ?: "")
    var location by mutableStateOf(initial?.location ?: "")
    var manufacturer by mutableStateOf(initial?.manufacturer ?: "")
    var model by mutableStateOf(initial?.model ?: "")
    var status by mutableStateOf(initial?.status ?: "Running")
    var criticality by mutableStateOf(initial?.criticality ?: "Medium")
    var locationId by mutableStateOf(initial?.locationId)
    var mobility by mutableStateOf(initial?.mobility ?: "Fixed")
    var incursOperatingCost by mutableStateOf(initial?.incursOperatingCost ?: false)
    var orgOverrideReason by mutableStateOf(initial?.orgOverrideReason ?: "")
    var parentAssetId by mutableStateOf(initial?.parentAssetId)
    var warrantyProvider by mutableStateOf(initial?.warrantyProvider ?: "")
    var warrantyStart by mutableStateOf(initial?.warrantyStart ?: "")
    var warrantyEnd by mutableStateOf(initial?.warrantyEnd ?: "")
    var warrantyType by mutableStateOf(initial?.warrantyType ?: "")
    var warrantyCategory by mutableStateOf(initial?.warrantyCategory ?: "")
    var warrantyTerms by mutableStateOf(initial?.warrantyTerms ?: "")
    var coveredServices by mutableStateOf(initial?.coveredServices ?: "")
    var excludedServices by mutableStateOf(initial?.excludedServices ?: "")
    var warrantyCounterType by mutableStateOf(initial?.warrantyCounterType ?: "")
    var warrantyCounterLimit by mutableStateOf((initial?.warrantyCounterLimit ?: 0.0).toString())
    var warrantyClaimRequired by mutableStateOf(initial?.warrantyClaimRequired ?: false)
    var warrantyClaimStatus by mutableStateOf(initial?.warrantyClaimStatus ?: "")
    var warrantyContact by mutableStateOf(initial?.warrantyContact ?: "")
    var warrantyDocument by mutableStateOf(initial?.warrantyDocument ?: "")
    var vendorWarranty by mutableStateOf(initial?.vendorWarranty ?: false)
    var manufacturerWarranty by mutableStateOf(initial?.manufacturerWarranty ?: false)
    var customerWarranty by mutableStateOf(initial?.customerWarranty ?: false)
    var warrantyReference by mutableStateOf(initial?.warrantyReference ?: "")
    var serialNumber by mutableStateOf(initial?.serialNumber ?: "")
    var assetTag by mutableStateOf(initial?.assetTag ?: "")
    var supplier by mutableStateOf(initial?.supplier ?: "")
    var purchaseOrder by mutableStateOf(initial?.purchaseOrder ?: "")
    var purchaseCost by mutableStateOf((initial?.purchaseCost ?: 0.0).toString())
    var acquiredAt by mutableStateOf(initial?.acquiredAt ?: "")
    var category by mutableStateOf(initial?.category ?: "Machine")
    var objectType by mutableStateOf(initial?.objectType ?: "")
    var description by mutableStateOf(initial?.description ?: "")
    var maintenancePlant by mutableStateOf(initial?.maintenancePlant ?: "")
    var planningPlant by mutableStateOf(initial?.planningPlant ?: "")
    var plannerGroup by mutableStateOf(initial?.plannerGroup ?: "")
    var mainWorkCenter by mutableStateOf(initial?.mainWorkCenter ?: "")
    var productionWorkCenter by mutableStateOf(initial?.productionWorkCenter ?: "")
    var costCenter by mutableStateOf(initial?.costCenter ?: "")
    var responsiblePerson by mutableStateOf(initial?.responsiblePerson ?: "")
    var assetNumber by mutableStateOf(initial?.assetNumber ?: "")
    var constructionYear by mutableStateOf(initial?.constructionYear ?: "")
    var constructionMonth by mutableStateOf(initial?.constructionMonth ?: "")
    var startupDate by mutableStateOf(initial?.startupDate ?: "")
    var partnerName by mutableStateOf(initial?.partnerName ?: "")
    var partnerRole by mutableStateOf(initial?.partnerRole ?: "")
    var partnerPhone by mutableStateOf(initial?.partnerPhone ?: "")
    var partnerEmail by mutableStateOf(initial?.partnerEmail ?: "")
    var addressLine by mutableStateOf(initial?.addressLine ?: "")
    var city by mutableStateOf(initial?.city ?: "")
    var country by mutableStateOf(initial?.country ?: "")
    var standardClass by mutableStateOf(initial?.standardClass ?: "")
    var constructionType by mutableStateOf(initial?.constructionType ?: "")
    var inheritParentCharacteristics by mutableStateOf(initial?.inheritParentCharacteristics ?: true)
    var isLinearAsset by mutableStateOf(initial?.isLinearAsset ?: false)
    var linearStartPoint by mutableStateOf(formatLinearNumber(initial?.linearStartPoint ?: 0.0))
    var linearEndPoint by mutableStateOf(formatLinearNumber(initial?.linearEndPoint ?: 0.0))
    var linearUnit by mutableStateOf(initial?.linearUnit ?: "km")
    var linearReferencePattern by mutableStateOf(initial?.linearReferencePattern ?: "")
    var linearRouteCode by mutableStateOf(initial?.linearRouteCode ?: "")
    var linearStartMarker by mutableStateOf(initial?.linearStartMarker ?: "")
    var linearEndMarker by mutableStateOf(initial?.linearEndMarker ?: "")
    var linearStartMarkerDistance by mutableStateOf(formatLinearNumber(initial?.linearStartMarkerDistance ?: 0.0))
    var linearEndMarkerDistance by mutableStateOf(formatLinearNumber(initial?.linearEndMarkerDistance ?: 0.0))
    var linearMarkerUnit by mutableStateOf(initial?.linearMarkerUnit ?: "km")
    var linearHorizontalOffset by mutableStateOf(formatLinearNumber(initial?.linearHorizontalOffset ?: 0.0))
    var linearVerticalOffset by mutableStateOf(formatLinearNumber(initial?.linearVerticalOffset ?: 0.0))
    var linearOffsetUnit by mutableStateOf(initial?.linearOffsetUnit ?: "m")
    var linearDirection by mutableStateOf(initial?.linearDirection ?: "Both")
    var networkObjectCode by mutableStateOf(initial?.networkObjectCode ?: "")
    var networkObjectType by mutableStateOf(initial?.networkObjectType ?: "")
    var networkRelation by mutableStateOf(initial?.networkRelation ?: "")
    var networkAttributes by mutableStateOf(initial?.networkAttributes ?: "")
    var linearStartLatitude by mutableStateOf(initial?.linearStartLatitude?.let(::formatLinearNumber) ?: "")
    var linearStartLongitude by mutableStateOf(initial?.linearStartLongitude?.let(::formatLinearNumber) ?: "")
    var linearEndLatitude by mutableStateOf(initial?.linearEndLatitude?.let(::formatLinearNumber) ?: "")
    var linearEndLongitude by mutableStateOf(initial?.linearEndLongitude?.let(::formatLinearNumber) ?: "")
    // Asset identity governance
    var longDescription by mutableStateOf(initial?.longDescription ?: "")
    var alternativeLabel by mutableStateOf(initial?.alternativeLabel ?: "")
    var externalAssetCode by mutableStateOf(initial?.externalAssetCode ?: "")
    var legacyAssetCode by mutableStateOf(initial?.legacyAssetCode ?: "")
    var barcode by mutableStateOf(initial?.barcode ?: "")
    var qrCode by mutableStateOf(initial?.qrCode ?: "")
    var equipmentCategory by mutableStateOf(initial?.equipmentCategory ?: "")
    var assetClass by mutableStateOf(initial?.assetClass ?: "")
    var assetSubclass by mutableStateOf(initial?.assetSubclass ?: "")
    var company by mutableStateOf(initial?.company ?: "")
    var site by mutableStateOf(initial?.site ?: "")
    var safetyCritical by mutableStateOf(initial?.safetyCritical ?: false)
    var riskLevel by mutableStateOf(initial?.riskLevel ?: "")
    var requiredPermits by mutableStateOf(initial?.requiredPermits ?: "")
    var safetyInstructions by mutableStateOf(initial?.safetyInstructions ?: "")
    var ppeRequired by mutableStateOf(initial?.ppeRequired ?: "")
    var isolationRequired by mutableStateOf(initial?.isolationRequired ?: false)
    var complianceRequirements by mutableStateOf(initial?.complianceRequirements ?: "")
    var financialStatus by mutableStateOf(initial?.financialStatus ?: "")
    var bookValue by mutableStateOf((initial?.bookValue ?: 0.0).toString())
    var capitalizationAt by mutableStateOf(initial?.capitalizationAt ?: "")
    // Manufacturing & technical specifications
    var countryOfOrigin by mutableStateOf(initial?.countryOfOrigin ?: "")
    var nameplateData by mutableStateOf(initial?.nameplateData ?: "")
    var capacity by mutableStateOf(initial?.capacity ?: "")
    var power by mutableStateOf(initial?.power ?: "")
    var voltage by mutableStateOf(initial?.voltage ?: "")
    var current by mutableStateOf(initial?.current ?: "")
    var frequency by mutableStateOf(initial?.frequency ?: "")
    var speed by mutableStateOf(initial?.speed ?: "")
    var pressure by mutableStateOf(initial?.pressure ?: "")
    var flowRate by mutableStateOf(initial?.flowRate ?: "")
    var temperatureRange by mutableStateOf(initial?.temperatureRange ?: "")
    var weight by mutableStateOf(initial?.weight ?: "")
    var dimensions by mutableStateOf(initial?.dimensions ?: "")
    var material by mutableStateOf(initial?.material ?: "")
    var designStandard by mutableStateOf(initial?.designStandard ?: "")
    var technicalSpecGroup by mutableStateOf(initial?.technicalSpecGroup ?: "")
    var requiresSerialTracking by mutableStateOf(initial?.requiresSerialTracking ?: false)
}

/** Builds an [AssetEntity] from the current form state for the given lifecycle status. */
internal fun AssetFormState.toAssetEntity(
    initial: AssetEntity?,
    statusValue: String,
    today: String,
    serialLocked: Boolean,
    warrantyDatesLocked: Boolean,
    hasOrgOverride: Boolean,
    linearStartValue: Double?,
    linearEndValue: Double?
): AssetEntity =
                    AssetEntity(
                    id = initial?.id ?: 0,
                    code = code.trim(),
                    name = name.trim(),
                    groupName = group.ifBlank { "General" },
                    location = location,
                    manufacturer = manufacturer,
                    model = model,
                    status = statusValue,
                    criticality = criticality,
                    installedAt = initial?.installedAt ?: today,
                    lastInspectionAt = initial?.lastInspectionAt ?: today,
                    locationId = locationId,
                    warrantyProvider = warrantyProvider.trim(),
                    parentAssetId = parentAssetId,
                    serialNumber = if (serialLocked) (initial?.serialNumber ?: "") else serialNumber.trim(),
                    assetTag = assetTag.trim(),
                    supplier = supplier.trim(),
                    purchaseOrder = purchaseOrder.trim(),
                    purchaseCost = purchaseCost.toDoubleOrNull() ?: 0.0,
                    acquiredAt = acquiredAt.trim(),
                    category = category,
                    objectType = objectType.trim(),
                    description = description.trim(),
                    maintenancePlant = maintenancePlant.trim(),
                    planningPlant = planningPlant.trim(),
                    plannerGroup = plannerGroup.trim(),
                    mainWorkCenter = mainWorkCenter.trim(),
                    productionWorkCenter = productionWorkCenter.trim(),
                    costCenter = costCenter.trim(),
                    responsiblePerson = responsiblePerson.trim(),
                    assetNumber = assetNumber.trim(),
                    constructionYear = constructionYear.trim(),
                    constructionMonth = constructionMonth.trim(),
                    startupDate = startupDate.trim(),
                    partnerName = partnerName.trim(),
                    partnerRole = partnerRole,
                    partnerPhone = partnerPhone.trim(),
                    partnerEmail = partnerEmail.trim(),
                    addressLine = addressLine.trim(),
                    city = city.trim(),
                    country = country.trim(),
                    standardClass = standardClass.trim(),
                    constructionType = constructionType.trim(),
                    inheritParentCharacteristics = inheritParentCharacteristics,
                    isLinearAsset = isLinearAsset,
                    linearStartPoint = if (isLinearAsset) linearStartValue ?: 0.0 else 0.0,
                    linearEndPoint = if (isLinearAsset) linearEndValue ?: 0.0 else 0.0,
                    linearUnit = linearUnit,
                    linearReferencePattern = if (isLinearAsset) linearReferencePattern.trim() else "",
                    linearRouteCode = if (isLinearAsset) linearRouteCode.trim() else "",
                    linearStartMarker = if (isLinearAsset) linearStartMarker.trim() else "",
                    linearEndMarker = if (isLinearAsset) linearEndMarker.trim() else "",
                    linearStartMarkerDistance = if (isLinearAsset) linearStartMarkerDistance.toDoubleOrNull() ?: 0.0 else 0.0,
                    linearEndMarkerDistance = if (isLinearAsset) linearEndMarkerDistance.toDoubleOrNull() ?: 0.0 else 0.0,
                    linearMarkerUnit = linearMarkerUnit,
                    linearHorizontalOffset = if (isLinearAsset) linearHorizontalOffset.toDoubleOrNull() ?: 0.0 else 0.0,
                    linearVerticalOffset = if (isLinearAsset) linearVerticalOffset.toDoubleOrNull() ?: 0.0 else 0.0,
                    linearOffsetUnit = linearOffsetUnit,
                    linearDirection = linearDirection,
                    networkObjectCode = if (isLinearAsset) networkObjectCode.trim() else "",
                    networkObjectType = if (isLinearAsset) networkObjectType else "",
                    networkRelation = if (isLinearAsset) networkRelation else "",
                    networkAttributes = if (isLinearAsset) networkAttributes.trim() else "",
                    linearStartLatitude = if (isLinearAsset) linearStartLatitude.toDoubleOrNull() else null,
                    linearStartLongitude = if (isLinearAsset) linearStartLongitude.toDoubleOrNull() else null,
                    linearEndLatitude = if (isLinearAsset) linearEndLatitude.toDoubleOrNull() else null,
                    linearEndLongitude = if (isLinearAsset) linearEndLongitude.toDoubleOrNull() else null,
                    longDescription = longDescription.trim(),
                    alternativeLabel = alternativeLabel.trim(),
                    externalAssetCode = externalAssetCode.trim(),
                    legacyAssetCode = legacyAssetCode.trim(),
                    barcode = barcode.trim(),
                    qrCode = qrCode.trim(),
                    equipmentCategory = equipmentCategory.trim(),
                    assetClass = assetClass.trim(),
                    assetSubclass = assetSubclass.trim(),
                    company = company.trim(),
                    site = site.trim(),
                    safetyCritical = safetyCritical,
                    riskLevel = riskLevel,
                    requiredPermits = requiredPermits.trim(),
                    safetyInstructions = safetyInstructions.trim(),
                    ppeRequired = ppeRequired.trim(),
                    isolationRequired = isolationRequired,
                    complianceRequirements = complianceRequirements.trim(),
                    financialStatus = financialStatus,
                    bookValue = bookValue.toDoubleOrNull() ?: 0.0,
                    capitalizationAt = capitalizationAt.trim(),
                    countryOfOrigin = countryOfOrigin.trim(),
                    nameplateData = nameplateData.trim(),
                    capacity = capacity.trim(),
                    power = power.trim(),
                    voltage = voltage.trim(),
                    current = current.trim(),
                    frequency = frequency.trim(),
                    speed = speed.trim(),
                    pressure = pressure.trim(),
                    flowRate = flowRate.trim(),
                    temperatureRange = temperatureRange.trim(),
                    weight = weight.trim(),
                    dimensions = dimensions.trim(),
                    material = material.trim(),
                    designStandard = designStandard.trim(),
                    technicalSpecGroup = technicalSpecGroup.trim(),
                    requiresSerialTracking = requiresSerialTracking,
                    warrantyType = warrantyType,
                    warrantyCategory = warrantyCategory.trim(),
                    warrantyTerms = warrantyTerms.trim(),
                    coveredServices = coveredServices.trim(),
                    excludedServices = excludedServices.trim(),
                    warrantyCounterType = warrantyCounterType,
                    warrantyCounterLimit = warrantyCounterLimit.toDoubleOrNull() ?: 0.0,
                    warrantyClaimRequired = warrantyClaimRequired,
                    warrantyClaimStatus = if (warrantyClaimRequired) warrantyClaimStatus else "",
                    warrantyContact = warrantyContact.trim(),
                    warrantyDocument = warrantyDocument.trim(),
                    vendorWarranty = vendorWarranty,
                    manufacturerWarranty = manufacturerWarranty,
                    customerWarranty = customerWarranty,
                    warrantyReference = warrantyReference.trim(),
                    // AST-WAR-003: preserve locked dates when the user cannot override them.
                    warrantyStart = if (warrantyDatesLocked) (initial?.warrantyStart ?: "") else warrantyStart.trim(),
                    warrantyEnd = if (warrantyDatesLocked) (initial?.warrantyEnd ?: "") else warrantyEnd.trim(),
                    mobility = mobility,
                    incursOperatingCost = incursOperatingCost,
                    orgOverrideReason = if (hasOrgOverride) orgOverrideReason.trim() else ""
                )

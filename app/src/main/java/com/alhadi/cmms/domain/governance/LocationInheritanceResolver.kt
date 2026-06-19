package com.alhadi.cmms.domain.governance

import com.alhadi.cmms.data.entity.FunctionalLocationEntity

/** Effective defaults resolved from a location, its reference location, and its parents. */
data class EffectiveLocationDefaults(
    val locationName: String = "",
    val organizationCode: String = "",
    val plantCode: String = "",
    val costCenterCode: String = "",
    val workCenterCode: String = "",
    val ownerDepartment: String = "",
    val planningGroup: String = "",
    val criticality: String = "",
    val assetType: String = "",
    val maintenanceStrategy: String = "",
    val sourceChain: List<String> = emptyList()
)

object LocationInheritanceResolver {
    fun resolve(locationId: Long?, locations: List<FunctionalLocationEntity>): EffectiveLocationDefaults {
        if (locationId == null) return EffectiveLocationDefaults()
        val byId = locations.associateBy { it.id }
        val selected = byId[locationId] ?: return EffectiveLocationDefaults()
        val chain = mutableListOf<FunctionalLocationEntity>()
        val visited = mutableSetOf<Long>()

        fun addLocation(id: Long?) {
            if (id == null || !visited.add(id)) return
            val location = byId[id] ?: return
            chain += location
            if (location.inheritFromReference) addLocation(location.referenceLocationId)
            if (location.inheritFromParent) addLocation(location.parentId)
        }

        addLocation(selected.id)
        fun firstText(selector: (FunctionalLocationEntity) -> String): String =
            chain.firstNotNullOfOrNull { selector(it).takeIf(String::isNotBlank) }.orEmpty()

        return EffectiveLocationDefaults(
            locationName = selected.name,
            organizationCode = firstText { it.organizationCode },
            plantCode = firstText { it.plantCode },
            costCenterCode = firstText { it.costCenterCode },
            workCenterCode = firstText { it.workCenterCode },
            ownerDepartment = firstText { it.defaultOwnerDepartment },
            planningGroup = firstText { it.defaultPlanningGroup },
            criticality = firstText { it.defaultCriticality },
            assetType = firstText { it.defaultAssetType },
            maintenanceStrategy = firstText { it.defaultMaintenanceStrategy },
            sourceChain = chain.map { it.code }
        )
    }

    fun detectCycle(location: FunctionalLocationEntity, locations: List<FunctionalLocationEntity>): Boolean {
        val byId = locations.associateBy { it.id }
        val visited = mutableSetOf<Long>()
        var cursor = location.parentId
        while (cursor != null) {
            if (cursor == location.id || !visited.add(cursor)) return true
            cursor = byId[cursor]?.parentId
        }
        return false
    }
}

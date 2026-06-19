package com.alhadi.cmms.domain.governance

import com.alhadi.cmms.data.entity.SerializedItemEntity

object SerialGovernance {
    private val transitions = mapOf(
        "InStock" to setOf("Installed", "Reserved", "Repair", "Scrapped"),
        "Reserved" to setOf("InStock", "Installed", "Repair"),
        "Installed" to setOf("InStock", "Repair", "Scrapped"),
        "Repair" to setOf("InStock", "Installed", "Scrapped"),
        "Scrapped" to emptySet()
    )

    fun validate(item: SerializedItemEntity) {
        require(item.serialNumber.trim().isNotEmpty()) { "Serial number is required." }
        require(item.itemType in setOf("Asset", "SparePart", "Rotable", "Tool")) { "Unsupported serialized item type." }
        require(item.assetId != null || item.partId != null) { "Serialized item must reference an asset or spare part." }
        require(item.status in transitions.keys) { "Unsupported serialized item status." }
    }

    fun canTransition(from: String, to: String): Boolean =
        from == to || transitions[from]?.contains(to) == true

    fun requireTransition(from: String, to: String) {
        if (!canTransition(from, to)) throw IllegalStateException("Illegal serial status transition: $from -> $to")
    }
}

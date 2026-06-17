package com.alhadi.cmms.data

/**
 * Lifecycle / movement event types for an asset. Used by AssetMovementEntity and the
 * Asset 360 history timeline. Values are stored as-is in the database; Arabic labels are
 * resolved for display only.
 */
object MovementType {
    const val INSTALL = "Install"
    const val TRANSFER = "Transfer"
    const val DISMANTLE = "Dismantle"
    const val RETIRE = "Retire"

    val all = listOf(INSTALL, TRANSFER, DISMANTLE, RETIRE)

    fun label(type: String): String = when (type) {
        INSTALL -> "تركيب"
        TRANSFER -> "نقل"
        DISMANTLE -> "فك"
        RETIRE -> "تقاعد"
        else -> type
    }
}

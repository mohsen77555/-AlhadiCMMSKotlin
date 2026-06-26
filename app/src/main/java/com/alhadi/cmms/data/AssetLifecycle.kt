package com.alhadi.cmms.data

/**
 * Equipment lifecycle governance (EQ-LIFE-010..020).
 *
 * Defines the allowed status transitions of an asset (equipment master). A change is only
 * permitted when the target status is listed among the current status' allowed successors.
 * "Disposed" is a terminal status (scrapped) — no further transitions are possible.
 */
object AssetLifecycle {

    /** Canonical ordered list of equipment statuses. */
    val ALL = listOf(
        "Draft", "Active", "Running", "Warning", "Stopped",
        "Under Maintenance", "Standby", "Retired", "Disposed"
    )

    private val transitions: Map<String, List<String>> = mapOf(
        "Draft" to listOf("Active", "Retired"),
        "Active" to listOf("Running", "Standby", "Under Maintenance", "Stopped", "Warning", "Retired"),
        "Running" to listOf("Warning", "Stopped", "Under Maintenance", "Standby", "Active"),
        "Warning" to listOf("Running", "Stopped", "Under Maintenance", "Active"),
        "Stopped" to listOf("Under Maintenance", "Active", "Running", "Retired"),
        "Under Maintenance" to listOf("Active", "Running", "Standby", "Stopped"),
        "Standby" to listOf("Active", "Running", "Under Maintenance", "Retired"),
        "Retired" to listOf("Disposed", "Active"),
        "Disposed" to emptyList()
    )

    /** Statuses an asset currently in [status] may move to (excludes the current status). */
    fun allowedNext(status: String): List<String> = transitions[status] ?: ALL.filter { it != status }

    /** Whether moving from [from] to [to] is a governed, legal transition. */
    fun canTransition(from: String, to: String): Boolean =
        from != to && to in allowedNext(from)

    /** Terminal (scrapped) status — the asset has left service permanently. */
    fun isTerminal(status: String): Boolean = status == "Disposed"
}

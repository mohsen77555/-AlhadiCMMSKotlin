package com.alhadi.cmms.domain.governance

/**
 * Work order state machine. UI labels may stay Arabic/English, but business logic must use
 * a single transition table to avoid illegal jumps such as Open -> Closed.
 */
object WorkOrderLifecycle {
    const val OPEN = "Open"
    const val IN_PROGRESS = "In Progress"
    const val TECHNICALLY_COMPLETED = "Technically Completed"
    const val CLOSED = "Closed"
    const val CANCELLED = "Cancelled"
    const val ON_HOLD = "On Hold"

    private val allowedTransitions: Map<String, Set<String>> = mapOf(
        OPEN to setOf(IN_PROGRESS, ON_HOLD, CANCELLED),
        ON_HOLD to setOf(OPEN, IN_PROGRESS, CANCELLED),
        IN_PROGRESS to setOf(TECHNICALLY_COMPLETED, ON_HOLD, CANCELLED),
        TECHNICALLY_COMPLETED to setOf(CLOSED, IN_PROGRESS),
        CLOSED to emptySet(),
        CANCELLED to emptySet()
    )

    fun canTransition(from: String, to: String): Boolean {
        val source = canonical(from)
        val target = canonical(to)
        return source == target || allowedTransitions[source]?.contains(target) == true
    }

    fun requireTransition(from: String, to: String) {
        if (!canTransition(from, to)) {
            throw IllegalStateException("Illegal work order transition: $from -> $to")
        }
    }

    fun requiresConfirmedOperations(targetStatus: String): Boolean =
        canonical(targetStatus) == TECHNICALLY_COMPLETED || canonical(targetStatus) == CLOSED

    fun requiresExecutionEvidence(targetStatus: String): Boolean = canonical(targetStatus) == CLOSED

    fun canonical(status: String): String = when (status.trim().lowercase()) {
        "open", "draft", "requested" -> OPEN
        "in progress", "in_progress", "started" -> IN_PROGRESS
        "technically completed", "technically_completed", "completed by technician" -> TECHNICALLY_COMPLETED
        "closed" -> CLOSED
        "cancelled", "canceled" -> CANCELLED
        "on hold", "waiting material", "waiting approval" -> ON_HOLD
        else -> status.trim()
    }
}

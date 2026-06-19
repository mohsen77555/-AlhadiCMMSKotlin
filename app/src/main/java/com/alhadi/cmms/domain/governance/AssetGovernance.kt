package com.alhadi.cmms.domain.governance

object AssetGovernance {
    private val transitions = mapOf(
        "Draft" to setOf("Acquired"),
        "Acquired" to setOf("Installed", "InStorage"),
        "InStorage" to setOf("Installed"),
        "Installed" to setOf("InService", "Decommissioned"),
        "InService" to setOf("Standby", "Decommissioned"),
        "Standby" to setOf("InService", "Decommissioned"),
        "Decommissioned" to setOf("InStorage", "Disposed"),
        "Disposed" to emptySet()
    )

    fun validateImpact(value: Int): Int {
        require(value in 1..5) { "Criticality impact must be between 1 and 5." }
        return value
    }

    fun calculateCriticalityScore(
        safety: Int,
        production: Int,
        environment: Int,
        service: Int,
        financial: Int
    ): Int {
        val values = listOf(safety, production, environment, service, financial)
        values.forEach(::validateImpact)
        return values.sum()
    }

    fun criticalityRating(score: Int): String = when (score) {
        in 5..7 -> "Low"
        in 8..12 -> "Medium"
        in 13..18 -> "High"
        in 19..25 -> "Critical"
        else -> throw IllegalArgumentException("Criticality score must be between 5 and 25.")
    }

    fun canTransitionLifecycle(from: String, to: String): Boolean =
        from == to || transitions[from]?.contains(to) == true

    fun requireLifecycleTransition(from: String, to: String) {
        if (!canTransitionLifecycle(from, to)) {
            throw IllegalStateException("Illegal asset lifecycle transition: $from -> $to")
        }
    }

    fun requiresApproval(target: String): Boolean =
        target == "Decommissioned" || target == "Disposed"
}

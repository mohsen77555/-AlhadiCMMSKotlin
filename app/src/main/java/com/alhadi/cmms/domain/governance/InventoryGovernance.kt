package com.alhadi.cmms.domain.governance

/**
 * Central inventory rules used to protect stock movements before they reach Room.
 *
 * This file intentionally contains no Android dependency so the rules can be unit-tested quickly.
 */
object InventoryGovernance {
    private val issueTypes = setOf("issue", "issue_to_work_order", "issue to work order")
    private val receiptTypes = setOf("receive", "receipt", "return", "return_from_work_order", "return from work order")

    fun validateMovement(transactionType: String, quantity: Int, onHandQty: Int? = null): InventoryMovementDecision {
        require(quantity > 0) { "Inventory quantity must be greater than zero." }
        val normalized = transactionType.trim().lowercase()
        require(normalized.isNotBlank()) { "Inventory transaction type is required." }

        return when {
            normalized in issueTypes -> {
                if (onHandQty != null && quantity > onHandQty) {
                    throw IllegalStateException("Insufficient stock: requested $quantity but available $onHandQty.")
                }
                InventoryMovementDecision(stockDelta = -quantity, consumesStock = true)
            }
            normalized in receiptTypes -> InventoryMovementDecision(stockDelta = quantity, consumesStock = false)
            else -> throw IllegalArgumentException("Unsupported inventory transaction type: $transactionType")
        }
    }

    fun projectedOnHand(currentOnHand: Int, transactionType: String, quantity: Int): Int {
        return currentOnHand + validateMovement(transactionType, quantity, currentOnHand).stockDelta
    }
}

data class InventoryMovementDecision(
    val stockDelta: Int,
    val consumesStock: Boolean
)

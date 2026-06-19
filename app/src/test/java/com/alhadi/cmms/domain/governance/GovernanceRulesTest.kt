package com.alhadi.cmms.domain.governance

import com.alhadi.cmms.data.BackupBundle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

class GovernanceRulesTest {
    @Test
    fun inventoryIssueReducesStockAndBlocksOverIssue() {
        assertEquals(7, InventoryGovernance.projectedOnHand(10, "Issue", 3))
        assertThrowsState { InventoryGovernance.validateMovement("Issue", 11, onHandQty = 10) }
    }

    @Test
    fun inventoryRejectsZeroAndUnknownMovement() {
        assertThrowsArgument { InventoryGovernance.validateMovement("Issue", 0, onHandQty = 10) }
        assertThrowsArgument { InventoryGovernance.validateMovement("Unknown", 1, onHandQty = 10) }
    }

    @Test
    fun workOrderLifecycleAllowsOnlyGovernedTransitions() {
        assertTrue(WorkOrderLifecycle.canTransition("Open", "In Progress"))
        assertTrue(WorkOrderLifecycle.canTransition("In Progress", "Technically Completed"))
        assertTrue(WorkOrderLifecycle.canTransition("Technically Completed", "Closed"))
        assertFalse(WorkOrderLifecycle.canTransition("Open", "Closed"))
    }

    @Test
    fun backupValidationRejectsEmptyBundle() {
        val result = BackupGovernance.validateForRestore(BackupBundle(appDbVersion = BackupGovernance.CURRENT_DB_VERSION))
        assertFalse(result.isValid)
        assertTrue(result.errors.isNotEmpty())
    }

    private fun assertThrowsArgument(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException")
        } catch (_: IllegalArgumentException) {
            // expected
        }
    }

    private fun assertThrowsState(block: () -> Unit) {
        try {
            block()
            fail("Expected IllegalStateException")
        } catch (_: IllegalStateException) {
            // expected
        }
    }
}

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

    @Test
    fun assetCriticalityUsesFiveGovernedImpactDimensions() {
        val score = AssetGovernance.calculateCriticalityScore(
            safety = 5,
            production = 5,
            environment = 3,
            service = 4,
            financial = 4
        )
        assertEquals(21, score)
        assertEquals("Critical", AssetGovernance.criticalityRating(score))
        assertThrowsArgument {
            AssetGovernance.calculateCriticalityScore(0, 1, 1, 1, 1)
        }
    }

    @Test
    fun assetLifecycleBlocksInvalidJumpsAndRequiresApprovalForExit() {
        assertTrue(AssetGovernance.canTransitionLifecycle("Draft", "Acquired"))
        assertTrue(AssetGovernance.canTransitionLifecycle("InService", "Standby"))
        assertFalse(AssetGovernance.canTransitionLifecycle("Draft", "InService"))
        assertTrue(AssetGovernance.requiresApproval("Decommissioned"))
        assertTrue(AssetGovernance.requiresApproval("Disposed"))
        assertFalse(AssetGovernance.requiresApproval("InService"))
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

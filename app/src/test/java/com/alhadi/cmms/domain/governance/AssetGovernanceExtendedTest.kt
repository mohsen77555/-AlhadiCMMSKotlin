package com.alhadi.cmms.domain.governance

import com.alhadi.cmms.data.BackupBundle
import com.alhadi.cmms.data.entity.AssetClassificationEntity
import com.alhadi.cmms.data.entity.AssetPartnerEntity
import com.alhadi.cmms.data.entity.AssetWarrantyEntity
import com.alhadi.cmms.data.entity.MeasuringPointEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AssetGovernanceExtendedTest {
    @Test
    fun warrantyUsesDateRangeAndStatus() {
        val warranty = AssetWarrantyEntity(
            assetId = 1,
            provider = "Vendor",
            startDate = "2026-01-01",
            endDate = "2026-12-31",
            status = "Active"
        )
        assertTrue(warranty.isActiveOn("2026-06-19"))
        assertFalse(warranty.isActiveOn("2027-01-01"))
        assertFalse(warranty.copy(status = "Expired").isActiveOn("2026-06-19"))
    }

    @Test
    fun partnerAndClassificationKeepGovernanceMetadata() {
        val partner = AssetPartnerEntity(
            assetId = 7,
            partnerRole = "Maintenance Responsible",
            partnerName = "Maintenance Team",
            isPrimary = true
        )
        val classification = AssetClassificationEntity(
            assetId = 7,
            classCode = "ROTATING",
            className = "Rotating Equipment",
            isPrimary = true
        )
        assertTrue(partner.isPrimary)
        assertEquals("ROTATING", classification.classCode)
        assertTrue(classification.isPrimary)
    }

    @Test
    fun measuringPointSupportsWarningAndCriticalLimits() {
        val point = MeasuringPointEntity(
            assetId = 1,
            name = "Bearing temperature",
            unit = "C",
            isCounter = false,
            upperLimit = 90.0,
            upperWarningLimit = 80.0,
            pointCode = "TEMP-BRG-01"
        )
        assertEquals("TEMP-BRG-01", point.pointCode)
        assertEquals(80.0, point.upperWarningLimit!!, 0.0)
        assertEquals(90.0, point.upperLimit!!, 0.0)
    }

    @Test
    fun backupBundleIncludesNewGovernanceCollections() {
        val bundle = BackupBundle(
            appDbVersion = BackupGovernance.CURRENT_DB_VERSION,
            assetPartners = listOf(
                AssetPartnerEntity(assetId = 1, partnerRole = "Owner", partnerName = "Operations")
            )
        )
        assertEquals(1, bundle.assetPartners.size)
        assertEquals(1, bundle.totalRecords)
    }
}

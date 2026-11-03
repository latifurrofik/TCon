package com.tcon.companion.core

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test

class TConVersioningTest {
    @Test fun versionCodeIncreasesWithSemanticVersion() {
        assertEquals(1_000L, TConVersioning.versionCodeFor("0.1.0"))
        assertEquals(1_001L, TConVersioning.versionCodeFor("0.1.1"))
        assertEquals(2_000L, TConVersioning.versionCodeFor("0.2.0"))
        assertEquals(1_000_000L, TConVersioning.versionCodeFor("1.0.0"))
    }

    @Test fun comparesNumericComponentsNotStrings() {
        assertTrue(TConVersioning.compare("0.10.0", "0.9.9") > 0)
        assertTrue(TConVersioning.compare("1.0.0", "0.99.999") > 0)
        assertEquals(0, TConVersioning.compare("2.3.4", "2.3.4"))
    }

    @Test fun rejectsNonCanonicalAndOutOfRangeVersions() {
        assertFalse(TConVersioning.isValid("01.2.3"))
        assertFalse(TConVersioning.isValid("1.2"))
        assertFalse(TConVersioning.isValid("v1.2.3"))
        assertThrows(IllegalArgumentException::class.java) {
            TConVersioning.versionCodeFor("2100.1.0")
        }
    }
}

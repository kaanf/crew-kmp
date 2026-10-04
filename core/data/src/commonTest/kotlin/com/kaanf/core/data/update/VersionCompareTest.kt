package com.kaanf.core.data.update

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VersionCompareTest {
    @Test
    fun comparesNumericallyPerSegment() {
        assertTrue("1.10".isNewerVersionThan("1.9"))
        assertTrue("1.2.1".isNewerVersionThan("1.2"))
        assertTrue("2.0".isNewerVersionThan("1.99.99"))
        assertFalse("1.2".isNewerVersionThan("1.2.0"))
        assertFalse("1.2".isNewerVersionThan("1.3"))
    }
}

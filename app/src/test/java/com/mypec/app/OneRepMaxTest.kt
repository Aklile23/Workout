package com.mypec.app

import com.mypec.app.domain.usecase.OneRepMax
import org.junit.Assert.assertEquals
import org.junit.Test

class OneRepMaxTest {

    @Test
    fun singleRepEqualsWeight() {
        assertEquals(100.0, OneRepMax.epley(100.0, 1), 0.001)
    }

    @Test
    fun epleyMatchesFormula() {
        // 100 * (1 + 5/30) = 116.666...
        assertEquals(116.6667, OneRepMax.epley(100.0, 5), 0.01)
    }

    @Test
    fun zeroRepsIsZero() {
        assertEquals(0.0, OneRepMax.epley(100.0, 0), 0.001)
    }

    @Test
    fun roundsToNearestStep() {
        assertEquals(102.5, OneRepMax.roundToNearest(101.3, 2.5), 0.001)
    }
}

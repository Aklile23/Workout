package com.mypec.app

import com.mypec.app.domain.usecase.PlateCalculator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PlateCalculatorTest {

    @Test
    fun loadsExactWeightWithDefaultBar() {
        val result = PlateCalculator.calculate(targetKg = 100.0, barKg = 20.0)
        // 40kg per side: 25 + 15
        assertEquals(listOf(25.0, 15.0), result.perSide)
        assertTrue(result.isExact)
        assertEquals(100.0, result.achievableWeight, 0.001)
    }

    @Test
    fun targetAtOrBelowBarReturnsNoPlates() {
        val result = PlateCalculator.calculate(targetKg = 20.0, barKg = 20.0)
        assertTrue(result.perSide.isEmpty())
        assertTrue(result.isExact)
    }

    @Test
    fun roundsDownWhenNotExact() {
        val result = PlateCalculator.calculate(targetKg = 101.0, barKg = 20.0)
        assertTrue(result.achievableWeight <= 101.0)
        assertTrue(!result.isExact)
    }
}

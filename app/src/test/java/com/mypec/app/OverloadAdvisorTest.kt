package com.mypec.app

import com.mypec.app.data.local.entity.SetLogEntity
import com.mypec.app.domain.usecase.OverloadAdvisor
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OverloadAdvisorTest {

    private fun set(weight: Double, reps: Int) = SetLogEntity(
        id = "x", sessionId = "s", exerciseId = "e", position = 0,
        setNumber = 1, weightKg = weight, reps = reps, isCompleted = true,
    )

    @Test
    fun addsWeightWhenTopOfRangeHit() {
        val suggestion = OverloadAdvisor.suggest(listOf(set(100.0, 12)), repMin = 8, repMax = 12)
        assertTrue(suggestion != null)
        assertEquals(102.5, suggestion!!.suggestedWeightKg, 0.001)
        assertEquals(8, suggestion.suggestedReps)
    }

    @Test
    fun repsOutWhenBelowRange() {
        val suggestion = OverloadAdvisor.suggest(listOf(set(100.0, 9)), repMin = 8, repMax = 12)
        assertEquals(100.0, suggestion!!.suggestedWeightKg, 0.001)
        assertEquals(10, suggestion.suggestedReps)
    }

    @Test
    fun returnsNullWithoutHistory() {
        assertEquals(null, OverloadAdvisor.suggest(emptyList(), 8, 12))
    }

    @Test
    fun detectsStall() {
        assertTrue(OverloadAdvisor.isStalling(listOf(100.0, 100.0, 100.0)))
        assertTrue(!OverloadAdvisor.isStalling(listOf(105.0, 100.0, 98.0)))
    }
}

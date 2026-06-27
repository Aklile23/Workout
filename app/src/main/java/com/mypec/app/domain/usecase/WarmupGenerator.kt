package com.mypec.app.domain.usecase

data class WarmupSet(val weightKg: Double, val reps: Int, val percent: Int)

/** Builds a simple warm-up ramp toward a working weight. */
object WarmupGenerator {

    fun generate(workingWeightKg: Double, barKg: Double = 20.0): List<WarmupSet> {
        if (workingWeightKg <= barKg) return emptyList()
        val steps = listOf(40 to 8, 60 to 5, 80 to 3)
        return steps.map { (percent, reps) ->
            val raw = workingWeightKg * percent / 100.0
            val rounded = OneRepMax.roundToNearest(raw.coerceAtLeast(barKg), 2.5)
            WarmupSet(rounded, reps, percent)
        }.distinctBy { it.weightKg }
    }
}

package com.mypec.app.domain.usecase

import com.mypec.app.data.local.entity.SetLogEntity

data class OverloadSuggestion(
    val suggestedWeightKg: Double,
    val suggestedReps: Int,
    val rationale: String,
)

/**
 * Suggests the next session's target for an exercise based on the previous best working set
 * and the prescribed rep range. Hitting the top of the range -> add weight; otherwise repeat
 * and aim for more reps.
 */
object OverloadAdvisor {

    fun suggest(
        previousSets: List<SetLogEntity>,
        repMin: Int,
        repMax: Int,
        incrementKg: Double = 2.5,
    ): OverloadSuggestion? {
        val working = previousSets.filter { !it.isWarmup }
        if (working.isEmpty()) return null
        // best set = highest estimated 1RM
        val best = working.maxByOrNull { OneRepMax.epley(it.weightKg, it.reps) } ?: return null

        return if (best.reps >= repMax) {
            OverloadSuggestion(
                suggestedWeightKg = OneRepMax.roundToNearest(best.weightKg + incrementKg, incrementKg),
                suggestedReps = repMin,
                rationale = "You hit the top of the rep range last time. Add ${incrementKg}kg.",
            )
        } else {
            OverloadSuggestion(
                suggestedWeightKg = best.weightKg,
                suggestedReps = (best.reps + 1).coerceAtMost(repMax),
                rationale = "Repeat ${best.weightKg.toCleanString()}kg and beat ${best.reps} reps.",
            )
        }
    }

    /** Detects a stall: same-or-lower top e1RM across the last [window] sessions. */
    fun isStalling(e1rmBySessionDesc: List<Double>, window: Int = 3): Boolean {
        if (e1rmBySessionDesc.size < window) return false
        val recent = e1rmBySessionDesc.take(window)
        return recent.zipWithNext().all { (newer, older) -> newer <= older + 0.01 }
    }
}

fun Double.toCleanString(): String =
    if (this % 1.0 == 0.0) this.toInt().toString() else this.toString()

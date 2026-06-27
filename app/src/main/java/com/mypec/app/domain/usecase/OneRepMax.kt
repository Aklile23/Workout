package com.mypec.app.domain.usecase

import kotlin.math.roundToInt

/** Estimated 1RM helpers. */
object OneRepMax {

    /** Epley formula: weight * (1 + reps / 30). Reps of 1 returns the weight itself. */
    fun epley(weightKg: Double, reps: Int): Double {
        if (reps <= 0) return 0.0
        if (reps == 1) return weightKg
        return weightKg * (1.0 + reps / 30.0)
    }

    /** Estimate the weight you could lift for [targetReps] given a known 1RM. */
    fun weightForReps(oneRm: Double, targetReps: Int): Double {
        if (targetReps <= 1) return oneRm
        return oneRm / (1.0 + targetReps / 30.0)
    }

    fun roundToNearest(value: Double, step: Double = 2.5): Double {
        if (step <= 0) return value
        return (value / step).roundToInt() * step
    }
}

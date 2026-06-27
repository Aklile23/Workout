package com.mypec.app.domain.usecase

import kotlin.math.abs

data class PlateResult(
    val perSide: List<Double>,
    val achievableWeight: Double,
    val isExact: Boolean,
    val leftover: Double,
)

/** Computes which plates to load on each side of a barbell (all weights in kg). */
object PlateCalculator {

    val DEFAULT_PLATES = listOf(25.0, 20.0, 15.0, 10.0, 5.0, 2.5, 1.25)

    fun calculate(
        targetKg: Double,
        barKg: Double = 20.0,
        availablePlates: List<Double> = DEFAULT_PLATES,
    ): PlateResult {
        if (targetKg <= barKg) {
            return PlateResult(emptyList(), barKg, abs(targetKg - barKg) < 0.001, targetKg - barKg)
        }
        var perSide = (targetKg - barKg) / 2.0
        val plates = availablePlates.sortedDescending()
        val result = mutableListOf<Double>()
        for (plate in plates) {
            while (perSide >= plate - 0.0001) {
                result.add(plate)
                perSide -= plate
            }
        }
        val loadedPerSide = result.sum()
        val achievable = barKg + loadedPerSide * 2
        return PlateResult(
            perSide = result,
            achievableWeight = achievable,
            isExact = abs(achievable - targetKg) < 0.001,
            leftover = targetKg - achievable,
        )
    }
}

package com.mypec.app.core

import kotlin.math.roundToInt

object Format {
    fun kg(value: Double): String {
        val rounded = (value * 100).roundToInt() / 100.0
        return if (rounded % 1.0 == 0.0) "${rounded.toInt()} kg" else "$rounded kg"
    }

    fun kgPlain(value: Double): String {
        val rounded = (value * 100).roundToInt() / 100.0
        return if (rounded % 1.0 == 0.0) rounded.toInt().toString() else rounded.toString()
    }

    fun reps(value: Int): String = "$value reps"

    fun oneDecimal(value: Double): String = "%.1f".format(value)
}

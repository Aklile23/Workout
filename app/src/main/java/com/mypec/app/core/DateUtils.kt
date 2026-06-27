package com.mypec.app.core

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

object DateUtils {
    fun today(): LocalDate = LocalDate.now()

    fun todayEpochDay(): Long = LocalDate.now().toEpochDay()

    fun LocalDate.epochDay(): Long = this.toEpochDay()

    fun epochDayToLocalDate(epochDay: Long): LocalDate = LocalDate.ofEpochDay(epochDay)

    fun nowMillis(): Long = Instant.now().toEpochMilli()

    private val medium = DateTimeFormatter.ofPattern("EEE, d MMM", Locale.getDefault())
    private val full = DateTimeFormatter.ofPattern("EEEE, d MMMM yyyy", Locale.getDefault())

    fun formatMedium(epochDay: Long): String = epochDayToLocalDate(epochDay).format(medium)
    fun formatFull(epochDay: Long): String = epochDayToLocalDate(epochDay).format(full)

    fun weekdayName(weekday: Int): String =
        java.time.DayOfWeek.of(weekday).getDisplayName(TextStyle.FULL, Locale.getDefault())

    fun formatDuration(millis: Long): String {
        val totalSeconds = millis / 1000
        val h = totalSeconds / 3600
        val m = (totalSeconds % 3600) / 60
        val s = totalSeconds % 60
        return if (h > 0) "%d:%02d:%02d".format(h, m, s) else "%d:%02d".format(m, s)
    }
}

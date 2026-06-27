package com.mypec.app.domain.usecase

import com.mypec.app.data.local.entity.WorkoutSessionEntity
import com.mypec.app.domain.model.SessionStatus

data class WeeklyAdherence(
    val planned: Int,
    val completed: Int,
    val skipped: Int,
) {
    val ratio: Float get() = if (planned == 0) 0f else completed.toFloat() / planned.toFloat()
    val percent: Int get() = (ratio * 100).toInt()
}

object Adherence {
    /**
     * @param plannedTrainingDays number of scheduled (non-rest) days in the week
     * @param sessions sessions that fall within the week
     */
    fun forWeek(plannedTrainingDays: Int, sessions: List<WorkoutSessionEntity>): WeeklyAdherence {
        val completed = sessions.count { it.status == SessionStatus.COMPLETED.name }
        val skipped = sessions.count { it.status == SessionStatus.SKIPPED.name }
        return WeeklyAdherence(
            planned = plannedTrainingDays.coerceAtLeast(completed),
            completed = completed,
            skipped = skipped,
        )
    }
}

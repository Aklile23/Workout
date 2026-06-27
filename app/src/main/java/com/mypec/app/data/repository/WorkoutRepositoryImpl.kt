package com.mypec.app.data.repository

import com.mypec.app.core.DateUtils
import com.mypec.app.data.local.dao.ProgramDao
import com.mypec.app.data.local.dao.WorkoutDao
import com.mypec.app.data.local.entity.SetLogEntity
import com.mypec.app.data.local.entity.WorkoutSessionEntity
import com.mypec.app.data.local.relation.SetLogWithExercise
import com.mypec.app.domain.model.SessionStatus
import com.mypec.app.domain.repository.MetricsRepository
import com.mypec.app.domain.repository.WorkoutRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WorkoutRepositoryImpl @Inject constructor(
    private val workoutDao: WorkoutDao,
    private val programDao: ProgramDao,
    private val metricsRepository: MetricsRepository,
) : WorkoutRepository {

    override fun observeActiveSession(): Flow<WorkoutSessionEntity?> =
        workoutDao.observeActiveSession()

    override fun observeAllSessions(): Flow<List<WorkoutSessionEntity>> =
        workoutDao.observeAllSessions()

    override fun observeSessionsBetween(from: Long, to: Long): Flow<List<WorkoutSessionEntity>> =
        workoutDao.observeSessionsBetween(from, to)

    override fun observeSession(id: String): Flow<WorkoutSessionEntity?> =
        workoutDao.observeSession(id)

    override fun observeSets(sessionId: String): Flow<List<SetLogWithExercise>> =
        workoutDao.observeSetsForSession(sessionId)

    override suspend fun getSession(id: String): WorkoutSessionEntity? = workoutDao.getSession(id)

    override suspend fun getSessionForDay(day: Long): WorkoutSessionEntity? =
        workoutDao.getSessionForDay(day)

    override suspend fun startSessionFromVariant(variantId: String, dateEpochDay: Long): String {
        val variant = programDao.getVariantWithExercises(variantId)
        val sessionId = UUID.randomUUID().toString()
        val program = programDao.getActiveProgram()
        workoutDao.upsertSession(
            WorkoutSessionEntity(
                id = sessionId,
                programId = program?.id,
                dayVariantId = variantId,
                title = variant?.variant?.label ?: "Workout",
                dateEpochDay = dateEpochDay,
                status = SessionStatus.IN_PROGRESS.name,
                startedAt = DateUtils.nowMillis(),
            )
        )
        variant?.exercises?.sortedBy { it.routineExercise.orderIndex }?.forEachIndexed { position, re ->
            val previous = workoutDao.getPreviousSets(re.exercise.id, sessionId)
            repeat(re.routineExercise.targetSets) { setIdx ->
                val prev = previous.getOrNull(setIdx)
                workoutDao.upsertSet(
                    SetLogEntity(
                        id = UUID.randomUUID().toString(),
                        sessionId = sessionId,
                        exerciseId = re.exercise.id,
                        position = position,
                        setNumber = setIdx + 1,
                        weightKg = prev?.weightKg ?: 0.0,
                        reps = 0,
                        supersetGroup = re.routineExercise.supersetGroup,
                        isCompleted = false,
                    )
                )
            }
        }
        return sessionId
    }

    override suspend fun startFreestyleSession(dateEpochDay: Long): String {
        val sessionId = UUID.randomUUID().toString()
        workoutDao.upsertSession(
            WorkoutSessionEntity(
                id = sessionId,
                programId = programDao.getActiveProgram()?.id,
                dayVariantId = null,
                title = "Freestyle Workout",
                dateEpochDay = dateEpochDay,
                status = SessionStatus.IN_PROGRESS.name,
                startedAt = DateUtils.nowMillis(),
            )
        )
        return sessionId
    }

    override suspend fun upsertSet(set: SetLogEntity) =
        workoutDao.upsertSet(set.copy(updatedAt = DateUtils.nowMillis(), dirty = true))

    override suspend fun deleteSet(set: SetLogEntity) = workoutDao.deleteSet(set)

    override suspend fun addExerciseToSession(sessionId: String, exerciseId: String) {
        val position = workoutDao.maxPosition(sessionId) + 1
        val previous = workoutDao.getPreviousSets(exerciseId, sessionId)
        val sets = previous.size.coerceIn(1, 4).let { if (it == 0) 1 else it }
        repeat(sets) { setIdx ->
            workoutDao.upsertSet(
                SetLogEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    exerciseId = exerciseId,
                    position = position,
                    setNumber = setIdx + 1,
                    weightKg = previous.getOrNull(setIdx)?.weightKg ?: 0.0,
                    reps = 0,
                )
            )
        }
    }

    override suspend fun completeSession(id: String) {
        val session = workoutDao.getSession(id) ?: return
        workoutDao.upsertSession(
            session.copy(
                status = SessionStatus.COMPLETED.name,
                endedAt = DateUtils.nowMillis(),
                updatedAt = DateUtils.nowMillis(),
                dirty = true,
            )
        )
        metricsRepository.recomputeRecordsForSession(id)
    }

    override suspend fun skipDay(title: String, variantId: String?, dateEpochDay: Long) {
        val existing = workoutDao.getSessionForDay(dateEpochDay)
        // Don't overwrite a finished workout, and never create duplicates for the same day.
        if (existing != null) {
            if (existing.status == SessionStatus.COMPLETED.name) return
            workoutDao.upsertSession(
                existing.copy(
                    status = SessionStatus.SKIPPED.name,
                    title = title,
                    dayVariantId = existing.dayVariantId ?: variantId,
                    updatedAt = DateUtils.nowMillis(),
                    dirty = true,
                )
            )
        } else {
            workoutDao.upsertSession(
                WorkoutSessionEntity(
                    id = UUID.randomUUID().toString(),
                    programId = programDao.getActiveProgram()?.id,
                    dayVariantId = variantId,
                    title = title,
                    dateEpochDay = dateEpochDay,
                    status = SessionStatus.SKIPPED.name,
                )
            )
        }
    }

    override suspend fun unskipDay(dateEpochDay: Long) {
        workoutDao.softDeleteSkippedForDay(dateEpochDay, DateUtils.nowMillis())
    }

    override suspend fun getPreviousSets(
        exerciseId: String,
        excludeSessionId: String,
    ): List<SetLogEntity> = workoutDao.getPreviousSets(exerciseId, excludeSessionId)

    override fun observeAllCompletedSets() = workoutDao.observeAllCompletedSets()

    override suspend fun getHistoryForExercise(exerciseId: String): List<SetLogEntity> =
        workoutDao.getHistoryForExercise(exerciseId).map { it.setLog }

    override suspend fun copyPreviousIntoSession(sessionId: String) {
        val sets = workoutDao.getSetsForSession(sessionId)
        sets.groupBy { it.exerciseId }.forEach { (exerciseId, current) ->
            val previous = workoutDao.getPreviousSets(exerciseId, sessionId)
            current.sortedBy { it.setNumber }.forEachIndexed { idx, set ->
                val prev = previous.getOrNull(idx) ?: return@forEachIndexed
                workoutDao.upsertSet(set.copy(weightKg = prev.weightKg, reps = prev.reps))
            }
        }
    }
}

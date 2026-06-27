package com.mypec.app.domain.repository

import com.mypec.app.data.local.entity.AchievementEntity
import com.mypec.app.data.local.entity.BodyMeasurementEntity
import com.mypec.app.data.local.entity.BodyWeightEntity
import com.mypec.app.data.local.entity.ExerciseEntity
import com.mypec.app.data.local.entity.PersonalRecordEntity
import com.mypec.app.data.local.entity.ProgramEntity
import com.mypec.app.data.local.entity.ProgressPhotoEntity
import com.mypec.app.data.local.entity.SetLogEntity
import com.mypec.app.data.local.entity.WorkoutSessionEntity
import com.mypec.app.data.local.relation.DayWithVariants
import com.mypec.app.data.local.relation.SetLogWithExercise
import com.mypec.app.data.local.relation.VariantWithExercises
import kotlinx.coroutines.flow.Flow

interface ProgramRepository {
    fun observeActiveProgram(): Flow<ProgramEntity?>
    fun observeDays(programId: String): Flow<List<DayWithVariants>>
    suspend fun getActiveProgram(): ProgramEntity?
    suspend fun getDayForWeekday(programId: String, weekday: Int): DayWithVariants?
    suspend fun getVariant(variantId: String): VariantWithExercises?
    suspend fun getPlannedTrainingDays(programId: String): Int
    suspend fun ensureSeeded()

    fun observeExercises(): Flow<List<ExerciseEntity>>
    fun searchExercises(query: String): Flow<List<ExerciseEntity>>
    suspend fun getExercise(id: String): ExerciseEntity?
    suspend fun upsertExercise(exercise: ExerciseEntity)
}

interface WorkoutRepository {
    fun observeActiveSession(): Flow<WorkoutSessionEntity?>
    fun observeAllSessions(): Flow<List<WorkoutSessionEntity>>
    fun observeSessionsBetween(from: Long, to: Long): Flow<List<WorkoutSessionEntity>>
    fun observeSession(id: String): Flow<WorkoutSessionEntity?>
    fun observeSets(sessionId: String): Flow<List<SetLogWithExercise>>
    suspend fun getSession(id: String): WorkoutSessionEntity?
    suspend fun getSessionForDay(day: Long): WorkoutSessionEntity?
    suspend fun startSessionFromVariant(variantId: String, dateEpochDay: Long): String
    suspend fun startFreestyleSession(dateEpochDay: Long): String
    suspend fun upsertSet(set: SetLogEntity)
    suspend fun deleteSet(set: SetLogEntity)
    suspend fun addExerciseToSession(sessionId: String, exerciseId: String)
    suspend fun completeSession(id: String)
    suspend fun skipDay(title: String, variantId: String?, dateEpochDay: Long)
    suspend fun getPreviousSets(exerciseId: String, excludeSessionId: String): List<SetLogEntity>
    suspend fun copyPreviousIntoSession(sessionId: String)
    fun observeAllCompletedSets(): Flow<List<SetLogEntity>>
    suspend fun getHistoryForExercise(exerciseId: String): List<SetLogEntity>
}

interface MetricsRepository {
    fun observeBodyWeights(): Flow<List<BodyWeightEntity>>
    fun observeLatestBodyWeight(): Flow<BodyWeightEntity?>
    suspend fun addBodyWeight(dateEpochDay: Long, weightKg: Double)

    fun observeMeasurements(): Flow<List<BodyMeasurementEntity>>
    suspend fun addMeasurement(dateEpochDay: Long, type: String, valueCm: Double)

    fun observePhotos(): Flow<List<ProgressPhotoEntity>>
    suspend fun addPhoto(dateEpochDay: Long, uri: String, note: String?)

    fun observeRecords(): Flow<List<PersonalRecordEntity>>
    fun observeAchievements(): Flow<List<AchievementEntity>>
    suspend fun recomputeRecordsForSession(sessionId: String): List<PersonalRecordEntity>
}

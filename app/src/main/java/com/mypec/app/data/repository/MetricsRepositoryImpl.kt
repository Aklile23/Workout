package com.mypec.app.data.repository

import com.mypec.app.core.DateUtils
import com.mypec.app.data.local.dao.MetricsDao
import com.mypec.app.data.local.dao.WorkoutDao
import com.mypec.app.data.local.entity.AchievementEntity
import com.mypec.app.data.local.entity.BodyMeasurementEntity
import com.mypec.app.data.local.entity.BodyWeightEntity
import com.mypec.app.data.local.entity.PersonalRecordEntity
import com.mypec.app.data.local.entity.ProgressPhotoEntity
import com.mypec.app.domain.model.RecordType
import com.mypec.app.domain.repository.MetricsRepository
import com.mypec.app.domain.usecase.OneRepMax
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MetricsRepositoryImpl @Inject constructor(
    private val metricsDao: MetricsDao,
    private val workoutDao: WorkoutDao,
) : MetricsRepository {

    override fun observeBodyWeights(): Flow<List<BodyWeightEntity>> = metricsDao.observeBodyWeights()

    override fun observeLatestBodyWeight(): Flow<BodyWeightEntity?> =
        metricsDao.observeLatestBodyWeight()

    override suspend fun addBodyWeight(dateEpochDay: Long, weightKg: Double) {
        metricsDao.upsertBodyWeight(
            BodyWeightEntity(
                id = "bw_$dateEpochDay",
                dateEpochDay = dateEpochDay,
                weightKg = weightKg,
            )
        )
    }

    override fun observeMeasurements(): Flow<List<BodyMeasurementEntity>> =
        metricsDao.observeMeasurements()

    override suspend fun addMeasurement(dateEpochDay: Long, type: String, valueCm: Double) {
        metricsDao.upsertMeasurement(
            BodyMeasurementEntity(
                id = "bm_${type}_$dateEpochDay",
                dateEpochDay = dateEpochDay,
                type = type,
                valueCm = valueCm,
            )
        )
    }

    override fun observePhotos(): Flow<List<ProgressPhotoEntity>> = metricsDao.observePhotos()

    override suspend fun addPhoto(dateEpochDay: Long, uri: String, note: String?) {
        metricsDao.upsertPhoto(
            ProgressPhotoEntity(
                id = UUID.randomUUID().toString(),
                dateEpochDay = dateEpochDay,
                uri = uri,
                note = note,
            )
        )
    }

    override fun observeRecords(): Flow<List<PersonalRecordEntity>> = metricsDao.observeRecords()

    override fun observeAchievements(): Flow<List<AchievementEntity>> =
        metricsDao.observeAchievements()

    override suspend fun recomputeRecordsForSession(sessionId: String): List<PersonalRecordEntity> {
        val session = workoutDao.getSession(sessionId) ?: return emptyList()
        val sets = workoutDao.getSetsForSession(sessionId).filter { it.isCompleted && !it.isWarmup }
        val newPrs = mutableListOf<PersonalRecordEntity>()

        sets.groupBy { it.exerciseId }.forEach { (exerciseId, exSets) ->
            val maxWeight = exSets.maxOf { it.weightKg }
            val maxReps = exSets.maxOf { it.reps }
            val bestE1rm = exSets.maxOf { OneRepMax.epley(it.weightKg, it.reps) }
            val maxVolume = exSets.maxOf { it.weightKg * it.reps }

            checkAndStore(exerciseId, RecordType.MAX_WEIGHT, maxWeight, session.dateEpochDay, sessionId)?.let { newPrs += it }
            checkAndStore(exerciseId, RecordType.MAX_REPS, maxReps.toDouble(), session.dateEpochDay, sessionId)?.let { newPrs += it }
            checkAndStore(exerciseId, RecordType.BEST_E1RM, bestE1rm, session.dateEpochDay, sessionId)?.let { newPrs += it }
            checkAndStore(exerciseId, RecordType.MAX_SET_VOLUME, maxVolume, session.dateEpochDay, sessionId)?.let { newPrs += it }
        }
        return newPrs
    }

    private suspend fun checkAndStore(
        exerciseId: String,
        type: RecordType,
        value: Double,
        dateEpochDay: Long,
        sessionId: String,
    ): PersonalRecordEntity? {
        if (value <= 0.0) return null
        val existing = metricsDao.getRecord(exerciseId, type.name)
        if (existing != null && existing.value >= value) return null
        val record = PersonalRecordEntity(
            id = existing?.id ?: "pr_${exerciseId}_${type.name}",
            exerciseId = exerciseId,
            type = type.name,
            value = value,
            achievedAtEpochDay = dateEpochDay,
            sessionId = sessionId,
        )
        metricsDao.upsertRecord(record)
        return if (existing != null) record else null // only celebrate improvements over a prior PR
    }

    suspend fun unlockAchievement(key: String, title: String, description: String) {
        metricsDao.upsertAchievement(
            AchievementEntity(
                key = key,
                title = title,
                description = description,
                unlockedAtMillis = DateUtils.nowMillis(),
            )
        )
    }
}

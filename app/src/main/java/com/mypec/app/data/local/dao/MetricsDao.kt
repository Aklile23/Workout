package com.mypec.app.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.mypec.app.data.local.entity.AchievementEntity
import com.mypec.app.data.local.entity.BodyMeasurementEntity
import com.mypec.app.data.local.entity.BodyWeightEntity
import com.mypec.app.data.local.entity.PersonalRecordEntity
import com.mypec.app.data.local.entity.ProgressPhotoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MetricsDao {

    @Upsert
    suspend fun upsertBodyWeight(entity: BodyWeightEntity)

    @Query("SELECT * FROM body_weights WHERE isDeleted = 0 ORDER BY dateEpochDay ASC")
    fun observeBodyWeights(): Flow<List<BodyWeightEntity>>

    @Query("SELECT * FROM body_weights WHERE isDeleted = 0 ORDER BY dateEpochDay DESC LIMIT 1")
    fun observeLatestBodyWeight(): Flow<BodyWeightEntity?>

    @Upsert
    suspend fun upsertMeasurement(entity: BodyMeasurementEntity)

    @Query("SELECT * FROM body_measurements WHERE isDeleted = 0 ORDER BY dateEpochDay DESC")
    fun observeMeasurements(): Flow<List<BodyMeasurementEntity>>

    @Upsert
    suspend fun upsertPhoto(entity: ProgressPhotoEntity)

    @Query("SELECT * FROM progress_photos WHERE isDeleted = 0 ORDER BY dateEpochDay DESC")
    fun observePhotos(): Flow<List<ProgressPhotoEntity>>

    @Upsert
    suspend fun upsertRecord(entity: PersonalRecordEntity)

    @Query("SELECT * FROM personal_records WHERE isDeleted = 0 ORDER BY achievedAtEpochDay DESC")
    fun observeRecords(): Flow<List<PersonalRecordEntity>>

    @Query("SELECT * FROM personal_records WHERE exerciseId = :exerciseId AND type = :type AND isDeleted = 0 LIMIT 1")
    suspend fun getRecord(exerciseId: String, type: String): PersonalRecordEntity?

    @Upsert
    suspend fun upsertAchievement(entity: AchievementEntity)

    @Query("SELECT * FROM achievements ORDER BY unlockedAtMillis DESC")
    fun observeAchievements(): Flow<List<AchievementEntity>>
}

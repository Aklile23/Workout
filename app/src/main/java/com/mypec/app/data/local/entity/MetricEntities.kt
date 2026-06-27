package com.mypec.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "body_weights")
data class BodyWeightEntity(
    @PrimaryKey val id: String,
    val dateEpochDay: Long,
    val weightKg: Double,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

@Entity(tableName = "body_measurements", indices = [Index("type")])
data class BodyMeasurementEntity(
    @PrimaryKey val id: String,
    val dateEpochDay: Long,
    val type: String, // CHEST, ARM, WAIST, THIGH, CALF, SHOULDERS, HIPS
    val valueCm: Double,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

@Entity(tableName = "progress_photos")
data class ProgressPhotoEntity(
    @PrimaryKey val id: String,
    val dateEpochDay: Long,
    val uri: String,
    val pose: String? = null,
    val note: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

@Entity(tableName = "personal_records", indices = [Index("exerciseId")])
data class PersonalRecordEntity(
    @PrimaryKey val id: String,
    val exerciseId: String,
    val type: String, // MAX_WEIGHT, MAX_REPS, BEST_E1RM, MAX_SET_VOLUME
    val value: Double,
    val achievedAtEpochDay: Long,
    val sessionId: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

@Entity(tableName = "achievements")
data class AchievementEntity(
    @PrimaryKey val key: String,
    val title: String,
    val description: String,
    val unlockedAtMillis: Long,
)

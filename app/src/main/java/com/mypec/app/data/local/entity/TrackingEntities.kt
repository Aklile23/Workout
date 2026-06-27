package com.mypec.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "workout_sessions",
    indices = [Index("dateEpochDay")],
)
data class WorkoutSessionEntity(
    @PrimaryKey val id: String,
    val programId: String? = null,
    val dayVariantId: String? = null,
    val title: String,
    val dateEpochDay: Long,
    val status: String, // PLANNED, IN_PROGRESS, COMPLETED, SKIPPED
    val startedAt: Long? = null,
    val endedAt: Long? = null,
    val notes: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

@Entity(
    tableName = "set_logs",
    indices = [Index("sessionId"), Index("exerciseId")],
)
data class SetLogEntity(
    @PrimaryKey val id: String,
    val sessionId: String,
    val exerciseId: String,
    val position: Int = 0, // exercise order within the session (same for every set of an exercise)
    val setNumber: Int,
    val weightKg: Double,
    val reps: Int,
    val rpe: Double? = null,
    val isWarmup: Boolean = false,
    val isCompleted: Boolean = false,
    val supersetGroup: Int? = null,
    val note: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

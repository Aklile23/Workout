package com.mypec.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "programs")
data class ProgramEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String? = null,
    val isActive: Boolean = true,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

@Entity(
    tableName = "routine_days",
    indices = [Index("programId")],
)
data class RoutineDayEntity(
    @PrimaryKey val id: String,
    val programId: String,
    val weekday: Int, // ISO-8601: Monday = 1 ... Sunday = 7
    val title: String,
    val subtitle: String? = null,
    val isOptional: Boolean = false,
    val orderIndex: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

@Entity(
    tableName = "day_variants",
    indices = [Index("routineDayId")],
)
data class DayVariantEntity(
    @PrimaryKey val id: String,
    val routineDayId: String,
    val label: String,
    val orderIndex: Int = 0,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

@Entity(
    tableName = "routine_exercises",
    indices = [Index("dayVariantId"), Index("exerciseId")],
)
data class RoutineExerciseEntity(
    @PrimaryKey val id: String,
    val dayVariantId: String,
    val exerciseId: String,
    val orderIndex: Int,
    val targetSets: Int,
    val repMin: Int,
    val repMax: Int,
    val supersetGroup: Int? = null,
    val notes: String? = null,
    val updatedAt: Long = System.currentTimeMillis(),
    val isDeleted: Boolean = false,
    val dirty: Boolean = true,
)

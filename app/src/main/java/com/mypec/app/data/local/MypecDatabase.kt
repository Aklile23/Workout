package com.mypec.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mypec.app.data.local.dao.ExerciseDao
import com.mypec.app.data.local.dao.MetricsDao
import com.mypec.app.data.local.dao.ProgramDao
import com.mypec.app.data.local.dao.WorkoutDao
import com.mypec.app.data.local.entity.AchievementEntity
import com.mypec.app.data.local.entity.BodyMeasurementEntity
import com.mypec.app.data.local.entity.BodyWeightEntity
import com.mypec.app.data.local.entity.DayVariantEntity
import com.mypec.app.data.local.entity.ExerciseEntity
import com.mypec.app.data.local.entity.PersonalRecordEntity
import com.mypec.app.data.local.entity.ProgramEntity
import com.mypec.app.data.local.entity.ProgressPhotoEntity
import com.mypec.app.data.local.entity.RoutineDayEntity
import com.mypec.app.data.local.entity.RoutineExerciseEntity
import com.mypec.app.data.local.entity.SetLogEntity
import com.mypec.app.data.local.entity.WorkoutSessionEntity

@Database(
    entities = [
        ExerciseEntity::class,
        ProgramEntity::class,
        RoutineDayEntity::class,
        DayVariantEntity::class,
        RoutineExerciseEntity::class,
        WorkoutSessionEntity::class,
        SetLogEntity::class,
        BodyWeightEntity::class,
        BodyMeasurementEntity::class,
        ProgressPhotoEntity::class,
        PersonalRecordEntity::class,
        AchievementEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class MypecDatabase : RoomDatabase() {
    abstract fun exerciseDao(): ExerciseDao
    abstract fun programDao(): ProgramDao
    abstract fun workoutDao(): WorkoutDao
    abstract fun metricsDao(): MetricsDao

    companion object {
        const val NAME = "mypec.db"
    }
}

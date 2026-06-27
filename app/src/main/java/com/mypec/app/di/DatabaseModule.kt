package com.mypec.app.di

import android.content.Context
import androidx.room.Room
import com.mypec.app.data.local.MypecDatabase
import com.mypec.app.data.local.dao.ExerciseDao
import com.mypec.app.data.local.dao.MetricsDao
import com.mypec.app.data.local.dao.ProgramDao
import com.mypec.app.data.local.dao.WorkoutDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MypecDatabase =
        Room.databaseBuilder(context, MypecDatabase::class.java, MypecDatabase.NAME)
            .fallbackToDestructiveMigration()
            .build()

    @Provides
    fun provideExerciseDao(db: MypecDatabase): ExerciseDao = db.exerciseDao()

    @Provides
    fun provideProgramDao(db: MypecDatabase): ProgramDao = db.programDao()

    @Provides
    fun provideWorkoutDao(db: MypecDatabase): WorkoutDao = db.workoutDao()

    @Provides
    fun provideMetricsDao(db: MypecDatabase): MetricsDao = db.metricsDao()
}

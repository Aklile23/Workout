package com.mypec.app.di

import com.mypec.app.data.repository.MetricsRepositoryImpl
import com.mypec.app.data.repository.ProgramRepositoryImpl
import com.mypec.app.data.repository.WorkoutRepositoryImpl
import com.mypec.app.domain.repository.MetricsRepository
import com.mypec.app.domain.repository.ProgramRepository
import com.mypec.app.domain.repository.WorkoutRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindProgramRepository(impl: ProgramRepositoryImpl): ProgramRepository

    @Binds
    @Singleton
    abstract fun bindWorkoutRepository(impl: WorkoutRepositoryImpl): WorkoutRepository

    @Binds
    @Singleton
    abstract fun bindMetricsRepository(impl: MetricsRepositoryImpl): MetricsRepository
}

package com.mypec.app.data.repository

import com.mypec.app.data.local.dao.ExerciseDao
import com.mypec.app.data.local.dao.ProgramDao
import com.mypec.app.data.local.entity.ExerciseEntity
import com.mypec.app.data.local.entity.ProgramEntity
import com.mypec.app.data.local.relation.DayWithVariants
import com.mypec.app.data.local.relation.VariantWithExercises
import com.mypec.app.data.local.seed.ProgramSeeder
import com.mypec.app.domain.repository.ProgramRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProgramRepositoryImpl @Inject constructor(
    private val programDao: ProgramDao,
    private val exerciseDao: ExerciseDao,
) : ProgramRepository {

    private val seeder = ProgramSeeder(exerciseDao, programDao)

    override fun observeActiveProgram(): Flow<ProgramEntity?> = programDao.observeActiveProgram()

    override fun observeDays(programId: String): Flow<List<DayWithVariants>> =
        programDao.observeDays(programId)

    override suspend fun getActiveProgram(): ProgramEntity? = programDao.getActiveProgram()

    override suspend fun getDayForWeekday(programId: String, weekday: Int): DayWithVariants? =
        programDao.getDayForWeekday(programId, weekday)

    override suspend fun getVariant(variantId: String): VariantWithExercises? =
        programDao.getVariantWithExercises(variantId)

    override suspend fun getPlannedTrainingDays(programId: String): Int =
        programDao.getDaysOnce(programId).count { !it.isOptional }

    override suspend fun ensureSeeded() = seeder.seedIfEmpty()

    override fun observeExercises(): Flow<List<ExerciseEntity>> = exerciseDao.observeAll()

    override fun searchExercises(query: String): Flow<List<ExerciseEntity>> =
        exerciseDao.search(query)

    override suspend fun getExercise(id: String): ExerciseEntity? = exerciseDao.getById(id)

    override suspend fun upsertExercise(exercise: ExerciseEntity) = exerciseDao.upsert(exercise)
}

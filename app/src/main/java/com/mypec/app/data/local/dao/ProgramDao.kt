package com.mypec.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.mypec.app.data.local.entity.DayVariantEntity
import com.mypec.app.data.local.entity.ProgramEntity
import com.mypec.app.data.local.entity.RoutineDayEntity
import com.mypec.app.data.local.entity.RoutineExerciseEntity
import com.mypec.app.data.local.relation.DayWithVariants
import com.mypec.app.data.local.relation.VariantWithExercises
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {

    @Query("SELECT * FROM programs WHERE isActive = 1 AND isDeleted = 0 LIMIT 1")
    fun observeActiveProgram(): Flow<ProgramEntity?>

    @Query("SELECT * FROM programs WHERE isActive = 1 AND isDeleted = 0 LIMIT 1")
    suspend fun getActiveProgram(): ProgramEntity?

    @Transaction
    @Query("SELECT * FROM routine_days WHERE programId = :programId AND isDeleted = 0 ORDER BY orderIndex ASC")
    fun observeDays(programId: String): Flow<List<DayWithVariants>>

    @Transaction
    @Query("SELECT * FROM routine_days WHERE programId = :programId AND weekday = :weekday AND isDeleted = 0 ORDER BY orderIndex ASC LIMIT 1")
    suspend fun getDayForWeekday(programId: String, weekday: Int): DayWithVariants?

    @Transaction
    @Query("SELECT * FROM day_variants WHERE id = :variantId")
    suspend fun getVariantWithExercises(variantId: String): VariantWithExercises?

    @Query("SELECT * FROM routine_days WHERE programId = :programId AND isDeleted = 0 ORDER BY orderIndex ASC")
    suspend fun getDaysOnce(programId: String): List<RoutineDayEntity>

    @Upsert
    suspend fun upsertProgram(program: ProgramEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDays(days: List<RoutineDayEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVariants(variants: List<DayVariantEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRoutineExercises(items: List<RoutineExerciseEntity>)

    @Upsert
    suspend fun upsertRoutineExercise(item: RoutineExerciseEntity)

    @Query("SELECT COUNT(*) FROM programs")
    suspend fun programCount(): Int
}

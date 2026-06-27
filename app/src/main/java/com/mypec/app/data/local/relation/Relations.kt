package com.mypec.app.data.local.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.mypec.app.data.local.entity.DayVariantEntity
import com.mypec.app.data.local.entity.ExerciseEntity
import com.mypec.app.data.local.entity.RoutineDayEntity
import com.mypec.app.data.local.entity.RoutineExerciseEntity
import com.mypec.app.data.local.entity.SetLogEntity

data class RoutineExerciseWithExercise(
    @Embedded val routineExercise: RoutineExerciseEntity,
    @Relation(parentColumn = "exerciseId", entityColumn = "id")
    val exercise: ExerciseEntity,
)

data class VariantWithExercises(
    @Embedded val variant: DayVariantEntity,
    @Relation(
        entity = RoutineExerciseEntity::class,
        parentColumn = "id",
        entityColumn = "dayVariantId",
    )
    val exercises: List<RoutineExerciseWithExercise>,
)

data class DayWithVariants(
    @Embedded val day: RoutineDayEntity,
    @Relation(
        entity = DayVariantEntity::class,
        parentColumn = "id",
        entityColumn = "routineDayId",
    )
    val variants: List<VariantWithExercises>,
)

data class SetLogWithExercise(
    @Embedded val setLog: SetLogEntity,
    @Relation(parentColumn = "exerciseId", entityColumn = "id")
    val exercise: ExerciseEntity,
)

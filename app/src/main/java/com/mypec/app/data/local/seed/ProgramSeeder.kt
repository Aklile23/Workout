package com.mypec.app.data.local.seed

import com.mypec.app.data.local.dao.ExerciseDao
import com.mypec.app.data.local.dao.ProgramDao
import com.mypec.app.data.local.entity.DayVariantEntity
import com.mypec.app.data.local.entity.ExerciseEntity
import com.mypec.app.data.local.entity.ProgramEntity
import com.mypec.app.data.local.entity.RoutineDayEntity
import com.mypec.app.data.local.entity.RoutineExerciseEntity
import com.mypec.app.domain.model.Muscle

/** Seeds the myPeC 4-day upper/lower split (plus optional Saturday) on first launch. */
class ProgramSeeder(
    private val exerciseDao: ExerciseDao,
    private val programDao: ProgramDao,
) {
    suspend fun seedIfEmpty() {
        if (programDao.programCount() > 0) return

        val exercises = SEED_EXERCISES.map {
            ExerciseEntity(
                id = exerciseId(it.name),
                name = it.name,
                primaryMuscle = it.muscle.name,
                equipment = it.equipment,
                isCustom = false,
                dirty = false,
            )
        }
        exerciseDao.insertAll(exercises)

        val programId = "program_mypec_default"
        programDao.upsertProgram(
            ProgramEntity(
                id = programId,
                name = "myPeC Split",
                description = "4-day upper/lower split. Saturday optional.",
                isActive = true,
                dirty = false,
            )
        )

        val days = mutableListOf<RoutineDayEntity>()
        val variants = mutableListOf<DayVariantEntity>()
        val routineExercises = mutableListOf<RoutineExerciseEntity>()

        SEED_DAYS.forEachIndexed { dayIndex, day ->
            val dayId = "day_${day.weekday}"
            days += RoutineDayEntity(
                id = dayId,
                programId = programId,
                weekday = day.weekday,
                title = day.title,
                subtitle = day.subtitle,
                isOptional = day.isOptional,
                orderIndex = dayIndex,
                dirty = false,
            )
            day.variants.forEachIndexed { vIndex, variant ->
                val variantId = "${dayId}_v$vIndex"
                variants += DayVariantEntity(
                    id = variantId,
                    routineDayId = dayId,
                    label = variant.label,
                    orderIndex = vIndex,
                    dirty = false,
                )
                variant.rows.forEachIndexed { rIndex, row ->
                    routineExercises += RoutineExerciseEntity(
                        id = "${variantId}_$rIndex",
                        dayVariantId = variantId,
                        exerciseId = exerciseId(row.exName),
                        orderIndex = rIndex,
                        targetSets = row.sets,
                        repMin = row.repMin,
                        repMax = row.repMax,
                        notes = row.note,
                        dirty = false,
                    )
                }
            }
        }

        programDao.insertDays(days)
        programDao.insertVariants(variants)
        programDao.insertRoutineExercises(routineExercises)
    }

    companion object {
        fun exerciseId(name: String): String =
            "ex_" + name.lowercase().replace(Regex("[^a-z0-9]+"), "_").trim('_')
    }
}

private data class SeedEx(val name: String, val muscle: Muscle, val equipment: String)
private data class SeedRow(
    val exName: String,
    val sets: Int,
    val repMin: Int,
    val repMax: Int,
    val note: String? = null,
)
private data class SeedVariant(val label: String, val rows: List<SeedRow>)
private data class SeedDay(
    val weekday: Int,
    val title: String,
    val subtitle: String?,
    val isOptional: Boolean,
    val variants: List<SeedVariant>,
)

private val SEED_EXERCISES = listOf(
    SeedEx("Barbell Bench Press", Muscle.CHEST, "Barbell"),
    SeedEx("Weighted Pull-up", Muscle.BACK, "Bodyweight"),
    SeedEx("Incline Dumbbell Press", Muscle.CHEST, "Dumbbell"),
    SeedEx("Chest-Supported Row", Muscle.BACK, "Machine"),
    SeedEx("Pec Deck / Cable Fly", Muscle.CHEST, "Machine"),
    SeedEx("Lateral Raise", Muscle.SHOULDERS, "Dumbbell"),
    SeedEx("Triceps Pushdown", Muscle.TRICEPS, "Cable"),
    SeedEx("Dumbbell Curl", Muscle.BICEPS, "Dumbbell"),
    SeedEx("Squat", Muscle.QUADS, "Barbell"),
    SeedEx("Leg Press", Muscle.QUADS, "Machine"),
    SeedEx("Romanian Deadlift", Muscle.HAMSTRINGS, "Barbell"),
    SeedEx("Leg Extension", Muscle.QUADS, "Machine"),
    SeedEx("Lying Leg Curl", Muscle.HAMSTRINGS, "Machine"),
    SeedEx("Standing Calf Raise", Muscle.CALVES, "Machine"),
    SeedEx("Cable Crunch", Muscle.CORE, "Cable"),
    SeedEx("Incline Barbell Press", Muscle.CHEST, "Barbell"),
    SeedEx("Seated Cable Row", Muscle.BACK, "Cable"),
    SeedEx("Lat Pulldown", Muscle.BACK, "Cable"),
    SeedEx("Machine Shoulder Press", Muscle.SHOULDERS, "Machine"),
    SeedEx("Cable Lateral Raise", Muscle.SHOULDERS, "Cable"),
    SeedEx("Rear Delt Fly", Muscle.SHOULDERS, "Machine"),
    SeedEx("Overhead Triceps Extension", Muscle.TRICEPS, "Cable"),
    SeedEx("Preacher Curl", Muscle.BICEPS, "Machine"),
    SeedEx("Hip Thrust", Muscle.GLUTES, "Barbell"),
    SeedEx("Bulgarian Split Squat", Muscle.QUADS, "Dumbbell"),
    SeedEx("Calf Raise", Muscle.CALVES, "Machine"),
    SeedEx("Plank", Muscle.CORE, "Bodyweight"),
    SeedEx("Cable Curl", Muscle.BICEPS, "Cable"),
    SeedEx("Hammer Curl", Muscle.BICEPS, "Dumbbell"),
    SeedEx("Incline Machine Press", Muscle.CHEST, "Machine"),
    SeedEx("Cable Fly", Muscle.CHEST, "Cable"),
    SeedEx("Dips", Muscle.CHEST, "Bodyweight"),
    SeedEx("Hanging Leg Raise", Muscle.CORE, "Bodyweight"),
)

private val SEED_DAYS = listOf(
    SeedDay(
        weekday = 1,
        title = "Upper A",
        subtitle = "Chest + Back Heavy",
        isOptional = false,
        variants = listOf(
            SeedVariant(
                "Upper A",
                listOf(
                    SeedRow("Barbell Bench Press", 4, 5, 8),
                    SeedRow("Weighted Pull-up", 4, 6, 10),
                    SeedRow("Incline Dumbbell Press", 3, 8, 12),
                    SeedRow("Chest-Supported Row", 4, 8, 12),
                    SeedRow("Pec Deck / Cable Fly", 3, 12, 15),
                    SeedRow("Lateral Raise", 4, 12, 20),
                    SeedRow("Triceps Pushdown", 3, 10, 15),
                    SeedRow("Dumbbell Curl", 3, 10, 15),
                ),
            ),
        ),
    ),
    SeedDay(
        weekday = 2,
        title = "Lower A",
        subtitle = "Quad Focus",
        isOptional = false,
        variants = listOf(
            SeedVariant(
                "Lower A",
                listOf(
                    SeedRow("Squat", 4, 6, 10),
                    SeedRow("Leg Press", 3, 10, 15),
                    SeedRow("Romanian Deadlift", 3, 8, 12),
                    SeedRow("Leg Extension", 4, 12, 20),
                    SeedRow("Lying Leg Curl", 3, 10, 15),
                    SeedRow("Standing Calf Raise", 4, 8, 15),
                    SeedRow("Cable Crunch", 3, 10, 15),
                ),
            ),
        ),
    ),
    SeedDay(
        weekday = 4,
        title = "Upper B",
        subtitle = "Shoulders + Back + Arms",
        isOptional = false,
        variants = listOf(
            SeedVariant(
                "Upper B",
                listOf(
                    SeedRow("Incline Barbell Press", 3, 8, 12),
                    SeedRow("Seated Cable Row", 4, 8, 12),
                    SeedRow("Lat Pulldown", 3, 10, 12, "Different grip from Monday"),
                    SeedRow("Machine Shoulder Press", 3, 8, 12),
                    SeedRow("Cable Lateral Raise", 4, 12, 20),
                    SeedRow("Rear Delt Fly", 4, 12, 20),
                    SeedRow("Overhead Triceps Extension", 3, 10, 15),
                    SeedRow("Preacher Curl", 3, 10, 15),
                ),
            ),
        ),
    ),
    SeedDay(
        weekday = 5,
        title = "Lower B",
        subtitle = "Posterior Chain + Full Legs",
        isOptional = false,
        variants = listOf(
            SeedVariant(
                "Lower B",
                listOf(
                    SeedRow("Romanian Deadlift", 4, 6, 10),
                    SeedRow("Squat", 3, 8, 12, "Hack/front squat or leg press"),
                    SeedRow("Hip Thrust", 3, 8, 12),
                    SeedRow("Lying Leg Curl", 4, 10, 15),
                    SeedRow("Bulgarian Split Squat", 3, 8, 12, "Each leg"),
                    SeedRow("Calf Raise", 4, 10, 15),
                    SeedRow("Plank", 3, 0, 0, "Hold / cable crunch"),
                ),
            ),
        ),
    ),
    SeedDay(
        weekday = 6,
        title = "Optional Pump Day",
        subtitle = "Pick one option. 45-70 min.",
        isOptional = true,
        variants = listOf(
            SeedVariant(
                "Option A - Arms + Delts",
                listOf(
                    SeedRow("Cable Lateral Raise", 4, 15, 25),
                    SeedRow("Rear Delt Fly", 3, 15, 25),
                    SeedRow("Triceps Pushdown", 3, 12, 20),
                    SeedRow("Overhead Triceps Extension", 3, 12, 20),
                    SeedRow("Cable Curl", 3, 12, 20),
                    SeedRow("Hammer Curl", 3, 12, 20),
                ),
            ),
            SeedVariant(
                "Option B - Chest + Arms",
                listOf(
                    SeedRow("Incline Machine Press", 3, 10, 15),
                    SeedRow("Cable Fly", 3, 12, 20),
                    SeedRow("Dips", 3, 8, 12),
                    SeedRow("Triceps Pushdown", 3, 12, 20),
                    SeedRow("Preacher Curl", 3, 10, 15),
                    SeedRow("Hammer Curl", 3, 12, 15),
                ),
            ),
            SeedVariant(
                "Option C - Weak Point Day",
                listOf(
                    SeedRow("Cable Lateral Raise", 4, 15, 25, "Target a lagging body part: delts, arms, upper chest, calves, or back width"),
                ),
            ),
        ),
    ),
)

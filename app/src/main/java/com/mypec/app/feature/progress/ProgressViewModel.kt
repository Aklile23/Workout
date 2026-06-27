package com.mypec.app.feature.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.domain.model.Muscle
import com.mypec.app.domain.repository.MetricsRepository
import com.mypec.app.domain.repository.ProgramRepository
import com.mypec.app.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class ProgressUiState(
    val bodyWeightPoints: List<Float> = emptyList(),
    val latestBodyWeight: Double? = null,
    val weeklyVolume: List<Pair<String, Float>> = emptyList(),
    val muscleVolume: List<Pair<String, Float>> = emptyList(),
    val totalVolume: Double = 0.0,
    val totalSets: Int = 0,
)

@HiltViewModel
class ProgressViewModel @Inject constructor(
    workoutRepository: WorkoutRepository,
    metricsRepository: MetricsRepository,
    programRepository: ProgramRepository,
) : ViewModel() {

    val uiState = combine(
        metricsRepository.observeBodyWeights(),
        workoutRepository.observeAllCompletedSets(),
        workoutRepository.observeAllSessions(),
        programRepository.observeExercises(),
    ) { weights, sets, sessions, exercises ->
        val dateBySession = sessions.associate { it.id to it.dateEpochDay }
        val muscleByExercise = exercises.associate { it.id to it.primaryMuscle }

        val weekBuckets = sets.groupBy { (dateBySession[it.sessionId] ?: 0L) / 7 }
            .toSortedMap()
            .entries.toList().takeLast(8)
            .mapIndexed { idx, entry ->
                "W${idx + 1}" to entry.value.sumOf { it.weightKg * it.reps }.toFloat()
            }

        val muscleBuckets = sets.groupBy { muscleByExercise[it.exerciseId] ?: Muscle.FULL_BODY.name }
            .map { (muscle, list) -> Muscle.fromName(muscle).display to list.sumOf { it.weightKg * it.reps }.toFloat() }
            .sortedByDescending { it.second }
            .take(6)

        ProgressUiState(
            bodyWeightPoints = weights.map { it.weightKg.toFloat() },
            latestBodyWeight = weights.lastOrNull()?.weightKg,
            weeklyVolume = weekBuckets,
            muscleVolume = muscleBuckets,
            totalVolume = sets.sumOf { it.weightKg * it.reps },
            totalSets = sets.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgressUiState())
}

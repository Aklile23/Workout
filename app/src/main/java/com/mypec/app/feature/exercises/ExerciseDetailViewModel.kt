package com.mypec.app.feature.exercises

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.data.local.entity.ExerciseEntity
import com.mypec.app.domain.repository.ProgramRepository
import com.mypec.app.domain.repository.WorkoutRepository
import com.mypec.app.domain.usecase.OneRepMax
import com.mypec.app.ui.navigation.Dest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ExerciseDetailUiState(
    val exercise: ExerciseEntity? = null,
    val e1rmPoints: List<Float> = emptyList(),
    val bestE1rm: Double = 0.0,
    val bestWeight: Double = 0.0,
    val totalSets: Int = 0,
    val loading: Boolean = true,
)

@HiltViewModel
class ExerciseDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val programRepository: ProgramRepository,
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    private val exerciseId: String = checkNotNull(savedStateHandle[Dest.ExerciseDetail.ARG])

    private val _state = MutableStateFlow(ExerciseDetailUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val exercise = programRepository.getExercise(exerciseId)
            val history = workoutRepository.getHistoryForExercise(exerciseId)
            val chronological = history.reversed()
            val points = chronological.map { OneRepMax.epley(it.weightKg, it.reps).toFloat() }
            _state.update {
                it.copy(
                    exercise = exercise,
                    e1rmPoints = points,
                    bestE1rm = history.maxOfOrNull { s -> OneRepMax.epley(s.weightKg, s.reps) } ?: 0.0,
                    bestWeight = history.maxOfOrNull { s -> s.weightKg } ?: 0.0,
                    totalSets = history.size,
                    loading = false,
                )
            }
        }
    }
}

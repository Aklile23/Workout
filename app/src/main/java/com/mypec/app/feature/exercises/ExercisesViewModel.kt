package com.mypec.app.feature.exercises

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.data.local.entity.ExerciseEntity
import com.mypec.app.domain.model.Muscle
import com.mypec.app.domain.repository.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ExercisesViewModel @Inject constructor(
    private val programRepository: ProgramRepository,
) : ViewModel() {

    val query = MutableStateFlow("")

    val exercises = query.flatMapLatest { q ->
        if (q.isBlank()) programRepository.observeExercises() else programRepository.searchExercises(q)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setQuery(value: String) { query.value = value }

    fun addExercise(name: String, muscle: Muscle) {
        if (name.isBlank()) return
        viewModelScope.launch {
            programRepository.upsertExercise(
                ExerciseEntity(
                    id = "custom_" + UUID.randomUUID().toString(),
                    name = name.trim(),
                    primaryMuscle = muscle.name,
                    equipment = "Other",
                    isCustom = true,
                )
            )
        }
    }
}

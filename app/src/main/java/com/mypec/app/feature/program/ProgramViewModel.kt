package com.mypec.app.feature.program

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.core.DateUtils
import com.mypec.app.data.local.entity.ProgramEntity
import com.mypec.app.data.local.relation.DayWithVariants
import com.mypec.app.domain.repository.ProgramRepository
import com.mypec.app.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProgramUiState(
    val program: ProgramEntity? = null,
    val days: List<DayWithVariants> = emptyList(),
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProgramViewModel @Inject constructor(
    private val programRepository: ProgramRepository,
    private val workoutRepository: WorkoutRepository,
) : ViewModel() {

    private val program = programRepository.observeActiveProgram()

    val uiState = program.flatMapLatest { p ->
        if (p == null) flowOf(ProgramUiState()) else
            combine(flowOf(p), programRepository.observeDays(p.id)) { prog, days ->
                ProgramUiState(prog, days)
            }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ProgramUiState())

    fun startVariant(variantId: String, onStarted: (String) -> Unit) {
        viewModelScope.launch {
            val id = workoutRepository.startSessionFromVariant(variantId, DateUtils.todayEpochDay())
            onStarted(id)
        }
    }
}

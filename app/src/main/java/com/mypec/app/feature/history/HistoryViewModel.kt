package com.mypec.app.feature.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.data.local.entity.WorkoutSessionEntity
import com.mypec.app.domain.repository.WorkoutRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class HistoryUiState(
    val sessions: List<WorkoutSessionEntity> = emptyList(),
    val statusByDay: Map<Long, String> = emptyMap(),
    val completedCount: Int = 0,
    val skippedCount: Int = 0,
)

@HiltViewModel
class HistoryViewModel @Inject constructor(
    workoutRepository: WorkoutRepository,
) : ViewModel() {

    val uiState = workoutRepository.observeAllSessions().map { sessions ->
        HistoryUiState(
            sessions = sessions,
            statusByDay = sessions.associate { it.dateEpochDay to it.status },
            completedCount = sessions.count { it.status == "COMPLETED" },
            skippedCount = sessions.count { it.status == "SKIPPED" },
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HistoryUiState())
}

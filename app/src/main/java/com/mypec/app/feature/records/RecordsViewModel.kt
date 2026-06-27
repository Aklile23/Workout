package com.mypec.app.feature.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.data.local.entity.AchievementEntity
import com.mypec.app.domain.model.RecordType
import com.mypec.app.domain.repository.MetricsRepository
import com.mypec.app.domain.repository.ProgramRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

data class RecordRow(
    val exerciseName: String,
    val typeLabel: String,
    val valueLabel: String,
)

data class RecordsUiState(
    val records: List<RecordRow> = emptyList(),
    val achievements: List<AchievementEntity> = emptyList(),
)

@HiltViewModel
class RecordsViewModel @Inject constructor(
    metricsRepository: MetricsRepository,
    programRepository: ProgramRepository,
) : ViewModel() {

    val uiState = combine(
        metricsRepository.observeRecords(),
        metricsRepository.observeAchievements(),
        programRepository.observeExercises(),
    ) { records, achievements, exercises ->
        val nameById = exercises.associate { it.id to it.name }
        RecordsUiState(
            records = records.map { pr ->
                val type = RecordType.fromName(pr.type)
                RecordRow(
                    exerciseName = nameById[pr.exerciseId] ?: "Exercise",
                    typeLabel = type.display,
                    valueLabel = when (type) {
                        RecordType.MAX_REPS -> "${pr.value.toInt()} reps"
                        else -> "${if (pr.value % 1.0 == 0.0) pr.value.toInt().toString() else "%.1f".format(pr.value)} kg"
                    },
                )
            },
            achievements = achievements,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), RecordsUiState())
}

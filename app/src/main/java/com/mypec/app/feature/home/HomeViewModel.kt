package com.mypec.app.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.core.DateUtils
import com.mypec.app.data.local.entity.BodyWeightEntity
import com.mypec.app.data.local.entity.WorkoutSessionEntity
import com.mypec.app.data.local.relation.DayWithVariants
import com.mypec.app.domain.repository.MetricsRepository
import com.mypec.app.domain.repository.ProgramRepository
import com.mypec.app.domain.repository.WorkoutRepository
import com.mypec.app.domain.usecase.Adherence
import com.mypec.app.domain.usecase.WeeklyAdherence
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.DayOfWeek
import java.time.LocalDate
import javax.inject.Inject

data class HomeUiState(
    val todayName: String = "",
    val todayDateLabel: String = "",
    val todayDay: DayWithVariants? = null,
    val isRestDay: Boolean = false,
    val activeSession: WorkoutSessionEntity? = null,
    val adherence: WeeklyAdherence = WeeklyAdherence(0, 0, 0),
    val latestWeight: BodyWeightEntity? = null,
    val loading: Boolean = true,
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val programRepository: ProgramRepository,
    private val workoutRepository: WorkoutRepository,
    metricsRepository: MetricsRepository,
) : ViewModel() {

    private val today: LocalDate = LocalDate.now()
    private val todayWeekday = today.dayOfWeek.value
    private val weekStart = today.with(DayOfWeek.MONDAY).toEpochDay()
    private val weekEnd = weekStart + 6

    private val daysFlow = programRepository.observeActiveProgram().flatMapLatest { program ->
        if (program == null) flowOf(emptyList()) else programRepository.observeDays(program.id)
    }

    val uiState = combine(
        daysFlow,
        workoutRepository.observeActiveSession(),
        workoutRepository.observeSessionsBetween(weekStart, weekEnd),
        metricsRepository.observeLatestBodyWeight(),
    ) { days, active, weekSessions, weight ->
        val todayDay = days.firstOrNull { it.day.weekday == todayWeekday }
        val planned = days.count { !it.day.isOptional }
        HomeUiState(
            todayName = todayDay?.day?.title ?: "Rest Day",
            todayDateLabel = DateUtils.formatFull(today.toEpochDay()),
            todayDay = todayDay,
            isRestDay = todayDay == null,
            activeSession = active,
            adherence = Adherence.forWeek(planned, weekSessions),
            latestWeight = weight,
            loading = false,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), HomeUiState())

    fun startVariant(variantId: String, onStarted: (String) -> Unit) {
        viewModelScope.launch {
            val id = workoutRepository.startSessionFromVariant(variantId, today.toEpochDay())
            onStarted(id)
        }
    }

    fun startFreestyle(onStarted: (String) -> Unit) {
        viewModelScope.launch {
            val id = workoutRepository.startFreestyleSession(today.toEpochDay())
            onStarted(id)
        }
    }

    fun skipToday() {
        val state = uiState.value
        val day = state.todayDay ?: return
        viewModelScope.launch {
            workoutRepository.skipDay(
                title = day.day.title,
                variantId = day.variants.firstOrNull()?.variant?.id,
                dateEpochDay = today.toEpochDay(),
            )
        }
    }
}

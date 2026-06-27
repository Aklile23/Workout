package com.mypec.app.feature.workout

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.core.DateUtils
import com.mypec.app.data.local.entity.SetLogEntity
import com.mypec.app.data.local.entity.WorkoutSessionEntity
import com.mypec.app.domain.repository.ProgramRepository
import com.mypec.app.domain.repository.WorkoutRepository
import com.mypec.app.domain.usecase.OneRepMax
import com.mypec.app.domain.usecase.OverloadAdvisor
import com.mypec.app.domain.usecase.OverloadSuggestion
import com.mypec.app.ui.navigation.Dest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

data class ExerciseBlock(
    val exerciseId: String,
    val exerciseName: String,
    val muscle: String,
    val sets: List<SetLogEntity>,
    val previous: List<SetLogEntity>,
    val suggestion: OverloadSuggestion?,
)

data class WorkoutUiState(
    val session: WorkoutSessionEntity? = null,
    val blocks: List<ExerciseBlock> = emptyList(),
    val elapsedLabel: String = "0:00",
    val totalVolume: Double = 0.0,
    val completedSets: Int = 0,
    val totalSets: Int = 0,
)

data class RestTimerState(
    val running: Boolean = false,
    val remaining: Int = 0,
    val total: Int = 0,
    val justFinished: Boolean = false,
)

@HiltViewModel
class WorkoutViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val workoutRepository: WorkoutRepository,
    private val programRepository: ProgramRepository,
) : ViewModel() {

    private val sessionId: String = checkNotNull(savedStateHandle[Dest.Workout.ARG])

    private val previousByExercise = MutableStateFlow<Map<String, List<SetLogEntity>>>(emptyMap())
    private val rangeByExercise = MutableStateFlow<Map<String, Pair<Int, Int>>>(emptyMap())

    private val ticker = flow {
        while (true) {
            emit(DateUtils.nowMillis())
            delay(1000)
        }
    }

    val uiState = combine(
        workoutRepository.observeSession(sessionId),
        workoutRepository.observeSets(sessionId),
        previousByExercise,
        rangeByExercise,
        ticker,
    ) { session, sets, previousMap, rangeMap, now ->
        val grouped = sets.groupBy { it.setLog.exerciseId }
        val orderedExerciseIds = sets.sortedBy { it.setLog.position }.map { it.setLog.exerciseId }.distinct()
        val blocks = orderedExerciseIds.map { exId ->
            val group = grouped[exId].orEmpty()
            val previous = previousMap[exId].orEmpty()
            val range = rangeMap[exId]
            val suggestion = range?.let {
                OverloadAdvisor.suggest(previous, it.first, it.second)
            }
            ExerciseBlock(
                exerciseId = exId,
                exerciseName = group.firstOrNull()?.exercise?.name ?: "Exercise",
                muscle = group.firstOrNull()?.exercise?.primaryMuscle ?: "",
                sets = group.map { it.setLog }.sortedBy { it.setNumber },
                previous = previous,
                suggestion = suggestion,
            )
        }
        val completedSets = sets.count { it.setLog.isCompleted }
        val volume = sets.filter { it.setLog.isCompleted }.sumOf { it.setLog.weightKg * it.setLog.reps }
        val elapsed = session?.startedAt?.let { DateUtils.formatDuration(now - it) } ?: "0:00"
        WorkoutUiState(
            session = session,
            blocks = blocks,
            elapsedLabel = elapsed,
            totalVolume = volume,
            completedSets = completedSets,
            totalSets = sets.size,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), WorkoutUiState())

    val exercises = programRepository.observeExercises()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _rest = MutableStateFlow(RestTimerState())
    val rest = _rest.asStateFlow()
    private var restJob: Job? = null

    init {
        viewModelScope.launch {
            val session = workoutRepository.getSession(sessionId)
            val variant = session?.dayVariantId?.let { programRepository.getVariant(it) }
            rangeByExercise.value = variant?.exercises?.associate {
                it.exercise.id to (it.routineExercise.repMin to it.routineExercise.repMax)
            }.orEmpty()
        }
        viewModelScope.launch {
            workoutRepository.observeSets(sessionId).collect { sets ->
                loadPreviousForInternal(sets.map { it.setLog.exerciseId }.distinct())
            }
        }
    }

    private suspend fun loadPreviousForInternal(exerciseIds: List<String>) {
        val map = previousByExercise.value.toMutableMap()
        var changed = false
        exerciseIds.forEach { id ->
            if (!map.containsKey(id)) {
                map[id] = workoutRepository.getPreviousSets(id, sessionId)
                changed = true
            }
        }
        if (changed) previousByExercise.value = map
    }

    fun loadPreviousFor(exerciseIds: List<String>) {
        viewModelScope.launch { loadPreviousForInternal(exerciseIds) }
    }

    fun updateSet(set: SetLogEntity, weightKg: Double = set.weightKg, reps: Int = set.reps, rpe: Double? = set.rpe) {
        viewModelScope.launch {
            workoutRepository.upsertSet(set.copy(weightKg = weightKg, reps = reps, rpe = rpe))
        }
    }

    fun toggleComplete(set: SetLogEntity, defaultRest: Int) {
        viewModelScope.launch {
            val nowComplete = !set.isCompleted
            workoutRepository.upsertSet(set.copy(isCompleted = nowComplete))
            if (nowComplete) startRest(defaultRest)
        }
    }

    fun addSet(block: ExerciseBlock) {
        viewModelScope.launch {
            val last = block.sets.lastOrNull()
            workoutRepository.upsertSet(
                SetLogEntity(
                    id = UUID.randomUUID().toString(),
                    sessionId = sessionId,
                    exerciseId = block.exerciseId,
                    position = block.sets.firstOrNull()?.position ?: 0,
                    setNumber = (block.sets.maxOfOrNull { it.setNumber } ?: 0) + 1,
                    weightKg = last?.weightKg ?: 0.0,
                    reps = 0,
                ),
            )
        }
    }

    fun deleteSet(set: SetLogEntity) {
        viewModelScope.launch { workoutRepository.deleteSet(set) }
    }

    fun addExercise(exerciseId: String) {
        viewModelScope.launch {
            workoutRepository.addExerciseToSession(sessionId, exerciseId)
            loadPreviousFor(listOf(exerciseId))
        }
    }

    fun copyPrevious() {
        viewModelScope.launch { workoutRepository.copyPreviousIntoSession(sessionId) }
    }

    fun finish(onDone: () -> Unit) {
        viewModelScope.launch {
            workoutRepository.completeSession(sessionId)
            onDone()
        }
    }

    fun startRest(seconds: Int) {
        restJob?.cancel()
        _rest.value = RestTimerState(running = true, remaining = seconds, total = seconds)
        restJob = viewModelScope.launch {
            var remaining = seconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                _rest.update { it.copy(remaining = remaining) }
            }
            _rest.update { it.copy(running = false, justFinished = true) }
        }
    }

    fun adjustRest(delta: Int) {
        _rest.update { it.copy(remaining = (it.remaining + delta).coerceAtLeast(0)) }
    }

    fun stopRest() {
        restJob?.cancel()
        _rest.value = RestTimerState()
    }

    fun consumeRestFinished() {
        _rest.update { it.copy(justFinished = false) }
    }

    fun estimatedOneRm(weightKg: Double, reps: Int): Double = OneRepMax.epley(weightKg, reps)
}

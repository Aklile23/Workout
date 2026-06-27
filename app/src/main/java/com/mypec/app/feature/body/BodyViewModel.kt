package com.mypec.app.feature.body

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mypec.app.core.DateUtils
import com.mypec.app.data.local.entity.BodyMeasurementEntity
import com.mypec.app.data.local.entity.BodyWeightEntity
import com.mypec.app.domain.repository.MetricsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BodyUiState(
    val weights: List<BodyWeightEntity> = emptyList(),
    val weightPoints: List<Float> = emptyList(),
    val measurements: List<BodyMeasurementEntity> = emptyList(),
)

@HiltViewModel
class BodyViewModel @Inject constructor(
    private val metricsRepository: MetricsRepository,
) : ViewModel() {

    val uiState = combine(
        metricsRepository.observeBodyWeights(),
        metricsRepository.observeMeasurements(),
    ) { weights, measurements ->
        BodyUiState(
            weights = weights.reversed(),
            weightPoints = weights.map { it.weightKg.toFloat() },
            measurements = measurements,
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BodyUiState())

    fun addWeight(kg: Double) {
        viewModelScope.launch { metricsRepository.addBodyWeight(DateUtils.todayEpochDay(), kg) }
    }

    fun addMeasurement(type: String, cm: Double) {
        viewModelScope.launch { metricsRepository.addMeasurement(DateUtils.todayEpochDay(), type, cm) }
    }
}

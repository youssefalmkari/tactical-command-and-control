package com.example.tacticalcommandandcontrol.feature.liveops

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import com.example.tacticalcommandandcontrol.core.domain.usecase.drone.ObserveDronesUseCase
import com.example.tacticalcommandandcontrol.core.domain.usecase.telemetry.ObserveAllTelemetryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class LiveOpsViewModel @Inject constructor(
    observeDronesUseCase: ObserveDronesUseCase,
    observeAllTelemetryUseCase: ObserveAllTelemetryUseCase,
) : ViewModel() {

    val uiState: StateFlow<LiveOpsUiState> = combine(
        observeDronesUseCase(),
        observeAllTelemetryUseCase(),
    ) { drones, telemetryMap ->
        LiveOpsUiState.Success(
            drones = drones,
            telemetry = telemetryMap,
        ) as LiveOpsUiState
    }
        .catch { emit(LiveOpsUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = LiveOpsUiState.Loading,
        )
}

sealed interface LiveOpsUiState {
    data object Loading : LiveOpsUiState
    data class Success(
        val drones: List<Drone>,
        val telemetry: Map<String, TelemetrySnapshot>,
    ) : LiveOpsUiState
    data class Error(val message: String) : LiveOpsUiState
}

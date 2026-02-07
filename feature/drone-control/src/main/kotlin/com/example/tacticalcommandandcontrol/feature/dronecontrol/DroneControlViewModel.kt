package com.example.tacticalcommandandcontrol.feature.dronecontrol

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tacticalcommandandcontrol.core.domain.model.CommandResult
import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.model.DroneCommand
import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import com.example.tacticalcommandandcontrol.core.domain.usecase.drone.GetDroneByIdUseCase
import com.example.tacticalcommandandcontrol.core.domain.usecase.drone.ObserveDronesUseCase
import com.example.tacticalcommandandcontrol.core.domain.usecase.drone.SendCommandUseCase
import com.example.tacticalcommandandcontrol.core.domain.usecase.telemetry.ObserveTelemetryStreamUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DroneControlViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val observeDronesUseCase: ObserveDronesUseCase,
    private val getDroneByIdUseCase: GetDroneByIdUseCase,
    private val sendCommandUseCase: SendCommandUseCase,
    private val observeTelemetryStreamUseCase: ObserveTelemetryStreamUseCase,
) : ViewModel() {

    private val _selectedDroneId = MutableStateFlow<String?>(savedStateHandle["droneId"])
    val selectedDroneId: StateFlow<String?> = _selectedDroneId.asStateFlow()

    private val _commandStatus = MutableStateFlow<CommandStatus>(CommandStatus.Idle)
    val commandStatus: StateFlow<CommandStatus> = _commandStatus.asStateFlow()

    val dronesState: StateFlow<List<Drone>> = observeDronesUseCase()
        .catch { emit(emptyList()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList(),
        )

    val selectedDroneState: StateFlow<DroneControlUiState> = _selectedDroneId
        .flatMapLatest { droneId ->
            if (droneId == null) {
                flowOf(DroneControlUiState.NoDroneSelected)
            } else {
                combine(
                    getDroneByIdUseCase(droneId),
                    observeTelemetryStreamUseCase(droneId),
                ) { drone, telemetry ->
                    if (drone != null) {
                        DroneControlUiState.DroneSelected(
                            drone = drone,
                            telemetry = telemetry,
                        )
                    } else {
                        DroneControlUiState.NoDroneSelected
                    }
                }.catch {
                    emit(DroneControlUiState.Error(it.message ?: "Unknown error"))
                }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = DroneControlUiState.NoDroneSelected,
        )

    fun selectDrone(droneId: String) {
        _selectedDroneId.value = droneId
        _commandStatus.value = CommandStatus.Idle
    }

    fun sendCommand(command: DroneCommand) {
        val droneId = _selectedDroneId.value ?: return
        _commandStatus.value = CommandStatus.Sending(command)

        viewModelScope.launch {
            val result = sendCommandUseCase(droneId, command)
            _commandStatus.value = CommandStatus.Result(command, result)
        }
    }

    fun clearCommandStatus() {
        _commandStatus.value = CommandStatus.Idle
    }
}

sealed interface DroneControlUiState {
    data object NoDroneSelected : DroneControlUiState
    data class DroneSelected(
        val drone: Drone,
        val telemetry: TelemetrySnapshot,
    ) : DroneControlUiState
    data class Error(val message: String) : DroneControlUiState
}

sealed interface CommandStatus {
    data object Idle : CommandStatus
    data class Sending(val command: DroneCommand) : CommandStatus
    data class Result(val command: DroneCommand, val result: CommandResult) : CommandStatus
}

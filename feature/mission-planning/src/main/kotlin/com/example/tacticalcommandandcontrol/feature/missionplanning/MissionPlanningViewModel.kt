package com.example.tacticalcommandandcontrol.feature.missionplanning

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tacticalcommandandcontrol.core.domain.model.Mission
import com.example.tacticalcommandandcontrol.core.domain.model.MissionStatus
import com.example.tacticalcommandandcontrol.core.domain.usecase.mission.CreateMissionUseCase
import com.example.tacticalcommandandcontrol.core.domain.usecase.mission.DeleteMissionUseCase
import com.example.tacticalcommandandcontrol.core.domain.usecase.mission.GetMissionsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import javax.inject.Inject

@HiltViewModel
class MissionPlanningViewModel @Inject constructor(
    getMissionsUseCase: GetMissionsUseCase,
    private val createMissionUseCase: CreateMissionUseCase,
    private val deleteMissionUseCase: DeleteMissionUseCase,
) : ViewModel() {

    val uiState: StateFlow<MissionPlanningUiState> = getMissionsUseCase()
        .map<List<Mission>, MissionPlanningUiState> { missions ->
            MissionPlanningUiState.Success(missions = missions)
        }
        .catch { emit(MissionPlanningUiState.Error(it.message ?: "Unknown error")) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = MissionPlanningUiState.Loading,
        )

    private val _events = MutableStateFlow<MissionPlanningEvent?>(null)
    val events: StateFlow<MissionPlanningEvent?> = _events

    fun createMission(name: String, description: String) {
        viewModelScope.launch {
            val mission = Mission(
                id = "",
                name = name,
                description = description,
                status = MissionStatus.DRAFT,
                waypoints = emptyList(),
                assignedDroneIds = emptyList(),
                createdAt = Instant.now(),
                updatedAt = Instant.now(),
            )
            createMissionUseCase(mission)
                .onSuccess { id -> _events.value = MissionPlanningEvent.MissionCreated(id) }
        }
    }

    fun deleteMission(missionId: String) {
        viewModelScope.launch {
            deleteMissionUseCase(missionId)
        }
    }

    fun consumeEvent() {
        _events.value = null
    }
}

sealed interface MissionPlanningUiState {
    data object Loading : MissionPlanningUiState
    data class Success(val missions: List<Mission>) : MissionPlanningUiState
    data class Error(val message: String) : MissionPlanningUiState
}

sealed interface MissionPlanningEvent {
    data class MissionCreated(val missionId: String) : MissionPlanningEvent
}

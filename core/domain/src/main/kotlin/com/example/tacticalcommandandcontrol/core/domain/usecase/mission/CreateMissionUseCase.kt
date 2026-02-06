package com.example.tacticalcommandandcontrol.core.domain.usecase.mission

import com.example.tacticalcommandandcontrol.core.domain.model.Mission
import com.example.tacticalcommandandcontrol.core.domain.repository.MissionRepository
import javax.inject.Inject

class CreateMissionUseCase @Inject constructor(
    private val missionRepository: MissionRepository,
) {
    suspend operator fun invoke(mission: Mission): Result<String> = runCatching {
        require(mission.name.isNotBlank()) { "Mission name must not be blank" }
        missionRepository.createMission(mission)
    }
}

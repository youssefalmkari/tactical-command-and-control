package com.example.tacticalcommandandcontrol.core.domain.usecase.mission

import com.example.tacticalcommandandcontrol.core.domain.model.Mission
import com.example.tacticalcommandandcontrol.core.domain.repository.MissionRepository
import javax.inject.Inject

class UpdateMissionUseCase @Inject constructor(
    private val missionRepository: MissionRepository,
) {
    suspend operator fun invoke(mission: Mission): Result<Unit> = runCatching {
        missionRepository.updateMission(mission)
    }
}

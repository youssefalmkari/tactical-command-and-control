package com.example.tacticalcommandandcontrol.core.domain.usecase.mission

import com.example.tacticalcommandandcontrol.core.domain.repository.MissionRepository
import javax.inject.Inject

class DeleteMissionUseCase @Inject constructor(
    private val missionRepository: MissionRepository,
) {
    suspend operator fun invoke(missionId: String): Result<Unit> = runCatching {
        missionRepository.deleteMission(missionId)
    }
}

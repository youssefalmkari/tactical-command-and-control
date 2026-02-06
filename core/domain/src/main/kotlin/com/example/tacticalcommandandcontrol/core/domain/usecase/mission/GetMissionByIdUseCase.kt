package com.example.tacticalcommandandcontrol.core.domain.usecase.mission

import com.example.tacticalcommandandcontrol.core.domain.model.Mission
import com.example.tacticalcommandandcontrol.core.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMissionByIdUseCase @Inject constructor(
    private val missionRepository: MissionRepository,
) {
    operator fun invoke(missionId: String): Flow<Mission?> =
        missionRepository.observeMission(missionId)
}

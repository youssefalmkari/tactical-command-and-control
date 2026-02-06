package com.example.tacticalcommandandcontrol.core.domain.usecase.drone

import com.example.tacticalcommandandcontrol.core.domain.model.CommandResult
import com.example.tacticalcommandandcontrol.core.domain.model.DroneCommand
import com.example.tacticalcommandandcontrol.core.domain.repository.DroneRepository
import javax.inject.Inject

class ReturnToLaunchUseCase @Inject constructor(
    private val droneRepository: DroneRepository,
) {
    suspend operator fun invoke(droneId: String): CommandResult =
        droneRepository.sendCommand(droneId, DroneCommand.ReturnToLaunch)
}

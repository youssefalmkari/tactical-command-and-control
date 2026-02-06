package com.example.tacticalcommandandcontrol.core.domain.usecase.drone

import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.repository.DroneRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDroneByIdUseCase @Inject constructor(
    private val droneRepository: DroneRepository,
) {
    operator fun invoke(droneId: String): Flow<Drone?> =
        droneRepository.observeDrone(droneId)
}

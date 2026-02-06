package com.example.tacticalcommandandcontrol.core.domain.usecase.drone

import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.repository.DroneRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDronesUseCase @Inject constructor(
    private val droneRepository: DroneRepository,
) {
    operator fun invoke(): Flow<List<Drone>> =
        droneRepository.observeDrones()
}

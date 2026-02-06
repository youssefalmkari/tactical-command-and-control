package com.example.tacticalcommandandcontrol.core.domain.repository

import com.example.tacticalcommandandcontrol.core.domain.model.CommandResult
import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.model.DroneCommand
import kotlinx.coroutines.flow.Flow

interface DroneRepository {
    fun observeDrones(): Flow<List<Drone>>
    fun observeDrone(droneId: String): Flow<Drone?>
    suspend fun sendCommand(droneId: String, command: DroneCommand): CommandResult
}

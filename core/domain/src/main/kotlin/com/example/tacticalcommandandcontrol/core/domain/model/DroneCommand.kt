package com.example.tacticalcommandandcontrol.core.domain.model

sealed class DroneCommand {
    data object Arm : DroneCommand()
    data object Disarm : DroneCommand()
    data class Takeoff(val altitudeMeters: Double) : DroneCommand()
    data object Land : DroneCommand()
    data object ReturnToLaunch : DroneCommand()
    data class GoTo(val position: Position) : DroneCommand()
    data class StartMission(val missionId: String) : DroneCommand()
    data class SetFlightMode(val mode: FlightMode) : DroneCommand()
    data object EmergencyStop : DroneCommand()
}

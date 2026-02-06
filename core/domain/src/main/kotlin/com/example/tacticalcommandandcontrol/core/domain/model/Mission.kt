package com.example.tacticalcommandandcontrol.core.domain.model

import java.time.Instant

data class Mission(
    val id: String,
    val name: String,
    val description: String = "",
    val status: MissionStatus,
    val waypoints: List<Waypoint>,
    val assignedDroneIds: List<String>,
    val createdAt: Instant,
    val updatedAt: Instant,
) {
    fun canExecute(): Boolean =
        waypoints.isNotEmpty() && assignedDroneIds.isNotEmpty() && status == MissionStatus.PLANNED
}

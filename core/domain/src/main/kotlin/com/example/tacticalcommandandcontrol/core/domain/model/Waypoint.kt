package com.example.tacticalcommandandcontrol.core.domain.model

import java.time.Duration

data class Waypoint(
    val sequence: Int,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val action: WaypointAction,
    val holdTime: Duration = Duration.ZERO,
    val acceptRadius: Double = 5.0,
    val speed: Double = 0.0,
)

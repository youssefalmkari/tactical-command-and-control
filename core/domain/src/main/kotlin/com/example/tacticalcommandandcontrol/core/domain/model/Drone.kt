package com.example.tacticalcommandandcontrol.core.domain.model

import java.time.Instant

data class Drone(
    val id: String,
    val name: String,
    val type: String,
    val status: DroneStatus,
    val flightMode: FlightMode,
    val position: Position?,
    val attitude: Attitude?,
    val battery: BatteryState?,
    val isConnected: Boolean,
    val lastSeen: Instant,
)

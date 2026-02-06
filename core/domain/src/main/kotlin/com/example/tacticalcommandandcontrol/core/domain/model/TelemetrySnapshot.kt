package com.example.tacticalcommandandcontrol.core.domain.model

import java.time.Instant

data class TelemetrySnapshot(
    val droneId: String,
    val position: Position,
    val attitude: Attitude,
    val battery: BatteryState,
    val flightMode: FlightMode,
    val timestamp: Instant,
)

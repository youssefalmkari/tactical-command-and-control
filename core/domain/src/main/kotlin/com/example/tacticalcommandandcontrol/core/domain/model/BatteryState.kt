package com.example.tacticalcommandandcontrol.core.domain.model

data class BatteryState(
    val remainingPercent: Int,
    val voltageMillivolts: Int,
    val currentMilliamps: Int = 0,
    val temperatureCelsius: Double = 0.0,
)

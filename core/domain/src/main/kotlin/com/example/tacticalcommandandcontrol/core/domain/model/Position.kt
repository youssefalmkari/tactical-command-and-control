package com.example.tacticalcommandandcontrol.core.domain.model

data class Position(
    val latitude: Double,
    val longitude: Double,
    val altitudeMsl: Double,
    val relativeAltitude: Double = 0.0,
    val heading: Double = 0.0,
    val groundSpeed: Double = 0.0,
)

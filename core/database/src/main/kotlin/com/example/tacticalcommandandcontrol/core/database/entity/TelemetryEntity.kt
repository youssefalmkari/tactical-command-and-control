package com.example.tacticalcommandandcontrol.core.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "telemetry",
    indices = [
        Index("droneId"),
        Index("timestampEpochMillis"),
        Index("droneId", "timestampEpochMillis"),
    ],
)
data class TelemetryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val droneId: String,
    val latitude: Double,
    val longitude: Double,
    val altitudeMsl: Double,
    val relativeAltitude: Double,
    val heading: Double,
    val groundSpeed: Double,
    val rollDeg: Double,
    val pitchDeg: Double,
    val yawDeg: Double,
    val batteryRemainingPercent: Int,
    val batteryVoltageMillivolts: Int,
    val batteryCurrentMilliamps: Int,
    val batteryTemperatureCelsius: Double,
    val flightMode: String,
    val timestampEpochMillis: Long,
)

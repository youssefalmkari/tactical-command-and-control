package com.example.tacticalcommandandcontrol.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "drones")
data class DroneEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val type: String,
    val status: String,
    val flightMode: String,
    val latitude: Double?,
    val longitude: Double?,
    val altitudeMsl: Double?,
    val relativeAltitude: Double?,
    val heading: Double?,
    val groundSpeed: Double?,
    val rollDeg: Double?,
    val pitchDeg: Double?,
    val yawDeg: Double?,
    val batteryRemainingPercent: Int?,
    val batteryVoltageMillivolts: Int?,
    val batteryCurrentMilliamps: Int?,
    val batteryTemperatureCelsius: Double?,
    val isConnected: Boolean,
    val lastSeenEpochMillis: Long,
)

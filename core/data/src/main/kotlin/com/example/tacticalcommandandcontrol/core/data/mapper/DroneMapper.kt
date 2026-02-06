package com.example.tacticalcommandandcontrol.core.data.mapper

import com.example.tacticalcommandandcontrol.core.database.entity.DroneEntity
import com.example.tacticalcommandandcontrol.core.domain.model.Attitude
import com.example.tacticalcommandandcontrol.core.domain.model.BatteryState
import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.model.DroneStatus
import com.example.tacticalcommandandcontrol.core.domain.model.FlightMode
import com.example.tacticalcommandandcontrol.core.domain.model.Position
import java.time.Instant

fun DroneEntity.toDomain(): Drone {
    val lat = latitude
    val lon = longitude
    val alt = altitudeMsl
    val roll = rollDeg
    val pitch = pitchDeg
    val yaw = yawDeg
    val batPercent = batteryRemainingPercent
    val batVolts = batteryVoltageMillivolts

    return Drone(
        id = id,
        name = name,
        type = type,
        status = DroneStatus.entries.find { it.name == status } ?: DroneStatus.UNKNOWN,
        flightMode = FlightMode.entries.find { it.name == flightMode } ?: FlightMode.MANUAL,
        position = if (lat != null && lon != null && alt != null) {
            Position(
                latitude = lat,
                longitude = lon,
                altitudeMsl = alt,
                relativeAltitude = relativeAltitude ?: 0.0,
                heading = heading ?: 0.0,
                groundSpeed = groundSpeed ?: 0.0,
            )
        } else null,
        attitude = if (roll != null && pitch != null && yaw != null) {
            Attitude(rollDeg = roll, pitchDeg = pitch, yawDeg = yaw)
        } else null,
        battery = if (batPercent != null && batVolts != null) {
            BatteryState(
                remainingPercent = batPercent,
                voltageMillivolts = batVolts,
                currentMilliamps = batteryCurrentMilliamps ?: 0,
                temperatureCelsius = batteryTemperatureCelsius ?: 0.0,
            )
        } else null,
        isConnected = isConnected,
        lastSeen = Instant.ofEpochMilli(lastSeenEpochMillis),
    )
}

fun Drone.toEntity(): DroneEntity = DroneEntity(
    id = id,
    name = name,
    type = type,
    status = status.name,
    flightMode = flightMode.name,
    latitude = position?.latitude,
    longitude = position?.longitude,
    altitudeMsl = position?.altitudeMsl,
    relativeAltitude = position?.relativeAltitude,
    heading = position?.heading,
    groundSpeed = position?.groundSpeed,
    rollDeg = attitude?.rollDeg,
    pitchDeg = attitude?.pitchDeg,
    yawDeg = attitude?.yawDeg,
    batteryRemainingPercent = battery?.remainingPercent,
    batteryVoltageMillivolts = battery?.voltageMillivolts,
    batteryCurrentMilliamps = battery?.currentMilliamps,
    batteryTemperatureCelsius = battery?.temperatureCelsius,
    isConnected = isConnected,
    lastSeenEpochMillis = lastSeen.toEpochMilli(),
)

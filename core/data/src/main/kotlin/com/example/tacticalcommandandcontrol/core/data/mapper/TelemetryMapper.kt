package com.example.tacticalcommandandcontrol.core.data.mapper

import com.example.tacticalcommandandcontrol.core.database.entity.TelemetryEntity
import com.example.tacticalcommandandcontrol.core.domain.model.Attitude
import com.example.tacticalcommandandcontrol.core.domain.model.BatteryState
import com.example.tacticalcommandandcontrol.core.domain.model.FlightMode
import com.example.tacticalcommandandcontrol.core.domain.model.Position
import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import java.time.Instant

fun TelemetryEntity.toDomain(): TelemetrySnapshot = TelemetrySnapshot(
    droneId = droneId,
    position = Position(
        latitude = latitude,
        longitude = longitude,
        altitudeMsl = altitudeMsl,
        relativeAltitude = relativeAltitude,
        heading = heading,
        groundSpeed = groundSpeed,
    ),
    attitude = Attitude(
        rollDeg = rollDeg,
        pitchDeg = pitchDeg,
        yawDeg = yawDeg,
    ),
    battery = BatteryState(
        remainingPercent = batteryRemainingPercent,
        voltageMillivolts = batteryVoltageMillivolts,
        currentMilliamps = batteryCurrentMilliamps,
        temperatureCelsius = batteryTemperatureCelsius,
    ),
    flightMode = FlightMode.entries.find { it.name == flightMode } ?: FlightMode.MANUAL,
    timestamp = Instant.ofEpochMilli(timestampEpochMillis),
)

fun TelemetrySnapshot.toEntity(): TelemetryEntity = TelemetryEntity(
    droneId = droneId,
    latitude = position.latitude,
    longitude = position.longitude,
    altitudeMsl = position.altitudeMsl,
    relativeAltitude = position.relativeAltitude,
    heading = position.heading,
    groundSpeed = position.groundSpeed,
    rollDeg = attitude.rollDeg,
    pitchDeg = attitude.pitchDeg,
    yawDeg = attitude.yawDeg,
    batteryRemainingPercent = battery.remainingPercent,
    batteryVoltageMillivolts = battery.voltageMillivolts,
    batteryCurrentMilliamps = battery.currentMilliamps,
    batteryTemperatureCelsius = battery.temperatureCelsius,
    flightMode = flightMode.name,
    timestampEpochMillis = timestamp.toEpochMilli(),
)

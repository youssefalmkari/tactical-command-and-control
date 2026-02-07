package com.example.tacticalcommandandcontrol.demo

import com.example.tacticalcommandandcontrol.core.database.entity.DroneEntity
import com.example.tacticalcommandandcontrol.core.database.entity.TelemetryEntity
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

enum class FlightPattern {
    CIRCULAR,
    LINEAR_RETURN,
    STATIONARY,
}

class SimulatedDroneState(
    val id: String,
    val name: String,
    val type: String,
    initialStatus: String,
    initialFlightMode: String,
    initialLat: Double,
    initialLon: Double,
    initialAltMsl: Double,
    initialRelAlt: Double,
    initialBattery: Int,
    val flightPattern: FlightPattern,
) {
    // Current state
    var status: String = initialStatus
        private set
    var flightMode: String = initialFlightMode
        private set
    var latitude: Double = initialLat
        private set
    var longitude: Double = initialLon
        private set
    var altitudeMsl: Double = initialAltMsl
        private set
    var relativeAltitude: Double = initialRelAlt
        private set
    var heading: Double = 0.0
        private set
    var groundSpeed: Double = 0.0
        private set
    var rollDeg: Double = 0.0
        private set
    var pitchDeg: Double = 0.0
        private set
    var yawDeg: Double = 0.0
        private set
    var batteryPercent: Double = initialBattery.toDouble()
        private set
    var voltageMillivolts: Int = 16800
        private set
    var currentMilliamps: Int = 0
        private set
    var temperatureCelsius: Double = 35.0
        private set

    // Circular patrol state
    private var angle: Double = 0.0
    private val circleCenter = Pair(initialLat, initialLon)
    private val circleRadius = 0.0018 // ~200m in degrees
    private val angularSpeed = 0.075 // rad/sec (~15 m/s at 200m radius)

    // Linear return state
    private val startLat = initialLat
    private val startLon = initialLon
    private val baseLat = 37.7749
    private val baseLon = -122.4194
    private val startAlt = initialAltMsl
    private val targetAlt = 10.0
    private var progress: Double = 0.0
    private val returnDuration = 120.0 // seconds

    fun syncStatus(dbStatus: String) {
        if (dbStatus != status) {
            status = dbStatus
            when (status) {
                "ARMED" -> {
                    flightMode = "STABILIZED"
                    currentMilliamps = 5000
                }
                "FLYING" -> {
                    flightMode = "AUTO_MISSION"
                    currentMilliamps = 15000
                }
                "RETURNING" -> {
                    flightMode = "AUTO_RTL"
                    currentMilliamps = 18000
                    progress = 0.0
                }
                "LANDING" -> {
                    flightMode = "AUTO_RTL"
                    currentMilliamps = 12000
                }
                "IDLE", "LANDED" -> {
                    flightMode = "MANUAL"
                    groundSpeed = 0.0
                    currentMilliamps = 500
                }
            }
        }
    }

    fun step(dt: Double) {
        when (status) {
            "FLYING" -> stepFlying(dt)
            "RETURNING" -> stepReturning(dt)
            "ARMED" -> stepArmed(dt)
            "LANDING" -> stepLanding(dt)
            "IDLE", "LANDED" -> stepIdle(dt)
        }
        updateBattery(dt)
        updateVoltage()
    }

    private fun stepFlying(dt: Double) {
        when (flightPattern) {
            FlightPattern.CIRCULAR -> {
                angle += angularSpeed * dt
                latitude = circleCenter.first + circleRadius * cos(angle)
                longitude = circleCenter.second + circleRadius * sin(angle)
                heading = Math.toDegrees(angle + Math.PI / 2) % 360
                if (heading < 0) heading += 360
                groundSpeed = 15.0 + Random.nextDouble(-0.5, 0.5)
                rollDeg = -15.0 * sin(angularSpeed * dt) + Random.nextDouble(-1.0, 1.0)
                pitchDeg = Random.nextDouble(-2.0, 2.0)
                yawDeg = heading + Random.nextDouble(-1.0, 1.0)
                currentMilliamps = 15000 + Random.nextInt(-500, 500)
            }
            FlightPattern.STATIONARY -> {
                latitude += Random.nextDouble(-0.00001, 0.00001)
                longitude += Random.nextDouble(-0.00001, 0.00001)
                groundSpeed = Random.nextDouble(0.0, 0.5)
                heading += Random.nextDouble(-1.0, 1.0)
                rollDeg = Random.nextDouble(-2.0, 2.0)
                pitchDeg = Random.nextDouble(-2.0, 2.0)
            }
            FlightPattern.LINEAR_RETURN -> stepReturning(dt)
        }
    }

    private fun stepReturning(dt: Double) {
        progress = min(1.0, progress + dt / returnDuration)
        latitude = startLat + (baseLat - startLat) * progress
        longitude = startLon + (baseLon - startLon) * progress
        altitudeMsl = startAlt + (targetAlt - startAlt) * progress
        relativeAltitude = max(0.0, altitudeMsl - 10.0)
        groundSpeed = if (progress < 1.0) 12.0 + Random.nextDouble(-0.5, 0.5) else 0.0
        heading = 220.0 + Random.nextDouble(-2.0, 2.0)
        rollDeg = Random.nextDouble(-3.0, 3.0)
        pitchDeg = -5.0 + Random.nextDouble(-1.0, 1.0)
        currentMilliamps = 18000 + Random.nextInt(-500, 500)

        if (progress >= 1.0) {
            status = "LANDED"
            flightMode = "MANUAL"
            groundSpeed = 0.0
            relativeAltitude = 0.0
            currentMilliamps = 500
        }
    }

    private fun stepArmed(dt: Double) {
        latitude += Random.nextDouble(-0.000001, 0.000001)
        longitude += Random.nextDouble(-0.000001, 0.000001)
        groundSpeed = 0.0
        rollDeg = Random.nextDouble(-0.5, 0.5)
        pitchDeg = Random.nextDouble(-0.5, 0.5)
        currentMilliamps = 5000 + Random.nextInt(-200, 200)
    }

    private fun stepLanding(dt: Double) {
        relativeAltitude = max(0.0, relativeAltitude - 1.5 * dt)
        altitudeMsl = max(10.0, altitudeMsl - 1.5 * dt)
        groundSpeed = max(0.0, groundSpeed - 2.0 * dt)
        currentMilliamps = 12000 + Random.nextInt(-300, 300)

        if (relativeAltitude <= 0.0) {
            status = "LANDED"
            flightMode = "MANUAL"
            groundSpeed = 0.0
            relativeAltitude = 0.0
            currentMilliamps = 500
        }
    }

    private fun stepIdle(dt: Double) {
        currentMilliamps = 500 + Random.nextInt(-50, 50)
        groundSpeed = 0.0
    }

    private fun updateBattery(dt: Double) {
        val drainRate = when (status) {
            "FLYING" -> 0.02
            "RETURNING" -> 0.03
            "ARMED" -> 0.005
            "LANDING" -> 0.015
            else -> 0.001
        }
        batteryPercent = max(0.0, batteryPercent - drainRate * dt)
    }

    private fun updateVoltage() {
        // Simulate voltage curve: 16.8V full -> 14.0V empty (4S LiPo)
        voltageMillivolts = (14000 + (batteryPercent / 100.0) * 2800).toInt()
        temperatureCelsius = 35.0 + (abs(currentMilliamps) / 1000.0) * 0.5 + Random.nextDouble(-0.3, 0.3)
    }

    fun toDroneEntity(timestampMillis: Long): DroneEntity = DroneEntity(
        id = id,
        name = name,
        type = type,
        status = status,
        flightMode = flightMode,
        latitude = latitude,
        longitude = longitude,
        altitudeMsl = altitudeMsl,
        relativeAltitude = relativeAltitude,
        heading = heading,
        groundSpeed = groundSpeed,
        rollDeg = rollDeg,
        pitchDeg = pitchDeg,
        yawDeg = yawDeg,
        batteryRemainingPercent = batteryPercent.toInt(),
        batteryVoltageMillivolts = voltageMillivolts,
        batteryCurrentMilliamps = currentMilliamps,
        batteryTemperatureCelsius = temperatureCelsius,
        isConnected = true,
        lastSeenEpochMillis = timestampMillis,
    )

    fun toTelemetryEntity(timestampMillis: Long): TelemetryEntity = TelemetryEntity(
        droneId = id,
        latitude = latitude,
        longitude = longitude,
        altitudeMsl = altitudeMsl,
        relativeAltitude = relativeAltitude,
        heading = heading,
        groundSpeed = groundSpeed,
        rollDeg = rollDeg,
        pitchDeg = pitchDeg,
        yawDeg = yawDeg,
        batteryRemainingPercent = batteryPercent.toInt(),
        batteryVoltageMillivolts = voltageMillivolts,
        batteryCurrentMilliamps = currentMilliamps,
        batteryTemperatureCelsius = temperatureCelsius,
        flightMode = flightMode,
        timestampEpochMillis = timestampMillis,
    )
}

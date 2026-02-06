package com.example.tacticalcommandandcontrol.core.data.mapper

import com.example.tacticalcommandandcontrol.core.database.entity.TelemetryEntity
import com.example.tacticalcommandandcontrol.core.domain.model.Attitude
import com.example.tacticalcommandandcontrol.core.domain.model.BatteryState
import com.example.tacticalcommandandcontrol.core.domain.model.FlightMode
import com.example.tacticalcommandandcontrol.core.domain.model.Position
import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Instant

class TelemetryMapperTest {

    private val now = Instant.parse("2025-06-15T12:00:00Z")

    private val entity = TelemetryEntity(
        id = 42,
        droneId = "drone-1",
        latitude = 37.7749,
        longitude = -122.4194,
        altitudeMsl = 100.0,
        relativeAltitude = 50.0,
        heading = 180.0,
        groundSpeed = 12.5,
        rollDeg = 1.5,
        pitchDeg = -2.0,
        yawDeg = 180.0,
        batteryRemainingPercent = 72,
        batteryVoltageMillivolts = 22100,
        batteryCurrentMilliamps = 14500,
        batteryTemperatureCelsius = 38.0,
        flightMode = "AUTO_MISSION",
        timestampEpochMillis = now.toEpochMilli(),
    )

    @Test
    fun `entity to domain maps all fields`() {
        val snapshot = entity.toDomain()

        assertEquals("drone-1", snapshot.droneId)
        assertEquals(37.7749, snapshot.position.latitude, 0.0001)
        assertEquals(-122.4194, snapshot.position.longitude, 0.0001)
        assertEquals(100.0, snapshot.position.altitudeMsl, 0.0001)
        assertEquals(180.0, snapshot.position.heading, 0.0001)
        assertEquals(12.5, snapshot.position.groundSpeed, 0.0001)
        assertEquals(1.5, snapshot.attitude.rollDeg, 0.0001)
        assertEquals(-2.0, snapshot.attitude.pitchDeg, 0.0001)
        assertEquals(72, snapshot.battery.remainingPercent)
        assertEquals(FlightMode.AUTO_MISSION, snapshot.flightMode)
        assertEquals(now, snapshot.timestamp)
    }

    @Test
    fun `domain to entity round trip`() {
        val snapshot = TelemetrySnapshot(
            droneId = "drone-2",
            position = Position(
                latitude = 38.0,
                longitude = -121.0,
                altitudeMsl = 200.0,
                relativeAltitude = 100.0,
                heading = 270.0,
                groundSpeed = 20.0,
            ),
            attitude = Attitude(rollDeg = 0.0, pitchDeg = 0.0, yawDeg = 270.0),
            battery = BatteryState(
                remainingPercent = 50,
                voltageMillivolts = 21000,
                currentMilliamps = 16000,
                temperatureCelsius = 40.0,
            ),
            flightMode = FlightMode.OFFBOARD,
            timestamp = now,
        )
        val converted = snapshot.toEntity()

        assertEquals("drone-2", converted.droneId)
        assertEquals(38.0, converted.latitude, 0.0001)
        assertEquals(270.0, converted.heading, 0.0001)
        assertEquals(50, converted.batteryRemainingPercent)
        assertEquals("OFFBOARD", converted.flightMode)
        assertEquals(now.toEpochMilli(), converted.timestampEpochMillis)
    }

    @Test
    fun `unknown flight mode defaults to MANUAL`() {
        val badEntity = entity.copy(flightMode = "NONEXISTENT")
        val snapshot = badEntity.toDomain()
        assertEquals(FlightMode.MANUAL, snapshot.flightMode)
    }
}

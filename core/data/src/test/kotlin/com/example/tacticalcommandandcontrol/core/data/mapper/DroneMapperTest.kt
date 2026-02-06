package com.example.tacticalcommandandcontrol.core.data.mapper

import com.example.tacticalcommandandcontrol.core.database.entity.DroneEntity
import com.example.tacticalcommandandcontrol.core.domain.model.Attitude
import com.example.tacticalcommandandcontrol.core.domain.model.BatteryState
import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.model.DroneStatus
import com.example.tacticalcommandandcontrol.core.domain.model.FlightMode
import com.example.tacticalcommandandcontrol.core.domain.model.Position
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Instant

class DroneMapperTest {

    private val now = Instant.parse("2025-06-15T12:00:00Z")

    private val fullEntity = DroneEntity(
        id = "drone-1",
        name = "Alpha",
        type = "Quadcopter",
        status = "FLYING",
        flightMode = "AUTO_MISSION",
        latitude = 37.7749,
        longitude = -122.4194,
        altitudeMsl = 100.0,
        relativeAltitude = 50.0,
        heading = 90.0,
        groundSpeed = 15.0,
        rollDeg = 1.0,
        pitchDeg = 2.0,
        yawDeg = 3.0,
        batteryRemainingPercent = 85,
        batteryVoltageMillivolts = 22400,
        batteryCurrentMilliamps = 15000,
        batteryTemperatureCelsius = 35.5,
        isConnected = true,
        lastSeenEpochMillis = now.toEpochMilli(),
    )

    @Test
    fun `entity to domain maps all fields`() {
        val drone = fullEntity.toDomain()

        assertEquals("drone-1", drone.id)
        assertEquals("Alpha", drone.name)
        assertEquals("Quadcopter", drone.type)
        assertEquals(DroneStatus.FLYING, drone.status)
        assertEquals(FlightMode.AUTO_MISSION, drone.flightMode)
        assertTrue(drone.isConnected)
        assertEquals(now, drone.lastSeen)

        assertNotNull(drone.position)
        assertEquals(37.7749, drone.position!!.latitude, 0.0001)
        assertEquals(-122.4194, drone.position!!.longitude, 0.0001)
        assertEquals(100.0, drone.position!!.altitudeMsl, 0.0001)

        assertNotNull(drone.attitude)
        assertEquals(1.0, drone.attitude!!.rollDeg, 0.0001)

        assertNotNull(drone.battery)
        assertEquals(85, drone.battery!!.remainingPercent)
        assertEquals(22400, drone.battery!!.voltageMillivolts)
    }

    @Test
    fun `entity with null position maps to null`() {
        val entity = fullEntity.copy(latitude = null, longitude = null, altitudeMsl = null)
        val drone = entity.toDomain()
        assertNull(drone.position)
    }

    @Test
    fun `entity with null battery maps to null`() {
        val entity = fullEntity.copy(batteryRemainingPercent = null, batteryVoltageMillivolts = null)
        val drone = entity.toDomain()
        assertNull(drone.battery)
    }

    @Test
    fun `entity with unknown status defaults to UNKNOWN`() {
        val entity = fullEntity.copy(status = "NONEXISTENT")
        val drone = entity.toDomain()
        assertEquals(DroneStatus.UNKNOWN, drone.status)
    }

    @Test
    fun `domain to entity round trip preserves data`() {
        val drone = fullEntity.toDomain()
        val entity = drone.toEntity()

        assertEquals(fullEntity.id, entity.id)
        assertEquals(fullEntity.name, entity.name)
        assertEquals(fullEntity.status, entity.status)
        assertEquals(fullEntity.latitude, entity.latitude)
        assertEquals(fullEntity.longitude, entity.longitude)
        assertEquals(fullEntity.batteryRemainingPercent, entity.batteryRemainingPercent)
    }

    @Test
    fun `domain with null position maps to null entity fields`() {
        val drone = Drone(
            id = "drone-2",
            name = "Bravo",
            type = "Fixed Wing",
            status = DroneStatus.IDLE,
            flightMode = FlightMode.MANUAL,
            position = null,
            attitude = null,
            battery = null,
            isConnected = false,
            lastSeen = now,
        )
        val entity = drone.toEntity()
        assertNull(entity.latitude)
        assertNull(entity.longitude)
        assertNull(entity.batteryRemainingPercent)
    }

}

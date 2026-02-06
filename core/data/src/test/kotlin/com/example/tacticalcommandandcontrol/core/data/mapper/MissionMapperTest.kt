package com.example.tacticalcommandandcontrol.core.data.mapper

import com.example.tacticalcommandandcontrol.core.database.entity.MissionEntity
import com.example.tacticalcommandandcontrol.core.database.entity.WaypointEntity
import com.example.tacticalcommandandcontrol.core.database.relation.MissionWithWaypoints
import com.example.tacticalcommandandcontrol.core.domain.model.Mission
import com.example.tacticalcommandandcontrol.core.domain.model.MissionStatus
import com.example.tacticalcommandandcontrol.core.domain.model.Waypoint
import com.example.tacticalcommandandcontrol.core.domain.model.WaypointAction
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Duration
import java.time.Instant

class MissionMapperTest {

    private val now = Instant.parse("2025-06-15T12:00:00Z")

    @Test
    fun `MissionWithWaypoints to domain maps correctly`() {
        val missionEntity = MissionEntity(
            id = "mission-1",
            name = "Recon Alpha",
            description = "Survey zone A",
            status = "PLANNED",
            assignedDroneIds = "drone-1,drone-2",
            createdAtEpochMillis = now.toEpochMilli(),
            updatedAtEpochMillis = now.plusSeconds(3600).toEpochMilli(),
        )
        val waypointEntities = listOf(
            WaypointEntity(
                id = 1,
                missionId = "mission-1",
                sequence = 1,
                latitude = 37.0,
                longitude = -122.0,
                altitude = 50.0,
                action = "NAVIGATE",
                holdTimeMillis = 0,
                acceptRadius = 5.0,
                speed = 10.0,
            ),
            WaypointEntity(
                id = 2,
                missionId = "mission-1",
                sequence = 0,
                latitude = 37.1,
                longitude = -122.1,
                altitude = 60.0,
                action = "TAKEOFF",
                holdTimeMillis = 5000,
                acceptRadius = 3.0,
                speed = 0.0,
            ),
        )

        val missionWithWaypoints = MissionWithWaypoints(
            mission = missionEntity,
            waypoints = waypointEntities,
        )

        val mission = missionWithWaypoints.toDomain()

        assertEquals("mission-1", mission.id)
        assertEquals("Recon Alpha", mission.name)
        assertEquals(MissionStatus.PLANNED, mission.status)
        assertEquals(listOf("drone-1", "drone-2"), mission.assignedDroneIds)
        assertEquals(2, mission.waypoints.size)

        // Sorted by sequence
        assertEquals(0, mission.waypoints[0].sequence)
        assertEquals(1, mission.waypoints[1].sequence)
    }

    @Test
    fun `empty assignedDroneIds produces empty list`() {
        val entity = MissionEntity(
            id = "m1",
            name = "Test",
            description = "",
            status = "DRAFT",
            assignedDroneIds = "",
            createdAtEpochMillis = now.toEpochMilli(),
            updatedAtEpochMillis = now.toEpochMilli(),
        )
        val result = MissionWithWaypoints(entity, emptyList()).toDomain()
        assertTrue(result.assignedDroneIds.isEmpty())
    }

    @Test
    fun `Mission to entity round trip`() {
        val mission = Mission(
            id = "m2",
            name = "Patrol",
            description = "Border patrol",
            status = MissionStatus.EXECUTING,
            waypoints = emptyList(),
            assignedDroneIds = listOf("d1", "d2", "d3"),
            createdAt = now,
            updatedAt = now,
        )
        val entity = mission.toEntity()

        assertEquals("m2", entity.id)
        assertEquals("EXECUTING", entity.status)
        assertEquals("d1,d2,d3", entity.assignedDroneIds)
    }

    @Test
    fun `Waypoint to entity preserves all fields`() {
        val waypoint = Waypoint(
            sequence = 3,
            latitude = 38.0,
            longitude = -121.0,
            altitude = 75.0,
            action = WaypointAction.LOITER,
            holdTime = Duration.ofSeconds(30),
            acceptRadius = 10.0,
            speed = 5.0,
        )
        val entity = waypoint.toEntity("mission-x")

        assertEquals("mission-x", entity.missionId)
        assertEquals(3, entity.sequence)
        assertEquals("LOITER", entity.action)
        assertEquals(30000L, entity.holdTimeMillis)
        assertEquals(10.0, entity.acceptRadius, 0.001)
    }

    @Test
    fun `unknown status defaults to DRAFT`() {
        val entity = MissionEntity(
            id = "m3",
            name = "Test",
            description = "",
            status = "BOGUS",
            assignedDroneIds = "",
            createdAtEpochMillis = now.toEpochMilli(),
            updatedAtEpochMillis = now.toEpochMilli(),
        )
        val mission = MissionWithWaypoints(entity, emptyList()).toDomain()
        assertEquals(MissionStatus.DRAFT, mission.status)
    }
}

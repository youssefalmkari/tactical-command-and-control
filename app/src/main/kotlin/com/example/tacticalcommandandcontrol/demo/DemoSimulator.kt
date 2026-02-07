package com.example.tacticalcommandandcontrol.demo

import com.example.tacticalcommandandcontrol.core.database.dao.DroneDao
import com.example.tacticalcommandandcontrol.core.database.dao.MissionDao
import com.example.tacticalcommandandcontrol.core.database.dao.TelemetryDao
import com.example.tacticalcommandandcontrol.core.database.dao.WaypointDao
import com.example.tacticalcommandandcontrol.core.database.entity.MissionEntity
import com.example.tacticalcommandandcontrol.core.database.entity.WaypointEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
class DemoSimulator(
    private val droneDao: DroneDao,
    private val telemetryDao: TelemetryDao,
    private val missionDao: MissionDao,
    private val waypointDao: WaypointDao,
    private val scope: CoroutineScope,
) {
    private val drones = listOf(
        SimulatedDroneState(
            id = "drone-1",
            name = "Alpha-1",
            type = "Quadcopter",
            initialStatus = "FLYING",
            initialFlightMode = "AUTO_MISSION",
            initialLat = 37.7750,
            initialLon = -122.4195,
            initialAltMsl = 100.0,
            initialRelAlt = 50.0,
            initialBattery = 85,
            flightPattern = FlightPattern.CIRCULAR,
        ),
        SimulatedDroneState(
            id = "drone-2",
            name = "Bravo-2",
            type = "Fixed Wing",
            initialStatus = "ARMED",
            initialFlightMode = "STABILIZED",
            initialLat = 37.7749,
            initialLon = -122.4194,
            initialAltMsl = 10.0,
            initialRelAlt = 0.0,
            initialBattery = 95,
            flightPattern = FlightPattern.STATIONARY,
        ),
        SimulatedDroneState(
            id = "drone-3",
            name = "Charlie-3",
            type = "Quadcopter",
            initialStatus = "IDLE",
            initialFlightMode = "MANUAL",
            initialLat = 37.7748,
            initialLon = -122.4180,
            initialAltMsl = 10.0,
            initialRelAlt = 0.0,
            initialBattery = 60,
            flightPattern = FlightPattern.STATIONARY,
        ),
        SimulatedDroneState(
            id = "drone-4",
            name = "Delta-4",
            type = "VTOL",
            initialStatus = "RETURNING",
            initialFlightMode = "AUTO_RTL",
            initialLat = 37.7800,
            initialLon = -122.4250,
            initialAltMsl = 80.0,
            initialRelAlt = 70.0,
            initialBattery = 40,
            flightPattern = FlightPattern.LINEAR_RETURN,
        ),
    )

    private var iterationCount = 0L

    fun start() {
        scope.launch {
            seedDatabase()
            runTelemetryLoop()
        }
    }

    private suspend fun seedDatabase() {
        Timber.d("DemoSimulator: Seeding database with mock data")

        // Seed drones
        val now = System.currentTimeMillis()
        droneDao.upsertAll(drones.map { it.toDroneEntity(now) })

        // Seed missions
        seedMissions(now)

        Timber.d("DemoSimulator: Database seeded successfully")
    }

    private suspend fun seedMissions(now: Long) {
        val sfCenterLat = 37.775
        val sfCenterLon = -122.419

        // Mission 1: Perimeter Sweep (hexagonal, EXECUTING)
        missionDao.upsert(
            MissionEntity(
                id = "mission-1",
                name = "Perimeter Sweep",
                description = "Hexagonal patrol pattern around the operational area",
                status = "EXECUTING",
                assignedDroneIds = "[\"drone-1\"]",
                createdAtEpochMillis = now - 3_600_000,
                updatedAtEpochMillis = now,
            )
        )
        waypointDao.deleteByMissionId("mission-1")
        val hexRadius = 0.003
        waypointDao.insertAll(
            (0..5).map { i ->
                val angle = Math.toRadians(60.0 * i)
                WaypointEntity(
                    missionId = "mission-1",
                    sequence = i,
                    latitude = sfCenterLat + hexRadius * kotlin.math.cos(angle),
                    longitude = sfCenterLon + hexRadius * kotlin.math.sin(angle),
                    altitude = 100.0,
                    action = if (i == 0) "TAKEOFF" else "NAVIGATE",
                    holdTimeMillis = 5000,
                    acceptRadius = 5.0,
                    speed = 15.0,
                )
            }
        )

        // Mission 2: Supply Drop (linear route, PLANNED)
        missionDao.upsert(
            MissionEntity(
                id = "mission-2",
                name = "Supply Drop",
                description = "Linear supply delivery route to forward operating base",
                status = "PLANNED",
                assignedDroneIds = "[\"drone-2\"]",
                createdAtEpochMillis = now - 1_800_000,
                updatedAtEpochMillis = now - 900_000,
            )
        )
        waypointDao.deleteByMissionId("mission-2")
        waypointDao.insertAll(
            listOf(
                WaypointEntity(
                    missionId = "mission-2",
                    sequence = 0,
                    latitude = sfCenterLat,
                    longitude = sfCenterLon,
                    altitude = 50.0,
                    action = "TAKEOFF",
                    holdTimeMillis = 0,
                    acceptRadius = 5.0,
                    speed = 20.0,
                ),
                WaypointEntity(
                    missionId = "mission-2",
                    sequence = 1,
                    latitude = sfCenterLat + 0.005,
                    longitude = sfCenterLon + 0.003,
                    altitude = 80.0,
                    action = "NAVIGATE",
                    holdTimeMillis = 0,
                    acceptRadius = 5.0,
                    speed = 20.0,
                ),
                WaypointEntity(
                    missionId = "mission-2",
                    sequence = 2,
                    latitude = sfCenterLat + 0.010,
                    longitude = sfCenterLon + 0.006,
                    altitude = 80.0,
                    action = "LOITER",
                    holdTimeMillis = 30_000,
                    acceptRadius = 5.0,
                    speed = 0.0,
                ),
                WaypointEntity(
                    missionId = "mission-2",
                    sequence = 3,
                    latitude = sfCenterLat,
                    longitude = sfCenterLon,
                    altitude = 20.0,
                    action = "LAND",
                    holdTimeMillis = 0,
                    acceptRadius = 5.0,
                    speed = 10.0,
                ),
            )
        )

        // Mission 3: Recon Alpha (triangle, DRAFT)
        missionDao.upsert(
            MissionEntity(
                id = "mission-3",
                name = "Recon Alpha",
                description = "Triangular reconnaissance pattern for area survey",
                status = "DRAFT",
                assignedDroneIds = "[]",
                createdAtEpochMillis = now - 600_000,
                updatedAtEpochMillis = now - 600_000,
            )
        )
        waypointDao.deleteByMissionId("mission-3")
        waypointDao.insertAll(
            listOf(
                WaypointEntity(
                    missionId = "mission-3",
                    sequence = 0,
                    latitude = sfCenterLat + 0.004,
                    longitude = sfCenterLon,
                    altitude = 60.0,
                    action = "TAKEOFF",
                    holdTimeMillis = 0,
                    acceptRadius = 5.0,
                    speed = 12.0,
                ),
                WaypointEntity(
                    missionId = "mission-3",
                    sequence = 1,
                    latitude = sfCenterLat - 0.002,
                    longitude = sfCenterLon - 0.004,
                    altitude = 60.0,
                    action = "NAVIGATE",
                    holdTimeMillis = 10_000,
                    acceptRadius = 5.0,
                    speed = 12.0,
                ),
                WaypointEntity(
                    missionId = "mission-3",
                    sequence = 2,
                    latitude = sfCenterLat - 0.002,
                    longitude = sfCenterLon + 0.004,
                    altitude = 60.0,
                    action = "NAVIGATE",
                    holdTimeMillis = 10_000,
                    acceptRadius = 5.0,
                    speed = 12.0,
                ),
            )
        )
    }

    private suspend fun runTelemetryLoop() {
        Timber.d("DemoSimulator: Starting 1 Hz telemetry loop")
        while (true) {
            val now = System.currentTimeMillis()

            for (drone in drones) {
                // Read current status from DB to pick up command-triggered changes
                val dbDrone = droneDao.getById(drone.id)
                if (dbDrone != null) {
                    drone.syncStatus(dbDrone.status)
                }

                // Advance simulation
                drone.step(1.0)

                // Write updated drone state
                droneDao.upsert(drone.toDroneEntity(now))

                // Write telemetry row
                telemetryDao.insert(drone.toTelemetryEntity(now))
            }

            iterationCount++

            // Periodic cleanup every 60 seconds
            if (iterationCount % 60 == 0L) {
                val cutoff = now - 5 * 60 * 1000 // 5 minutes
                telemetryDao.deleteOlderThan(cutoff)
                Timber.d("DemoSimulator: Cleaned telemetry older than 5 minutes")
            }

            delay(1000)
        }
    }
}

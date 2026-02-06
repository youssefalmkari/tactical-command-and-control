package com.example.tacticalcommandandcontrol.core.data.mapper

import com.example.tacticalcommandandcontrol.core.database.entity.MissionEntity
import com.example.tacticalcommandandcontrol.core.database.entity.WaypointEntity
import com.example.tacticalcommandandcontrol.core.database.relation.MissionWithWaypoints
import com.example.tacticalcommandandcontrol.core.domain.model.Mission
import com.example.tacticalcommandandcontrol.core.domain.model.MissionStatus
import com.example.tacticalcommandandcontrol.core.domain.model.Waypoint
import com.example.tacticalcommandandcontrol.core.domain.model.WaypointAction
import java.time.Duration
import java.time.Instant

fun MissionWithWaypoints.toDomain(): Mission = Mission(
    id = mission.id,
    name = mission.name,
    description = mission.description,
    status = MissionStatus.entries.find { it.name == mission.status } ?: MissionStatus.DRAFT,
    waypoints = waypoints
        .sortedBy { it.sequence }
        .map { it.toDomain() },
    assignedDroneIds = mission.assignedDroneIds
        .split(",")
        .filter { it.isNotBlank() },
    createdAt = Instant.ofEpochMilli(mission.createdAtEpochMillis),
    updatedAt = Instant.ofEpochMilli(mission.updatedAtEpochMillis),
)

fun WaypointEntity.toDomain(): Waypoint = Waypoint(
    sequence = sequence,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    action = WaypointAction.entries.find { it.name == action } ?: WaypointAction.NAVIGATE,
    holdTime = Duration.ofMillis(holdTimeMillis),
    acceptRadius = acceptRadius,
    speed = speed,
)

fun Mission.toEntity(): MissionEntity = MissionEntity(
    id = id,
    name = name,
    description = description,
    status = status.name,
    assignedDroneIds = assignedDroneIds.joinToString(","),
    createdAtEpochMillis = createdAt.toEpochMilli(),
    updatedAtEpochMillis = updatedAt.toEpochMilli(),
)

fun Waypoint.toEntity(missionId: String): WaypointEntity = WaypointEntity(
    missionId = missionId,
    sequence = sequence,
    latitude = latitude,
    longitude = longitude,
    altitude = altitude,
    action = action.name,
    holdTimeMillis = holdTime.toMillis(),
    acceptRadius = acceptRadius,
    speed = speed,
)

package com.example.tacticalcommandandcontrol.core.database.relation

import androidx.room.Embedded
import androidx.room.Relation
import com.example.tacticalcommandandcontrol.core.database.entity.MissionEntity
import com.example.tacticalcommandandcontrol.core.database.entity.WaypointEntity

data class MissionWithWaypoints(
    @Embedded
    val mission: MissionEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "missionId",
    )
    val waypoints: List<WaypointEntity>,
)

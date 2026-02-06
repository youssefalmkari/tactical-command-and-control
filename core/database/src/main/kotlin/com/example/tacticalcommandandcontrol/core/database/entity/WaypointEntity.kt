package com.example.tacticalcommandandcontrol.core.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "waypoints",
    foreignKeys = [
        ForeignKey(
            entity = MissionEntity::class,
            parentColumns = ["id"],
            childColumns = ["missionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("missionId")],
)
data class WaypointEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val missionId: String,
    val sequence: Int,
    val latitude: Double,
    val longitude: Double,
    val altitude: Double,
    val action: String,
    val holdTimeMillis: Long,
    val acceptRadius: Double,
    val speed: Double,
)

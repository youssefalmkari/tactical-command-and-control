package com.example.tacticalcommandandcontrol.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class MissionEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val status: String,
    val assignedDroneIds: String, // JSON array stored as string
    val createdAtEpochMillis: Long,
    val updatedAtEpochMillis: Long,
)

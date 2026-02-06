package com.example.tacticalcommandandcontrol.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.tacticalcommandandcontrol.core.database.converter.Converters
import com.example.tacticalcommandandcontrol.core.database.dao.DroneDao
import com.example.tacticalcommandandcontrol.core.database.dao.MissionDao
import com.example.tacticalcommandandcontrol.core.database.dao.TelemetryDao
import com.example.tacticalcommandandcontrol.core.database.dao.WaypointDao
import com.example.tacticalcommandandcontrol.core.database.entity.DroneEntity
import com.example.tacticalcommandandcontrol.core.database.entity.MissionEntity
import com.example.tacticalcommandandcontrol.core.database.entity.TelemetryEntity
import com.example.tacticalcommandandcontrol.core.database.entity.WaypointEntity

@Database(
    entities = [
        DroneEntity::class,
        MissionEntity::class,
        WaypointEntity::class,
        TelemetryEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@TypeConverters(Converters::class)
abstract class C2Database : RoomDatabase() {
    abstract fun droneDao(): DroneDao
    abstract fun missionDao(): MissionDao
    abstract fun waypointDao(): WaypointDao
    abstract fun telemetryDao(): TelemetryDao

    companion object {
        const val DATABASE_NAME = "c2_database"
    }
}

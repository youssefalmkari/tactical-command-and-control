package com.example.tacticalcommandandcontrol.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tacticalcommandandcontrol.core.database.entity.WaypointEntity

@Dao
interface WaypointDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(waypoints: List<WaypointEntity>)

    @Query("DELETE FROM waypoints WHERE missionId = :missionId")
    suspend fun deleteByMissionId(missionId: String)

    @Query("SELECT * FROM waypoints WHERE missionId = :missionId ORDER BY sequence ASC")
    suspend fun getByMissionId(missionId: String): List<WaypointEntity>
}

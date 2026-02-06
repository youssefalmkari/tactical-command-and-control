package com.example.tacticalcommandandcontrol.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Upsert
import com.example.tacticalcommandandcontrol.core.database.entity.MissionEntity
import com.example.tacticalcommandandcontrol.core.database.relation.MissionWithWaypoints
import kotlinx.coroutines.flow.Flow

@Dao
interface MissionDao {

    @Transaction
    @Query("SELECT * FROM missions ORDER BY updatedAtEpochMillis DESC")
    fun observeAll(): Flow<List<MissionWithWaypoints>>

    @Transaction
    @Query("SELECT * FROM missions WHERE id = :missionId")
    fun observeById(missionId: String): Flow<MissionWithWaypoints?>

    @Transaction
    @Query("SELECT * FROM missions WHERE id = :missionId")
    suspend fun getById(missionId: String): MissionWithWaypoints?

    @Upsert
    suspend fun upsert(mission: MissionEntity)

    @Query("DELETE FROM missions WHERE id = :missionId")
    suspend fun deleteById(missionId: String)

    @Query("DELETE FROM missions")
    suspend fun deleteAll()
}

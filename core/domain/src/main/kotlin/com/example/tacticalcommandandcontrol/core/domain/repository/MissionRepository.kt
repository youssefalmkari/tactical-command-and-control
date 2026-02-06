package com.example.tacticalcommandandcontrol.core.domain.repository

import com.example.tacticalcommandandcontrol.core.domain.model.Mission
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    fun observeMissions(): Flow<List<Mission>>
    fun observeMission(missionId: String): Flow<Mission?>
    suspend fun createMission(mission: Mission): String
    suspend fun updateMission(mission: Mission)
    suspend fun deleteMission(missionId: String)
    suspend fun syncMissions(): Result<Unit>
}

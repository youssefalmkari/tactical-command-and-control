package com.example.tacticalcommandandcontrol.core.data.repository

import com.example.tacticalcommandandcontrol.core.data.mapper.toDomain
import com.example.tacticalcommandandcontrol.core.data.mapper.toEntity
import com.example.tacticalcommandandcontrol.core.database.dao.MissionDao
import com.example.tacticalcommandandcontrol.core.database.dao.WaypointDao
import com.example.tacticalcommandandcontrol.core.domain.model.Mission
import com.example.tacticalcommandandcontrol.core.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MissionRepositoryImpl @Inject constructor(
    private val missionDao: MissionDao,
    private val waypointDao: WaypointDao,
) : MissionRepository {

    override fun observeMissions(): Flow<List<Mission>> =
        missionDao.observeAll().map { list ->
            list.map { it.toDomain() }
        }

    override fun observeMission(missionId: String): Flow<Mission?> =
        missionDao.observeById(missionId).map { it?.toDomain() }

    override suspend fun createMission(mission: Mission): String {
        val id = mission.id.ifBlank { UUID.randomUUID().toString() }
        val missionWithId = mission.copy(id = id)

        missionDao.upsert(missionWithId.toEntity())
        waypointDao.insertAll(
            missionWithId.waypoints.map { it.toEntity(id) }
        )

        Timber.d("Created mission: $id")
        return id
    }

    override suspend fun updateMission(mission: Mission) {
        missionDao.upsert(mission.toEntity())

        // Replace all waypoints
        waypointDao.deleteByMissionId(mission.id)
        waypointDao.insertAll(
            mission.waypoints.map { it.toEntity(mission.id) }
        )

        Timber.d("Updated mission: ${mission.id}")
    }

    override suspend fun deleteMission(missionId: String) {
        // Waypoints cascade-deleted via foreign key
        missionDao.deleteById(missionId)
        Timber.d("Deleted mission: $missionId")
    }

    override suspend fun syncMissions(): Result<Unit> {
        // Sync via MQTT will be handled in the network layer.
        // For now, local-only operation succeeds.
        return Result.success(Unit)
    }
}

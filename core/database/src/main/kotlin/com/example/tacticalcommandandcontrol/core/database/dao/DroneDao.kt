package com.example.tacticalcommandandcontrol.core.database.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.tacticalcommandandcontrol.core.database.entity.DroneEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DroneDao {

    @Query("SELECT * FROM drones ORDER BY name ASC")
    fun observeAll(): Flow<List<DroneEntity>>

    @Query("SELECT * FROM drones WHERE id = :droneId")
    fun observeById(droneId: String): Flow<DroneEntity?>

    @Query("SELECT * FROM drones WHERE id = :droneId")
    suspend fun getById(droneId: String): DroneEntity?

    @Upsert
    suspend fun upsert(drone: DroneEntity)

    @Upsert
    suspend fun upsertAll(drones: List<DroneEntity>)

    @Query("DELETE FROM drones WHERE id = :droneId")
    suspend fun deleteById(droneId: String)

    @Query("DELETE FROM drones")
    suspend fun deleteAll()
}

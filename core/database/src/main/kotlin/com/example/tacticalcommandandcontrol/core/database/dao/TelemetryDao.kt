package com.example.tacticalcommandandcontrol.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tacticalcommandandcontrol.core.database.entity.TelemetryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TelemetryDao {

    @Query(
        """
        SELECT * FROM telemetry
        WHERE droneId = :droneId
        ORDER BY timestampEpochMillis DESC
        LIMIT 1
        """
    )
    fun observeLatest(droneId: String): Flow<TelemetryEntity?>

    @Query(
        """
        SELECT t.* FROM telemetry t
        INNER JOIN (
            SELECT droneId, MAX(timestampEpochMillis) AS maxTs
            FROM telemetry
            GROUP BY droneId
        ) latest ON t.droneId = latest.droneId AND t.timestampEpochMillis = latest.maxTs
        """
    )
    fun observeAllLatest(): Flow<List<TelemetryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(telemetry: TelemetryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(telemetry: List<TelemetryEntity>)

    @Query("DELETE FROM telemetry WHERE timestampEpochMillis < :cutoffEpochMillis")
    suspend fun deleteOlderThan(cutoffEpochMillis: Long)

    @Query("SELECT COUNT(*) FROM telemetry")
    suspend fun count(): Int
}

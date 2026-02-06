package com.example.tacticalcommandandcontrol.core.domain.repository

import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import kotlinx.coroutines.flow.Flow

interface TelemetryRepository {
    fun observeTelemetry(droneId: String): Flow<TelemetrySnapshot>
    fun observeAllTelemetry(): Flow<Map<String, TelemetrySnapshot>>
}

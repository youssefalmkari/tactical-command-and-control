package com.example.tacticalcommandandcontrol.core.domain.usecase.telemetry

import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import com.example.tacticalcommandandcontrol.core.domain.repository.TelemetryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAllTelemetryUseCase @Inject constructor(
    private val telemetryRepository: TelemetryRepository,
) {
    operator fun invoke(): Flow<Map<String, TelemetrySnapshot>> =
        telemetryRepository.observeAllTelemetry()
}

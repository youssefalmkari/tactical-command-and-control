package com.example.tacticalcommandandcontrol.core.domain.usecase.telemetry

import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import com.example.tacticalcommandandcontrol.core.domain.repository.TelemetryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveTelemetryStreamUseCase @Inject constructor(
    private val telemetryRepository: TelemetryRepository,
) {
    operator fun invoke(droneId: String): Flow<TelemetrySnapshot> =
        telemetryRepository.observeTelemetry(droneId)
}

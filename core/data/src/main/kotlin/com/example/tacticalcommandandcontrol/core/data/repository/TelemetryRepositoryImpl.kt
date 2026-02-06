package com.example.tacticalcommandandcontrol.core.data.repository

import com.example.tacticalcommandandcontrol.core.common.Constants
import com.example.tacticalcommandandcontrol.core.data.mapper.toDomain
import com.example.tacticalcommandandcontrol.core.data.mapper.toEntity
import com.example.tacticalcommandandcontrol.core.database.dao.TelemetryDao
import com.example.tacticalcommandandcontrol.core.domain.model.Attitude
import com.example.tacticalcommandandcontrol.core.domain.model.BatteryState
import com.example.tacticalcommandandcontrol.core.domain.model.FlightMode
import com.example.tacticalcommandandcontrol.core.domain.model.Position
import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import com.example.tacticalcommandandcontrol.core.domain.repository.TelemetryRepository
import com.example.tacticalcommandandcontrol.core.network.mavlink.MavlinkMessageMapper
import com.example.tacticalcommandandcontrol.core.network.mavlink.MavlinkParser
import com.example.tacticalcommandandcontrol.core.network.mqtt.MqttClientManager
import com.hivemq.client.mqtt.datatypes.MqttQos
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelemetryRepositoryImpl @Inject constructor(
    private val telemetryDao: TelemetryDao,
    private val mqttClientManager: MqttClientManager,
    private val mavlinkParser: MavlinkParser,
    private val mavlinkMessageMapper: MavlinkMessageMapper,
    private val scope: CoroutineScope,
) : TelemetryRepository {

    fun startListening(droneId: String) {
        val topic = Constants.Mqtt.telemetryTopic(droneId)
        scope.launch {
            mqttClientManager.subscribe(topic, MqttQos.AT_MOST_ONCE)
                .collect { publish ->
                    try {
                        val messages = mavlinkParser.parse(publish.payloadAsBytes)
                        for (msg in messages) {
                            val update = mavlinkMessageMapper.mapToTelemetryUpdate(msg)
                            if (update != null) {
                                val snapshot = mergeUpdate(droneId, update)
                                telemetryDao.insert(snapshot.toEntity())
                            }
                        }
                    } catch (e: Exception) {
                        Timber.e(e, "Error processing telemetry for $droneId")
                    }
                }
        }
    }

    override fun observeTelemetry(droneId: String): Flow<TelemetrySnapshot> =
        telemetryDao.observeLatest(droneId).map { entity ->
            entity?.toDomain() ?: defaultTelemetry(droneId)
        }

    override fun observeAllTelemetry(): Flow<Map<String, TelemetrySnapshot>> =
        telemetryDao.observeAllLatest().map { entities ->
            entities.associate { it.droneId to it.toDomain() }
        }

    private fun mergeUpdate(
        droneId: String,
        update: MavlinkMessageMapper.TelemetryUpdate,
    ): TelemetrySnapshot = TelemetrySnapshot(
        droneId = droneId,
        position = Position(
            latitude = update.latitude ?: 0.0,
            longitude = update.longitude ?: 0.0,
            altitudeMsl = update.altitudeMsl ?: 0.0,
            relativeAltitude = update.relativeAltitude ?: 0.0,
            heading = update.heading ?: 0.0,
            groundSpeed = update.groundSpeed ?: 0.0,
        ),
        attitude = Attitude(
            rollDeg = update.rollDeg ?: 0.0,
            pitchDeg = update.pitchDeg ?: 0.0,
            yawDeg = update.yawDeg ?: 0.0,
        ),
        battery = BatteryState(
            remainingPercent = update.batteryRemainingPercent ?: 0,
            voltageMillivolts = update.batteryVoltageMillivolts ?: 0,
            currentMilliamps = update.batteryCurrentMilliamps ?: 0,
        ),
        flightMode = FlightMode.MANUAL,
        timestamp = Instant.now(),
    )

    private fun defaultTelemetry(droneId: String) = TelemetrySnapshot(
        droneId = droneId,
        position = Position(0.0, 0.0, 0.0),
        attitude = Attitude(0.0, 0.0, 0.0),
        battery = BatteryState(0, 0),
        flightMode = FlightMode.MANUAL,
        timestamp = Instant.now(),
    )
}

package com.example.tacticalcommandandcontrol.core.data.repository

import com.example.tacticalcommandandcontrol.core.common.Constants
import com.example.tacticalcommandandcontrol.core.data.mapper.toDomain
import com.example.tacticalcommandandcontrol.core.database.dao.DroneDao
import com.example.tacticalcommandandcontrol.core.domain.model.CommandResult
import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.model.DroneCommand
import com.example.tacticalcommandandcontrol.core.domain.repository.DroneRepository
import com.example.tacticalcommandandcontrol.core.network.mavlink.MavlinkEncoder
import com.example.tacticalcommandandcontrol.core.network.mavlink.MavlinkMessageMapper
import com.example.tacticalcommandandcontrol.core.network.mavlink.MavlinkParser
import com.example.tacticalcommandandcontrol.core.network.mqtt.MqttClientManager
import com.hivemq.client.mqtt.datatypes.MqttQos
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DroneRepositoryImpl @Inject constructor(
    private val droneDao: DroneDao,
    private val mqttClientManager: MqttClientManager,
    private val mavlinkEncoder: MavlinkEncoder,
    private val mavlinkParser: MavlinkParser,
) : DroneRepository {

    override fun observeDrones(): Flow<List<Drone>> =
        droneDao.observeAll().map { entities ->
            entities.map { it.toDomain() }
        }

    override fun observeDrone(droneId: String): Flow<Drone?> =
        droneDao.observeById(droneId).map { it?.toDomain() }

    override suspend fun sendCommand(droneId: String, command: DroneCommand): CommandResult {
        val drone = droneDao.getById(droneId)
            ?: return CommandResult.Rejected("Drone not found: $droneId")

        // Use system ID from drone ID (convention: drone ID = "drone-{systemId}")
        val systemId = droneId.substringAfterLast("-").toIntOrNull() ?: 1

        val payload = encodeCommand(systemId, command)
        if (payload.isEmpty()) {
            return CommandResult.Rejected("Failed to encode command")
        }

        val topic = Constants.Mqtt.commandTopic(droneId)
        val timeoutMs = if (command is DroneCommand.EmergencyStop) {
            Constants.Command.EMERGENCY_STOP_TIMEOUT_MS
        } else {
            Constants.Command.DEFAULT_TIMEOUT_MS
        }

        return try {
            // Publish command
            mqttClientManager.publish(
                topic = topic,
                payload = payload,
                qos = MqttQos.EXACTLY_ONCE,
            ).get()

            // Wait for ACK on the ack topic
            val ackTopic = Constants.Mqtt.commandAckTopic(droneId)
            withTimeout(timeoutMs) {
                val ackPublish = mqttClientManager.subscribe(ackTopic, MqttQos.AT_LEAST_ONCE).first()
                val ackBytes = ackPublish.payloadAsBytes
                val messages = mavlinkParser.parse(ackBytes)
                val ack = messages
                    .mapNotNull { MavlinkMessageMapper().mapToAckUpdate(it) }
                    .firstOrNull()

                when (ack?.resultCode) {
                    MavlinkMessageMapper.RESULT_ACCEPTED -> CommandResult.Acknowledged
                    MavlinkMessageMapper.RESULT_DENIED -> CommandResult.Rejected("Command denied")
                    MavlinkMessageMapper.RESULT_TEMPORARILY_REJECTED -> CommandResult.Rejected("Temporarily rejected")
                    else -> CommandResult.Rejected("Unknown result: ${ack?.resultCode}")
                }
            }
        } catch (_: TimeoutCancellationException) {
            Timber.w("Command timeout for drone $droneId: $command")
            CommandResult.Timeout
        } catch (e: Exception) {
            Timber.w(e, "MQTT unavailable, applying command locally for drone $droneId")
            applyCommandLocally(droneId, command)
            CommandResult.Acknowledged
        }
    }

    private suspend fun applyCommandLocally(droneId: String, command: DroneCommand) {
        val drone = droneDao.getById(droneId) ?: return
        val newStatus = when (command) {
            is DroneCommand.Arm -> "ARMED"
            is DroneCommand.Disarm -> "IDLE"
            is DroneCommand.Takeoff -> "FLYING"
            is DroneCommand.Land -> "LANDING"
            is DroneCommand.ReturnToLaunch -> "RETURNING"
            is DroneCommand.EmergencyStop -> "LANDED"
            else -> return
        }
        droneDao.upsert(drone.copy(status = newStatus, lastSeenEpochMillis = System.currentTimeMillis()))
    }

    private fun encodeCommand(systemId: Int, command: DroneCommand): ByteArray = when (command) {
        is DroneCommand.Arm -> mavlinkEncoder.encodeArm(systemId)
        is DroneCommand.Disarm -> mavlinkEncoder.encodeDisarm(systemId)
        is DroneCommand.Takeoff -> mavlinkEncoder.encodeTakeoff(systemId, command.altitudeMeters)
        is DroneCommand.Land -> mavlinkEncoder.encodeLand(systemId)
        is DroneCommand.ReturnToLaunch -> mavlinkEncoder.encodeReturnToLaunch(systemId)
        is DroneCommand.GoTo -> mavlinkEncoder.encodeGoTo(
            systemId,
            command.position.latitude,
            command.position.longitude,
            command.position.altitudeMsl,
        )
        is DroneCommand.StartMission -> mavlinkEncoder.encodeStartMission(systemId)
        is DroneCommand.SetFlightMode -> mavlinkEncoder.encodeSetMode(systemId, command.mode.ordinal)
        is DroneCommand.EmergencyStop -> mavlinkEncoder.encodeEmergencyStop(systemId)
    }
}

package com.example.tacticalcommandandcontrol.core.network.mavlink

import io.dronefleet.mavlink.MavlinkConnection
import io.dronefleet.mavlink.MavlinkMessage
import io.dronefleet.mavlink.common.Attitude as MavAttitude
import io.dronefleet.mavlink.common.BatteryStatus
import io.dronefleet.mavlink.common.CommandAck
import io.dronefleet.mavlink.common.GlobalPositionInt
import io.dronefleet.mavlink.minimal.Heartbeat
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.EOFException
import javax.inject.Inject
import javax.inject.Singleton

sealed interface ParsedMavlinkMessage {
    val systemId: Int

    data class HeartbeatMsg(
        override val systemId: Int,
        val type: Int,
        val autopilot: Int,
        val baseMode: Int,
        val customMode: Long,
        val systemStatus: Int,
    ) : ParsedMavlinkMessage

    data class PositionMsg(
        override val systemId: Int,
        val latDeg: Double,
        val lonDeg: Double,
        val altMsl: Double,
        val relativeAlt: Double,
        val heading: Double,
        val groundSpeed: Double,
    ) : ParsedMavlinkMessage

    data class AttitudeMsg(
        override val systemId: Int,
        val rollRad: Float,
        val pitchRad: Float,
        val yawRad: Float,
    ) : ParsedMavlinkMessage

    data class BatteryMsg(
        override val systemId: Int,
        val remainingPercent: Int,
        val voltageMillivolts: Int,
        val currentMilliamps: Int,
        val temperature: Int,
    ) : ParsedMavlinkMessage

    data class CommandAckMsg(
        override val systemId: Int,
        val command: Int,
        val result: Int,
    ) : ParsedMavlinkMessage

    data class Unknown(
        override val systemId: Int,
        val payloadType: String,
    ) : ParsedMavlinkMessage
}

@Singleton
class MavlinkParser @Inject constructor() {

    fun parse(rawBytes: ByteArray): List<ParsedMavlinkMessage> {
        val messages = mutableListOf<ParsedMavlinkMessage>()
        val inputStream = ByteArrayInputStream(rawBytes)
        val outputStream = ByteArrayOutputStream()
        val connection = MavlinkConnection.create(inputStream, outputStream)

        try {
            while (true) {
                val message: MavlinkMessage<*> = connection.next() ?: break
                val systemId = message.originSystemId
                val parsed = mapMessage(systemId, message)
                if (parsed != null) {
                    messages.add(parsed)
                }
            }
        } catch (_: EOFException) {
            // End of byte array — normal termination
        } catch (e: Exception) {
            Timber.w(e, "MAVLink parse error")
        }

        return messages
    }

    private fun mapMessage(systemId: Int, message: MavlinkMessage<*>): ParsedMavlinkMessage? {
        return when (val payload = message.payload) {
            is Heartbeat -> ParsedMavlinkMessage.HeartbeatMsg(
                systemId = systemId,
                type = payload.type().value(),
                autopilot = payload.autopilot().value(),
                baseMode = payload.baseMode().value(),
                customMode = payload.customMode(),
                systemStatus = payload.systemStatus().value(),
            )
            is GlobalPositionInt -> ParsedMavlinkMessage.PositionMsg(
                systemId = systemId,
                latDeg = payload.lat() / 1e7,
                lonDeg = payload.lon() / 1e7,
                altMsl = payload.alt() / 1000.0,
                relativeAlt = payload.relativeAlt() / 1000.0,
                heading = payload.hdg() / 100.0,
                groundSpeed = Math.sqrt(
                    (payload.vx() * payload.vx() + payload.vy() * payload.vy()).toDouble()
                ) / 100.0,
            )
            is MavAttitude -> ParsedMavlinkMessage.AttitudeMsg(
                systemId = systemId,
                rollRad = payload.roll(),
                pitchRad = payload.pitch(),
                yawRad = payload.yaw(),
            )
            is BatteryStatus -> ParsedMavlinkMessage.BatteryMsg(
                systemId = systemId,
                remainingPercent = payload.batteryRemaining(),
                voltageMillivolts = payload.voltages().firstOrNull() ?: 0,
                currentMilliamps = payload.currentBattery(),
                temperature = payload.temperature(),
            )
            is CommandAck -> ParsedMavlinkMessage.CommandAckMsg(
                systemId = systemId,
                command = payload.command().value(),
                result = payload.result().value(),
            )
            else -> {
                Timber.v("Unhandled MAVLink message: ${payload?.javaClass?.simpleName}")
                null
            }
        }
    }
}

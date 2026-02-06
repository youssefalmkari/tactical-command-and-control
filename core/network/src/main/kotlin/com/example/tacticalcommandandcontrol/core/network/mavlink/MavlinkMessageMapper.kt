package com.example.tacticalcommandandcontrol.core.network.mavlink

import com.example.tacticalcommandandcontrol.core.network.mavlink.ParsedMavlinkMessage.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Maps parsed MAVLink messages into structured data that the data layer
 * can convert into domain models. Keeps network-layer types from leaking
 * into the domain.
 */
@Singleton
class MavlinkMessageMapper @Inject constructor() {

    data class TelemetryUpdate(
        val droneSystemId: Int,
        val latitude: Double? = null,
        val longitude: Double? = null,
        val altitudeMsl: Double? = null,
        val relativeAltitude: Double? = null,
        val heading: Double? = null,
        val groundSpeed: Double? = null,
        val rollDeg: Double? = null,
        val pitchDeg: Double? = null,
        val yawDeg: Double? = null,
        val batteryRemainingPercent: Int? = null,
        val batteryVoltageMillivolts: Int? = null,
        val batteryCurrentMilliamps: Int? = null,
        val batteryTemperature: Int? = null,
        val isArmed: Boolean? = null,
        val flightModeCustom: Long? = null,
        val systemStatus: Int? = null,
    )

    data class AckUpdate(
        val droneSystemId: Int,
        val commandId: Int,
        val resultCode: Int,
    )

    fun mapToTelemetryUpdate(message: ParsedMavlinkMessage): TelemetryUpdate? = when (message) {
        is HeartbeatMsg -> TelemetryUpdate(
            droneSystemId = message.systemId,
            isArmed = (message.baseMode and 0x80) != 0, // MAV_MODE_FLAG_SAFETY_ARMED
            flightModeCustom = message.customMode,
            systemStatus = message.systemStatus,
        )
        is PositionMsg -> TelemetryUpdate(
            droneSystemId = message.systemId,
            latitude = message.latDeg,
            longitude = message.lonDeg,
            altitudeMsl = message.altMsl,
            relativeAltitude = message.relativeAlt,
            heading = message.heading,
            groundSpeed = message.groundSpeed,
        )
        is AttitudeMsg -> TelemetryUpdate(
            droneSystemId = message.systemId,
            rollDeg = Math.toDegrees(message.rollRad.toDouble()),
            pitchDeg = Math.toDegrees(message.pitchRad.toDouble()),
            yawDeg = Math.toDegrees(message.yawRad.toDouble()),
        )
        is BatteryMsg -> TelemetryUpdate(
            droneSystemId = message.systemId,
            batteryRemainingPercent = message.remainingPercent,
            batteryVoltageMillivolts = message.voltageMillivolts,
            batteryCurrentMilliamps = message.currentMilliamps,
            batteryTemperature = message.temperature,
        )
        is CommandAckMsg, is Unknown -> null
    }

    fun mapToAckUpdate(message: ParsedMavlinkMessage): AckUpdate? = when (message) {
        is CommandAckMsg -> AckUpdate(
            droneSystemId = message.systemId,
            commandId = message.command,
            resultCode = message.result,
        )
        else -> null
    }

    companion object {
        /** MAV_RESULT_ACCEPTED */
        const val RESULT_ACCEPTED = 0
        /** MAV_RESULT_DENIED */
        const val RESULT_DENIED = 1
        /** MAV_RESULT_TEMPORARILY_REJECTED */
        const val RESULT_TEMPORARILY_REJECTED = 4
        /** MAV_RESULT_IN_PROGRESS */
        const val RESULT_IN_PROGRESS = 5
    }
}

package com.example.tacticalcommandandcontrol.core.network.mavlink

import io.dronefleet.mavlink.MavlinkConnection
import io.dronefleet.mavlink.common.CommandLong
import io.dronefleet.mavlink.common.MavCmd
import timber.log.Timber
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MavlinkEncoder @Inject constructor(
    private val signer: MavlinkSigner,
) {

    companion object {
        private const val GCS_SYSTEM_ID = 255
        private const val GCS_COMPONENT_ID = 190
        private const val TARGET_COMPONENT_AUTOPILOT = 1
    }

    fun encodeArm(targetSystemId: Int, force: Boolean = false): ByteArray =
        encodeCommandLong(
            targetSystemId = targetSystemId,
            command = MavCmd.MAV_CMD_COMPONENT_ARM_DISARM,
            param1 = 1f,
            param2 = if (force) 21196f else 0f,
        )

    fun encodeDisarm(targetSystemId: Int, force: Boolean = false): ByteArray =
        encodeCommandLong(
            targetSystemId = targetSystemId,
            command = MavCmd.MAV_CMD_COMPONENT_ARM_DISARM,
            param1 = 0f,
            param2 = if (force) 21196f else 0f,
        )

    fun encodeTakeoff(targetSystemId: Int, altitudeMeters: Double): ByteArray =
        encodeCommandLong(
            targetSystemId = targetSystemId,
            command = MavCmd.MAV_CMD_NAV_TAKEOFF,
            param7 = altitudeMeters.toFloat(),
        )

    fun encodeLand(targetSystemId: Int): ByteArray =
        encodeCommandLong(
            targetSystemId = targetSystemId,
            command = MavCmd.MAV_CMD_NAV_LAND,
        )

    fun encodeReturnToLaunch(targetSystemId: Int): ByteArray =
        encodeCommandLong(
            targetSystemId = targetSystemId,
            command = MavCmd.MAV_CMD_NAV_RETURN_TO_LAUNCH,
        )

    fun encodeGoTo(
        targetSystemId: Int,
        latitude: Double,
        longitude: Double,
        altitude: Double,
    ): ByteArray = encodeCommandLong(
        targetSystemId = targetSystemId,
        command = MavCmd.MAV_CMD_DO_REPOSITION,
        param5 = latitude.toFloat(),
        param6 = longitude.toFloat(),
        param7 = altitude.toFloat(),
    )

    fun encodeSetMode(targetSystemId: Int, customMode: Int): ByteArray =
        encodeCommandLong(
            targetSystemId = targetSystemId,
            command = MavCmd.MAV_CMD_DO_SET_MODE,
            param1 = 1f, // MAV_MODE_FLAG_CUSTOM_MODE_ENABLED
            param2 = customMode.toFloat(),
        )

    fun encodeEmergencyStop(targetSystemId: Int): ByteArray =
        encodeCommandLong(
            targetSystemId = targetSystemId,
            command = MavCmd.MAV_CMD_COMPONENT_ARM_DISARM,
            param1 = 0f,
            param2 = 21196f, // Force disarm magic number
        )

    fun encodeStartMission(targetSystemId: Int): ByteArray =
        encodeCommandLong(
            targetSystemId = targetSystemId,
            command = MavCmd.MAV_CMD_MISSION_START,
            param1 = 0f, // First waypoint
            param2 = 0f, // Last waypoint (0 = end)
        )

    private fun encodeCommandLong(
        targetSystemId: Int,
        command: MavCmd,
        param1: Float = 0f,
        param2: Float = 0f,
        param3: Float = 0f,
        param4: Float = 0f,
        param5: Float = 0f,
        param6: Float = 0f,
        param7: Float = 0f,
    ): ByteArray {
        val commandLong = CommandLong.builder()
            .command(command)
            .targetSystem(targetSystemId)
            .targetComponent(TARGET_COMPONENT_AUTOPILOT)
            .confirmation(0)
            .param1(param1)
            .param2(param2)
            .param3(param3)
            .param4(param4)
            .param5(param5)
            .param6(param6)
            .param7(param7)
            .build()

        val outputStream = ByteArrayOutputStream()
        val inputStream = ByteArrayInputStream(ByteArray(0))
        val connection = MavlinkConnection.create(inputStream, outputStream)

        try {
            val signingParams = signer.getSigningParams()
            if (signingParams != null) {
                connection.send2(
                    GCS_SYSTEM_ID,
                    GCS_COMPONENT_ID,
                    commandLong,
                    signingParams.linkId,
                    signingParams.timestamp,
                    signingParams.secretKey,
                )
            } else {
                connection.send2(GCS_SYSTEM_ID, GCS_COMPONENT_ID, commandLong)
            }
        } catch (e: Exception) {
            Timber.e(e, "MAVLink encode error for command: ${command.name}")
        }

        return outputStream.toByteArray()
    }
}

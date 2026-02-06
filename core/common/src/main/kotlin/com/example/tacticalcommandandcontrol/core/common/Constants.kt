package com.example.tacticalcommandandcontrol.core.common

object Constants {

    object Mqtt {
        const val DEFAULT_PORT = 8883
        const val KEEP_ALIVE_SECONDS = 30
        const val CONNECTION_TIMEOUT_SECONDS = 10
        const val RECONNECT_DELAY_MS = 5000L
        const val MAX_RECONNECT_ATTEMPTS = 10

        const val TELEMETRY_TOPIC_PREFIX = "c2/drones/"
        const val TELEMETRY_TOPIC_SUFFIX = "/telemetry"
        const val COMMAND_TOPIC_SUFFIX = "/commands"
        const val COMMAND_ACK_TOPIC_SUFFIX = "/command_ack"
        const val MISSION_TOPIC_PREFIX = "c2/missions/"
        const val MISSION_PLAN_SUFFIX = "/plan"
        const val MISSION_STATUS_SUFFIX = "/status"

        fun telemetryTopic(droneId: String): String =
            "$TELEMETRY_TOPIC_PREFIX$droneId$TELEMETRY_TOPIC_SUFFIX"

        fun commandTopic(droneId: String): String =
            "$TELEMETRY_TOPIC_PREFIX$droneId$COMMAND_TOPIC_SUFFIX"

        fun commandAckTopic(droneId: String): String =
            "$TELEMETRY_TOPIC_PREFIX$droneId$COMMAND_ACK_TOPIC_SUFFIX"

        fun missionPlanTopic(missionId: String): String =
            "$MISSION_TOPIC_PREFIX$missionId$MISSION_PLAN_SUFFIX"

        fun missionStatusTopic(missionId: String): String =
            "$MISSION_TOPIC_PREFIX$missionId$MISSION_STATUS_SUFFIX"
    }

    object Telemetry {
        const val STALE_THRESHOLD_SECONDS = 10L
        const val CRITICAL_BATTERY_PERCENT = 15
        const val LOW_BATTERY_PERCENT = 30
        const val CLEANUP_RETENTION_HOURS = 24L
    }

    object Command {
        const val DEFAULT_TIMEOUT_MS = 10_000L
        const val EMERGENCY_STOP_TIMEOUT_MS = 5_000L
        const val MAX_RETRY_ATTEMPTS = 3
    }

    object Mission {
        const val MAX_WAYPOINTS = 100
        const val MIN_WAYPOINT_ALTITUDE_M = 5.0
        const val MAX_WAYPOINT_ALTITUDE_M = 500.0
        const val DEFAULT_WAYPOINT_ACCEPT_RADIUS_M = 5.0
    }
}

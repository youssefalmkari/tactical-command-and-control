package com.example.tacticalcommandandcontrol.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.tacticalcommandandcontrol.core.domain.model.DroneStatus
import com.example.tacticalcommandandcontrol.core.domain.model.MissionStatus

@Composable
fun DroneStatus.toColor(): Color = when (this) {
    DroneStatus.UNKNOWN -> DroneUnknownColor
    DroneStatus.IDLE -> DroneIdleColor
    DroneStatus.ARMED -> DroneArmedColor
    DroneStatus.FLYING -> DroneFlyingColor
    DroneStatus.RETURNING -> DroneReturningColor
    DroneStatus.LANDING -> DroneLandingColor
    DroneStatus.LANDED -> DroneLandedColor
    DroneStatus.LOST_LINK -> DroneLostLinkColor
}

fun DroneStatus.toDisplayLabel(): String = when (this) {
    DroneStatus.UNKNOWN -> "Unknown"
    DroneStatus.IDLE -> "Idle"
    DroneStatus.ARMED -> "Armed"
    DroneStatus.FLYING -> "Flying"
    DroneStatus.RETURNING -> "Returning"
    DroneStatus.LANDING -> "Landing"
    DroneStatus.LANDED -> "Landed"
    DroneStatus.LOST_LINK -> "Lost Link"
}

@Composable
fun MissionStatus.toColor(): Color = when (this) {
    MissionStatus.DRAFT -> MissionDraftColor
    MissionStatus.PLANNED -> MissionPlannedColor
    MissionStatus.UPLOADED -> MissionUploadedColor
    MissionStatus.EXECUTING -> MissionExecutingColor
    MissionStatus.PAUSED -> MissionPausedColor
    MissionStatus.COMPLETED -> MissionCompletedColor
    MissionStatus.ABORTED -> MissionAbortedColor
}

fun MissionStatus.toDisplayLabel(): String = when (this) {
    MissionStatus.DRAFT -> "Draft"
    MissionStatus.PLANNED -> "Planned"
    MissionStatus.UPLOADED -> "Uploaded"
    MissionStatus.EXECUTING -> "Executing"
    MissionStatus.PAUSED -> "Paused"
    MissionStatus.COMPLETED -> "Completed"
    MissionStatus.ABORTED -> "Aborted"
}

fun batteryColor(remainingPercent: Int): Color = when {
    remainingPercent <= 15 -> BatteryCriticalColor
    remainingPercent <= 30 -> BatteryMediumColor
    else -> BatteryFullColor
}

fun connectionColor(isConnected: Boolean): Color =
    if (isConnected) ConnectedColor else DisconnectedColor

fun dataFreshnessColor(isStale: Boolean): Color =
    if (isStale) StaleDataColor else FreshDataColor

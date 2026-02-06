package com.example.tacticalcommandandcontrol.core.ui.theme

import androidx.compose.ui.graphics.Color

// Primary - Tactical blue
val TacticalBlue80 = Color(0xFFADC6FF)
val TacticalBlue60 = Color(0xFF5B8DEF)
val TacticalBlue40 = Color(0xFF2D5DB8)
val TacticalBlue20 = Color(0xFF0D3B82)

// Secondary - Steel/Slate
val Steel80 = Color(0xFFBCC7DC)
val Steel60 = Color(0xFF7B8FAB)
val Steel40 = Color(0xFF4A5F7A)
val Steel20 = Color(0xFF263448)

// Tertiary - Amber for warnings/attention
val Amber80 = Color(0xFFFFD180)
val Amber60 = Color(0xFFFFAB40)
val Amber40 = Color(0xFFC67C00)
val Amber20 = Color(0xFF7A4C00)

// Error
val ErrorRed80 = Color(0xFFFFB4AB)
val ErrorRed40 = Color(0xFFBA1A1A)

// Surfaces - Dark tactical backgrounds
val SurfaceDarkest = Color(0xFF0A0E14)
val SurfaceDark = Color(0xFF111820)
val SurfaceMedium = Color(0xFF1A2332)
val SurfaceLight = Color(0xFF243044)

// On-surface text
val OnSurfacePrimary = Color(0xFFE2E8F0)
val OnSurfaceSecondary = Color(0xFF94A3B8)
val OnSurfaceTertiary = Color(0xFF64748B)

// --- Domain Status Colors ---

// Drone Status
val DroneIdleColor = Color(0xFF94A3B8)
val DroneArmedColor = Color(0xFFFFAB40)
val DroneFlyingColor = Color(0xFF4CAF50)
val DroneReturningColor = Color(0xFF42A5F5)
val DroneLandingColor = Color(0xFF7E57C2)
val DroneLandedColor = Color(0xFF78909C)
val DroneLostLinkColor = Color(0xFFEF5350)
val DroneUnknownColor = Color(0xFF616161)

// Mission Status
val MissionDraftColor = Color(0xFF78909C)
val MissionPlannedColor = Color(0xFF42A5F5)
val MissionUploadedColor = Color(0xFF5C6BC0)
val MissionExecutingColor = Color(0xFF4CAF50)
val MissionPausedColor = Color(0xFFFFAB40)
val MissionCompletedColor = Color(0xFF26A69A)
val MissionAbortedColor = Color(0xFFEF5350)

// Battery
val BatteryFullColor = Color(0xFF4CAF50)
val BatteryMediumColor = Color(0xFFFFAB40)
val BatteryCriticalColor = Color(0xFFEF5350)

// Connection
val ConnectedColor = Color(0xFF4CAF50)
val DisconnectedColor = Color(0xFFEF5350)
val ReconnectingColor = Color(0xFFFFAB40)

// Stale data
val FreshDataColor = Color(0xFF4CAF50)
val StaleDataColor = Color(0xFFEF5350)

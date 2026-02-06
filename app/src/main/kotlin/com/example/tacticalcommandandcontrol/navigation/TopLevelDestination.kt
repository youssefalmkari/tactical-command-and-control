package com.example.tacticalcommandandcontrol.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Star
import androidx.compose.ui.graphics.vector.ImageVector

enum class TopLevelDestination(
    val label: String,
    val icon: ImageVector,
    val route: String,
) {
    MISSION_PLANNING(
        label = "Missions",
        icon = Icons.AutoMirrored.Filled.List,
        route = "mission_planning",
    ),
    LIVE_OPS(
        label = "Live Ops",
        icon = Icons.Default.Place,
        route = "live_ops",
    ),
    DRONE_CONTROL(
        label = "Control",
        icon = Icons.Default.Star,
        route = "drone_control",
    ),
}

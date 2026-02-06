package com.example.tacticalcommandandcontrol.feature.dronecontrol.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.tacticalcommandandcontrol.feature.dronecontrol.DroneControlScreen

const val DRONE_CONTROL_ROUTE = "drone_control"

fun NavGraphBuilder.droneControlScreen() {
    composable(DRONE_CONTROL_ROUTE) {
        DroneControlScreen()
    }
}

fun NavController.navigateToDroneControl() {
    navigate(DRONE_CONTROL_ROUTE)
}

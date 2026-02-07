package com.example.tacticalcommandandcontrol.feature.dronecontrol.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.tacticalcommandandcontrol.feature.dronecontrol.DroneControlScreen

const val DRONE_CONTROL_ROUTE = "drone_control"
const val DRONE_ID_ARG = "droneId"

fun NavGraphBuilder.droneControlScreen() {
    composable(
        route = "$DRONE_CONTROL_ROUTE?$DRONE_ID_ARG={$DRONE_ID_ARG}",
        arguments = listOf(
            navArgument(DRONE_ID_ARG) {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) {
        DroneControlScreen()
    }
}

fun NavController.navigateToDroneControl(droneId: String? = null) {
    val route = if (droneId != null) {
        "$DRONE_CONTROL_ROUTE?$DRONE_ID_ARG=$droneId"
    } else {
        DRONE_CONTROL_ROUTE
    }
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = droneId == null
    }
}

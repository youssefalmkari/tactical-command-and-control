package com.example.tacticalcommandandcontrol.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun C2NavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = TopLevelDestination.LIVE_OPS.route,
        modifier = modifier,
    ) {
        composable(TopLevelDestination.MISSION_PLANNING.route) {
            MissionPlanningPlaceholder()
        }
        composable(TopLevelDestination.LIVE_OPS.route) {
            LiveOpsPlaceholder()
        }
        composable(TopLevelDestination.DRONE_CONTROL.route) {
            DroneControlPlaceholder()
        }
    }
}

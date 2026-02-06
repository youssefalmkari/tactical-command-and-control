package com.example.tacticalcommandandcontrol.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.example.tacticalcommandandcontrol.feature.dronecontrol.navigation.droneControlScreen
import com.example.tacticalcommandandcontrol.feature.dronecontrol.navigation.navigateToDroneControl
import com.example.tacticalcommandandcontrol.feature.liveops.navigation.liveOpsScreen
import com.example.tacticalcommandandcontrol.feature.missionplanning.navigation.missionPlanningScreen

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
        missionPlanningScreen()
        liveOpsScreen(
            onDroneClick = { navController.navigateToDroneControl() },
        )
        droneControlScreen()
    }
}

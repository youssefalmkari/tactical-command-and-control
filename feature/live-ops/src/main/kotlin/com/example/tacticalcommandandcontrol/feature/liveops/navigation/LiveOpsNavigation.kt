package com.example.tacticalcommandandcontrol.feature.liveops.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.tacticalcommandandcontrol.feature.liveops.LiveOpsScreen

const val LIVE_OPS_ROUTE = "live_ops"

fun NavGraphBuilder.liveOpsScreen(
    onDroneClick: (String) -> Unit = {},
) {
    composable(LIVE_OPS_ROUTE) {
        LiveOpsScreen(onDroneClick = onDroneClick)
    }
}

fun NavController.navigateToLiveOps() {
    navigate(LIVE_OPS_ROUTE)
}

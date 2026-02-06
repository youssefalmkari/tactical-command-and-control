package com.example.tacticalcommandandcontrol.feature.missionplanning.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.tacticalcommandandcontrol.feature.missionplanning.MissionPlanningScreen

const val MISSION_PLANNING_ROUTE = "mission_planning"

fun NavGraphBuilder.missionPlanningScreen(
    onMissionClick: (String) -> Unit = {},
) {
    composable(MISSION_PLANNING_ROUTE) {
        MissionPlanningScreen(onMissionClick = onMissionClick)
    }
}

fun NavController.navigateToMissionPlanning() {
    navigate(MISSION_PLANNING_ROUTE)
}

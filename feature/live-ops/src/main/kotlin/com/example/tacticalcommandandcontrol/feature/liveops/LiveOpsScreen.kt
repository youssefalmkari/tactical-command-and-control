package com.example.tacticalcommandandcontrol.feature.liveops

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.tacticalcommandandcontrol.core.common.util.DateTimeUtils
import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import com.example.tacticalcommandandcontrol.core.ui.component.BatteryIndicator
import com.example.tacticalcommandandcontrol.core.ui.component.ErrorView
import com.example.tacticalcommandandcontrol.core.ui.component.LoadingOverlay
import com.example.tacticalcommandandcontrol.core.ui.component.StatusIndicator
import com.example.tacticalcommandandcontrol.core.ui.component.TelemetryCard
import com.example.tacticalcommandandcontrol.core.ui.theme.toColor
import com.example.tacticalcommandandcontrol.core.ui.theme.toDisplayLabel

@Composable
fun LiveOpsScreen(
    onDroneClick: (String) -> Unit = {},
    viewModel: LiveOpsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when (val state = uiState) {
        is LiveOpsUiState.Loading -> LoadingOverlay()
        is LiveOpsUiState.Error -> ErrorView(message = state.message)
        is LiveOpsUiState.Success -> {
            if (state.drones.isEmpty()) {
                EmptyDroneList()
            } else {
                LiveOpsDashboard(
                    drones = state.drones,
                    telemetry = state.telemetry,
                    onDroneClick = onDroneClick,
                )
            }
        }
    }
}

@Composable
private fun LiveOpsDashboard(
    drones: List<Drone>,
    telemetry: Map<String, TelemetrySnapshot>,
    onDroneClick: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.fillMaxSize()) {
        // Left panel: Drone list
        LazyColumn(
            modifier = Modifier
                .weight(0.4f)
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(drones, key = { it.id }) { drone ->
                DroneStatusCard(
                    drone = drone,
                    onClick = { onDroneClick(drone.id) },
                )
            }
        }

        // Right panel: Telemetry detail for selected/first drone
        Column(
            modifier = Modifier
                .weight(0.6f)
                .fillMaxHeight()
                .padding(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "Telemetry Feed",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )

            if (telemetry.isEmpty()) {
                Text(
                    text = "No telemetry data available",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(
                        telemetry.entries.toList(),
                        key = { it.key },
                    ) { (droneId, snapshot) ->
                        val isStale = DateTimeUtils.isStale(snapshot.timestamp)
                        Column {
                            Text(
                                text = "Drone: $droneId",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(bottom = 4.dp),
                            )
                            TelemetryCard(
                                telemetry = snapshot,
                                isStale = isStale,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DroneStatusCard(
    drone: Drone,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = drone.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                StatusIndicator(
                    color = drone.status.toColor(),
                    label = drone.status.toDisplayLabel(),
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = drone.type,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                drone.battery?.let {
                    BatteryIndicator(remainingPercent = it.remainingPercent)
                }
            }
        }
    }
}

@Composable
private fun EmptyDroneList(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "No Drones Connected",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Waiting for drone connections...",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

package com.example.tacticalcommandandcontrol.feature.dronecontrol

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.tacticalcommandandcontrol.core.domain.model.CommandResult
import com.example.tacticalcommandandcontrol.core.domain.model.Drone
import com.example.tacticalcommandandcontrol.core.domain.model.DroneCommand
import com.example.tacticalcommandandcontrol.core.ui.component.BatteryIndicator
import com.example.tacticalcommandandcontrol.core.ui.component.StatusIndicator
import com.example.tacticalcommandandcontrol.core.ui.component.TelemetryCard
import com.example.tacticalcommandandcontrol.core.ui.theme.toColor
import com.example.tacticalcommandandcontrol.core.ui.theme.toDisplayLabel

@Composable
fun DroneControlScreen(
    viewModel: DroneControlViewModel = hiltViewModel(),
) {
    val drones by viewModel.dronesState.collectAsStateWithLifecycle()
    val selectedState by viewModel.selectedDroneState.collectAsStateWithLifecycle()
    val commandStatus by viewModel.commandStatus.collectAsStateWithLifecycle()

    Row(modifier = Modifier.fillMaxSize()) {
        // Left: Drone selector
        DroneSelector(
            drones = drones,
            selectedDroneId = viewModel.selectedDroneId.collectAsStateWithLifecycle().value,
            onDroneSelect = viewModel::selectDrone,
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight()
                .padding(8.dp),
        )

        // Right: Control panel
        when (val state = selectedState) {
            is DroneControlUiState.NoDroneSelected -> {
                NoDroneSelectedPanel(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight(),
                )
            }
            is DroneControlUiState.DroneSelected -> {
                ControlPanel(
                    drone = state.drone,
                    telemetry = state.telemetry,
                    commandStatus = commandStatus,
                    onCommand = viewModel::sendCommand,
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight()
                        .padding(8.dp),
                )
            }
            is DroneControlUiState.Error -> {
                Column(
                    modifier = Modifier
                        .weight(0.7f)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = state.message,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

@Composable
private fun DroneSelector(
    drones: List<Drone>,
    selectedDroneId: String?,
    onDroneSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        items(drones, key = { it.id }) { drone ->
            val isSelected = drone.id == selectedDroneId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onDroneSelect(drone.id) },
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) {
                        MaterialTheme.colorScheme.primaryContainer
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant
                    },
                ),
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = drone.name,
                        style = MaterialTheme.typography.titleSmall,
                        color = if (isSelected) {
                            MaterialTheme.colorScheme.onPrimaryContainer
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                    )
                    StatusIndicator(
                        color = drone.status.toColor(),
                        label = drone.status.toDisplayLabel(),
                        dotSize = 8.dp,
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ControlPanel(
    drone: Drone,
    telemetry: com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot,
    commandStatus: CommandStatus,
    onCommand: (DroneCommand) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        // Drone header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = drone.name,
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                StatusIndicator(
                    color = drone.status.toColor(),
                    label = drone.status.toDisplayLabel(),
                )
            }
            drone.battery?.let {
                BatteryIndicator(
                    remainingPercent = it.remainingPercent,
                    voltageMillivolts = it.voltageMillivolts,
                )
            }
        }

        // Telemetry
        TelemetryCard(
            telemetry = telemetry,
            isStale = DateTimeUtils.isStale(telemetry.timestamp),
        )

        // Command buttons
        Text(
            text = "Commands",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            CommandButton(label = "ARM", onClick = { onCommand(DroneCommand.Arm) })
            CommandButton(label = "DISARM", onClick = { onCommand(DroneCommand.Disarm) })
            CommandButton(label = "TAKEOFF", onClick = { onCommand(DroneCommand.Takeoff(10.0)) })
            CommandButton(label = "LAND", onClick = { onCommand(DroneCommand.Land) })
            CommandButton(label = "RTL", onClick = { onCommand(DroneCommand.ReturnToLaunch) })
            CommandButton(
                label = "E-STOP",
                onClick = { onCommand(DroneCommand.EmergencyStop) },
                isEmergency = true,
            )
        }

        // Command status feedback
        when (commandStatus) {
            is CommandStatus.Idle -> {}
            is CommandStatus.Sending -> {
                Text(
                    text = "Sending command...",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.tertiary,
                )
            }
            is CommandStatus.Result -> {
                val (color, text) = when (commandStatus.result) {
                    is CommandResult.Acknowledged -> MaterialTheme.colorScheme.primary to "Command acknowledged"
                    is CommandResult.Rejected -> MaterialTheme.colorScheme.error to "Rejected: ${(commandStatus.result as CommandResult.Rejected).reason}"
                    is CommandResult.Timeout -> MaterialTheme.colorScheme.error to "Command timed out"
                    is CommandResult.Queued -> MaterialTheme.colorScheme.tertiary to "Command queued (offline)"
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodySmall,
                    color = color,
                )
            }
        }
    }
}

@Composable
private fun CommandButton(
    label: String,
    onClick: () -> Unit,
    isEmergency: Boolean = false,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.height(56.dp),
        colors = if (isEmergency) {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error,
                contentColor = MaterialTheme.colorScheme.onError,
            )
        } else {
            ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        },
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
        )
    }
}

@Composable
private fun NoDroneSelectedPanel(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Select a Drone",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = "Choose a drone from the list to control",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

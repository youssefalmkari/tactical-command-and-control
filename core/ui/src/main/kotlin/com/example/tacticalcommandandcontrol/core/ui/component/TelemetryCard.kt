package com.example.tacticalcommandandcontrol.core.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.tacticalcommandandcontrol.core.common.util.CoordinateUtils
import com.example.tacticalcommandandcontrol.core.common.util.DateTimeUtils
import com.example.tacticalcommandandcontrol.core.domain.model.TelemetrySnapshot
import com.example.tacticalcommandandcontrol.core.ui.theme.TelemetryFontFamily
import com.example.tacticalcommandandcontrol.core.ui.theme.dataFreshnessColor

@Composable
fun TelemetryCard(
    telemetry: TelemetrySnapshot,
    isStale: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            // Header with timestamp freshness
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Telemetry",
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    StatusDot(
                        color = dataFreshnessColor(isStale),
                        size = 6.dp,
                    )
                    Text(
                        text = DateTimeUtils.formatElapsed(telemetry.timestamp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Position
            TelemetryRow(
                label = "POS",
                value = CoordinateUtils.formatDecimalDegrees(
                    telemetry.position.latitude,
                    telemetry.position.longitude,
                ),
            )

            // Altitude
            TelemetryRow(
                label = "ALT",
                value = CoordinateUtils.formatAltitude(telemetry.position.altitudeMsl),
            )

            // Heading / Speed
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TelemetryRow(
                    label = "HDG",
                    value = CoordinateUtils.formatHeading(telemetry.position.heading),
                    modifier = Modifier.weight(1f),
                )
                TelemetryRow(
                    label = "SPD",
                    value = CoordinateUtils.formatSpeed(telemetry.position.groundSpeed),
                    modifier = Modifier.weight(1f),
                )
            }

            // Attitude
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TelemetryRow(
                    label = "R",
                    value = String.format("%.1f\u00B0", telemetry.attitude.rollDeg),
                    modifier = Modifier.weight(1f),
                )
                TelemetryRow(
                    label = "P",
                    value = String.format("%.1f\u00B0", telemetry.attitude.pitchDeg),
                    modifier = Modifier.weight(1f),
                )
                TelemetryRow(
                    label = "Y",
                    value = String.format("%.1f\u00B0", telemetry.attitude.yawDeg),
                    modifier = Modifier.weight(1f),
                )
            }

            // Battery
            BatteryIndicator(
                remainingPercent = telemetry.battery.remainingPercent,
                voltageMillivolts = telemetry.battery.voltageMillivolts,
            )
        }
    }
}

@Composable
private fun TelemetryRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall,
            fontFamily = TelemetryFontFamily,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

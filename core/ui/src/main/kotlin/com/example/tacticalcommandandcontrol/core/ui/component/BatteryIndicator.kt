package com.example.tacticalcommandandcontrol.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.tacticalcommandandcontrol.core.ui.theme.TelemetryFontFamily
import com.example.tacticalcommandandcontrol.core.ui.theme.batteryColor

private val BAR_WIDTH = 32.dp
private val BAR_PADDING = 2.dp

@Composable
fun BatteryIndicator(
    remainingPercent: Int,
    modifier: Modifier = Modifier,
    showPercentText: Boolean = true,
    voltageMillivolts: Int? = null,
) {
    val color = batteryColor(remainingPercent)
    val fraction = (remainingPercent.coerceIn(0, 100)) / 100f
    val fillWidth = (BAR_WIDTH - BAR_PADDING * 2) * fraction

    Row(
        modifier = modifier.semantics {
            contentDescription = "Battery: $remainingPercent percent"
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        // Battery bar
        Box(
            modifier = Modifier
                .width(BAR_WIDTH)
                .height(14.dp)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline,
                    shape = MaterialTheme.shapes.extraSmall,
                )
                .padding(BAR_PADDING),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(fillWidth)
                    .clip(MaterialTheme.shapes.extraSmall)
                    .background(color),
            )
        }

        if (showPercentText) {
            Text(
                text = "$remainingPercent%",
                style = MaterialTheme.typography.labelMedium,
                fontFamily = TelemetryFontFamily,
                color = color,
            )
        }

        if (voltageMillivolts != null) {
            Text(
                text = "${voltageMillivolts / 1000.0}V",
                style = MaterialTheme.typography.labelSmall,
                fontFamily = TelemetryFontFamily,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

package com.example.tacticalcommandandcontrol.core.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.example.tacticalcommandandcontrol.core.ui.theme.ConnectedColor
import com.example.tacticalcommandandcontrol.core.ui.theme.DisconnectedColor
import com.example.tacticalcommandandcontrol.core.ui.theme.ReconnectingColor

enum class ConnectionState {
    CONNECTED,
    DISCONNECTED,
    RECONNECTING,
}

@Composable
fun ConnectionStatusBar(
    state: ConnectionState,
    modifier: Modifier = Modifier,
) {
    val backgroundColor by animateColorAsState(
        targetValue = when (state) {
            ConnectionState.CONNECTED -> ConnectedColor.copy(alpha = 0.15f)
            ConnectionState.DISCONNECTED -> DisconnectedColor.copy(alpha = 0.15f)
            ConnectionState.RECONNECTING -> ReconnectingColor.copy(alpha = 0.15f)
        },
        label = "connectionBarColor",
    )

    val dotColor = when (state) {
        ConnectionState.CONNECTED -> ConnectedColor
        ConnectionState.DISCONNECTED -> DisconnectedColor
        ConnectionState.RECONNECTING -> ReconnectingColor
    }

    val label = when (state) {
        ConnectionState.CONNECTED -> "Connected"
        ConnectionState.DISCONNECTED -> "Disconnected"
        ConnectionState.RECONNECTING -> "Reconnecting..."
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .semantics { contentDescription = "Connection status: $label" },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        StatusDot(color = dotColor, size = 8.dp)
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = dotColor,
        )
    }
}

package com.example.tacticalcommandandcontrol.core.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun StatusIndicator(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
    dotSize: Dp = 10.dp,
) {
    Row(
        modifier = modifier.semantics {
            contentDescription = "Status: $label"
        },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Box(
            modifier = Modifier
                .size(dotSize)
                .clip(CircleShape)
                .background(color),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

@Composable
fun StatusDot(
    color: Color,
    modifier: Modifier = Modifier,
    size: Dp = 10.dp,
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(color),
    )
}

@Composable
fun StatusChip(
    color: Color,
    label: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = label,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.15f),
                shape = MaterialTheme.shapes.small,
            )
            .padding(horizontal = 8.dp, vertical = 4.dp),
    )
}

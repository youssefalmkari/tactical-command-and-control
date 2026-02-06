package com.example.tacticalcommandandcontrol.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val TacticalDarkColorScheme = darkColorScheme(
    primary = TacticalBlue80,
    onPrimary = TacticalBlue20,
    primaryContainer = TacticalBlue40,
    onPrimaryContainer = TacticalBlue80,

    secondary = Steel80,
    onSecondary = Steel20,
    secondaryContainer = Steel40,
    onSecondaryContainer = Steel80,

    tertiary = Amber80,
    onTertiary = Amber20,
    tertiaryContainer = Amber40,
    onTertiaryContainer = Amber80,

    error = ErrorRed80,
    onError = ErrorRed40,
    errorContainer = ErrorRed40,
    onErrorContainer = ErrorRed80,

    background = SurfaceDarkest,
    onBackground = OnSurfacePrimary,

    surface = SurfaceDark,
    onSurface = OnSurfacePrimary,
    surfaceVariant = SurfaceMedium,
    onSurfaceVariant = OnSurfaceSecondary,

    outline = OnSurfaceTertiary,
    outlineVariant = Steel40,
)

@Composable
fun TacticalTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = TacticalDarkColorScheme,
        typography = TacticalTypography,
        shapes = TacticalShapes,
        content = content,
    )
}

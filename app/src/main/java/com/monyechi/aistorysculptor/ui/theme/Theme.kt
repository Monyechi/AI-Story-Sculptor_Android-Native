package com.monyechi.aistorysculptor.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = MediumForestGreen,
    onPrimary = White,
    primaryContainer = DarkForestGreen,
    onPrimaryContainer = White,
    secondary = DarkOlive,
    onSecondary = White,
    secondaryContainer = Beige,
    onSecondaryContainer = DarkOlive,
    tertiary = AccentGreen,
    onTertiary = White,
    background = WarmCream,
    onBackground = DarkOlive,
    surface = White,
    onSurface = DarkText,
    surfaceVariant = Beige,
    onSurfaceVariant = DarkOlive,
    error = DangerRed,
    onError = White,
    outline = OliveAccent,
)

private val DarkColors = darkColorScheme(
    primary = AccentGreen,
    onPrimary = DarkForestGreen,
    primaryContainer = MediumForestGreen,
    onPrimaryContainer = White,
    secondary = Beige,
    onSecondary = DarkOlive,
    secondaryContainer = DarkOlive,
    onSecondaryContainer = Beige,
    tertiary = YellowGreen,
    onTertiary = DarkOlive,
    background = DarkForestGreen,
    onBackground = WarmCream,
    surface = DarkOlive,
    onSurface = Beige,
    surfaceVariant = MediumForestGreen,
    onSurfaceVariant = Beige,
    error = DangerRed,
    onError = White,
    outline = OliveAccent,
)

@Composable
fun AIStorySculptorTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}

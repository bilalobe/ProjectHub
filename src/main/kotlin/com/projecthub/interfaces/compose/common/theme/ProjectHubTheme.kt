package com.projecthub.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color

// Brand colors
val ProjectHubBlue = Color(0xFF1565C0)
val ProjectHubGreen = Color(0xFF43A047)
val ProjectHubOrange = Color(0xFFFB8C00)
val ProjectHubRed = Color(0xFFE53935)

// Light theme colors
private val LightColors = lightColors(
    primary = ProjectHubBlue,
    primaryVariant = Color(0xFF0D47A1),
    secondary = ProjectHubGreen,
    secondaryVariant = Color(0xFF2E7D32),
    background = Color.White,
    surface = Color(0xFFF5F5F5),
    error = ProjectHubRed,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF212121),
    onSurface = Color(0xFF212121),
    onError = Color.White
)

// Dark theme colors
private val DarkColors = darkColors(
    primary = Color(0xFF2196F3),
    primaryVariant = ProjectHubBlue,
    secondary = Color(0xFF66BB6A),
    secondaryVariant = ProjectHubGreen,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFEF5350),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White,
    onError = Color.Black
)

/**
 * Main theme for the ProjectHub application that can be used across
 * both desktop and mobile platforms.
 */
@Composable
fun ProjectHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColors else LightColors

    MaterialTheme(
        colors = colors,
        typography = ProjectHubTypography,
        shapes = ProjectHubShapes,
        content = content
    )
}
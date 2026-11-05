package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val DarkColorScheme = darkColorScheme(
    primary = ElectricBlue,
    onPrimary = DeepNavyDark,
    primaryContainer = DeepNavySurfaceVariant,
    onPrimaryContainer = ElectricBlueLight,
    secondary = CyberViolet,
    onSecondary = Color.White,
    secondaryContainer = CyberVioletDark,
    onSecondaryContainer = CyberVioletLight,
    tertiary = NeonCyan,
    onTertiary = DeepNavyDark,
    background = DeepNavyDark,
    onBackground = TextPrimaryDark,
    surface = DeepNavySurface,
    onSurface = TextPrimaryDark,
    surfaceVariant = DeepNavySurfaceVariant,
    onSurfaceVariant = TextSecondaryDark,
    outline = GlassBorderDark,
    error = StatusError,
    onError = Color.White
)

val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0077B6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF0369A1),
    secondary = CyberViolet,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFEDE9FE),
    onSecondaryContainer = CyberVioletDark,
    tertiary = Color(0xFF0D9488),
    onTertiary = Color.White,
    background = LightBackground,
    onBackground = TextPrimaryLight,
    surface = LightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = TextSecondaryLight,
    outline = GlassBorderLight,
    error = StatusError,
    onError = Color.White
)

@Composable
fun NexusTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}

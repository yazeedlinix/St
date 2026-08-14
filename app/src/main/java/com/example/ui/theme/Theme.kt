package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ProfessionalPolishColorScheme = lightColorScheme(
    primary = IndigoPrimary,
    onPrimary = Color.White,
    primaryContainer = BlueLightBg,
    onPrimaryContainer = IndigoPrimary,
    secondary = BlueAccent,
    onSecondary = Color.White,
    secondaryContainer = BlueBadgeBg,
    onSecondaryContainer = IndigoDark,
    tertiary = ZoneSteelA,
    onTertiary = Color.White,
    background = BackgroundPolish,
    onBackground = TextPrimary,
    surface = SurfacePolish,
    onSurface = TextPrimary,
    surfaceVariant = Slate100,
    onSurfaceVariant = TextSecondary,
    outline = Slate200,
    outlineVariant = Slate300,
    error = SafetyRed,
    onError = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ProfessionalPolishColorScheme,
        typography = Typography,
        content = content
    )
}


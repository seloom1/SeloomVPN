package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = CyberBg,
    primaryContainer = Color(0xFF00384D),
    onPrimaryContainer = NeonCyanBright,
    secondary = ElectricBlue,
    onSecondary = Color.White,
    tertiary = NeonPurple,
    onTertiary = Color.White,
    background = CyberBg,
    onBackground = TextWhite,
    surface = CyberCardBg,
    onSurface = TextWhite,
    surfaceVariant = Color(0xFF0C2242),
    onSurfaceVariant = TextMuted,
    outline = NeonCyan.copy(alpha = 0.5f)
)

@Composable
fun MyApplicationTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}

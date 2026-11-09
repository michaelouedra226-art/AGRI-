package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = Color(0xFF003915),
    primaryContainer = Color(0xFF14532D),
    onPrimaryContainer = Color(0xFFBBF7D0),
    secondary = Color(0xFF86EFAC),
    onSecondary = Color(0xFF064E1F),
    secondaryContainer = Color(0xFF1E3A24),
    onSecondaryContainer = Color(0xFFD1FAE5),
    tertiary = Color(0xFFF59E0B),
    background = Color(0xFF0F1511),
    onBackground = Color(0xFFF0F5F1),
    surface = Color(0xFF18221B),
    onSurface = Color(0xFFF0F5F1),
    surfaceVariant = Color(0xFF233127),
    onSurfaceVariant = Color(0xFFCBDAD0),
    outline = Color(0xFF4A6150)
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF14532D),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1FAE5),
    onPrimaryContainer = Color(0xFF064E1F),
    secondary = Color(0xFF2E7D32),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE8F5E9),
    onSecondaryContainer = Color(0xFF14532D),
    tertiary = Color(0xFFB45309),
    background = Color(0xFFF7F5EE),
    onBackground = Color(0xFF17231D),
    surface = Color.White,
    onSurface = Color(0xFF17231D),
    surfaceVariant = Color(0xFFE8EDE8),
    onSurfaceVariant = Color(0xFF47554E),
    outline = Color(0xFFCBD5CB)
)

@Composable
fun AgriIaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

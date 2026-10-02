package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val CosmicDarkColorScheme = darkColorScheme(
    primary = CelestialGold,
    onPrimary = Color(0xFF1E1500),
    primaryContainer = Color(0xFF4A3A00),
    onPrimaryContainer = CelestialGold,
    secondary = CelestialCyan,
    onSecondary = Color(0xFF003544),
    secondaryContainer = Color(0xFF004D63),
    onSecondaryContainer = Color(0xFFBBE9FF),
    tertiary = CelestialPurple,
    onTertiary = Color.White,
    background = CosmicDeepNavy,
    onBackground = CosmicStarWhite,
    surface = CosmicSurfaceNavy,
    onSurface = CosmicStarWhite,
    surfaceVariant = CosmicSurfaceElevated,
    onSurfaceVariant = Color(0xFFD3D8E8),
    outline = CosmicBorder
)

private val CosmicLightColorScheme = lightColorScheme(
    primary = Color(0xFF8B6B00),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFECC4),
    onPrimaryContainer = Color(0xFF2B1F00),
    secondary = Color(0xFF00677F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFBBE9FF),
    onSecondaryContainer = Color(0xFF001F29),
    tertiary = Color(0xFF7524A6),
    onTertiary = Color.White,
    background = Color(0xFFF7F8FC),
    onBackground = Color(0xFF131A2E),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF131A2E),
    surfaceVariant = Color(0xFFE9ECF5),
    onSurfaceVariant = Color(0xFF434754),
    outline = Color(0xFFBAC0D4)
)

@Composable
fun AstroPredictorTheme(
    darkTheme: Boolean = true, // Default to rich cosmic dark theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> CosmicDarkColorScheme
        else -> CosmicLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

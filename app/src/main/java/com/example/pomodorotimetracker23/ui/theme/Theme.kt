package com.example.pomodorotimetracker23.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun PomodoroTimeTracker23Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    // Dynamische Farben sind ab Android 12+ verfügbar
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = PomodoroTypography, // Verwenden hier die Typografie aus Type.kt
        content = content
    )
}

private val DarkColorScheme = darkColorScheme(
    primary = PomodoroColors.Primary,
    secondary = PomodoroColors.Secondary,
    tertiary = PomodoroColors.Error
)

private val LightColorScheme = lightColorScheme(
    primary = PomodoroColors.Primary,
    secondary = PomodoroColors.Secondary,
    tertiary = PomodoroColors.Error
)
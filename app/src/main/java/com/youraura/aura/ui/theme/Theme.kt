package com.youraura.aura.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF6C63FF),
    secondary = Color(0xFF03DAC6),
    tertiary = Color(0xFFBB86FC),
    background = Color(0xFF16213E),
    surface = Color(0xFF1A1A2E),
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun AuraTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography(
            displayLarge = MaterialTheme.typography.displayLarge,
            displayMedium = MaterialTheme.typography.displayMedium,
            displaySmall = MaterialTheme.typography.displaySmall,
            headlineLarge = MaterialTheme.typography.headlineLarge,
            headlineMedium = MaterialTheme.typography.headlineMedium,
            headlineSmall = MaterialTheme.typography.headlineSmall,
            titleLarge = MaterialTheme.typography.titleLarge,
            titleMedium = MaterialTheme.typography.titleMedium,
            titleSmall = MaterialTheme.typography.titleSmall,
            bodyLarge = MaterialTheme.typography.bodyLarge,
            bodyMedium = MaterialTheme.typography.bodyMedium,
            bodySmall = MaterialTheme.typography.bodySmall,
            labelLarge = MaterialTheme.typography.labelLarge,
            labelMedium = MaterialTheme.typography.labelMedium,
            labelSmall = MaterialTheme.typography.labelSmall
        ),
        content = content
    )
}

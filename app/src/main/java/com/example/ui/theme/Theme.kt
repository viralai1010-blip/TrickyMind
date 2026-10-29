package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GameColorScheme = darkColorScheme(
    primary = CardBorderNeon,
    onPrimary = Color.White,
    secondary = ActionYellow,
    onSecondary = Color(0xFF1E1B24),
    tertiary = ActionPurple,
    background = CosmicDarkBottom,
    onBackground = Color.White,
    surface = CardBackground,
    onSurface = Color.White,
    surfaceVariant = ModalItemBg,
    onSurfaceVariant = Color.White
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = GameColorScheme,
        typography = Typography,
        content = content
    )
}

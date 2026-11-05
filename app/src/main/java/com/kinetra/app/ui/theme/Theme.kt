package com.kinetra.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

// ==========================================
// PRESERVED & ENHANCED DARK COLOR SCHEME
// ==========================================
private val DarkColorScheme = darkColorScheme(
    background = BackgroundDark,
    surface = BackgroundDark,
    primary = GlowBlueBright,
    onPrimary = TextWhite,
    onBackground = TextWhite,
    onSurface = TextWhite,

    // Extended Semantic Mappings
    secondary = ElectricCyan,
    onSecondary = BackgroundDark,
    tertiary = AthleticGreen,
    onTertiary = TextWhite,
    surfaceVariant = CardBackground,
    onSurfaceVariant = TextGray,
    outline = ButtonBorder,
    outlineVariant = CardBorderSubtle
)

@Composable
fun KinetraTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}

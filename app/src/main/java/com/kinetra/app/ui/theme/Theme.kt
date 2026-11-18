package com.kinetra.app.ui.theme

import android.app.Activity
import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.view.WindowCompat

val LocalKinetraColors = staticCompositionLocalOf {
    buildKinetraColors(AppearanceSettings(), isSystemDark = false)
}

val LocalKinetraStyle = staticCompositionLocalOf {
    VisualStyle.CLEAN_MINIMAL
}

val LocalAppearanceSettings = staticCompositionLocalOf {
    AppearanceSettings()
}

/**
 * Clean, modern athletic typography scale for KINETRA.
 */
val KinetraTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 36.sp,
        lineHeight = 42.sp,
        letterSpacing = (-0.5).sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 26.sp,
        lineHeight = 32.sp,
        letterSpacing = (-0.25).sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 15.sp,
        lineHeight = 20.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 15.sp,
        lineHeight = 22.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Normal,
        fontSize = 13.sp,
        lineHeight = 19.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.4.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.SansSerif,
        fontWeight = FontWeight.SemiBold,
        fontSize = 10.sp,
        lineHeight = 14.sp,
        letterSpacing = 1.2.sp
    )
)

/**
 * Convenience accessor for active KINETRA theme tokens.
 */
object KinetraTheme {
    val colors: KinetraColors
        @Composable get() = LocalKinetraColors.current

    val style: VisualStyle
        @Composable get() = LocalKinetraStyle.current

    val settings: AppearanceSettings
        @Composable get() = LocalAppearanceSettings.current

    val cardRadius: Dp
        @Composable get() = LocalKinetraStyle.current.cardCornerDp.dp

    val buttonRadius: Dp
        @Composable get() = LocalKinetraStyle.current.buttonCornerDp.dp

    val chipRadius: Dp
        @Composable get() = LocalKinetraStyle.current.chipCornerDp.dp

    val borderWidth: Dp
        @Composable get() = LocalKinetraStyle.current.borderWidthDp.dp

    val cardElevation: Dp
        @Composable get() = LocalKinetraStyle.current.cardElevationDp.dp
}

@Composable
fun KinetraTheme(
    overrideSettings: AppearanceSettings? = null,
    forceDarkSystemBars: Boolean = false,
    content: @Composable () -> Unit
) {
    val persistedSettings by AppearanceManager.settings.collectAsState()
    val settings = overrideSettings ?: persistedSettings
    val systemDark = isSystemInDarkTheme()

    // Synchronize the reactive token bridge immediately when settings or system theme change
    if (KinetraDynamicTokens.currentSettings != settings || KinetraDynamicTokens.isSystemDark != systemDark) {
        KinetraDynamicTokens.update(settings, systemDark)
    }

    val targetColors = remember(settings, systemDark) {
        buildKinetraColors(settings, systemDark)
    }

    val context = LocalContext.current
    val view = LocalView.current
    val reducedMotion = remember(context) {
        if (view.isInEditMode) true
        else runCatching {
            Settings.Global.getFloat(
                context.contentResolver,
                Settings.Global.ANIMATOR_DURATION_SCALE,
                1f
            ) == 0f
        }.getOrDefault(false)
    }
    val animDuration = if (reducedMotion) 0 else 220

    val animBg by animateColorAsState(targetColors.background, tween(animDuration), label = "theme_bg")
    val animSurface by animateColorAsState(targetColors.cardBackground, tween(animDuration), label = "theme_surface")
    val animPrimary by animateColorAsState(targetColors.accentPrimary, tween(animDuration), label = "theme_primary")
    val animSecondary by animateColorAsState(targetColors.accentSecondary, tween(animDuration), label = "theme_secondary")
    val animOnPrimary by animateColorAsState(targetColors.onAccent, tween(animDuration), label = "theme_on_primary")
    val animOnSurface by animateColorAsState(targetColors.textPrimary, tween(animDuration), label = "theme_on_surface")
    val animBorder by animateColorAsState(targetColors.cardBorder, tween(animDuration), label = "theme_border")

    val colorScheme = if (targetColors.isDark) {
        darkColorScheme(
            background = animBg,
            surface = animBg,
            primary = animPrimary,
            onPrimary = animOnPrimary,
            onBackground = animOnSurface,
            onSurface = animOnSurface,
            secondary = animSecondary,
            onSecondary = animOnPrimary,
            tertiary = targetColors.athleticGreen,
            onTertiary = PureWhite,
            surfaceVariant = animSurface,
            onSurfaceVariant = targetColors.textGray,
            outline = animBorder,
            outlineVariant = targetColors.cardBorderSubtle,
            error = targetColors.athleticRed
        )
    } else {
        lightColorScheme(
            background = animBg,
            surface = animBg,
            primary = animPrimary,
            onPrimary = animOnPrimary,
            onBackground = animOnSurface,
            onSurface = animOnSurface,
            secondary = animSecondary,
            onSecondary = animOnPrimary,
            tertiary = targetColors.athleticGreen,
            onTertiary = PureWhite,
            surfaceVariant = animSurface,
            onSurfaceVariant = targetColors.textGray,
            outline = animBorder,
            outlineVariant = targetColors.cardBorderSubtle,
            error = targetColors.athleticRed
        )
    }

    val style = settings.visualStyle
    val shapes = remember(style) {
        Shapes(
            small = RoundedCornerShape(style.chipCornerDp.dp),
            medium = RoundedCornerShape(style.cardCornerDp.dp),
            large = RoundedCornerShape(style.buttonCornerDp.dp)
        )
    }

    // Update Android status & navigation bar color and icon polarity for crisp Light & Dark contrast
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                val useDarkBars = forceDarkSystemBars || targetColors.isDark
                @Suppress("DEPRECATION")
                window.statusBarColor = if (forceDarkSystemBars) KinetraBlack.toArgb() else targetColors.background.toArgb()
                @Suppress("DEPRECATION")
                window.navigationBarColor = if (forceDarkSystemBars) KinetraBlack.toArgb() else targetColors.navigationBarBg.toArgb()
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !useDarkBars
                controller.isAppearanceLightNavigationBars = !useDarkBars
            }
        }
    }

    CompositionLocalProvider(
        LocalKinetraColors provides targetColors,
        LocalKinetraStyle provides style,
        LocalAppearanceSettings provides settings
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = KinetraTypography,
            shapes = shapes,
            content = content
        )
    }
}

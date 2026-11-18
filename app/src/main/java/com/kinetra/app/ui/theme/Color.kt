package com.kinetra.app.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

// ==========================================
// OFFICIAL KINETRA BRAND CONSTANTS
// ==========================================
val KinetraMaroon = Color(0xFF800020)
val KinetraMaroonDark = Color(0xFF5E0017)
val KinetraMaroonLight = Color(0xFF9E1B3B)
val KinetraMaroonDarkAccent = Color(0xFFD65A75)

val KinetraBlack = Color(0xFF0A0A0A)
val KinetraCharcoal = Color(0xFF171717)
val KinetraOffWhite = Color(0xFFF7F6F4)
val KinetraWhite = Color(0xFFFFFFFF)
val KinetraSecondaryGray = Color(0xFF777777)
val KinetraBorderLight = Color(0xFFE7E1E2)

/**
 * Centralized semantic color palette for KINETRA.
 * Built around the official Deep Maroon (#800020), Matte Black (#0A0A0A),
 * Charcoal (#171717), Off-white (#F7F6F4), and White (#FFFFFF) identity.
 */
@Immutable
data class KinetraColors(
    val isDark: Boolean,
    val background: Color,
    val surface: Color,
    val cardBackground: Color,
    val cardBackgroundElevated: Color,
    val surfaceGlass: Color,
    val navigationBarBg: Color,
    val buttonBackground: Color,
    val accentTintSurface: Color,
    val canvasGridLine: Color,

    // Borders
    val buttonBorder: Color,
    val cardBorder: Color,
    val cardBorderSubtle: Color,
    val cardBorderBright: Color,
    val cardBorderCyan: Color,

    // Primary & Secondary Brand Accents
    val accentPrimary: Color,
    val accentSecondary: Color,
    val accentText: Color,
    val accentGlow: Color,
    val accentSoft: Color,
    val onAccent: Color,
    val indicatorActive: Color,
    val indicatorInactive: Color,

    // Typography Hierarchy
    val textPrimary: Color,
    val textSecondary: Color,
    val textGray: Color,
    val textMuted: Color,
    val textDim: Color,

    // Restrained Semantic Status Colors
    val athleticGreen: Color,
    val athleticGreenDark: Color,
    val athleticAmber: Color,
    val athleticOrange: Color,
    val athleticRed: Color,
    val athleticPurple: Color
)

/**
 * Builds the complete KINETRA semantic palette for any ThemeMode, AccentPalette, and VisualStyle.
 */
fun buildKinetraColors(
    settings: AppearanceSettings,
    isSystemDark: Boolean
): KinetraColors {
    val isDark = when (settings.themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemDark
    }

    val palette = settings.accentPalette
    val style = settings.visualStyle

    val primary = if (isDark) palette.primaryDark else palette.primaryLight
    val secondary = if (isDark) palette.secondaryDark else palette.secondaryLight
    val accentText = if (isDark) palette.secondaryDark else palette.primaryLight
    val onAccent = if (isDark) palette.onPrimaryDark else palette.onPrimaryLight
    val tintContainer = if (isDark) palette.tintContainerDark else palette.tintContainerLight

    val vibrantBoost = when (style) {
        VisualStyle.ENERGETIC_VIBRANT -> 0.14f
        VisualStyle.ROUNDED_SOFT -> 0.04f
        VisualStyle.CLEAN_MINIMAL -> 0.0f
    }

    return if (!isDark) {
        // ==========================================
        // KINETRA LIGHT MODE — Off-white #F7F6F4, White cards, Charcoal #171717, Deep Maroon #800020
        // ==========================================
        val baseBg = lerp(KinetraOffWhite, tintContainer, vibrantBoost * 0.4f)
        val baseCard = KinetraWhite
        val baseBorder = lerp(
            KinetraBorderLight,
            primary,
            if (style == VisualStyle.ENERGETIC_VIBRANT) 0.18f else 0.0f
        )

        KinetraColors(
            isDark = false,
            background = baseBg,
            surface = Color(0xFFEFEDEA),
            cardBackground = baseCard,
            cardBackgroundElevated = KinetraWhite,
            surfaceGlass = Color(0xF0FFFFFF),
            navigationBarBg = KinetraWhite,
            buttonBackground = baseCard,
            accentTintSurface = tintContainer,
            canvasGridLine = Color(0xFFEBE4E5),

            buttonBorder = baseBorder,
            cardBorder = baseBorder,
            cardBorderSubtle = Color(0xFFF0EBEB),
            cardBorderBright = primary,
            cardBorderCyan = primary,

            accentPrimary = primary,
            accentSecondary = secondary,
            accentText = accentText,
            accentGlow = lerp(primary, KinetraWhite, 0.15f),
            accentSoft = tintContainer,
            onAccent = onAccent,
            indicatorActive = primary,
            indicatorInactive = KinetraBorderLight,

            textPrimary = KinetraCharcoal,
            textSecondary = Color(0xFF454545),
            textGray = KinetraSecondaryGray,
            textMuted = KinetraSecondaryGray,
            textDim = Color(0xFF666666),

            athleticGreen = Color(0xFF1F7A4C),
            athleticGreenDark = Color(0xFF165B38),
            athleticAmber = Color(0xFFB4690E),
            athleticOrange = Color(0xFFB8501A),
            athleticRed = Color(0xFFB91C1C),
            athleticPurple = primary
        )
    } else {
        // ==========================================
        // KINETRA DARK MODE — Matte Black #0A0A0A, Dark Charcoal cards, White text, Maroon highlights
        // ==========================================
        val baseBg = KinetraBlack
        val baseCard = lerp(Color(0xFF161616), tintContainer, vibrantBoost * 0.35f)
        val baseElevated = lerp(Color(0xFF1E1E1E), tintContainer, vibrantBoost * 0.30f)
        val baseBorder = lerp(
            Color(0xFF2B2526),
            primary,
            if (style == VisualStyle.ENERGETIC_VIBRANT) 0.32f else 0.08f
        )

        KinetraColors(
            isDark = true,
            background = baseBg,
            surface = Color(0xFF121212),
            cardBackground = baseCard,
            cardBackgroundElevated = baseElevated,
            surfaceGlass = Color(0xE6161616),
            navigationBarBg = Color(0xFF111111),
            buttonBackground = baseCard,
            accentTintSurface = tintContainer,
            canvasGridLine = Color(0xFF241E1F),

            buttonBorder = baseBorder,
            cardBorder = baseBorder,
            cardBorderSubtle = Color(0xFF211C1D),
            cardBorderBright = accentText,
            cardBorderCyan = accentText,

            accentPrimary = primary,
            accentSecondary = secondary,
            accentText = accentText,
            accentGlow = accentText,
            accentSoft = tintContainer,
            onAccent = onAccent,
            indicatorActive = primary,
            indicatorInactive = Color(0xFF2C2627),

            textPrimary = KinetraWhite,
            textSecondary = Color(0xFFD6D2D3),
            textGray = Color(0xFF9E9899),
            textMuted = Color(0xFF857F80),
            textDim = Color(0xFFA8A2A3),

            athleticGreen = Color(0xFF2EA067),
            athleticGreenDark = Color(0xFF1F7A4C),
            athleticAmber = Color(0xFFD98B2B),
            athleticOrange = Color(0xFFD97236),
            athleticRed = Color(0xFFDC4C4C),
            athleticPurple = accentText
        )
    }
}

/**
 * Reactive global token bridge backed by Compose snapshot state.
 * Keeps every screen synchronized with ThemeMode, AccentPalette, and VisualStyle.
 */
object KinetraDynamicTokens {
    var isSystemDark by mutableStateOf(false)
    var currentSettings by mutableStateOf(AppearanceSettings())
        private set
    var colors by mutableStateOf(buildKinetraColors(AppearanceSettings(), false))
        private set

    fun update(settings: AppearanceSettings, systemDark: Boolean) {
        isSystemDark = systemDark
        currentSettings = settings
        colors = buildKinetraColors(settings, systemDark)
    }
}

// ==========================================
// FIXED HIGH-CONTRAST UTILITY TOKENS
// ==========================================
val PureWhite = KinetraWhite
val PureDark = KinetraBlack

// ==========================================
// DYNAMIC SEMANTIC DESIGN TOKENS
// ==========================================
val BackgroundDark: Color get() = KinetraDynamicTokens.colors.background
val GlowBlue: Color get() = KinetraDynamicTokens.colors.cardBorderBright.copy(alpha = 0.35f)
val GlowBlueBright: Color get() = KinetraDynamicTokens.colors.accentText
val TextWhite: Color get() = KinetraDynamicTokens.colors.textPrimary
val TextGray: Color get() = KinetraDynamicTokens.colors.textGray
val ButtonBackground: Color get() = KinetraDynamicTokens.colors.buttonBackground
val ButtonBorder: Color get() = KinetraDynamicTokens.colors.buttonBorder
val IndicatorActive: Color get() = KinetraDynamicTokens.colors.indicatorActive
val IndicatorInactive: Color get() = KinetraDynamicTokens.colors.indicatorInactive
val OnAccent: Color get() = KinetraDynamicTokens.colors.onAccent
val AccentTintSurface: Color get() = KinetraDynamicTokens.colors.accentTintSurface
val CanvasGridLine: Color get() = KinetraDynamicTokens.colors.canvasGridLine

// Surfaces & Depth
val SurfaceDark: Color get() = KinetraDynamicTokens.colors.surface
val CardBackground: Color get() = KinetraDynamicTokens.colors.cardBackground
val CardBackgroundElevated: Color get() = KinetraDynamicTokens.colors.cardBackgroundElevated
val SurfaceGlass: Color get() = KinetraDynamicTokens.colors.surfaceGlass
val NavigationBarBg: Color get() = KinetraDynamicTokens.colors.navigationBarBg

// Borders & Strokes
val CardBorder: Color get() = KinetraDynamicTokens.colors.cardBorder
val CardBorderSubtle: Color get() = KinetraDynamicTokens.colors.cardBorderSubtle
val CardBorderBright: Color get() = KinetraDynamicTokens.colors.cardBorderBright
val CardBorderCyan: Color get() = KinetraDynamicTokens.colors.cardBorderCyan

// Dynamic Brand Accent Tokens (mapped to KINETRA Maroon palette)
val ElectricCyan: Color get() = KinetraDynamicTokens.colors.accentText
val ElectricCyanDark: Color get() = KinetraDynamicTokens.colors.accentPrimary
val ElectricCyanGlow: Color get() = KinetraDynamicTokens.colors.accentText
val ElectricBlue: Color get() = KinetraDynamicTokens.colors.accentPrimary
val ElectricBlueLight: Color get() = KinetraDynamicTokens.colors.accentGlow

// Clean Semantic Aliases
val BrandPrimary: Color get() = KinetraDynamicTokens.colors.accentPrimary
val BrandAccentText: Color get() = KinetraDynamicTokens.colors.accentText

// Athletic Semantic Status Accents
val AthleticGreen: Color get() = KinetraDynamicTokens.colors.athleticGreen
val AthleticGreenDark: Color get() = KinetraDynamicTokens.colors.athleticGreenDark
val AthleticAmber: Color get() = KinetraDynamicTokens.colors.athleticAmber
val AthleticOrange: Color get() = KinetraDynamicTokens.colors.athleticOrange
val AthleticRed: Color get() = KinetraDynamicTokens.colors.athleticRed
val AthleticPurple: Color get() = KinetraDynamicTokens.colors.athleticPurple

// Typography Hierarchy
val TextMuted: Color get() = KinetraDynamicTokens.colors.textMuted
val TextDim: Color get() = KinetraDynamicTokens.colors.textDim
val TextSecondary: Color get() = KinetraDynamicTokens.colors.textSecondary

// Brand Gradients (Maroon-first, zero blue/cyan)
val BrushElectricBlue: Brush
    get() = Brush.horizontalGradient(
        colors = listOf(
            KinetraDynamicTokens.colors.accentPrimary,
            lerp(KinetraDynamicTokens.colors.accentPrimary, KinetraMaroonLight, 0.55f)
        )
    )

val BrushCyanToGreen: Brush
    get() = Brush.horizontalGradient(
        colors = listOf(
            KinetraDynamicTokens.colors.accentPrimary,
            KinetraDynamicTokens.colors.accentSecondary
        )
    )

val BrushFlame: Brush
    get() = Brush.horizontalGradient(
        colors = listOf(AthleticOrange, AthleticAmber)
    )

val BrushDarkCard: Brush
    get() = Brush.verticalGradient(
        colors = listOf(CardBackgroundElevated, CardBackground)
    )

val BrushSoftHero: Brush
    get() = Brush.verticalGradient(
        colors = listOf(
            BrandPrimary.copy(alpha = if (KinetraDynamicTokens.colors.isDark) 0.16f else 0.08f),
            Color.Transparent
        )
    )

val BrushMidnightGlow: Brush
    get() = Brush.radialGradient(
        colors = listOf(
            BrandPrimary.copy(alpha = if (KinetraDynamicTokens.colors.isDark) 0.24f else 0.10f),
            Color.Transparent
        )
    )

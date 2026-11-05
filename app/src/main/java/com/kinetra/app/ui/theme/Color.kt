package com.kinetra.app.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// ==========================================
// PRESERVED ORIGINAL DESIGN TOKENS
// ==========================================
val BackgroundDark = Color(0xFF050811)
val GlowBlue = Color(0xFF1E3A8A)
val GlowBlueBright = Color(0xFF3B82F6)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFF9CA3AF)
val ButtonBackground = Color(0xFF0F172A)
val ButtonBorder = Color(0xFF1E3A8A)
val IndicatorActive = Color(0xFF2563EB)
val IndicatorInactive = Color(0xFF1E293B)

// ==========================================
// EXTENDED ATHLETIC PREMIUM PALETTE
// ==========================================

// Surfaces & Depth
val SurfaceDark = Color(0xFF070B14)
val CardBackground = Color(0xFF0B1220)
val CardBackgroundElevated = Color(0xFF111A2E)
val SurfaceGlass = Color(0xCC0B1220)
val NavigationBarBg = Color(0xF2080D18)

// Borders & Strokes
val CardBorder = Color(0xFF1E2D4A)
val CardBorderSubtle = Color(0xFF16233B)
val CardBorderBright = Color(0xFF2563EB)
val CardBorderCyan = Color(0xFF00E5FF)

// Electric Blue & Cyan Accents
val ElectricCyan = Color(0xFF00E5FF)
val ElectricCyanDark = Color(0xFF00B4D8)
val ElectricCyanGlow = Color(0xFF38BDF8)
val ElectricBlue = Color(0xFF2563EB)
val ElectricBlueLight = Color(0xFF60A5FA)

// Athletic Performance Accents
val AthleticGreen = Color(0xFF10B981)
val AthleticGreenDark = Color(0xFF059669)
val AthleticAmber = Color(0xFFF59E0B)
val AthleticOrange = Color(0xFFFF6B00)
val AthleticRed = Color(0xFFEF4444)
val AthleticPurple = Color(0xFF8B5CF6)

// Typography Hierarchy
val TextMuted = Color(0xFF64748B)
val TextDim = Color(0xFF94A3B8)
val TextSecondary = Color(0xFFCBD5E1)

// Athletic Gradients
val BrushElectricBlue = Brush.horizontalGradient(
    colors = listOf(ElectricBlue, ElectricCyan)
)

val BrushCyanToGreen = Brush.horizontalGradient(
    colors = listOf(ElectricCyan, AthleticGreen)
)

val BrushFlame = Brush.horizontalGradient(
    colors = listOf(AthleticOrange, AthleticAmber)
)

val BrushDarkCard = Brush.verticalGradient(
    colors = listOf(CardBackgroundElevated, CardBackground)
)

val BrushMidnightGlow = Brush.radialGradient(
    colors = listOf(
        Color(0xFF1E3A8A).copy(alpha = 0.45f),
        Color(0xFF0F172A).copy(alpha = 0.15f),
        Color.Transparent
    )
)

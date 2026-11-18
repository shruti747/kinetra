package com.kinetra.app.ui.theme

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * User-selectable appearance mode across KINETRA.
 */
enum class ThemeMode(val displayName: String, val subtitle: String) {
    LIGHT("Light", "Off-white & crisp cards"),
    DARK("Dark", "Matte black & charcoal"),
    SYSTEM("System", "Follows device setting")
}

/**
 * Curated KINETRA brand accent palettes.
 * KINETRA Maroon (#800020) is the official default identity.
 */
enum class AccentPalette(
    val displayName: String,
    val tagline: String,
    val primaryLight: Color,
    val secondaryLight: Color,
    val primaryDark: Color,
    val secondaryDark: Color,
    val onPrimaryLight: Color,
    val onPrimaryDark: Color,
    val tintContainerLight: Color,
    val tintContainerDark: Color,
    val previewSwatches: List<Color>
) {
    KINETRA_MAROON(
        displayName = "KINETRA Maroon",
        tagline = "Official deep maroon & obsidian",
        primaryLight = Color(0xFF800020),
        secondaryLight = Color(0xFF9E1B3B),
        primaryDark = Color(0xFF800020),
        secondaryDark = Color(0xFFD65A75),
        onPrimaryLight = Color(0xFFFFFFFF),
        onPrimaryDark = Color(0xFFFFFFFF),
        tintContainerLight = Color(0xFFF6ECEE),
        tintContainerDark = Color(0xFF261016),
        previewSwatches = listOf(Color(0xFF800020), Color(0xFF0A0A0A), Color(0xFF9E1B3B))
    ),
    OBSIDIAN_CRIMSON(
        displayName = "Obsidian Graphite",
        tagline = "Monochrome charcoal & subtle maroon",
        primaryLight = Color(0xFF171717),
        secondaryLight = Color(0xFF800020),
        primaryDark = Color(0xFF800020),
        secondaryDark = Color(0xFFE07A90),
        onPrimaryLight = Color(0xFFFFFFFF),
        onPrimaryDark = Color(0xFFFFFFFF),
        tintContainerLight = Color(0xFFF0EDEE),
        tintContainerDark = Color(0xFF221C1D),
        previewSwatches = listOf(Color(0xFF171717), Color(0xFF800020), Color(0xFF777777))
    ),
    FOREST_ENDURANCE(
        displayName = "Forest Endurance",
        tagline = "Deep athletic pine & muted sage",
        primaryLight = Color(0xFF1B5E3F),
        secondaryLight = Color(0xFF267852),
        primaryDark = Color(0xFF1F6E4A),
        secondaryDark = Color(0xFF4CA87C),
        onPrimaryLight = Color(0xFFFFFFFF),
        onPrimaryDark = Color(0xFFFFFFFF),
        tintContainerLight = Color(0xFFECF4F0),
        tintContainerDark = Color(0xFF12261D),
        previewSwatches = listOf(Color(0xFF1B5E3F), Color(0xFF267852), Color(0xFF4CA87C))
    ),
    WARM_BRONZE(
        displayName = "Athletic Bronze",
        tagline = "Warm bronze & refined terracotta",
        primaryLight = Color(0xFF8A4B1C),
        secondaryLight = Color(0xFFA85F27),
        primaryDark = Color(0xFF945120),
        secondaryDark = Color(0xFFD98B52),
        onPrimaryLight = Color(0xFFFFFFFF),
        onPrimaryDark = Color(0xFFFFFFFF),
        tintContainerLight = Color(0xFFF7F0E9),
        tintContainerDark = Color(0xFF261A10),
        previewSwatches = listOf(Color(0xFF8A4B1C), Color(0xFFA85F27), Color(0xFFD98B52))
    )
}

/**
 * Visual style presets that adjust corner roundness, border weight, and surface depth.
 */
enum class VisualStyle(
    val displayName: String,
    val subtitle: String,
    val cardCornerDp: Float,
    val buttonCornerDp: Float,
    val chipCornerDp: Float,
    val borderWidthDp: Float,
    val cardElevationDp: Float,
    val accentTintAlpha: Float
) {
    CLEAN_MINIMAL(
        displayName = "Clean & Minimal",
        subtitle = "Crisp geometry & quiet borders",
        cardCornerDp = 16f,
        buttonCornerDp = 26f,
        chipCornerDp = 12f,
        borderWidthDp = 1.0f,
        cardElevationDp = 0f,
        accentTintAlpha = 0.05f
    ),
    ROUNDED_SOFT(
        displayName = "Rounded & Soft",
        subtitle = "Smooth curves & gentle depth",
        cardCornerDp = 20f,
        buttonCornerDp = 28f,
        chipCornerDp = 16f,
        borderWidthDp = 1.0f,
        cardElevationDp = 2f,
        accentTintAlpha = 0.08f
    ),
    ENERGETIC_VIBRANT(
        displayName = "Bold & Sporty",
        subtitle = "Pronounced frames & rich accents",
        cardCornerDp = 14f,
        buttonCornerDp = 22f,
        chipCornerDp = 10f,
        borderWidthDp = 1.4f,
        cardElevationDp = 3f,
        accentTintAlpha = 0.12f
    )
}

data class AppearanceSettings(
    val themeMode: ThemeMode = ThemeMode.LIGHT,
    val accentPalette: AccentPalette = AccentPalette.KINETRA_MAROON,
    val visualStyle: VisualStyle = VisualStyle.CLEAN_MINIMAL
)

/**
 * Persistent preference manager for KINETRA appearance settings.
 * Restores synchronously on app startup so every screen matches immediately.
 */
object AppearanceManager {
    private const val PREFS_NAME = "kinetra_appearance_prefs"
    private const val KEY_THEME_MODE = "theme_mode"
    private const val KEY_ACCENT_PALETTE = "accent_palette"
    private const val KEY_VISUAL_STYLE = "visual_style"
    private const val KEY_BRAND_V2_MIGRATED = "brand_v2_maroon_migrated"

    private var prefs: SharedPreferences? = null

    private val _settings = MutableStateFlow(AppearanceSettings())
    val settings: StateFlow<AppearanceSettings> = _settings.asStateFlow()

    fun initialize(context: Context) {
        val sp = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sp

        val themeMode = sp.getString(KEY_THEME_MODE, null)
            ?.let { runCatching { ThemeMode.valueOf(it) }.getOrNull() }
            ?: ThemeMode.LIGHT

        // Ensure legacy pre-rebrand blue/cyan accent prefs migrate cleanly to KINETRA Maroon
        val migrated = sp.getBoolean(KEY_BRAND_V2_MIGRATED, false)
        val accentPalette = if (!migrated) {
            sp.edit()
                .putString(KEY_ACCENT_PALETTE, AccentPalette.KINETRA_MAROON.name)
                .putBoolean(KEY_BRAND_V2_MIGRATED, true)
                .apply()
            AccentPalette.KINETRA_MAROON
        } else {
            sp.getString(KEY_ACCENT_PALETTE, null)
                ?.let { runCatching { AccentPalette.valueOf(it) }.getOrNull() }
                ?: AccentPalette.KINETRA_MAROON
        }

        val visualStyle = sp.getString(KEY_VISUAL_STYLE, null)
            ?.let { runCatching { VisualStyle.valueOf(it) }.getOrNull() }
            ?: VisualStyle.CLEAN_MINIMAL

        val restored = AppearanceSettings(
            themeMode = themeMode,
            accentPalette = accentPalette,
            visualStyle = visualStyle
        )
        _settings.value = restored
        KinetraDynamicTokens.update(restored, KinetraDynamicTokens.isSystemDark)
    }

    fun setThemeMode(mode: ThemeMode) {
        val updated = _settings.value.copy(themeMode = mode)
        _settings.value = updated
        prefs?.edit()?.putString(KEY_THEME_MODE, mode.name)?.apply()
        KinetraDynamicTokens.update(updated, KinetraDynamicTokens.isSystemDark)
    }

    fun setAccentPalette(palette: AccentPalette) {
        val updated = _settings.value.copy(accentPalette = palette)
        _settings.value = updated
        prefs?.edit()
            ?.putString(KEY_ACCENT_PALETTE, palette.name)
            ?.putBoolean(KEY_BRAND_V2_MIGRATED, true)
            ?.apply()
        KinetraDynamicTokens.update(updated, KinetraDynamicTokens.isSystemDark)
    }

    fun setVisualStyle(style: VisualStyle) {
        val updated = _settings.value.copy(visualStyle = style)
        _settings.value = updated
        prefs?.edit()?.putString(KEY_VISUAL_STYLE, style.name)?.apply()
        KinetraDynamicTokens.update(updated, KinetraDynamicTokens.isSystemDark)
    }
}

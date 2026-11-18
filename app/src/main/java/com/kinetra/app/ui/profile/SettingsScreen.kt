package com.kinetra.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.data.UserSession
import com.kinetra.app.ui.components.*
import com.kinetra.app.ui.theme.*

@Composable
fun SettingsScreen(onBack: () -> Unit, onSignOut: () -> Unit) {
    val scrollState = rememberScrollState()
    val currentUser by UserSession.userProfile.collectAsState()
    val appearance by AppearanceManager.settings.collectAsState()
    var privateProfile by remember { mutableStateOf(true) }
    var locationAccess by remember { mutableStateOf(false) }

    val switchColors = SwitchDefaults.colors(
        checkedThumbColor = OnAccent,
        checkedTrackColor = BrandPrimary,
        uncheckedThumbColor = TextGray,
        uncheckedTrackColor = SurfaceDark,
        uncheckedBorderColor = ButtonBorder
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KinetraScreenHeader(
                title = "Settings",
                subtitle = "KINETRA · PREFERENCES",
                onBack = onBack
            )

            // Account Identity Summary
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    val displayName = currentUser.name.ifBlank { "KINETRA Athlete" }
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(BrandPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = displayName.take(1).uppercase(),
                            color = OnAccent,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Column {
                        Text(
                            text = displayName,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = currentUser.email.ifBlank { "Signed in" },
                            color = BrandAccentText,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // ==========================================
            // APPEARANCE & THEME CUSTOMIZATION
            // ==========================================
            SectionHeader(title = "Appearance & Theme")

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "Theme Mode",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose Light Mode, Dark Mode, or follow your device setting.",
                    color = TextGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeMode.values().forEach { mode ->
                        val isSelected = appearance.themeMode == mode
                        val icon: ImageVector = when (mode) {
                            ThemeMode.LIGHT -> Icons.Filled.LightMode
                            ThemeMode.DARK -> Icons.Filled.DarkMode
                            ThemeMode.SYSTEM -> Icons.Filled.SettingsBrightness
                        }
                        ThemeModeTile(
                            label = mode.displayName,
                            icon = icon,
                            isSelected = isSelected,
                            onClick = { AppearanceManager.setThemeMode(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = CardBorderSubtle)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Brand Accent",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "KINETRA Maroon is the official default accent palette.",
                    color = TextGray,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AccentPalette.values().forEach { palette ->
                        val isSelected = appearance.accentPalette == palette
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) AccentTintSurface else BackgroundDark)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) BrandPrimary else CardBorder,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { AppearanceManager.setAccentPalette(palette) }
                                .padding(horizontal = 12.dp, vertical = 11.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .clip(CircleShape)
                                        .background(palette.primaryLight)
                                        .border(1.dp, CardBorder, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Filled.Check,
                                            contentDescription = "Selected",
                                            tint = PureWhite,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                                Column {
                                    Text(
                                        text = palette.displayName,
                                        color = TextWhite,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                                    )
                                    Text(
                                        text = palette.tagline,
                                        color = TextGray,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            if (palette == AccentPalette.KINETRA_MAROON) {
                                KinetraBadge(
                                    text = "DEFAULT",
                                    textColor = BrandAccentText,
                                    backgroundColor = CardBackground,
                                    borderColor = CardBorder
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
                HorizontalDivider(color = CardBorderSubtle)
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Surface Style",
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VisualStyle.values().forEach { style ->
                        val isSelected = appearance.visualStyle == style
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) BrandPrimary else BackgroundDark)
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) BrandPrimary else CardBorder,
                                    shape = RoundedCornerShape(10.dp)
                                )
                                .clickable { AppearanceManager.setVisualStyle(style) }
                                .padding(horizontal = 6.dp, vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = style.displayName.substringBefore(" "),
                                color = if (isSelected) OnAccent else TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            // Privacy
            SectionHeader(title = "Privacy")
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Private profile", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "Hide route start and end locations", color = TextGray, fontSize = 12.sp)
                    }
                    Switch(
                        checked = privateProfile,
                        onCheckedChange = { privateProfile = it },
                        colors = switchColors
                    )
                }
            }

            // GPS Permissions
            SectionHeader(title = "GPS Permissions")
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Location access", color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (locationAccess) "Granted · background tracking ready" else "Not granted · background off",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                    Switch(
                        checked = locationAccess,
                        onCheckedChange = { locationAccess = it },
                        colors = switchColors
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Sign Out Action
            KinetraOutlinedButton(
                text = "Sign out",
                onClick = onSignOut,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = Icons.Filled.Logout,
                borderColor = BrandPrimary.copy(alpha = 0.45f),
                containerColor = AccentTintSurface,
                contentColor = BrandAccentText
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun ThemeModeTile(
    label: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) BrandPrimary else BackgroundDark)
            .border(
                width = 1.dp,
                color = if (isSelected) BrandPrimary else CardBorder,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isSelected) OnAccent else TextGray,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = label,
            color = if (isSelected) OnAccent else TextWhite,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true, name = "Settings - Light")
@Composable
private fun SettingsScreenLightPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.LIGHT)) {
        SettingsScreen(onBack = {}, onSignOut = {})
    }
}

@Preview(showBackground = true, name = "Settings - Dark")
@Composable
private fun SettingsScreenDarkPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.DARK)) {
        SettingsScreen(onBack = {}, onSignOut = {})
    }
}

package com.kinetra.app.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.data.UserSession
import com.kinetra.app.ui.components.KinetraBrandLockup
import com.kinetra.app.ui.components.KinetraIconButton
import com.kinetra.app.ui.main.Screen
import com.kinetra.app.ui.theme.*

/**
 * Minimal, branded side navigation drawer for KINETRA.
 */
@Composable
fun KinetraDrawerContent(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val userProfile by UserSession.userProfile.collectAsState()
    val athleteName = userProfile.name.ifBlank { "Athlete" }
    val athleteEmail = userProfile.email.ifBlank { "KINETRA Athlete" }

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(312.dp),
        color = CardBackground
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 18.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Top Brand Lockup + Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                KinetraBrandLockup(badgeSize = 34.dp, wordmarkSizeSp = 15f)
                KinetraIconButton(
                    icon = Icons.Filled.Close,
                    onClick = onCloseDrawer,
                    contentDescription = "Close Menu",
                    size = 36.dp,
                    tint = TextGray,
                    containerColor = SurfaceDark,
                    borderColor = CardBorderSubtle
                )
            }

            // Athlete Identity Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(BackgroundDark)
                    .border(1.dp, CardBorder, RoundedCornerShape(14.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(BrandPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = athleteName.take(1).uppercase(),
                        color = OnAccent,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = athleteName,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = athleteEmail,
                        color = TextGray,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }

            HorizontalDivider(color = CardBorderSubtle)

            Text(
                text = "TRAINING & PERFORMANCE",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            DrawerNavItem(
                title = "Home",
                icon = Icons.Filled.Home,
                isSelected = currentScreen == Screen.MainContent,
                onClick = {
                    onNavigate(Screen.MainContent)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                title = "Live Activity Tracking",
                icon = Icons.Filled.DirectionsRun,
                isSelected = currentScreen == Screen.LiveTracking,
                badge = "LIVE",
                badgeColor = BrandPrimary,
                onClick = {
                    onNavigate(Screen.LiveTracking)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                title = "Connected Devices",
                icon = Icons.Filled.Watch,
                isSelected = currentScreen == Screen.ConnectedDevices,
                badge = "WEAR",
                badgeColor = BrandAccentText,
                onClick = {
                    onNavigate(Screen.ConnectedDevices)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                title = "Workout History",
                icon = Icons.Filled.History,
                isSelected = currentScreen == Screen.WorkoutHistory,
                onClick = {
                    onNavigate(Screen.WorkoutHistory)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                title = "My Goals",
                icon = Icons.Filled.TrackChanges,
                isSelected = currentScreen == Screen.MyGoals,
                onClick = {
                    onNavigate(Screen.MyGoals)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                title = "Challenges",
                icon = Icons.Filled.EmojiEvents,
                isSelected = currentScreen == Screen.Challenges,
                onClick = {
                    onNavigate(Screen.Challenges)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                title = "Achievements",
                icon = Icons.Filled.MilitaryTech,
                isSelected = currentScreen == Screen.Achievements,
                onClick = {
                    onNavigate(Screen.Achievements)
                    onCloseDrawer()
                }
            )

            HorizontalDivider(color = CardBorderSubtle)

            Text(
                text = "PREFERENCES & SUPPORT",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.2.sp
            )

            DrawerNavItem(
                title = "Settings & Appearance",
                icon = Icons.Filled.Settings,
                isSelected = currentScreen == Screen.Settings,
                onClick = {
                    onNavigate(Screen.Settings)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                title = "Help & Support",
                icon = Icons.Filled.HelpOutline,
                isSelected = currentScreen == Screen.HelpSupport,
                onClick = {
                    onNavigate(Screen.HelpSupport)
                    onCloseDrawer()
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                text = "KINETRA • Sports & Fitness",
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(top = 8.dp, bottom = 6.dp)
            )
        }
    }
}

@Composable
private fun DrawerNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    badge: String? = null,
    badgeColor: Color = BrandAccentText,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) AccentTintSurface else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) BrandPrimary.copy(alpha = 0.28f) else Color.Transparent,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 11.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) BrandAccentText else TextGray,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                color = if (isSelected) BrandAccentText else TextWhite,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }

        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(badgeColor.copy(alpha = 0.14f))
                    .padding(horizontal = 7.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = if (badgeColor == BrandPrimary && KinetraTheme.colors.isDark) BrandAccentText else badgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

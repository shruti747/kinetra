package com.kinetra.app.ui.drawer

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.data.UserSession
import com.kinetra.app.ui.main.Screen
import com.kinetra.app.ui.theme.*

/**
 * Modern side navigation drawer for Kinetra.
 */
@Composable
fun KinetraDrawerContent(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    onCloseDrawer: () -> Unit
) {
    val scrollState = rememberScrollState()
    val athleteName = UserSession.userProfile.value.name

    Surface(
        modifier = Modifier
            .fillMaxHeight()
            .width(310.dp),
        color = CardBackgroundElevated
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Profile Card
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(ButtonBackground)
                    .border(1.dp, ButtonBorder, RoundedCornerShape(16.dp))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(BrushElectricBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = athleteName.take(1).uppercase(),
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = athleteName,
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "PRO ATHLETE • KINETRA",
                        color = ElectricCyan,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            HorizontalDivider(color = CardBorderSubtle)

            // Drawer Items
            Text(
                text = "MAIN NAVIGATION",
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
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
                badgeColor = AthleticGreen,
                onClick = {
                    onNavigate(Screen.LiveTracking)
                    onCloseDrawer()
                }
            )

            DrawerNavItem(
                title = "Connected Devices",
                icon = Icons.Filled.Watch,
                isSelected = currentScreen == Screen.ConnectedDevices,
                badge = "GT 4",
                badgeColor = ElectricCyan,
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
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )

            DrawerNavItem(
                title = "Settings",
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

            // Footer
            Text(
                text = "KINETRA v1.2.0 • Sports & Telemetry",
                color = TextMuted,
                fontSize = 10.sp,
                modifier = Modifier.padding(bottom = 12.dp)
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
    badgeColor: Color = ElectricCyan,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isSelected) ElectricBlue.copy(alpha = 0.2f) else Color.Transparent)
            .border(1.dp, if (isSelected) ElectricCyan else Color.Transparent, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) ElectricCyan else TextGray,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                color = if (isSelected) TextWhite else TextSecondary,
                fontSize = 14.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
            )
        }

        if (badge != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(badgeColor.copy(alpha = 0.2f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = badge,
                    color = badgeColor,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

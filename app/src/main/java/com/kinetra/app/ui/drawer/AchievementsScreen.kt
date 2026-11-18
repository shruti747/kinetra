package com.kinetra.app.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraBadge
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraScreenHeader
import com.kinetra.app.ui.theme.*

data class BadgeItem(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isUnlocked: Boolean,
    val color: Color
)

@Composable
fun AchievementsScreen(onBack: () -> Unit) {
    val badges = listOf(
        BadgeItem("First Run", "Recorded your first outdoor run", Icons.Filled.DirectionsRun, true, BrandAccentText),
        BadgeItem("5K Finisher", "Completed a continuous 5 kilometer activity", Icons.Filled.MilitaryTech, true, BrandAccentText),
        BadgeItem("10K Legend", "Pushed through a 10K endurance distance", Icons.Filled.WorkspacePremium, true, AthleticAmber),
        BadgeItem("7-Day Streak", "Maintained a 7-day consecutive workout streak", Icons.Filled.FlashOn, true, AthleticGreen),
        BadgeItem("Pace Master", "Sustained sub-5:00 min/km pace for over 3 km", Icons.Filled.Speed, false, TextMuted),
        BadgeItem("Half Marathon", "Completed a 21.1 kilometer distance", Icons.Filled.Stars, false, TextMuted)
    )

    val unlockedCount = badges.count { it.isUnlocked }

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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KinetraScreenHeader(
                title = "Achievements",
                subtitle = "KINETRA · MILESTONES",
                onBack = onBack,
                trailingContent = {
                    KinetraBadge(
                        text = "$unlockedCount / ${badges.size}",
                        textColor = OnAccent,
                        backgroundColor = BrandPrimary,
                        borderColor = BrandPrimary
                    )
                }
            )

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(badges) { badge ->
                    KinetraCard(
                        modifier = Modifier.fillMaxWidth(),
                        borderColor = if (badge.isUnlocked) CardBorder else CardBorderSubtle,
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (badge.isUnlocked) badge.color.copy(alpha = 0.14f)
                                        else SurfaceDark
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (badge.isUnlocked) badge.color.copy(alpha = 0.5f) else CardBorderSubtle,
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = badge.icon,
                                    contentDescription = badge.title,
                                    tint = if (badge.isUnlocked) badge.color else TextMuted,
                                    modifier = Modifier.size(25.dp)
                                )
                            }

                            Text(
                                text = badge.title,
                                color = if (badge.isUnlocked) TextWhite else TextGray,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = badge.description,
                                color = TextGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )

                            KinetraBadge(
                                text = if (badge.isUnlocked) "UNLOCKED" else "LOCKED",
                                textColor = if (badge.isUnlocked) BrandAccentText else TextMuted,
                                backgroundColor = if (badge.isUnlocked) AccentTintSurface else SurfaceDark,
                                borderColor = if (badge.isUnlocked) BrandPrimary.copy(alpha = 0.25f) else CardBorderSubtle
                            )
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, name = "Achievements - Light")
@Composable
private fun AchievementsLightPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.LIGHT)) {
        AchievementsScreen(onBack = {})
    }
}

@Preview(showBackground = true, name = "Achievements - Dark")
@Composable
private fun AchievementsDarkPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.DARK)) {
        AchievementsScreen(onBack = {})
    }
}

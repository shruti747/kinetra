package com.kinetra.app.ui.drawer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        BadgeItem("First Run", "Recorded your first outdoor run", Icons.Filled.DirectionsRun, true, AthleticGreen),
        BadgeItem("5K Finisher", "Completed a continuous 5 kilometer activity", Icons.Filled.MilitaryTech, true, ElectricCyan),
        BadgeItem("10K Legend", "Pushed through a grueling 10K distance", Icons.Filled.WorkspacePremium, true, AthleticAmber),
        BadgeItem("Consistency Champion", "Maintained a 7-day consecutive workout streak", Icons.Filled.FlashOn, true, AthleticOrange),
        BadgeItem("Pace Master", "Sustained sub-5:00 min/km pace for over 3 km", Icons.Filled.Speed, false, AthleticPurple),
        BadgeItem("Half Marathon", "Completed a 21.1 kilometer distance", Icons.Filled.Stars, false, TextMuted)
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ButtonBackground)
                        .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "ATHLETE ACHIEVEMENTS",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(badges) { badge ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                        border = BorderStroke(1.dp, if (badge.isUnlocked) ButtonBorder else CardBorderSubtle)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(54.dp)
                                    .clip(CircleShape)
                                    .background(badge.color.copy(alpha = if (badge.isUnlocked) 0.2f else 0.05f))
                                    .border(1.dp, if (badge.isUnlocked) badge.color else TextMuted.copy(alpha = 0.3f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = badge.icon,
                                    contentDescription = badge.title,
                                    tint = if (badge.isUnlocked) badge.color else TextMuted,
                                    modifier = Modifier.size(26.dp)
                                )
                            }

                            Text(
                                text = badge.title,
                                color = if (badge.isUnlocked) TextWhite else TextMuted,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )

                            Text(
                                text = badge.description,
                                color = TextGray,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 15.sp
                            )
                        }
                    }
                }
            }
        }
    }
}


package com.kinetra.app.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.*
import com.kinetra.app.ui.theme.*

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onStartRunClick: () -> Unit,
    onRaceDetailsClick: () -> Unit,
    onAiIntelligenceClick: () -> Unit,
    onNotificationsClick: () -> Unit = {},
    unreadNotificationsCount: Int = 0,
    onMenuClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    HomeScreenContent(
        state = state,
        unreadNotificationsCount = unreadNotificationsCount,
        onStartRunClick = onStartRunClick,
        onRaceDetailsClick = onRaceDetailsClick,
        onAiIntelligenceClick = onAiIntelligenceClick,
        onNotificationsClick = onNotificationsClick,
        onMenuClick = onMenuClick
    )
}

@Composable
internal fun HomeScreenContent(
    state: HomeUiState,
    unreadNotificationsCount: Int,
    onStartRunClick: () -> Unit,
    onRaceDetailsClick: () -> Unit,
    onAiIntelligenceClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onMenuClick: () -> Unit
) {
    val scrollState = rememberScrollState()

    val weeklyKmNumeric = remember(state.weeklyMileage) {
        state.weeklyMileage.replace("km", "", ignoreCase = true).trim().toFloatOrNull() ?: 0f
    }
    val weeklyProgress = (weeklyKmNumeric / 35f).coerceIn(0f, 1f)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    KinetraIconButton(
                        icon = Icons.Filled.Menu,
                        onClick = onMenuClick,
                        contentDescription = "Open Menu",
                        size = 40.dp
                    )

                    Column {
                        Text(
                            text = "Morning, ${state.athleteName}.",
                            color = TextWhite,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.2).sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "${state.dateString} · ${state.locationString}",
                            color = TextGray,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(ButtonBackground)
                            .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                            .clickable { onNotificationsClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Notifications,
                            contentDescription = "Notifications",
                            tint = if (unreadNotificationsCount > 0) BrandAccentText else TextGray,
                            modifier = Modifier.size(20.dp)
                        )
                        if (unreadNotificationsCount > 0) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .padding(6.dp)
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(BrandPrimary)
                            )
                        }
                    }

                    KinetraAppBadge(size = 40.dp, cornerRadius = 12.dp)
                }
            }

            // Primary Start Workout Action Card / Hero CTA
            KinetraButton(
                text = "Start live workout",
                onClick = onStartRunClick,
                leadingIcon = Icons.Filled.PlayArrow,
                modifier = Modifier.fillMaxWidth(),
                height = 54.dp
            )

            // Featured Next Race Card
            KinetraClickableCard(
                onClick = onRaceDetailsClick,
                modifier = Modifier.fillMaxWidth(),
                borderColor = CardBorder,
                contentPadding = PaddingValues(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KinetraBadge(
                        text = "YOUR NEXT RACE",
                        textColor = BrandAccentText,
                        backgroundColor = AccentTintSurface,
                        borderColor = BrandPrimary.copy(alpha = 0.2f)
                    )
                    Text(
                        text = state.nextRaceFee,
                        color = TextWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = state.nextRaceName,
                    color = TextWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.3).sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "${state.nextRaceLocation} · ${state.nextRaceDate}",
                        color = TextGray,
                        fontSize = 12.sp,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "View Race Details",
                        tint = BrandAccentText,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Metrics Row: Weekly Mileage & 10K Personal Best
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KinetraCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WEEKLY GOAL",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.DirectionsRun,
                            contentDescription = null,
                            tint = BrandAccentText,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = state.weeklyMileage,
                        color = TextWhite,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProgressTrack(
                        progress = weeklyProgress,
                        height = 5.dp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "of 35 km target",
                        color = TextGray,
                        fontSize = 11.sp
                    )
                }

                KinetraCard(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "10K BEST",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 0.8.sp
                        )
                        Icon(
                            imageVector = Icons.Filled.EmojiEvents,
                            contentDescription = null,
                            tint = BrandAccentText,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = state.personalBest,
                        color = TextWhite,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(19.dp))
                    Text(
                        text = "Pace ${state.pbAvgPace}",
                        color = BrandAccentText,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Last Activity Card
            SectionHeader(title = "Recent Activity")

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LAST SESSION · ${state.lastActivityDate}",
                        color = TextGray,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.Speed,
                        contentDescription = null,
                        tint = BrandAccentText,
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.lastActivityTitle,
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "GPS telemetry · synced to profile",
                            color = TextGray,
                            fontSize = 12.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = state.lastActivityTime,
                        color = BrandAccentText,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // AI Performance Insight Banner
            KinetraClickableCard(
                onClick = onAiIntelligenceClick,
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = AccentTintSurface,
                borderColor = BrandPrimary.copy(alpha = 0.25f),
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoAwesome,
                            contentDescription = null,
                            tint = BrandAccentText,
                            modifier = Modifier.size(15.dp)
                        )
                        Text(
                            text = "PERFORMANCE INSIGHT",
                            color = BrandAccentText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                    Text(
                        text = "View analysis ›",
                        color = BrandAccentText,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = state.aiRecommendation,
                    color = TextWhite,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    lineHeight = 20.sp
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

private val sampleHomePreviewState = HomeUiState(
    athleteName = "Alex",
    dateString = "WED 18 NOV 2026",
    locationString = "LONDON, UK",
    nextRaceName = "RIVERSIDE 10K",
    nextRaceDate = "22 NOV 2026 · 09:00",
    nextRaceLocation = "Victoria Park, London",
    nextRaceFee = "£35",
    weeklyMileage = "28.5 km",
    personalBest = "46:12",
    pbAvgPace = "4:37 /km",
    lastActivityTitle = "Morning Tempo Run",
    lastActivityTime = "47:36",
    lastActivityDate = "YESTERDAY",
    aiRecommendation = "Your pacing stayed steady across splits 6–9 km. Add an easy 5 km recovery jog before race day."
)

@Preview(showBackground = true, name = "Home Dashboard - Light")
@Composable
private fun HomeScreenLightPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.LIGHT)) {
        HomeScreenContent(
            state = sampleHomePreviewState,
            unreadNotificationsCount = 2,
            onStartRunClick = {},
            onRaceDetailsClick = {},
            onAiIntelligenceClick = {},
            onNotificationsClick = {},
            onMenuClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Home Dashboard - Dark")
@Composable
private fun HomeScreenDarkPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.DARK)) {
        HomeScreenContent(
            state = sampleHomePreviewState,
            unreadNotificationsCount = 2,
            onStartRunClick = {},
            onRaceDetailsClick = {},
            onAiIntelligenceClick = {},
            onNotificationsClick = {},
            onMenuClick = {}
        )
    }
}

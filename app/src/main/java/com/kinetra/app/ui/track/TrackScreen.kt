package com.kinetra.app.ui.track

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraBadge
import com.kinetra.app.ui.components.KinetraButton
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraOutlinedButton
import com.kinetra.app.ui.theme.*

@Composable
fun TrackScreen(
    viewModel: TrackViewModel,
    onViewAnalysis: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()

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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KINETRA · TRACK",
                        color = BrandAccentText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "Victoria Park, London",
                        color = TextWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                KinetraBadge(
                    text = "GPS SESSION",
                    textColor = BrandAccentText,
                    backgroundColor = AccentTintSurface,
                    borderColor = BrandPrimary.copy(alpha = 0.25f)
                )
            }

            // Mode Selector Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                TrackState.values().forEach { mode ->
                    val isSelected = state.currentTrackState == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) BrandPrimary else CardBackground)
                            .border(
                                width = 1.dp,
                                color = if (isSelected) BrandPrimary else CardBorder,
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.setState(mode) }
                            .padding(horizontal = 4.dp, vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mode.name.replace("_", " "),
                            color = if (isSelected) OnAccent else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }

            when (state.currentTrackState) {
                TrackState.LIVE_GPS -> LiveGpsView(
                    state = state,
                    onPause = { viewModel.setState(TrackState.PAUSED) },
                    onFinish = { viewModel.setState(TrackState.COMPLETED) },
                    onGhostClick = { viewModel.setState(TrackState.GHOST_RUNNER) }
                )
                TrackState.PAUSED -> PausedView(
                    state = state,
                    onResume = { viewModel.setState(TrackState.LIVE_GPS) },
                    onConfirmFinish = { viewModel.setState(TrackState.COMPLETED) }
                )
                TrackState.COMPLETED -> CompletedView(onViewAnalysis = onViewAnalysis)
                TrackState.GHOST_RUNNER -> GhostRunnerView(onBack = { viewModel.setState(TrackState.LIVE_GPS) })
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun LiveGpsView(state: TrackUiState, onPause: () -> Unit, onFinish: () -> Unit, onGhostClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KinetraCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BrandPrimary.copy(alpha = 0.4f),
            contentPadding = PaddingValues(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ELAPSED TIME",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = state.elapsedTime,
                    color = TextWhite,
                    fontSize = 46.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-1).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "MOVING SPEED · ${state.movingSpeed}",
                    color = BrandAccentText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KinetraCard(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp)) {
                Text(text = "DISTANCE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = state.distance, color = TextWhite, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            }
            KinetraCard(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp)) {
                Text(text = "AVG PACE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = state.avgPace, color = BrandAccentText, fontSize = 21.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onGhostClick() },
            shape = RoundedCornerShape(KinetraTheme.cardRadius),
            colors = CardDefaults.cardColors(containerColor = AccentTintSurface),
            border = BorderStroke(1.dp, BrandPrimary.copy(alpha = 0.25f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "GHOST RUNNER", color = BrandAccentText, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(text = state.ghostDiff, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "View ›", color = BrandAccentText, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KinetraOutlinedButton(
                text = "Pause",
                onClick = onPause,
                modifier = Modifier.weight(1f),
                height = 50.dp
            )
            KinetraButton(
                text = "Finish",
                onClick = onFinish,
                modifier = Modifier.weight(1f),
                height = 50.dp
            )
        }
    }
}

@Composable
fun PausedView(state: TrackUiState, onResume: () -> Unit, onConfirmFinish: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KinetraCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(24.dp)) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "SESSION PAUSED", color = BrandAccentText, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.elapsedTime, color = TextWhite, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Timer paused", color = TextGray, fontSize = 12.sp)
            }
        }

        KinetraCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
            Text(text = "FINISH CONFIRMATION", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "Save ${state.distance} · ${state.elapsedTime} to your workout log.", color = TextWhite, fontSize = 14.sp)
        }

        KinetraButton(
            text = "Resume workout",
            onClick = onResume,
            modifier = Modifier.fillMaxWidth()
        )

        KinetraOutlinedButton(
            text = "Confirm finish",
            onClick = onConfirmFinish,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun CompletedView(onViewAnalysis: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KinetraCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BrandPrimary.copy(alpha = 0.45f),
            contentPadding = PaddingValues(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "TRAINING COMPLETE", color = BrandAccentText, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "47:36", color = TextWhite, fontSize = 44.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Victoria Park · 16 Nov 2026", color = TextGray, fontSize = 12.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            KinetraCard(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp)) {
                Text(text = "DISTANCE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "10.00 km", color = TextWhite, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
            KinetraCard(modifier = Modifier.weight(1f), contentPadding = PaddingValues(16.dp)) {
                Text(text = "AVG PACE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "4:46 /km", color = BrandAccentText, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold)
            }
        }

        KinetraButton(
            text = "View AI analysis ›",
            onClick = onViewAnalysis,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
fun GhostRunnerView(onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        KinetraCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = BrandPrimary.copy(alpha = 0.45f),
            contentPadding = PaddingValues(24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "GHOST RUNNER · PB COMPARISON", color = BrandAccentText, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "54s BEHIND", color = TextWhite, fontSize = 34.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Chasing your 10K personal best (46:12)", color = TextGray, fontSize = 13.sp)
            }
        }

        KinetraCard(modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(16.dp)) {
            Text(text = "CHECKPOINT AT 6.20 KM", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(text = "You: 29:33  ·  PB: 28:39", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(3.dp))
            Text(text = "Projected finish: 47:40 (1:28 behind PB)", color = BrandAccentText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        }

        KinetraOutlinedButton(
            text = "Back to Live GPS",
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

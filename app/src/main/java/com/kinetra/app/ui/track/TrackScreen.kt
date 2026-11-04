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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header & State switcher tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KINETRA / TRACK",
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = "Victoria Park, London",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .background(ButtonBackground, RoundedCornerShape(12.dp))
                        .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(text = "SIMULATED GPS", color = GlowBlueBright, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Mode Selector tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrackState.values().forEach { mode ->
                    val isSelected = state.currentTrackState == mode
                    Button(
                        onClick = { viewModel.setState(mode) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isSelected) IndicatorActive else ButtonBackground
                        ),
                        contentPadding = PaddingValues(horizontal = 4.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = mode.name.replace("_", " "),
                            color = TextWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Render view based on state
            when (state.currentTrackState) {
                TrackState.LIVE_GPS -> LiveGpsView(state, onPause = { viewModel.setState(TrackState.PAUSED) }, onFinish = { viewModel.setState(TrackState.COMPLETED) }, onGhostClick = { viewModel.setState(TrackState.GHOST_RUNNER) })
                TrackState.PAUSED -> PausedView(state, onResume = { viewModel.setState(TrackState.LIVE_GPS) }, onConfirmFinish = { viewModel.setState(TrackState.COMPLETED) })
                TrackState.COMPLETED -> CompletedView(onViewAnalysis = onViewAnalysis)
                TrackState.GHOST_RUNNER -> GhostRunnerView(onBack = { viewModel.setState(TrackState.LIVE_GPS) })
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun LiveGpsView(state: TrackUiState, onPause: () -> Unit, onFinish: () -> Unit, onGhostClick: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ButtonBackground),
            border = BorderStroke(1.dp, GlowBlueBright)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "ELAPSED TIME", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = state.elapsedTime, color = TextWhite, fontSize = 48.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "MOVING SPEED: ${state.movingSpeed}", color = GlowBlueBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ButtonBackground), border = BorderStroke(1.dp, ButtonBorder)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Distance", color = TextGray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = state.distance, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ButtonBackground), border = BorderStroke(1.dp, ButtonBorder)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Avg Pace", color = TextGray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = state.avgPace, color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth().clickable { onGhostClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ButtonBackground),
            border = BorderStroke(1.dp, ButtonBorder)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "GHOST RUNNER", color = GlowBlueBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = state.ghostDiff, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "View ›", color = GlowBlueBright, fontSize = 14.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = onPause, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(25.dp), colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground), border = BorderStroke(1.dp, ButtonBorder)) {
                Text("Pause", color = TextWhite, fontWeight = FontWeight.Bold)
            }
            Button(onClick = onFinish, modifier = Modifier.weight(1f).height(50.dp), shape = RoundedCornerShape(25.dp), colors = ButtonDefaults.buttonColors(containerColor = IndicatorActive)) {
                Text("Finish", color = TextWhite, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun PausedView(state: TrackUiState, onResume: () -> Unit, onConfirmFinish: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ButtonBackground),
            border = BorderStroke(1.dp, ButtonBorder)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "RUN PAUSED", color = GlowBlueBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = state.elapsedTime, color = TextWhite, fontSize = 42.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Timer frozen", color = TextGray, fontSize = 12.sp)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ButtonBackground),
            border = BorderStroke(1.dp, ButtonBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "FINISH CONFIRMATION", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Save ${state.distance} / ${state.elapsedTime} to your activity log.", color = TextWhite, fontSize = 14.sp)
            }
        }

        Button(onClick = onResume, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(26.dp), colors = ButtonDefaults.buttonColors(containerColor = IndicatorActive)) {
            Text("Resume run", color = TextWhite, fontWeight = FontWeight.Bold)
        }

        Button(onClick = onConfirmFinish, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(26.dp), colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground), border = BorderStroke(1.dp, ButtonBorder)) {
            Text("Confirm finish", color = TextWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CompletedView(onViewAnalysis: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ButtonBackground),
            border = BorderStroke(1.dp, GlowBlueBright)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "TRAINING COMPLETE", color = GlowBlueBright, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "47:36", color = TextWhite, fontSize = 44.sp, fontWeight = FontWeight.ExtraBold)
                Text(text = "Victoria Park · 16 Nov 2026", color = TextGray, fontSize = 12.sp)
            }
        }

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ButtonBackground), border = BorderStroke(1.dp, ButtonBorder)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Distance", color = TextGray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "10.00 km", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            Card(modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = ButtonBackground), border = BorderStroke(1.dp, ButtonBorder)) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Avg Pace", color = TextGray, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "4:46 /km", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Button(
            onClick = onViewAnalysis,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(26.dp),
            colors = ButtonDefaults.buttonColors(containerColor = IndicatorActive)
        ) {
            Text("View AI analysis ›", color = TextWhite, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GhostRunnerView(onBack: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = ButtonBackground),
            border = BorderStroke(1.dp, GlowBlueBright)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "GHOST RUNNER · PB COMPARISON", color = GlowBlueBright, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "54s BEHIND", color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Chasing your 10K personal best (46:12)", color = TextGray, fontSize = 13.sp)
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = ButtonBackground),
            border = BorderStroke(1.dp, ButtonBorder)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(text = "CHECKPOINT AT 6.20 KM", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "You: 29:33  /  PB: 28:39", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Projected finish: 47:40 (1:28 behind PB)", color = GlowBlueBright, fontSize = 13.sp)
            }
        }

        Button(onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(26.dp), colors = ButtonDefaults.buttonColors(containerColor = ButtonBackground), border = BorderStroke(1.dp, ButtonBorder)) {
            Text("Back to Live GPS", color = TextWhite, fontWeight = FontWeight.Bold)
        }
    }
}

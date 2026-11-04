package com.kinetra.app.ui.ai

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
fun AiIntelligenceScreen(
    viewModel: AiIntelligenceViewModel,
    onBack: () -> Unit
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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "KINETRA  /  AI INTELLIGENCE", color = GlowBlueBright, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text(text = "Performance Analysis", color = TextWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "Back", color = GlowBlueBright, fontSize = 14.sp, modifier = Modifier.clickable { onBack() })
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, GlowBlueBright)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "${state.athleteName} · ${state.runDate}", color = TextGray, fontSize = 12.sp)
                    Text(text = state.time, color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Distance ${state.distance} · Avg pace ${state.avgPace} · Elevation ${state.elevationGain}", color = GlowBlueBright, fontSize = 13.sp)
                }
            }

            // Pace Progression Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "PACE PROGRESSION · FASTER FINISH", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = state.paceProgression, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Split spread: ${state.splitSpread}", color = TextGray, fontSize = 13.sp)
                }
            }

            // Next 10K AI Estimate Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "NEXT 10K  /  AI ESTIMATE", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = state.nextEstimate, color = GlowBlueBright, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                    Text(text = "Illustrative range. Not an official result.", color = TextGray, fontSize = 12.sp)
                }
            }

            // Race-Week Focus Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "ALEX’S RACE-WEEK FOCUS", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(text = state.raceWeekFocus, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

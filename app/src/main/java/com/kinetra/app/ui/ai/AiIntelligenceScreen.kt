package com.kinetra.app.ui.ai

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraScreenHeader
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
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KinetraScreenHeader(
                title = "Performance Analysis",
                subtitle = "KINETRA · INTELLIGENCE",
                onBack = onBack
            )

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BrandPrimary.copy(alpha = 0.45f),
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(text = "${state.athleteName} · ${state.runDate}", color = TextGray, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = state.time, color = TextWhite, fontSize = 36.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Distance ${state.distance} · Avg pace ${state.avgPace} · Elevation ${state.elevationGain}",
                    color = BrandAccentText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(text = "PACE PROGRESSION · FASTER FINISH", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = state.paceProgression, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Split spread: ${state.splitSpread}", color = TextGray, fontSize = 13.sp)
            }

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = AccentTintSurface,
                borderColor = BrandPrimary.copy(alpha = 0.25f),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(text = "NEXT 10K · AI ESTIMATE", color = BrandAccentText, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = state.nextEstimate, color = BrandAccentText, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "Illustrative projection based on recent splits.", color = TextGray, fontSize = 12.sp)
            }

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(text = "RACE-WEEK FOCUS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = state.raceWeekFocus, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 22.sp)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

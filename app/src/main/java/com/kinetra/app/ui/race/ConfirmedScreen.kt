package com.kinetra.app.ui.race

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.theme.*

@Composable
fun ConfirmedScreen(
    viewModel: RaceViewModel,
    onReturnHome: () -> Unit
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
            Text(text = "CONFIRMED · DEMO", color = GlowBlueBright, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            Text(text = "YOU’RE ON THE LIST.", color = TextWhite, fontSize = 32.sp, fontWeight = FontWeight.ExtraBold)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, GlowBlueBright)
            ) {
                Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "REGISTRATION REFERENCE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = state.registrationReference, color = GlowBlueBright, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = state.raceTitle, color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${state.raceDate} · ${state.location}", color = TextGray, fontSize = 13.sp)
                    Text(text = "Participant: ${state.participantName}", color = TextGray, fontSize = 13.sp)
                }
            }

            Button(
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndicatorActive)
            ) {
                Text(text = "Return to Home", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

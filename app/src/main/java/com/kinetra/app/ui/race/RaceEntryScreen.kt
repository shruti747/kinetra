package com.kinetra.app.ui.race

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
fun RaceEntryScreen(
    viewModel: RaceViewModel,
    onSubmitSuccess: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    var acceptedWaiver by remember { mutableStateOf(false) }
    var confirmedFit by remember { mutableStateOf(false) }

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
                    Text(text = "KINETRA / RACE ENTRY", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text(text = state.raceTitle, color = TextWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "Back", color = GlowBlueBright, fontSize = 14.sp, modifier = Modifier.clickable { onBack() })
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "PARTICIPANT / LONDON, UK", color = GlowBlueBright, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = state.participantName, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Emergency contact: ${state.emergencyContact}", color = TextGray, fontSize = 13.sp)
                }
            }

            // Checkboxes
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = acceptedWaiver, onCheckedChange = { acceptedWaiver = it })
                Text(text = "I accept the event waiver, terms and privacy policy.", color = TextWhite, fontSize = 13.sp)
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(checked = confirmedFit, onCheckedChange = { confirmedFit = it })
                Text(text = "I confirm I am fit to participate.", color = TextWhite, fontSize = 13.sp)
            }

            Button(
                onClick = onSubmitSuccess,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(26.dp),
                colors = ButtonDefaults.buttonColors(containerColor = IndicatorActive)
            ) {
                Text(text = "Submit registration · ${state.fee}", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

package com.kinetra.app.ui.race

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraButton
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraScreenHeader
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

    val checkboxColors = CheckboxDefaults.colors(
        checkedColor = BrandPrimary,
        uncheckedColor = TextGray,
        checkmarkColor = OnAccent
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
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KinetraScreenHeader(
                title = state.raceTitle,
                subtitle = "KINETRA · RACE ENTRY",
                onBack = onBack
            )

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(
                    text = "PARTICIPANT · LONDON, UK",
                    color = BrandAccentText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = state.participantName,
                    color = TextWhite,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Emergency contact: ${state.emergencyContact}",
                    color = TextGray,
                    fontSize = 13.sp
                )
            }

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(14.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = acceptedWaiver,
                        onCheckedChange = { acceptedWaiver = it },
                        colors = checkboxColors
                    )
                    Text(
                        text = "I accept the event waiver, terms and privacy policy.",
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = confirmedFit,
                        onCheckedChange = { confirmedFit = it },
                        colors = checkboxColors
                    )
                    Text(
                        text = "I confirm I am fit to participate.",
                        color = TextWhite,
                        fontSize = 13.sp
                    )
                }
            }

            KinetraButton(
                text = "Submit registration · ${state.fee}",
                onClick = onSubmitSuccess,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

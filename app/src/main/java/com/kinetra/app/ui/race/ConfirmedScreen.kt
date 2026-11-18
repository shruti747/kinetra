package com.kinetra.app.ui.race

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraButton
import com.kinetra.app.ui.components.KinetraCard
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
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(BrandPrimary),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = "Confirmed",
                    tint = OnAccent,
                    modifier = Modifier.size(28.dp)
                )
            }

            Column {
                Text(
                    text = "REGISTRATION CONFIRMED",
                    color = BrandAccentText,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.4.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "You're on the list.",
                    color = TextWhite,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp
                )
            }

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BrandPrimary.copy(alpha = 0.4f),
                contentPadding = PaddingValues(20.dp)
            ) {
                Text(
                    text = "REGISTRATION REFERENCE",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.registrationReference,
                    color = BrandAccentText,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CardBorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = state.raceTitle,
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "${state.raceDate} · ${state.location}", color = TextSecondary, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = "Participant: ${state.participantName}", color = TextGray, fontSize = 13.sp)
            }

            KinetraButton(
                text = "Return to Home",
                onClick = onReturnHome,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

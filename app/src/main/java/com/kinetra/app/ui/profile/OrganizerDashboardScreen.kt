package com.kinetra.app.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraScreenHeader
import com.kinetra.app.ui.components.ProgressTrack
import com.kinetra.app.ui.components.SectionHeader
import com.kinetra.app.ui.theme.*

@Composable
fun OrganizerDashboardScreen(onBack: () -> Unit) {
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
                title = "Organizer Dashboard",
                subtitle = "VICTORIA PARK ATHLETICS",
                onBack = onBack
            )

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BrandPrimary.copy(alpha = 0.4f),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(text = "Riverside 10K", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "22 Nov 2026 · 09:00 · Victoria Park, London", color = TextGray, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(14.dp))
                ProgressTrack(progress = 0.69f, height = 6.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Column {
                        Text(text = "REGISTRATIONS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "347 / 500 (69%)", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "ENTRY VALUE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.8.sp)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = "£12,145", color = BrandAccentText, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            SectionHeader(title = "Participants (3 of 347)")

            val participants = listOf(
                Triple("Alex Morgan", "KTR-1024 · Paid", "Bib 142 assigned"),
                Triple("Jamie Reeves", "KTR-1025 · Paid", "Bib pending"),
                Triple("Sara Kim", "KTR-1026 · Paid", "Bib 144 assigned")
            )

            participants.forEach { (name, ref, bib) ->
                KinetraCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = name, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = ref, color = BrandAccentText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = bib, color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

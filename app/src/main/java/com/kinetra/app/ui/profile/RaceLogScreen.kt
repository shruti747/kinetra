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
import com.kinetra.app.ui.components.SectionHeader
import com.kinetra.app.ui.theme.*

@Composable
fun RaceLogScreen(onBack: () -> Unit) {
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
                title = "Event History",
                subtitle = "KINETRA · RACE LOG",
                onBack = onBack
            )

            SectionHeader(title = "Upcoming Registration")

            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BrandPrimary.copy(alpha = 0.45f),
                contentPadding = PaddingValues(18.dp)
            ) {
                Text(text = "Riverside 10K", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "22 Nov 2026 · 09:00 · 10 km · £35", color = BrandAccentText, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(text = "Victoria Park, London · KTR-1024", color = TextGray, fontSize = 12.sp)
            }

            SectionHeader(title = "Completed Activities")

            val activities = listOf(
                Triple("Parkland 10K", "08 Nov 2026 · 10 km", "46:12 · Personal Best"),
                Triple("Canal 5K", "25 Oct 2026 · 5 km", "18:42 · Personal Best"),
                Triple("Thames Half", "18 Oct 2026 · 21.1 km", "1:38:04 · Personal Best"),
                Triple("10km Training", "16 Nov 2026 · 10 km", "47:36 · GPS Session")
            )

            activities.forEach { (title, date, detail) ->
                KinetraCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = date, color = TextGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(text = detail, color = BrandAccentText, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Text(text = "›", color = BrandAccentText, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

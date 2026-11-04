package com.kinetra.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
                    Text(text = "KINETRA / RACE LOG", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text(text = "Event History", color = TextWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "Back", color = GlowBlueBright, fontSize = 14.sp, modifier = Modifier.clickable { onBack() })
            }

            // Upcoming
            Text(text = "UPCOMING / REGISTRATION", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, GlowBlueBright)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Riverside 10K", color = TextWhite, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "22 Nov 2026 · 09:00 · 10 km · £35", color = GlowBlueBright, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "Victoria Park, London · KTR-1024", color = TextGray, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "COMPLETED ACTIVITIES", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            val activities = listOf(
                Triple("Parkland 10K", "08 Nov 2026 · 10 km", "46:12 (Official · demo · PB)"),
                Triple("Canal 5K", "25 Oct 2026 · 5 km", "18:42 (GPS estimate · PB)"),
                Triple("Thames Half", "18 Oct 2026 · 21.1 km", "1:38:04 (Official · demo · PB)"),
                Triple("10km training", "16 Nov 2026 · 10 km", "47:36 (GPS estimate · official pending)")
            )

            activities.forEach { (title, date, detail) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                    border = BorderStroke(1.dp, ButtonBorder)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = date, color = TextGray, fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = detail, color = GlowBlueBright, fontSize = 11.sp)
                        }
                        Text(text = "Details ›", color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

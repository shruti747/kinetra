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
                    Text(text = "VICTORIA PARK ATHLETICS / DEMO", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text(text = "Organizer Dashboard", color = TextWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "Back", color = GlowBlueBright, fontSize = 14.sp, modifier = Modifier.clickable { onBack() })
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, GlowBlueBright)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Text(text = "Riverside 10K", color = TextWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = "22 Nov 2026 · 09:00 · Victoria Park, London", color = TextGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text(text = "REGISTRATIONS", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = "347 / 500 (69%)", color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                        Column {
                            Text(text = "ENTRY VALUE", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            Text(text = "£12,145", color = GlowBlueBright, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Text(text = "PARTICIPANTS (3 OF 347)", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            val participants = listOf(
                Triple("Alex Morgan", "KTR-1024 · Paid", "Bib 142 assigned"),
                Triple("Jamie Reeves", "KTR-1025 · Paid", "Bib pending"),
                Triple("Sara Kim", "KTR-1026 · Paid", "Bib 144 assigned")
            )

            participants.forEach { (name, ref, bib) ->
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
                            Text(text = name, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = ref, color = GlowBlueBright, fontSize = 12.sp)
                        }
                        Text(text = bib, color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

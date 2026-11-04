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
fun NotificationsScreen(onBack: () -> Unit) {
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
                    Text(text = "KINETRA / ACTIVITY", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
                    Text(text = "Notifications", color = TextWhite, fontSize = 26.sp, fontWeight = FontWeight.Bold)
                }
                Text(text = "Back", color = GlowBlueBright, fontSize = 14.sp, modifier = Modifier.clickable { onBack() })
            }

            val notifications = listOf(
                Triple("REMINDER · TODAY, 09:00", "Riverside 10K", "6 days to go · 22 Nov, 09:00 · Victoria Park, London"),
                Triple("REGISTRATION · YESTERDAY, 18:12", "Your entry is confirmed", "Riverside 10K · KTR-1024 · £35 demo"),
                Triple("ACHIEVEMENT · 08 NOV", "New 10K best: 46:12", "Official · demo · Sub-47 club unlocked"),
                Triple("ANNOUNCEMENT · 13 NOV", "Race-day briefing is ready", "Riverside 10K · Bag drop opens at 08:00")
            )

            notifications.forEach { (header, title, desc) ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                    border = BorderStroke(1.dp, ButtonBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = header, color = GlowBlueBright, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = desc, color = TextGray, fontSize = 12.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

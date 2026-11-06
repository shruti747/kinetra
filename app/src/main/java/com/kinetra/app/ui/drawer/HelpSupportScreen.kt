package com.kinetra.app.ui.drawer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.theme.*

@Composable
fun HelpSupportScreen(onBack: () -> Unit) {
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
                .padding(20.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(ButtonBackground)
                        .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                        .clickable { onBack() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = TextWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = "HELP & DEVICE SUPPORT",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            HelpCard(
                question = "How do I pair my Huawei Watch GT 4?",
                answer = "1. Install the official Huawei Health app (com.huawei.health).\n2. Pair your Watch GT 4 inside Huawei Health via Bluetooth.\n3. Open Kinetra > Connected Devices and tap 'Connect'.\n4. Authorize Kinetra to access sensor telemetry."
            )

            HelpCard(
                question = "What if I don't own a smartwatch?",
                answer = "Kinetra automatically uses your smartphone's internal accelerometer, gyroscope, and GPS sensors. You will receive genuine distance, speed, pace, and motion tracking without needing a watch."
            )

            HelpCard(
                question = "Why does tracking require a Foreground Service?",
                answer = "Android terminates background location apps to save battery. Kinetra runs a specialized Android 14 Location/Health Foreground Service with a persistent notification so your workout route and pace never pause when you lock your screen or switch apps."
            )

            HelpCard(
                question = "How is my workout data saved?",
                answer = "Every completed activity is instantly preserved in your local Kinetra workout database, synchronized with your athlete profile, and queued for synchronization with the XAMPP backend."
            )
        }
    }
}

@Composable
private fun HelpCard(question: String, answer: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ButtonBackground),
        border = BorderStroke(1.dp, ButtonBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = question, color = ElectricCyan, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = answer, color = TextGray, fontSize = 13.sp, lineHeight = 19.sp)
        }
    }
}


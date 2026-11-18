package com.kinetra.app.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraScreenHeader
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
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            KinetraScreenHeader(
                title = "Help & Support",
                subtitle = "KINETRA · GUIDE",
                onBack = onBack
            )

            HelpCard(
                question = "How do I pair my Huawei Watch GT 4?",
                answer = "1. Install the official Huawei Health app (com.huawei.health).\n2. Pair your Watch GT 4 inside Huawei Health via Bluetooth.\n3. Open KINETRA > Connected Devices and tap 'Connect'.\n4. Authorize KINETRA to access sensor telemetry."
            )

            HelpCard(
                question = "What if I don't own a smartwatch?",
                answer = "KINETRA automatically uses your smartphone's internal accelerometer, gyroscope, and GPS sensors. You receive genuine distance, speed, pace, and motion tracking without needing a watch."
            )

            HelpCard(
                question = "Why does tracking require a Foreground Service?",
                answer = "Android stops background location tasks to preserve battery. KINETRA runs an Android 14 Location Foreground Service with a persistent status notification so your route and pace stay uninterrupted when your screen locks."
            )

            HelpCard(
                question = "How is my workout data saved?",
                answer = "Every completed workout is preserved in your local KINETRA database, linked to your athlete profile, and ready for backend synchronization."
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun HelpCard(question: String, answer: String) {
    KinetraCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(18.dp)
    ) {
        Text(
            text = question,
            color = BrandAccentText,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = answer,
            color = TextSecondary,
            fontSize = 13.sp,
            lineHeight = 20.sp
        )
    }
}

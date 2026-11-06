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
import androidx.compose.material.icons.filled.EmojiEvents
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
fun ChallengesScreen(onBack: () -> Unit) {
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
                    text = "ATHLETIC CHALLENGES",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            ChallengeCard(
                title = "November 50K Milestone",
                description = "Complete 50 kilometers of outdoor running throughout November.",
                participants = "1,420 Athletes Joined",
                reward = "+500 Kinetra XP"
            )

            ChallengeCard(
                title = "Weekend 10K Streak",
                description = "Log a continuous 10K run under 60 minutes this weekend.",
                participants = "840 Athletes Joined",
                reward = "+250 Kinetra XP & Gold Badge"
            )

            ChallengeCard(
                title = "Climb & Elevation Master",
                description = "Gain 500m of total elevation across trail runs or outdoor cycling.",
                participants = "310 Athletes Joined",
                reward = "+400 Kinetra XP"
            )
        }
    }
}

@Composable
private fun ChallengeCard(title: String, description: String, participants: String, reward: String) {
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
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = title, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Icon(imageVector = Icons.Filled.EmojiEvents, contentDescription = null, tint = AthleticAmber)
            }
            Text(text = description, color = TextGray, fontSize = 13.sp)
            HorizontalDivider(color = CardBorderSubtle)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = participants, color = TextMuted, fontSize = 11.sp)
                Text(text = reward, color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}


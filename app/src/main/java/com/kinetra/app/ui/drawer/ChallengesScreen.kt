package com.kinetra.app.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraBadge
import com.kinetra.app.ui.components.KinetraCard
import com.kinetra.app.ui.components.KinetraScreenHeader
import com.kinetra.app.ui.components.SectionHeader
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
                .navigationBarsPadding()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            KinetraScreenHeader(
                title = "Challenges",
                subtitle = "KINETRA · COMMUNITY",
                onBack = onBack
            )

            SectionHeader(title = "Featured Monthly Challenges")

            ChallengeCard(
                title = "November 50K Milestone",
                description = "Complete 50 kilometers of outdoor running throughout November.",
                participants = "1,420 Athletes Joined",
                reward = "+500 KINETRA XP"
            )

            ChallengeCard(
                title = "Weekend 10K Streak",
                description = "Log a continuous 10K run under 60 minutes this weekend.",
                participants = "840 Athletes Joined",
                reward = "+250 XP · Gold Badge"
            )

            ChallengeCard(
                title = "Climb & Elevation Master",
                description = "Gain 500m of total elevation across trail runs or outdoor cycling.",
                participants = "310 Athletes Joined",
                reward = "+400 KINETRA XP"
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun ChallengeCard(title: String, description: String, participants: String, reward: String) {
    KinetraCard(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(18.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = TextWhite,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AccentTintSurface),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Filled.EmojiEvents,
                    contentDescription = null,
                    tint = BrandAccentText,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = description,
            color = TextGray,
            fontSize = 13.sp,
            lineHeight = 19.sp
        )

        Spacer(modifier = Modifier.height(14.dp))
        HorizontalDivider(color = CardBorderSubtle)
        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = participants, color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            KinetraBadge(
                text = reward,
                textColor = BrandAccentText,
                backgroundColor = AccentTintSurface,
                borderColor = BrandPrimary.copy(alpha = 0.25f)
            )
        }
    }
}

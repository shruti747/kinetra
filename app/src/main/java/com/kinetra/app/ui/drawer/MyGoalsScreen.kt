package com.kinetra.app.ui.drawer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.*
import com.kinetra.app.ui.theme.*

@Composable
fun MyGoalsScreen(onBack: () -> Unit) {
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
                title = "Goals & Progress",
                subtitle = "KINETRA · TARGETS",
                onBack = onBack
            )

            // Overall Completion Summary Card
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = AccentTintSurface,
                borderColor = BrandPrimary.copy(alpha = 0.25f),
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "ACTIVE GOALS COMPLETION",
                            color = BrandAccentText,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "76% Overall",
                            color = TextWhite,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    KinetraBadge(
                        text = "4 ACTIVE",
                        textColor = OnAccent,
                        backgroundColor = BrandPrimary,
                        borderColor = BrandPrimary
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                ProgressTrack(progress = 0.76f, height = 7.dp)
            }

            SectionHeader(title = "Weekly & Monthly Milestones")

            GoalCard("Weekly Running Distance", "28.5 / 35.0 km", 0.81f, "4 days remaining")
            GoalCard("Monthly Active Workouts", "14 / 20 Sessions", 0.70f, "12 days remaining")
            GoalCard("Weekly Calorie Burn", "2,450 / 3,000 kcal", 0.82f, "On track")
            GoalCard("Long Run Milestone", "15.2 / 21.1 km", 0.72f, "Half Marathon Target")

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun GoalCard(title: String, progressText: String, progress: Float, subtitle: String) {
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
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${(progress * 100).toInt()}%",
                color = BrandAccentText,
                fontSize = 13.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = progressText,
            color = TextSecondary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium
        )

        Spacer(modifier = Modifier.height(10.dp))

        ProgressTrack(
            progress = progress,
            height = 7.dp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = subtitle,
            color = TextGray,
            fontSize = 11.sp
        )
    }
}

@Preview(showBackground = true, name = "Goals & Progress - Light")
@Composable
private fun MyGoalsLightPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.LIGHT)) {
        MyGoalsScreen(onBack = {})
    }
}

@Preview(showBackground = true, name = "Goals & Progress - Dark")
@Composable
private fun MyGoalsDarkPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.DARK)) {
        MyGoalsScreen(onBack = {})
    }
}

package com.kinetra.app.ui.drawer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.tracking.model.ActivityType
import com.kinetra.app.tracking.model.WorkoutSession
import com.kinetra.app.tracking.repository.WorkoutRepository
import com.kinetra.app.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WorkoutHistoryScreen(
    onBack: () -> Unit
) {
    val workouts by WorkoutRepository.workouts.collectAsState()
    val dateFormat = SimpleDateFormat("MMM dd, yyyy • hh:mm a", Locale.getDefault())

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(20.dp),
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
                    text = "WORKOUT HISTORY",
                    color = TextWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }

            if (workouts.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No workouts recorded yet. Start tracking to build your history!", color = TextGray)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(workouts) { session ->
                        WorkoutCard(session, dateFormat.format(Date(session.startTimeMillis)))
                    }
                }
            }
        }
    }
}

@Composable
private fun WorkoutCard(session: WorkoutSession, dateString: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = ButtonBackground),
        border = BorderStroke(1.dp, ButtonBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val icon = when (session.activityType) {
                        ActivityType.RUNNING -> Icons.Filled.DirectionsRun
                        ActivityType.CYCLING -> Icons.Filled.DirectionsBike
                        ActivityType.WALKING -> Icons.Filled.DirectionsWalk
                        ActivityType.GENERAL_WORKOUT -> Icons.Filled.FitnessCenter
                    }
                    Icon(imageVector = icon, contentDescription = null, tint = ElectricCyan, modifier = Modifier.size(20.dp))
                    Text(text = session.activityType.displayName, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }

                Text(text = dateString, color = TextMuted, fontSize = 11.sp)
            }

            HorizontalDivider(color = CardBorderSubtle)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                MetricColumn("DISTANCE", String.format("%.2f km", session.distanceMeters / 1000.0), ElectricCyan)
                MetricColumn("DURATION", formatDuration(session.durationSeconds), TextWhite)
                MetricColumn("PACE", session.averagePaceFormatted, TextWhite)
                MetricColumn("CALORIES", "${session.caloriesBurned} kcal", AthleticAmber)
                if (session.averageHeartRate != null) {
                    MetricColumn("AVG HR", "${session.averageHeartRate} bpm", AthleticRed)
                }
            }
        }
    }
}

@Composable
private fun MetricColumn(label: String, value: String, color: Color) {
    Column {
        Text(text = label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(text = value, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
    }
}

private fun formatDuration(seconds: Long): String {
    val m = seconds / 60
    val s = seconds % 60
    return String.format("%d:%02d", m, s)
}


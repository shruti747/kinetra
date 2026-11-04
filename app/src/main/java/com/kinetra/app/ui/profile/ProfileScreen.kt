package com.kinetra.app.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onOrganizerClick: () -> Unit,
    onNotificationsClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
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
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "KINETRA / ATHLETE",
                        color = TextGray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.5.sp
                    )
                    Text(
                        text = state.name,
                        color = TextWhite,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clickable { onNotificationsClick() }
                        .background(ButtonBackground, RoundedCornerShape(12.dp))
                        .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Notifications (${state.unreadNotificationsCount})",
                        color = GlowBlueBright,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Athlete Info Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = state.location, color = TextGray, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = state.sports, color = GlowBlueBright, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    Divider(color = ButtonBorder)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(text = "NEXT GOAL", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = state.nextGoal, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Personal Records Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = ButtonBackground),
                border = BorderStroke(1.dp, ButtonBorder)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "PERSONAL RECORDS", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    state.personalRecords.forEach { (distance, time) ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(text = distance, color = TextWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(text = time, color = TextGray, fontSize = 13.sp)
                        }
                    }
                }
            }

            // Navigation Links
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileMenuButton(title = "Event History & Race Log", onClick = onHistoryClick)
                ProfileMenuButton(title = "Settings & Preferences", onClick = onSettingsClick)
                ProfileMenuButton(title = "Organizer Dashboard ›", onClick = onOrganizerClick)
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileMenuButton(title: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ButtonBackground),
        border = BorderStroke(1.dp, ButtonBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = "›", color = GlowBlueBright, fontSize = 18.sp)
        }
    }
}

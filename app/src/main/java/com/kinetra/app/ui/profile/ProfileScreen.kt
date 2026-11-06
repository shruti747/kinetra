package com.kinetra.app.ui.profile

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.*
import com.kinetra.app.ui.theme.*

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onOrganizerClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onEditProfileClick: () -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    // Trigger profile refresh when screen appears
    LaunchedEffect(Unit) {
        viewModel.loadProfile()
    }

    val profileBitmap = remember(state.profilePhoto) {
        if (state.profilePhoto.isNotEmpty()) {
            runCatching {
                val parsedUri = Uri.parse(state.profilePhoto)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    val source = android.graphics.ImageDecoder.createSource(context.contentResolver, parsedUri)
                    android.graphics.ImageDecoder.decodeBitmap(source)
                } else {
                    @Suppress("DEPRECATION")
                    MediaStore.Images.Media.getBitmap(context.contentResolver, parsedUri)
                }
            }.getOrNull()
        } else null
    }

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
            // Header Bar
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

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Edit Profile quick button
                    Box(
                        modifier = Modifier
                            .clickable { onEditProfileClick() }
                            .background(ButtonBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = ElectricCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Edit",
                                color = TextWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Notifications button
                    Box(
                        modifier = Modifier
                            .clickable { onNotificationsClick() }
                            .background(ButtonBackground, RoundedCornerShape(12.dp))
                            .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = if (state.unreadNotificationsCount > 0) ElectricCyan else TextGray,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${state.unreadNotificationsCount}",
                                color = if (state.unreadNotificationsCount > 0) ElectricCyan else TextGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Athlete Identity Card with Circular Photo
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                borderColor = CardBorder
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(ButtonBackground)
                            .border(2.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (profileBitmap != null) {
                            Image(
                                bitmap = profileBitmap.asImageBitmap(),
                                contentDescription = "Profile Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val initials = state.name.split(" ")
                                .filter { it.isNotBlank() }
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercase() }
                                .joinToString("")
                                .ifEmpty { "K" }

                            Text(
                                text = initials,
                                color = ElectricCyan,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.name,
                            color = TextWhite,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = state.email,
                            color = TextGray,
                            fontSize = 12.sp
                        )
                        if (state.phone.isNotEmpty()) {
                            Text(
                                text = state.phone,
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${state.location} · ${state.sports}",
                            color = GlowBlueBright,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (state.bio.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.bio,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 18.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = ButtonBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "NEXT GOAL", color = TextGray, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(text = state.nextGoal, color = TextWhite, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                    Text(
                        text = state.weeklyKm,
                        color = ElectricCyan,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            // Personal Records Card
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = CardBackground,
                borderColor = CardBorder
            ) {
                Text(text = "PERSONAL RECORDS", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(12.dp))
                state.personalRecords.forEach { (distance, time) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = distance, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                        Text(text = time, color = TextGray, fontSize = 13.sp)
                    }
                }
            }

            // Navigation Links
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileMenuButton(title = "Edit Athlete Profile", onClick = onEditProfileClick)
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
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = ButtonBackground),
        border = BorderStroke(1.dp, ButtonBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title, color = TextWhite, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Text(text = "›", color = GlowBlueBright, fontSize = 18.sp)
        }
    }
}

package com.kinetra.app.ui.profile

import android.graphics.Bitmap
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
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
    val context = LocalContext.current

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

    ProfileScreenContent(
        state = state,
        profileBitmap = profileBitmap,
        onHistoryClick = onHistoryClick,
        onSettingsClick = onSettingsClick,
        onOrganizerClick = onOrganizerClick,
        onNotificationsClick = onNotificationsClick,
        onEditProfileClick = onEditProfileClick
    )
}

@Composable
internal fun ProfileScreenContent(
    state: ProfileUiState,
    profileBitmap: Bitmap?,
    onHistoryClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onOrganizerClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onEditProfileClick: () -> Unit
) {
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
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "KINETRA · ATHLETE",
                        color = BrandAccentText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.4.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = state.name.ifBlank { "Athlete Profile" },
                        color = TextWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.3).sp
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ButtonBackground)
                            .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                            .clickable { onEditProfileClick() }
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Edit,
                                contentDescription = "Edit Profile",
                                tint = BrandAccentText,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Edit",
                                color = TextWhite,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(ButtonBackground)
                            .border(1.dp, ButtonBorder, RoundedCornerShape(12.dp))
                            .clickable { onNotificationsClick() }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Filled.Notifications,
                                contentDescription = "Notifications",
                                tint = if (state.unreadNotificationsCount > 0) BrandAccentText else TextGray,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${state.unreadNotificationsCount}",
                                color = if (state.unreadNotificationsCount > 0) BrandAccentText else TextGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Athlete Identity Card
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(AccentTintSurface)
                            .border(2.dp, BrandPrimary, CircleShape),
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
                                color = BrandAccentText,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = state.name.ifBlank { "KINETRA Athlete" },
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
                        val meta = listOf(state.location, state.sports).filter { it.isNotBlank() }.joinToString(" · ")
                        if (meta.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(5.dp))
                            Text(
                                text = meta,
                                color = BrandAccentText,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                if (state.bio.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.bio,
                        color = TextSecondary,
                        fontSize = 13.sp,
                        lineHeight = 19.sp
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = CardBorderSubtle)
                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "CURRENT GOAL",
                            color = TextGray,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = state.nextGoal,
                            color = TextWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    KinetraBadge(
                        text = state.weeklyKm,
                        textColor = OnAccent,
                        backgroundColor = BrandPrimary,
                        borderColor = BrandPrimary
                    )
                }
            }

            // Personal Records Card
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                SectionHeader(title = "Personal Records")
                Spacer(modifier = Modifier.height(12.dp))
                state.personalRecords.forEachIndexed { idx, (distance, time) ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = distance,
                            color = TextWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = time,
                            color = if (time.contains("No record", ignoreCase = true)) TextGray else BrandAccentText,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    if (idx < state.personalRecords.lastIndex) {
                        HorizontalDivider(color = CardBorderSubtle)
                    }
                }
            }

            // Navigation Links
            SectionHeader(title = "Account & Tools")

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                ProfileMenuButton(title = "Edit Athlete Profile", icon = Icons.Filled.Person, onClick = onEditProfileClick)
                ProfileMenuButton(title = "Event History & Race Log", icon = Icons.Filled.EmojiEvents, onClick = onHistoryClick)
                ProfileMenuButton(title = "Settings & Appearance", icon = Icons.Filled.Settings, onClick = onSettingsClick)
                ProfileMenuButton(title = "Organizer Dashboard", icon = Icons.Filled.Dashboard, onClick = onOrganizerClick)
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun ProfileMenuButton(
    title: String,
    onClick: () -> Unit,
    icon: ImageVector? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (icon != null) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AccentTintSurface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = null,
                            tint = BrandAccentText,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = BrandAccentText,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

private val previewProfileState = ProfileUiState(
    name = "Alex Morgan",
    email = "alex.morgan@kinetra.app",
    phone = "+44 7700 900123",
    location = "London, UK",
    sports = "Running · Cycling",
    bio = "Training for sub-45 10K and spring half marathon.",
    weeklyKm = "28.5 km",
    nextGoal = "35 km Weekly Distance",
    personalRecords = listOf(
        "5K" to "18:42",
        "10K" to "46:12",
        "Half Marathon" to "1:38:04"
    ),
    unreadNotificationsCount = 2
)

@Preview(showBackground = true, name = "Profile Screen - Light")
@Composable
private fun ProfileScreenLightPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.LIGHT)) {
        ProfileScreenContent(
            state = previewProfileState,
            profileBitmap = null,
            onHistoryClick = {},
            onSettingsClick = {},
            onOrganizerClick = {},
            onNotificationsClick = {},
            onEditProfileClick = {}
        )
    }
}

@Preview(showBackground = true, name = "Profile Screen - Dark")
@Composable
private fun ProfileScreenDarkPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.DARK)) {
        ProfileScreenContent(
            state = previewProfileState,
            profileBitmap = null,
            onHistoryClick = {},
            onSettingsClick = {},
            onOrganizerClick = {},
            onNotificationsClick = {},
            onEditProfileClick = {}
        )
    }
}

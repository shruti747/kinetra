package com.kinetra.app.ui.profile

import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.*
import com.kinetra.app.ui.theme.*

@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel,
    onBack: () -> Unit,
    onSaveSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var name by remember { mutableStateOf(state.name) }
    var email by remember { mutableStateOf(state.email) }
    var phone by remember { mutableStateOf(state.phone) }
    var location by remember { mutableStateOf(state.location) }
    var bio by remember { mutableStateOf(state.bio) }
    var selectedSports by remember { mutableStateOf(state.sports) }
    var profilePhotoUri by remember { mutableStateOf(state.profilePhoto) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var generalError by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            profilePhotoUri = uri.toString()
        }
    }

    val profileBitmap = remember(profilePhotoUri) {
        if (profilePhotoUri.isNotEmpty()) {
            runCatching {
                val parsedUri = Uri.parse(profilePhotoUri)
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

    val availableSports = listOf("Running", "Cycling", "Walking", "Marathon", "Trail Running")

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
                title = "Edit Profile",
                subtitle = "ATHLETE IDENTITY",
                onBack = onBack,
                trailingContent = {
                    Text(
                        text = "Cancel",
                        color = TextGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.clickable { onBack() }
                    )
                }
            )

            // Circular Profile Photo Picker
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(96.dp)
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
                            val initials = name.split(" ")
                                .filter { it.isNotBlank() }
                                .take(2)
                                .mapNotNull { it.firstOrNull()?.uppercase() }
                                .joinToString("")
                                .ifEmpty { "K" }

                            Text(
                                text = initials,
                                color = BrandAccentText,
                                fontSize = 30.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        KinetraOutlinedButton(
                            text = "Change Photo",
                            onClick = {
                                photoPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            height = 40.dp,
                            leadingIcon = Icons.Filled.PhotoCamera
                        )

                        if (profilePhotoUri.isNotEmpty()) {
                            KinetraOutlinedButton(
                                text = "Remove",
                                onClick = { profilePhotoUri = "" },
                                height = 40.dp,
                                borderColor = AthleticRed.copy(alpha = 0.45f),
                                contentColor = AthleticRed
                            )
                        }
                    }
                }
            }

            // Input Fields
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    KinetraTextField(
                        value = name,
                        onValueChange = {
                            name = it
                            nameError = null
                        },
                        label = "Full Name",
                        placeholder = "e.g. Alex Morgan",
                        leadingIcon = Icons.Filled.Person,
                        errorMessage = nameError
                    )

                    KinetraTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = null
                        },
                        label = "Email Address",
                        placeholder = "athlete@kinetra.com",
                        leadingIcon = Icons.Filled.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        errorMessage = emailError
                    )

                    KinetraTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = "Phone Number",
                        placeholder = "+44 7700 900123",
                        leadingIcon = Icons.Filled.Phone,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
                    )

                    KinetraTextField(
                        value = location,
                        onValueChange = { location = it },
                        label = "Location / City",
                        placeholder = "e.g. London, UK",
                        leadingIcon = Icons.Filled.LocationOn
                    )

                    KinetraTextField(
                        value = bio,
                        onValueChange = { bio = it },
                        label = "Athlete Bio",
                        placeholder = "Tell fellow athletes about your goals...",
                        leadingIcon = Icons.Filled.Info,
                        singleLine = false
                    )
                }
            }

            // Preferred Sports Selector
            KinetraCard(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp)
            ) {
                Text(
                    text = "PREFERRED SPORTS",
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    availableSports.forEach { sport ->
                        val isSelected = selectedSports.contains(sport, ignoreCase = true)
                        KinetraBadge(
                            text = sport,
                            textColor = if (isSelected) OnAccent else TextSecondary,
                            backgroundColor = if (isSelected) BrandPrimary else BackgroundDark,
                            borderColor = if (isSelected) BrandPrimary else ButtonBorder,
                            modifier = Modifier.clickable {
                                selectedSports = if (isSelected) {
                                    selectedSports.replace(sport, "").replace("· ·", "·").trim(' ', '·')
                                } else {
                                    if (selectedSports.isEmpty()) sport else "$selectedSports · $sport"
                                }
                            }
                        )
                    }
                }
            }

            AnimatedVisibility(visible = generalError != null || state.errorMessage != null) {
                Text(
                    text = generalError ?: state.errorMessage.orEmpty(),
                    color = AthleticRed,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            KinetraButton(
                text = "Save Changes",
                onClick = {
                    nameError = null
                    emailError = null
                    generalError = null

                    if (name.isBlank()) {
                        nameError = "Athlete name cannot be empty."
                        return@KinetraButton
                    }
                    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()) {
                        emailError = "Please enter a valid email address."
                        return@KinetraButton
                    }

                    viewModel.updateProfile(
                        name = name,
                        email = email,
                        phone = phone,
                        bio = bio,
                        location = location,
                        sports = selectedSports,
                        profilePhotoUri = profilePhotoUri,
                        context = context,
                        onSuccess = { onSaveSuccess() },
                        onError = { err -> generalError = err }
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                isLoading = state.isSaving
            )

            KinetraOutlinedButton(
                text = "Cancel",
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

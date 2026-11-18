package com.kinetra.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.*
import com.kinetra.app.ui.theme.*

@Composable
fun PasswordRecoveryScreen(
    viewModel: AuthViewModel,
    onBackToSignIn: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var linkSent by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KinetraBrandLockup(badgeSize = 34.dp, wordmarkSizeSp = 15f)
                    KinetraIconButton(
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        onClick = onBackToSignIn,
                        contentDescription = "Back",
                        size = 40.dp
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                Text(
                    text = "Reset. Refocus.",
                    color = TextWhite,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Enter your email address to preview KINETRA account recovery instructions.",
                    color = TextGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(28.dp))

                KinetraCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    KinetraTextField(
                        value = state.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = "Email address",
                        placeholder = "athlete@kinetra.com",
                        leadingIcon = Icons.Filled.Email,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                KinetraButton(
                    text = "Send reset link",
                    onClick = { linkSent = true },
                    modifier = Modifier.fillMaxWidth()
                )

                if (linkSent) {
                    Spacer(modifier = Modifier.height(20.dp))
                    KinetraCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = AccentTintSurface,
                        borderColor = BrandPrimary.copy(alpha = 0.25f),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        Text(
                            text = "RESET LINK PREVIEW",
                            color = BrandAccentText,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Recovery instructions are simulated for ${state.email.ifBlank { "your account" }}. No real reset link is generated.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Back to sign in",
                    color = BrandAccentText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onBackToSignIn() }
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Preview mode only. No email was sent.",
                    color = TextGray,
                    fontSize = 11.sp
                )
            }
        }
    }
}

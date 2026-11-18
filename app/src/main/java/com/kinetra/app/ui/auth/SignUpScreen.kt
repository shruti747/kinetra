package com.kinetra.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.*
import com.kinetra.app.ui.theme.*

@Composable
fun SignUpScreen(
    viewModel: AuthViewModel,
    onSignUpSuccess: () -> Unit,
    onSignInClick: () -> Unit,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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
                .navigationBarsPadding()
                .verticalScroll(scrollState),
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
                        onClick = onBack,
                        contentDescription = "Back",
                        size = 40.dp
                    )
                }

                Spacer(modifier = Modifier.height(26.dp))

                Text(
                    text = "Create your account.",
                    color = TextWhite,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.4).sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Start tracking your workouts with your own private KINETRA athlete profile.",
                    color = TextGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(22.dp))

                KinetraCard(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = PaddingValues(18.dp)
                ) {
                    KinetraTextField(
                        value = state.fullName,
                        onValueChange = { viewModel.updateFullName(it) },
                        label = "Full name",
                        placeholder = "e.g. Alex Morgan",
                        leadingIcon = Icons.Filled.Person,
                        enabled = !state.isLoading
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    KinetraTextField(
                        value = state.email,
                        onValueChange = { viewModel.updateEmail(it) },
                        label = "Email address",
                        placeholder = "athlete@kinetra.com",
                        leadingIcon = Icons.Filled.Email,
                        enabled = !state.isLoading,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    KinetraTextField(
                        value = state.password,
                        onValueChange = { viewModel.updatePassword(it) },
                        label = "Password (min 8 characters)",
                        placeholder = "Create a strong password",
                        leadingIcon = Icons.Filled.Lock,
                        isPassword = true,
                        enabled = !state.isLoading
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    KinetraTextField(
                        value = state.confirmPassword,
                        onValueChange = { viewModel.updateConfirmPassword(it) },
                        label = "Confirm password",
                        placeholder = "Re-enter your password",
                        leadingIcon = Icons.Filled.Lock,
                        isPassword = true,
                        enabled = !state.isLoading
                    )
                }

                if (state.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = state.errorMessage.orEmpty(),
                        color = AthleticRed,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                KinetraButton(
                    text = "Create account",
                    onClick = { viewModel.register(context = context, onSuccess = onSignUpSuccess) },
                    enabled = !state.isLoading,
                    isLoading = state.isLoading,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Already have an account? ",
                        color = TextGray,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Sign in",
                        color = BrandAccentText,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.clickable { onSignInClick() }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

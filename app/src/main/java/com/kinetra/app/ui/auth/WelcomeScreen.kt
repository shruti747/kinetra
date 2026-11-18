package com.kinetra.app.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraAppBadge
import com.kinetra.app.ui.components.KinetraButton
import com.kinetra.app.ui.components.KinetraOutlinedButton
import com.kinetra.app.ui.theme.*

@Composable
fun WelcomeScreen(
    onCreateAccountClick: () -> Unit,
    onSignInClick: () -> Unit,
    onGoogleSignInClick: () -> Unit,
    onDemoSkip: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Official Brand Identity
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 24.dp)
            ) {
                KinetraAppBadge(size = 64.dp, cornerRadius = 16.dp)
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "KINETRA",
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 5.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "SPORTS & FITNESS",
                    color = BrandAccentText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 2.5.sp
                )
            }

            // Center Editorial Hero
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "TRAIN WITH\nPRECISION.\nOWN EVERY MILE.",
                    color = TextWhite,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center,
                    lineHeight = 40.sp,
                    letterSpacing = (-0.5).sp
                )
                Spacer(modifier = Modifier.height(14.dp))
                Text(
                    text = "Track walking, running and cycling with live GPS telemetry, race-day tools and performance insights.",
                    color = TextGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 21.sp
                )
            }

            // Primary & Secondary Actions
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                KinetraButton(
                    text = "Create an account",
                    onClick = onCreateAccountClick,
                    modifier = Modifier.fillMaxWidth()
                )

                KinetraOutlinedButton(
                    text = "Sign in",
                    onClick = onSignInClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorder)
                    Text(text = "OR", color = TextGray, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    HorizontalDivider(modifier = Modifier.weight(1f), color = CardBorder)
                }

                KinetraOutlinedButton(
                    text = "Continue with Google",
                    onClick = onGoogleSignInClick,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Already on the team? Sign in",
                    color = BrandAccentText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable { onSignInClick() }
                )

                Text(
                    text = "Sign in with your KINETRA account to sync your personal workouts.",
                    color = TextGray,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .padding(top = 4.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "Welcome - Light")
@Composable
private fun WelcomeScreenLightPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.LIGHT)) {
        WelcomeScreen({}, {}, {}, {})
    }
}

@Preview(showBackground = true, name = "Welcome - Dark")
@Composable
private fun WelcomeScreenDarkPreview() {
    KinetraTheme(overrideSettings = AppearanceSettings(themeMode = ThemeMode.DARK)) {
        WelcomeScreen({}, {}, {}, {})
    }
}

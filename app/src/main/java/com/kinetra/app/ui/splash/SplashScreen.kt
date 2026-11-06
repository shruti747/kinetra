package com.kinetra.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.R
import com.kinetra.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Premium KINETRA Animated Launch Splash Screen.
 * Features:
 * - Deep midnight navy background (#050811)
 * - Atmospheric electric blue fog gradients at top and bottom
 * - Smooth upward slide + fade-in for the K emblem
 * - Gentle horizontal slide + fade-in for the KINETRA wordmark
 * - Clean fade-in for the SPORTS & FITNESS tagline
 * - Automatic transition (~800 ms total) to the Home screen
 */
@Composable
fun KinetraAnimatedSplashScreen(
    onAnimationComplete: () -> Unit
) {
    // Animation driver states
    val emblemAlpha = remember { Animatable(0f) }
    val emblemOffsetY = remember { Animatable(24f) }

    val wordmarkAlpha = remember { Animatable(0f) }
    val wordmarkOffsetX = remember { Animatable(-30f) }

    val taglineAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        // Step 1: Slide K emblem upward slightly while fading it in (0 - 380 ms)
        launch {
            emblemAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
        launch {
            emblemOffsetY.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )
        }

        delay(160)

        // Step 2: Slide KINETRA wordmark in gently from the left (160 - 540 ms)
        launch {
            wordmarkAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 350, easing = FastOutSlowInEasing)
            )
        }
        launch {
            wordmarkOffsetX.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )
        }

        delay(180)

        // Step 3: Fade in the SPORTS & FITNESS tagline (340 - 640 ms)
        taglineAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
        )

        // Step 4: Brief hold to perceive the full brand mark (~160 ms)
        delay(160)

        // Step 5: Complete and transition to Home
        onAnimationComplete()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // ==========================================
        // TOP BLUE FOG ATMOSPHERE
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A).copy(alpha = 0.55f),
                            Color(0xFF0F172A).copy(alpha = 0.25f),
                            Color.Transparent
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .align(Alignment.TopCenter)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF2563EB).copy(alpha = 0.35f),
                            Color(0xFF1E3A8A).copy(alpha = 0.12f),
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )

        // ==========================================
        // BOTTOM BLUE FOG ATMOSPHERE
        // ==========================================
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0D1B3E).copy(alpha = 0.30f),
                            Color(0xFF1E3A8A).copy(alpha = 0.55f)
                        )
                    )
                )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .align(Alignment.BottomCenter)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            ElectricCyan.copy(alpha = 0.22f),
                            Color(0xFF1E3A8A).copy(alpha = 0.15f),
                            Color.Transparent
                        ),
                        radius = 600f
                    )
                )
        )

        // ==========================================
        // CENTER BRANDING CHOREOGRAPHY
        // ==========================================
        Column(
            modifier = Modifier.align(Alignment.Center),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // 1. K Emblem: upward slide + fade in
            Box(
                modifier = Modifier
                    .offset(y = emblemOffsetY.value.dp)
                    .graphicsLayer { alpha = emblemAlpha.value }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_kinetra_splash_logo),
                    contentDescription = "Kinetra Emblem",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(92.dp)
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            // 2. KINETRA Wordmark: left slide + fade in
            Box(
                modifier = Modifier
                    .offset(x = wordmarkOffsetX.value.dp)
                    .graphicsLayer { alpha = wordmarkAlpha.value }
            ) {
                Text(
                    text = "KINETRA",
                    color = TextWhite,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 6.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. SPORTS & FITNESS Tagline: fade in
            Box(
                modifier = Modifier.graphicsLayer { alpha = taglineAlpha.value }
            ) {
                Text(
                    text = "SPORTS & FITNESS",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 3.5.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ==========================================
// PRESERVED ORIGINAL SPLASH SCREEN COMPOSABLE
// ==========================================
@Composable
fun SplashScreen(
    viewModel: SplashViewModel,
    onDemoClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundDark)
    ) {
        // Center radial glow effect
        Box(
            modifier = Modifier
                .size(350.dp)
                .align(Alignment.Center)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color(0xFF1E3A8A).copy(alpha = 0.5f),
                            Color(0xFF0F172A).copy(alpha = 0.2f),
                            Color.Transparent
                        )
                    )
                )
        )

        // Main layout content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERFORMANCE / REIMAGINED",
                    color = TextGray,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(ButtonBackground)
                        .border(1.dp, ButtonBorder, RoundedCornerShape(50.dp))
                        .clickable {
                            viewModel.onDemoClicked()
                            onDemoClick()
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Demo",
                        color = TextWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Center Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "KINETRA",
                    color = TextWhite,
                    fontSize = 42.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 4.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Make every run count.",
                    color = TextGray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

            // Bottom Content
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Row(
                    modifier = Modifier.width(160.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    for (i in 1..uiState.totalSteps) {
                        val isActive = i <= uiState.currentStep
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(1.5.dp))
                                .background(if (isActive) IndicatorActive else IndicatorInactive)
                        )
                    }
                }

                Text(
                    text = uiState.statusText,
                    color = TextGray,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    textAlign = TextAlign.Center
                )

                Box(
                    modifier = Modifier
                        .width(120.dp)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(Color.White.copy(alpha = 0.3f))
                )
            }
        }
    }
}

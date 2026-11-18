package com.kinetra.app.ui.splash

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.kinetra.app.ui.components.KinetraLogoMark
import com.kinetra.app.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Official KINETRA Splash Screen.
 * - Pure matte black (#0A0A0A) background
 * - Centered deep maroon (#800020) geometric K symbol
 * - Clean, modern uppercase white KINETRA wordmark underneath
 * - Subtle fade-in + scale animation (~820 ms total) with smooth handoff
 */
@Composable
fun KinetraAnimatedSplashScreen(
    onAnimationComplete: () -> Unit
) {
    val emblemAlpha = remember { Animatable(0f) }
    val emblemScale = remember { Animatable(0.88f) }
    val wordmarkAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch {
            emblemAlpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 380, easing = FastOutSlowInEasing)
            )
        }
        launch {
            emblemScale.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 420, easing = FastOutSlowInEasing)
            )
        }

        delay(150)

        wordmarkAlpha.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 320, easing = FastOutSlowInEasing)
        )

        delay(340)
        onAnimationComplete()
    }

    KinetraSplashContent(
        emblemAlpha = emblemAlpha.value,
        emblemScale = emblemScale.value,
        wordmarkAlpha = wordmarkAlpha.value
    )
}

@Composable
private fun KinetraSplashContent(
    emblemAlpha: Float = 1f,
    emblemScale: Float = 1f,
    wordmarkAlpha: Float = 1f
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KinetraBlack),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier.graphicsLayer {
                    alpha = emblemAlpha
                    scaleX = emblemScale
                    scaleY = emblemScale
                }
            ) {
                KinetraLogoMark(
                    size = 96.dp,
                    tint = KinetraMaroon
                )
            }

            Spacer(modifier = Modifier.height(22.dp))

            Column(
                modifier = Modifier.graphicsLayer { alpha = wordmarkAlpha },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "KINETRA",
                    color = KinetraWhite,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 8.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "SPORTS & FITNESS",
                    color = Color(0xFF8C8687),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 3.2.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Preview(showBackground = true, name = "KINETRA Splash Screen")
@Composable
private fun KinetraSplashPreview() {
    KinetraSplashContent()
}

// ==========================================
// PRESERVED LEGACY SPLASH COMPOSABLE (REBRANDED)
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
            .background(KinetraBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PERFORMANCE / REIMAGINED",
                    color = Color(0xFF9E9899),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.5.sp
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color(0xFF171717))
                        .border(1.dp, Color(0xFF2B2526), RoundedCornerShape(50.dp))
                        .clickable {
                            viewModel.onDemoClicked()
                            onDemoClick()
                        }
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Demo",
                        color = KinetraWhite,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                KinetraLogoMark(size = 84.dp, tint = KinetraMaroon)
                Spacer(modifier = Modifier.height(18.dp))
                Text(
                    text = "KINETRA",
                    color = KinetraWhite,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 7.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Make every run count.",
                    color = Color(0xFF9E9899),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }

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
                                .background(if (isActive) KinetraMaroon else Color(0xFF2B2526))
                        )
                    }
                }

                Text(
                    text = uiState.statusText,
                    color = Color(0xFF9E9899),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.2.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

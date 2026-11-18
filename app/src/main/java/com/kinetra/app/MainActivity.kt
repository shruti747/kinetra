package com.kinetra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.kinetra.app.data.SessionManager
import com.kinetra.app.tracking.repository.WorkoutRepository
import com.kinetra.app.ui.main.MainContainer
import com.kinetra.app.ui.splash.KinetraAnimatedSplashScreen
import com.kinetra.app.ui.theme.AppearanceManager
import com.kinetra.app.ui.theme.KinetraTheme
import com.kinetra.app.wearable.WearableManager

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        // Initialize AndroidX SplashScreen for system cold launch
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Restore saved appearance preferences & device-local auth session before first composition
        AppearanceManager.initialize(this)
        SessionManager.initialize(this)

        // Initialize wearable sensor manager & local workout repository
        WearableManager.initialize(this)
        WorkoutRepository.initialize(this)

        setContent {
            var showAnimatedSplash by rememberSaveable { mutableStateOf(true) }

            KinetraTheme(forceDarkSystemBars = showAnimatedSplash) {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    Crossfade(
                        targetState = showAnimatedSplash,
                        animationSpec = tween(durationMillis = 320),
                        label = "splash_to_home_crossfade"
                    ) { isSplash ->
                        if (isSplash) {
                            KinetraAnimatedSplashScreen(
                                onAnimationComplete = { showAnimatedSplash = false }
                            )
                        } else {
                            MainContainer()
                        }
                    }
                }
            }
        }
    }
}

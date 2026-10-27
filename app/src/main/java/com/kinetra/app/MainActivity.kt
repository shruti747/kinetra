package com.kinetra.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.kinetra.app.ui.main.MainScreen
import com.kinetra.app.ui.main.MainViewModel
import com.kinetra.app.ui.splash.SplashScreen
import com.kinetra.app.ui.splash.SplashViewModel
import com.kinetra.app.ui.theme.KinetraTheme

class MainActivity : ComponentActivity() {

    private val splashViewModel: SplashViewModel by viewModels()
    private val mainViewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KinetraTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val splashState by splashViewModel.uiState.collectAsState()
                    var showMain by remember { mutableStateOf(false) }

                    // If splash requests navigation to main or user clicked Demo
                    if (splashState.navigateToMain || showMain) {
                        MainScreen(
                            viewModel = mainViewModel,
                            onBackToSplash = {
                                splashViewModel.resetNavigation()
                                showMain = false
                            }
                        )
                    } else {
                        SplashScreen(
                            viewModel = splashViewModel,
                            onDemoClick = {
                                showMain = true
                            }
                        )
                    }
                }
            }
        }
    }
}

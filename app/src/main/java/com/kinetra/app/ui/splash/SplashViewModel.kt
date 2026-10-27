package com.kinetra.app.ui.splash

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class SplashUiState(
    val currentStep: Int = 3,
    val totalSteps: Int = 4,
    val navigateToMain: Boolean = false,
    val statusText: String = "ATHLETE INTELLIGENCE / V1.0"
)

class SplashViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    fun onDemoClicked() {
        _uiState.update { it.copy(navigateToMain = true) }
    }

    fun resetNavigation() {
        _uiState.update { it.copy(navigateToMain = false) }
    }
}

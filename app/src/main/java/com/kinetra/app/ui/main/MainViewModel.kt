package com.kinetra.app.ui.main

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class MainUiState(
    val userName: String = "Athlete",
    val totalDistance: String = "12.4 km",
    val avgPace: String = "4'42\" /km",
    val activeRunsCount: Int = 3
)

class MainViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(MainUiState())
    val uiState: StateFlow<MainUiState> = _uiState.asStateFlow()
}

package com.kinetra.app.ui.home

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class HomeUiState(
    val athleteName: String = "Alex",
    val dateString: String = "MON 16 NOV 2026",
    val locationString: String = "LONDON, UK",
    val nextRaceName: String = "RIVERSIDE 10K",
    val nextRaceDate: String = "22 NOV 2026 · 09:00",
    val nextRaceLocation: String = "Victoria Park, London",
    val nextRaceFee: String = "£35",
    val weeklyMileage: String = "24.8 km",
    val personalBest: String = "46:12",
    val pbAvgPace: String = "4:46 /km",
    val lastActivityTitle: String = "10 km training run",
    val lastActivityTime: String = "47:36",
    val lastActivityDate: String = "15 NOV",
    val aiRecommendation: String = "Keep race-week effort light. Try 5 km at a conversational pace."
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}

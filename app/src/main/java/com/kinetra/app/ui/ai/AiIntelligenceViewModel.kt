package com.kinetra.app.ui.ai

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class AiIntelligenceUiState(
    val athleteName: String = "Alex Morgan",
    val runDate: String = "16 Nov 2026",
    val time: String = "47:36",
    val distance: String = "10.00 km",
    val avgPace: String = "4:46 /km",
    val elevationGain: String = "+58 m",
    val paceProgression: String = "First 6 km 4:48  →  Last 4 km 4:42 /km",
    val splitSpread: String = "4:52 slowest  /  4:40 fastest",
    val nextEstimate: String = "46:50–48:10",
    val raceWeekFocus: String = "5 km easy, at conversational effort. Keep volume light before 22 Nov."
)

class AiIntelligenceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AiIntelligenceUiState())
    val uiState: StateFlow<AiIntelligenceUiState> = _uiState.asStateFlow()
}

package com.kinetra.app.ui.track

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class TrackState {
    LIVE_GPS,
    PAUSED,
    COMPLETED,
    GHOST_RUNNER
}

data class TrackUiState(
    val currentTrackState: TrackState = TrackState.LIVE_GPS,
    val distance: String = "6.20 km",
    val elapsedTime: String = "29:33",
    val movingSpeed: String = "12.6 km/h",
    val avgPace: String = "4:46 /km",
    val elevationGain: String = "34 m",
    val ghostDiff: String = "~54s behind your PB",
    val splits: List<Pair<String, String>> = listOf(
        "04" to "4:47",
        "05" to "4:46",
        "06" to "4:45"
    )
)

class TrackViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(TrackUiState())
    val uiState: StateFlow<TrackUiState> = _uiState.asStateFlow()

    fun setState(state: TrackState) {
        _uiState.value = _uiState.value.copy(currentTrackState = state)
    }
}

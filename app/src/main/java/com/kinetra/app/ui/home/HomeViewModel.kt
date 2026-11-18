package com.kinetra.app.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetra.app.data.UserSession
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class HomeUiState(
    val athleteName: String = "Athlete",
    val dateString: String = "",
    val locationString: String = "KINETRA",
    val nextRaceName: String = "RIVERSIDE 10K",
    val nextRaceDate: String = "22 NOV 2026 · 09:00",
    val nextRaceLocation: String = "Victoria Park, London",
    val nextRaceFee: String = "£35",
    val weeklyMileage: String = "0.0 km",
    val personalBest: String = "--:--",
    val pbAvgPace: String = "--:-- /km",
    val lastActivityTitle: String = "No recent workout yet",
    val lastActivityTime: String = "--:--",
    val lastActivityDate: String = "TODAY",
    val aiRecommendation: String = "Start a live workout to unlock personalized pace and recovery guidance."
)

class HomeViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState(dateString = todayFormatted()))
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            UserSession.userProfile.collect { profile ->
                val firstName = profile.name
                    .trim()
                    .substringBefore(" ")
                    .ifBlank { "Athlete" }
                val location = profile.location
                    ?.trim()
                    ?.uppercase(Locale.getDefault())
                    .orEmpty()
                    .ifBlank { "KINETRA ATHLETE" }

                _uiState.update {
                    it.copy(
                        athleteName = firstName,
                        dateString = todayFormatted(),
                        locationString = location
                    )
                }
            }
        }
    }

    fun clearUserData() {
        _uiState.value = HomeUiState(dateString = todayFormatted())
    }

    private fun todayFormatted(): String {
        return SimpleDateFormat("EEE dd MMM yyyy", Locale.getDefault())
            .format(Date())
            .uppercase(Locale.getDefault())
    }
}

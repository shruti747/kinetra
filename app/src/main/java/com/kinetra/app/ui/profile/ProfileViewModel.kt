package com.kinetra.app.ui.profile

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class ProfileUiState(
    val name: String = "Alex Morgan",
    val location: String = "London, UK",
    val sports: String = "Running · Cycling",
    val weeklyKm: String = "24.8 km",
    val nextGoal: String = "Break 46 minutes for 10K",
    val personalRecords: List<Pair<String, String>> = listOf(
        "5K" to "18:42 GPS estimate",
        "10K" to "46:12 Official · demo",
        "Half" to "1:38:04 Official · demo"
    ),
    val unreadNotificationsCount: Int = 2
)

class ProfileViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
}

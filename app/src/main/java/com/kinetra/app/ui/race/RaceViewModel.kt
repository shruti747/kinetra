package com.kinetra.app.ui.race

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class RaceUiState(
    val raceTitle: String = "Riverside 10K",
    val raceDate: String = "SUN 22 NOV 2026 · 09:00",
    val location: String = "Victoria Park, London",
    val distance: String = "10 km",
    val fee: String = "£35",
    val elevation: String = "+24 m",
    val description: String = "Fast, flat, two-lap course. Chip timing, water stations and a finisher medal.",
    val participantName: String = "Alex Morgan",
    val emergencyContact: String = "Maya Morgan · +44 7700 900123",
    val registrationReference: String = "KTR-1024"
)

class RaceViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(RaceUiState())
    val uiState: StateFlow<RaceUiState> = _uiState.asStateFlow()
}

package com.kinetra.app.ui.discover

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class EventItem(
    val title: String,
    val category: String,
    val date: String,
    val distance: String,
    val fee: String,
    val location: String,
    val isFeatured: Boolean = false
)

data class DiscoverUiState(
    val searchQuery: String = "",
    val selectedCategory: String = "All sports",
    val events: List<EventItem> = listOf(
        EventItem("RIVERSIDE 10K", "RUNNING", "22 NOV 2026 · 09:00", "10 km", "£35", "Victoria Park, London", true),
        EventItem("London Winter Marathon", "MARATHON", "06 DEC 2026 · 09:00", "42.2 km", "£65", "Lee Valley, London", false),
        EventItem("Richmond Park Cyclathon", "CYCLING", "29 NOV 2026 · 08:30", "60 km", "£40", "Richmond Park, London", false),
        EventItem("Docklands Winter Duathlon", "MULTISPORT", "13 DEC 2026 · 10:00", "Run / bike / run", "£50", "Royal Victoria Dock, London", false)
    )
)

class DiscoverViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(DiscoverUiState())
    val uiState: StateFlow<DiscoverUiState> = _uiState.asStateFlow()

    fun updateSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun selectCategory(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }
}

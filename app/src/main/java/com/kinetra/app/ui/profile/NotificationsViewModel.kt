package com.kinetra.app.ui.profile

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kinetra.app.data.*
import com.kinetra.app.ui.notifications.NotificationHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class NotificationCategory(val displayName: String, val apiCategory: String) {
    ALL("All", "all"),
    REMINDERS("Reminders", "reminder"),
    UPDATES("Updates", "update"),
    ALERTS("Alerts", "alert")
}

data class NotificationsUiState(
    val notifications: List<NotificationItem> = emptyList(),
    val selectedCategory: NotificationCategory = NotificationCategory.ALL,
    val unreadCount: Int = 0,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
) {
    val filteredList: List<NotificationItem>
        get() = when (selectedCategory) {
            NotificationCategory.ALL -> notifications
            else -> notifications.filter { it.category.equals(selectedCategory.apiCategory, ignoreCase = true) }
        }
}

class NotificationsViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState: StateFlow<NotificationsUiState> = _uiState.asStateFlow()

    init {
        loadNotifications()
    }

    fun loadNotifications() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = UserSession.currentUserId

            try {
                val response = RetrofitClient.kinetraApi.getNotifications(userId)
                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()?.notifications ?: emptyList()
                    val unread = response.body()?.unread_count ?: list.count { !it.is_read }
                    _uiState.update {
                        it.copy(
                            notifications = list,
                            unreadCount = unread,
                            isLoading = false
                        )
                    }
                } else {
                    // Seed fallback if response was empty or error
                    fallbackToLocalNotifications()
                }
            } catch (e: Exception) {
                fallbackToLocalNotifications()
            }
        }
    }

    private fun fallbackToLocalNotifications() {
        val sampleList = listOf(
            NotificationItem(
                id = 1,
                title = "Morning 5K Training Reminder",
                message = "Keep your race-week pace steady. Victoria Park 10K is in 6 days.",
                category = "reminder",
                is_read = false,
                created_at = "Today, 08:30"
            ),
            NotificationItem(
                id = 2,
                title = "New Personal Record Unlocked!",
                message = "Congratulations! You set a new 10K PB: 46:12 in your last timed trial.",
                category = "update",
                is_read = false,
                created_at = "Yesterday, 17:45"
            ),
            NotificationItem(
                id = 3,
                title = "Account Security Alert",
                message = "Your Kinetra athlete profile was successfully verified on your OPPO CPH2603.",
                category = "alert",
                is_read = false,
                created_at = "2 days ago"
            ),
            NotificationItem(
                id = 4,
                title = "Weekly Mileage Goal Progress",
                message = "You have completed 24.8 km of your 35 km weekly goal. Just 10.2 km left to hit your target!",
                category = "reminder",
                is_read = true,
                created_at = "3 days ago"
            )
        )
        _uiState.update {
            it.copy(
                notifications = sampleList,
                unreadCount = sampleList.count { item -> !item.is_read },
                isLoading = false
            )
        }
    }

    fun selectCategory(category: NotificationCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun markAsRead(notificationId: Int) {
        viewModelScope.launch {
            val userId = UserSession.currentUserId

            // Optimistic UI update
            _uiState.update { current ->
                val updated = current.notifications.map {
                    if (it.id == notificationId) it.copy(is_read = true) else it
                }
                current.copy(
                    notifications = updated,
                    unreadCount = updated.count { !it.is_read }
                )
            }

            try {
                RetrofitClient.kinetraApi.markNotificationRead(
                    MarkReadRequest(user_id = userId, notification_id = notificationId)
                )
            } catch (_: Exception) {
                // Keep optimistic state
            }
        }
    }

    fun markAllAsRead() {
        viewModelScope.launch {
            val userId = UserSession.currentUserId

            // Optimistic update
            _uiState.update { current ->
                val updated = current.notifications.map { it.copy(is_read = true) }
                current.copy(
                    notifications = updated,
                    unreadCount = 0
                )
            }

            try {
                RetrofitClient.kinetraApi.markNotificationRead(
                    MarkReadRequest(user_id = userId, mark_all = true)
                )
            } catch (_: Exception) {
                // Keep optimistic state
            }
        }
    }

    fun triggerDeviceNotification(context: Context, item: NotificationItem) {
        NotificationHelper.showSystemNotification(
            context = context,
            title = item.title,
            message = item.message,
            notificationId = item.id
        )
    }
}


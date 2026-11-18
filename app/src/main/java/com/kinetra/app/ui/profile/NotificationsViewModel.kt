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

    fun loadNotifications() {
        if (!SessionManager.isAuthenticated()) {
            clearUserData()
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val userId = SessionManager.getCurrentUserId()
            if (userId <= 0) {
                clearUserData()
                return@launch
            }

            try {
                val response = RetrofitClient.kinetraApi.getNotifications(userId)
                if (response.code() == 401 || response.code() == 403) {
                    SessionManager.clearSession()
                    clearUserData()
                    return@launch
                }

                if (response.isSuccessful && response.body()?.success == true) {
                    val list = response.body()?.notifications ?: emptyList()
                    val unread = response.body()?.unread_count ?: list.count { !it.is_read }
                    _uiState.update {
                        it.copy(
                            notifications = list,
                            unreadCount = unread,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _uiState.update {
                        it.copy(
                            notifications = emptyList(),
                            unreadCount = 0,
                            isLoading = false
                        )
                    }
                }
            } catch (_: Exception) {
                // Never inject another user's sample notifications when offline
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Unable to refresh notifications right now."
                    )
                }
            }
        }
    }

    fun clearUserData() {
        _uiState.value = NotificationsUiState()
    }

    fun selectCategory(category: NotificationCategory) {
        _uiState.update { it.copy(selectedCategory = category) }
    }

    fun markAsRead(notificationId: Int) {
        if (!SessionManager.isAuthenticated()) return
        viewModelScope.launch {
            val userId = SessionManager.getCurrentUserId()
            if (userId <= 0) return@launch

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
                val response = RetrofitClient.kinetraApi.markNotificationRead(
                    MarkReadRequest(user_id = userId, notification_id = notificationId)
                )
                if (response.code() == 401 || response.code() == 403) {
                    SessionManager.clearSession()
                    clearUserData()
                }
            } catch (_: Exception) {
                // Keep optimistic state when offline
            }
        }
    }

    fun markAllAsRead() {
        if (!SessionManager.isAuthenticated()) return
        viewModelScope.launch {
            val userId = SessionManager.getCurrentUserId()
            if (userId <= 0) return@launch

            _uiState.update { current ->
                val updated = current.notifications.map { it.copy(is_read = true) }
                current.copy(
                    notifications = updated,
                    unreadCount = 0
                )
            }

            try {
                val response = RetrofitClient.kinetraApi.markNotificationRead(
                    MarkReadRequest(user_id = userId, mark_all = true)
                )
                if (response.code() == 401 || response.code() == 403) {
                    SessionManager.clearSession()
                    clearUserData()
                }
            } catch (_: Exception) {
                // Keep optimistic state when offline
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

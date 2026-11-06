package com.kinetra.app.data

data class NotificationItem(
    val id: Int,
    val title: String,
    val message: String,
    val category: String, // "reminder", "update", "alert"
    val is_read: Boolean,
    val created_at: String
)

data class NotificationListResponse(
    val success: Boolean,
    val unread_count: Int = 0,
    val notifications: List<NotificationItem>? = null,
    val message: String? = null
)

data class MarkReadRequest(
    val user_id: Int,
    val notification_id: Int? = null,
    val mark_all: Boolean = false
)

data class BaseResponse(
    val success: Boolean,
    val message: String? = null
)


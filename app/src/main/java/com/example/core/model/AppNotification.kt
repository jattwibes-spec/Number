package com.example.core.model

enum class NotificationType {
    VERSION_UPDATE,
    MAINTENANCE,
    SECURITY,
    ACCOUNT,
    ANNOUNCEMENT
}

data class AppNotification(
    val id: String,
    val title: String,
    val body: String,
    val type: NotificationType,
    val timestamp: String,
    val isRead: Boolean = false,
    val actionUrl: String? = null
)

data class SearchHistoryItem(
    val id: String,
    val query: String,
    val category: SearchCategory,
    val resultCount: Int,
    val timestamp: String
)

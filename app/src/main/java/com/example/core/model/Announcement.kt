package com.example.core.model

enum class AnnouncementPriority {
    INFO,
    WARNING,
    CRITICAL
}

data class Announcement(
    val id: String,
    val title: String,
    val content: String,
    val priority: AnnouncementPriority = AnnouncementPriority.INFO,
    val date: String,
    val isMaintenanceNotice: Boolean = false,
    val author: String = "SecOps Administration"
)

data class AppVersion(
    val currentVersion: String = "2.4.0",
    val latestVersion: String = "2.5.0",
    val minSupportedVersion: String = "2.2.0",
    val releaseNotes: String = "• Upgraded query speed with DuckDB 1.1 remote caching\n• Enhanced privacy masking filter algorithm\n• Security audit trail live sync with FastAPI\n• Modernized dark glassmorphic dashboard",
    val isMandatoryUpdate: Boolean = false,
    val isMaintenanceMode: Boolean = false,
    val updateUrl: String = "https://play.google.com/store/apps/details?id=com.aistudio.searchplatform.qxrvpt"
)

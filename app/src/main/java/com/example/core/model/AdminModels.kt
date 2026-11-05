package com.example.core.model

data class AdminStats(
    val totalRegisteredUsers: Int = 1428,
    val activeUsers: Int = 1385,
    val suspendedUsers: Int = 43,
    val totalSearchesToday: Int = 8942,
    val apiRequestsToday: Int = 34912,
    val avgResponseTimeMs: Int = 68,
    val recentSecurityAlerts: Int = 3,
    val currentDuckDbCacheHitRate: Float = 94.6f
)

data class ManagedUser(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole,
    val status: AccountStatus,
    val dailyQuota: Int,
    val monthlyQuota: Int,
    val searchesUsedToday: Int,
    val totalSearches: Int,
    val lastActive: String,
    val registeredDate: String,
    val activeSessionsCount: Int = 1
)

data class AuditLog(
    val id: String,
    val timestamp: String,
    val actor: String,
    val action: String,
    val target: String,
    val details: String,
    val ipAddress: String = "192.168.1.104"
)

data class SecurityEvent(
    val id: String,
    val timestamp: String,
    val eventType: String,
    val severity: AnnouncementPriority,
    val description: String,
    val resolved: Boolean = false
)

data class SearchSettings(
    val globalDailySearchLimit: Int = 200,
    val apiRateLimitPerMinute: Int = 60,
    val maxResultsPerQuery: Int = 50,
    val abuseDetectionEnabled: Boolean = true,
    val requestThrottlingEnabled: Boolean = true
)

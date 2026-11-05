package com.example.core.model

enum class UserRole {
    USER,
    ADMIN
}

enum class AccountStatus {
    ACTIVE,
    SUSPENDED,
    PENDING_VERIFICATION
}

data class User(
    val id: String,
    val name: String,
    val email: String,
    val role: UserRole = UserRole.USER,
    val status: AccountStatus = AccountStatus.ACTIVE,
    val dailyQuota: Int = 100,
    val searchesUsedToday: Int = 14,
    val totalSearches: Int = 342,
    val emailVerified: Boolean = true,
    val organization: String = "Enterprise Intelligence Unit",
    val department: String = "Cyber Investigations",
    val memberSince: String = "Jan 2026",
    val token: String = "jwt.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IlVzZXIiLCJpYXQiOjE1MTYyMzkwMjJ9"
) {
    val remainingDailySearches: Int
        get() = (dailyQuota - searchesUsedToday).coerceAtLeast(0)

    val quotaUsageFraction: Float
        get() = if (dailyQuota > 0) searchesUsedToday.toFloat() / dailyQuota.toFloat() else 0f
}

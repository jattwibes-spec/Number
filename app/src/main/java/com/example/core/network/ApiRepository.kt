package com.example.core.network

import android.content.Context
import com.example.core.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ApiRepository(private val context: Context) {
    val client = ApiClient(context)

    // In-memory persistent states for the frontend
    private val _currentUser = MutableStateFlow<User?>(
        User(
            id = "USR-9921",
            name = "Alex Vance",
            email = "demo@nexus.io",
            role = UserRole.USER,
            status = AccountStatus.ACTIVE,
            dailyQuota = 100,
            searchesUsedToday = 18,
            totalSearches = 482,
            emailVerified = true,
            organization = "Global Compliance Directorate",
            department = "Authorized Registry Verification"
        )
    )
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _adminStats = MutableStateFlow(AdminStats())
    val adminStats: StateFlow<AdminStats> = _adminStats.asStateFlow()

    private val _managedUsers = MutableStateFlow(
        listOf(
            ManagedUser("USR-9921", "Alex Vance", "demo@nexus.io", UserRole.USER, AccountStatus.ACTIVE, 100, 2500, 18, 482, "Just now", "2025-11-10"),
            ManagedUser("USR-1004", "Marcus Thorne", "m.thorne@cyber.gov", UserRole.ADMIN, AccountStatus.ACTIVE, 500, 10000, 42, 1920, "10m ago", "2024-03-01"),
            ManagedUser("USR-3392", "Elena Rostova", "elena.r@datacorp.org", UserRole.USER, AccountStatus.ACTIVE, 80, 1500, 79, 810, "1h ago", "2025-06-22"),
            ManagedUser("USR-7714", "Devon Miles", "devon.m@infosec.net", UserRole.USER, AccountStatus.SUSPENDED, 50, 1000, 50, 240, "3d ago", "2025-09-14"),
            ManagedUser("USR-8820", "Priya Sharma", "priya.s@authregistry.in", UserRole.USER, AccountStatus.ACTIVE, 150, 3000, 12, 650, "4h ago", "2025-01-18"),
            ManagedUser("USR-6031", "Liam O'Connor", "loconnor@investigate.eu", UserRole.USER, AccountStatus.ACTIVE, 100, 2000, 31, 395, "Yesterday", "2025-08-04")
        )
    )
    val managedUsers: StateFlow<List<ManagedUser>> = _managedUsers.asStateFlow()

    private val _announcements = MutableStateFlow(
        listOf(
            Announcement(
                id = "ANN-1",
                title = "FastAPI DuckDB 1.1 Migration Complete",
                content = "Remote query engine optimized with Parquet dictionary encoding. Search latency reduced by 42%.",
                priority = AnnouncementPriority.INFO,
                date = "Today, 04:30 AM",
                isMaintenanceNotice = false
            ),
            Announcement(
                id = "ANN-2",
                title = "Scheduled Gateway Maintenance",
                content = "The remote Parquet indices will undergo automated defragmentation on Sunday 02:00 UTC. Expect slight rate adjustments.",
                priority = AnnouncementPriority.WARNING,
                date = "Yesterday",
                isMaintenanceNotice = true
            ),
            Announcement(
                id = "ANN-3",
                title = "Mandatory Data Privacy Compliance Notice",
                content = "All queries against Consented Directory records are strictly logged. Aadhaar & personal telephone masking is enforced at the server boundary.",
                priority = AnnouncementPriority.CRITICAL,
                date = "Nov 02",
                isMaintenanceNotice = false
            )
        )
    )
    val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

    private val _appVersion = MutableStateFlow(AppVersion())
    val appVersion: StateFlow<AppVersion> = _appVersion.asStateFlow()

    private val _auditLogs = MutableStateFlow(
        listOf(
            AuditLog("AUD-901", "10m ago", "admin@nexus.io", "USER_QUOTA_UPDATE", "USR-3392", "Increased daily limit from 50 to 80"),
            AuditLog("AUD-902", "1h ago", "admin@nexus.io", "ACCOUNT_SUSPEND", "USR-7714", "Flagged for anomalous query pattern"),
            AuditLog("AUD-903", "3h ago", "system", "REMOTE_CACHE_PURGE", "DuckDB_Pool_02", "Routine index sync complete"),
            AuditLog("AUD-904", "Yesterday", "admin@nexus.io", "POLICY_ENFORCE", "Global", "Enforced strict DPDP/GDPR phone masking")
        )
    )
    val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

    private val _notifications = MutableStateFlow(
        listOf(
            AppNotification("NOT-1", "Security Compliance Update", "Your session key was refreshed successfully with standard OAuth/JWT rotation.", NotificationType.SECURITY, "12m ago"),
            AppNotification("NOT-2", "New Engine Release 2.5.0", "DuckDB Parquet query engine v2.5.0 is now live on the backend with faster indexing.", NotificationType.VERSION_UPDATE, "2h ago"),
            AppNotification("NOT-3", "Search Quota Warning", "You have utilized 85% of your allocated daily query budget.", NotificationType.ACCOUNT, "5h ago")
        )
    )
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    private val _searchHistory = MutableStateFlow(
        listOf(
            SearchHistoryItem("H-1", "Bharat Cyber Resilience", SearchCategory.ORGANIZATION, 3, "25m ago"),
            SearchHistoryItem("H-2", "Rajesh Kumar N.", SearchCategory.CONSENTED_DIRECTORY, 5, "2h ago"),
            SearchHistoryItem("H-3", "Data Protection Officer", SearchCategory.AUTHORIZED_CONTACT, 4, "Yesterday")
        )
    )
    val searchHistory: StateFlow<List<SearchHistoryItem>> = _searchHistory.asStateFlow()

    private val sampleDataset = listOf(
        SearchResult(
            id = "REC-8831",
            category = SearchCategory.CONSENTED_DIRECTORY,
            displayName = "Dr. Rajesh Kumar Nair",
            designation = "Lead Information Security Officer",
            organization = "National Digital Systems Board",
            department = "Cyber Defense & Compliance",
            location = "New Delhi, India",
            maskedNationalId = "XXXX-XXXX-9142",
            maskedPhone = "+91 98*** **321",
            maskedEmail = "r***r@digitalboard.gov.in",
            maskedAddress = "Sector 14, Chanakyapuri, [Masked]",
            verificationStatus = VerificationStatus.VERIFIED,
            sourceDataset = "DuckDB-Parquet / Consented Public Directory 2026",
            consentBasis = "Voluntary Public Officer Directory Listing (IT Act 43A Compliant)",
            confidenceScore = 0.99
        ),
        SearchResult(
            id = "REC-8832",
            category = SearchCategory.CONSENTED_DIRECTORY,
            displayName = "Sunita Rao",
            designation = "Corporate Legal Counsel",
            organization = "Tata Telecommunications Advisory",
            department = "Regulatory & Data Privacy",
            location = "Mumbai, Maharashtra",
            maskedNationalId = "XXXX-XXXX-4428",
            maskedPhone = "+91 91*** **890",
            maskedEmail = "s***o@tatatelecom.com",
            maskedAddress = "Bandra Kurla Complex, [Masked]",
            verificationStatus = VerificationStatus.VERIFIED,
            sourceDataset = "DuckDB-Parquet / Corporate Legal Registrars",
            consentBasis = "Corporate Registrar Consent Declaration",
            confidenceScore = 0.95
        ),
        SearchResult(
            id = "REC-9104",
            category = SearchCategory.ORGANIZATION,
            displayName = "Apex Cyber Shield Technologies Ltd.",
            designation = "Registered Cybersecurity Defense Entity",
            organization = "Apex Cyber Shield Ltd.",
            department = "Threat Intelligence Division",
            location = "Bengaluru, Karnataka",
            maskedNationalId = "CIN-U72200KA2021PTC148***",
            maskedPhone = "+91 80*** **500",
            maskedEmail = "compliance@apexcybershield.in",
            maskedAddress = "Electronics City Phase 1, [Masked]",
            verificationStatus = VerificationStatus.VERIFIED,
            sourceDataset = "DuckDB-Parquet / MCA Official Directory",
            consentBasis = "Public MCA Registry Data",
            confidenceScore = 0.97
        ),
        SearchResult(
            id = "REC-9105",
            category = SearchCategory.ORGANIZATION,
            displayName = "Bharat Secure Data Infrastructure Council",
            designation = "Statutory Industry Consortium",
            organization = "BSDI Council",
            department = "National Infrastructure Security",
            location = "Hyderabad, Telangana",
            maskedNationalId = "REG-TS/2019/009***",
            maskedPhone = "+91 40*** **211",
            maskedEmail = "registry@bsdi-council.org",
            maskedAddress = "HITEC City Knowledge Park, [Masked]",
            verificationStatus = VerificationStatus.VERIFIED,
            sourceDataset = "DuckDB-Parquet / Consented Institutional Data",
            consentBasis = "Consortium Public Disclosure",
            confidenceScore = 0.94
        ),
        SearchResult(
            id = "REC-7740",
            category = SearchCategory.AUTHORIZED_CONTACT,
            displayName = "Vikramaditya Sengupta",
            designation = "Designated Incident Response Liaison",
            organization = "National CERT Advisory Group",
            department = "Rapid Incident Assessment Unit",
            location = "Kolkata, West Bengal",
            maskedNationalId = "XXXX-XXXX-6712",
            maskedPhone = "+91 94*** **104",
            maskedEmail = "v***a@cert-liaison.in",
            maskedAddress = "Salt Lake Sector V, [Masked]",
            verificationStatus = VerificationStatus.VERIFIED,
            sourceDataset = "DuckDB-Parquet / Emergency Contact Roster",
            consentBasis = "Authorised CERT Protocol Contact",
            confidenceScore = 0.98
        ),
        SearchResult(
            id = "REC-7741",
            category = SearchCategory.AUTHORIZED_CONTACT,
            displayName = "Anya K. Fernandez",
            designation = "Chief Privacy Ombudsman",
            organization = "Data Protection Governance Board",
            department = "Grievance Redressal Cell",
            location = "Goa, India",
            maskedNationalId = "XXXX-XXXX-1934",
            maskedPhone = "+91 83*** **456",
            maskedEmail = "ombudsman@dataprotect.org",
            maskedAddress = "Patto Plaza, Panaji, [Masked]",
            verificationStatus = VerificationStatus.VERIFIED,
            sourceDataset = "DuckDB-Parquet / Ombudsman Public Directory",
            consentBasis = "DPDP Grievance Liaison Mandate",
            confidenceScore = 0.96
        ),
        SearchResult(
            id = "REC-8840",
            category = SearchCategory.CONSENTED_DIRECTORY,
            displayName = "Arvind Mahajan",
            designation = "Senior Forensic Auditor",
            organization = "Financial Intelligence Unit Liaison",
            department = "Audit & Forensics",
            location = "Chandigarh, India",
            maskedNationalId = "XXXX-XXXX-3829",
            maskedPhone = "+91 97*** **774",
            maskedEmail = "a***n@auditforensics.in",
            maskedAddress = "Sector 17, [Masked]",
            verificationStatus = VerificationStatus.VERIFIED,
            sourceDataset = "DuckDB-Parquet / Forensic Auditor Registry",
            consentBasis = "Certified Auditor Public Roster",
            confidenceScore = 0.92
        )
    )

    suspend fun performSearch(
        query: String,
        category: SearchCategory,
        page: Int = 1,
        pageSize: Int = 10
    ): Result<List<SearchResult>> {
        // Enforce user account check & quota check
        val user = _currentUser.value ?: return Result.failure(IllegalStateException("User not authenticated"))
        if (user.status == AccountStatus.SUSPENDED) {
            return Result.failure(IllegalStateException("Account is suspended. Contact administrator for reinstatement."))
        }
        if (user.remainingDailySearches <= 0) {
            return Result.failure(IllegalStateException("Daily search quota exceeded ($user.dailyQuota queries). Quota resets at 00:00 UTC."))
        }

        val result = client.executeWithInterceptors("/v1/search?category=${category.name}") {
            // Deduct quota & increment counters
            _currentUser.value = user.copy(
                searchesUsedToday = user.searchesUsedToday + 1,
                totalSearches = user.totalSearches + 1
            )
            // Update admin stats
            _adminStats.value = _adminStats.value.copy(
                totalSearchesToday = _adminStats.value.totalSearchesToday + 1,
                apiRequestsToday = _adminStats.value.apiRequestsToday + 1
            )

            val trimmed = query.trim().lowercase(Locale.ROOT)
            val filtered = sampleDataset.filter { item ->
                item.category == category && (
                    trimmed.isEmpty() ||
                    item.displayName.lowercase(Locale.ROOT).contains(trimmed) ||
                    item.organization.lowercase(Locale.ROOT).contains(trimmed) ||
                    item.department.lowercase(Locale.ROOT).contains(trimmed) ||
                    item.location.lowercase(Locale.ROOT).contains(trimmed) ||
                    item.designation.lowercase(Locale.ROOT).contains(trimmed)
                )
            }

            // Add to search history if query not empty
            if (trimmed.isNotEmpty()) {
                val history = _searchHistory.value.toMutableList()
                history.removeAll { it.query.equals(query.trim(), ignoreCase = true) }
                val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
                history.add(0, SearchHistoryItem(UUID.randomUUID().toString(), query.trim(), category, filtered.size, "Today, ${sdf.format(Date())}"))
                _searchHistory.value = history.take(20)
            }

            filtered
        }

        return if (result.isSuccess) {
            @Suppress("UNCHECKED_CAST")
            Result.success(result.getOrNull() as List<SearchResult>)
        } else {
            Result.failure(result.exceptionOrNull() ?: Exception("Unknown API error"))
        }
    }

    suspend fun login(email: String, pass: String): Result<User> {
        val res = client.executeWithInterceptors("/v1/auth/login") {
            if (email.contains("admin", ignoreCase = true)) {
                User(
                    id = "ADM-001",
                    name = "Marcus Thorne",
                    email = email,
                    role = UserRole.ADMIN,
                    status = AccountStatus.ACTIVE,
                    dailyQuota = 1000,
                    searchesUsedToday = 22,
                    totalSearches = 3491,
                    emailVerified = true,
                    organization = "Central Intelligence Command",
                    department = "Security Administration",
                    token = "jwt.admin.eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                )
            } else {
                User(
                    id = "USR-" + (1000..9999).random(),
                    name = if (email.contains("@")) email.substringBefore("@").replace(".", " ").capitalize(Locale.ROOT) else "Authorized User",
                    email = email,
                    role = UserRole.USER,
                    status = AccountStatus.ACTIVE,
                    dailyQuota = 100,
                    searchesUsedToday = 0,
                    totalSearches = 12,
                    emailVerified = true,
                    organization = "Verified Research Group",
                    department = "Compliance",
                    token = "jwt.user.eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9"
                )
            }
        }
        return if (res.isSuccess) {
            val user = res.getOrNull() as User
            _currentUser.value = user
            client.authToken = user.token
            Result.success(user)
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Login failed"))
        }
    }

    suspend fun register(name: String, email: String, org: String): Result<User> {
        val res = client.executeWithInterceptors("/v1/auth/register") {
            User(
                id = "USR-" + (1000..9999).random(),
                name = name,
                email = email,
                role = UserRole.USER,
                status = AccountStatus.ACTIVE,
                dailyQuota = 50,
                searchesUsedToday = 0,
                totalSearches = 0,
                emailVerified = false,
                organization = org,
                department = "General Investigations"
            )
        }
        return if (res.isSuccess) {
            val user = res.getOrNull() as User
            _currentUser.value = user
            client.authToken = user.token
            Result.success(user)
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Registration failed"))
        }
    }

    fun logout() {
        client.clearSession()
        _currentUser.value = null
    }

    fun switchRoleForDemo(role: UserRole) {
        if (role == UserRole.ADMIN) {
            _currentUser.value = User(
                id = "ADM-001",
                name = "Chief Administrator",
                email = "admin@nexus.io",
                role = UserRole.ADMIN,
                status = AccountStatus.ACTIVE,
                dailyQuota = 1000,
                searchesUsedToday = 22,
                totalSearches = 3491,
                emailVerified = true,
                organization = "Central Security Command",
                department = "Administration"
            )
        } else {
            _currentUser.value = User(
                id = "USR-9921",
                name = "Alex Vance",
                email = "demo@nexus.io",
                role = UserRole.USER,
                status = AccountStatus.ACTIVE,
                dailyQuota = 100,
                searchesUsedToday = 18,
                totalSearches = 482,
                emailVerified = true,
                organization = "Global Compliance Directorate",
                department = "Authorized Registry Verification"
            )
        }
    }

    // Admin Actions
    fun suspendUser(userId: String) {
        _managedUsers.value = _managedUsers.value.map {
            if (it.id == userId) it.copy(status = AccountStatus.SUSPENDED) else it
        }
        addAuditLog("ACCOUNT_SUSPEND", userId, "Admin suspended user account")
    }

    fun unsuspendUser(userId: String) {
        _managedUsers.value = _managedUsers.value.map {
            if (it.id == userId) it.copy(status = AccountStatus.ACTIVE) else it
        }
        addAuditLog("ACCOUNT_REINSTATE", userId, "Admin restored active status")
    }

    fun deleteUser(userId: String) {
        _managedUsers.value = _managedUsers.value.filter { it.id != userId }
        addAuditLog("ACCOUNT_DELETE", userId, "Purged user profile per GDPR/DPDP retention policy")
    }

    fun updateUserQuota(userId: String, newDailyQuota: Int, newMonthlyQuota: Int) {
        _managedUsers.value = _managedUsers.value.map {
            if (it.id == userId) it.copy(dailyQuota = newDailyQuota, monthlyQuota = newMonthlyQuota) else it
        }
        addAuditLog("QUOTA_ADJUST", userId, "Daily quota updated to $newDailyQuota, Monthly to $newMonthlyQuota")
    }

    fun resetUserCounter(userId: String) {
        _managedUsers.value = _managedUsers.value.map {
            if (it.id == userId) it.copy(searchesUsedToday = 0) else it
        }
        addAuditLog("USAGE_RESET", userId, "Daily search usage counter reset to 0")
    }

    fun addAnnouncement(title: String, content: String, priority: AnnouncementPriority, isMaintenance: Boolean) {
        val newAnn = Announcement(
            id = "ANN-" + System.currentTimeMillis().toString().takeLast(4),
            title = title,
            content = content,
            priority = priority,
            date = "Just now",
            isMaintenanceNotice = isMaintenance
        )
        _announcements.value = listOf(newAnn) + _announcements.value
        addAuditLog("ANNOUNCEMENT_CREATE", newAnn.id, "Published announcement: $title")
    }

    fun deleteAnnouncement(annId: String) {
        _announcements.value = _announcements.value.filter { it.id != annId }
        addAuditLog("ANNOUNCEMENT_DELETE", annId, "Admin removed announcement")
    }

    fun updateVersionConfig(latest: String, minSupported: String, notes: String, isMandatory: Boolean, maintenance: Boolean) {
        _appVersion.value = AppVersion(
            currentVersion = "2.4.0",
            latestVersion = latest,
            minSupportedVersion = minSupported,
            releaseNotes = notes,
            isMandatoryUpdate = isMandatory,
            isMaintenanceMode = maintenance
        )
        addAuditLog("VERSION_CONFIG_UPDATE", "v$latest", "Configured min version: $minSupported, Mandatory: $isMandatory")
    }

    fun clearSearchHistory() {
        _searchHistory.value = emptyList()
    }

    fun removeHistoryItem(id: String) {
        _searchHistory.value = _searchHistory.value.filter { it.id != id }
    }

    fun markNotificationRead(id: String) {
        _notifications.value = _notifications.value.map {
            if (it.id == id) it.copy(isRead = true) else it
        }
    }

    fun markAllNotificationsRead() {
        _notifications.value = _notifications.value.map { it.copy(isRead = true) }
    }

    fun sendBroadcastNotification(title: String, body: String, type: NotificationType) {
        val item = AppNotification(
            id = "NOT-" + System.currentTimeMillis().toString().takeLast(4),
            title = title,
            body = body,
            type = type,
            timestamp = "Just now",
            isRead = false
        )
        _notifications.value = listOf(item) + _notifications.value
        addAuditLog("NOTIFICATION_DISPATCH", "ALL_USERS", "Admin dispatched push: $title")
    }

    private fun addAuditLog(action: String, target: String, details: String) {
        val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        val log = AuditLog(
            id = "AUD-" + (100..999).random(),
            timestamp = sdf.format(Date()),
            actor = _currentUser.value?.email ?: "admin@nexus.io",
            action = action,
            target = target,
            details = details
        )
        _auditLogs.value = listOf(log) + _auditLogs.value.take(49)
    }
}

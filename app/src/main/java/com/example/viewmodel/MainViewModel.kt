package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.model.*
import com.example.core.network.ApiRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class AppScreen {
    SPLASH,
    AUTH,
    DASHBOARD,
    SEARCH,
    HISTORY,
    PROFILE,
    NOTIFICATIONS,
    ADMIN
}

enum class AdminTab {
    OVERVIEW,
    USERS,
    SEARCH_CONFIG,
    ANNOUNCEMENTS,
    VERSION_CTRL,
    AUDIT_LOGS
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    val repository = ApiRepository(application.applicationContext)

    // Navigation State
    private val _currentScreen = MutableStateFlow(AppScreen.SPLASH)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    // Backstack history for back handler
    private val backStack = mutableListOf<AppScreen>()

    // Theme state
    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    // Auth flows
    val currentUser = repository.currentUser
    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()
    private val _isAuthLoading = MutableStateFlow(false)
    val isAuthLoading: StateFlow<Boolean> = _isAuthLoading.asStateFlow()

    // Search state
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(SearchCategory.CONSENTED_DIRECTORY)
    val selectedCategory: StateFlow<SearchCategory> = _selectedCategory.asStateFlow()

    private val _searchResults = MutableStateFlow<List<SearchResult>>(emptyList())
    val searchResults: StateFlow<List<SearchResult>> = _searchResults.asStateFlow()

    private val _isSearchLoading = MutableStateFlow(false)
    val isSearchLoading: StateFlow<Boolean> = _isSearchLoading.asStateFlow()

    private val _searchError = MutableStateFlow<String?>(null)
    val searchError: StateFlow<String?> = _searchError.asStateFlow()

    private val _hasSearched = MutableStateFlow(false)
    val hasSearched: StateFlow<Boolean> = _hasSearched.asStateFlow()

    // Admin state
    val adminStats = repository.adminStats
    val managedUsers = repository.managedUsers
    val announcements = repository.announcements
    val appVersion = repository.appVersion
    val auditLogs = repository.auditLogs
    val notifications = repository.notifications
    val searchHistory = repository.searchHistory

    private val _selectedAdminTab = MutableStateFlow(AdminTab.OVERVIEW)
    val selectedAdminTab: StateFlow<AdminTab> = _selectedAdminTab.asStateFlow()

    private val _userFilterQuery = MutableStateFlow("")
    val userFilterQuery: StateFlow<String> = _userFilterQuery.asStateFlow()

    // Settings & API state
    private val _apiBaseUrlInput = MutableStateFlow(repository.client.baseUrl)
    val apiBaseUrlInput: StateFlow<String> = _apiBaseUrlInput.asStateFlow()

    private val _networkLatency = MutableStateFlow(repository.client.simulatedLatencyMs)
    val networkLatency: StateFlow<Long> = _networkLatency.asStateFlow()

    private val _simulateError = MutableStateFlow(repository.client.simulateNetworkError)
    val simulateError: StateFlow<Boolean> = _simulateError.asStateFlow()

    init {
        // Pre-fetch initial search sample
        viewModelScope.launch {
            triggerSearch("", SearchCategory.CONSENTED_DIRECTORY)
        }
    }

    fun navigateTo(screen: AppScreen) {
        if (_currentScreen.value != screen) {
            backStack.add(_currentScreen.value)
            _currentScreen.value = screen
        }
    }

    fun navigateBack(): Boolean {
        if (backStack.isNotEmpty()) {
            _currentScreen.value = backStack.removeAt(backStack.size - 1)
            return true
        }
        if (_currentScreen.value != AppScreen.DASHBOARD && _currentScreen.value != AppScreen.AUTH) {
            _currentScreen.value = AppScreen.DASHBOARD
            return true
        }
        return false
    }

    fun toggleTheme() {
        _isDarkTheme.value = !_isDarkTheme.value
    }

    fun setAdminTab(tab: AdminTab) {
        _selectedAdminTab.value = tab
    }

    fun setUserFilter(q: String) {
        _userFilterQuery.value = q
    }

    fun updateSearchQuery(q: String) {
        _searchQuery.value = q
    }

    fun setCategory(cat: SearchCategory) {
        _selectedCategory.value = cat
        triggerSearch(_searchQuery.value, cat)
    }

    fun triggerSearch(query: String = _searchQuery.value, category: SearchCategory = _selectedCategory.value) {
        viewModelScope.launch {
            _isSearchLoading.value = true
            _searchError.value = null
            _hasSearched.value = true

            val res = repository.performSearch(query, category)
            if (res.isSuccess) {
                _searchResults.value = res.getOrDefault(emptyList())
            } else {
                _searchError.value = res.exceptionOrNull()?.message ?: "Search failed"
                _searchResults.value = emptyList()
            }
            _isSearchLoading.value = false
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val res = repository.login(email, pass)
            _isAuthLoading.value = false
            if (res.isSuccess) {
                _currentScreen.value = AppScreen.DASHBOARD
            } else {
                _authError.value = res.exceptionOrNull()?.message ?: "Login failed"
            }
        }
    }

    fun register(name: String, email: String, org: String) {
        viewModelScope.launch {
            _isAuthLoading.value = true
            _authError.value = null
            val res = repository.register(name, email, org)
            _isAuthLoading.value = false
            if (res.isSuccess) {
                _currentScreen.value = AppScreen.DASHBOARD
            } else {
                _authError.value = res.exceptionOrNull()?.message ?: "Registration failed"
            }
        }
    }

    fun logout() {
        repository.logout()
        backStack.clear()
        _currentScreen.value = AppScreen.AUTH
    }

    fun quickLoginAs(role: UserRole) {
        repository.switchRoleForDemo(role)
        _currentScreen.value = AppScreen.DASHBOARD
    }

    // Admin Operations
    fun suspendUser(id: String) = repository.suspendUser(id)
    fun unsuspendUser(id: String) = repository.unsuspendUser(id)
    fun deleteUser(id: String) = repository.deleteUser(id)
    fun updateUserQuota(id: String, daily: Int, monthly: Int) = repository.updateUserQuota(id, daily, monthly)
    fun resetUserCounter(id: String) = repository.resetUserCounter(id)
    fun addAnnouncement(title: String, body: String, priority: AnnouncementPriority, isMaintenance: Boolean) =
        repository.addAnnouncement(title, body, priority, isMaintenance)
    fun deleteAnnouncement(id: String) = repository.deleteAnnouncement(id)
    fun updateVersion(latest: String, min: String, notes: String, isMandatory: Boolean, maintenance: Boolean) =
        repository.updateVersionConfig(latest, min, notes, isMandatory, maintenance)

    // History & Notifications
    fun clearHistory() = repository.clearSearchHistory()
    fun removeHistoryItem(id: String) = repository.removeHistoryItem(id)
    fun markNotificationRead(id: String) = repository.markNotificationRead(id)
    fun markAllNotificationsRead() = repository.markAllNotificationsRead()
    fun sendBroadcast(title: String, body: String, type: NotificationType) =
        repository.sendBroadcastNotification(title, body, type)

    // API Settings
    fun saveApiUrl(url: String) {
        repository.client.baseUrl = url
        _apiBaseUrlInput.value = url
    }

    fun toggleSimulateError(err: Boolean) {
        repository.client.simulateNetworkError = err
        _simulateError.value = err
    }

    fun setSimulatedLatency(ms: Long) {
        repository.client.simulatedLatencyMs = ms
        _networkLatency.value = ms
    }
}

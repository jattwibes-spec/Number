package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.UserRole
import com.example.ui.screens.admin.AdminScreen
import com.example.ui.screens.auth.AuthScreen
import com.example.ui.screens.history.HistoryScreen
import com.example.ui.screens.home.HomeScreen
import com.example.ui.screens.notifications.NotificationsScreen
import com.example.ui.screens.profile.ProfileScreen
import com.example.ui.screens.search.SearchScreen
import com.example.ui.screens.splash.SplashScreen
import com.example.ui.theme.CyberViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NexusTheme
import com.example.ui.theme.StatusWarning
import com.example.viewmodel.AppScreen
import com.example.viewmodel.MainViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsState()

            NexusTheme(darkTheme = isDarkTheme) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val appVersion by viewModel.appVersion.collectAsState()
    val notifications by viewModel.notifications.collectAsState()
    val unreadCount = notifications.count { !it.isRead }

    var updateBannerDismissed by remember { mutableStateOf(false) }

    // Android System Back handling
    BackHandler(enabled = currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.SPLASH) {
        if (!viewModel.navigateBack()) {
            if (currentScreen != AppScreen.DASHBOARD && currentScreen != AppScreen.AUTH) {
                viewModel.navigateTo(AppScreen.DASHBOARD)
            }
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentWindowInsets = WindowInsets.safeDrawing,
        bottomBar = {
            if (currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.AUTH) {
                NexusBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { viewModel.navigateTo(it) },
                    isAdmin = currentUser?.role == UserRole.ADMIN,
                    unreadNotifications = unreadCount
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // In-App Update Notice Banner (if new version is published)
                if (!updateBannerDismissed && appVersion.latestVersion != appVersion.currentVersion && currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.AUTH) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Brush.horizontalGradient(listOf(Color(0xFF1E293B), Color(0xFF0F172A))))
                            .border(1.dp, StatusWarning.copy(alpha = 0.5f))
                            .padding(horizontal = 14.dp, vertical = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.NewReleases,
                                    contentDescription = null,
                                    tint = StatusWarning,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Update v${appVersion.latestVersion} available (DuckDB 1.1 caching)",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    maxLines = 1
                                )
                            }

                            IconButton(
                                onClick = { updateBannerDismissed = true },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Dismiss",
                                    tint = Color.Gray,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }

                // Screen Switcher
                Box(modifier = Modifier.weight(1f)) {
                    when (currentScreen) {
                        AppScreen.SPLASH -> SplashScreen(
                            onFinished = {
                                if (currentUser != null) {
                                    viewModel.navigateTo(AppScreen.DASHBOARD)
                                } else {
                                    viewModel.navigateTo(AppScreen.AUTH)
                                }
                            }
                        )
                        AppScreen.AUTH -> AuthScreen(viewModel = viewModel)
                        AppScreen.DASHBOARD -> HomeScreen(viewModel = viewModel)
                        AppScreen.SEARCH -> SearchScreen(viewModel = viewModel)
                        AppScreen.HISTORY -> HistoryScreen(viewModel = viewModel)
                        AppScreen.PROFILE -> ProfileScreen(viewModel = viewModel)
                        AppScreen.NOTIFICATIONS -> NotificationsScreen(viewModel = viewModel)
                        AppScreen.ADMIN -> AdminScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun NexusBottomNavBar(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    isAdmin: Boolean,
    unreadNotifications: Int
) {
    val haptic = LocalHapticFeedback.current

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(22.dp)),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavItem(
                icon = if (currentScreen == AppScreen.DASHBOARD) Icons.Filled.Home else Icons.Outlined.Home,
                label = "Home",
                isSelected = currentScreen == AppScreen.DASHBOARD,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNavigate(AppScreen.DASHBOARD)
                },
                testTag = "nav_home"
            )

            NavItem(
                icon = if (currentScreen == AppScreen.SEARCH) Icons.Filled.Search else Icons.Outlined.Search,
                label = "Search",
                isSelected = currentScreen == AppScreen.SEARCH,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNavigate(AppScreen.SEARCH)
                },
                testTag = "nav_search"
            )

            NavItem(
                icon = if (currentScreen == AppScreen.HISTORY) Icons.Filled.History else Icons.Outlined.History,
                label = "History",
                isSelected = currentScreen == AppScreen.HISTORY,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNavigate(AppScreen.HISTORY)
                },
                testTag = "nav_history"
            )

            NavItem(
                icon = if (currentScreen == AppScreen.NOTIFICATIONS) Icons.Filled.Notifications else Icons.Outlined.Notifications,
                label = "Alerts",
                isSelected = currentScreen == AppScreen.NOTIFICATIONS,
                badgeCount = unreadNotifications,
                onClick = {
                    haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                    onNavigate(AppScreen.NOTIFICATIONS)
                },
                testTag = "nav_notifications"
            )

            if (isAdmin) {
                NavItem(
                    icon = if (currentScreen == AppScreen.ADMIN) Icons.Filled.AdminPanelSettings else Icons.Outlined.AdminPanelSettings,
                    label = "Admin",
                    isSelected = currentScreen == AppScreen.ADMIN,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigate(AppScreen.ADMIN)
                    },
                    testTag = "nav_admin",
                    accentColor = CyberViolet
                )
            } else {
                NavItem(
                    icon = if (currentScreen == AppScreen.PROFILE) Icons.Filled.Person else Icons.Outlined.Person,
                    label = "Profile",
                    isSelected = currentScreen == AppScreen.PROFILE,
                    onClick = {
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                        onNavigate(AppScreen.PROFILE)
                    },
                    testTag = "nav_profile"
                )
            }
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    badgeCount: Int = 0,
    accentColor: Color = ElectricBlue
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(22.dp)
                )

                if (badgeCount > 0) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .align(Alignment.TopEnd)
                            .clip(CircleShape)
                            .background(accentColor)
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = label,
                color = if (isSelected) accentColor else MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 10.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
        }
    }
}

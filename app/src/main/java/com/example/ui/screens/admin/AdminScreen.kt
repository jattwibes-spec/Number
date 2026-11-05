package com.example.ui.screens.admin

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.AdminTab
import com.example.viewmodel.MainViewModel

@Composable
fun AdminScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val selectedTab by viewModel.selectedAdminTab.collectAsState()
    val stats by viewModel.adminStats.collectAsState()
    val managedUsers by viewModel.managedUsers.collectAsState()
    val announcements by viewModel.announcements.collectAsState()
    val appVersion by viewModel.appVersion.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()

    var userSearchText by remember { mutableStateOf("") }

    // Dialog States
    var selectedUserForQuota by remember { mutableStateOf<ManagedUser?>(null) }
    var userToDelete by remember { mutableStateOf<ManagedUser?>(null) }
    var showCreateAnnDialog by remember { mutableStateOf(false) }
    var showVersionEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("admin_screen")
    ) {
        // Admin Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(CyberViolet.copy(alpha = 0.2f))
                            .border(1.dp, CyberViolet, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = null,
                            tint = CyberViolet,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Admin Governance Console",
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                        Text(
                            text = "SecOps Oversight & Quota Enforcement",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(StatusSuccess.copy(alpha = 0.15f))
                        .border(1.dp, StatusSuccess.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text("AUTHORIZED", color = StatusSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scrollable Sub-tabs
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminTab.values().forEach { tab ->
                    val isSelected = tab == selectedTab
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                if (isSelected) CyberViolet
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f)
                            )
                            .clickable { viewModel.setAdminTab(tab) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                            .testTag("admin_tab_${tab.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (tab) {
                                AdminTab.OVERVIEW -> "Overview"
                                AdminTab.USERS -> "Users (${managedUsers.size})"
                                AdminTab.SEARCH_CONFIG -> "Search Policies"
                                AdminTab.ANNOUNCEMENTS -> "Announcements"
                                AdminTab.VERSION_CTRL -> "Version Ctrl"
                                AdminTab.AUDIT_LOGS -> "Audit Trail"
                            },
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Tab Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            when (selectedTab) {
                AdminTab.OVERVIEW -> AdminOverviewTab(stats, appVersion)
                AdminTab.USERS -> AdminUsersTab(
                    users = managedUsers,
                    searchQuery = userSearchText,
                    onSearchChange = { userSearchText = it },
                    onSuspend = { id ->
                        viewModel.suspendUser(id)
                        Toast.makeText(context, "Account suspended", Toast.LENGTH_SHORT).show()
                    },
                    onUnsuspend = { id ->
                        viewModel.unsuspendUser(id)
                        Toast.makeText(context, "Account reinstated", Toast.LENGTH_SHORT).show()
                    },
                    onEditQuota = { selectedUserForQuota = it },
                    onDelete = { userToDelete = it },
                    onResetCounter = { id ->
                        viewModel.resetUserCounter(id)
                        Toast.makeText(context, "Daily search usage reset to 0", Toast.LENGTH_SHORT).show()
                    }
                )
                AdminTab.SEARCH_CONFIG -> AdminSearchConfigTab()
                AdminTab.ANNOUNCEMENTS -> AdminAnnouncementsTab(
                    announcements = announcements,
                    onCreateClick = { showCreateAnnDialog = true },
                    onDeleteClick = { id ->
                        viewModel.deleteAnnouncement(id)
                        Toast.makeText(context, "Announcement removed", Toast.LENGTH_SHORT).show()
                    }
                )
                AdminTab.VERSION_CTRL -> AdminVersionTab(
                    appVersion = appVersion,
                    onEditClick = { showVersionEditDialog = true }
                )
                AdminTab.AUDIT_LOGS -> AdminAuditLogsTab(auditLogs = auditLogs)
            }
        }

        // Quota Edit Dialog
        if (selectedUserForQuota != null) {
            var dailyQuotaInput by remember { mutableStateOf(selectedUserForQuota!!.dailyQuota.toString()) }
            var monthlyQuotaInput by remember { mutableStateOf(selectedUserForQuota!!.monthlyQuota.toString()) }

            AlertDialog(
                onDismissRequest = { selectedUserForQuota = null },
                title = { Text("Configure Search Quota") },
                text = {
                    Column {
                        Text(
                            text = "Account: ${selectedUserForQuota!!.name} (${selectedUserForQuota!!.id})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = dailyQuotaInput,
                            onValueChange = { dailyQuotaInput = it },
                            label = { Text("Daily Query Limit") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = monthlyQuotaInput,
                            onValueChange = { monthlyQuotaInput = it },
                            label = { Text("Monthly Query Limit") },
                            singleLine = true
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            val d = dailyQuotaInput.toIntOrNull() ?: selectedUserForQuota!!.dailyQuota
                            val m = monthlyQuotaInput.toIntOrNull() ?: selectedUserForQuota!!.monthlyQuota
                            viewModel.updateUserQuota(selectedUserForQuota!!.id, d, m)
                            Toast.makeText(context, "Quota updated successfully", Toast.LENGTH_SHORT).show()
                            selectedUserForQuota = null
                        }
                    ) {
                        Text("Save Quota")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { selectedUserForQuota = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Delete User Confirmation Dialog
        if (userToDelete != null) {
            AlertDialog(
                onDismissRequest = { userToDelete = null },
                title = { Text("Purge User Account") },
                text = {
                    Text("Are you sure you want to permanently delete ${userToDelete!!.name} (${userToDelete!!.id})? This action complies with DPDP/GDPR right to erasure.")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteUser(userToDelete!!.id)
                            Toast.makeText(context, "User profile erased", Toast.LENGTH_SHORT).show()
                            userToDelete = null
                        }
                    ) {
                        Text("Confirm Delete", color = MaterialTheme.colorScheme.error)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { userToDelete = null }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Create Announcement Dialog
        if (showCreateAnnDialog) {
            var titleInput by remember { mutableStateOf("") }
            var contentInput by remember { mutableStateOf("") }
            var isMaintenance by remember { mutableStateOf(false) }

            AlertDialog(
                onDismissRequest = { showCreateAnnDialog = false },
                title = { Text("Publish Announcement") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Announcement Title") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = contentInput,
                            onValueChange = { contentInput = it },
                            label = { Text("Broadcast Message") },
                            maxLines = 4
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = isMaintenance, onCheckedChange = { isMaintenance = it })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark as Maintenance Notice", fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (titleInput.isNotEmpty() && contentInput.isNotEmpty()) {
                                viewModel.addAnnouncement(
                                    titleInput,
                                    contentInput,
                                    if (isMaintenance) AnnouncementPriority.WARNING else AnnouncementPriority.INFO,
                                    isMaintenance
                                )
                                Toast.makeText(context, "Announcement published to all users", Toast.LENGTH_SHORT).show()
                                showCreateAnnDialog = false
                            }
                        }
                    ) {
                        Text("Publish")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showCreateAnnDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Version Edit Dialog
        if (showVersionEditDialog) {
            var latestInput by remember { mutableStateOf(appVersion.latestVersion) }
            var minInput by remember { mutableStateOf(appVersion.minSupportedVersion) }
            var notesInput by remember { mutableStateOf(appVersion.releaseNotes) }
            var mandatoryToggle by remember { mutableStateOf(appVersion.isMandatoryUpdate) }
            var maintenanceToggle by remember { mutableStateOf(appVersion.isMaintenanceMode) }

            AlertDialog(
                onDismissRequest = { showVersionEditDialog = false },
                title = { Text("Configure Application Version") },
                text = {
                    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                        OutlinedTextField(
                            value = latestInput,
                            onValueChange = { latestInput = it },
                            label = { Text("Latest Version String") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = minInput,
                            onValueChange = { minInput = it },
                            label = { Text("Minimum Supported Version") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = notesInput,
                            onValueChange = { notesInput = it },
                            label = { Text("Release Notes") },
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = mandatoryToggle, onCheckedChange = { mandatoryToggle = it })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enforce Mandatory Update", fontSize = 12.sp)
                        }
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Checkbox(checked = maintenanceToggle, onCheckedChange = { maintenanceToggle = it })
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Enable Maintenance Lockout", fontSize = 12.sp)
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.updateVersion(latestInput, minInput, notesInput, mandatoryToggle, maintenanceToggle)
                            Toast.makeText(context, "Version governance synced", Toast.LENGTH_SHORT).show()
                            showVersionEditDialog = false
                        }
                    ) {
                        Text("Save Version")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showVersionEditDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun AdminOverviewTab(stats: AdminStats, version: AppVersion) {
    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text(
                text = "REAL-TIME INFRASTRUCTURE METRICS",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Total Users",
                    value = "${stats.totalRegisteredUsers}",
                    sub = "${stats.activeUsers} Active",
                    color = ElectricBlue,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Suspended",
                    value = "${stats.suspendedUsers}",
                    sub = "Policy Flagged",
                    color = StatusError,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricCard(
                    title = "Searches Today",
                    value = "${stats.totalSearchesToday}",
                    sub = "DuckDB Parquet Queries",
                    color = CyberViolet,
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Avg Latency",
                    value = "${stats.avgResponseTimeMs} ms",
                    sub = "Cache Hit: ${stats.currentDuckDbCacheHitRate}%",
                    color = StatusSuccess,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Security Events & Abuse Guard",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(StatusSuccess.copy(alpha = 0.15f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("Zero Breaches", color = StatusSuccess, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "• Token refresh rate: 100% nominal\n• Failed login attempts (last 24h): 4\n• Automated DuckDB rate limiter: Active\n• Aadhaar masking enforcement: 100% audited",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(
    title: String,
    value: String,
    sub: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    GlassCard(modifier = modifier) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, color = color, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(sub, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
        }
    }
}

@Composable
fun AdminUsersTab(
    users: List<ManagedUser>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onSuspend: (String) -> Unit,
    onUnsuspend: (String) -> Unit,
    onEditQuota: (ManagedUser) -> Unit,
    onDelete: (ManagedUser) -> Unit,
    onResetCounter: (String) -> Unit
) {
    val filtered = users.filter {
        searchQuery.isEmpty() ||
        it.name.contains(searchQuery, ignoreCase = true) ||
        it.id.contains(searchQuery, ignoreCase = true) ||
        it.email.contains(searchQuery, ignoreCase = true)
    }

    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Filter users by name or ID (e.g. USR-9921)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("admin_user_filter_input")
            )
        }

        items(filtered, key = { it.id }) { user ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = user.name,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${user.id})",
                                    color = ElectricBlue,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Text(
                                text = user.email,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp
                            )
                        }

                        StatusPill(
                            label = user.status.name,
                            isActive = user.status == AccountStatus.ACTIVE
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Daily Quota: ${user.searchesUsedToday} / ${user.dailyQuota}",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Total Queries: ${user.totalSearches}",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 12.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
                    Spacer(modifier = Modifier.height(8.dp))

                    // Admin Action Buttons Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(onClick = { onEditQuota(user) }) {
                            Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Quota", fontSize = 11.sp)
                        }

                        TextButton(onClick = { onResetCounter(user.id) }) {
                            Icon(Icons.Default.RestartAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Reset", fontSize = 11.sp)
                        }

                        if (user.status == AccountStatus.ACTIVE) {
                            TextButton(onClick = { onSuspend(user.id) }) {
                                Icon(Icons.Default.Block, contentDescription = null, tint = StatusError, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Suspend", color = StatusError, fontSize = 11.sp)
                            }
                        } else {
                            TextButton(onClick = { onUnsuspend(user.id) }) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = StatusSuccess, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Restore", color = StatusSuccess, fontSize = 11.sp)
                            }
                        }

                        IconButton(onClick = { onDelete(user) }) {
                            Icon(Icons.Default.Delete, contentDescription = "Purge Account", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AdminSearchConfigTab() {
    var globalQuota by remember { mutableStateOf(200f) }
    var rateLimit by remember { mutableStateOf(60f) }
    var maxResults by remember { mutableStateOf(50f) }
    var abuseDetection by remember { mutableStateOf(true) }
    var throttling by remember { mutableStateOf(true) }

    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Global Daily Search Cap: ${globalQuota.toInt()} queries/user",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = globalQuota,
                        onValueChange = { globalQuota = it },
                        valueRange = 50f..1000f,
                        colors = SliderDefaults.colors(thumbColor = CyberViolet, activeTrackColor = CyberViolet)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "API Rate Limit: ${rateLimit.toInt()} requests/minute",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = rateLimit,
                        onValueChange = { rateLimit = it },
                        valueRange = 10f..120f,
                        colors = SliderDefaults.colors(thumbColor = ElectricBlue, activeTrackColor = ElectricBlue)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Max Results Per Query: ${maxResults.toInt()}",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Slider(
                        value = maxResults,
                        onValueChange = { maxResults = it },
                        valueRange = 10f..100f,
                        colors = SliderDefaults.colors(thumbColor = NeonCyan, activeTrackColor = NeonCyan)
                    )
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Abuse & Scrape Detection", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Auto-quarantine suspicious rapid bulk query requests", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                        Switch(checked = abuseDetection, onCheckedChange = { abuseDetection = it })
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Dynamic Request Throttling", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                            Text("Enforce HTTP 429 backoff when backend load spikes", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                        Switch(checked = throttling, onCheckedChange = { throttling = it })
                    }
                }
            }
        }
    }
}

@Composable
fun AdminAnnouncementsTab(
    announcements: List<Announcement>,
    onCreateClick: () -> Unit,
    onDeleteClick: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Broadcast Announcements",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                NeonButton(
                    text = "New Announcement",
                    icon = Icons.Default.Add,
                    onClick = onCreateClick,
                    testTag = "create_announcement_button"
                )
            }
        }

        items(announcements, key = { it.id }) { ann ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (ann.isMaintenanceNotice) Icons.Default.Warning else Icons.Default.Campaign,
                                contentDescription = null,
                                tint = if (ann.isMaintenanceNotice) StatusWarning else ElectricBlue,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = ann.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.SemiBold,
                                fontSize = 14.sp
                            )
                        }

                        IconButton(
                            onClick = { onDeleteClick(ann.id) },
                            modifier = Modifier.size(28.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = ann.content,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Published by ${ann.author} • ${ann.date}",
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AdminVersionTab(
    appVersion: AppVersion,
    onEditClick: () -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Version Status Matrix", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        IconButton(onClick = onEditClick) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Version", tint = ElectricBlue)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Current Deployed Build:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("v${appVersion.currentVersion}", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Latest Play Store Release:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("v${appVersion.latestVersion}", color = ElectricBlue, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Minimum Supported Version:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text("v${appVersion.minSupportedVersion}", color = StatusWarning, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Mandatory Update Enforced:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text(if (appVersion.isMandatoryUpdate) "YES" else "NO", color = if (appVersion.isMandatoryUpdate) StatusError else StatusSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Maintenance Lockout:", color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 12.sp)
                        Text(if (appVersion.isMaintenanceMode) "ACTIVE" else "OFFLINE", color = if (appVersion.isMaintenanceMode) StatusError else StatusSuccess, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Release Notes (Latest Update):", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = appVersion.releaseNotes,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }
        }
    }
}

@Composable
fun AdminAuditLogsTab(auditLogs: List<AuditLog>) {
    LazyColumn(
        contentPadding = PaddingValues(top = 10.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                text = "IMMUTABLE ADMINISTRATIVE AUDIT TRAIL",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        items(auditLogs, key = { it.id }) { log ->
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(CyberViolet.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(log.action, color = CyberViolet, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(log.target, color = MaterialTheme.colorScheme.onSurface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Text(log.timestamp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(log.details, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Actor: ${log.actor} • IP: ${log.ipAddress}", color = ElectricBlue, fontSize = 9.sp)
                }
            }
        }
    }
}

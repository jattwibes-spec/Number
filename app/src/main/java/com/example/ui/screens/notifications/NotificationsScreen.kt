package com.example.ui.screens.notifications

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.core.model.AppNotification
import com.example.core.model.NotificationType
import com.example.core.model.UserRole
import com.example.ui.components.GlassCard
import com.example.ui.components.NeonButton
import com.example.ui.components.NeonOutlineButton
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun NotificationsScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val notifications by viewModel.notifications.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var showBroadcastDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("notifications_screen")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Notification Center",
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "${notifications.count { !it.isRead }} unread alerts",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (currentUser?.role == UserRole.ADMIN) {
                    NeonButton(
                        text = "Broadcast",
                        icon = Icons.Default.Send,
                        onClick = { showBroadcastDialog = true },
                        testTag = "admin_broadcast_button"
                    )
                }

                if (notifications.any { !it.isRead }) {
                    NeonOutlineButton(
                        text = "Read All",
                        onClick = { viewModel.markAllNotificationsRead() },
                        testTag = "mark_all_read_button"
                    )
                }
            }
        }

        if (notifications.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.NotificationsNone,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = "No Notifications",
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "System updates, security alerts, and version notifications will appear here.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 12.sp
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp, top = 4.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(notifications, key = { it.id }) { item ->
                    val icon = when (item.type) {
                        NotificationType.VERSION_UPDATE -> Icons.Default.SystemUpdate
                        NotificationType.MAINTENANCE -> Icons.Default.Build
                        NotificationType.SECURITY -> Icons.Default.Security
                        NotificationType.ACCOUNT -> Icons.Default.AccountCircle
                        NotificationType.ANNOUNCEMENT -> Icons.Default.Campaign
                    }
                    val iconColor = when (item.type) {
                        NotificationType.SECURITY -> StatusError
                        NotificationType.MAINTENANCE -> StatusWarning
                        NotificationType.VERSION_UPDATE -> ElectricBlue
                        NotificationType.ACCOUNT -> CyberViolet
                        NotificationType.ANNOUNCEMENT -> NeonCyan
                    }

                    GlassCard(
                        modifier = Modifier.fillMaxWidth(),
                        onClick = { viewModel.markNotificationRead(item.id) },
                        borderColor = if (!item.isRead) ElectricBlue.copy(alpha = 0.6f) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(iconColor.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = iconColor,
                                    modifier = Modifier.size(18.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = item.title,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontWeight = if (!item.isRead) FontWeight.Bold else FontWeight.SemiBold,
                                        fontSize = 14.sp
                                    )
                                    if (!item.isRead) {
                                        Box(
                                            modifier = Modifier
                                                .size(8.dp)
                                                .clip(CircleShape)
                                                .background(ElectricBlue)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = item.body,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 12.sp,
                                    lineHeight = 16.sp
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Text(
                                    text = item.timestamp,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Admin Broadcast Dialog
        if (showBroadcastDialog) {
            var bTitle by remember { mutableStateOf("") }
            var bBody by remember { mutableStateOf("") }
            var bType by remember { mutableStateOf(NotificationType.ANNOUNCEMENT) }

            AlertDialog(
                onDismissRequest = { showBroadcastDialog = false },
                title = { Text("Broadcast Push Notification") },
                text = {
                    Column {
                        Text("Target: All Registered Users & Active Sessions", fontSize = 11.sp, color = ElectricBlue)
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = bTitle,
                            onValueChange = { bTitle = it },
                            label = { Text("Notification Title") },
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = bBody,
                            onValueChange = { bBody = it },
                            label = { Text("Notification Message") },
                            maxLines = 3
                        )
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            if (bTitle.isNotEmpty() && bBody.isNotEmpty()) {
                                viewModel.sendBroadcast(bTitle, bBody, bType)
                                Toast.makeText(context, "Notification sent to all active users", Toast.LENGTH_SHORT).show()
                                showBroadcastDialog = false
                            }
                        }
                    ) {
                        Text("Send Broadcast")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showBroadcastDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

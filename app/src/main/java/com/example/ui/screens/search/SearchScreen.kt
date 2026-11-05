package com.example.ui.screens.search

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.model.SearchCategory
import com.example.core.model.SearchResult
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.viewmodel.MainViewModel

@Composable
fun SearchScreen(
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    val query by viewModel.searchQuery.collectAsState()
    val category by viewModel.selectedCategory.collectAsState()
    val results by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isSearchLoading.collectAsState()
    val error by viewModel.searchError.collectAsState()
    val hasSearched by viewModel.hasSearched.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()

    var expandedCardId by remember { mutableStateOf<String?>(null) }
    var showExportDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        // Sticky Search Header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            // Category Selector Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchCategory.values().forEach { cat ->
                    val isSelected = cat == category
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .then(
                                if (isSelected) Modifier.background(Brush.horizontalGradient(listOf(ElectricBlue, CyberViolet)))
                                else Modifier.background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            )
                            .border(
                                1.dp,
                                if (isSelected) ElectricBlue else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.setCategory(cat) }
                            .padding(vertical = 8.dp, horizontal = 6.dp)
                            .testTag("category_tab_${cat.name}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (cat) {
                                SearchCategory.CONSENTED_DIRECTORY -> "Directory"
                                SearchCategory.ORGANIZATION -> "Organization"
                                SearchCategory.AUTHORIZED_CONTACT -> "Contacts"
                            },
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }
            }

            // Search Bar Input with Action Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    .padding(horizontal = 12.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = ElectricBlue,
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                TextField(
                    value = query,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = "Search by name, organization or department...",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 13.sp
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent,
                        disabledContainerColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("search_text_input")
                )

                if (query.isNotEmpty()) {
                    IconButton(
                        onClick = {
                            viewModel.updateSearchQuery("")
                            viewModel.triggerSearch("", category)
                        },
                        modifier = Modifier.testTag("clear_search_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Clear search",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(Brush.horizontalGradient(listOf(ElectricBlue, CyberViolet)))
                        .clickable { viewModel.triggerSearch() }
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                        .testTag("submit_search_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Query",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Privacy Compliance Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(DeepNavySurfaceVariant.copy(alpha = 0.6f))
                    .border(0.8.dp, ElectricBlue.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
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
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = ElectricBlue,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Sensitive fields masked by default (IT Act 43A & DPDP/GDPR compliant)",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }

                    if (results.isNotEmpty()) {
                        Text(
                            text = "${results.size} matches",
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }
                }
            }
        }

        // Results Section
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 100.dp, top = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLoading) {
                items(3) {
                    SearchResultSkeleton()
                }
            } else if (error != null) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.ErrorOutline,
                                contentDescription = null,
                                tint = StatusError,
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Search Query Interrupted",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = error ?: "Unknown gateway error",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                lineHeight = 16.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            NeonButton(
                                text = "Retry Query",
                                onClick = { viewModel.triggerSearch() },
                                testTag = "retry_query_button"
                            )
                        }
                    }
                }
            } else if (results.isEmpty() && hasSearched) {
                item {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(32.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(44.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "No Authorized Records Found",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "Verify your query keywords or switch to another category (e.g., Consented Directory or Organization Records).",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 12.sp,
                                lineHeight = 17.sp
                            )
                        }
                    }
                }
            } else {
                items(results, key = { it.id }) { item ->
                    val isExpanded = expandedCardId == item.id
                    SearchResultCard(
                        item = item,
                        isExpanded = isExpanded,
                        onToggleExpand = {
                            expandedCardId = if (isExpanded) null else item.id
                        },
                        onCopyField = { label, value ->
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
                            Toast.makeText(context, "$label copied to clipboard", Toast.LENGTH_SHORT).show()
                        },
                        onShareRecord = {
                            val shareText = """
                                [NEXUS SEARCH - AUTHORIZED RECORD SUMMARY]
                                ID: ${item.id}
                                Name: ${item.displayName}
                                Designation: ${item.designation}
                                Organization: ${item.organization}
                                Department: ${item.department}
                                Location: ${item.location}
                                Data Source: ${item.sourceDataset}
                                Legal Basis: ${item.consentBasis}
                                Compliance: Sensitive PII masked per DPDP/GDPR.
                            """.trimIndent()
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, shareText)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Share Authorized Record"))
                        }
                    )
                }

                if (results.isNotEmpty()) {
                    item {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Showing 1-${results.size} of ${results.size} authorized records",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 11.sp
                            )

                            NeonOutlineButton(
                                text = "Export Results",
                                icon = Icons.Default.Download,
                                onClick = { showExportDialog = true },
                                testTag = "export_results_button"
                            )
                        }
                    }
                }
            }
        }

        // Export Dialog
        if (showExportDialog) {
            AlertDialog(
                onDismissRequest = { showExportDialog = false },
                title = { Text("Export Authorized Summary") },
                text = {
                    Text("The permitted dataset (${results.size} records) has been formatted into compliance JSON with sensitive fields masked. Would you like to share the report?")
                },
                confirmButton = {
                    TextButton(
                        onClick = {
                            showExportDialog = false
                            val jsonSummary = "{\n  \"export_timestamp\": \"2026-11-05\",\n  \"record_count\": ${results.size},\n  \"compliance_policy\": \"DPDP_GDPR_MASKED\",\n  \"records\": ${results.map { it.id }}\n}"
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, jsonSummary)
                                type = "application/json"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Export Report JSON"))
                        }
                    ) {
                        Text("Export Now")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showExportDialog = false }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
fun SearchResultCard(
    item: SearchResult,
    isExpanded: Boolean,
    onToggleExpand: () -> Unit,
    onCopyField: (label: String, value: String) -> Unit,
    onShareRecord: () -> Unit
) {
    val rotation by animateFloatAsState(if (isExpanded) 180f else 0f, label = "card_arrow_rotation")

    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("result_card_${item.id}"),
        onClick = onToggleExpand
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: ID, Badges, Expand Arrow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(ElectricBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = item.id,
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    VerificationBadge(item.verificationStatus)
                }

                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                        .size(20.dp)
                        .rotate(rotation)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Display Name & Designation
            Text(
                text = item.displayName,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = "${item.designation} • ${item.organization}",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(top = 2.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = item.location,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Text(
                    text = "•",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp
                )

                Text(
                    text = item.department,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }

            // Expanded Sensitive Fields & Audit Info
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "PROTECTED & MASKED ATTRIBUTES",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    MaskedFieldRow(
                        label = "National Identifier / Reg",
                        value = item.maskedNationalId,
                        onCopy = { onCopyField("ID", item.maskedNationalId) }
                    )

                    MaskedFieldRow(
                        label = "Authorized Phone",
                        value = item.maskedPhone,
                        onCopy = { onCopyField("Phone", item.maskedPhone) }
                    )

                    MaskedFieldRow(
                        label = "Official Contact Email",
                        value = item.maskedEmail,
                        onCopy = { onCopyField("Email", item.maskedEmail) }
                    )

                    MaskedFieldRow(
                        label = "Registered Address",
                        value = item.maskedAddress,
                        onCopy = { onCopyField("Address", item.maskedAddress) }
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Legal Basis & Provenance Card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "Legal Consent Basis:",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = item.consentBasis,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Data Source: ${item.sourceDataset}",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = onShareRecord) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share Record",
                                tint = ElectricBlue,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MaskedFieldRow(
    label: String,
    value: String,
    onCopy: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 11.sp
            )
            Text(
                text = value,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }

        IconButton(
            onClick = onCopy,
            modifier = Modifier.size(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ContentCopy,
                contentDescription = "Copy field",
                tint = ElectricBlue,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

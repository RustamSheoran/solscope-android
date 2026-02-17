package com.example.solscope.presentation.watchlist

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.shadow
import com.example.solscope.data.watchlist.WatchlistRepository
import com.example.solscope.presentation.components.GlassButton
import com.example.solscope.presentation.components.GlassCard
import com.example.solscope.ui.theme.CyberColors
import androidx.compose.ui.res.painterResource
import com.example.solscope.R

@Composable
fun WatchlistScreen(
    onAnalyze: (String) -> Unit,
    viewModel: WatchlistViewModel,
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {
    val watchlist by viewModel.watchlist.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Top bar: Theme toggle (Left aligned, vertically higher)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        painter = painterResource(
                            id = if (isDarkTheme) R.drawable.ic_sun else R.drawable.ic_moon
                        ),
                        contentDescription = if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center, // Or Start? User said "watchlist header" just like solscope. SolScope title is centered.
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WATCHLIST",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (watchlist.isEmpty()) {
                Box(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No wallets tracked yet.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            } else {
                // Page-based pagination
                val pageSize = 10
                var currentPage by remember { mutableIntStateOf(0) }
                val totalPages = ((watchlist.size - 1) / pageSize) + 1
                // Clamp page if items removed
                val safePage = currentPage.coerceIn(0, (totalPages - 1).coerceAtLeast(0))
                if (safePage != currentPage) currentPage = safePage
                val pageItems = watchlist.drop(currentPage * pageSize).take(pageSize)

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(pageItems) { entry ->
                        WatchlistCard(
                            entry = entry,
                            onClick = { onAnalyze(entry.address) },
                            onDelete = { viewModel.removeAddress(entry.address) }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Page navigation — always visible for consistency
                    item(key = "watchlist_page_nav") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            GlassButton(
                                onClick = { if (currentPage > 0) currentPage-- },
                                containerColor = if (currentPage > 0) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(
                                    "← PREV",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (currentPage > 0) MaterialTheme.colorScheme.onSecondary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }

                            Text(
                                "Page ${currentPage + 1} of $totalPages",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            GlassButton(
                                onClick = { if (currentPage < totalPages - 1) currentPage++ },
                                containerColor = if (currentPage < totalPages - 1) MaterialTheme.colorScheme.secondary
                                    else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                                modifier = Modifier.height(40.dp)
                            ) {
                                Text(
                                    "NEXT →",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (currentPage < totalPages - 1) MaterialTheme.colorScheme.onSecondary
                                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Floating Action Button
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp)
        ) {
            // Custom Purple for user request
            val purpleAccent = Color(0xFFD0BCFF) // Light purple/lavender for dark theme visibility
            
            GlassButton(
                onClick = { showAddDialog = true },
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .size(65.dp) // Bigger button slightly
                    .shadow(8.dp, CircleShape, spotColor = MaterialTheme.colorScheme.primary)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Wallet",
                    tint = purpleAccent,
                    modifier = Modifier.size(54.dp) // Bigger icon
                )
            }
        }

        if (showAddDialog) {
            AddWalletDialog(
                onDismiss = { showAddDialog = false },
                onAdd = { address ->
                    viewModel.addAddress(address)
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun WatchlistCard(
    entry: WatchlistEntry,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    GlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = entry.address.take(4) + "..." + entry.address.takeLast(4),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                 
                Spacer(modifier = Modifier.height(4.dp))

                if (entry.balanceLoading) {
                    Text(
                        "Updating...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                    )
                } else if (entry.error) {
                    Text(
                        "Connection Error",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                } else {
                    // Balance row: "124979.98 SOL  (8.3M $)"
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            entry.balance?.let { String.format("%.2f SOL", it) } ?: "N/A",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.Bold
                        )
                        entry.balance?.let { bal ->
                            val usd = bal * SOL_PRICE_USD
                            Text(
                                "(${formatUsd(usd)})",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }

                // Last txn time: "🕐 2d ago"
                entry.lastTxnTime?.let { ts ->
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        "🕐 ${formatRelativeTime(ts)}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.45f)
                    )
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                )
            }
        }
    }
}

// Approximate SOL price — good enough for a quick estimate display
private const val SOL_PRICE_USD = 170.0

private fun formatUsd(usd: Double): String {
    return when {
        usd >= 1_000_000_000 -> String.format("%.1fB $", usd / 1_000_000_000)
        usd >= 1_000_000 -> String.format("%.1fM $", usd / 1_000_000)
        usd >= 1_000 -> String.format("%.1fK $", usd / 1_000)
        else -> String.format("%.2f $", usd)
    }
}

private fun formatRelativeTime(epochSeconds: Long): String {
    val now = System.currentTimeMillis() / 1000
    val diff = now - epochSeconds
    if (diff < 60) return "Just now"

    val years = diff / 31536000
    val months = (diff % 31536000) / 2592000
    val days = (diff % 2592000) / 86400
    val hours = (diff % 86400) / 3600
    val mins = (diff % 3600) / 60
    val secs = diff % 60

    val parts = mutableListOf<String>()
    if (years > 0) parts.add("${years}y")
    if (months > 0) parts.add("${months}m")
    if (days > 0) parts.add("${days}d")
    if (hours > 0) parts.add("${hours}h")
    if (mins > 0) parts.add("${mins}m")
    if (secs > 0 && years == 0L && months == 0L && days == 0L) parts.add("${secs}s")

    // Show up to 3 most significant units
    return parts.take(3).joinToString(" ")
}

@Composable
fun AddWalletDialog(
    onDismiss: () -> Unit,
    onAdd: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val base58Regex = Regex("^[1-9A-HJ-NP-Za-km-z]+$")
    
    // Custom Dialog Content using GlassCard
    // Custom Dialog Content - Solid and "Bigger"
    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .clip(RoundedCornerShape(24.dp)),
            color = MaterialTheme.colorScheme.surface, // Solid background
            tonalElevation = 8.dp,
            shadowElevation = 12.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "TRACK WALLET",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                OutlinedTextField(
                    value = text,
                    onValueChange = { 
                        // Reject spaces and newlines
                        text = it.filter { char -> !char.isWhitespace() }
                    },
                    placeholder = { 
                        Text(
                            "Paste Solana Address", 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        ) 
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        cursorColor = MaterialTheme.colorScheme.primary,
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), // Slight tint
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.1f),
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )

                Spacer(modifier = Modifier.height(32.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Cancel Button
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .clickable(onClick = onDismiss),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "CANCEL",
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    val isValid = text.length in 32..44 && text.matches(base58Regex)
                    
                    // Add Button
                    Button(
                        onClick = {
                            if (isValid) {
                                onAdd(text)
                            }
                        },
                        enabled = isValid,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isValid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (isValid) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                        )
                    ) {
                        Text(
                            "ADD",
                            fontWeight = FontWeight.Bold 
                        )
                    }
                }
            }
        }
    }
}

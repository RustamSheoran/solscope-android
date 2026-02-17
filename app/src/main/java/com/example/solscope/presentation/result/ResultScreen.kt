package com.example.solscope.presentation.result

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.solscope.R
import com.example.solscope.domain.model.ErrorType
import com.example.solscope.domain.model.ResultState
import com.example.solscope.domain.risk.RiskLevel
import com.example.solscope.domain.risk.RiskScore
import com.example.solscope.presentation.components.GlassButton
import com.example.solscope.presentation.components.GlassCard
import com.example.solscope.ui.theme.CyberColors
import com.example.solscope.presentation.watchlist.WatchlistViewModel
import com.example.solscope.presentation.components.AssetDetailSheet
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Add

@Composable
fun ResultScreen(
    address: String,
    onBack: () -> Unit,
    viewModel: ResultViewModel = viewModel(factory = ResultViewModel.Factory),
    watchlistViewModel: WatchlistViewModel
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(address) {
        if (state is ResultState.Idle) {
             viewModel.analyze(address)
        }
    }

    val showBackButton = state is ResultState.Success

    val watchlist by watchlistViewModel.watchlist.collectAsState()
    val isWatchlisted = remember(watchlist, address) { watchlist.any { it.address == address } }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(16.dp)) // Reduced top spacing

            // Header Row: Back | Address+Copy | Watchlist
            AnimatedVisibility(
                visible = showBackButton,
                enter = fadeIn(tween(300)),
                exit = fadeOut(tween(150))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Back Button
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f), CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    // Only show Address/Copy & Watchlist if successful
                    if (state is ResultState.Success) {
                        // Address & Copy
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = address.take(4) + "..." + address.takeLast(4),
                                style = MaterialTheme.typography.bodyMedium,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Icon(
                                painter = painterResource(id = R.drawable.ic_copy),
                                contentDescription = "Copy Address",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .size(14.dp)
                                    .clickable {
                                        val clipboard = context.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                                        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("Address", address))
                                        android.widget.Toast.makeText(context, "Address Copied", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                            )
                        }

                        // Watchlist Toggle
                        IconButton(
                            onClick = {
                                if (isWatchlisted) {
                                    watchlistViewModel.removeAddress(address)
                                } else {
                                    watchlistViewModel.addAddress(address)
                                }
                            },
                            modifier = Modifier
                                .background(
                                    if (isWatchlisted) MaterialTheme.colorScheme.primary 
                                    else MaterialTheme.colorScheme.surfaceVariant,
                                    CircleShape
                                )
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = if (isWatchlisted) Icons.Filled.Star else Icons.Filled.Add,
                                contentDescription = if (isWatchlisted) "Remove from Watchlist" else "Add to Watchlist",
                                tint = if (isWatchlisted) com.example.solscope.ui.theme.CyberColors.NeonMint else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Lightweight crossfade — no measure overhead like AnimatedContent
            Crossfade(
                targetState = state,
                animationSpec = tween(350),
                label = "ResultStateCrossfade"
            ) { targetState ->
                when (targetState) {
                    is ResultState.Idle -> { Box(modifier = Modifier.fillMaxSize()) }
                    is ResultState.Loading -> {
                        LoadingView(modifier = Modifier.fillMaxSize())
                    }
                    is ResultState.Error -> {
                        ErrorView(
                            message = targetState.message,
                            errorType = targetState.errorType,
                            onRetry = { viewModel.analyze(address) },
                            onBack = onBack,
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    is ResultState.Success -> {
                        SuccessView(score = targetState.data)
                    }
                }
            }
        }
    }
}

@Composable
fun LoadingView(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "SCANNING BLOCKCHAIN...",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            letterSpacing = 2.sp
        )
    }
}

@Composable
fun ErrorView(
    message: String,
    errorType: ErrorType,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val title = when (errorType) {
        ErrorType.INVALID_ADDRESS -> "INVALID TARGET"
        ErrorType.NETWORK_ERROR -> "CONNECTION LOST"
        ErrorType.RATE_LIMITED -> "RATE LIMIT"
        ErrorType.SERVER_ERROR -> "SYSTEM ERROR"
        ErrorType.UNKNOWN -> "UNKNOWN ERROR"
    }

    val iconTint = MaterialTheme.colorScheme.error

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.3f))

        GlassCard(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Error",
                    tint = iconTint,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    message,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        if (errorType == ErrorType.INVALID_ADDRESS) {
            GlassButton(
                onClick = onBack,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("RETURN TO HOME", fontWeight = FontWeight.Bold)
            }
        } else {
            GlassButton(
                onClick = onRetry,
                containerColor = MaterialTheme.colorScheme.primary,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("RETRY CONNECTION", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            GlassButton(
                onClick = onBack,
                containerColor = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            ) {
                Text("RETURN TO HOME", fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.weight(0.7f))
    }
}

@Composable
fun SuccessView(score: RiskScore) {
    val uriHandler = LocalUriHandler.current

    val riskColor = when (score.level) {
        RiskLevel.SAFE -> CyberColors.NeonMint
        RiskLevel.WARNING -> CyberColors.NeonOrange
        RiskLevel.CRITICAL -> CyberColors.NeonRed
        RiskLevel.UNKNOWN -> Color.Gray
    }

    var animationTriggered by remember { mutableStateOf(false) }
    val animatedScore by animateIntAsState(
        targetValue = if (animationTriggered) score.score else 0,
        animationSpec = tween(durationMillis = 1200, easing = FastOutSlowInEasing),
        label = "Score Animation"
    )

    LaunchedEffect(Unit) { animationTriggered = true }

    val tokens = score.snapshot?.tokenAccounts ?: emptyList()
    val allSignatures = score.snapshot?.recentSignatures ?: emptyList()

    val pageSize = 10

    // Tab state: 0 = Activity, 1 = Assets
    var selectedTab by remember { mutableIntStateOf(0) }

    // Token sorting and pagination
    val sortedTokens = remember(tokens) { tokens.sortedByDescending { it.uiAmount } }
    var tokenPage by remember { mutableIntStateOf(0) }
    val tokenTotalPages = if (sortedTokens.isEmpty()) 1 else ((sortedTokens.size - 1) / pageSize) + 1
    val pageTokens = sortedTokens.drop(tokenPage * pageSize).take(pageSize)

    // Transaction pagination
    var currentPage by remember { mutableIntStateOf(0) }
    val totalPages = if (allSignatures.isEmpty()) 1 else ((allSignatures.size - 1) / pageSize) + 1
    val pageSignatures = allSignatures.drop(currentPage * pageSize).take(pageSize)

    // Detail sheet states
    var selectedSignature by remember { mutableStateOf<com.example.solscope.data.rpc.model.SignatureInfo?>(null) }
    var selectedAsset by remember { mutableStateOf<com.example.solscope.data.rpc.model.TokenAccountInfo?>(null) }
    
    val context = LocalContext.current

    // Transaction Detail Sheet overlay
    selectedSignature?.let { sigInfo ->
        com.example.solscope.presentation.components.TransactionDetailSheet(
            signatureInfo = sigInfo,
            onDismiss = { selectedSignature = null },
            onOpenBrowser = { sig ->
                selectedSignature = null
                uriHandler.openUri("https://solscan.io/tx/$sig")
            }
        )
    }
    
    // Asset Detail Sheet overlay
    selectedAsset?.let { token ->
        AssetDetailSheet(
            token = token,
            onDismiss = { selectedAsset = null },
            onOpenBrowser = { mint ->
                selectedAsset = null
                uriHandler.openUri("https://solscan.io/token/$mint")
            }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // HUD Circle
        item(key = "hud") {
            Spacer(modifier = Modifier.height(24.dp))
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(200.dp)
                    .border(1.dp, riskColor.copy(alpha = 0.3f), CircleShape)
                    .background(riskColor.copy(alpha = 0.05f), CircleShape)
            ) {
                CircularProgressIndicator(
                    progress = { animatedScore / 100f },
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    color = riskColor,
                    strokeWidth = 8.dp,
                    trackColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    strokeCap = androidx.compose.ui.graphics.StrokeCap.Butt
                )
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "RISK SCORE",
                        style = MaterialTheme.typography.labelSmall,
                        color = riskColor.copy(alpha = 0.8f),
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = animatedScore.toString(),
                        style = MaterialTheme.typography.displayLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 72.sp
                    )
                }
            }
        }

        // Status label
        item(key = "status") {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = when (score.level) {
                    RiskLevel.SAFE -> "SECURE WALLET"
                    RiskLevel.WARNING -> "CAUTION ADVISED"
                    RiskLevel.CRITICAL -> "HIGH RISK DETECTED"
                    else -> "UNKNOWN STATUS"
                },
                style = MaterialTheme.typography.headlineMedium,
                color = riskColor
            )
            Spacer(modifier = Modifier.height(16.dp))
        }

        // SOL Balance
        score.snapshot?.let { snap ->
            item(key = "balance_card") {
                val solBalance = snap.balance.toDouble() / 1_000_000_000.0
                val usdValue = solBalance * 170.0  // approximate SOL price
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "BALANCE",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                letterSpacing = 1.5.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                String.format("%.2f SOL", solBalance),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        Text(
                            formatUsdValue(usdValue),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.secondary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Positive Signals (only show if there are any)
        if (score.positives.isNotEmpty()) {
            item(key = "positives_header") {
                Text(
                    "POSITIVE SIGNALS",
                    style = MaterialTheme.typography.labelLarge,
                    color = CyberColors.NeonMint,
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 12.dp)
                )
            }

            items(score.positives.size, key = { "positive_$it" }) { index ->
                val positive = score.positives[index]
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✓", color = CyberColors.NeonMint, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(positive, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // Risk Factors (only show header if there are actual risks)
        if (score.reasons.isNotEmpty()) {
            item(key = "factors_header") {
                Spacer(modifier = Modifier.height(if (score.positives.isNotEmpty()) 16.dp else 0.dp))
                Text(
                    "RISK FACTORS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 12.dp)
                )
            }

            items(score.reasons.size, key = { "reason_$it" }) { index ->
                val reason = score.reasons[index]
                GlassCard(modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("⚠", color = riskColor, fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(reason, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }

        // No findings at all
        if (score.reasons.isEmpty() && score.positives.isEmpty()) {
            item(key = "no_factors") {
                Text(
                    "ANALYSIS",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 12.dp)
                )
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "No anomalous patterns detected.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }
        }

        // ── Tab Toggle: Assets | Activity ──
        item(key = "tab_toggle") {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.onSurface.copy(alpha = 0.05f))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Activity tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (selectedTab == 0) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { selectedTab = 0 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Activity (${allSignatures.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 0) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                // Assets tab
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (selectedTab == 1) MaterialTheme.colorScheme.primary
                            else Color.Transparent
                        )
                        .clickable { selectedTab = 1 }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Assets (${tokens.size})",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTab == 1) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        // ═══════════════════════════════════
        //  ASSETS TAB
        // ═══════════════════════════════════
        if (selectedTab == 1) {
            if (sortedTokens.isEmpty()) {
                item(key = "no_assets") {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "No token holdings found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            } else {
                // Assets header
                item(key = "assets_header") {
                    Text(
                        "ASSETS (${sortedTokens.size})",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth().padding(start = 12.dp, bottom = 8.dp)
                    )
                }

                items(pageTokens.size, key = { "token_${tokenPage}_$it" }) { index ->
                    val token = pageTokens[index]
                    com.example.solscope.presentation.result.AssetItem(
                        mint = token.mint,
                        amount = token.uiAmount,
                        onClick = { selectedAsset = token }
                    )
                }

                // Token page navigation
                item(key = "token_page_nav") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GlassButton(
                            onClick = { if (tokenPage > 0) tokenPage-- },
                            containerColor = if (tokenPage > 0) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                "← PREV",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (tokenPage > 0) MaterialTheme.colorScheme.onSecondary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                        Text(
                            "Page ${tokenPage + 1} of $tokenTotalPages",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        GlassButton(
                            onClick = { if (tokenPage < tokenTotalPages - 1) tokenPage++ },
                            containerColor = if (tokenPage < tokenTotalPages - 1) MaterialTheme.colorScheme.secondary
                                else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
                            modifier = Modifier.height(40.dp)
                        ) {
                            Text(
                                "NEXT →",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = if (tokenPage < tokenTotalPages - 1) MaterialTheme.colorScheme.onSecondary
                                    else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                        }
                    }
                }
            }
        }

        // ═══════════════════════════════════
        //  ACTIVITY TAB
        // ═══════════════════════════════════
        if (selectedTab == 0) {
            if (allSignatures.isEmpty()) {
                item(key = "no_activity") {
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            "No recent transactions found.",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                            fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                    }
                }
            } else {
                items(pageSignatures.size, key = { "sig_${currentPage}_$it" }) { index ->
                    val sigInfo = pageSignatures[index]
                    val truncated = sigInfo.signature.take(5) + "..." + sigInfo.signature.takeLast(5)
                    val timeText = sigInfo.blockTime?.let { ts ->
                        formatRelativeTime(ts)
                    } ?: ""
                    val isError = sigInfo.err != null

                    GlassCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp)
                            .clickable { selectedSignature = sigInfo }
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = truncated,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                                )
                                if (timeText.isNotEmpty()) {
                                    Text(
                                        text = timeText,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    )
                                }
                            }
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                if (isError) {
                                    Text(
                                        "FAIL",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Text(
                                    text = "Details →",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.tertiary
                                )
                            }
                        }
                    }
                }

                // Page navigation
                item(key = "page_nav") {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
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

    return parts.take(3).joinToString(" ")
}

private fun formatUsdValue(usd: Double): String {
    return when {
        usd >= 1_000_000_000 -> String.format("%.1fB $", usd / 1_000_000_000)
        usd >= 1_000_000 -> String.format("%.1fM $", usd / 1_000_000)
        usd >= 1_000 -> String.format("%.1fK $", usd / 1_000)
        else -> String.format("%.2f $", usd)
    }
}

fun formatTokenAmount(amount: Double): String {
    return when {
        amount >= 1_000_000_000_000 -> String.format("%.1fT", amount / 1_000_000_000_000)
        amount >= 1_000_000_000 -> String.format("%.1fB", amount / 1_000_000_000)
        amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
        amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
        amount >= 1 -> String.format("%.2f", amount)
        else -> String.format("%.4f", amount)
    }
}


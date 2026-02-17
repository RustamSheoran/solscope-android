package com.example.solscope.presentation.result

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.solscope.domain.model.ErrorType
import com.example.solscope.domain.model.ResultState
import com.example.solscope.domain.risk.RiskLevel
import com.example.solscope.domain.risk.RiskScore
import com.example.solscope.presentation.components.ClayButton
import com.example.solscope.presentation.components.ClayCard
import com.example.solscope.presentation.theme.*

@Composable
fun ResultScreen(
    address: String,
    onBack: () -> Unit,
    viewModel: ResultViewModel = viewModel(factory = ResultViewModel.Factory)
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(address) {
        if (state is ResultState.Idle) {
             viewModel.analyze(address)
        }
    }

    val showBackButton = state is ResultState.Success

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AutumnBackground)
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            if (showBackButton) {
                ClayButton(
                    onClick = onBack,
                    backgroundColor = AutumnSecondary,
                    modifier = Modifier.height(48.dp),
                    cornerRadius = 24.dp
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Back",
                            tint = AutumnText,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Back",
                            color = AutumnText,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (val s = state) {
                is ResultState.Idle -> { /* No-op */ }
                is ResultState.Loading -> {
                    LoadingView(modifier = Modifier.weight(1f))
                }
                is ResultState.Error -> {
                    ErrorView(
                        message = s.message,
                        errorType = s.errorType,
                        onRetry = { viewModel.analyze(address) },
                        onBack = onBack,
                        modifier = Modifier.weight(1f)
                    )
                }
                is ResultState.Success -> {
                    SuccessView(score = s.data)
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
            color = AutumnPrimary,
            modifier = Modifier.size(48.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Analyzing wallet activity...",
            color = AutumnText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
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
    // Different title and icon per error type
    val title = when (errorType) {
        ErrorType.INVALID_ADDRESS -> "Wrong Wallet Address"
        ErrorType.NETWORK_ERROR -> "Connection Failed"
        ErrorType.RATE_LIMITED -> "Slow Down"
        ErrorType.SERVER_ERROR -> "Server Issue"
        ErrorType.UNKNOWN -> "Analysis Failed"
    }

    val iconTint = when (errorType) {
        ErrorType.INVALID_ADDRESS -> AutumnWarning
        ErrorType.NETWORK_ERROR -> AutumnCritical
        else -> AutumnCritical
    }

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(0.3f)) 
        
        ClayCard(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp), 
            color = AutumnBackground
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Filled.Warning,
                    contentDescription = "Error",
                    tint = iconTint,
                    modifier = Modifier.size(56.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    title,
                    color = AutumnText,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    message,
                    color = AutumnText.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        if (errorType == ErrorType.INVALID_ADDRESS) {
            // Invalid address → Go back to home to re-enter
            ClayButton(
                onClick = onBack,
                backgroundColor = AutumnText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(64.dp),
                cornerRadius = 20.dp
            ) {
                Text(
                    "Go Back to Home",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AutumnSecondary
                )
            }
        } else {
            ClayButton(
                onClick = onRetry,
                backgroundColor = AutumnText,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(64.dp),
                cornerRadius = 20.dp
            ) {
                Text(
                    "Retry",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = AutumnSecondary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            ClayButton(
                onClick = onBack,
                backgroundColor = AutumnSecondary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(52.dp),
                cornerRadius = 20.dp
            ) {
                Text(
                    "Go Back to Home",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = AutumnText
                )
            }
        }
        
        Spacer(modifier = Modifier.weight(0.7f)) 
    }
}

@Composable
fun SuccessView(score: RiskScore) {
    val scrollState = rememberScrollState()

    // Map to Autumn Palette
    val riskColor = when (score.level) {
        RiskLevel.SAFE -> AutumnSafe
        RiskLevel.WARNING -> AutumnWarning
        RiskLevel.CRITICAL -> AutumnCritical
        RiskLevel.UNKNOWN -> Color.Gray
    }

    var animationTriggered by remember { mutableStateOf(false) }
    val animatedScore by animateIntAsState(
        targetValue = if (animationTriggered) score.score else 0,
        animationSpec = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
        label = "Score Animation"
    )

    LaunchedEffect(Unit) {
        animationTriggered = true
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        // Score Circle
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(180.dp)
                .background(AutumnBackground, CircleShape)
                .border(8.dp, Color.White.copy(alpha = 0.6f), CircleShape)
        ) {
             CircularProgressIndicator(
                progress = { animatedScore / 100f },
                modifier = Modifier.fillMaxSize().padding(16.dp),
                color = riskColor,
                strokeWidth = 12.dp,
                trackColor = ClayShadowDark.copy(alpha = 0.3f),
                strokeCap = androidx.compose.ui.graphics.StrokeCap.Round
            )
            
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = animatedScore.toString(),
                    fontSize = 64.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AutumnText
                )
                Text(
                    text = "/ 100",
                    fontSize = 14.sp,
                    color = AutumnText.copy(alpha = 0.5f)
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = when (score.level) {
                RiskLevel.SAFE -> "Low Risk Wallet"
                RiskLevel.WARNING -> "Moderate Risk"
                RiskLevel.CRITICAL -> "High Risk Warning"
                else -> "Unknown Risk"
            },
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = riskColor
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            "Risk Factors",
            color = AutumnText,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (score.reasons.isEmpty()) {
             ClayCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), color = AutumnBackground) {
                 Text(
                     "No specific risk factors detected.",
                     color = AutumnText.copy(alpha = 0.7f),
                     fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                 )
             }
        } else {
            score.reasons.forEach { reason ->
                ClayCard(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), color = AutumnBackground) {
                    Row(verticalAlignment = Alignment.Top) {
                        Text(
                            "•",
                            color = riskColor,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 24.sp
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            reason,
                            color = AutumnText,
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
        
        Spacer(modifier = Modifier.height(48.dp))
    }
}

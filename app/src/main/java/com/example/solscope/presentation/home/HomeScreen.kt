package com.example.solscope.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.solscope.presentation.components.PrimaryCard
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {

    var walletAddress by remember { mutableStateOf("") }
    var riskScore by remember { mutableStateOf(0) }
    var isAnalyzing by remember { mutableStateOf(false) }

    // Animated color based on risk
    val riskColor by animateColorAsState(
        targetValue = when {
            riskScore < 30 -> Color(0xFF4CAF50)
            riskScore < 70 -> Color(0xFFFFB020)
            else -> Color(0xFFFF4D4F)
        },
        animationSpec = tween(600),
        label = ""
    )

    // Animated number counter
    val animatedScore by animateIntAsState(
        targetValue = riskScore,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = ""
    )

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "SolScope",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent
                )
            )
        }
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF023859),
                            Color(0xFF011C40)
                        )
                    )
                )
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {

            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(modifier = Modifier.height(24.dp))

                // Wallet Input
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically { it / 3 }
                ) {
                    PrimaryCard {
                        OutlinedTextField(
                            value = walletAddress,
                            onValueChange = { walletAddress = it },
                            label = { Text("Solana Wallet Address") },
                            modifier = Modifier.fillMaxWidth(),
                            maxLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Color(0xFF54ACBF),
                                unfocusedBorderColor = Color(0xFF2F4F6F),
                                cursorColor = Color(0xFF54ACBF),
                                focusedLabelColor = Color(0xFF54ACBF)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Button
                val scale by animateFloatAsState(
                    targetValue = if (isAnalyzing) 0.96f else 1f,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = ""
                )

                Button(
                    onClick = {
                        isAnalyzing = true
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .scale(scale),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF54ACBF)
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    if (isAnalyzing) {
                        CircularProgressIndicator(
                            color = Color.White,
                            strokeWidth = 2.dp,
                            modifier = Modifier.size(18.dp)
                        )
                    } else {
                        Text(
                            "Analyze Wallet",
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Fake analysis simulation (UI only)
                LaunchedEffect(isAnalyzing) {
                    if (isAnalyzing) {
                        delay(1200)
                        riskScore = (20..85).random()
                        isAnalyzing = false
                    }
                }

                // Risk Card
                AnimatedVisibility(
                    visible = riskScore > 0,
                    enter = fadeIn(animationSpec = tween(500)) +
                            slideInVertically(animationSpec = tween(500)) { it / 2 }
                ) {
                    PrimaryCard {

                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth()
                        ) {

                            Text(
                                text = "Risk Score",
                                color = Color(0xFFA7EBF2),
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = animatedScore.toString(),
                                fontSize = 48.sp,
                                fontWeight = FontWeight.Bold,
                                color = riskColor
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            Text(
                                text = when {
                                    animatedScore < 30 -> "Low Risk"
                                    animatedScore < 70 -> "Medium Risk"
                                    else -> "High Risk"
                                },
                                color = riskColor
                            )
                        }
                    }
                }
            }
        }
    }
}
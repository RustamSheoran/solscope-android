package com.example.solscope.presentation.home

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import com.example.solscope.presentation.components.ClayButton
import com.example.solscope.presentation.components.ClayCard
import com.example.solscope.presentation.theme.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    onAnalyze: (String) -> Unit
) {

    var walletAddress by remember { mutableStateOf("") }
    val platformContext = LocalContext.current
    val keyboardController = LocalSoftwareKeyboardController.current
    
    // Demo Address (Known active whale wallet for testing)
    val demoAddress = "FxvohgxLw4GbpATWEVxoNkyS9wV3G27gt3whnXJi8rqa"

    Scaffold(
        containerColor = AutumnBackground
    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 20.dp)
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top
            ) {

                Spacer(modifier = Modifier.height(100.dp))
                
                Text(
                    "SolScope",
                    fontWeight = FontWeight.Bold,
                    fontSize = 36.sp,
                    color = AutumnText
                )
                Text(
                    "Risk Analysis Engine",
                    fontSize = 18.sp,
                    color = AutumnText.copy(alpha = 0.8f) 
                )

                Spacer(modifier = Modifier.height(56.dp))

                // Wallet Input - 3D ClayCard
                ClayCard(
                    modifier = Modifier.fillMaxWidth(),
                    color = AutumnBackground
                ) {
                    Text(
                        "Target Wallet",
                        fontWeight = FontWeight.Bold,
                        color = AutumnText,
                        fontSize = 18.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = walletAddress,
                        onValueChange = { input ->
                            // Reject spaces and newlines
                            val filtered = input.filter { !it.isWhitespace() }
                            walletAddress = filtered
                        },
                        placeholder = { 
                            Text(
                                "Enter Solana address...",
                                color = AutumnText.copy(alpha = 0.6f) 
                            ) 
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        maxLines = 1,
                        textStyle = TextStyle(
                            color = AutumnText,
                            fontWeight = FontWeight.Medium,
                            fontSize = 16.sp
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Ascii,
                            imeAction = ImeAction.Done
                        ),
                        trailingIcon = {
                            IconButton(onClick = {
                                val clipboard = platformContext.getSystemService(android.content.Context.CLIPBOARD_SERVICE) as? android.content.ClipboardManager
                                val clipData = clipboard?.primaryClip
                                if (clipData != null && clipData.itemCount > 0) {
                                    val text = clipData.getItemAt(0).text.toString()
                                    if (text.isNotBlank()) {
                                        walletAddress = text.filter { !it.isWhitespace() }
                                    }
                                }
                            }) {
                                Text(
                                    "Paste",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = AutumnText.copy(alpha = 0.6f)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AutumnPrimary,
                            unfocusedBorderColor = Color.Transparent, 
                            cursorColor = AutumnPrimary,
                            focusedLabelColor = AutumnPrimary,
                            unfocusedContainerColor = Color.White.copy(alpha = 0.5f), 
                            focusedContainerColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Solana addresses are 32-44 chars (base58)
                val isValidAddress = walletAddress.length >= 32

                // Color transition only when valid address (32+ chars)
                val analyzeColor by animateColorAsState(
                    targetValue = if (isValidAddress) AutumnText else AutumnSecondary,
                    animationSpec = tween(durationMillis = 200),
                    label = "AnalyzeColor"
                )
                val analyzeTextColor by animateColorAsState(
                    targetValue = if (isValidAddress) Color.White else AutumnText.copy(alpha = 0.5f),
                    animationSpec = tween(durationMillis = 200),
                    label = "AnalyzeTextColor"
                )
                val analyzeText = if (isValidAddress) "Analyze Wallet" else "Analyze"

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Analyze Button — color fills when valid address entered
                    ClayButton(
                        onClick = {
                            if (isValidAddress) {
                                keyboardController?.hide()
                                onAnalyze(walletAddress)
                            }
                        },
                        enabled = isValidAddress,
                        backgroundColor = analyzeColor,
                        modifier = Modifier
                            .weight(1f)
                            .height(72.dp),
                        cornerRadius = 20.dp
                    ) {
                        Text(
                            analyzeText,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = if (isValidAddress) 21.sp else 18.sp,
                            letterSpacing = if (isValidAddress) 0.5.sp else 0.sp,
                            color = analyzeTextColor
                        )
                    }

                    // Demo Button — fades when valid address entered
                    AnimatedVisibility(
                        visible = !isValidAddress,
                        enter = fadeIn(tween(150)) + expandHorizontally(tween(150)),
                        exit = fadeOut(tween(150)) + shrinkHorizontally(tween(150))
                    ) {
                        ClayButton(
                            onClick = { walletAddress = demoAddress },
                            backgroundColor = AutumnPrimary,
                            modifier = Modifier
                                .width(100.dp)
                                .height(72.dp),
                            cornerRadius = 20.dp
                        ) {
                            Text(
                                "Demo",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 17.sp,
                                color = AutumnText
                            )
                        }
                    }
                }
            }
        }
    }
}
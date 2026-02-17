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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.solscope.presentation.components.ConnectWalletButton
import com.example.solscope.presentation.components.GlassCard
import com.example.solscope.presentation.components.GlassButton
import com.example.solscope.ui.theme.CyberColors
import androidx.compose.ui.res.painterResource
import com.example.solscope.R

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun HomeScreen(
    onAnalyze: (String) -> Unit,
    connectedWallet: String? = null,
    onConnect: () -> Unit = {},
    onDisconnect: () -> Unit = {},
    isDarkTheme: Boolean = true,
    onToggleTheme: () -> Unit = {}
) {

    var address by remember { mutableStateOf("") }
    val platformContext = LocalContext.current
    val focusManager = LocalFocusManager.current
    
    // Demo Address (Known active whale wallet for testing)
    val demoAddress = "FxvohgxLw4GbpATWEVxoNkyS9wV3G27gt3whnXJi8rqa"

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    focusManager.clearFocus()
                })
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            // Top bar: Theme toggle + Connect
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        painter = painterResource(
                            id = if (isDarkTheme) R.drawable.ic_sun else R.drawable.ic_moon
                        ),
                        contentDescription = if (isDarkTheme) "Light Mode" else "Dark Mode",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                ConnectWalletButton(
                    connectedAddress = connectedWallet,
                    onConnect = onConnect,
                    onDisconnect = onDisconnect
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Hero branding
            Text(
                text = "SolScope",
                style = MaterialTheme.typography.displayLarge,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 40.sp,
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Solana Risk Analysis Engine",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Wallet Input
            GlassCard(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(
                        "Target Wallet",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    OutlinedTextField(
                        value = address,
                        onValueChange = { input ->
                            val filtered = input.filter { !it.isWhitespace() }
                            address = filtered
                        },
                        placeholder = {
                            Text(
                                "Enter Solana address...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        maxLines = 1,
                        textStyle = TextStyle(
                            color = MaterialTheme.colorScheme.onSurface,
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
                                        address = text.filter { !it.isWhitespace() }
                                    }
                                }
                            }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_paste),
                                    contentDescription = "Paste",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            val base58Regex = Regex("^[1-9A-HJ-NP-Za-km-z]+$")
            val isValidAddress = address.length in 32..44 && address.matches(base58Regex)
            val analyzeText = if (isValidAddress) "ANALYZE WALLET" else "ANALYZE"

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Analyze Button — uses onPrimary for text so it's visible in both modes
                GlassButton(
                    onClick = {
                        if (isValidAddress) {
                            focusManager.clearFocus()
                            onAnalyze(address)
                        }
                    },
                    enabled = isValidAddress,
                    containerColor = if (isValidAddress) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.12f),
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                ) {
                    Text(
                        analyzeText,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (isValidAddress) MaterialTheme.colorScheme.onPrimary
                            else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                    )
                }

                // Demo Button
                AnimatedVisibility(
                    visible = !isValidAddress,
                    enter = fadeIn(tween(150)) + expandHorizontally(tween(150)),
                    exit = fadeOut(tween(150)) + shrinkHorizontally(tween(150))
                ) {
                    GlassButton(
                        onClick = { address = demoAddress },
                        containerColor = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier
                            .width(100.dp)
                            .height(56.dp)
                    ) {
                        Text(
                            "DEMO",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondary
                        )
                    }
                }
            }
        }
    }
}
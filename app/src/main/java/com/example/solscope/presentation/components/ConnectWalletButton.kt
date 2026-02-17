package com.example.solscope.presentation.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ConnectWalletButton(
    connectedAddress: String?,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isConnected = connectedAddress != null
    val text = if (connectedAddress != null) {
        connectedAddress.take(4) + "..." + connectedAddress.takeLast(4)
    } else {
        "Connect"
    }

    val bgColor = if (isConnected)
        MaterialTheme.colorScheme.surfaceVariant
    else
        MaterialTheme.colorScheme.primary

    GlassButton(
        onClick = if (isConnected) onDisconnect else onConnect,
        containerColor = bgColor,
        modifier = modifier
    ) {
        Text(
            text = text,
            color = if (isConnected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
        )
    }
}


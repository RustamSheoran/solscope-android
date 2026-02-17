package com.example.solscope.data.wallet

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

// STUB IMPLEMENTATION - Dependency not resolving
class WalletConnectionManager {

    private val _connectedWallet = MutableStateFlow<String?>(null)
    val connectedWallet: StateFlow<String?> = _connectedWallet.asStateFlow()

    // Stub connect function
    // In real impl, this would take ActivityResultSender
    suspend fun connect(sender: Any? = null) {
        delay(1000) // Simulate network delay
        // Mock success
        _connectedWallet.value = "FxvohgxLw4GbpATWEVxoNkyS9wV3G27gt3whnXJi8rqa" 
    }

    fun disconnect() {
        _connectedWallet.value = null
    }
}

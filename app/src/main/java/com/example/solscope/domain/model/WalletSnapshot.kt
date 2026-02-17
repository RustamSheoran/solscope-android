package com.example.solscope.domain.model

/**
 * Data contract for a wallet's current state.
 * To be fully defined in Phase 2.
 */
data class WalletSnapshot(
    val address: String,
    val balance: Long,
    val transactionCount: Int,
    val isExecutable: Boolean
)

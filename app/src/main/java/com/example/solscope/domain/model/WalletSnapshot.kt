package com.example.solscope.domain.model

import com.example.solscope.data.rpc.model.SignatureInfo
import com.example.solscope.data.rpc.model.TokenAccountInfo

/**
 * Data contract for a wallet's current on-chain state.
 */
data class WalletSnapshot(
    val address: String,
    val balance: Long,
    val transactionCount: Int,
    val isExecutable: Boolean,
    val tokenAccounts: List<TokenAccountInfo> = emptyList(),
    val recentSignatures: List<SignatureInfo> = emptyList()
)

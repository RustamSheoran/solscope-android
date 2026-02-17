package com.example.solscope.data.rpc.model

/**
 * Parsed SPL token account information from getTokenAccountsByOwner.
 */
data class TokenAccountInfo(
    val mint: String,
    val amount: String,
    val decimals: Int,
    val uiAmount: Double
)

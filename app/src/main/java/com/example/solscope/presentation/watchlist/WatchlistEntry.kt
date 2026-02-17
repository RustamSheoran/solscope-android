package com.example.solscope.presentation.watchlist

data class WatchlistEntry(
    val address: String,
    val balance: Double? = null,
    val balanceLoading: Boolean = false,
    val error: Boolean = false,
    val lastTxnTime: Long? = null
)

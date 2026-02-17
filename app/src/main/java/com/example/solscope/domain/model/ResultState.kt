package com.example.solscope.domain.model

sealed class ResultState<out T> {
    object Idle : ResultState<Nothing>()
    object Loading : ResultState<Nothing>()
    data class Success<T>(val data: T) : ResultState<T>()
    data class Error(
        val message: String,
        val errorType: ErrorType = ErrorType.UNKNOWN,
        val throwable: Throwable? = null
    ) : ResultState<Nothing>()
}

enum class ErrorType {
    INVALID_ADDRESS,   // Wrong/malformed wallet address
    NETWORK_ERROR,     // No internet or connection timeout
    SERVER_ERROR,      // Solana RPC server issues
    RATE_LIMITED,      // Too many requests
    UNKNOWN            // Fallback
}

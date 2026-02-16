package com.example.solscope.data.rpc

/**
 * Minimal exception type for Solana RPC related failures.
 *
 * This is intentionally lightweight and data-layer specific.
 */
class SolanaRpcException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)


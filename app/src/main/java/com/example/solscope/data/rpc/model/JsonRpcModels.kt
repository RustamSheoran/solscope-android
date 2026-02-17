package com.example.solscope.data.rpc.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Minimal JSON-RPC 2.0 request envelope.
 */
@Serializable
data class JsonRpcRequest<T>(
    val jsonrpc: String = "2.0",
    val id: Int,
    val method: String,
    val params: T
)

/**
 * Minimal JSON-RPC 2.0 response envelope.
 */
@Serializable
data class JsonRpcResponse<T>(
    val jsonrpc: String,
    val id: Int,
    val result: T? = null,
    val error: JsonRpcError? = null
) {
    companion object
}

/**
 * JSON-RPC 2.0 error object.
 */
@Serializable
data class JsonRpcError(
    val code: Int,
    val message: String,
    val data: String? = null
) {
    companion object
}

/**
 * Result for `getBalance` method.
 *
 * See: https://docs.solana.com/api/http#getbalance
 */
@Serializable
data class GetBalanceResult(
    val context: RpcContext,
    val value: Long
)

@Serializable
data class RpcContext(
    val slot: Long
)

/**
 * Item returned from `getSignaturesForAddress`.
 *
 * Only the fields required for Phase 2 are modelled.
 */
@Serializable
data class SignatureInfo(
    val signature: String,
    val slot: Long,
    @SerialName("blockTime")
    val blockTime: Long? = null,
    val err: SignatureErrorInfo? = null
)

@Serializable
data class SignatureErrorInfo(
    val code: Int? = null,
    val message: String? = null
)


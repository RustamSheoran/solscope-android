package com.example.solscope.data.rpc

import com.example.solscope.data.rpc.model.GetBalanceResult
import com.example.solscope.data.rpc.model.JsonRpcRequest
import com.example.solscope.data.rpc.model.JsonRpcResponse
import com.example.solscope.data.rpc.model.SignatureInfo
import com.example.solscope.domain.model.SolanaNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * HTTP-based implementation of [SolanaRpcClient] using OkHttp
 * and kotlinx.serialization for lightweight JSON-RPC calls.
 */
class HttpSolanaRpcClient(
    private val client: OkHttpClient = OkHttpClient(),
    private val json: Json = Json {
        ignoreUnknownKeys = true
    }
) : SolanaRpcClient {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override suspend fun getBalance(
        address: String,
        network: SolanaNetwork
    ): Long = withContext(Dispatchers.IO) {
        val requestBody = buildJsonRpcRequestBody(
            method = "getBalance",
            params = listOf(address)
        )

        val httpRequest = Request.Builder()
            .url(resolveNetworkUrl(network))
            .post(requestBody)
            .build()

        executeAndParse<GetBalanceResult>(httpRequest).value
    }

    override suspend fun getSignaturesForAddress(
        address: String,
        network: SolanaNetwork,
        limit: Int
    ): List<String> = withContext(Dispatchers.IO) {
        val params = listOf(
            address,
            mapOf("limit" to limit)
        )

        val requestBody = buildJsonRpcRequestBody(
            method = "getSignaturesForAddress",
            params = params
        )

        val httpRequest = Request.Builder()
            .url(resolveNetworkUrl(network))
            .post(requestBody)
            .build()

        executeAndParse<List<SignatureInfo>>(httpRequest)
            .map { it.signature }
    }

    private fun resolveNetworkUrl(network: SolanaNetwork): String {
        return when (network) {
            SolanaNetwork.MAINNET -> "https://api.mainnet-beta.solana.com"
            SolanaNetwork.DEVNET -> "https://api.devnet.solana.com"
        }
    }

    private fun <T> buildJsonRpcRequestBody(
        method: String,
        params: T
    ) = JsonRpcRequest(
        id = nextRequestId(),
        method = method,
        params = params
    ).let { request ->
        val jsonString = json.encodeToString(request)
        jsonString.toRequestBody(jsonMediaType)
    }

    private suspend inline fun <reified T> executeAndParse(
        httpRequest: Request
    ): T = withContext(Dispatchers.IO) {
        val response = try {
            client.newCall(httpRequest).execute()
        } catch (t: Throwable) {
            throw SolanaRpcException("Network request to Solana RPC failed", t)
        }

        response.use { resp ->
            if (!resp.isSuccessful) {
                throw SolanaRpcException(
                    "Solana RPC HTTP error: ${resp.code} ${resp.message}"
                )
            }

            val bodyString = resp.body?.string()
                ?: throw SolanaRpcException("Solana RPC response body was null")

            val rpcResponse = try {
                json.decodeFromString<JsonRpcResponse<T>>(bodyString)
            } catch (t: Throwable) {
                throw SolanaRpcException("Failed to decode Solana RPC response", t)
            }

            rpcResponse.error?.let { error ->
                throw SolanaRpcException(
                    "Solana RPC error ${error.code}: ${error.message}"
                )
            }

            rpcResponse.result
                ?: throw SolanaRpcException("Solana RPC response missing result")
        }
    }

    private fun nextRequestId(): Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
}


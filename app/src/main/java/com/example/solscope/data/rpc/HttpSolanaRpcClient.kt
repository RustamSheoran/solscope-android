package com.example.solscope.data.rpc

import com.example.solscope.data.rpc.model.*
import com.example.solscope.domain.model.SolanaNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.util.concurrent.TimeUnit

/**
 * HTTP-based implementation of [SolanaRpcClient] using OkHttp
 * and Manual JSON parsing to avoid serialization issues.
 */
class HttpSolanaRpcClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build(),
    private val json: Json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
) : SolanaRpcClient {

    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()

    override suspend fun getBalance(
        address: String,
        network: SolanaNetwork
    ): Long = withContext(Dispatchers.IO) {
        val params = buildJsonArray {
            add(address)
        }
        
        val requestBody = buildManualJsonRpcRequestBody(
            method = "getBalance",
            params = params
        )

        val httpRequest = Request.Builder()
            .url(resolveNetworkUrl(network))
            .post(requestBody)
            .build()

        val resultElement = executeAndGetResult(httpRequest)
        
        // Manual Parsing for GetBalanceResult
        // Structure: { "context": { "slot": 123 }, "value": 123456 }
        val resultObj = resultElement.jsonObject
        val value = resultObj["value"]?.jsonPrimitive?.long 
            ?: throw SolanaRpcException("Missing 'value' in getBalance response")
            
        value
    }

    override suspend fun getSignaturesForAddress(
        address: String,
        network: SolanaNetwork,
        limit: Int
    ): List<String> = withContext(Dispatchers.IO) {
        val params = buildJsonArray {
            add(address)
            addJsonObject {
                put("limit", limit)
            }
        }

        val requestBody = buildManualJsonRpcRequestBody(
            method = "getSignaturesForAddress",
            params = params
        )

        val httpRequest = Request.Builder()
            .url(resolveNetworkUrl(network))
            .post(requestBody)
            .build()
        
        val resultElement = executeAndGetResult(httpRequest)
        
        // Manual Parsing for List<SignatureInfo>
        // Structure: [ { "signature": "...", ... }, ... ]
        val resultArray = resultElement.jsonArray
        resultArray.map { item ->
            val itemObj = item.jsonObject
            itemObj["signature"]?.jsonPrimitive?.content 
                ?: throw SolanaRpcException("Missing 'signature' in history item")
        }
    }

    override suspend fun getAccountInfo(
        address: String,
        network: SolanaNetwork
    ): AccountInfoValue? = withContext(Dispatchers.IO) {
        val params = buildJsonArray {
            add(address)
            addJsonObject {
                put("encoding", "jsonParsed")
            }
        }

        val requestBody = buildManualJsonRpcRequestBody(
            method = "getAccountInfo",
            params = params
        )

        val httpRequest = Request.Builder()
            .url(resolveNetworkUrl(network))
            .post(requestBody)
            .build()
            
        val resultElement = executeAndGetResult(httpRequest)
        
        // Manual Parsing for GetAccountInfoResult
        // Structure: { "context": ..., "value": { "data": ..., "owner": ... } or null }
        if (resultElement is JsonNull) return@withContext null
        val resultObj = resultElement.jsonObject
        
        val valueElement = resultObj["value"]
        if (valueElement == null || valueElement is JsonNull) {
            return@withContext null
        }
        
        val valueObj = valueElement.jsonObject
        
        // AccountInfoValue(data, executable, lamports, owner, rentEpoch)
        
        val owner = valueObj["owner"]?.jsonPrimitive?.content ?: ""
        val executable = valueObj["executable"]?.jsonPrimitive?.boolean ?: false
        val lamports = valueObj["lamports"]?.jsonPrimitive?.long ?: 0L
        
        // Handle rentEpoch safely as it can exceed Long.MAX_VALUE (UINT64)
        val rentEpochVal = valueObj["rentEpoch"]?.jsonPrimitive?.content ?: "0"
        val rentEpoch = rentEpochVal.toLongOrNull() ?: 0L // Default to 0 if overflow or invalid
        
        val data = emptyList<String>() 

        AccountInfoValue(
            data = data,
            executable = executable,
            lamports = lamports,
            owner = owner,
            rentEpoch = rentEpoch
        )
    }

    private fun resolveNetworkUrl(network: SolanaNetwork): String {
        return when (network) {
            SolanaNetwork.MAINNET -> "https://api.mainnet-beta.solana.com"
            SolanaNetwork.DEVNET -> "https://api.devnet.solana.com"
        }
    }

    private fun buildManualJsonRpcRequestBody(
        method: String,
        params: JsonElement
    ): okhttp3.RequestBody {
        val jsonObject = buildJsonObject {
            put("jsonrpc", "2.0")
            put("id", nextRequestId())
            put("method", method)
            put("params", params)
        }
        return jsonObject.toString().toRequestBody(jsonMediaType)
    }

    /**
     * Executes request and returns the 'result' JSON element.
     * Throws exception if error occurs.
     */
    private suspend fun executeAndGetResult(
        httpRequest: Request
    ): JsonElement = withContext(Dispatchers.IO) {
        val response = try {
            client.newCall(httpRequest).execute()
        } catch (t: Throwable) {
            // Include specific error message
            throw SolanaRpcException("Network request failed: ${t.message}", t)
        }

        response.use { resp ->
            if (!resp.isSuccessful) {
                throw SolanaRpcException(
                    "Solana RPC HTTP error: ${resp.code} ${resp.message}"
                )
            }

            val bodyString = resp.body?.string()
                ?: throw SolanaRpcException("Solana RPC response body was null")

            val jsonObject = try {
                json.parseToJsonElement(bodyString).jsonObject
            } catch (t: Throwable) {
                throw SolanaRpcException("Failed to decode Solana RPC response JSON", t)
            }

            // Check for error first
            if (jsonObject.containsKey("error")) {
                val errorElement = jsonObject["error"]
                if (errorElement != null) {
                   // Manual parsing
                    val errorObj = errorElement.jsonObject
                    val code = errorObj["code"]?.jsonPrimitive?.intOrNull ?: -1
                    val message = errorObj["message"]?.jsonPrimitive?.content ?: "Unknown error"
                    throw SolanaRpcException(
                        "Solana RPC error $code: $message"
                    )
                }
            }

            // Check for result
            if (jsonObject.containsKey("result")) {
                return@withContext jsonObject["result"] ?: JsonNull
            }
            
            throw SolanaRpcException("Solana RPC response missing result")
        }
    }

    private fun nextRequestId(): Int = (System.currentTimeMillis() % Int.MAX_VALUE).toInt()
}

package com.example.solscope.presentation.result

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.solscope.data.rpc.HttpSolanaRpcClient
import com.example.solscope.data.rpc.SolanaRpcClient
import com.example.solscope.data.rpc.SolanaRpcException
import com.example.solscope.data.rpc.WalletSnapshotBuilder
import com.example.solscope.domain.model.ErrorType
import com.example.solscope.domain.model.ResultState
import com.example.solscope.domain.model.SolanaNetwork
import com.example.solscope.domain.risk.DefaultRiskEngine
import com.example.solscope.domain.risk.RiskEngine
import com.example.solscope.domain.risk.RiskScore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class ResultViewModel(
    private val rpcClient: SolanaRpcClient,
    private val riskEngine: RiskEngine
) : ViewModel() {

    private val _uiState = MutableStateFlow<ResultState<RiskScore>>(ResultState.Idle)
    val uiState: StateFlow<ResultState<RiskScore>> = _uiState.asStateFlow()

    fun analyze(address: String) {
        _uiState.value = ResultState.Loading

        viewModelScope.launch {
            val sanitizedAddress = address.trim()
            try {
                val balance = rpcClient.getBalance(sanitizedAddress, SolanaNetwork.MAINNET)
                val history = rpcClient.getSignaturesForAddress(sanitizedAddress, SolanaNetwork.MAINNET, 50)
                val accountInfo = rpcClient.getAccountInfo(sanitizedAddress, SolanaNetwork.MAINNET)

                val snapshot = WalletSnapshotBuilder.fromRpcData(
                    address = sanitizedAddress,
                    balance = balance,
                    history = history,
                    accountInfo = accountInfo
                )

                val score = riskEngine.calculateRisk(snapshot)

                _uiState.value = ResultState.Success(score)

            } catch (e: Exception) {
                val (message, errorType) = classifyError(e)
                _uiState.value = ResultState.Error(
                    message = message,
                    errorType = errorType,
                    throwable = e
                )
            }
        }
    }

    /**
     * Classify exceptions into user-friendly error types and messages.
     */
    private fun classifyError(e: Exception): Pair<String, ErrorType> {
        val rawMessage = (e.message ?: "").lowercase()

        return when {
            // Invalid address patterns
            rawMessage.contains("invalid param") ||
            rawMessage.contains("wrong size") ||
            rawMessage.contains("invalid base58") ||
            rawMessage.contains("invalid pubkey") ||
            rawMessage.contains("could not parse") ||
            rawMessage.contains("invalid") && rawMessage.contains("address") ->
                "The wallet address you entered is invalid. Please check and try again." to ErrorType.INVALID_ADDRESS

            // Network connectivity issues
            e is UnknownHostException ||
            e is SocketTimeoutException ||
            rawMessage.contains("unable to resolve host") ||
            rawMessage.contains("failed to connect") ||
            rawMessage.contains("network") && rawMessage.contains("unreachable") ||
            rawMessage.contains("no address associated") ||
            rawMessage.contains("connection refused") ->
                "Unable to connect. Please check your internet connection and try again." to ErrorType.NETWORK_ERROR

            // Timeout
            rawMessage.contains("timeout") ||
            rawMessage.contains("timed out") ->
                "The request timed out. The Solana network might be busy — please try again." to ErrorType.NETWORK_ERROR

            // Rate limiting
            rawMessage.contains("429") ||
            rawMessage.contains("too many requests") ||
            rawMessage.contains("rate limit") ->
                "Too many requests. Please wait a moment and try again." to ErrorType.RATE_LIMITED

            // Server errors (5xx, RPC issues)
            rawMessage.contains("500") ||
            rawMessage.contains("502") ||
            rawMessage.contains("503") ||
            rawMessage.contains("internal server error") ||
            rawMessage.contains("service unavailable") ->
                "The Solana network is experiencing issues. Please try again later." to ErrorType.SERVER_ERROR

            // RPC-specific errors
            e is SolanaRpcException ->
                "Analysis failed: ${e.message}" to ErrorType.SERVER_ERROR

            // Fallback
            else ->
                "Something went wrong. Please try again." to ErrorType.UNKNOWN
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(ResultViewModel::class.java)) {
                    val rpc = HttpSolanaRpcClient()
                    val engine = DefaultRiskEngine()
                    return ResultViewModel(rpc, engine) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}

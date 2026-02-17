package com.example.solscope.presentation.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.solscope.data.rpc.HttpSolanaRpcClient
import com.example.solscope.data.rpc.SolanaRpcClient
import com.example.solscope.data.watchlist.WatchlistRepository
import com.example.solscope.domain.model.SolanaNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class WatchlistViewModel(
    private val repository: WatchlistRepository,
    private val rpcClient: SolanaRpcClient
) : ViewModel() {

    private val _watchlist = MutableStateFlow<List<WatchlistEntry>>(emptyList())
    val watchlist: StateFlow<List<WatchlistEntry>> = _watchlist.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        viewModelScope.launch {
            repository.watchlist.collectLatest { addresses ->
                // When the list changes, update our UI list and fetch balances
                val currentEntries = _watchlist.value.associateBy { it.address }
                
                val newEntries = addresses.map { address ->
                    // Preserve existing data if present
                    currentEntries[address] ?: WatchlistEntry(address = address, balanceLoading = true)
                }
                
                _watchlist.value = newEntries
                fetchData(newEntries.map { it.address })
            }
        }
    }

    fun addAddress(address: String) {
        val sanitized = address.trim()
        val base58Regex = Regex("^[1-9A-HJ-NP-Za-km-z]+$")
        if (sanitized.length !in 32..44 || !sanitized.matches(base58Regex)) return
        
        viewModelScope.launch {
            repository.addAddress(sanitized)
        }
    }

    fun removeAddress(address: String) {
        viewModelScope.launch {
            repository.removeAddress(address)
        }
    }

    fun refresh() {
        val addresses = _watchlist.value.map { it.address }
        fetchData(addresses)
    }

    private fun fetchData(addresses: List<String>) {
        if (addresses.isEmpty()) return
        
        viewModelScope.launch {
            _isLoading.value = true
            
            // Mark all as loading
            _watchlist.value = _watchlist.value.map { 
                 it.copy(balanceLoading = true) 
            }

            // Parallel fetch balance + last txn time
            val results = withContext(Dispatchers.IO) {
                addresses.map { address ->
                    async {
                        val balance = try {
                            val lamports = rpcClient.getBalance(address, SolanaNetwork.MAINNET)
                            lamports.toDouble() / 1_000_000_000.0
                        } catch (_: Exception) {
                            null
                        }

                        // Fetch last 1 signature to get the most recent txn time (no extra cost)
                        val lastTxnTime = try {
                            val sigs = rpcClient.getSignaturesForAddress(address, SolanaNetwork.MAINNET, 1)
                            sigs.firstOrNull()?.blockTime
                        } catch (_: Exception) {
                            null
                        }

                        Triple(address, balance, lastTxnTime)
                    }
                }.awaitAll()
            }

            // Update UI
            val resultMap = results.associateBy { it.first }
            _watchlist.value = _watchlist.value.map { entry ->
                val result = resultMap[entry.address]
                if (result != null) {
                    entry.copy(
                        balance = result.second, 
                        balanceLoading = false,
                        error = result.second == null,
                        lastTxnTime = result.third
                    )
                } else {
                    entry
                }
            }
            
            _isLoading.value = false
        }
    }

    class Factory(
        private val repository: WatchlistRepository,
        private val rpcClient: SolanaRpcClient = HttpSolanaRpcClient()
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WatchlistViewModel::class.java)) {
                return WatchlistViewModel(repository, rpcClient) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

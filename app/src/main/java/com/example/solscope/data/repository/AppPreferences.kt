package com.example.solscope.data.repository

import com.example.solscope.domain.model.SolanaNetwork
import kotlinx.coroutines.flow.Flow

interface AppPreferences {
    val selectedNetwork: Flow<SolanaNetwork>
    val isFeatureTourCompleted: Flow<Boolean>
    val isVibrationEnabled: Flow<Boolean>

    suspend fun setNetwork(network: SolanaNetwork)
    suspend fun setFeatureTourCompleted(completed: Boolean)
    suspend fun setVibrationEnabled(enabled: Boolean)
}

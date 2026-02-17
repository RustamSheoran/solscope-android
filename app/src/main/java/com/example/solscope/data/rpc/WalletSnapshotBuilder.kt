package com.example.solscope.data.rpc

import com.example.solscope.data.rpc.model.AccountInfoValue
import com.example.solscope.domain.model.WalletSnapshot

/**
 * Helper responsible for constructing [WalletSnapshot] instances from low-level RPC data.
 */
object WalletSnapshotBuilder {

    fun fromRpcData(
        address: String,
        balance: Long,
        history: List<String>,
        accountInfo: AccountInfoValue?
    ): WalletSnapshot {
        return WalletSnapshot(
            address = address,
            balance = balance,
            transactionCount = history.size,
            isExecutable = accountInfo?.executable ?: false
        )
    }
}

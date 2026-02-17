package com.example.solscope.data.rpc

import com.example.solscope.data.rpc.model.AccountInfoValue
import com.example.solscope.data.rpc.model.SignatureInfo
import com.example.solscope.data.rpc.model.TokenAccountInfo
import com.example.solscope.domain.model.WalletSnapshot

/**
 * Constructs [WalletSnapshot] instances from low-level RPC data.
 */
object WalletSnapshotBuilder {

    fun fromRpcData(
        address: String,
        balance: Long,
        history: List<SignatureInfo>,
        accountInfo: AccountInfoValue?,
        tokenAccounts: List<TokenAccountInfo> = emptyList()
    ): WalletSnapshot {
        return WalletSnapshot(
            address = address,
            balance = balance,
            transactionCount = history.size,
            isExecutable = accountInfo?.executable ?: false,
            tokenAccounts = tokenAccounts,
            recentSignatures = history
        )
    }
}

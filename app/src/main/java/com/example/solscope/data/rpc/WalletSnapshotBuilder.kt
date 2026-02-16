package com.example.solscope.data.rpc

import com.example.solscope.domain.model.WalletSnapshot

/**
 * Helper responsible for constructing [WalletSnapshot] instances
 * from low-level RPC data.
 *
 * This class intentionally contains no risk or business logic.
 */
object WalletSnapshotBuilder {

    /**
     * Build a basic [WalletSnapshot] from an address and its
     * current balance in lamports.
     */
    fun fromBalance(
        address: String,
        balanceLamports: Long
    ): WalletSnapshot {
        return WalletSnapshot(
            address = address,
            balance = balanceLamports
        )
    }
}


package com.example.solscope.data.rpc

import com.example.solscope.domain.model.SolanaNetwork

/**
 * Thin contract for Solana JSON-RPC operations.
 *
 * This interface is intentionally minimal and contains
 * no business logic – it is a low-level transport
 * abstraction over Solana's JSON-RPC API.
 */
interface SolanaRpcClient {

    /**
     * Returns the balance for the given address in lamports.
     *
     * @param address Base58-encoded Solana public key.
     * @param network Target Solana network to query.
     */
    suspend fun getBalance(
        address: String,
        network: SolanaNetwork
    ): Long

    /**
     * Returns a list of confirmed transaction signatures
     * involving the given address, ordered from newest
     * to oldest.
     *
     * @param address Base58-encoded Solana public key.
     * @param network Target Solana network to query.
     * @param limit Maximum number of signatures to return.
     */
    suspend fun getSignaturesForAddress(
        address: String,
        network: SolanaNetwork,
        limit: Int
    ): List<com.example.solscope.data.rpc.model.SignatureInfo>

    suspend fun getAccountInfo(
        address: String,
        network: SolanaNetwork
    ): com.example.solscope.data.rpc.model.AccountInfoValue?

    /**
     * Returns all SPL token accounts owned by the given address.
     */
    suspend fun getTokenAccountsByOwner(
        address: String,
        network: SolanaNetwork
    ): List<com.example.solscope.data.rpc.model.TokenAccountInfo>
}

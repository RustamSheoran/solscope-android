package com.example.solscope.data.rpc.model

import kotlinx.serialization.Serializable

@Serializable
data class GetAccountInfoResult(
    val context: RpcContext,
    val value: AccountInfoValue?
)

@Serializable
data class AccountInfoValue(
    val data: List<String>,
    val executable: Boolean,
    val lamports: Long,
    val owner: String,
    val rentEpoch: Long
)

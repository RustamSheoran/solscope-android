package com.example.solscope.domain.risk

import com.example.solscope.domain.model.WalletSnapshot

data class RiskScore(
    val score: Int,
    val level: RiskLevel,
    val reasons: List<String>,
    val positives: List<String> = emptyList(),
    val snapshot: WalletSnapshot? = null
)

enum class RiskLevel {
    SAFE,
    WARNING,
    CRITICAL,
    UNKNOWN
}

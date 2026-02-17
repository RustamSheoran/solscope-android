package com.example.solscope.domain.risk

data class RiskScore(
    val score: Int,
    val level: RiskLevel,
    val reasons: List<String>
)

enum class RiskLevel {
    SAFE,
    WARNING,
    CRITICAL,
    UNKNOWN
}

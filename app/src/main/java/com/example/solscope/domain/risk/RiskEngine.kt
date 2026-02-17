package com.example.solscope.domain.risk

/**
 * Interface for the Risk Analysis Engine.
 * Implementation will be handled in Phase 3.
 */
interface RiskEngine {
    /**
     * Analyzes the wallet snapshot and returns a conservative risk score.
     */
    fun calculateRisk(snapshot: com.example.solscope.domain.model.WalletSnapshot): RiskScore
}

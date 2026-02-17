package com.example.solscope.domain.risk

import com.example.solscope.domain.model.WalletSnapshot

class DefaultRiskEngine : RiskEngine {

    companion object {
        private const val BASE_SCORE = 50
        private const val MIN_SCORE = 5
        private const val MAX_SCORE = 95

        private const val LAMPORTS_PER_SOL = 1_000_000_000L
        private const val DUST_THRESHOLD = 10_000_000L // 0.01 SOL
        private const val SOLVENT_THRESHOLD = 500_000_000L // 0.5 SOL
        private const val HIGH_VALUE_THRESHOLD = 5_000_000_000L // 5.0 SOL
        
    }

    override fun calculateRisk(snapshot: WalletSnapshot): RiskScore {
        if (snapshot.isExecutable) {
            return RiskScore(50, RiskLevel.WARNING, listOf("Program address detected"))
        }

        val risks = mutableListOf<String>()
        val positives = mutableListOf<String>()
        var scoreDelta = 0

        with(snapshot) {
            // Rule Group 1: Balance Checks
            if (balance == 0L) {
                scoreDelta -= 30
                risks.add("Wallet is completely empty")
            } else if (balance < DUST_THRESHOLD) {
                scoreDelta -= 10
                risks.add("Very low balance (Dust)")
            }

            // Cumulative Balance Bonuses
            if (balance >= SOLVENT_THRESHOLD) {
                scoreDelta += 10
                positives.add("Sufficient SOL balance")
            }
            if (balance >= HIGH_VALUE_THRESHOLD) {
                scoreDelta += 10
                positives.add("High value wallet")
            }

            // Rule Group 2: Activity Checks
            when {
                transactionCount == 0 -> {
                    scoreDelta -= 40
                    risks.add("No transaction history")
                }
                transactionCount < 10 -> {
                    scoreDelta -= 20
                    risks.add("New or low-activity wallet")
                }
                transactionCount >= 50 -> {
                    scoreDelta += 20
                    positives.add("Established transaction history")
                }
            }
        }

        val finalScore = (BASE_SCORE + scoreDelta).coerceIn(MIN_SCORE, MAX_SCORE)

        return RiskScore(
            score = finalScore,
            level = when (finalScore) {
                in 0..39 -> RiskLevel.CRITICAL
                in 40..69 -> RiskLevel.WARNING
                else -> RiskLevel.SAFE
            },
            reasons = risks,
            positives = positives,
            snapshot = snapshot
        )
    }
}

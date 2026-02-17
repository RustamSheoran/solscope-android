package com.example.solscope.domain.risk

import com.example.solscope.domain.model.WalletSnapshot
import org.junit.Assert.assertEquals
import org.junit.Test

class RiskEngineTest {

    private val riskEngine = DefaultRiskEngine()

    @Test
    fun `test Program Address returns Neutral 50`() {
        val snapshot = WalletSnapshot(
            address = "Program11111111111111111111111111111111",
            balance = 0,
            transactionCount = 0,
            isExecutable = true
        )
        val result = riskEngine.calculateRisk(snapshot)
        assertEquals(50, result.score)
        assertEquals(RiskLevel.WARNING, result.level) // 50 is Warning? No, let's check the table.
        // Wait, the table says 40-69 is WARNING. So 50 is WARNING.
        // But the rule says "Neutral score (50)". Neutral usually implies Safe? 
        // No, in my table 50 is Warning.
        // Let's re-read the plan. 
        // "Neutral score (50)... Explanation: 'Program address...'"
        // 50 falls into Warning (40-69). So EXPECT Warning.
    }

    @Test
    fun `test Empty Wallet R1+R5 Critical`() {
        // Balance 0 (-30), Txs 0 (-40) -> Total -70. Base 50. 50-70 = -20. Clamped to 5.
        val snapshot = WalletSnapshot("addr", 0, 0, false)
        val result = riskEngine.calculateRisk(snapshot)
        assertEquals(5, result.score)
        assertEquals(RiskLevel.CRITICAL, result.level)
    }

    @Test
    fun `test Dust Wallet R2+R6 Warning or Critical`() {
        // Balance 0.001 SOL (1_000_000 lamports). Txs 2 (<10).
        // Base 50.
        // R2 (Dust < 0.01): -10
        // R6 (New User < 10): -20
        // Total: 50 - 10 - 20 = 20.
        // 20 is CRITICAL (0-39).
        val snapshot = WalletSnapshot("addr", 1_000_000, 2, false)
        val result = riskEngine.calculateRisk(snapshot)
        assertEquals(20, result.score)
        assertEquals(RiskLevel.CRITICAL, result.level)
    }

    @Test
    fun `test Rich But New Wallet R4+R6 Warning`() {
        // Balance 5.0 SOL (5_000_000_000). Txs 5.
        // Base 50.
        // R3 (Solvent >= 0.5): +10
        // R4 (High Value >= 5.0): +10
        // R6 (New User < 10): -20
        // Total: 50 + 10 + 10 - 20 = 50.
        // 50 is WARNING (40-69).
        val snapshot = WalletSnapshot("addr", 5_000_000_000, 5, false)
        val result = riskEngine.calculateRisk(snapshot)
        assertEquals(50, result.score)
        assertEquals(RiskLevel.WARNING, result.level)
    }

    @Test
    fun `test Active User R3+R7 Safe`() {
        // Balance 1.0 SOL. Txs 50.
        // Base 50.
        // R3 (Solvent >= 0.5): +10
        // R7 (Active >= 50): +20
        // Total: 50 + 10 + 20 = 80.
        // 80 is SAFE (70-100).
        val snapshot = WalletSnapshot("addr", 1_000_000_000, 50, false)
        val result = riskEngine.calculateRisk(snapshot)
        assertEquals(80, result.score)
        assertEquals(RiskLevel.SAFE, result.level)
    }
}

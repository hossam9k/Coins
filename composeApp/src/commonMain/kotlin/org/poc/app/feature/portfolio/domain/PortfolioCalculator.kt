package org.poc.app.feature.portfolio.domain

import org.poc.app.core.domain.model.PreciseDecimal

object PortfolioCalculator {
    private val PERCENTAGE_MULTIPLIER = PreciseDecimal.fromString("100")
    private val EPSILON = PreciseDecimal.fromString("0.0001") // For precision comparisons

    fun calculatePerformance(
        currentPrice: PreciseDecimal,
        avgPurchasePrice: PreciseDecimal,
    ): PreciseDecimal {
        if (avgPurchasePrice <= EPSILON) return PreciseDecimal.ZERO
        return ((currentPrice - avgPurchasePrice) / avgPurchasePrice) * PERCENTAGE_MULTIPLIER
    }

    fun calculateTotalValue(
        amount: PreciseDecimal,
        price: PreciseDecimal,
    ): PreciseDecimal = amount * price

    fun isValidAmount(amount: PreciseDecimal): Boolean = amount.isPositive()

    fun isValidPrice(price: PreciseDecimal): Boolean = !price.isNegative()

    fun isSignificantChange(
        oldValue: PreciseDecimal,
        newValue: PreciseDecimal,
    ): Boolean {
        val absOld = if (oldValue.isNegative()) PreciseDecimal.ZERO - oldValue else oldValue
        val absNew = if (newValue.isNegative()) PreciseDecimal.ZERO - newValue else newValue

        if (absOld <= EPSILON) return absNew > EPSILON
        val change = if (oldValue.isZero()) newValue else (newValue - oldValue) / oldValue
        val absChange = if (change.isNegative()) PreciseDecimal.ZERO - change else change
        return absChange > EPSILON
    }
}

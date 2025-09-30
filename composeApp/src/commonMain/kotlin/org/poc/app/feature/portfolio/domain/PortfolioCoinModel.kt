package org.poc.app.feature.portfolio.domain

import org.poc.app.feature.coins.domain.model.Coin
import org.poc.app.shared.common.domain.PreciseDecimal

data class PortfolioCoinModel(
    val coin: Coin,
    val performancePercent: PreciseDecimal,
    val averagePurchasePrice: PreciseDecimal,
    val ownedAmountInUnit: PreciseDecimal,
    val ownedAmountInFiat: PreciseDecimal,
)
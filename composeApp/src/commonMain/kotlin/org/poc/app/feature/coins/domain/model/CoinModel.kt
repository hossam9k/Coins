package org.poc.app.feature.coins.domain.model

import org.poc.app.shared.common.domain.PreciseDecimal

data class CoinModel(
    val coin: Coin,
    val price: PreciseDecimal,
    val change: PreciseDecimal,
)
package org.poc.app.feature.coins.domain.model

import org.poc.app.shared.common.domain.PreciseDecimal

data class PriceModel(
    val price: PreciseDecimal,
    val timestamp: Long
)
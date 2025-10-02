package org.poc.app.feature.coins.domain.model

import org.poc.app.core.domain.model.PreciseDecimal

data class PriceModel(
    val price: PreciseDecimal,
    val timestamp: Long,
)

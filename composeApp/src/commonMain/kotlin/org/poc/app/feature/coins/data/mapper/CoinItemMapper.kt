package org.poc.app.feature.coins.data.mapper

import org.poc.app.feature.coins.data.dto.CoinItemDto
import org.poc.app.feature.coins.data.dto.CoinPriceDto
import org.poc.app.feature.coins.domain.model.CoinModel
import org.poc.app.feature.coins.domain.model.Coin
import org.poc.app.feature.coins.domain.model.PriceModel
import org.poc.app.shared.business.domain.PreciseDecimal

/**
 * Coins Data Mapper
 * Maps data DTOs to domain models
 * Follows clean architecture principles - no business logic, only transformation
 */
object CoinsDataMapper {

    /**
     * Maps network DTO to domain CoinModel
     */
    fun CoinItemDto.toCoinModel(): CoinModel = CoinModel(
        coin = Coin(
            id = uuid,
            name = name,
            symbol = symbol,
            iconUrl = iconUrl,
        ),
        price = PreciseDecimal.fromString(price),
        change = PreciseDecimal.fromString(change),
    )

    /**
     * Maps network price DTO to domain PriceModel
     */
    fun CoinPriceDto.toPriceModel(): PriceModel = PriceModel(
        price = PreciseDecimal.fromString(price),
        timestamp = timestamp,
    )
}
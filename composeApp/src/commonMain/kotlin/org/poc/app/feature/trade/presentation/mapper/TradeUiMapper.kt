package org.poc.app.feature.trade.presentation.mapper

import org.poc.app.feature.coins.domain.model.Coin
import org.poc.app.feature.coins.domain.model.CoinModel
import org.poc.app.feature.trade.presentation.common.UiTradeCoinItem

/**
 * Trade UI Mapper
 * Maps domain models to presentation models and vice versa
 * Follows clean architecture principles - no business logic, only transformation
 */
object TradeUiMapper {
    /**
     * Maps domain CoinModel to UI trade representation
     */
    fun CoinModel.toUiTradeCoinItem(): UiTradeCoinItem =
        UiTradeCoinItem(
            id = coin.id,
            name = coin.name,
            symbol = coin.symbol,
            iconUrl = coin.iconUrl,
            price = price.toDouble(),
        )

    /**
     * Maps UI trade representation back to domain Coin
     */
    fun UiTradeCoinItem.toCoin(): Coin =
        Coin(
            id = id,
            name = name,
            symbol = symbol,
            iconUrl = iconUrl,
        )
}

package org.poc.app.feature.coins.presentation.mapper

import org.poc.app.core.domain.util.formatChangeDisplay
import org.poc.app.core.domain.util.formatPriceDisplay
import org.poc.app.feature.coins.domain.model.CoinModel
import org.poc.app.feature.coins.presentation.UiCoinListItem

/**
 * Coins UI Mapper
 * Maps domain models to presentation models
 * Follows clean architecture principles - no business logic, only transformation
 */
object CoinsUiMapper {
    /**
     * Maps domain CoinModel to UI list item representation
     */
    fun CoinModel.toUiCoinListItem(): UiCoinListItem =
        UiCoinListItem(
            id = coin.id,
            name = coin.name,
            iconUrl = coin.iconUrl,
            symbol = coin.symbol,
            formattedPrice = formatPriceDisplay(price),
            formattedChange = formatChangeDisplay(change),
            isPositive = !change.isNegative(),
        )
}

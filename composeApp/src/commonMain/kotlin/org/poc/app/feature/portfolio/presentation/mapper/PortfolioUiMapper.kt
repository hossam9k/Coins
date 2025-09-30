package org.poc.app.feature.portfolio.presentation.mapper

import org.poc.app.feature.portfolio.domain.PortfolioCoinModel
import org.poc.app.feature.portfolio.presentation.UiPortfolioCoinItem
import org.poc.app.shared.common.util.formatCryptoPrecise
import org.poc.app.shared.common.util.formatPriceDisplay
import org.poc.app.shared.common.util.formatChangeDisplay

/**
 * Portfolio UI Mapper
 * Maps domain models to presentation models
 * Follows clean architecture principles - no business logic, only transformation
 */
object PortfolioUiMapper {

    /**
     * Maps domain PortfolioCoinModel to UI representation
     */
    fun PortfolioCoinModel.toUiPortfolioCoinItem(): UiPortfolioCoinItem {
        return UiPortfolioCoinItem(
            id = coin.id,
            name = coin.name,
            iconUrl = coin.iconUrl,
            amountInUnitText = formatCryptoPrecise(ownedAmountInUnit, coin.symbol),
            amountInFiatText = formatPriceDisplay(ownedAmountInFiat),
            performancePercentText = formatChangeDisplay(performancePercent),
            isPositive = !performancePercent.isNegative()
        )
    }
}
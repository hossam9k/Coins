package org.poc.app.feature.portfolio.data.mapper

import org.poc.app.feature.coins.domain.model.Coin
import org.poc.app.core.domain.model.PreciseDecimal
import org.poc.app.feature.portfolio.data.local.PortfolioCoinEntity
import org.poc.app.feature.portfolio.domain.PortfolioCalculator
import org.poc.app.feature.portfolio.domain.PortfolioCoinModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Portfolio Data Mapper
 * Maps database entities to domain models and vice versa
 * Follows clean architecture principles - minimal business logic, mostly transformation
 */
object PortfolioDataMapper {

    /**
     * Maps database entity to domain model with current price calculation
     * Note: Calculations are done using domain services (PortfolioCalculator)
     */
    fun PortfolioCoinEntity.toPortfolioCoinModel(
        currentPrice: PreciseDecimal,
    ): PortfolioCoinModel {
        val avgPrice = PreciseDecimal.fromDouble(averagePurchasePrice)
        val amount = PreciseDecimal.fromDouble(amountOwned)

        return PortfolioCoinModel(
            coin = Coin(
                id = coinId,
                name = name,
                symbol = symbol,
                iconUrl = iconUrl
            ),
            performancePercent = PortfolioCalculator.calculatePerformance(currentPrice, avgPrice),
            averagePurchasePrice = avgPrice,
            ownedAmountInUnit = amount,
            ownedAmountInFiat = PortfolioCalculator.calculateTotalValue(amount, currentPrice),
        )
    }

    /**
     * Maps domain model to database entity
     */
    @OptIn(ExperimentalTime::class)
    fun PortfolioCoinModel.toPortfolioCoinEntity(): PortfolioCoinEntity {
        return PortfolioCoinEntity(
            coinId = coin.id,
            name = coin.name,
            symbol = coin.symbol,
            iconUrl = coin.iconUrl,
            amountOwned = ownedAmountInUnit.toDouble(),
            averagePurchasePrice = averagePurchasePrice.toDouble(),
            timestamp = Clock.System.now().toEpochMilliseconds()
        )
    }
}
package org.poc.app.feature.trade.domain

import kotlinx.coroutines.flow.first
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.EmptyResult
import org.poc.app.core.domain.model.PreciseDecimal
import org.poc.app.core.domain.model.Result
import org.poc.app.feature.coins.domain.model.Coin
import org.poc.app.feature.portfolio.domain.PortfolioCoinModel
import org.poc.app.feature.portfolio.domain.PortfolioRepository

class BuyCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) {
    suspend fun buyCoin(
        coin: Coin,
        amountInFiat: PreciseDecimal,
        price: PreciseDecimal,
    ): EmptyResult<DataError> {
        val balance = portfolioRepository.cashBalanceFlow().first()
        val balanceDecimal = PreciseDecimal.fromDouble(balance)

        if (balanceDecimal < amountInFiat) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        val existingCoinResult = portfolioRepository.getPortfolioCoinFlow(coin.id).first()
        when (existingCoinResult) {
            is Result.Success -> {
                val existingCoin = existingCoinResult.data
                val amountInUnit = amountInFiat / price

                if (existingCoin != null) {
                    // Update existing coin
                    val newAmountOwned = existingCoin.ownedAmountInUnit + amountInUnit
                    val newTotalInvestment = existingCoin.ownedAmountInFiat + amountInFiat
                    val newAveragePurchasePrice = newTotalInvestment / newAmountOwned

                    portfolioRepository.savePortfolioCoin(
                        existingCoin.copy(
                            ownedAmountInUnit = newAmountOwned,
                            ownedAmountInFiat = newTotalInvestment,
                            averagePurchasePrice = newAveragePurchasePrice,
                        ),
                    )
                } else {
                    // Create new coin entry
                    portfolioRepository.savePortfolioCoin(
                        PortfolioCoinModel(
                            coin = coin,
                            performancePercent = PreciseDecimal.ZERO,
                            averagePurchasePrice = price,
                            ownedAmountInFiat = amountInFiat,
                            ownedAmountInUnit = amountInUnit,
                        ),
                    )
                }
            }
            is Result.Error -> return Result.Error(existingCoinResult.error)
        }

        portfolioRepository.updateCashBalance(balance - amountInFiat.toDouble())
        return Result.Success(Unit)
    }
}

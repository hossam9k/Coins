package org.poc.app.feature.trade.domain

import kotlinx.coroutines.flow.first
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.EmptyResult
import org.poc.app.core.domain.model.PreciseDecimal
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.usecase.UseCase
import org.poc.app.feature.coins.domain.model.Coin
import org.poc.app.feature.portfolio.domain.PortfolioRepository

/**
 * Parameters for selling a coin.
 *
 * @param coin The coin to sell
 * @param amountInFiat The sell amount in fiat currency
 * @param price The current price per unit
 */
data class SellCoinParams(
    val coin: Coin,
    val amountInFiat: PreciseDecimal,
    val price: PreciseDecimal,
)

/**
 * Use case for executing a coin sale.
 * Handles ownership validation, portfolio updates, and cash balance addition.
 *
 * @param portfolioRepository The portfolio repository for data access
 */
class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) : UseCase<SellCoinParams, EmptyResult<DataError>> {
    companion object {
        private val SELL_ALL_THRESHOLD = PreciseDecimal.ONE
    }

    /**
     * Executes a coin sale.
     *
     * @param params The sale parameters (coin, amount, price)
     * @return EmptyResult indicating success or the specific error
     */
    override suspend operator fun invoke(params: SellCoinParams): EmptyResult<DataError> {
        val (coin, amountInFiat, price) = params
        val existingCoinResponse = portfolioRepository.getPortfolioCoinFlow(coin.id).first()

        when (existingCoinResponse) {
            is Result.Success -> {
                val existingCoin = existingCoinResponse.data

                if (existingCoin == null) {
                    return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
                }

                val sellAmountInUnit = amountInFiat / price
                val balance = portfolioRepository.cashBalanceFlow().first()

                if (existingCoin.ownedAmountInUnit < sellAmountInUnit) {
                    return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
                }

                val remainingAmountFiat = existingCoin.ownedAmountInFiat - amountInFiat
                val remainingAmountUnit = existingCoin.ownedAmountInUnit - sellAmountInUnit

                if (remainingAmountFiat < SELL_ALL_THRESHOLD) {
                    portfolioRepository.removeCoinFromPortfolio(coin.id)
                } else {
                    portfolioRepository.savePortfolioCoin(
                        existingCoin.copy(
                            ownedAmountInUnit = remainingAmountUnit,
                            ownedAmountInFiat = remainingAmountFiat,
                        ),
                    )
                }

                portfolioRepository.updateCashBalance(balance + amountInFiat.toDouble())
                return Result.Success(Unit)
            }
            is Result.Error -> {
                return Result.Error(existingCoinResponse.error)
            }
        }
    }
}

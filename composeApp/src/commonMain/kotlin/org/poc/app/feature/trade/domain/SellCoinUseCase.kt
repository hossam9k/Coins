package org.poc.app.feature.trade.domain
import kotlinx.coroutines.flow.first
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.EmptyResult
import org.poc.app.core.domain.model.PreciseDecimal
import org.poc.app.core.domain.model.Result
import org.poc.app.feature.coins.domain.model.Coin
import org.poc.app.feature.portfolio.domain.PortfolioRepository

class SellCoinUseCase(
    private val portfolioRepository: PortfolioRepository,
) {
    suspend fun sellCoin(
        coin: Coin,
        amountInFiat: PreciseDecimal,
        price: PreciseDecimal,
    ): EmptyResult<DataError> {
        val sellAllThreshold = PreciseDecimal.ONE
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

                if (remainingAmountFiat < sellAllThreshold) {
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

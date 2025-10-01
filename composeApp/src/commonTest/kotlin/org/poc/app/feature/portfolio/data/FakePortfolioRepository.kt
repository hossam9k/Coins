package org.poc.app.feature.portfolio.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import org.poc.app.feature.coins.domain.model.Coin
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.EmptyResult
import org.poc.app.core.domain.model.PreciseDecimal
import org.poc.app.feature.portfolio.domain.PortfolioCoinModel
import org.poc.app.feature.portfolio.domain.PortfolioRepository
import org.poc.app.core.domain.model.Result

class FakePortfolioRepository : PortfolioRepository {

    private val _data = MutableStateFlow<Result<List<PortfolioCoinModel>, DataError.Remote>>(
        Result.Success(emptyList())
    )

    private val _cashBalance = MutableStateFlow(INITIAL_CASH_BALANCE)
    private val _portfolioValue = MutableStateFlow(INITIAL_PORTFOLIO_VALUE)

    private val listOfCoins = mutableListOf<PortfolioCoinModel>()

    override suspend fun initializeBalance() {
        // no-op
    }

    override fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
        return _data.asStateFlow()
    }

    override fun getPortfolioCoinFlow(coinId: String): Flow<Result<PortfolioCoinModel?, DataError.Remote>> {
        return MutableStateFlow(Result.Success(portfolioCoin)).asStateFlow()
    }

    override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
        listOfCoins.add(portfolioCoin)
        _portfolioValue.value = listOfCoins.sumOf { it.ownedAmountInFiat.toDouble() }
        _data.value = Result.Success(listOfCoins)
        return Result.Success(Unit)
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        _data.update { Result.Success(emptyList()) }
    }

    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>> {
        return _portfolioValue.map { Result.Success(it) }
    }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>> {
        return _cashBalance.combine(_portfolioValue) { cashBalance, portfolioValue ->
            cashBalance + portfolioValue
        }.map { Result.Success(it) }
    }

    override fun cashBalanceFlow(): Flow<Double> {
        return _cashBalance.asStateFlow()
    }

    override suspend fun updateCashBalance(newBalance: Double) {
        _cashBalance.value = newBalance
    }

    fun simulateError() {
        _data.value = Result.Error(DataError.Remote.SERVER)
    }

    companion object {
        const val INITIAL_CASH_BALANCE = 10000.0
        const val INITIAL_PORTFOLIO_VALUE = 0.0

        val fakeCoin = Coin(
            id = "fakeId",
            name = "Fake Coin",
            symbol = "FAKE",
            iconUrl = "https://fake.url/fake.png"
        )
        val portfolioCoin = PortfolioCoinModel(
            coin = fakeCoin,
            ownedAmountInUnit = PreciseDecimal.fromDouble(1000.0),
            ownedAmountInFiat = PreciseDecimal.fromDouble(3000.0),
            performancePercent = PreciseDecimal.fromDouble(10.0),
            averagePurchasePrice = PreciseDecimal.fromDouble(10.0),
        )
    }
}
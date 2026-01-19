package org.poc.app.feature.portfolio.data

import androidx.sqlite.SQLiteException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.EmptyResult
import org.poc.app.core.domain.model.PreciseDecimal
import org.poc.app.core.domain.model.Result
import org.poc.app.feature.coins.domain.CoinsRepository
import org.poc.app.feature.coins.domain.model.CoinModel
import org.poc.app.feature.portfolio.data.local.PortfolioDao
import org.poc.app.feature.portfolio.data.local.UserBalanceDao
import org.poc.app.feature.portfolio.data.local.UserBalanceEntity
import org.poc.app.feature.portfolio.data.mapper.PortfolioDataMapper.toPortfolioCoinEntity
import org.poc.app.feature.portfolio.data.mapper.PortfolioDataMapper.toPortfolioCoinModel
import org.poc.app.feature.portfolio.domain.PortfolioCalculator
import org.poc.app.feature.portfolio.domain.PortfolioCoinModel
import org.poc.app.feature.portfolio.domain.PortfolioRepository

class PortfolioRepositoryImpl(
    private val portfolioDao: PortfolioDao,
    private val userBalanceDao: UserBalanceDao,
    private val coinsRepository: CoinsRepository,
) : PortfolioRepository {
    companion object {
        private const val DEFAULT_CASH_BALANCE = 10000.0
        private val MIN_PORTFOLIO_AMOUNT = PreciseDecimal.fromString("0.0001")
        private val MAX_PORTFOLIO_AMOUNT = PreciseDecimal.fromString("1000000.0")
    }

    private suspend fun fetchCoinsWithResult(): Result<List<CoinModel>, DataError> = coinsRepository.getCoins()

    override suspend fun initializeBalance() {
        val currentBalance = userBalanceDao.getCashBalance()
        if (currentBalance == null) {
            userBalanceDao.insertBalance(
                UserBalanceEntity(
                    cashBalance = DEFAULT_CASH_BALANCE,
                ),
            )
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError>> =
        portfolioDao
            .getAllOwnedCoins()
            .flatMapLatest { portfolioCoinEntities ->
                if (portfolioCoinEntities.isEmpty()) {
                    flowOf(Result.Success(emptyList<PortfolioCoinModel>()))
                } else {
                    flow<Result<List<PortfolioCoinModel>, DataError>> {
                        when (val result = fetchCoinsWithResult()) {
                            is Result.Error -> emit(Result.Error(result.error))
                            is Result.Success -> {
                                val portfolioCoins =
                                    portfolioCoinEntities.mapNotNull { portfolioCoinEntity ->
                                        val coin = result.data.find { it.coin.id == portfolioCoinEntity.coinId }
                                        coin?.let {
                                            portfolioCoinEntity.toPortfolioCoinModel(it.price)
                                        }
                                    }
                                emit(Result.Success(portfolioCoins))
                            }
                        }
                    }
                }
            }.catch {
                emit(Result.Error(DataError.Remote.UNKNOWN))
            }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun getPortfolioCoinFlow(coinId: String): Flow<Result<PortfolioCoinModel?, DataError>> =
        portfolioDao
            .getCoinByIdFlow(coinId)
            .flatMapLatest { portfolioCoinEntity ->
                if (portfolioCoinEntity == null) {
                    flowOf(Result.Success(null))
                } else {
                    flow<Result<PortfolioCoinModel?, DataError>> {
                        when (val result = coinsRepository.getCoinDetails(coinId)) {
                            is Result.Error -> emit(Result.Error(result.error))
                            is Result.Success -> {
                                val portfolioCoin = portfolioCoinEntity.toPortfolioCoinModel(result.data.price)
                                emit(Result.Success(portfolioCoin))
                            }
                        }
                    }
                }
            }.catch {
                emit(Result.Error(DataError.Remote.UNKNOWN))
            }

    override suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local> {
        // Input validation
        if (!PortfolioCalculator.isValidAmount(portfolioCoin.ownedAmountInUnit)) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        if (portfolioCoin.ownedAmountInUnit < MIN_PORTFOLIO_AMOUNT) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        if (portfolioCoin.ownedAmountInUnit > MAX_PORTFOLIO_AMOUNT) {
            return Result.Error(DataError.Local.INSUFFICIENT_FUNDS)
        }

        if (!PortfolioCalculator.isValidPrice(portfolioCoin.averagePurchasePrice)) {
            return Result.Error(DataError.Local.UNKNOWN)
        }

        try {
            portfolioDao.insert(portfolioCoin.toPortfolioCoinEntity())
            return Result.Success(Unit)
        } catch (_: SQLiteException) {
            return Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun removeCoinFromPortfolio(coinId: String) {
        portfolioDao.deletePortfolioItem(coinId)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    override fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError>> =
        portfolioDao
            .getAllOwnedCoins()
            .flatMapLatest { portfolioCoinsEntities ->
                if (portfolioCoinsEntities.isEmpty()) {
                    flowOf(Result.Success(0.0))
                } else {
                    flow<Result<Double, DataError>> {
                        when (val result = fetchCoinsWithResult()) {
                            is Result.Error -> emit(Result.Error(result.error))
                            is Result.Success -> {
                                var totalValue = PreciseDecimal.ZERO
                                for (ownedCoin in portfolioCoinsEntities) {
                                    val coinPrice = result.data.find { it.coin.id == ownedCoin.coinId }?.price ?: PreciseDecimal.ZERO
                                    val amount = PreciseDecimal.fromDouble(ownedCoin.amountOwned)
                                    totalValue = totalValue + PortfolioCalculator.calculateTotalValue(amount, coinPrice)
                                }
                                emit(Result.Success(totalValue.toDouble()))
                            }
                        }
                    }
                }
            }.catch {
                emit(Result.Error(DataError.Remote.UNKNOWN))
            }

    override fun cashBalanceFlow(): Flow<Double> =
        userBalanceDao.getCashBalanceFlow().map { balance ->
            balance ?: DEFAULT_CASH_BALANCE
        }

    override fun totalBalanceFlow(): Flow<Result<Double, DataError>> =
        combine(
            cashBalanceFlow(),
            calculateTotalPortfolioValue(),
        ) { cashBalance, portfolioResult ->
            when (portfolioResult) {
                is Result.Success -> {
                    Result.Success(cashBalance + portfolioResult.data)
                }
                is Result.Error -> {
                    Result.Error(portfolioResult.error)
                }
            }
        }

    override suspend fun updateCashBalance(newBalance: Double) {
        val preciseBalance = PreciseDecimal.fromDouble(newBalance)
        if (!PortfolioCalculator.isValidPrice(preciseBalance) || preciseBalance.isNegative()) {
            return // Skip invalid balance updates
        }
        userBalanceDao.updateCashBalance(newBalance)
    }
}

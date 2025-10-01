package org.poc.app.feature.portfolio.domain

import kotlinx.coroutines.flow.Flow
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.EmptyResult
import org.poc.app.core.domain.model.Result

interface PortfolioRepository {

    suspend fun initializeBalance()
    fun allPortfolioCoinsFlow(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>>
    fun getPortfolioCoinFlow(coinId: String): Flow<Result<PortfolioCoinModel?, DataError.Remote>>
    suspend fun savePortfolioCoin(portfolioCoin: PortfolioCoinModel): EmptyResult<DataError.Local>
    suspend fun removeCoinFromPortfolio(coinId: String)

    fun calculateTotalPortfolioValue(): Flow<Result<Double, DataError.Remote>>
    fun totalBalanceFlow(): Flow<Result<Double, DataError.Remote>>
    fun cashBalanceFlow(): Flow<Double>
    suspend fun updateCashBalance(newBalance: Double)

}
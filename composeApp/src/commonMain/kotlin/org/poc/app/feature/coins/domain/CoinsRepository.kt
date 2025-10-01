package org.poc.app.feature.coins.domain

import org.poc.app.feature.coins.domain.model.CoinModel
import org.poc.app.feature.coins.domain.model.PriceModel
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result

interface CoinsRepository {
    suspend fun getCoins(): Result<List<CoinModel>, DataError.Remote>
    suspend fun getCoinPriceHistory(coinId: String): Result<List<PriceModel>, DataError.Remote>
    suspend fun getCoinDetails(coinId: String): Result<CoinModel, DataError.Remote>
}
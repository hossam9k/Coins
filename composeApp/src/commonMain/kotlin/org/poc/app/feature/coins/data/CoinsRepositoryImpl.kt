package org.poc.app.feature.coins.data

import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.model.map
import org.poc.app.feature.coins.data.CoinsRemoteDataSource
import org.poc.app.feature.coins.data.mapper.CoinsDataMapper.toCoinModel
import org.poc.app.feature.coins.data.mapper.CoinsDataMapper.toPriceModel
import org.poc.app.feature.coins.domain.CoinsRepository
import org.poc.app.feature.coins.domain.model.CoinModel
import org.poc.app.feature.coins.domain.model.PriceModel

class CoinsRepositoryImpl(
    private val remoteDataSource: CoinsRemoteDataSource,
) : CoinsRepository {
    override suspend fun getCoins(): Result<List<CoinModel>, DataError> =
        remoteDataSource.getListOfCoins().map { response ->
            response.data.coins.map { it.toCoinModel() }
        }

    override suspend fun getCoinPriceHistory(coinId: String): Result<List<PriceModel>, DataError> =
        remoteDataSource.getPriceHistory(coinId).map { response ->
            response.data.history.map { it.toPriceModel() }
        }

    override suspend fun getCoinDetails(coinId: String): Result<CoinModel, DataError> =
        remoteDataSource.getCoinById(coinId).map { response ->
            response.data.coin.toCoinModel()
        }
}

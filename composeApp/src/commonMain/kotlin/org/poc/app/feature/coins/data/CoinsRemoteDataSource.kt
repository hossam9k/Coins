package org.poc.app.feature.coins.data

import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.feature.coins.data.dto.CoinDetailsResponseDto
import org.poc.app.feature.coins.data.dto.CoinPriceHistoryResponseDto
import org.poc.app.feature.coins.data.dto.CoinsResponseDto

interface CoinsRemoteDataSource {
    suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote>

    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote>

    suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote>
}

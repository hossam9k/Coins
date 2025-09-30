package org.poc.app.feature.coins.data.remote.api

import org.poc.app.feature.coins.data.dto.CoinDetailsResponseDto
import org.poc.app.feature.coins.data.dto.CoinPriceHistoryResponseDto
import org.poc.app.feature.coins.data.dto.CoinsResponseDto
import org.poc.app.shared.common.domain.DataError
import org.poc.app.shared.common.domain.Result

interface CoinsRemoteDataSource {

    suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote>

    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote>

    suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote>
}
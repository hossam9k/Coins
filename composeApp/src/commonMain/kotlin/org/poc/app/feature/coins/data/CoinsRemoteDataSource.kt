package org.poc.app.feature.coins.data

import org.poc.app.core.data.datasource.RemoteDataSource
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.feature.coins.data.dto.CoinDetailsResponseDto
import org.poc.app.feature.coins.data.dto.CoinPriceHistoryResponseDto
import org.poc.app.feature.coins.data.dto.CoinsResponseDto

/**
 * Remote data source interface for coin-related API operations.
 * Implementations handle actual network requests via HTTP client.
 *
 * @see KtorCoinsRemoteDataSource for Ktor implementation
 */
interface CoinsRemoteDataSource : RemoteDataSource {
    suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError>

    suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError>

    suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError>
}

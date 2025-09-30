package org.poc.app.feature.coins.data.remote.impl

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.poc.app.feature.coins.data.dto.CoinDetailsResponseDto
import org.poc.app.feature.coins.data.dto.CoinPriceHistoryResponseDto
import org.poc.app.feature.coins.data.dto.CoinsResponseDto
import org.poc.app.feature.coins.data.remote.api.CoinsRemoteDataSource
import org.poc.app.shared.common.domain.DataError
import org.poc.app.shared.common.domain.Result
import org.poc.app.shared.common.network.safeCall
import org.poc.app.shared.common.network.NetworkConfig

class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient
) : CoinsRemoteDataSource {

    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> {
        return safeCall<CoinsResponseDto> {
            httpClient.get("${NetworkConfig.BASE_URL}coins")
        }
    }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> {
        return safeCall<CoinPriceHistoryResponseDto> {
            httpClient.get("${NetworkConfig.BASE_URL}coin/$coinId/history")
        }
    }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> {
        return safeCall<CoinDetailsResponseDto> {
            httpClient.get("${NetworkConfig.BASE_URL}coin/$coinId")
        }
    }
}
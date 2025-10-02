package org.poc.app.feature.coins.data

import io.ktor.client.HttpClient
import io.ktor.client.request.get
import org.poc.app.core.data.network.ApiConfig
import org.poc.app.core.data.network.safeCall
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.feature.coins.data.dto.CoinDetailsResponseDto
import org.poc.app.feature.coins.data.dto.CoinPriceHistoryResponseDto
import org.poc.app.feature.coins.data.dto.CoinsResponseDto

class KtorCoinsRemoteDataSource(
    private val httpClient: HttpClient,
) : CoinsRemoteDataSource {
    override suspend fun getListOfCoins(): Result<CoinsResponseDto, DataError.Remote> =
        safeCall<CoinsResponseDto> {
            // In Ktor, when defaultRequest has a base URL,
            // we need to provide the full URL or use urlString
            httpClient.get(urlString = "${ApiConfig.baseUrl}${ApiConfig.Endpoints.ALL_COINS}")
        }

    override suspend fun getPriceHistory(coinId: String): Result<CoinPriceHistoryResponseDto, DataError.Remote> =
        safeCall<CoinPriceHistoryResponseDto> {
            httpClient.get(urlString = "${ApiConfig.baseUrl}coin/$coinId/history")
        }

    override suspend fun getCoinById(coinId: String): Result<CoinDetailsResponseDto, DataError.Remote> =
        safeCall<CoinDetailsResponseDto> {
            // Coinranking API uses: /coin/{uuid}
            httpClient.get(urlString = "${ApiConfig.baseUrl}${ApiConfig.Endpoints.COIN_DETAILS}/$coinId")
        }
}

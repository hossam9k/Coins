package org.poc.app.feature.coins.domain

import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.feature.coins.domain.model.PriceModel

class GetCoinPriceHistoryUseCase(
    private val repository: CoinsRepository,
) {
    suspend fun execute(coinId: String): Result<List<PriceModel>, DataError.Remote> = repository.getCoinPriceHistory(coinId)
}

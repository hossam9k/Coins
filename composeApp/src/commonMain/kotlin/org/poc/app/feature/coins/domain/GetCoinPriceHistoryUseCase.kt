package org.poc.app.feature.coins.domain

import org.poc.app.feature.coins.domain.model.PriceModel
import org.poc.app.shared.common.domain.DataError
import org.poc.app.shared.common.domain.Result

class GetCoinPriceHistoryUseCase(
    private val repository: CoinsRepository,
) {

    suspend fun execute(coinId: String): Result<List<PriceModel>, DataError.Remote> {
        return repository.getCoinPriceHistory(coinId)
    }
}
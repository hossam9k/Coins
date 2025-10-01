package org.poc.app.feature.coins.domain

import org.poc.app.feature.coins.domain.model.CoinModel
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result

class GetCoinDetailsUseCase(
    private val repository: CoinsRepository,
) {

    suspend fun execute(coinId: String): Result<CoinModel, DataError.Remote> {
        return repository.getCoinDetails(coinId)
    }
}
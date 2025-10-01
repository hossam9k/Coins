package org.poc.app.feature.coins.domain

import org.poc.app.feature.coins.domain.model.CoinModel
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result


class GetCoinsListUseCase(
    private val repository: CoinsRepository,
) {
    suspend fun execute(): Result<List<CoinModel>, DataError.Remote> {
        return repository.getCoins()
    }
}
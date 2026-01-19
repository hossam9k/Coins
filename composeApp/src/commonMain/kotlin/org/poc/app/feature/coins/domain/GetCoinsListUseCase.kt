package org.poc.app.feature.coins.domain

import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.usecase.NoParamUseCase
import org.poc.app.feature.coins.domain.model.CoinModel

/**
 * Use case for fetching the list of all available coins.
 *
 * @param repository The coins repository for data access
 */
class GetCoinsListUseCase(
    private val repository: CoinsRepository,
) : NoParamUseCase<Result<List<CoinModel>, DataError>> {
    /**
     * Fetches all available coins.
     *
     * @return Result containing list of coin models or an error
     */
    override suspend operator fun invoke(): Result<List<CoinModel>, DataError> = repository.getCoins()
}

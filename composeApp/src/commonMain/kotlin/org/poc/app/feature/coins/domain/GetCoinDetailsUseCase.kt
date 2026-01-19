package org.poc.app.feature.coins.domain

import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.usecase.UseCase
import org.poc.app.feature.coins.domain.model.CoinModel

/**
 * Use case for fetching detailed information about a specific coin.
 *
 * @param repository The coins repository for data access
 */
class GetCoinDetailsUseCase(
    private val repository: CoinsRepository,
) : UseCase<String, Result<CoinModel, DataError>> {
    /**
     * Fetches coin details by ID.
     *
     * @param params The coin ID to fetch details for
     * @return Result containing the coin model or an error
     */
    override suspend operator fun invoke(params: String): Result<CoinModel, DataError> =
        repository.getCoinDetails(params)
}

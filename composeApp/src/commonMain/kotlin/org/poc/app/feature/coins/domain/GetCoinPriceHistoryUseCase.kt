package org.poc.app.feature.coins.domain

import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.usecase.UseCase
import org.poc.app.feature.coins.domain.model.PriceModel

/**
 * Use case for fetching price history of a specific coin.
 *
 * @param repository The coins repository for data access
 */
class GetCoinPriceHistoryUseCase(
    private val repository: CoinsRepository,
) : UseCase<String, Result<List<PriceModel>, DataError>> {
    /**
     * Fetches price history for a coin.
     *
     * @param params The coin ID to fetch price history for
     * @return Result containing list of price data points or an error
     */
    override suspend operator fun invoke(params: String): Result<List<PriceModel>, DataError> =
        repository.getCoinPriceHistory(params)
}

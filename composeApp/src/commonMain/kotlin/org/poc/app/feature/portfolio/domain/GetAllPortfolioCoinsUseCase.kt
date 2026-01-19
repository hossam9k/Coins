package org.poc.app.feature.portfolio.domain

import kotlinx.coroutines.flow.Flow
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.usecase.NoParamFlowUseCase

/**
 * Use case for observing all portfolio coins.
 * Returns a reactive Flow that emits updates when portfolio changes.
 *
 * @param portfolioRepository The portfolio repository for data access
 */
class GetAllPortfolioCoinsUseCase(
    private val portfolioRepository: PortfolioRepository,
) : NoParamFlowUseCase<Result<List<PortfolioCoinModel>, DataError>> {
    /**
     * Returns a Flow of portfolio coins with their current values.
     *
     * @return Flow emitting list of portfolio coins or errors
     */
    override operator fun invoke(): Flow<Result<List<PortfolioCoinModel>, DataError>> = portfolioRepository.allPortfolioCoinsFlow()
}

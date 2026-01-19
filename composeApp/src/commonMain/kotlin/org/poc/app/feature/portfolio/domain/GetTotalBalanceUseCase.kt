package org.poc.app.feature.portfolio.domain

import kotlinx.coroutines.flow.Flow
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import org.poc.app.core.domain.usecase.NoParamFlowUseCase

/**
 * Use case for observing the user's total balance.
 * Combines cash balance and portfolio value into a single reactive stream.
 *
 * @param portfolioRepository The portfolio repository for data access
 */
class GetTotalBalanceUseCase(
    private val portfolioRepository: PortfolioRepository,
) : NoParamFlowUseCase<Result<Double, DataError>> {
    /**
     * Returns a Flow of the total balance (cash + portfolio value).
     *
     * @return Flow emitting total balance or errors
     */
    override operator fun invoke(): Flow<Result<Double, DataError>> = portfolioRepository.totalBalanceFlow()
}

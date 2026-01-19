package org.poc.app.feature.portfolio.domain

import kotlinx.coroutines.flow.Flow
import org.poc.app.core.domain.usecase.NoParamFlowUseCase

/**
 * Use case for observing the user's cash balance.
 * Returns a reactive Flow that emits updates when balance changes.
 *
 * @param portfolioRepository The portfolio repository for data access
 */
class GetCashBalanceUseCase(
    private val portfolioRepository: PortfolioRepository,
) : NoParamFlowUseCase<Double> {
    /**
     * Returns a Flow of the current cash balance.
     *
     * @return Flow emitting the cash balance as Double
     */
    override operator fun invoke(): Flow<Double> =
        portfolioRepository.cashBalanceFlow()
}

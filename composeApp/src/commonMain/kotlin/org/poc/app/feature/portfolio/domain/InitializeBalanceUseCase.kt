package org.poc.app.feature.portfolio.domain

import org.poc.app.core.domain.usecase.NoParamUseCase

/**
 * Use case for initializing the user's balance.
 * Should be called on app startup to ensure balance exists.
 *
 * @param portfolioRepository The portfolio repository for data access
 */
class InitializeBalanceUseCase(
    private val portfolioRepository: PortfolioRepository,
) : NoParamUseCase<Unit> {
    /**
     * Initializes the balance if not already present.
     */
    override suspend operator fun invoke() {
        portfolioRepository.initializeBalance()
    }
}

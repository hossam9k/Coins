package org.poc.app.feature.portfolio.domain

/**
 * Use case for initializing portfolio balance
 * Handles initial setup and balance calculations
 */
class InitializeBalanceUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    suspend operator fun invoke() {
        portfolioRepository.initializeBalance()
    }
}
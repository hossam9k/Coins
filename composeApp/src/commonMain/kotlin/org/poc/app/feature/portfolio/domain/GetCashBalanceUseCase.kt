package org.poc.app.feature.portfolio.domain

import kotlinx.coroutines.flow.Flow

/**
 * Use case for getting cash balance
 * Provides reactive access to user's cash balance
 */
class GetCashBalanceUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    operator fun invoke(): Flow<Double> {
        return portfolioRepository.cashBalanceFlow()
    }
}
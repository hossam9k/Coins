package org.poc.app.feature.portfolio.domain

import kotlinx.coroutines.flow.Flow
import org.poc.app.shared.common.domain.DataError
import org.poc.app.shared.common.domain.Result

/**
 * Use case for getting total portfolio balance
 * Combines cash balance and portfolio value
 */
class GetTotalBalanceUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    operator fun invoke(): Flow<Result<Double, DataError>> {
        return portfolioRepository.totalBalanceFlow()
    }
}
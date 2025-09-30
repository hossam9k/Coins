package org.poc.app.feature.portfolio.domain

import kotlinx.coroutines.flow.Flow
import org.poc.app.shared.common.domain.DataError
import org.poc.app.shared.common.domain.Result

/**
 * Use case for getting all portfolio coins
 * Follows Clean Architecture - Domain layer depends only on abstractions
 */
class GetAllPortfolioCoinsUseCase(
    private val portfolioRepository: PortfolioRepository
) {
    operator fun invoke(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> {
        return portfolioRepository.allPortfolioCoinsFlow()
    }
}
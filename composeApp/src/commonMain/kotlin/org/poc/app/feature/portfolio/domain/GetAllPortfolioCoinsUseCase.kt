package org.poc.app.feature.portfolio.domain

import kotlinx.coroutines.flow.Flow
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result

/**
 * Use case for getting all portfolio coins
 * Follows Clean Architecture - Domain layer depends only on abstractions
 */
class GetAllPortfolioCoinsUseCase(
    private val portfolioRepository: PortfolioRepository,
) {
    operator fun invoke(): Flow<Result<List<PortfolioCoinModel>, DataError.Remote>> = portfolioRepository.allPortfolioCoinsFlow()
}

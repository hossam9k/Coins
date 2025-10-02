package org.poc.app.feature.portfolio.domain

import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Error

sealed class PortfolioError : Error {
    object InsufficientFunds : PortfolioError()

    object CoinNotFound : PortfolioError()

    object InvalidAmount : PortfolioError()

    object InvalidPrice : PortfolioError()

    object UserNotInitialized : PortfolioError()

    data class NetworkError(
        val cause: DataError.Remote,
    ) : PortfolioError()

    data class DatabaseError(
        val cause: DataError.Local,
    ) : PortfolioError()
}

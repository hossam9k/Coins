package org.poc.app.core.domain.usecase

import kotlinx.coroutines.flow.Flow

/**
 * Base interface for all use cases in the application.
 * Enforces consistent invocation pattern across the codebase.
 *
 * Use cases encapsulate a single business operation and follow
 * the Single Responsibility Principle (SRP).
 *
 * ## Usage
 * ```kotlin
 * class GetUserUseCase(
 *     private val repository: UserRepository
 * ) : UseCase<String, Result<User, DataError>> {
 *     override suspend fun invoke(params: String) = repository.getUser(params)
 * }
 *
 * // Invocation
 * val user = getUserUseCase("user-id")
 * ```
 *
 * @param P Parameter type for the use case
 * @param R Return type of the use case
 *
 * Reference: Clean Architecture by Robert C. Martin
 */
interface UseCase<in P, out R> {
    /**
     * Executes the use case with the given parameters.
     *
     * @param params Input parameters for the use case
     * @return Result of the operation
     */
    suspend operator fun invoke(params: P): R
}

/**
 * Use case without input parameters.
 *
 * ## Usage
 * ```kotlin
 * class GetAllCoinsUseCase(
 *     private val repository: CoinRepository
 * ) : NoParamUseCase<Result<List<Coin>, DataError>> {
 *     override suspend fun invoke() = repository.getCoins()
 * }
 *
 * // Invocation
 * val coins = getAllCoinsUseCase()
 * ```
 *
 * @param R Return type of the use case
 */
interface NoParamUseCase<out R> {
    /**
     * Executes the use case without parameters.
     *
     * @return Result of the operation
     */
    suspend operator fun invoke(): R
}

/**
 * Flow-based use case for reactive data streams.
 * Use this when the use case needs to emit multiple values over time.
 *
 * ## Usage
 * ```kotlin
 * class ObservePortfolioUseCase(
 *     private val repository: PortfolioRepository
 * ) : FlowUseCase<String, Result<Portfolio, DataError>> {
 *     override fun invoke(params: String) = repository.observePortfolio(params)
 * }
 *
 * // Invocation
 * observePortfolioUseCase("portfolio-id").collect { result -> ... }
 * ```
 *
 * @param P Parameter type for the use case
 * @param R Return type emitted by the Flow
 */
interface FlowUseCase<in P, out R> {
    /**
     * Returns a Flow that emits values over time.
     *
     * @param params Input parameters for the use case
     * @return Flow emitting results
     */
    operator fun invoke(params: P): Flow<R>
}

/**
 * Flow-based use case without input parameters.
 *
 * ## Usage
 * ```kotlin
 * class ObserveAllCoinsUseCase(
 *     private val repository: CoinRepository
 * ) : NoParamFlowUseCase<Result<List<Coin>, DataError>> {
 *     override fun invoke() = repository.observeCoins()
 * }
 *
 * // Invocation
 * observeAllCoinsUseCase().collect { result -> ... }
 * ```
 *
 * @param R Return type emitted by the Flow
 */
interface NoParamFlowUseCase<out R> {
    /**
     * Returns a Flow that emits values over time.
     *
     * @return Flow emitting results
     */
    operator fun invoke(): Flow<R>
}

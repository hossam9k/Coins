package org.poc.app.core.data.datasource

import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result

/**
 * Base marker interface for all data sources in the application.
 * Data sources are responsible for fetching and storing data from/to
 * specific sources (network, database, cache, etc.).
 *
 * ## Architecture
 * Data sources sit between repositories and actual data providers:
 * ```
 * Repository -> DataSource Interface -> DataSource Implementation -> External Source
 * ```
 *
 * ## Naming Convention
 * - Interface: `{Feature}RemoteDataSource` or `{Feature}LocalDataSource`
 * - Implementation: `{Technology}{Feature}DataSource` (e.g., `KtorCoinsDataSource`)
 *
 * Reference: Clean Architecture by Robert C. Martin
 */
interface DataSource

/**
 * Marker interface for remote data sources (API, network).
 * Remote data sources handle network requests and return typed Results.
 *
 * ## Implementation Guidelines
 * - Use [Result] type for all operations that can fail
 * - Return [DataError.Remote] for network-related errors
 * - Use DTOs (Data Transfer Objects) for response types
 * - Keep implementations thin - just network calls and error mapping
 *
 * ## Usage
 * ```kotlin
 * interface CoinsRemoteDataSource : RemoteDataSource {
 *     suspend fun getCoins(): Result<CoinsResponseDto, DataError>
 *     suspend fun getCoinById(id: String): Result<CoinDto, DataError>
 * }
 *
 * class KtorCoinsRemoteDataSource(
 *     private val client: HttpClient
 * ) : CoinsRemoteDataSource {
 *     override suspend fun getCoins() = safeCall<CoinsResponseDto> {
 *         client.get("coins")
 *     }
 * }
 * ```
 *
 * @see DataError for error types
 */
interface RemoteDataSource : DataSource

/**
 * Marker interface for local data sources (database, cache, preferences).
 * Local data sources handle persistence operations.
 *
 * ## Implementation Guidelines
 * - Use [Result] type for operations that can fail
 * - Return [DataError.Local] for storage-related errors
 * - Use Entities for database types
 * - Return [kotlinx.coroutines.flow.Flow] for reactive queries
 *
 * ## Usage
 * ```kotlin
 * interface PortfolioLocalDataSource : LocalDataSource {
 *     suspend fun savePortfolio(entity: PortfolioEntity): EmptyResult<DataError.Local>
 *     fun observePortfolio(): Flow<List<PortfolioEntity>>
 * }
 *
 * class RoomPortfolioLocalDataSource(
 *     private val dao: PortfolioDao
 * ) : PortfolioLocalDataSource {
 *     override suspend fun savePortfolio(entity: PortfolioEntity) = try {
 *         dao.insert(entity)
 *         Result.Success(Unit)
 *     } catch (e: SQLiteException) {
 *         Result.Error(DataError.Local.DISK_FULL)
 *     }
 * }
 * ```
 *
 * @see DataError.Local for error types
 */
interface LocalDataSource : DataSource

/**
 * Marker interface for cached data sources.
 * Cache data sources provide in-memory caching capabilities.
 *
 * ## Implementation Guidelines
 * - Implement cache invalidation strategy
 * - Consider memory constraints
 * - Provide cache clearing mechanism
 *
 * ## Usage
 * ```kotlin
 * interface CoinsCacheDataSource : CacheDataSource {
 *     suspend fun getCachedCoins(): List<CoinDto>?
 *     suspend fun cacheCoins(coins: List<CoinDto>)
 *     suspend fun clearCache()
 *     fun isCacheValid(): Boolean
 * }
 * ```
 */
interface CacheDataSource : DataSource

/**
 * Generic interface for data sources that support CRUD operations.
 * Extend this for data sources that need standard create, read, update, delete operations.
 *
 * @param T The type of entity this data source operates on
 * @param ID The type of identifier for the entity
 * @param E The error type for operations
 *
 * ## Usage
 * ```kotlin
 * interface UserLocalDataSource : CrudDataSource<UserEntity, String, DataError.Local> {
 *     // Inherits: getById, getAll, save, delete, deleteById
 *     // Add custom methods as needed
 *     suspend fun getUserByEmail(email: String): Result<UserEntity?, DataError.Local>
 * }
 * ```
 */
interface CrudDataSource<T, ID, E : org.poc.app.core.domain.model.Error> : DataSource {
    /**
     * Retrieves an entity by its identifier.
     *
     * @param id The unique identifier of the entity
     * @return Result containing the entity or null if not found
     */
    suspend fun getById(id: ID): Result<T?, E>

    /**
     * Retrieves all entities.
     *
     * @return Result containing list of all entities
     */
    suspend fun getAll(): Result<List<T>, E>

    /**
     * Saves an entity (insert or update).
     *
     * @param entity The entity to save
     * @return Result indicating success or failure
     */
    suspend fun save(entity: T): Result<Unit, E>

    /**
     * Deletes an entity.
     *
     * @param entity The entity to delete
     * @return Result indicating success or failure
     */
    suspend fun delete(entity: T): Result<Unit, E>

    /**
     * Deletes an entity by its identifier.
     *
     * @param id The unique identifier of the entity to delete
     * @return Result indicating success or failure
     */
    suspend fun deleteById(id: ID): Result<Unit, E>
}

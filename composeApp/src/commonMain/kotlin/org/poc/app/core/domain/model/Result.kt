package org.poc.app.core.domain.model

/**
 * A sealed interface representing the result of an operation that can either succeed or fail.
 * This provides a type-safe alternative to exception-based error handling.
 *
 * @param D The type of data returned on success
 * @param E The type of error returned on failure (must extend Error)
 */
sealed interface Result<out D, out E : Error> {
    /**
     * Represents a successful operation result containing data.
     * Uses Nothing as the error type since success cannot have an error.
     */
    data class Success<out D>(
        val data: D,
    ) : Result<D, Nothing>

    /**
     * Represents a failed operation result containing error information.
     * Uses Nothing as the data type since errors don't carry success data.
     */
    data class Error<out E : org.poc.app.core.domain.model.Error>(
        val error: E,
    ) : Result<Nothing, E>
}

/**
 * Transforms the success data of a Result using the provided mapping function.
 * Only applies transformation if Result is Success, otherwise passes Error unchanged.
 *
 * @param map Function to transform success data
 * @return New Result with transformed data or original error
 */
inline fun <T, E : Error, R> Result<T, E>.map(map: (T) -> R): Result<R, E> =
    when (this) {
        is Result.Error -> Result.Error(error) // Pass through the error unchanged
        is Result.Success -> Result.Success(map(data)) // Transform the success data
    }

/**
 * Converts Result to EmptyResult (Result<Unit, E>).
 * Useful when only caring about success/failure status.
 *
 * @return EmptyResult preserving error type, discarding success data
 */
fun <T, E : Error> Result<T, E>.asEmptyDataResult(): EmptyResult<E> {
    return map { } // Map to Unit, effectively discarding the data
}

/**
 * Executes action if Result is Success, returns original Result.
 * Enables method chaining for success operations.
 *
 * @param action Function to execute with success data
 * @return Original Result unchanged for chaining
 */
inline fun <T, E : Error> Result<T, E>.onSuccess(action: (T) -> Unit): Result<T, E> =
    when (this) {
        is Result.Error -> this // Return unchanged if error
        is Result.Success -> {
            action(data) // Execute the side-effect with success data
            this // Return the original Result for chaining
        }
    }

/**
 * Executes action if Result is Error, returns original Result.
 * Enables method chaining for error handling (logging, analytics).
 *
 * @param action Function to execute with error data
 * @return Original Result unchanged for chaining
 */
inline fun <T, E : Error> Result<T, E>.onError(action: (E) -> Unit): Result<T, E> =
    when (this) {
        is Result.Error -> {
            action(error) // Execute the side-effect with error data
            this // Return the original Result for chaining
        }

        is Result.Success -> this // Return unchanged if success
    }

/**
 * Logs error if Result is Error, returns original Result.
 * Useful for error tracking and debugging.
 *
 * @param logger Logger instance to use
 * @param tag Tag for logging
 * @param message Optional custom message
 * @return Original Result unchanged for chaining
 */
fun <T, E : Error> Result<T, E>.logError(
    logger: Logger,
    tag: String,
    message: String = "Operation failed",
): Result<T, E> =
    onError { error ->
        logger.error(tag, "$message: $error")
    }

/**
 * Logs analytics event if Result is Error.
 *
 * @param analytics Analytics logger instance
 * @param eventName Name of the error event
 * @param additionalData Additional data to log
 * @return Original Result unchanged for chaining
 */
fun <T, E : Error> Result<T, E>.logAnalyticsError(
    analytics: AnalyticsLogger,
    eventName: String,
    additionalData: Map<String, Any> = emptyMap(),
): Result<T, E> =
    onError { error ->
        analytics.logEvent(eventName, additionalData + mapOf("error" to error.toString()))
    }

/**
 * Result that carries no data on success, only Unit.
 * For operations where only success/failure status matters.
 *
 * Example: API calls confirming action completion.
 */
typealias EmptyResult<E> = Result<Unit, E>

/**
 * Retries an operation that returns a Result on failure.
 * Uses exponential backoff with configurable initial delay.
 *
 * @param maxAttempts Maximum number of attempts (default: 3)
 * @param initialDelayMs Initial delay in milliseconds (default: 1000ms)
 * @param shouldRetry Predicate to determine if retry should occur for given error
 * @param block The operation to execute and potentially retry
 * @return Final Result after all attempts
 */
suspend fun <T, E : Error> retryOnError(
    maxAttempts: Int = 3,
    initialDelayMs: Long = 1000,
    shouldRetry: (E) -> Boolean = { true },
    block: suspend () -> Result<T, E>,
): Result<T, E> {
    var lastResult = block()
    var attempt = 1

    while (lastResult is Result.Error && attempt < maxAttempts && shouldRetry(lastResult.error)) {
        kotlinx.coroutines.delay(initialDelayMs * attempt)
        lastResult = block()
        attempt++
    }

    return lastResult
}

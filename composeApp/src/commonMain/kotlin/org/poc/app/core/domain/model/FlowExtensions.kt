package org.poc.app.core.domain.model

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

fun <T> Flow<T>.toResultFlow(): Flow<Result<T, DataError>> =
    this
        .map { Result.Success(it) as Result<T, DataError> }
        .catch { emit(Result.Error(DataError.Remote.UNKNOWN)) }

fun <T, E : Error> Flow<Result<T, E>>.mapError(transform: (E) -> DataError): Flow<Result<T, DataError>> =
    this.map { result ->
        when (result) {
            is Result.Success -> Result.Success(result.data)
            is Result.Error -> Result.Error(transform(result.error))
        }
    }

fun <T> Flow<T?>.toNonNullResultFlow(defaultValue: T): Flow<Result<T, DataError>> =
    this
        .map { Result.Success(it ?: defaultValue) as Result<T, DataError> }
        .catch { emit(Result.Error(DataError.Remote.UNKNOWN)) }

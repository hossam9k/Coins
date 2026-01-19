package org.poc.app.core.data.network

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.HttpErrorCode
import org.poc.app.core.domain.model.Result

/**
 * Maps HTTP responses to typed Results.
 *
 * Single Responsibility: Convert HTTP status codes to [DataError.Remote].
 *
 * ## HTTP Status Code Reference
 * | Code | Error Type |
 * |------|------------|
 * | 2xx | Success |
 * | 400 | BAD_REQUEST |
 * | 401 | UNAUTHORIZED |
 * | 403 | FORBIDDEN |
 * | 404 | NOT_FOUND |
 * | 408 | REQUEST_TIMEOUT |
 * | 409 | CONFLICT |
 * | 413 | PAYLOAD_TOO_LARGE |
 * | 429 | TOO_MANY_REQUESTS |
 * | 500 | SERVER_ERROR |
 * | 503 | SERVICE_UNAVAILABLE |
 * | 5xx | SERVER_ERROR |
 *
 * Reference: https://github.com/philipplackner/Chirp
 */
object ResponseMapper {

    /**
     * Checks if the HTTP status code indicates success (2xx).
     */
    fun isSuccess(statusCode: Int): Boolean = statusCode in 200..299

    /**
     * Maps HTTP status code to [DataError.Remote].
     *
     * @param statusCode The HTTP status code
     * @return Corresponding DataError.Remote
     */
    fun mapStatusCodeToError(statusCode: Int): DataError.Remote =
        HttpErrorCode.fromStatusCode(statusCode)

    /**
     * Maps HTTP response to error Result.
     */
    fun mapToError(response: HttpResponse): Result.Error<DataError.Remote> =
        Result.Error(mapStatusCodeToError(response.status.value))
}

/**
 * Deserializes response body to typed data.
 *
 * @return Result.Success with data or Result.Error with SERIALIZATION error
 */
suspend inline fun <reified T> HttpResponse.deserialize(): Result<T, DataError.Remote> =
    try {
        Result.Success(body<T>())
    } catch (e: Exception) {
        Result.Error(DataError.Remote.SERIALIZATION)
    }

/**
 * Maps HTTP response to Result based on status code.
 * Only handles HTTP-level success/failure, not business errors.
 *
 * @return Result with deserialized data or HTTP error
 */
suspend inline fun <reified T> HttpResponse.toResult(): Result<T, DataError.Remote> =
    if (ResponseMapper.isSuccess(status.value)) {
        deserialize()
    } else {
        ResponseMapper.mapToError(this)
    }

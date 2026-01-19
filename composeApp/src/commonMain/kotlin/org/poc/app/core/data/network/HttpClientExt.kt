package org.poc.app.core.data.network

import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import org.poc.app.core.domain.model.BusinessErrorCode
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result

/**
 * HTTP client extension functions for safe API calls.
 *
 * Composes [ExceptionHandler], [ResponseMapper], and [BusinessErrorHandler]
 * into simple, high-level functions.
 *
 * ## Functions
 *
 * | Function | Use Case |
 * |----------|----------|
 * | [safeCall] | Standard API calls (handles HTTP + business errors) |
 * | [safeApiCall] | APIs using `ApiResponse<T>` wrapper format |
 *
 * ## Usage
 * ```kotlin
 * // Standard API call
 * val result = safeCall<UserDto> { client.get("user") }
 *
 * // With custom error parser
 * val result = safeCall<UserDto>(MyApiErrorParser) { client.get("user") }
 *
 * // For ApiResponse<T> wrapped responses
 * val result = safeApiCall<UserDto> { client.get("profile") }
 * ```
 *
 * Reference: https://github.com/philipplackner/Chirp
 */

// =============================================================================
// SAFE CALL - Primary function for all API calls
// =============================================================================

/**
 * Executes HTTP request with comprehensive error handling.
 *
 * Handles:
 * 1. Network exceptions → [DataError.Remote] (NO_INTERNET, REQUEST_TIMEOUT, etc.)
 * 2. HTTP 4xx/5xx → [DataError.Remote] (UNAUTHORIZED, NOT_FOUND, SERVER_ERROR, etc.)
 * 3. Business errors in body → [DataError.Business] (when API returns 200 with error)
 * 4. Parse failure → [DataError.Remote.SERIALIZATION]
 *
 * ## Example
 * ```kotlin
 * suspend fun getUser(id: String): Result<UserDto, DataError> =
 *     safeCall { client.get("users/$id") }
 * ```
 *
 * @param errorParser Parser for API-specific error format (default: [DefaultApiErrorParser])
 * @param execute Lambda that performs the HTTP request
 * @return [Result] with data or [DataError]
 */
suspend inline fun <reified T> safeCall(
    errorParser: ApiErrorParser = DefaultApiErrorParser,
    execute: () -> HttpResponse,
): Result<T, DataError> {
    // Step 1: Execute with exception handling
    val responseResult = executeRequest(execute)

    return when (responseResult) {
        is Result.Error -> Result.Error(responseResult.error)
        is Result.Success -> {
            val response = responseResult.data

            // Step 2: Check HTTP status
            if (!ResponseMapper.isSuccess(response.status.value)) {
                return Result.Error(ResponseMapper.mapStatusCodeToError(response.status.value))
            }

            // Step 3: Read body once
            val bodyText = response.bodyAsText()

            // Step 4: Check for business error in body
            val businessError =
                BusinessErrorHandler.parseBusinessError(
                    body = bodyText,
                    parser = errorParser,
                )
            if (businessError != null) {
                return Result.Error(businessError)
            }

            // Step 5: Parse as expected type
            try {
                Result.Success(BusinessErrorHandler.json.decodeFromString<T>(bodyText))
            } catch (e: Exception) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
    }
}

// =============================================================================
// SAFE API CALL - For ApiResponse<T> wrapper format
// =============================================================================

/**
 * Executes HTTP request expecting [ApiResponse] wrapper format.
 *
 * Use this when API always wraps responses in a standard format:
 * ```json
 * { "success": true, "data": { ... } }
 * { "success": false, "errorCode": "...", "message": "..." }
 * ```
 *
 * This function **unwraps** the data from the response wrapper.
 *
 * ## Example
 * ```kotlin
 * // API returns: { "success": true, "data": { "id": 1, "name": "John" } }
 * // Result contains: UserDto(id=1, name="John")
 * suspend fun getProfile(): Result<UserDto, DataError> =
 *     safeApiCall { client.get("profile") }
 * ```
 *
 * @param execute Lambda that performs the HTTP request
 * @return [Result] with unwrapped data or [DataError]
 */
suspend inline fun <reified T> safeApiCall(execute: () -> HttpResponse): Result<T, DataError> {
    // Step 1: Execute with exception handling
    val responseResult = executeRequest(execute)

    return when (responseResult) {
        is Result.Error -> Result.Error(responseResult.error)
        is Result.Success -> {
            val response = responseResult.data

            // Step 2: Check HTTP status
            if (!ResponseMapper.isSuccess(response.status.value)) {
                return Result.Error(ResponseMapper.mapStatusCodeToError(response.status.value))
            }

            // Step 3: Parse as ApiResponse wrapper and unwrap
            try {
                val apiResponse = response.body<ApiResponse<T>>()

                if (apiResponse.success && apiResponse.data != null) {
                    Result.Success(apiResponse.data)
                } else {
                    val errorCode = apiResponse.errorCode ?: "UNKNOWN"
                    val message = apiResponse.message
                    Result.Error(BusinessErrorCode.fromCode(errorCode, message))
                }
            } catch (e: Exception) {
                Result.Error(DataError.Remote.SERIALIZATION)
            }
        }
    }
}

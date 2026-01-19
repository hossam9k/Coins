package org.poc.app.core.data.network

import kotlinx.serialization.json.Json
import org.poc.app.core.domain.model.BusinessErrorCode
import org.poc.app.core.domain.model.DataError

/**
 * Handles detection and parsing of business errors from API responses.
 *
 * Single Responsibility: Detect and convert business errors from response body.
 *
 * ## What is a Business Error?
 * HTTP returns 200 (success) but response body indicates error:
 * ```json
 * {
 *   "success": false,
 *   "errorCode": "PROFILE_INCOMPLETE",
 *   "message": "Please complete your profile"
 * }
 * ```
 *
 * ## Customization
 * Projects should implement [ApiErrorParser] for their API format.
 *
 * Reference: https://github.com/philipplackner/Chirp
 */
object BusinessErrorHandler {
    /**
     * JSON parser for error responses.
     * Configured for lenient parsing to handle various API formats.
     */
    val json =
        Json {
            ignoreUnknownKeys = true
            isLenient = true
        }

    /**
     * Attempts to parse a business error from response body.
     *
     * @param body Response body as text
     * @param parser Parser for the specific API format
     * @return [DataError.Business] if business error detected, null otherwise
     */
    fun parseBusinessError(
        body: String,
        parser: ApiErrorParser = DefaultApiErrorParser,
    ): DataError.Business? {
        val errorInfo = parser.parse(body) ?: return null

        return if (errorInfo.isError && errorInfo.code != null) {
            BusinessErrorCode.fromCode(errorInfo.code, errorInfo.message)
        } else {
            null
        }
    }
}

/**
 * Parsed error information from API response.
 *
 * This is a normalized representation of error data that can come
 * from various API formats.
 */
data class BusinessErrorInfo(
    /** Whether the response indicates an error */
    val isError: Boolean,
    /** The error code (e.g., "PROFILE_INCOMPLETE") */
    val code: String?,
    /** Human-readable error message */
    val message: String?,
)

/**
 * Interface for parsing API-specific error response formats.
 *
 * ## Why Interface?
 * Different APIs have different error formats:
 * - `{ "success": false, "errorCode": "..." }`
 * - `{ "error": { "code": "...", "message": "..." } }`
 * - `{ "status": 1001, "msg": "..." }`
 *
 * Each project implements this interface for their API format.
 *
 * ## Implementation Example
 * ```kotlin
 * object MyApiErrorParser : ApiErrorParser {
 *     @Serializable
 *     data class MyErrorResponse(
 *         val status: Int,
 *         val errorCode: String? = null,
 *         val msg: String? = null
 *     )
 *
 *     override fun parse(body: String): BusinessErrorInfo? {
 *         return try {
 *             val response = Json.decodeFromString<MyErrorResponse>(body)
 *             BusinessErrorInfo(
 *                 isError = response.status != 0,
 *                 code = response.errorCode,
 *                 message = response.msg
 *             )
 *         } catch (e: Exception) {
 *             null
 *         }
 *     }
 * }
 * ```
 */
interface ApiErrorParser {
    /**
     * Parses response body to extract error information.
     *
     * @param body Response body as text
     * @return [BusinessErrorInfo] if parseable, null if not an error format
     */
    fun parse(body: String): BusinessErrorInfo?
}

/**
 * Default implementation supporting common API error formats.
 *
 * Supports:
 * - `{ "success": false, "errorCode": "...", "message": "..." }`
 * - `{ "error": { "code": "...", "message": "..." } }`
 */
object DefaultApiErrorParser : ApiErrorParser {
    override fun parse(body: String): BusinessErrorInfo? =
        try {
            val errorBody = BusinessErrorHandler.json.decodeFromString<ApiErrorBody>(body)
            BusinessErrorInfo(
                isError = errorBody.isBusinessError(),
                code = errorBody.extractErrorCode(),
                message = errorBody.extractErrorMessage(),
            )
        } catch (
            @Suppress("SwallowedException") e: Exception,
        ) {
            // Parsing failed - not a business error format
            null
        }
}

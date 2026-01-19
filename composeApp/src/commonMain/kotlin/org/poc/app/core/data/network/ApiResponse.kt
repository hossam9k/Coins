package org.poc.app.core.data.network

import kotlinx.serialization.Serializable

/**
 * Generic API response wrapper DTOs.
 *
 * Single Responsibility: Define data structures for API responses.
 *
 * ## Common API Response Patterns
 *
 * ### Pattern 1: Success flag with data/error
 * ```json
 * { "success": true, "data": { ... } }
 * { "success": false, "errorCode": "PROFILE_INCOMPLETE", "message": "..." }
 * ```
 *
 * ### Pattern 2: Data or error (nullable)
 * ```json
 * { "data": { ... }, "error": null }
 * { "data": null, "error": { "code": "...", "message": "..." } }
 * ```
 *
 * ### Pattern 3: Status code in body
 * ```json
 * { "status": 0, "data": { ... } }
 * { "status": 1001, "message": "..." }
 * ```
 *
 * ## Customization
 * Create your own response DTO for your specific API format:
 * ```kotlin
 * @Serializable
 * data class MyApiResponse<T>(
 *     val status: Int,
 *     val data: T? = null,
 *     val errorCode: String? = null,
 *     val msg: String? = null
 * ) {
 *     val isSuccess: Boolean get() = status == 0
 * }
 * ```
 */

/**
 * Generic API response wrapper.
 *
 * Use when API wraps all responses in a standard format:
 * ```json
 * { "success": true, "data": { "id": 1, "name": "..." } }
 * { "success": false, "errorCode": "ERROR_CODE", "message": "..." }
 * ```
 *
 * @param T The type of data on success
 */
@Serializable
data class ApiResponse<T>(
    /** Indicates if the request was successful at business level */
    val success: Boolean = true,
    /** The actual response data (null on error) */
    val data: T? = null,
    /** Business error code (null on success) */
    val errorCode: String? = null,
    /** Human-readable error message */
    val message: String? = null,
    /** Additional error details/metadata */
    val errorDetails: Map<String, String>? = null,
) {
    /**
     * Check if this response indicates a business error.
     */
    fun isBusinessError(): Boolean = !success || errorCode != null
}

/**
 * Simplified error response for detecting business errors.
 *
 * Use this when you only need to check if a response is an error,
 * before deserializing the actual data type.
 *
 * Supports multiple common formats:
 * - Direct fields: `{ "success": false, "errorCode": "...", "message": "..." }`
 * - Nested error: `{ "error": { "code": "...", "message": "..." } }`
 */
@Serializable
data class ApiErrorBody(
    /** Success flag (some APIs use this) */
    val success: Boolean = true,
    /** Direct error code field */
    val errorCode: String? = null,
    /** Direct message field */
    val message: String? = null,
    /** Nested error object (some APIs use this) */
    val error: ErrorDetail? = null,
) {
    /**
     * Nested error detail object.
     */
    @Serializable
    data class ErrorDetail(
        val code: String? = null,
        val message: String? = null,
    )

    /**
     * Check if this response indicates a business error.
     * Checks both direct fields and nested error object.
     */
    fun isBusinessError(): Boolean = !success || errorCode != null || error?.code != null

    /**
     * Extract error code from either direct field or nested object.
     */
    fun extractErrorCode(): String? = errorCode ?: error?.code

    /**
     * Extract error message from either direct field or nested object.
     */
    fun extractErrorMessage(): String? = message ?: error?.message
}

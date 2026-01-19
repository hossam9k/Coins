package org.poc.app.core.data.network

import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Logger
import org.poc.app.core.domain.model.Result

/**
 * Network error logging utilities.
 *
 * Single Responsibility: Log network errors for debugging and analytics.
 *
 * ## Usage
 * ```kotlin
 * val result = safeCall<UserDto> { client.get("user") }
 *     .logNetworkError(logger, "UserRepository")
 *     .logAnalytics(analytics, "get_user_failed")
 * ```
 */
object NetworkErrorLogger {
    private const val DEFAULT_TAG = "Network"

    /**
     * Logs a [DataError] with appropriate severity and context.
     *
     * @param error The error to log
     * @param logger Logger instance
     * @param tag Log tag (usually class name)
     * @param context Additional context (e.g., endpoint, params)
     */
    fun log(
        error: DataError,
        logger: Logger,
        tag: String = DEFAULT_TAG,
        context: String? = null,
    ) {
        val message = buildLogMessage(error, context)

        when (error) {
            // Debug level for expected/user-caused errors
            DataError.Remote.NO_INTERNET,
            DataError.Remote.REQUEST_TIMEOUT,
            -> logger.debug(tag, message)

            is DataError.Business.ValidationFailed -> logger.debug(tag, message)

            // Warning level for client errors
            DataError.Remote.UNAUTHORIZED,
            DataError.Remote.FORBIDDEN,
            DataError.Remote.NOT_FOUND,
            DataError.Remote.BAD_REQUEST,
            DataError.Remote.TOO_MANY_REQUESTS,
            DataError.Remote.CONFLICT,
            DataError.Remote.PAYLOAD_TOO_LARGE,
            DataError.Remote.CONNECTION_REFUSED,
            -> logger.warn(tag, message)

            is DataError.Business.SessionExpired -> logger.warn(tag, message)

            // Error level for server/unexpected errors
            DataError.Remote.SERVER_ERROR,
            DataError.Remote.SERVICE_UNAVAILABLE,
            DataError.Remote.SSL_ERROR,
            DataError.Remote.SERIALIZATION,
            DataError.Remote.UNKNOWN,
            -> logger.error(tag, message)

            // Info level for other business errors (expected flow)
            is DataError.Business -> logger.info(tag, message)

            // Warning for local errors
            is DataError.Local -> logger.warn(tag, message)
        }
    }

    /**
     * Logs error to analytics for tracking.
     *
     * @param error The error to log
     * @param analytics Analytics logger
     * @param eventName Event name for analytics
     * @param additionalData Extra data to include
     */
    fun logAnalytics(
        error: DataError,
        analytics: AnalyticsLogger,
        eventName: String,
        additionalData: Map<String, Any> = emptyMap(),
    ) {
        val errorData = buildAnalyticsData(error) + additionalData
        analytics.logEvent(eventName, errorData)
    }

    private fun buildLogMessage(
        error: DataError,
        context: String?,
    ): String {
        val baseMessage =
            when (error) {
                is DataError.Remote -> "Remote error: ${error.name}"
                is DataError.Local -> "Local error: ${error.name}"
                is DataError.Business -> "Business error: ${getBusinessErrorCode(error)}"
            }

        val details =
            when (error) {
                is DataError.Business -> error.message?.let { " - $it" } ?: ""
                else -> ""
            }

        val contextStr = context?.let { " [$it]" } ?: ""

        return "$baseMessage$details$contextStr"
    }

    /**
     * Extracts error code from Business error types.
     */
    private fun getBusinessErrorCode(error: DataError.Business): String =
        when (error) {
            is DataError.Business.ProfileIncomplete -> "PROFILE_INCOMPLETE"
            is DataError.Business.EmailNotVerified -> "EMAIL_NOT_VERIFIED"
            is DataError.Business.PhoneNotVerified -> "PHONE_NOT_VERIFIED"
            is DataError.Business.KycRequired -> "KYC_REQUIRED"
            is DataError.Business.AccountSuspended -> "ACCOUNT_SUSPENDED"
            is DataError.Business.MinimumAmountNotMet -> "MINIMUM_AMOUNT_NOT_MET"
            is DataError.Business.MaximumAmountExceeded -> "MAXIMUM_AMOUNT_EXCEEDED"
            is DataError.Business.DailyLimitReached -> "DAILY_LIMIT_REACHED"
            is DataError.Business.FeatureDisabled -> "FEATURE_DISABLED"
            is DataError.Business.ResourceUnavailable -> "RESOURCE_UNAVAILABLE"
            is DataError.Business.ValidationFailed -> "VALIDATION_FAILED"
            is DataError.Business.DuplicateEntry -> "DUPLICATE_ENTRY"
            is DataError.Business.SessionExpired -> "SESSION_EXPIRED"
            is DataError.Business.ReAuthRequired -> "REAUTH_REQUIRED"
            is DataError.Business.Unknown -> error.code
        }

    private fun buildAnalyticsData(error: DataError): Map<String, Any> {
        val data =
            mutableMapOf<String, Any>(
                "error_category" to
                    when (error) {
                        is DataError.Remote -> "remote"
                        is DataError.Local -> "local"
                        is DataError.Business -> "business"
                    },
            )

        when (error) {
            is DataError.Remote -> {
                data["error_type"] = error.name
            }
            is DataError.Business -> {
                data["error_type"] = getBusinessErrorCode(error)
                error.message?.let { data["message"] = it }
            }
            is DataError.Local -> {
                data["error_type"] = error.name
            }
        }

        return data
    }
}

/**
 * Extension to log network errors on Result.
 *
 * Logs only on error, passes through on success.
 * Enables fluent chaining:
 * ```kotlin
 * safeCall<UserDto> { ... }
 *     .logNetworkError(logger, "UserRepo", "getUser")
 *     .onSuccess { ... }
 * ```
 */
fun <T, E : DataError> Result<T, E>.logNetworkError(
    logger: Logger,
    tag: String,
    context: String? = null,
): Result<T, E> {
    if (this is Result.Error) {
        NetworkErrorLogger.log(error, logger, tag, context)
    }
    return this
}

/**
 * Extension to log error to analytics.
 *
 * Logs only on error, passes through on success.
 * ```kotlin
 * safeCall<UserDto> { ... }
 *     .logNetworkAnalytics(analytics, "api_error", mapOf("endpoint" to "user"))
 * ```
 */
fun <T, E : DataError> Result<T, E>.logNetworkAnalytics(
    analytics: AnalyticsLogger,
    eventName: String,
    additionalData: Map<String, Any> = emptyMap(),
): Result<T, E> {
    if (this is Result.Error) {
        NetworkErrorLogger.logAnalytics(error, analytics, eventName, additionalData)
    }
    return this
}

package org.poc.app.core.domain.model

/**
 * Sealed interface that defines all possible data-related errors in the application.
 * Extends the base Error interface to provide type safety for Result types.
 *
 * This error hierarchy follows clean architecture principles and provides:
 * - Type-safe error handling with exhaustive when expressions
 * - Clear separation between remote (network), local (device), and business errors
 * - Enum for simple errors (Remote, Local), Sealed class for complex errors (Business)
 *
 * Reference: https://github.com/philipplackner/Chirp
 */
sealed interface DataError : Error {
    /**
     * Network and remote server related errors.
     * Uses enum for type-safe, exhaustive error handling.
     * Simple errors with no additional data needed.
     *
     * ## Categories
     * - Authentication: UNAUTHORIZED, FORBIDDEN
     * - Client (4xx): BAD_REQUEST, NOT_FOUND, CONFLICT, PAYLOAD_TOO_LARGE, TOO_MANY_REQUESTS
     * - Network: NO_INTERNET, REQUEST_TIMEOUT, SSL_ERROR, CONNECTION_REFUSED
     * - Server (5xx): SERVER_ERROR, SERVICE_UNAVAILABLE
     * - Parsing: SERIALIZATION
     * - Unknown: UNKNOWN
     */
    enum class Remote : DataError {
        // Authentication errors
        UNAUTHORIZED,
        FORBIDDEN,

        // Client errors (4xx)
        BAD_REQUEST,
        NOT_FOUND,
        CONFLICT,
        PAYLOAD_TOO_LARGE,
        TOO_MANY_REQUESTS,

        // Network errors
        NO_INTERNET,
        REQUEST_TIMEOUT,
        SSL_ERROR,
        CONNECTION_REFUSED,

        // Server errors (5xx)
        SERVER_ERROR,
        SERVICE_UNAVAILABLE,

        // Parsing errors
        SERIALIZATION,

        // Unknown
        UNKNOWN,
    }

    /**
     * Local device and storage related errors.
     * Uses enum - simple errors with no additional data.
     */
    enum class Local : DataError {
        DISK_FULL,
        NOT_FOUND,
        INSUFFICIENT_FUNDS,
        UNKNOWN,
    }

    /**
     * Business logic errors returned by API with HTTP 200.
     * Uses sealed class to carry additional context from API response.
     *
     * ## Why Sealed Class (not Enum)?
     * Business errors often need to carry:
     * - Original error message from API
     * - Field-specific validation errors
     * - Additional context (limits, required actions, etc.)
     *
     * ## Usage
     * ```kotlin
     * when (error) {
     *     is DataError.Business.ProfileIncomplete -> {
     *         showMessage(error.message ?: "Complete your profile")
     *         highlightFields(error.requiredFields)
     *     }
     * }
     * ```
     */
    sealed class Business(
        open val message: String? = null,
    ) : DataError {
        // ============== User/Profile Errors ==============

        /** User profile is incomplete */
        data class ProfileIncomplete(
            override val message: String? = null,
            val requiredFields: List<String>? = null,
        ) : Business(message)

        /** Email not verified */
        data class EmailNotVerified(
            override val message: String? = null,
        ) : Business(message)

        /** Phone not verified */
        data class PhoneNotVerified(
            override val message: String? = null,
        ) : Business(message)

        /** KYC verification required */
        data class KycRequired(
            override val message: String? = null,
        ) : Business(message)

        /** Account is suspended */
        data class AccountSuspended(
            override val message: String? = null,
            val reason: String? = null,
        ) : Business(message)

        // ============== Transaction/Trading Errors ==============

        /** Minimum amount not met */
        data class MinimumAmountNotMet(
            override val message: String? = null,
            val minimumAmount: String? = null,
        ) : Business(message)

        /** Maximum amount exceeded */
        data class MaximumAmountExceeded(
            override val message: String? = null,
            val maximumAmount: String? = null,
        ) : Business(message)

        /** Daily limit reached */
        data class DailyLimitReached(
            override val message: String? = null,
            val limit: String? = null,
        ) : Business(message)

        /** Feature/service temporarily disabled */
        data class FeatureDisabled(
            override val message: String? = null,
            val featureName: String? = null,
        ) : Business(message)

        /** Resource no longer available */
        data class ResourceUnavailable(
            override val message: String? = null,
            val resourceId: String? = null,
        ) : Business(message)

        // ============== Validation Errors ==============

        /** Invalid input data */
        data class ValidationFailed(
            override val message: String? = null,
            val fieldErrors: Map<String, String>? = null,
        ) : Business(message)

        /** Duplicate resource/entry */
        data class DuplicateEntry(
            override val message: String? = null,
            val field: String? = null,
        ) : Business(message)

        // ============== Session/Auth Business Errors ==============

        /** Session expired (from response body, not HTTP 401) */
        data class SessionExpired(
            override val message: String? = null,
        ) : Business(message)

        /** Action requires re-authentication */
        data class ReAuthRequired(
            override val message: String? = null,
        ) : Business(message)

        // ============== Generic Error ==============

        /**
         * Generic business error for unknown error codes.
         * Use as fallback when API returns unrecognized error code.
         */
        data class Unknown(
            val code: String,
            override val message: String? = null,
        ) : Business(message)
    }
}

/**
 * Maps HTTP status codes to [DataError.Remote] enum values.
 */
object HttpErrorCode {
    fun fromStatusCode(statusCode: Int): DataError.Remote =
        when (statusCode) {
            400 -> DataError.Remote.BAD_REQUEST
            401 -> DataError.Remote.UNAUTHORIZED
            403 -> DataError.Remote.FORBIDDEN
            404 -> DataError.Remote.NOT_FOUND
            408 -> DataError.Remote.REQUEST_TIMEOUT
            409 -> DataError.Remote.CONFLICT
            413 -> DataError.Remote.PAYLOAD_TOO_LARGE
            429 -> DataError.Remote.TOO_MANY_REQUESTS
            500 -> DataError.Remote.SERVER_ERROR
            503 -> DataError.Remote.SERVICE_UNAVAILABLE
            in 500..599 -> DataError.Remote.SERVER_ERROR
            else -> DataError.Remote.UNKNOWN
        }
}

/**
 * Maps API error code strings to [DataError.Business].
 *
 * ## Customization
 * Projects can extend this by creating their own mapper:
 * ```kotlin
 * object MyBusinessErrorMapper {
 *     fun fromCode(code: String, message: String?): DataError.Business =
 *         when (code.uppercase()) {
 *             "MY_CUSTOM_ERROR" -> MyCustomError(message)
 *             else -> BusinessErrorCode.fromCode(code, message)
 *         }
 * }
 * ```
 */
object BusinessErrorCode {
    private val codeToErrorFactory: Map<String, (String?) -> DataError.Business> =
        mapOf(
            // Profile/User errors
            "PROFILE_INCOMPLETE" to { msg -> DataError.Business.ProfileIncomplete(msg) },
            "INCOMPLETE_PROFILE" to { msg -> DataError.Business.ProfileIncomplete(msg) },
            "EMAIL_NOT_VERIFIED" to { msg -> DataError.Business.EmailNotVerified(msg) },
            "UNVERIFIED_EMAIL" to { msg -> DataError.Business.EmailNotVerified(msg) },
            "PHONE_NOT_VERIFIED" to { msg -> DataError.Business.PhoneNotVerified(msg) },
            "UNVERIFIED_PHONE" to { msg -> DataError.Business.PhoneNotVerified(msg) },
            "KYC_REQUIRED" to { msg -> DataError.Business.KycRequired(msg) },
            "KYC_PENDING" to { msg -> DataError.Business.KycRequired(msg) },
            "VERIFICATION_REQUIRED" to { msg -> DataError.Business.KycRequired(msg) },
            "ACCOUNT_SUSPENDED" to { msg -> DataError.Business.AccountSuspended(msg) },
            "ACCOUNT_BLOCKED" to { msg -> DataError.Business.AccountSuspended(msg) },
            "USER_SUSPENDED" to { msg -> DataError.Business.AccountSuspended(msg) },
            // Transaction errors
            "MINIMUM_AMOUNT_NOT_MET" to { msg -> DataError.Business.MinimumAmountNotMet(msg) },
            "MIN_AMOUNT" to { msg -> DataError.Business.MinimumAmountNotMet(msg) },
            "AMOUNT_TOO_LOW" to { msg -> DataError.Business.MinimumAmountNotMet(msg) },
            "MAXIMUM_AMOUNT_EXCEEDED" to { msg -> DataError.Business.MaximumAmountExceeded(msg) },
            "MAX_AMOUNT" to { msg -> DataError.Business.MaximumAmountExceeded(msg) },
            "AMOUNT_TOO_HIGH" to { msg -> DataError.Business.MaximumAmountExceeded(msg) },
            "DAILY_LIMIT_REACHED" to { msg -> DataError.Business.DailyLimitReached(msg) },
            "LIMIT_EXCEEDED" to { msg -> DataError.Business.DailyLimitReached(msg) },
            "QUOTA_EXCEEDED" to { msg -> DataError.Business.DailyLimitReached(msg) },
            "FEATURE_DISABLED" to { msg -> DataError.Business.FeatureDisabled(msg) },
            "SERVICE_UNAVAILABLE" to { msg -> DataError.Business.FeatureDisabled(msg) },
            "MAINTENANCE" to { msg -> DataError.Business.FeatureDisabled(msg) },
            "RESOURCE_UNAVAILABLE" to { msg -> DataError.Business.ResourceUnavailable(msg) },
            "NOT_AVAILABLE" to { msg -> DataError.Business.ResourceUnavailable(msg) },
            "DELISTED" to { msg -> DataError.Business.ResourceUnavailable(msg) },
            // Validation errors
            "VALIDATION_FAILED" to { msg -> DataError.Business.ValidationFailed(msg) },
            "INVALID_INPUT" to { msg -> DataError.Business.ValidationFailed(msg) },
            "VALIDATION_ERROR" to { msg -> DataError.Business.ValidationFailed(msg) },
            "DUPLICATE_ENTRY" to { msg -> DataError.Business.DuplicateEntry(msg) },
            "ALREADY_EXISTS" to { msg -> DataError.Business.DuplicateEntry(msg) },
            "DUPLICATE" to { msg -> DataError.Business.DuplicateEntry(msg) },
            // Session errors
            "SESSION_EXPIRED" to { msg -> DataError.Business.SessionExpired(msg) },
            "TOKEN_EXPIRED" to { msg -> DataError.Business.SessionExpired(msg) },
            "REAUTH_REQUIRED" to { msg -> DataError.Business.ReAuthRequired(msg) },
            "REAUTHENTICATION_REQUIRED" to { msg -> DataError.Business.ReAuthRequired(msg) },
        )

    /**
     * Converts API error code string to [DataError.Business].
     *
     * @param code The error code from API response (case-insensitive)
     * @param message Optional error message from API
     * @return Typed [DataError.Business] or [DataError.Business.Unknown]
     */
    fun fromCode(
        code: String,
        message: String? = null,
    ): DataError.Business {
        val factory = codeToErrorFactory[code.uppercase()]
        return factory?.invoke(message) ?: DataError.Business.Unknown(code, message)
    }
}

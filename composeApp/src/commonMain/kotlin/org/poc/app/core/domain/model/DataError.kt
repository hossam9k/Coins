package org.poc.app.core.domain.model

/**
 * Sealed interface that defines all possible data-related errors in the application.
 * Extends the base Error interface to provide type safety for Result types.
 */
sealed interface DataError : Error {
    /**
     * Network and remote server related errors.
     * These errors occur during API calls or network operations.
     */
    enum class Remote : DataError {
        /** Request took too long to complete */
        REQUEST_TIMEOUT,

        /** Server returned 429 - too many requests */
        TOO_MANY_REQUESTS,

        /** No internet connection available */
        NO_INTERNET,

        /** Server error (5xx status codes) */
        SERVER,

        /** Failed to parse response data */
        SERIALIZATION,

        /** Unexpected or unhandled error */
        UNKNOWN,
    }

    /**
     * Local device and storage related errors.
     * These errors occur on the device itself.
     */
    enum class Local : DataError {
        /** Device storage is full */
        DISK_FULL,

        /** Not enough funds for the operation */
        INSUFFICIENT_FUNDS,

        /** Unexpected or unhandled local error */
        UNKNOWN,
    }
}

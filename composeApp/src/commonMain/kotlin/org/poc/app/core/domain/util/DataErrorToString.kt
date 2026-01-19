package org.poc.app.core.domain.util

import kmp_poc.composeapp.generated.resources.Res
import kmp_poc.composeapp.generated.resources.error_account_suspended
import kmp_poc.composeapp.generated.resources.error_bad_request
import kmp_poc.composeapp.generated.resources.error_business_generic
import kmp_poc.composeapp.generated.resources.error_conflict
import kmp_poc.composeapp.generated.resources.error_connection_refused
import kmp_poc.composeapp.generated.resources.error_daily_limit
import kmp_poc.composeapp.generated.resources.error_disk_full
import kmp_poc.composeapp.generated.resources.error_duplicate_entry
import kmp_poc.composeapp.generated.resources.error_email_not_verified
import kmp_poc.composeapp.generated.resources.error_feature_disabled
import kmp_poc.composeapp.generated.resources.error_forbidden
import kmp_poc.composeapp.generated.resources.error_insufficient_balance
import kmp_poc.composeapp.generated.resources.error_kyc_required
import kmp_poc.composeapp.generated.resources.error_maximum_amount
import kmp_poc.composeapp.generated.resources.error_minimum_amount
import kmp_poc.composeapp.generated.resources.error_no_internet
import kmp_poc.composeapp.generated.resources.error_not_found
import kmp_poc.composeapp.generated.resources.error_payload_too_large
import kmp_poc.composeapp.generated.resources.error_phone_not_verified
import kmp_poc.composeapp.generated.resources.error_profile_incomplete
import kmp_poc.composeapp.generated.resources.error_reauth_required
import kmp_poc.composeapp.generated.resources.error_request_timeout
import kmp_poc.composeapp.generated.resources.error_resource_unavailable
import kmp_poc.composeapp.generated.resources.error_serialization
import kmp_poc.composeapp.generated.resources.error_server
import kmp_poc.composeapp.generated.resources.error_service_unavailable
import kmp_poc.composeapp.generated.resources.error_session_expired
import kmp_poc.composeapp.generated.resources.error_ssl
import kmp_poc.composeapp.generated.resources.error_too_many_requests
import kmp_poc.composeapp.generated.resources.error_unauthorized
import kmp_poc.composeapp.generated.resources.error_unknown
import kmp_poc.composeapp.generated.resources.error_validation_failed
import org.jetbrains.compose.resources.StringResource
import org.poc.app.core.domain.model.DataError

/**
 * Extension function to convert DataError to user-friendly string resource.
 * This provides a centralized mapping for error messages across the app.
 *
 * ## Error Categories
 * 1. **Remote Errors** (enum) - HTTP status codes and network issues
 * 2. **Local Errors** (enum) - Device storage and local data issues
 * 3. **Business Errors** (sealed class) - API returns 200 but error in response body
 *
 * Reference: https://github.com/philipplackner/Chirp
 */
fun DataError.toUiText(): StringResource =
    when (this) {
        // ============== Remote Errors (enum) ==============

        // Authentication errors
        DataError.Remote.UNAUTHORIZED -> Res.string.error_unauthorized
        DataError.Remote.FORBIDDEN -> Res.string.error_forbidden

        // Client errors (4xx)
        DataError.Remote.BAD_REQUEST -> Res.string.error_bad_request
        DataError.Remote.NOT_FOUND -> Res.string.error_not_found
        DataError.Remote.CONFLICT -> Res.string.error_conflict
        DataError.Remote.PAYLOAD_TOO_LARGE -> Res.string.error_payload_too_large
        DataError.Remote.TOO_MANY_REQUESTS -> Res.string.error_too_many_requests

        // Network errors
        DataError.Remote.NO_INTERNET -> Res.string.error_no_internet
        DataError.Remote.REQUEST_TIMEOUT -> Res.string.error_request_timeout
        DataError.Remote.SSL_ERROR -> Res.string.error_ssl
        DataError.Remote.CONNECTION_REFUSED -> Res.string.error_connection_refused

        // Server errors (5xx)
        DataError.Remote.SERVER_ERROR -> Res.string.error_server
        DataError.Remote.SERVICE_UNAVAILABLE -> Res.string.error_service_unavailable

        // Parsing errors
        DataError.Remote.SERIALIZATION -> Res.string.error_serialization

        // Unknown errors
        DataError.Remote.UNKNOWN -> Res.string.error_unknown

        // ============== Local Errors (enum) ==============

        DataError.Local.DISK_FULL -> Res.string.error_disk_full
        DataError.Local.NOT_FOUND -> Res.string.error_not_found
        DataError.Local.INSUFFICIENT_FUNDS -> Res.string.error_insufficient_balance
        DataError.Local.UNKNOWN -> Res.string.error_unknown

        // ============== Business Errors (sealed class) ==============
        // These are HTTP 200 responses with error codes in body

        // User/Profile errors
        is DataError.Business.ProfileIncomplete -> Res.string.error_profile_incomplete
        is DataError.Business.EmailNotVerified -> Res.string.error_email_not_verified
        is DataError.Business.PhoneNotVerified -> Res.string.error_phone_not_verified
        is DataError.Business.KycRequired -> Res.string.error_kyc_required
        is DataError.Business.AccountSuspended -> Res.string.error_account_suspended

        // Transaction/Trading errors
        is DataError.Business.MinimumAmountNotMet -> Res.string.error_minimum_amount
        is DataError.Business.MaximumAmountExceeded -> Res.string.error_maximum_amount
        is DataError.Business.DailyLimitReached -> Res.string.error_daily_limit
        is DataError.Business.FeatureDisabled -> Res.string.error_feature_disabled
        is DataError.Business.ResourceUnavailable -> Res.string.error_resource_unavailable

        // Validation errors
        is DataError.Business.ValidationFailed -> Res.string.error_validation_failed
        is DataError.Business.DuplicateEntry -> Res.string.error_duplicate_entry

        // Session errors
        is DataError.Business.SessionExpired -> Res.string.error_session_expired
        is DataError.Business.ReAuthRequired -> Res.string.error_reauth_required

        // Unknown business error (fallback for unrecognized error codes)
        is DataError.Business.Unknown -> Res.string.error_business_generic
    }

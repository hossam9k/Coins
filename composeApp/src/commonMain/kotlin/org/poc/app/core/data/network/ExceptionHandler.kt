package org.poc.app.core.data.network

import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.network.sockets.SocketTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.statement.HttpResponse
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.ensureActive
import kotlinx.io.IOException
import org.poc.app.core.domain.model.DataError
import org.poc.app.core.domain.model.Result
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.coroutineContext

/**
 * Centralized network exception handler.
 *
 * Single Responsibility: Convert network exceptions to typed [DataError.Remote].
 *
 * ## Supported Exceptions
 * - [CancellationException] → Rethrown (structured concurrency)
 * - [SocketTimeoutException] → REQUEST_TIMEOUT
 * - [HttpRequestTimeoutException] → REQUEST_TIMEOUT
 * - [ConnectTimeoutException] → REQUEST_TIMEOUT
 * - [UnresolvedAddressException] → NO_INTERNET
 * - [IOException] → Mapped based on message (SSL_ERROR, CONNECTION_REFUSED, etc.)
 * - Other exceptions → UNKNOWN
 *
 * Reference: https://github.com/philipplackner/Chirp
 */
object ExceptionHandler {
    /**
     * Executes an HTTP request with comprehensive exception handling.
     *
     * @param onError Callback when an error occurs, receives the mapped error
     * @param execute The HTTP request to execute
     * @return HttpResponse on success, null on error (error passed to onError)
     */
    suspend inline fun execute(
        onError: (DataError.Remote) -> Unit,
        execute: () -> HttpResponse,
    ): HttpResponse? =
        try {
            execute()
        } catch (e: CancellationException) {
            // CRITICAL: Never catch cancellation - rethrow for structured concurrency
            throw e
        } catch (
            @Suppress("SwallowedException") e: SocketTimeoutException,
        ) {
            onError(DataError.Remote.REQUEST_TIMEOUT)
            null
        } catch (
            @Suppress("SwallowedException") e: HttpRequestTimeoutException,
        ) {
            onError(DataError.Remote.REQUEST_TIMEOUT)
            null
        } catch (
            @Suppress("SwallowedException") e: ConnectTimeoutException,
        ) {
            onError(DataError.Remote.REQUEST_TIMEOUT)
            null
        } catch (
            @Suppress("SwallowedException") e: UnresolvedAddressException,
        ) {
            onError(DataError.Remote.NO_INTERNET)
            null
        } catch (e: IOException) {
            onError(mapIOException(e))
            null
        } catch (
            @Suppress("SwallowedException") e: Exception,
        ) {
            coroutineContext.ensureActive()
            onError(DataError.Remote.UNKNOWN)
            null
        }

    /**
     * Maps IOException to specific DataError.Remote types.
     *
     * Analyzes exception message to provide granular error types:
     * - SSL/Certificate/Handshake → SSL_ERROR
     * - Refused/Reset → CONNECTION_REFUSED
     * - Other → NO_INTERNET
     */
    fun mapIOException(e: IOException): DataError.Remote {
        val message = e.message?.lowercase() ?: ""
        return when {
            message.containsAny("ssl", "certificate", "handshake") ->
                DataError.Remote.SSL_ERROR

            message.containsAny("refused", "reset") ->
                DataError.Remote.CONNECTION_REFUSED

            else -> DataError.Remote.NO_INTERNET
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean = keywords.any { this.contains(it) }
}

/**
 * Executes HTTP request with exception handling, returning Result directly.
 *
 * This is a convenience wrapper around [ExceptionHandler.execute] that
 * returns [Result.Error] on exception instead of using a callback.
 *
 * @param execute The HTTP request to execute
 * @return Result with HttpResponse or DataError.Remote
 */
suspend inline fun executeRequest(execute: () -> HttpResponse): Result<HttpResponse, DataError.Remote> {
    var error: DataError.Remote? = null

    val response =
        ExceptionHandler.execute(
            onError = { error = it },
            execute = execute,
        )

    return if (response != null) {
        Result.Success(response)
    } else {
        Result.Error(error ?: DataError.Remote.UNKNOWN)
    }
}

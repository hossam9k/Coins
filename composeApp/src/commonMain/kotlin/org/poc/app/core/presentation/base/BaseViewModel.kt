package org.poc.app.core.presentation.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DispatcherProvider
import org.poc.app.core.domain.model.Logger
import kotlin.time.TimeSource

/**
 * Production-ready Base ViewModel with comprehensive infrastructure
 *
 * Features:
 * - Safe coroutine management with automatic error handling
 * - Comprehensive logging and analytics integration
 * - Thread-safe operations with proper synchronization
 * - Performance monitoring and debugging tools
 * - Automatic lifecycle management
 */
abstract class BaseViewModel(
    protected val dispatcherProvider: DispatcherProvider,
    protected val logger: Logger,
    protected val analytics: AnalyticsLogger,
) : ViewModel() {
    companion object {
        private const val TAG = "BaseViewModel"
    }

    // Thread-safe operation management
    private val operationMutex = Mutex()
    private val runningOperations = mutableSetOf<String>()
    private val timeSource = TimeSource.Monotonic

    /**
     * Global exception handler that logs errors and reports to analytics
     */
    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            logger.error(TAG, "Unhandled coroutine exception in ${this::class.simpleName}", throwable)
            analytics.logError(
                throwable,
                mapOf(
                    "viewModel" to this::class.simpleName.orEmpty(),
                    "errorType" to "unhandled_coroutine",
                ),
            )
            handleUnexpectedError(throwable)
        }

    init {
        // Log ViewModel lifecycle
        logger.debug(TAG, "ViewModel created: ${this::class.simpleName}")
        logScreenView()
    }

    /**
     * Safe coroutine launch with automatic error handling and operation tracking
     */
    protected fun launchSafe(
        operationName: String? = null,
        preventDuplicates: Boolean = false,
        onError: (Throwable) -> Unit = { handleUnexpectedError(it) },
        block: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch(dispatcherProvider.default + exceptionHandler) {
            val opName = operationName ?: "anonymous_operation"

            if (preventDuplicates) {
                operationMutex.withLock {
                    if (runningOperations.contains(opName)) {
                        logger.debug(TAG, "Skipping duplicate operation: $opName")
                        return@launch
                    }
                    runningOperations.add(opName)
                }
            }

            try {
                logger.debug(TAG, "Starting operation: $opName")
                block()
                logger.debug(TAG, "Completed operation: $opName")
            } catch (e: Exception) {
                logger.error(TAG, "Error in operation: $opName", e)
                analytics.logError(
                    e,
                    mapOf(
                        "viewModel" to this@BaseViewModel::class.simpleName.orEmpty(),
                        "operation" to opName,
                        "errorType" to "safe_launch",
                    ),
                )
                onError(e)
            } finally {
                if (preventDuplicates) {
                    operationMutex.withLock {
                        runningOperations.remove(opName)
                    }
                }
            }
        }
    }

    /**
     * Launch coroutine on IO dispatcher for heavy operations
     */
    protected fun launchIO(
        operationName: String? = null,
        onError: (Throwable) -> Unit = { handleUnexpectedError(it) },
        block: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch(dispatcherProvider.io + exceptionHandler) {
            val opName = operationName ?: "io_operation"
            try {
                logger.debug(TAG, "Starting IO operation: $opName")
                block()
                logger.debug(TAG, "Completed IO operation: $opName")
            } catch (e: Exception) {
                logger.error(TAG, "Error in IO operation: $opName", e)
                analytics.logError(
                    e,
                    mapOf(
                        "viewModel" to this@BaseViewModel::class.simpleName.orEmpty(),
                        "operation" to opName,
                        "errorType" to "io_launch",
                    ),
                )
                onError(e)
            }
        }
    }

    /**
     * Launch coroutine on Main dispatcher for UI operations
     */
    protected fun launchMain(
        operationName: String? = null,
        onError: (Throwable) -> Unit = { handleUnexpectedError(it) },
        block: suspend CoroutineScope.() -> Unit,
    ) {
        viewModelScope.launch(dispatcherProvider.main + exceptionHandler) {
            val opName = operationName ?: "ui_operation"
            try {
                logger.debug(TAG, "Starting UI operation: $opName")
                block()
                logger.debug(TAG, "Completed UI operation: $opName")
            } catch (e: Exception) {
                logger.error(TAG, "Error in UI operation: $opName", e)
                analytics.logError(
                    e,
                    mapOf(
                        "viewModel" to this@BaseViewModel::class.simpleName.orEmpty(),
                        "operation" to opName,
                        "errorType" to "main_launch",
                    ),
                )
                onError(e)
            }
        }
    }

    /**
     * Handle unexpected errors - can be overridden by subclasses
     */
    protected open fun handleUnexpectedError(throwable: Throwable) {
        val viewModelName = this::class.simpleName.orEmpty()
        logger.error(TAG, "Unexpected error in $viewModelName", throwable)
        analytics.logError(
            throwable,
            mapOf(
                "viewModel" to viewModelName,
                "errorType" to "unexpected",
                "timestamp" to timeSource.markNow().elapsedNow().inWholeMilliseconds,
            ),
        )
    }

    /**
     * Log user actions for analytics with enhanced context
     */
    protected fun logUserAction(
        actionName: String,
        parameters: Map<String, Any> = emptyMap(),
        includeTimestamp: Boolean = true,
    ) {
        val eventParams =
            parameters.toMutableMap().apply {
                put("action", actionName)
                put("screen", this@BaseViewModel::class.simpleName.orEmpty())
                if (includeTimestamp) {
                    put("timestamp", timeSource.markNow().elapsedNow().inWholeMilliseconds)
                }
            }

        analytics.logEvent("user_action", eventParams)
        logger.debug(TAG, "User action logged: $actionName with params: $eventParams")
    }

    /**
     * Log screen views for analytics with session tracking
     */
    protected fun logScreenView(screenName: String? = null) {
        val actualScreenName = screenName ?: this::class.simpleName.orEmpty().replace("ViewModel", "").ifEmpty { "UnknownScreen" }

        analytics.logEvent(
            "screen_view",
            mapOf(
                "screen_name" to actualScreenName,
                "viewModel" to this::class.simpleName.orEmpty(),
                "timestamp" to timeSource.markNow().elapsedNow().inWholeMilliseconds,
            ),
        )

        logger.info(TAG, "Screen view logged: $actualScreenName")
    }

    /**
     * Log performance metrics for operations
     */
    protected fun logPerformanceMetric(
        operationName: String,
        durationMs: Long,
        additionalData: Map<String, Any> = emptyMap(),
    ) {
        val metrics =
            additionalData.toMutableMap().apply {
                put("operation", operationName)
                put("duration_ms", durationMs)
                put("viewModel", this@BaseViewModel::class.simpleName.orEmpty())
                put("timestamp", timeSource.markNow().elapsedNow().inWholeMilliseconds)
            }

        analytics.logEvent("performance_metric", metrics)
        logger.info(TAG, "Performance metric: $operationName took ${durationMs}ms")
    }

    /**
     * Check if an operation is currently running (useful for preventing duplicates)
     */
    protected suspend fun isOperationRunning(operationName: String): Boolean =
        operationMutex.withLock { runningOperations.contains(operationName) }

    /**
     * Get current running operations (useful for debugging)
     */
    protected suspend fun getRunningOperations(): Set<String> = operationMutex.withLock { runningOperations.toSet() }

    override fun onCleared() {
        super.onCleared()
        logger.debug(TAG, "ViewModel cleared: ${this::class.simpleName}")
        analytics.logEvent(
            "viewmodel_lifecycle",
            mapOf(
                "event" to "cleared",
                "viewModel" to this::class.simpleName.orEmpty(),
                "timestamp" to timeSource.markNow().elapsedNow().inWholeMilliseconds,
            ),
        )
    }
}

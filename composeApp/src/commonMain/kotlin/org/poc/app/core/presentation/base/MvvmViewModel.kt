package org.poc.app.core.presentation.base

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.poc.app.core.domain.model.AnalyticsLogger
import org.poc.app.core.domain.model.DispatcherProvider
import org.poc.app.core.domain.model.Logger
import kotlin.time.TimeSource

/**
 * Production-ready MVVM ViewModel extending BaseViewModel for comprehensive infrastructure
 *
 * Combines MVVM pattern with BaseViewModel features:
 * - Thread-safe state updates with reactive StateFlow
 * - Safe coroutine management from BaseViewModel
 * - Comprehensive error handling and logging
 * - Action deduplication for expensive operations
 * - Automatic analytics and performance tracking
 * - Event management with configurable buffer (one-time events)
 * - Support for both state and event patterns
 *
 * @param State - Represents the UI state (must implement UiState for type safety)
 * @param Event - Represents one-time UI events (navigation, toasts, etc.)
 */
abstract class MvvmViewModel<State : UiState, Event>(
    initialState: State,
    dispatcherProvider: DispatcherProvider,
    logger: Logger,
    analytics: AnalyticsLogger,
    private val eventBufferSize: Int = 64,
) : BaseViewModel(dispatcherProvider, logger, analytics) {
    companion object {
        private const val TAG = "MvvmViewModel"
    }

    // Thread-safe state management
    private val stateMutex = Mutex()
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected val currentState: State get() = _state.value

    // One-time events with configurable buffer
    private val _event = Channel<Event>(capacity = eventBufferSize)
    val event = _event.receiveAsFlow()

    // Performance tracking
    private val timeSource = TimeSource.Monotonic

    /**
     * Thread-safe state updates with validation
     * Supports both lambda and direct state updates
     */
    protected suspend fun updateState(reducer: (State) -> State) {
        stateMutex.withLock {
            val oldState = currentState
            val newState = reducer(oldState)

            // Skip update if state hasn't actually changed
            if (oldState == newState) return@withLock

            validateState(newState)

            // Analytics: Track state changes
            logger.debug(TAG, "State changed from ${oldState::class.simpleName} to ${newState::class.simpleName}")

            _state.value = newState
        }
    }

    /**
     * Direct state update (non-lambda version for convenience)
     */
    protected suspend fun setState(newState: State) {
        updateState { newState }
    }

    /**
     * Validate state before updating (override for custom validation)
     */
    protected open fun validateState(state: State) {
        // Override in subclasses for custom validation
    }

    /**
     * Emit one-time event with comprehensive error handling
     */
    protected suspend fun emitEvent(event: Event) {
        try {
            _event.send(event)
            val eventName = event?.let { it::class.simpleName } ?: "UnknownEvent"
            logger.debug(TAG, "Event emitted: $eventName")

            // Track event analytics
            analytics.logEvent(
                "ui_event",
                mapOf(
                    "eventType" to eventName,
                    "viewModel" to this::class.simpleName.orEmpty(),
                ),
            )
        } catch (e: Exception) {
            val eventName = event?.let { it::class.simpleName } ?: "UnknownEvent"
            logger.error(TAG, "Failed to emit event: $eventName", e)
            analytics.logError(
                e,
                mapOf(
                    "eventType" to eventName,
                    "viewModel" to this::class.simpleName.orEmpty(),
                    "errorType" to "event_emission",
                ),
            )
        }
    }

    /**
     * Execute an action with comprehensive error handling and analytics
     * This is the primary method for executing business logic in MVVM
     */
    protected fun executeAction(
        actionName: String,
        preventDuplicates: Boolean = false,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend () -> Unit,
    ) {
        val startMark = timeSource.markNow()

        // Log user action through BaseViewModel
        logUserAction(
            "action",
            mapOf(
                "actionName" to actionName,
                "viewModel" to this::class.simpleName.orEmpty(),
            ),
        )

        // Use BaseViewModel's safe launching with operation tracking
        launchSafe(
            operationName = "action_$actionName",
            preventDuplicates = preventDuplicates,
            onError = { error ->
                logger.error(TAG, "Error executing action: $actionName", error)
                analytics.logError(
                    error,
                    mapOf(
                        "actionName" to actionName,
                        "viewModel" to this::class.simpleName.orEmpty(),
                        "errorType" to "action_execution",
                    ),
                )

                // Call custom error handler or default
                onError?.invoke(error) ?: handleActionError(actionName, error)
            },
        ) {
            try {
                block()

                // Log performance metric through BaseViewModel
                val executionTime = startMark.elapsedNow().inWholeMilliseconds
                logPerformanceMetric("action_$actionName", executionTime)
            } catch (e: Exception) {
                // Re-throw to be caught by launchSafe's error handler
                throw e
            }
        }
    }

    /**
     * Execute an IO-intensive action (network, database, etc.)
     */
    protected fun executeIOAction(
        actionName: String,
        onError: ((Throwable) -> Unit)? = null,
        block: suspend () -> Unit,
    ) {
        val startMark = timeSource.markNow()

        logUserAction(
            "io_action",
            mapOf(
                "actionName" to actionName,
                "viewModel" to this::class.simpleName.orEmpty(),
            ),
        )

        launchIO(
            operationName = "io_action_$actionName",
            onError = { error ->
                logger.error(TAG, "Error executing IO action: $actionName", error)
                analytics.logError(
                    error,
                    mapOf(
                        "actionName" to actionName,
                        "viewModel" to this::class.simpleName.orEmpty(),
                        "errorType" to "io_action_execution",
                    ),
                )

                onError?.invoke(error) ?: handleActionError(actionName, error)
            },
        ) {
            try {
                block()

                val executionTime = startMark.elapsedNow().inWholeMilliseconds
                logPerformanceMetric("io_action_$actionName", executionTime)
            } catch (e: Exception) {
                throw e
            }
        }
    }

    /**
     * Handle errors specific to action execution
     * Override this to provide custom error handling
     */
    protected open fun handleActionError(
        actionName: String,
        error: Throwable,
    ) {
        // Override in subclasses to handle errors appropriately
        // e.g., emit error events, update state with error, etc.
        logger.error(TAG, "Unhandled action error: $actionName", error)
    }

    /**
     * Convenience method to update loading state
     * Assumes your state has an isLoading property
     */
    protected suspend fun setLoading(isLoading: Boolean) {
        if (currentState is LoadingUiState) {
            updateState { state ->
                // This requires state to be a data class with copy method
                // Override this method if your state structure is different
                @Suppress("UNCHECKED_CAST")
                (state as? LoadingUiState)?.let {
                    // Subclasses should override this if they want to use it
                    state
                } ?: state
            }
        }
    }

    /**
     * Convenience method to update error state
     * Assumes your state has an error property
     */
    protected suspend fun setError(
        error: String?,
        errorDetails: String? = null,
    ) {
        if (currentState is ErrorUiState) {
            updateState { state ->
                @Suppress("UNCHECKED_CAST")
                (state as? ErrorUiState)?.let {
                    // Subclasses should override this if they want to use it
                    state
                } ?: state
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        _event.cancel()
    }
}
